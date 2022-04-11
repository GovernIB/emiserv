/**
 * 
 */
package es.caib.emiserv.logic.intf.service;

import es.caib.emiserv.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.emiserv.logic.intf.dto.CarregaDto;
import es.caib.emiserv.logic.intf.dto.EstadisticaDto;
import es.caib.emiserv.logic.intf.dto.EstadistiquesFiltreDto;
import es.caib.emiserv.logic.intf.dto.InformeGeneralEstatDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Date;
import java.util.List;

/**
 * Declaració dels mètodes per a la explotació de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@PreAuthorize("hasRole('EMS_REPORT')")
public interface ExplotacioService {

	/** Retorna una llista amb la informació per generar l'informe d'estat general.
	 * 
	 * @param dataInici Filtra per data inici.
	 * @param dataFi Filtra per data fi.
	 * @param tipusPeticio Filtra per backoffices, enrutador o tots si és null.
	 * @return Retorna la llista amb la informació.
	 */
	List<InformeGeneralEstatDto> informeGeneralEstat(
			Date dataInici,
			Date dataFi,
			ServeiTipusEnumDto tipusPeticio);

	/**
	 * Retorna una llista de les consultes realitzades donada una entitat
	 * i el filtre.
	 *
	 * @param entitatCodi
	 *            Atribut id de l'entitat.
	 * @param dataInici
	 * @param dataFi
	 * @param procedimentCodi
	 * @param serveiCodi
	 * @return la llista de consultes.
	 */
	@PreAuthorize("permitAll()")
    List<DadesObertesRespostaConsulta> findOpenData(
			String entitatCodi,
			Date dataInici,
			Date dataFi,
			String procedimentCodi,
			String serveiCodi);

	/**
	 * Retorna informació sobre la càrrega del sistema.
	 *
	 * @return la llista d'informació de càrrega.
	 */
	List<CarregaDto> findEstadistiquesCarrega();

	/**
	 * Retorna una estadística d'us dels procediments i serveis
	 * d'una entitat.
	 *
	 * @param filtre
	 *            Filtre de consultes.
	 * @return el llistat amb les estadístiques.
	 */
	List<EstadisticaDto> findEstadistiquesByFiltre(EstadistiquesFiltreDto filtre);

}
