/**
 * 
 */
package es.caib.emiserv.ejb;

import es.caib.comanda.model.server.monitoring.ContextInfo;
import es.caib.comanda.model.server.monitoring.FitxerContingut;
import es.caib.comanda.model.server.monitoring.FitxerInfo;
import es.caib.comanda.model.server.monitoring.IntegracioInfo;
import es.caib.comanda.model.server.monitoring.IntegracioSalut;
import es.caib.comanda.model.server.monitoring.MissatgeSalut;
import es.caib.comanda.model.server.monitoring.SubsistemaInfo;
import es.caib.comanda.model.server.monitoring.SubsistemaSalut;
import es.caib.comanda.ms.log.helper.LogFileStream;
import es.caib.emiserv.logic.intf.dto.SubsistemesEnum;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AplicacioService extends AbstractService<es.caib.emiserv.logic.intf.service.AplicacioService> implements es.caib.emiserv.logic.intf.service.AplicacioService {

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public String getIdiomaUsuariActual() {
		return getDelegateService().getIdiomaUsuariActual();
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public void updateIdiomaUsuariActual(String idioma) {
		getDelegateService().updateIdiomaUsuariActual(idioma);
	}

	@Override
	@PermitAll
	public void propagateDbProperties() {
		getDelegateService().propagateDbProperties();
	}

	@Override
	@PermitAll
	public void addSubsistemaExit(SubsistemesEnum subsistema, String serveiCodi, long duracioMs) {
		getDelegateService().addSubsistemaExit(subsistema, serveiCodi, duracioMs);
	}

	@Override
	@PermitAll
	public void addSubsistemaError(SubsistemesEnum subsistema, String serveiCodi) {
		getDelegateService().addSubsistemaError(subsistema, serveiCodi);
	}

    @Override
	@PermitAll
    public void addSubsistemaError(SubsistemesEnum subsistema) {
        getDelegateService().addSubsistemaError(subsistema);
    }

	@Override
	@PermitAll
	public void addIntegracioExit(String solicitantId, String serveiCodi, long duracioMs) {
		getDelegateService().addIntegracioExit(solicitantId, serveiCodi, duracioMs);
	}

	@Override
	@PermitAll
	public void addIntegracioError(String solicitantId, String serveiCodi) {
		getDelegateService().addIntegracioError(solicitantId, serveiCodi);
	}

    @Override
	@RolesAllowed({"EMS_COM"})
    public List<IntegracioInfo> getIntegracionsInfo() {
        return getDelegateService().getIntegracionsInfo();
    }

	@Override
	@PermitAll
	public List<IntegracioSalut> getIntegracionsSalut() {
		return getDelegateService().getIntegracionsSalut();
	}

	@Override
	@PermitAll
	public List<IntegracioSalut> getIntegracionsSalut(OffsetDateTime dataPeriode, OffsetDateTime dataTotal) {
		return getDelegateService().getIntegracionsSalut(dataPeriode, dataTotal);
	}

	@Override
	@RolesAllowed({"EMS_COM"})
	public List<SubsistemaInfo> getSubsistemesInfo() {
		return getDelegateService().getSubsistemesInfo();
	}

	@Override
	@PermitAll
	public List<SubsistemaSalut> getSubsistemesSalut() {
		return getDelegateService().getSubsistemesSalut();
	}

	@Override
	@RolesAllowed({"EMS_COM"})
	public List<ContextInfo> getContextsInfo(String baseUrl) {
		return getDelegateService().getContextsInfo(baseUrl);
	}

	@Override
	@PermitAll
	public List<MissatgeSalut> getMissatgesSalut() {
		return getDelegateService().getMissatgesSalut();
	}

	@Override
	@PermitAll
	public Integer measureDbLatencyMs() {
		return getDelegateService().measureDbLatencyMs();
	}

    @Override
	@RolesAllowed({"EMS_COM"})
    public List<FitxerInfo> llistarLogFiles() {
        return getDelegateService().llistarLogFiles();
    }

	@Override
	@RolesAllowed({"EMS_COM"})
	public FitxerContingut getLogFileByNom(String nomFitxer) {
		return getDelegateService().getLogFileByNom(nomFitxer);
	}

	@Override
	@RolesAllowed({"EMS_COM"})
	public LogFileStream getFileLogStreamByNom(String nomFitxer) {
		return getDelegateService().getFileLogStreamByNom(nomFitxer);
	}

	@Override
	@RolesAllowed({"EMS_COM"})
	public List<String> readLastNLogLines(String nomFitxer, Long nLinies) {
		return getDelegateService().readLastNLogLines(nomFitxer, nLinies);
	}

}
