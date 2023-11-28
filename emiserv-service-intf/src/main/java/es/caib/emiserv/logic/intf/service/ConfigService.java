package es.caib.emiserv.logic.intf.service;

import es.caib.emiserv.logic.intf.dto.ConfigDto;
import es.caib.emiserv.logic.intf.dto.ConfigGroupDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a la gestió dels paràmetres de configuració de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigService {

	/**
	 * Actualitza el valor d'una propietat de configuració.
	 *
	 * @param property Informació que es vol actualitzar.
	 * @return El DTO amb les dades modificades.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	ConfigDto updateProperty(ConfigDto property) throws Exception;

	/**
	 * Consulta totes les propietats de configuració de l'aplicació.
	 * Retorna les propietats estructurades en forma d'arbres segons al grup o subgrups al que pertanyen.
	 * Cada arbre està format per un grup de propietats que no pertany a cap altre grup.
	 *
	 * @return Retorna un llistat de tots els grups de propietats que no pertanyen a cap grup.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	List<ConfigGroupDto> findAll();


	/**
	 * Procediment que actualitza totes les propietats de configuració per a configurar-les amb
	 * el valor de les properties configurades a JBoss.
	 *
	 * L'execució d'aquest procés només actualitza el valor de les properties que ja existeixen a la base de dades
	 * i que a més també estan definides al fitxer JBoss.
	 *
	 * @return Llistat de les properties editades.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	List<String> syncFromJBossProperties();

//	<T> T getProperty(String key, Class<T> targetType, T defaultValue);
}

