package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.dto.AuditoriaFiltreDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import es.caib.emiserv.logic.intf.exception.OrdenacioNoValidaException;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilitats dels recursos d'auditoria de la interfície REACT (peticions de backoffice i de
 * l'enrutador). Els recursos deleguen a les consultes de la JSP (BackofficeService i
 * RedireccioService), que ja apliquen els permisos i paginen al servidor:
 * <ul>
 *   <li>el filtre Spring Filter de la graella es tradueix a {@link AuditoriaFiltreDto} (només els
 *   camps i operadors que fa servir el filtre de la interfície: {@code camp:'valor'},
 *   {@code dataPeticio>:'aaaa-mm-dd'} i {@code dataPeticio<:'aaaa-mm-dd'});</li>
 *   <li>el filtre ràpid cerca pel número de petició;</li>
 *   <li>l'ordenació només admet les columnes que la consulta sap ordenar.</li>
 * </ul>
 *
 * @author Limit Tecnologies
 */
@Component
public class AuditoriaResourceHelper {

	/** Columnes per les quals es pot ordenar (les mateixes que la JSP). */
	public static final Set<String> CAMPS_ORDENABLES = new LinkedHashSet<>(Arrays.asList(
			"dataPeticio", "peticioId", "procedimentCodiNom", "serveiCodi", "estat"));

	private static final Pattern TERME = Pattern.compile("(\\w+)\\s*(>:|<:|:)\\s*(?:'((?:[^'\\\\]|\\\\.)*)'|([^\\s)]+))");

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Filtre de la consulta a partir del filtre de la graella i del filtre ràpid.
	 *
	 * @throws ValidationException si el filtre té un camp o un valor desconegut.
	 */
	public AuditoriaFiltreDto toFiltre(String filter, String quickFilter) {
		AuditoriaFiltreDto filtre = new AuditoriaFiltreDto();
		if (filter != null && !filter.trim().isEmpty()) {
			Matcher matcher = TERME.matcher(filter);
			while (matcher.find()) {
				String camp = matcher.group(1);
				String operador = matcher.group(2);
				String valor = matcher.group(3) != null ?
						matcher.group(3).replace("\\'", "'").replace("\\\\", "\\") :
						matcher.group(4);
				aplicar(filtre, camp, operador, valor);
			}
		}
		if (quickFilter != null && !quickFilter.trim().isEmpty() && filtre.getNumeroPeticio() == null) {
			filtre.setNumeroPeticio(quickFilter.trim());
		}
		return filtre;
	}

	private static void aplicar(AuditoriaFiltreDto filtre, String camp, String operador, String valor) {
		try {
			switch (camp) {
				case "procediment":
					filtre.setProcediment(valor);
					break;
				case "servei":
					filtre.setServei(Long.valueOf(valor));
					break;
				case "serveiCodi":
					filtre.setServeiCodi(valor);
					break;
				case "estat":
					filtre.setEstat(PeticioEstatEnumDto.valueOf(valor));
					break;
				case "numeroPeticio":
					filtre.setNumeroPeticio(valor);
					break;
				case "dataPeticio":
					if (">:".equals(operador)) {
						filtre.setDataInici(data(valor, false));
					} else if ("<:".equals(operador)) {
						filtre.setDataFi(data(valor, true));
					} else {
						filtre.setDataInici(data(valor, false));
						filtre.setDataFi(data(valor, true));
					}
					break;
				default:
					throw new ValidationException("Camp de filtre desconegut: " + camp);
			}
		} catch (IllegalArgumentException | ParseException ex) {
			throw new ValidationException("Valor de filtre incorrecte (" + camp + "): " + valor);
		}
	}

	/**
	 * Data del filtre (aaaa-mm-dd, o amb hora ISO). La data final inclou tot el dia: la consulta
	 * compara {@code fechaPeticion <= dataFi}.
	 */
	private static Date data(String valor, boolean fiDelDia) throws ParseException {
		Date data = new SimpleDateFormat("yyyy-MM-dd").parse(valor.length() > 10 ? valor.substring(0, 10) : valor);
		if (fiDelDia) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(data);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			return cal.getTime();
		}
		return data;
	}

	/**
	 * Paginació de la consulta. Per defecte, les peticions més recents primer.
	 *
	 * @throws OrdenacioNoValidaException si s'ordena per una columna que la consulta no admet.
	 */
	public PaginacioParamsDto toPaginacio(Pageable pageable, int midaMaxima) {
		PaginacioParamsDto paginacio = new PaginacioParamsDto();
		if (pageable != null && pageable.isPaged()) {
			paginacio.setPaginaNum(pageable.getPageNumber());
			paginacio.setPaginaTamany(pageable.getPageSize());
		} else {
			paginacio.setPaginaNum(0);
			paginacio.setPaginaTamany(midaMaxima);
		}
		Sort sort = pageable != null ? pageable.getSort() : Sort.unsorted();
		for (Sort.Order ordre: sort) {
			if (!CAMPS_ORDENABLES.contains(ordre.getProperty())) {
				throw new OrdenacioNoValidaException(ordre.getProperty(), CAMPS_ORDENABLES);
			}
			paginacio.afegirOrdre(
					ordre.getProperty(),
					ordre.isAscending() ? PaginacioParamsDto.OrdreDireccioDto.ASCENDENT : PaginacioParamsDto.OrdreDireccioDto.DESCENDENT);
		}
		if (paginacio.getOrdres().isEmpty()) {
			paginacio.afegirOrdre("dataPeticio", PaginacioParamsDto.OrdreDireccioDto.DESCENDENT);
		}
		return paginacio;
	}

	/** Ids dels serveis pels seus codis (per enllaçar el servei de cada petició amb la seva finestra). */
	public Map<String, Long> getServeiIds(Collection<String> codis) {
		Map<String, Long> ids = new HashMap<>();
		if (codis == null || codis.isEmpty()) {
			return ids;
		}
		List<Object[]> files = entityManager.createQuery(
				"select s.codi, s.id from ServeiEntity s where s.codi in :codis", Object[].class).
				setParameter("codis", new LinkedHashSet<>(codis)).
				getResultList();
		for (Object[] fila: files) {
			ids.put((String)fila[0], (Long)fila[1]);
		}
		return ids;
	}

}
