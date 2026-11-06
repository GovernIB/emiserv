package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.intf.dto.OrganismeCessionariDto;
import es.caib.emiserv.logic.intf.model.OrganismeCessionariResource;
import es.caib.emiserv.logic.intf.service.OrganismeCessionariResourceService;
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
 * Servei del recurs de configuració {@link OrganismeCessionariResource} (interfície REACT). Delega a les mateixes
 * operacions que la JSP.
 *
 * @author Limit Tecnologies
 */
@Service
public class OrganismeCessionariResourceServiceImpl
		extends BaseConfiguracioResourceService<OrganismeCessionariResource, Long, OrganismeCessionariDto>
		implements OrganismeCessionariResourceService {

	@Autowired
	private ScspService scspService;

	private static final Map<String, String> CAMPS_ORDENABLES = new LinkedHashMap<>();
	static {
		CAMPS_ORDENABLES.put("cif", "cif");
		CAMPS_ORDENABLES.put("nom", "nom");
		CAMPS_ORDENABLES.put("dataAlta", "dataAlta");
		CAMPS_ORDENABLES.put("dataBaixa", "dataBaixa");
		CAMPS_ORDENABLES.put("codiUnitatTramitadora", "codiUnitatTramitadora");
		CAMPS_ORDENABLES.put("bloquejat", "bloquejat");
	}

	@Override
	protected Map<String, String> getCampsOrdenables() {
		return CAMPS_ORDENABLES;
	}

	@Override
	protected String getOrdrePerDefecte() {
		return OrganismeCessionariResource.Fields.nom;
	}

	@Override
	protected Collection<String> getCampsFiltrables() {
		return Arrays.asList(OrganismeCessionariResource.Fields.nom, OrganismeCessionariResource.Fields.cif);
	}

	@Override
	protected PaginaDto<OrganismeCessionariDto> consultar(Map<String, String> filtre, String quickFilter, PaginacioParamsDto paginacio) {
		OrganismeFiltreDto filtreDto = new OrganismeFiltreDto();
		// El filtre ràpid cerca pel nom
		filtreDto.setNom(quickFilter != null ? quickFilter : valor(filtre, OrganismeCessionariResource.Fields.nom));
		filtreDto.setCif(valor(filtre, OrganismeCessionariResource.Fields.cif));
		return scspService.organismeCessionariFindByFiltrePaginat(filtreDto, paginacio);
	}

	@Override
	protected OrganismeCessionariDto consultarPerId(Long id) {
		return scspService.organismeCessionariFindById(id);
	}

	@Override
	protected void validar(OrganismeCessionariResource resource, Long id) {
		// Com la validació @CifOrganismeCessionariNoRepetit de la JSP
		OrganismeCessionariDto existent = scspService.organismeCessionariFindByCif(resource.getCif());
		comprovarNoRepetit(existent != null ? existent.getId() : null, id, "configuracio.cif.repetit", resource.getCif());
	}

	@Override
	protected OrganismeCessionariDto crear(OrganismeCessionariDto dto) {
		return scspService.organismeCessionariCreate(dto);
	}

	@Override
	protected void actualitzar(OrganismeCessionariDto dto) {
		scspService.organismeCessionariUpdate(dto);
	}

	@Override
	protected void esborrar(Long id) {
		scspService.organismeCessionariDelete(id);
	}

	@Override
	protected OrganismeCessionariResource toResource(OrganismeCessionariDto dto) {
		OrganismeCessionariResource resource = new OrganismeCessionariResource();
		resource.setId(dto.getId());
		resource.setNom(dto.getNom());
		resource.setCif(dto.getCif());
		resource.setDataAlta(dto.getDataAlta());
		resource.setDataBaixa(dto.getDataBaixa());
		resource.setCodiUnitatTramitadora(dto.getCodiUnitatTramitadora());
		resource.setBloquejat(Boolean.TRUE.equals(dto.getBloquejat()));
		return resource;
	}

	@Override
	protected OrganismeCessionariDto toDto(OrganismeCessionariResource resource) {
		OrganismeCessionariDto dto = new OrganismeCessionariDto();
		dto.setId(resource.getId());
		dto.setNom(resource.getNom());
		dto.setCif(resource.getCif());
		dto.setDataAlta(resource.getDataAlta());
		dto.setDataBaixa(resource.getDataBaixa());
		dto.setCodiUnitatTramitadora(resource.getCodiUnitatTramitadora());
		dto.setBloquejat(resource.isBloquejat());
		return dto;
	}

	private static String valor(Map<String, String> filtre, String camp) {
		return es.caib.emiserv.logic.helper.ConfiguracioResourceHelper.valor(filtre, camp);
	}

}
