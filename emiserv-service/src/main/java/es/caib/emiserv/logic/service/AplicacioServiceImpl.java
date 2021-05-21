/**
 * 
 */
package es.caib.emiserv.logic.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.emiserv.logic.intf.service.AplicacioService;
import es.caib.emiserv.persist.entity.UsuariEntity;
import es.caib.emiserv.persist.repository.UsuariRepository;
import lombok.extern.slf4j.Slf4j;

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

}
