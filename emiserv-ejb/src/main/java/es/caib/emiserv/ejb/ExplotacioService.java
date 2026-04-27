/**
 * 
 */
package es.caib.emiserv.ejb;

import es.caib.comanda.model.server.monitoring.DimensioDesc;
import es.caib.comanda.model.server.monitoring.IndicadorDesc;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;
import es.caib.emiserv.client.dadesobertes.DadesObertesResposta;
import es.caib.emiserv.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.emiserv.logic.intf.dto.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Implementació de ServeiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@RolesAllowed({"EMS_REPORT", "EMS_ADMIN"})
public class ExplotacioService extends AbstractService<es.caib.emiserv.logic.intf.service.ExplotacioService> implements es.caib.emiserv.logic.intf.service.ExplotacioService {

	@Override
	public List<InformeGeneralEstatDto> informeGeneralEstat(
			Date dataInici, 
			Date dataFi,
			ServeiTipusEnumDto tipusPeticio) {
		return getDelegateService().informeGeneralEstat(dataInici, dataFi, tipusPeticio);
	}

	@Override
	public List<InformeEmisorEnrutatDto> informeEmisorEnrutat(Date dataInici, Date dataFi) {
		return getDelegateService().informeEmisorEnrutat(dataInici, dataFi);
	}

	@PermitAll
    @Override
    public List<DadesObertesRespostaConsulta> findOpenData(
			String entitatNif,
			Date dataInici,
			Date dataFi,
			String procedimentCodi,
			String serveiCodi) {
        return getDelegateService().findOpenData(entitatNif, dataInici, dataFi, procedimentCodi, serveiCodi);
    }

	@PermitAll
    @Override
    public DadesObertesResposta findOpenDataV2(ConsultaOpenDataDto consultaOpenDataDto) {
		return getDelegateService().findOpenDataV2(consultaOpenDataDto);
    }

    @Override
	public List<CarregaDto> findEstadistiquesCarrega() {
		return getDelegateService().findEstadistiquesCarrega();
	}

	@Override
	public List<EstadisticaDto> findEstadistiquesByFiltre(EstadistiquesFiltreDto filtre) {
		return getDelegateService().findEstadistiquesByFiltre(filtre);
	}

    @Override
	@RolesAllowed("EMS_COM")
    public List<DimensioDesc> getDimensions() {
        return getDelegateService().getDimensions();
    }

	@Override
	@RolesAllowed("EMS_COM")
	public List<IndicadorDesc> getIndicadors() {
		return getDelegateService().getIndicadors();
	}

    @Override
	@RolesAllowed("EMS_COM")
    public RegistresEstadistics consultaUltimesEstadistiques() {
        return getDelegateService().consultaUltimesEstadistiques();
    }

	@Override
	@RolesAllowed("EMS_COM")
	public RegistresEstadistics consultaEstadistiques(LocalDate date) {
		return getDelegateService().consultaEstadistiques(date);
	}

	@Override
	@RolesAllowed("EMS_COM")
	public List<RegistresEstadistics> consultaEstadistiques(LocalDate iniDate, LocalDate fiDate) {
		return getDelegateService().consultaEstadistiques(iniDate, fiDate);
	}
}
