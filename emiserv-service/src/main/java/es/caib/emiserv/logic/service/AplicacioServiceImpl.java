/**
 * 
 */
package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.helper.PropertiesHelper;
import es.caib.emiserv.logic.intf.service.AplicacioService;
import es.caib.emiserv.persist.entity.UsuariEntity;
import es.caib.emiserv.persist.repository.UsuariRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementació dels mètodes per a gestionar l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class AplicacioServiceImpl implements AplicacioService {

	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private PropertiesHelper propertiesHelper;

	@Autowired
	private Environment environment;

	@Override
	@Transactional(readOnly = true)
	public String getIdiomaUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userName = auth != null ? auth.getName(): null;
		log.debug("Obtenint l'usuari actual (usuari=" + userName + ")");
		if (userName == null) {
			return null;
		}
		Optional<UsuariEntity> usuari = usuariRepository.findById(userName);
		return (usuari.isPresent()) ? usuari.get().getIdioma() : null;
	}

	@Override
	@Transactional
	public void updateIdiomaUsuariActual(String idioma) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Actualitzant idioma de l'usuari actual (" +
				"usuari=" + auth.getName() + "," +
				"idioma=" + idioma + ")");
		Optional<UsuariEntity> usuari = usuariRepository.findById(auth.getName());
		if (usuari.isPresent()) {
			usuari.get().update(idioma);
		} else {
			usuariRepository.save(
					UsuariEntity.getBuilder(auth.getName()).
					idioma(idioma).
					build());
		}
	}

    @Override
    public void propagateDbProperties() {
		propertiesHelper.reloadDbProperties();
    }

//	@Override
//	public Map<String, String> readProperties() {
//		Map<String, String> properties = new HashMap<>();
//
//		properties.put("es.caib.emiserv.backoffice.caib.auth.password", environment.getProperty("es.caib.emiserv.backoffice.caib.auth.password"));
//		properties.put("es.caib.emiserv.backoffice.caib.auth.username", environment.getProperty("es.caib.emiserv.backoffice.caib.auth.username"));
//		properties.put("es.caib.emiserv.backoffice.caib.soap.action", environment.getProperty("es.caib.emiserv.backoffice.caib.soap.action"));
//		properties.put("es.caib.emiserv.backoffice.jar.path", environment.getProperty("es.caib.emiserv.backoffice.jar.path"));
//		properties.put("es.caib.emiserv.backoffice.mock", environment.getProperty("es.caib.emiserv.backoffice.mock"));
//		properties.put("es.caib.emiserv.backoffice.processar.datos.especificos.peticio", environment.getProperty("es.caib.emiserv.backoffice.processar.datos.especificos.peticio"));
//		properties.put("es.caib.emiserv.backoffice.SVDCCAACPASWS01.caib.auth.password", environment.getProperty("es.caib.emiserv.backoffice.SVDCCAACPASWS01.caib.auth.password"));
//		properties.put("es.caib.emiserv.backoffice.SVDCCAACPASWS01.caib.auth.username", environment.getProperty("es.caib.emiserv.backoffice.SVDCCAACPASWS01.caib.auth.username"));
//		properties.put("es.caib.emiserv.backoffice.SVDCCAACPASWS01.caib.soap.action", environment.getProperty("es.caib.emiserv.backoffice.SVDCCAACPASWS01.caib.soap.action"));
//		properties.put("es.caib.emiserv.backoffice.SVDCCAACPASWS01.processar.datos.especificos.peticio", environment.getProperty("es.caib.emiserv.backoffice.SVDCCAACPASWS01.processar.datos.especificos.peticio"));
//		properties.put("es.caib.emiserv.backoffice.SVDCCAACPCWS01.caib.auth.password", environment.getProperty("es.caib.emiserv.backoffice.SVDCCAACPCWS01.caib.auth.password"));
//		properties.put("es.caib.emiserv.backoffice.SVDCCAACPCWS01.caib.auth.username", environment.getProperty("es.caib.emiserv.backoffice.SVDCCAACPCWS01.caib.auth.username"));
//		properties.put("es.caib.emiserv.backoffice.SVDCCAACPCWS01.caib.soap.action", environment.getProperty("es.caib.emiserv.backoffice.SVDCCAACPCWS01.caib.soap.action"));
//		properties.put("es.caib.emiserv.backoffice.SVDCCAACPCWS01.processar.datos.especificos.peticio", environment.getProperty("es.caib.emiserv.backoffice.SVDCCAACPCWS01.processar.datos.especificos.peticio"));
//		properties.put("es.caib.emiserv.backoffice.SVDSCDDWS01.caib.auth.password", environment.getProperty("es.caib.emiserv.backoffice.SVDSCDDWS01.caib.auth.password"));
//		properties.put("es.caib.emiserv.backoffice.SVDSCDDWS01.caib.auth.username", environment.getProperty("es.caib.emiserv.backoffice.SVDSCDDWS01.caib.auth.username"));
//		properties.put("es.caib.emiserv.backoffice.SVDSCDDWS01.caib.soap.action", environment.getProperty("es.caib.emiserv.backoffice.SVDSCDDWS01.caib.soap.action"));
//		properties.put("es.caib.emiserv.backoffice.SVDSCDDWS01.processar.datos.especificos.peticio", environment.getProperty("es.caib.emiserv.backoffice.SVDSCDDWS01.processar.datos.especificos.peticio"));
//		properties.put("es.caib.emiserv.default.auditor", environment.getProperty("es.caib.emiserv.default.auditor"));
//		properties.put("es.caib.emiserv.security.mappableRoles", environment.getProperty("es.caib.emiserv.security.mappableRoles"));
//		properties.put("es.caib.emiserv.security.useResourceRoleMappings", environment.getProperty("es.caib.emiserv.security.useResourceRoleMappings"));
//		properties.put("es.caib.emiserv.tasca.backoffice.async.processar.pendents", environment.getProperty("es.caib.emiserv.tasca.backoffice.async.processar.pendents"));
//		properties.put("es.caib.emiserv.xsd.base.path", environment.getProperty("es.caib.emiserv.xsd.base.path"));
//
//		return properties;
//	}

}
