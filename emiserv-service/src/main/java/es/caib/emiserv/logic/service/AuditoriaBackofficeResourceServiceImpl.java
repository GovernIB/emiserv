package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.intf.dto.AuditoriaFiltreDto;
import es.caib.emiserv.logic.intf.dto.AuditoriaPeticioDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.model.AuditoriaBackofficeResource;
import es.caib.emiserv.logic.intf.service.AuditoriaBackofficeResourceService;
import es.caib.emiserv.logic.intf.service.BackofficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Servei del recurs d'auditoria de les peticions de backoffice (interfície REACT). Delega a
 * BackofficeService, com la JSP (AuditoriaBackofficeController).
 *
 * @author Limit Tecnologies
 */
@Service
public class AuditoriaBackofficeResourceServiceImpl
		extends BaseAuditoriaResourceService<AuditoriaBackofficeResource, String>
		implements AuditoriaBackofficeResourceService {

	private static final List<String> COLUMNES_EXPORTACIO = Arrays.asList(
			AuditoriaBackofficeResource.Fields.dataPeticio,
			AuditoriaBackofficeResource.Fields.peticioId,
			AuditoriaBackofficeResource.Fields.procedimentCodiNom,
			AuditoriaBackofficeResource.Fields.serveiCodi,
			AuditoriaBackofficeResource.Fields.serveiDescripcio,
			AuditoriaBackofficeResource.Fields.estat,
			AuditoriaBackofficeResource.Fields.estatScsp,
			AuditoriaBackofficeResource.Fields.error,
			AuditoriaBackofficeResource.Fields.sincrona,
			AuditoriaBackofficeResource.Fields.numTransmissions,
			AuditoriaBackofficeResource.Fields.processadesPercent,
			AuditoriaBackofficeResource.Fields.dataResposta);

	@Autowired
	private BackofficeService backofficeService;

	@Override
	protected PaginaDto<AuditoriaPeticioDto> consultar(AuditoriaFiltreDto filtre, PaginacioParamsDto paginacio) {
		return backofficeService.peticioFindByFiltrePaginat(filtre, paginacio);
	}

	@Override
	protected AuditoriaPeticioDto consultarPerId(String id) {
		return backofficeService.peticioFindById(id);
	}

	@Override
	protected AuditoriaBackofficeResource toResource(AuditoriaPeticioDto dto, Long serveiId) {
		AuditoriaBackofficeResource resource = new AuditoriaBackofficeResource();
		resource.setId(dto.getPeticioId());
		resource.setPeticioId(dto.getPeticioId());
		resource.setDataPeticio(dto.getDataPeticio());
		resource.setDataResposta(dto.getDataResposta());
		resource.setProcedimentCodi(dto.getProcedimentCodi());
		resource.setProcedimentNom(dto.getProcedimentNom());
		resource.setProcedimentCodiNom(dto.getProcedimentCodiNom());
		resource.setServeiCodi(dto.getServeiCodi());
		resource.setServeiDescripcio(dto.getServeiDescripcio());
		resource.setServeiId(serveiId);
		resource.setEstat(dto.getEstat());
		resource.setEstatScsp(dto.getEstatScsp());
		resource.setEstatScspError(dto.isEstatScspError());
		resource.setError(dto.getError());
		resource.setSincrona(dto.isSincrona());
		resource.setTer(dto.getTer());
		resource.setNumEnviaments(dto.getNumEnviaments());
		resource.setNumTransmissions(dto.getNumTransmissions());
		resource.setDataDarreraComprovacio(dto.getDataDarreraComprovacio());
		resource.setProcessadesTotal(dto.getProcessadesTotal());
		resource.setProcessadesError(dto.getProcessadesError());
		resource.setProcessadesPercent(dto.getProcessadesPercent());
		resource.setComunicacioBackofficeDisponible(dto.isComunicacioBackofficeDisponible());
		resource.setComunicacioBackofficeError(dto.isComunicacioBackofficeError());
		return resource;
	}

	@Override
	protected List<String> getColumnesExportacio() {
		return COLUMNES_EXPORTACIO;
	}

	@Override
	protected String getNomFitxerExportacio() {
		return "auditoria-backoffice";
	}

}
