package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.logic.intf.model.ServeiResource;
import es.caib.emiserv.logic.intf.service.ServeiResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilitats dels recursos de la interfície REACT que pertanyen a un servei (rutes, permisos i
 * autoritzacions). Aquests recursos no tenen entitat pròpia: deleguen als serveis de negoci de la
 * JSP i reben el servei amb el filtre de la graella ({@code serveiId:<id>}).
 *
 * @author Limit Tecnologies
 */
@Component
public class SubrecursServeiHelper {

	/** Camp del filtre amb l'identificador del servei. */
	public static final String CAMP_SERVEI_ID = "serveiId";

	private static final Pattern PATRO_SERVEI_ID = Pattern.compile("\\b" + CAMP_SERVEI_ID + "\\s*[:=]\\s*'?(\\d+)'?");

	@Autowired
	private ServeiResourceService serveiResourceService;

	/**
	 * Identificador del servei d'un filtre Spring Filter ({@code serveiId:5}).
	 *
	 * @throws ValidationException si el filtre no l'inclou.
	 */
	public Long getServeiIdDelFiltre(String filtre) {
		Long serveiId = extreureServeiId(filtre);
		if (serveiId == null) {
			throw new ValidationException("Cal indicar el servei amb el filtre " + CAMP_SERVEI_ID + ":<id>");
		}
		return serveiId;
	}

	static Long extreureServeiId(String filtre) {
		if (filtre == null) {
			return null;
		}
		Matcher matcher = PATRO_SERVEI_ID.matcher(filtre);
		return matcher.find() ? Long.valueOf(matcher.group(1)) : null;
	}

	/**
	 * Comprova que l'usuari pot accedir al servei: EMS_ADMIN, o EMS_RESP amb permís sobre el servei
	 * (el mateix filtre que el llistat de serveis). Si no, ResourceNotFoundException.
	 */
	public ServeiResource comprovarAccesServei(Long serveiId) {
		return serveiResourceService.getOne(serveiId, null);
	}

	/** Pàgina d'una llista ja ordenada (els subrecursos tenen pocs elements). */
	public <T> Page<T> pagina(List<T> elements, Pageable pageable) {
		if (pageable == null || pageable.isUnpaged()) {
			return new PageImpl<>(elements);
		}
		int inici = (int)Math.min(pageable.getOffset(), elements.size());
		int fi = Math.min(inici + pageable.getPageSize(), elements.size());
		return new PageImpl<>(elements.subList(inici, fi), pageable, elements.size());
	}

}
