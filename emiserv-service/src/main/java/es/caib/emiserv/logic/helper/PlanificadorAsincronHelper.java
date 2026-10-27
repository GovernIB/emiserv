/**
 *
 */
package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.dto.BackofficeAsyncTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.ServeiRutaDestiEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;
import es.caib.emiserv.persist.repository.BackofficePeticioRepository;
import es.caib.emiserv.persist.repository.BackofficeSolicitudRepository;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.ServeiRutaDestiRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreServicioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Planificador de l'enviament de les sol·licituds de les consultes asíncrones que es processen com a
 * peticions síncrones (backoffices i cedents que només admeten peticions síncrones).
 *
 * A cada execució de la tasca periòdica:
 * <ol>
 *   <li>Recupera els enviaments interromputs (aturada de l'aplicació o del node).</li>
 *   <li>Resol l'entitat destí de les sol·licituds noves dels enrutadors (cada sol·licitud pot anar a un cedent diferent).</li>
 *   <li>Engega, per a cada destí (servei i entitat) amb sol·licituds pendents, els fils d'enviament que
 *   permeten els límits de concurrència del destí i global. Cada fil envia sol·licituds del destí una
 *   rere l'altra, respectant el temps mínim entre enviaments al destí, fins que no en queden.</li>
 * </ol>
 *
 * L'estat de les sol·licituds es desa a la BBDD (es poden reprendre després d'una aturada) i la reserva
 * de cada sol·licitud és atòmica: no s'envia dues vegades encara que hi hagi més d'un node. Els límits
 * de concurrència i el temps entre enviaments s'apliquen a cada node.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class PlanificadorAsincronHelper {

	@Autowired
	private BackofficeHelper backofficeHelper;
	@Autowired
	private ConfiguracioAsincronHelper configuracioAsincronHelper;
	@Autowired
	private ServeiRepository serveiRepository;
	@Autowired
	private ServeiRutaDestiRepository serveiRutaDestiRepository;
	@Autowired
	private ScspCoreServicioRepository scspCoreServicioRepository;
	@Autowired
	private BackofficePeticioRepository backofficePeticioRepository;
	@Autowired
	private BackofficeSolicitudRepository backofficeSolicitudRepository;

	/** Enviaments que fa cada fil abans d'acabar (perquè es tornin a repartir entre els destins). */
	static final int MAX_ENVIAMENTS_PER_FIL = 50;

	private final AtomicInteger numFil = new AtomicInteger();
	private final ExecutorService executor = Executors.newCachedThreadPool(runnable -> {
		Thread thread = new Thread(runnable, "emiserv-async-enviament-" + numFil.incrementAndGet());
		thread.setDaemon(true);
		return thread;
	});
	private final Map<Desti, AtomicInteger> filsPerDesti = new ConcurrentHashMap<Desti, AtomicInteger>();
	private final AtomicInteger filsTotal = new AtomicInteger();
	private final Map<Desti, Long> properEnviamentPerDesti = new HashMap<Desti, Long>();
	private final Set<Long> solicitudsEnviant = ConcurrentHashMap.newKeySet();
	private volatile boolean aturat;

	public void planificar() {
		if (aturat) {
			return;
		}
		Map<Long, ServeiEntity> serveis = getServeisPerCertificat();
		if (serveis.isEmpty()) {
			return;
		}
		List<Long> certificats = new ArrayList<Long>(serveis.keySet());
		// Enviaments interromputs
		for (Long peticioId: backofficeHelper.recuperarEnviamentsInterromputs(new HashSet<Long>(solicitudsEnviant))) {
			actualitzarPeticio(peticioId, null);
		}
		// Entitat destí de les sol·licituds dels enrutadors
		List<Long> certificatsEnrutadors = new ArrayList<Long>();
		for (Map.Entry<Long, ServeiEntity> entry: serveis.entrySet()) {
			if (ServeiTipusEnumDto.ENRUTADOR.equals(entry.getValue().getTipus())) {
				certificatsEnrutadors.add(entry.getKey());
			}
		}
		if (!certificatsEnrutadors.isEmpty()) {
			for (Long solicitudId: backofficeSolicitudRepository.findIdsEnviablesSenseEntitat(certificatsEnrutadors, new Date())) {
				Long peticioId = backofficeHelper.resoldreEntitatSolicitud(solicitudId);
				if (peticioId != null) {
					actualitzarPeticio(peticioId, null);
				}
			}
		}
		// Peticions amb totes les sol·licituds processades però sense finalitzar (aturada abans del recompte)
		for (Long peticioId: backofficePeticioRepository.findIdsPendentsSenseSolicitudsPerEnviar(certificats)) {
			actualitzarPeticio(peticioId, null);
		}
		// Fils d'enviament per destí, repartits per torns entre els destins (la petició més antiga primer)
		List<DestiPendent> destins = new ArrayList<DestiPendent>();
		for (Object[] fila: backofficeSolicitudRepository.findDestinsAmbSolicitudsEnviables(certificats, new Date())) {
			Desti desti = new Desti((Long)fila[0], (String)fila[1]);
			ServeiEntity servei = serveis.get(desti.certificado);
			if (servei == null || isRutaPausada(servei, desti.entitatCodi)) {
				continue;
			}
			AtomicInteger fils = filsPerDesti.computeIfAbsent(desti, d -> new AtomicInteger());
			long pendents = ((Number)fila[2]).longValue();
			int nous = (int)Math.min(pendents, configuracioAsincronHelper.getConcurrenciaCedent(servei) - fils.get());
			if (nous > 0) {
				destins.add(new DestiPendent(desti, servei, fils, nous));
			}
		}
		boolean iniciat = true;
		while (iniciat) {
			iniciat = false;
			for (DestiPendent pendent: destins) {
				if (pendent.nous == 0) {
					continue;
				}
				if (!reservarFil(pendent.fils)) {
					return;
				}
				pendent.nous--;
				iniciat = true;
				log.debug("Iniciant un fil d'enviament de sol·licituds asíncrones (" +
						"servei=" + pendent.servei.getCodi() + ", " +
						"entitat=" + pendent.desti.entitatCodi + ", " +
						"filsDesti=" + pendent.fils.get() + ", " +
						"filsTotal=" + filsTotal.get() + ")");
				executor.submit(() -> enviarSolicitudsDesti(pendent.desti, pendent.servei, pendent.fils));
			}
		}
	}

	@PreDestroy
	public void aturar() {
		aturat = true;
		// Les sol·licituds que s'estiguin enviant es recuperaran en tornar a arrencar (enviament interromput)
		executor.shutdownNow();
	}

	int getFilsTotal() {
		return filsTotal.get();
	}

	private void enviarSolicitudsDesti(
			Desti desti,
			ServeiEntity servei,
			AtomicInteger fils) {
		try {
			// Cada fil fa un nombre limitat d'enviaments: a la propera execució es tornen a repartir
			// els fils entre tots els destins amb sol·licituds pendents
			for (int enviaments = 0; enviaments < MAX_ENVIAMENTS_PER_FIL && !aturat && !Thread.currentThread().isInterrupted(); enviaments++) {
				if (isRutaPausada(servei, desti.entitatCodi)) {
					break;
				}
				esperarTorn(desti, configuracioAsincronHelper.getIntervalCedentMs(servei));
				Long solicitudId = backofficeHelper.reservarSeguentSolicitud(desti.certificado, desti.entitatCodi);
				if (solicitudId == null) {
					break;
				}
				solicitudsEnviant.add(solicitudId);
				try {
					Long peticioId = backofficeHelper.enviarSolicitud(solicitudId);
					actualitzarPeticio(peticioId, solicitudId);
				} catch (Exception ex) {
					// La sol·licitud queda ENVIANT i es recuperarà com a enviament interromput
					log.error("Error enviant la sol·licitud asíncrona (id=" + solicitudId + ")", ex);
				} finally {
					solicitudsEnviant.remove(solicitudId);
				}
			}
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		} catch (Exception ex) {
			log.error("Error en el fil d'enviament de sol·licituds asíncrones (" +
					"servei=" + servei.getCodi() + ", " +
					"entitat=" + desti.entitatCodi + ")", ex);
		} finally {
			fils.decrementAndGet();
			filsTotal.decrementAndGet();
		}
	}

	private boolean reservarFil(AtomicInteger fils) {
		if (filsTotal.incrementAndGet() > configuracioAsincronHelper.getConcurrenciaGlobal()) {
			filsTotal.decrementAndGet();
			return false;
		}
		fils.incrementAndGet();
		return true;
	}

	/** Espera fins que es pot fer el següent enviament al destí (temps mínim entre enviaments). */
	private void esperarTorn(Desti desti, long intervalMs) throws InterruptedException {
		long ara = System.currentTimeMillis();
		long torn;
		synchronized (properEnviamentPerDesti) {
			Long proper = properEnviamentPerDesti.get(desti);
			torn = (proper != null && proper > ara) ? proper : ara;
			properEnviamentPerDesti.put(desti, torn + intervalMs);
		}
		if (torn > ara) {
			Thread.sleep(torn - ara);
		}
	}

	private void actualitzarPeticio(Long peticioId, Long solicitudId) {
		try {
			backofficeHelper.actualitzarEstatPeticio(peticioId, solicitudId);
		} catch (Exception ex) {
			// Es tornarà a recomptar a la propera execució (findIdsPendentsSenseSolicitudsPerEnviar)
			log.error("Error actualitzant l'estat de la petició asíncrona (id=" + peticioId + ")", ex);
		}
	}

	private boolean isRutaPausada(ServeiEntity servei, String entitatCodi) {
		if (entitatCodi == null || !servei.isEnrutador()) {
			return false;
		}
		ServeiRutaDestiEntity ruta = serveiRutaDestiRepository.findPrimeraByServeiAndEntitatCodi(servei, entitatCodi);
		return ruta != null && ruta.isPausada();
	}

	/**
	 * Serveis amb les peticions asíncrones processades com a síncrones: els de tipus backoffice amb
	 * aquest tipus de processament i els enrutadors (els cedents només admeten peticions síncrones).
	 */
	public Map<Long, ServeiEntity> getServeisPerCertificat() {
		Map<String, ServeiEntity> serveisPerCodi = new HashMap<String, ServeiEntity>();
		for (ServeiEntity servei: serveiRepository.findByBackofficeCaibAsyncTipus(BackofficeAsyncTipusEnumDto.PROCESSAR_SINCRONES)) {
			serveisPerCodi.put(servei.getCodi(), servei);
		}
		for (ServeiEntity servei: serveiRepository.findByTipusIn(Arrays.asList(ServeiTipusEnumDto.ENRUTADOR, ServeiTipusEnumDto.ENRUTADOR_MULTIPLE))) {
			serveisPerCodi.put(servei.getCodi(), servei);
		}
		Map<Long, ServeiEntity> serveis = new HashMap<Long, ServeiEntity>();
		if (!serveisPerCodi.isEmpty()) {
			for (ScspCoreServicioEntity servicio: scspCoreServicioRepository.findByCodigoCertificadoIn(new ArrayList<String>(serveisPerCodi.keySet()))) {
				serveis.put(servicio.getId(), serveisPerCodi.get(servicio.getCodigoCertificado()));
			}
		}
		return serveis;
	}

	private static class DestiPendent {
		final Desti desti;
		final ServeiEntity servei;
		final AtomicInteger fils;
		int nous;
		DestiPendent(Desti desti, ServeiEntity servei, AtomicInteger fils, int nous) {
			this.desti = desti;
			this.servei = servei;
			this.fils = fils;
			this.nous = nous;
		}
	}

	/** Destí de les sol·licituds: servei SCSP i, per als enrutadors, l'entitat (cedent). */
	static class Desti {
		final Long certificado;
		final String entitatCodi;
		Desti(Long certificado, String entitatCodi) {
			this.certificado = certificado;
			this.entitatCodi = entitatCodi;
		}
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Desti)) return false;
			Desti desti = (Desti)o;
			return Objects.equals(certificado, desti.certificado) && Objects.equals(entitatCodi, desti.entitatCodi);
		}
		@Override
		public int hashCode() {
			return Objects.hash(certificado, entitatCodi);
		}
	}

}
