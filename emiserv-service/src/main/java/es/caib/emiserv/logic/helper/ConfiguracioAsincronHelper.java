/**
 *
 */
package es.caib.emiserv.logic.helper;

import es.caib.emiserv.persist.entity.ServeiEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Configuració de les consultes asíncrones dels serveis enrutadors processades com a peticions
 * síncrones a cada cedent (cedents que només admeten peticions síncrones).
 *
 * Els valors es llegeixen a cada consulta per poder-los modificar des de la pantalla de
 * configuració sense reiniciar l'aplicació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class ConfiguracioAsincronHelper {

	static final String PREFIX = "es.caib.emiserv.async.";

	static final long PERIODE_PLANIFICADOR_MS = 10000;
	static final int CONCURRENCIA_GLOBAL = 10;
	static final long CEDENT_INTERVAL_MS = 1000;
	static final int CEDENT_CONCURRENCIA = 2;
	static final int TIMEOUT_S = 120;
	static final int REINTENTS_MAXIM = 3;
	static final int REINTENTS_ESPERA_S = 300;
	static final int ENVIAMENT_CADUCAT_S = 600;
	static final int TER_MARGE_PERCENT = 100;
	static final int TER_MINIM_MIN = 60;
	static final int TER_MAXIM_MIN = 1440;
	static final String IDPETICIO_PREFIX = "EMS";
	static final String CANCELACIO_CODI = "0227";
	/** Durada estimada d'una petició síncrona a un cedent (per al càlcul del TER). */
	static final long DURADA_ESTIMADA_ENVIAMENT_MS = 3000;

	@Autowired
	private Environment environment;

	public long getPeriodePlanificadorMs() {
		return getLong("planificador.periode", PERIODE_PLANIFICADOR_MS, 1000);
	}
	public int getConcurrenciaGlobal() {
		return (int)getLong("concurrencia.global", CONCURRENCIA_GLOBAL, 1);
	}
	/** Temps mínim entre peticions a un mateix cedent: el del servei o, si no en té, el global. */
	public long getIntervalCedentMs(ServeiEntity servei) {
		if (servei != null && servei.getAsyncInterval() != null && servei.getAsyncInterval() >= 0) {
			return servei.getAsyncInterval();
		}
		return getLong("cedent.interval", CEDENT_INTERVAL_MS, 0);
	}
	/** Peticions simultànies màximes a un mateix cedent: les del servei o, si no en té, les globals. */
	public int getConcurrenciaCedent(ServeiEntity servei) {
		if (servei != null && servei.getAsyncConcurrencia() != null && servei.getAsyncConcurrencia() >= 1) {
			return servei.getAsyncConcurrencia();
		}
		return (int)getLong("cedent.concurrencia", CEDENT_CONCURRENCIA, 1);
	}
	public int getTimeoutSegons() {
		return (int)getLong("timeout", TIMEOUT_S, 1);
	}
	public int getReintentsMaxim() {
		return (int)getLong("reintents.maxim", REINTENTS_MAXIM, 1);
	}
	/** Espera abans del reintent indicat (1 = primer reintent): es dobla a cada reintent. */
	public long getEsperaReintentMs(int reintent) {
		long espera = getLong("reintents.espera", REINTENTS_ESPERA_S, 0) * 1000L;
		int exponent = Math.max(0, Math.min(reintent - 1, 10));
		return espera * (1L << exponent);
	}
	public int getEnviamentCaducatSegons() {
		return (int)getLong("enviament.caducat", ENVIAMENT_CADUCAT_S, 1);
	}
	public int getTerMargePercent() {
		return (int)getLong("ter.marge", TER_MARGE_PERCENT, 0);
	}
	public int getTerMinimMinuts() {
		return (int)getLong("ter.minim", TER_MINIM_MIN, 0);
	}
	public int getTerMaximMinuts() {
		return (int)Math.max(getLong("ter.maxim", TER_MAXIM_MIN, 1), getTerMinimMinuts());
	}
	public String getPrefixIdPeticio() {
		return getString("idpeticio.prefix", IDPETICIO_PREFIX);
	}
	public String getCodiCancelacio() {
		return getString("cancelacio.codi", CANCELACIO_CODI);
	}

	/**
	 * Temps estimat de resposta (minuts) d'una consulta asíncrona d'un enrutador: les sol·licituds
	 * (les noves i les que ja hi ha pendents al mateix servei) s'envien al ritme del cedent, amb el
	 * marge configurat per a consultes simultànies i reintents, i entre el mínim i el màxim.
	 */
	public int calcularTerMinuts(ServeiEntity servei, long solicituds) {
		return calcularTerMinuts(
				solicituds,
				getIntervalCedentMs(servei),
				getConcurrenciaCedent(servei),
				getTerMargePercent(),
				getTerMinimMinuts(),
				getTerMaximMinuts());
	}

	/**
	 * Temps estimat que ocupa cada sol·licitud a la cua d'un destí: el temps entre enviaments o, si és
	 * més gran, la part proporcional de la durada estimada d'un enviament repartida entre els
	 * enviaments simultanis.
	 */
	public long getTempsPerSolicitudMs(ServeiEntity servei) {
		return getTempsPerSolicitudMs(getIntervalCedentMs(servei), getConcurrenciaCedent(servei));
	}

	static long getTempsPerSolicitudMs(long intervalMs, int concurrencia) {
		return Math.max(intervalMs, DURADA_ESTIMADA_ENVIAMENT_MS / Math.max(1, concurrencia));
	}

	static int calcularTerMinuts(
			long solicituds,
			long intervalMs,
			int concurrencia,
			int margePercent,
			int minimMinuts,
			int maximMinuts) {
		long perSolicitudMs = getTempsPerSolicitudMs(intervalMs, concurrencia);
		double totalMs = solicituds * perSolicitudMs * (1 + margePercent / 100.0);
		long minuts = (long)Math.ceil(totalMs / 60000.0);
		return (int)Math.min(Math.max(minuts, minimMinuts), maximMinuts);
	}

	private long getLong(String nom, long valorPerDefecte, long minim) {
		String valor = environment.getProperty(PREFIX + nom);
		if (valor == null || valor.isBlank()) {
			return valorPerDefecte;
		}
		try {
			long numero = Long.parseLong(valor.trim());
			if (numero < minim) {
				log.warn("Valor massa petit per a la propietat " + PREFIX + nom + " (" + valor + "), es fa servir " + valorPerDefecte);
				return valorPerDefecte;
			}
			return numero;
		} catch (NumberFormatException ex) {
			log.warn("Valor no numèric per a la propietat " + PREFIX + nom + " (" + valor + "), es fa servir " + valorPerDefecte);
			return valorPerDefecte;
		}
	}

	private String getString(String nom, String valorPerDefecte) {
		String valor = environment.getProperty(PREFIX + nom);
		return valor != null && !valor.isBlank() ? valor.trim() : valorPerDefecte;
	}

}
