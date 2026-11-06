package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.intf.dto.AuditoriaFiltreDto;
import es.caib.emiserv.logic.intf.dto.AuditoriaPeticioDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.model.AuditoriaEnrutadorResource;
import es.caib.emiserv.logic.intf.service.AuditoriaEnrutadorResourceService;
import es.caib.emiserv.logic.intf.service.RedireccioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Servei del recurs d'auditoria de les peticions de l'enrutador (interfície REACT). Delega a
 * RedireccioService, com la JSP (AuditoriaEnrutadorController).
 *
 * @author Limit Tecnologies
 */
@Service
public class AuditoriaEnrutadorResourceServiceImpl
		extends BaseAuditoriaResourceService<AuditoriaEnrutadorResource, Long>
		implements AuditoriaEnrutadorResourceService {

	private static final List<String> COLUMNES_EXPORTACIO = Arrays.asList(
			AuditoriaEnrutadorResource.Fields.dataPeticio,
			AuditoriaEnrutadorResource.Fields.peticioId,
			AuditoriaEnrutadorResource.Fields.procedimentCodiNom,
			AuditoriaEnrutadorResource.Fields.serveiCodi,
			AuditoriaEnrutadorResource.Fields.serveiDescripcio,
			AuditoriaEnrutadorResource.Fields.serveiTipus,
			AuditoriaEnrutadorResource.Fields.entitatCodi,
			AuditoriaEnrutadorResource.Fields.estat,
			AuditoriaEnrutadorResource.Fields.error,
			AuditoriaEnrutadorResource.Fields.dataResposta);

	@Autowired
	private RedireccioService redireccioService;

	@Override
	protected PaginaDto<AuditoriaPeticioDto> consultar(AuditoriaFiltreDto filtre, PaginacioParamsDto paginacio) {
		return redireccioService.peticioFindByFiltrePaginat(filtre, paginacio);
	}

	@Override
	protected AuditoriaPeticioDto consultarPerId(Long id) {
		return redireccioService.peticioFindById(id);
	}

	@Override
	protected AuditoriaEnrutadorResource toResource(AuditoriaPeticioDto dto, Long serveiId) {
		AuditoriaEnrutadorResource resource = new AuditoriaEnrutadorResource();
		resource.setId(dto.getId());
		resource.setPeticioId(dto.getPeticioId());
		resource.setDataPeticio(dto.getDataPeticio());
		resource.setDataResposta(dto.getDataResposta());
		resource.setProcedimentCodi(dto.getProcedimentCodi());
		resource.setProcedimentNom(dto.getProcedimentNom());
		resource.setProcedimentCodiNom(dto.getProcedimentCodiNom());
		resource.setServeiCodi(dto.getServeiCodi());
		resource.setServeiDescripcio(dto.getServeiDescripcio());
		resource.setServeiId(serveiId);
		resource.setServeiTipus(dto.getServeiTipus());
		resource.setEntitatCodi(dto.getEntitatCodi());
		resource.setEstat(dto.getEstat());
		resource.setError(dto.getError());
		resource.setTeRespostes(dto.isTeRespostes());
		return resource;
	}

	@Override
	protected List<String> getColumnesExportacio() {
		return COLUMNES_EXPORTACIO;
	}

	@Override
	protected String getNomFitxerExportacio() {
		return "auditoria-enrutador";
	}

}
