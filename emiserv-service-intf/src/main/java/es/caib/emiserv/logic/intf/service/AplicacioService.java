/**
 * 
 */
package es.caib.emiserv.logic.intf.service;

import es.caib.comanda.model.server.monitoring.ContextInfo;
import es.caib.comanda.model.server.monitoring.FitxerContingut;
import es.caib.comanda.model.server.monitoring.FitxerInfo;
import es.caib.comanda.model.server.monitoring.IntegracioInfo;
import es.caib.comanda.model.server.monitoring.IntegracioSalut;
import es.caib.comanda.model.server.monitoring.MissatgeSalut;
import es.caib.comanda.model.server.monitoring.SubsistemaInfo;
import es.caib.comanda.model.server.monitoring.SubsistemaSalut;
import es.caib.comanda.ms.log.helper.LogFileStream;
import es.caib.emiserv.logic.intf.dto.InterficieUsuariEnumDto;
import es.caib.emiserv.logic.intf.dto.TemaAplicacioEnumDto;
import es.caib.emiserv.logic.intf.dto.SubsistemesEnum;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Declaració dels mètodes comuns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AplicacioService {

	/**
	 * Obté l'usuari actual.
	 * 
	 * @return L'usuari actual.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public String getIdiomaUsuariActual();

	/**
	 * Configura l'idioma de l'usuari actual.
	 * 
	 * @param idioma
	 *            idioma de l'usuari.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public void updateIdiomaUsuariActual(String idioma);

	/**
	 * Obté la interfície efectiva de l'usuari actual: la seva preferència o, si no n'hi ha,
	 * la propietat es.caib.emiserv.interface.defecte (JSP si no està definida o no és vàlida).
	 *
	 * @return La interfície efectiva.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public InterficieUsuariEnumDto getInterficieEfectiva();

	/**
	 * Obté la preferència d'interfície de l'usuari actual, sense aplicar la propietat per defecte.
	 *
	 * @return La preferència, o null si no n'ha definit cap.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public InterficieUsuariEnumDto getInterficiePreferida();

	/**
	 * Configura la preferència d'interfície de l'usuari actual (crea l'usuari si no existeix).
	 *
	 * @param interficie
	 *            interfície preferida, o null per esborrar la preferència.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public void updateInterficie(InterficieUsuariEnumDto interficie);

	/**
	 * Retorna el tema de la interfície REACT de l'usuari actual.
	 *
	 * @return el tema, o null si l'usuari no n'ha triat cap.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public TemaAplicacioEnumDto getTemaUsuariActual();

	/**
	 * Configura el tema de la interfície REACT de l'usuari actual (crea l'usuari si no existeix).
	 *
	 * @param tema
	 *            el tema, o null per esborrar la preferència.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public void updateTemaUsuariActual(TemaAplicacioEnumDto tema);

	/**
	 * Carrega les propietats de la base de dades a l'environment
	 */
    public void propagateDbProperties();


	// Salut
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void addSubsistemaExit(SubsistemesEnum subsistema, String serveiCodi, long duracioMs);

	public void addSubsistemaError(SubsistemesEnum subsistema, String serveiCodi);
	public void addSubsistemaError(SubsistemesEnum subsistema);

	public void addIntegracioExit(String solicitantId, String serveiCodi, long duracioMs);
	public void addIntegracioError(String solicitantId, String serveiCodi);

	@PreAuthorize("hasRole('EMS_COM')")
	public List<IntegracioInfo> getIntegracionsInfo();

	public default List<IntegracioSalut> getIntegracionsSalut() {
		return getIntegracionsSalut(null, null);
	}

	public List<IntegracioSalut> getIntegracionsSalut(OffsetDateTime dataPeriode, OffsetDateTime dataTotal);

	@PreAuthorize("hasRole('EMS_COM')")
	public List<SubsistemaInfo> getSubsistemesInfo();

	public List<SubsistemaSalut> getSubsistemesSalut();

	@PreAuthorize("hasRole('EMS_COM')")
	public List<ContextInfo> getContextsInfo(String baseUrl);

	public List<MissatgeSalut> getMissatgesSalut();

	public Integer measureDbLatencyMs();

	// LOGS
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@PreAuthorize("hasRole('EMS_COM')")
    List<FitxerInfo> llistarLogFiles();

	@PreAuthorize("hasRole('EMS_COM')")
	FitxerContingut getLogFileByNom(String nomFitxer);

	@PreAuthorize("hasRole('EMS_COM')")
	LogFileStream getFileLogStreamByNom(String nomFitxer);

	@PreAuthorize("hasRole('EMS_COM')")
	List<String> readLastNLogLines(String nomFitxer, Long nLinies);
}
