package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.intf.dto.OrganismeDto;
import es.caib.emiserv.logic.intf.model.OrganismeResource;
import es.caib.emiserv.logic.intf.service.OrganismeResourceService;
import es.caib.emiserv.logic.intf.dto.OrganismeFiltreDto;
import java.util.Arrays;
import java.util.Collection;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.service.ScspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Servei del recurs de configuració {@link OrganismeResource} (interfície REACT). Delega a les mateixes
 * operacions que la JSP.
 *
 * @author Limit Tecnologies
 */
@Service
public class OrganismeResourceServiceImpl
		extends BaseConfiguracioResourceService<OrganismeResource, Long, OrganismeDto>
		implements OrganismeResourceService {

	@Autowired
	private ScspService scspService;

	private static final Map<String, String> CAMPS_ORDENABLES = new LinkedHashMap<>();
	static {
		CAMPS_ORDENABLES.put("cif", "idorganismo");
		CAMPS_ORDENABLES.put("nom", "nombreOrganismo");
		CAMPS_ORDENABLES.put("dataAlta", "fechaAlta");
		CAMPS_ORDENABLES.put("dataBaixa", "fechaBaja");
	}

	@Override
	protected Map<String, String> getCampsOrdenables() {
		return CAMPS_ORDENABLES;
	}

	@Override
	protected String getOrdrePerDefecte() {
		return OrganismeResource.Fields.nom;
	}

	@Override
	protected Collection<String> getCampsFiltrables() {
		return Arrays.asList(OrganismeResource.Fields.nom, OrganismeResource.Fields.cif);
	}

	@Override
	protected PaginaDto<OrganismeDto> consultar(Map<String, String> filtre, String quickFilter, PaginacioParamsDto paginacio) {
		OrganismeFiltreDto filtreDto = new OrganismeFiltreDto();
		// El filtre ràpid cerca pel nom
		filtreDto.setNom(quickFilter != null ? quickFilter : valor(filtre, OrganismeResource.Fields.nom));
		filtreDto.setCif(valor(filtre, OrganismeResource.Fields.cif));
		return scspService.organismeFindByFiltrePaginat(filtreDto, paginacio);
	}

	@Override
	protected OrganismeDto consultarPerId(Long id) {
		return scspService.organismeFindById(id);
	}

	@Override
	protected void validar(OrganismeResource resource, Long id) {
		// Com la validació @CifOrganismeNoRepetit de la JSP
		OrganismeDto existent = scspService.organismeFindByCif(resource.getCif());
		comprovarNoRepetit(existent != null ? existent.getId() : null, id, "configuracio.cif.repetit", resource.getCif());
	}

	@Override
	protected OrganismeDto crear(OrganismeDto dto) {
		return scspService.organismeCreate(dto);
	}

	@Override
	protected void actualitzar(OrganismeDto dto) {
		scspService.organismeUpdate(dto);
	}

	@Override
	protected void esborrar(Long id) {
		scspService.organismeDelete(id);
	}

	@Override
	protected OrganismeResource toResource(OrganismeDto dto) {
		OrganismeResource resource = new OrganismeResource();
		resource.setId(dto.getId());
		resource.setNom(dto.getNom());
		resource.setCif(dto.getCif());
		resource.setDataAlta(dto.getDataAlta());
		resource.setDataBaixa(dto.getDataBaixa());
		return resource;
	}

	@Override
	protected OrganismeDto toDto(OrganismeResource resource) {
		OrganismeDto dto = new OrganismeDto();
		dto.setId(resource.getId());
		dto.setNom(resource.getNom());
		dto.setCif(resource.getCif());
		dto.setDataAlta(resource.getDataAlta());
		dto.setDataBaixa(resource.getDataBaixa());
		return dto;
	}

	private static String valor(Map<String, String> filtre, String camp) {
		return es.caib.emiserv.logic.helper.ConfiguracioResourceHelper.valor(filtre, camp);
	}

}
