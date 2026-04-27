/**
 * 
 */
package es.caib.emiserv.logic.intf.service;

import es.caib.comanda.model.server.monitoring.DimensioDesc;
import es.caib.comanda.model.server.monitoring.IndicadorDesc;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;
import es.caib.emiserv.client.dadesobertes.DadesObertesResposta;
import es.caib.emiserv.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.emiserv.logic.intf.dto.CarregaDto;
import es.caib.emiserv.logic.intf.dto.ConsultaOpenDataDto;
import es.caib.emiserv.logic.intf.dto.EstadisticaDto;
import es.caib.emiserv.logic.intf.dto.EstadistiquesFiltreDto;
import es.caib.emiserv.logic.intf.dto.InformeEmisorEnrutatDto;
import es.caib.emiserv.logic.intf.dto.InformeGeneralEstatDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Declaració dels mètodes per a la explotació de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExplotacioService {

	/** Retorna una llista amb la informació per generar l'informe d'estat general.
	 * 
	 * @param dataInici Filtra per data inici.
	 * @param dataFi Filtra per data fi.
	 * @param tipusPeticio Filtra per backoffices, enrutador o tots si és null.
	 * @return Retorna la llista amb la informació.
	 */
	@PreAuthorize("hasAnyRole('EMS_REPORT', 'EMS_ADMIN')")
	List<InformeGeneralEstatDto> informeGeneralEstat(
			Date dataInici,
			Date dataFi,
			ServeiTipusEnumDto tipusPeticio);

	/**
	 * Retorna una llista amb la informació per generar l'informe
	 * de peticions realitzades a cada emissor pels serveis enrutats.
	 *
	 * @param dataInici Filtra per data inici.
	 * @param dataFi Filtra per data fi.
	 * @return Retorna la llista amb la informació.
	 */
	@PreAuthorize("hasAnyRole('EMS_REPORT', 'EMS_ADMIN')")
	List<InformeEmisorEnrutatDto> informeEmisorEnrutat(
			Date dataInici,
			Date dataFi);

	/**
	 * Retorna una llista de les consultes realitzades donada una entitat
	 * i el filtre.
	 *
	 * @param entitatNif
	 *            Atribut id de l'entitat.
	 * @param dataInici
	 * @param dataFi
	 * @param procedimentCodi
	 * @param serveiCodi
	 * @return la llista de consultes.
	 */
	@PreAuthorize("permitAll()")
    List<DadesObertesRespostaConsulta> findOpenData(
			String entitatNif,
			Date dataInici,
			Date dataFi,
			String procedimentCodi,
			String serveiCodi);

	/**
	 * Retorna una llista de les consultes realitzades donada una entitat
	 * i el filtre.
	 *
	 * @param consultaOpenDataDto
	 *            Paràmetres de la consulta.
	 * @return la llista de consultes.
	 */
	@PreAuthorize("permitAll()")
	DadesObertesResposta findOpenDataV2(ConsultaOpenDataDto consultaOpenDataDto);

	/**
	 * Retorna informació sobre la càrrega del sistema.
	 *
	 * @return la llista d'informació de càrrega.
	 */
	@PreAuthorize("hasRole('EMS_REPORT')")
	List<CarregaDto> findEstadistiquesCarrega();

	/**
	 * Retorna una estadística d'us dels procediments i serveis
	 * d'una entitat.
	 *
	 * @param filtre
	 *            Filtre de consultes.
	 * @return el llistat amb les estadístiques.
	 */
	@PreAuthorize("hasRole('EMS_REPORT')")
	List<EstadisticaDto> findEstadistiquesByFiltre(EstadistiquesFiltreDto filtre);

	@PreAuthorize("hasRole('EMS_COM')")
    List<DimensioDesc> getDimensions();

	@PreAuthorize("hasRole('EMS_COM')")
	List<IndicadorDesc> getIndicadors();

	@PreAuthorize("hasRole('EMS_COM')")
	RegistresEstadistics consultaUltimesEstadistiques();

	@PreAuthorize("hasRole('EMS_COM')")
	RegistresEstadistics consultaEstadistiques(LocalDate date);

	@PreAuthorize("hasRole('EMS_COM')")
	List<RegistresEstadistics> consultaEstadistiques(LocalDate iniDate, LocalDate fiDate);
}
