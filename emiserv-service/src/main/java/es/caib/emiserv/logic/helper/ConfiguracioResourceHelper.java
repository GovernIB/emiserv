package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.exception.OrdenacioNoValidaException;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilitats dels recursos dels manteniments de configuració de la interfície REACT (aplicacions,
 * emissors, organismes, claus...). Els recursos deleguen a les consultes de la JSP (ScspService i
 * EntitatService):
 * <ul>
 *   <li>el filtre Spring Filter de la graella es tradueix a un mapa camp → valor (només
 *   {@code camp:'valor'} i {@code camp~'%valor%'} units amb {@code and}, el que genera el filtre de la
 *   interfície); les consultes ja cerquen per «conté»;</li>
 *   <li>l'ordenació només admet les columnes indicades, amb el nom del camp de l'entitat (les
 *   consultes de la JSP no mapen tots els camps del DTO).</li>
 * </ul>
 *
 * @author Limit Tecnologies
 */
@Component
public class ConfiguracioResourceHelper {

	private static final Pattern TERME = Pattern.compile("(\\w+)\\s*(:|~)\\s*'((?:[^'\\\\]|\\\\.)*)'");

	private static final ResourceBundleMessageSource MISSATGES = new ResourceBundleMessageSource();
	static {
		MISSATGES.setBasename("es/caib/emiserv/logic/i18n/configuracio");
		MISSATGES.setDefaultEncoding("UTF-8");
		MISSATGES.setFallbackToSystemLocale(false);
	}

	/**
	 * Filtre de la consulta a partir del filtre de la graella.
	 *
	 * @param campsFiltrables camps que admet el filtre.
	 * @throws ValidationException si el filtre té un camp desconegut.
	 */
	public Map<String, String> toFiltre(String filter, Collection<String> campsFiltrables) {
		Map<String, String> filtre = new HashMap<>();
		if (filter != null && !filter.trim().isEmpty()) {
			Matcher matcher = TERME.matcher(filter);
			while (matcher.find()) {
				String camp = matcher.group(1);
				if (!campsFiltrables.contains(camp)) {
					throw new ValidationException("Camp de filtre desconegut: " + camp);
				}
				String valor = matcher.group(3).replace("\\'", "'").replace("\\\\", "\\");
				if ("~".equals(matcher.group(2))) {
					// «conté»: les consultes ja fan el like
					valor = valor.replaceAll("^%+|%+$", "");
				}
				filtre.put(camp, valor);
			}
		}
		return filtre;
	}

	/**
	 * Paginació de la consulta.
	 *
	 * @param campsOrdenables columnes ordenables (camp del recurs → camp de l'entitat).
	 * @param ordrePerDefecte camp del recurs per a l'ordre per defecte (ascendent); null, sense ordre.
	 * @param midaMaxima mida de la pàgina d'una consulta sense paginar.
	 * @throws OrdenacioNoValidaException si s'ordena per una columna no ordenable.
	 */
	public PaginacioParamsDto toPaginacio(
			Pageable pageable,
			Map<String, String> campsOrdenables,
			String ordrePerDefecte,
			int midaMaxima) {
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
			String campEntitat = campsOrdenables.get(ordre.getProperty());
			if (campEntitat == null) {
				throw new OrdenacioNoValidaException(ordre.getProperty(), campsOrdenables.keySet());
			}
			paginacio.afegirOrdre(
					campEntitat,
					ordre.isAscending() ? PaginacioParamsDto.OrdreDireccioDto.ASCENDENT : PaginacioParamsDto.OrdreDireccioDto.DESCENDENT);
		}
		if (paginacio.getOrdres().isEmpty() && ordrePerDefecte != null) {
			paginacio.afegirOrdre(campsOrdenables.get(ordrePerDefecte), PaginacioParamsDto.OrdreDireccioDto.ASCENDENT);
		}
		return paginacio;
	}

	/** Valor no buit (o null). */
	public static String valor(Map<String, String> filtre, String camp) {
		String valor = filtre.get(camp);
		return valor != null && !valor.trim().isEmpty() ? valor.trim() : null;
	}

	/** Missatge traduït (bundle es/caib/emiserv/logic/i18n/configuracio) en l'idioma de la petició. */
	public String missatge(String clau, Object... arguments) {
		return MISSATGES.getMessage(clau, arguments, clau, LocaleContextHolder.getLocale());
	}

}
