package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.helper.ConfiguracioResourceHelper;
import es.caib.emiserv.logic.intf.dto.AutoritatCertificacioDto;
import es.caib.emiserv.logic.intf.dto.AutoritatCertificacioFiltreDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.model.AutoritatCertificacioResource;
import es.caib.emiserv.logic.intf.service.AutoritatCertificacioResourceService;
import es.caib.emiserv.logic.intf.service.ScspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Servei del recurs de configuració {@link AutoritatCertificacioResource} (interfície REACT). Delega
 * a les mateixes operacions que la JSP (AutoritatCertificacioController).
 *
 * @author Limit Tecnologies
 */
@Service
public class AutoritatCertificacioResourceServiceImpl
		extends BaseConfiguracioResourceService<AutoritatCertificacioResource, Long, AutoritatCertificacioDto>
		implements AutoritatCertificacioResourceService {

	private static final Map<String, String> CAMPS_ORDENABLES = new LinkedHashMap<>();
	static {
		CAMPS_ORDENABLES.put("nombre", "nombre");
		CAMPS_ORDENABLES.put("codca", "codca");
	}

	@Autowired
	private ScspService scspService;

	@Override
	protected Map<String, String> getCampsOrdenables() {
		return CAMPS_ORDENABLES;
	}

	@Override
	protected String getOrdrePerDefecte() {
		return AutoritatCertificacioResource.Fields.nombre;
	}

	@Override
	protected Collection<String> getCampsFiltrables() {
		return Arrays.asList(AutoritatCertificacioResource.Fields.nombre, AutoritatCertificacioResource.Fields.codca);
	}

	@Override
	protected PaginaDto<AutoritatCertificacioDto> consultar(Map<String, String> filtre, String quickFilter, PaginacioParamsDto paginacio) {
		AutoritatCertificacioFiltreDto filtreDto = new AutoritatCertificacioFiltreDto();
		filtreDto.setNom(quickFilter != null ? quickFilter : ConfiguracioResourceHelper.valor(filtre, AutoritatCertificacioResource.Fields.nombre));
		filtreDto.setCodi(ConfiguracioResourceHelper.valor(filtre, AutoritatCertificacioResource.Fields.codca));
		return scspService.autoritatCertificacioFindByFiltrePaginat(filtreDto, paginacio);
	}

	@Override
	protected AutoritatCertificacioDto consultarPerId(Long id) {
		return scspService.autoritatCertificacioFindById(id);
	}

	@Override
	protected AutoritatCertificacioDto crear(AutoritatCertificacioDto dto) {
		return scspService.autoritatCertificacioCreate(dto);
	}

	@Override
	protected void actualitzar(AutoritatCertificacioDto dto) {
		scspService.autoritatCertificacioUpdate(dto);
	}

	@Override
	protected void esborrar(Long id) {
		scspService.autoritatCertificacioDelete(id);
	}

	@Override
	protected AutoritatCertificacioResource toResource(AutoritatCertificacioDto dto) {
		AutoritatCertificacioResource resource = new AutoritatCertificacioResource();
		resource.setId(dto.getId());
		resource.setCodca(dto.getCodca());
		resource.setNombre(dto.getNombre());
		return resource;
	}

	@Override
	protected AutoritatCertificacioDto toDto(AutoritatCertificacioResource resource) {
		AutoritatCertificacioDto dto = new AutoritatCertificacioDto();
		dto.setId(resource.getId());
		dto.setCodca(resource.getCodca());
		dto.setNombre(resource.getNombre());
		return dto;
	}

}
