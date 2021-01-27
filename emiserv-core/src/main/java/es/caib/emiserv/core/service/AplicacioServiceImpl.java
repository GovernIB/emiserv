/**
 * 
 */
package es.caib.emiserv.core.service;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.emiserv.core.api.dto.UsuariDto;
import es.caib.emiserv.core.api.exception.NotFoundException;
import es.caib.emiserv.core.api.service.AplicacioService;
import es.caib.emiserv.core.entity.UsuariEntity;
import es.caib.emiserv.core.helper.ConversioTipusHelper;
import es.caib.emiserv.core.helper.PluginHelper;
import es.caib.emiserv.core.repository.UsuariRepository;
import es.caib.emiserv.plugin.usuari.DadesUsuari;

/**
 * Implementació dels mètodes per a gestionar l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AplicacioServiceImpl implements AplicacioService {

	private Properties versionProperties;

	@Resource
	private UsuariRepository usuariRepository;

	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;


	@Override
	public String getVersioActual() {
		logger.debug("Obtenint versió actual de l'aplicació");
		try {
			return getVersionProperties().getProperty("app.version");
		} catch (IOException ex) {
			logger.error("No s'ha pogut llegir el fitxer version.properties", ex);
			return "???";
		}
	}

	@Transactional
	@Override
	public void processarAutenticacioUsuari() throws NotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Processant autenticació (usuariCodi=" + auth.getName() + ")");
		UsuariEntity usuari = usuariRepository.findOne(auth.getName());
		if (usuari == null) {
			logger.debug("Consultant plugin de dades d'usuari (usuariCodi=" + auth.getName() + ")");
			DadesUsuari dadesUsuari = pluginHelper.dadesUsuariConsultarAmbUsuariCodi(auth.getName());
			if (dadesUsuari != null) {
				usuari = usuariRepository.save(
						UsuariEntity.getBuilder(
								dadesUsuari.getCodi(),
								dadesUsuari.getNom(),
								dadesUsuari.getNif(),
								dadesUsuari.getEmail()).build());
			} else {
				throw new NotFoundException(
						auth.getName(),
						DadesUsuari.class);
			}
		} else {
			logger.debug("Consultant plugin de dades d'usuari (usuariCodi=" + auth.getName() + ")");
			DadesUsuari dadesUsuari = pluginHelper.dadesUsuariConsultarAmbUsuariCodi(auth.getName());
			if (dadesUsuari != null) {
				usuari.update(
						dadesUsuari.getNom(),
						dadesUsuari.getNif(),
						dadesUsuari.getEmail());
			} else {
				throw new NotFoundException(
						auth.getName(),
						DadesUsuari.class);
			}
		}
	}

	@Transactional(readOnly = true)
	@Override
	public UsuariDto getUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Obtenint usuari actual");
		return conversioTipusHelper.convertir(
				usuariRepository.findOne(auth.getName()),
				UsuariDto.class);
	}

	@Transactional
	@Override
	public UsuariDto updateUsuariActual(UsuariDto dto) {
		logger.debug("Actualitzant configuració de usuari actual");
		UsuariEntity usuari = usuariRepository.findOne(dto.getCodi());
		usuari.updateIdioma(
				dto.getIdioma());

		return conversioTipusHelper.convertir(
				usuari,
				UsuariDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public UsuariDto findUsuariAmbCodi(String codi) {
		logger.debug("Obtenint usuari amb codi (codi=" + codi + ")");
		return conversioTipusHelper.convertir(
				usuariRepository.findOne(codi),
				UsuariDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<UsuariDto> findUsuariAmbText(String text) {
		logger.debug("Consultant usuaris amb text (text=" + text + ")");
		return conversioTipusHelper.convertirList(
				usuariRepository.findByText(text),
				UsuariDto.class);
	}


	private Properties getVersionProperties() throws IOException {
		if (versionProperties == null) {
			versionProperties = new Properties();
			versionProperties.load(
					getClass().getResourceAsStream(
							"/es/caib/emiserv/core/version/version.properties"));
		}
		return versionProperties;
	}

	private static final Logger logger = LoggerFactory.getLogger(AplicacioServiceImpl.class);

}
