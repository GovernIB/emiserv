/**
 * 
 */
package es.caib.emiserv.ejb;

import es.caib.emiserv.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.emiserv.logic.intf.dto.CarregaDto;
import es.caib.emiserv.logic.intf.dto.EstadisticaDto;
import es.caib.emiserv.logic.intf.dto.EstadistiquesFiltreDto;
import es.caib.emiserv.logic.intf.dto.InformeGeneralEstatDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

/**
 * Implementació de ServeiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@RolesAllowed("EMS_REPORT")
public class ExplotacioService extends AbstractService<es.caib.emiserv.logic.intf.service.ExplotacioService> implements es.caib.emiserv.logic.intf.service.ExplotacioService {

	@Override
	public List<InformeGeneralEstatDto> informeGeneralEstat(
			Date dataInici, 
			Date dataFi,
			ServeiTipusEnumDto tipusPeticio) {
		return getDelegateService().informeGeneralEstat(dataInici, dataFi, tipusPeticio);
	}

	@PermitAll
    @Override
    public List<DadesObertesRespostaConsulta> findOpenData(
			String entitatCodi,
			Date dataInici,
			Date dataFi,
			String procedimentCodi,
			String serveiCodi) {
        return getDelegateService().findOpenData(entitatCodi, dataInici, dataFi, procedimentCodi, serveiCodi);
    }

	@Override
	public List<CarregaDto> findEstadistiquesCarrega() {
		return getDelegateService().findEstadistiquesCarrega();
	}

	@Override
	public List<EstadisticaDto> findEstadistiquesByFiltre(EstadistiquesFiltreDto filtre) {
		return getDelegateService().findEstadistiquesByFiltre(filtre);
	}
}
