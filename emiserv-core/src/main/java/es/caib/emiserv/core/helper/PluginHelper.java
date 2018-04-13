/**
 * 
 */
package es.caib.emiserv.core.helper;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import es.caib.emiserv.core.api.exception.SistemaExternException;
import es.caib.emiserv.plugin.usuari.DadesUsuari;
import es.caib.emiserv.plugin.usuari.DadesUsuariPlugin;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PluginHelper {

	private DadesUsuariPlugin dadesUsuariPlugin;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;



	public DadesUsuari dadesUsuariConsultarAmbUsuariCodi(
			String usuariCodi) throws SistemaExternException {
		try {
			return getDadesUsuariPlugin().consultarAmbUsuariCodi(usuariCodi);
		} catch (es.caib.emiserv.plugin.SistemaExternException ex) {
			throw new SistemaExternException(
					"Error en el plugin de dades d'usuari",
					ex);
		}
	}
	public DadesUsuari dadesUsuariConsultarAmbUsuariNif(
			String usuariNif) throws SistemaExternException {
		try {
			return getDadesUsuariPlugin().consultarAmbUsuariNif(usuariNif);
		} catch (es.caib.emiserv.plugin.SistemaExternException ex) {
			throw new SistemaExternException(
					"Error en el plugin de dades d'usuari",
					ex);
		}
	}
	public List<DadesUsuari> dadesUsuariConsultarUsuarisAmbGrup(
			String grupCodi) throws SistemaExternException {
		try {
			return getDadesUsuariPlugin().findUsuarisPerGrup(grupCodi);
		} catch (es.caib.emiserv.plugin.SistemaExternException ex) {
			throw new SistemaExternException(
					"Error en el plugin de dades d'usuari",
					ex);
		}
	}



	private DadesUsuariPlugin getDadesUsuariPlugin() {
		if (dadesUsuariPlugin == null) {
			String pluginClass = getPropertyPluginDadesUsuari();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					dadesUsuariPlugin = (DadesUsuariPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							"Error al crear la instància del plugin de dades d'usuari",
							ex);
				}
			} else {
				throw new SistemaExternException(
						"La classe del plugin de dades d'usuari no està configurada");
			}
		}
		return dadesUsuariPlugin;
	}

	private String getPropertyPluginDadesUsuari() {
		return PropertiesHelper.getProperties().getProperty("es.caib.emiserv.plugin.dades.usuari.class");
	}

}
