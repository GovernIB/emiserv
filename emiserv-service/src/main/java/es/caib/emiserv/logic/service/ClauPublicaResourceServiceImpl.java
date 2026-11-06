package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.intf.dto.ClauPublicaDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.model.ClauPublicaResource;
import es.caib.emiserv.logic.intf.service.ClauPublicaResourceService;
import es.caib.emiserv.logic.intf.service.ScspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Servei del recurs de configuració {@link ClauPublicaResource} (interfície REACT). Delega a les
 * mateixes operacions que la JSP (ClauPublicaController); el llistat no té filtre, com la JSP.
 *
 * @author Limit Tecnologies
 */
@Service
public class ClauPublicaResourceServiceImpl
		extends BaseConfiguracioResourceService<ClauPublicaResource, Long, ClauPublicaDto>
		implements ClauPublicaResourceService {

	private static final Map<String, String> CAMPS_ORDENABLES = new LinkedHashMap<>();
	static {
		for (String camp: new String[] { "alies", "nom", "numSerie", "dataAlta", "dataBaixa" }) {
			CAMPS_ORDENABLES.put(camp, camp);
		}
	}

	@Autowired
	private ScspService scspService;

	@Override
	protected Map<String, String> getCampsOrdenables() {
		return CAMPS_ORDENABLES;
	}

	@Override
	protected String getOrdrePerDefecte() {
		return ClauPublicaResource.Fields.alies;
	}

	@Override
	protected PaginaDto<ClauPublicaDto> consultar(Map<String, String> filtre, String quickFilter, PaginacioParamsDto paginacio) {
		return scspService.clauPublicaFindByFiltrePaginat(paginacio);
	}

	@Override
	protected ClauPublicaDto consultarPerId(Long id) {
		return scspService.findClauPublicaById(id);
	}

	@Override
	protected ClauPublicaDto crear(ClauPublicaDto dto) {
		return scspService.clauPublicaCreate(dto);
	}

	@Override
	protected void actualitzar(ClauPublicaDto dto) {
		scspService.clauPublicaUpdate(dto);
	}

	@Override
	protected void esborrar(Long id) {
		scspService.clauPublicaDelete(id);
	}

	@Override
	protected ClauPublicaResource toResource(ClauPublicaDto dto) {
		ClauPublicaResource resource = new ClauPublicaResource();
		resource.setId(dto.getId());
		resource.setAlies(dto.getAlies());
		resource.setNom(dto.getNom());
		resource.setNumSerie(dto.getNumSerie());
		resource.setDataAlta(dto.getDataAlta());
		resource.setDataBaixa(dto.getDataBaixa());
		return resource;
	}

	@Override
	protected ClauPublicaDto toDto(ClauPublicaResource resource) {
		ClauPublicaDto dto = new ClauPublicaDto();
		dto.setId(resource.getId());
		dto.setAlies(resource.getAlies());
		dto.setNom(resource.getNom());
		dto.setNumSerie(resource.getNumSerie());
		dto.setDataAlta(resource.getDataAlta());
		dto.setDataBaixa(resource.getDataBaixa());
		return dto;
	}

}
