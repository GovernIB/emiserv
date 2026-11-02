package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.dto.ScspModulDto;
import es.caib.emiserv.logic.intf.model.ScspModulResource;
import es.caib.emiserv.logic.intf.service.ScspModulResourceService;
import es.caib.emiserv.logic.intf.service.ScspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Servei del recurs de configuració {@link ScspModulResource} (interfície REACT). Com la JSP
 * (ScspModulController), només es poden modificar els indicadors d'entrada i sortida actives; el nom
 * i la descripció no canvien.
 *
 * @author Limit Tecnologies
 */
@Service
public class ScspModulResourceServiceImpl
		extends BaseConfiguracioResourceService<ScspModulResource, String, ScspModulDto>
		implements ScspModulResourceService {

	private static final Map<String, String> CAMPS_ORDENABLES = new LinkedHashMap<>();
	static {
		for (String camp: new String[] { "nom", "descripcio", "actiuEntrada", "actiuSortida" }) {
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
		return ScspModulResource.Fields.nom;
	}

	@Override
	protected PaginaDto<ScspModulDto> consultar(Map<String, String> filtre, String quickFilter, PaginacioParamsDto paginacio) {
		return scspService.getScspModuls(paginacio);
	}

	@Override
	protected ScspModulDto consultarPerId(String id) {
		return scspService.getScspModul(id);
	}

	@Override
	protected void actualitzar(ScspModulDto dto) {
		ScspModulDto actual = scspService.getScspModul(dto.getNom());
		actual.setActiuEntrada(dto.isActiuEntrada());
		actual.setActiuSortida(dto.isActiuSortida());
		scspService.updateScspModul(actual);
	}

	@Override
	protected ScspModulResource toResource(ScspModulDto dto) {
		ScspModulResource resource = new ScspModulResource();
		resource.setId(dto.getNom());
		resource.setNom(dto.getNom());
		resource.setDescripcio(dto.getDescripcio());
		resource.setActiuEntrada(dto.isActiuEntrada());
		resource.setActiuSortida(dto.isActiuSortida());
		return resource;
	}

	@Override
	protected ScspModulDto toDto(ScspModulResource resource) {
		ScspModulDto dto = new ScspModulDto();
		dto.setNom(resource.getId());
		dto.setDescripcio(resource.getDescripcio());
		dto.setActiuEntrada(resource.isActiuEntrada());
		dto.setActiuSortida(resource.isActiuSortida());
		return dto;
	}

}
