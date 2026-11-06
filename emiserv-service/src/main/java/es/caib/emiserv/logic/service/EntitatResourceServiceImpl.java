package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.helper.ConfiguracioResourceHelper;
import es.caib.emiserv.logic.intf.dto.EntitatDto;
import es.caib.emiserv.logic.intf.dto.EntitatFiltreDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.model.EntitatResource;
import es.caib.emiserv.logic.intf.service.EntitatResourceService;
import es.caib.emiserv.logic.intf.service.EntitatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Servei del recurs de configuració {@link EntitatResource} (interfície REACT). Delega a les mateixes
 * operacions que la JSP (EntitatController). El codi i el CIF no poden estar repetits (com
 * @CodiEntitatNoRepetit). La sincronització amb PINBAL és un camí propi de l'API
 * (EntitatApiController), perquè els seus missatges són de la JSP.
 *
 * @author Limit Tecnologies
 */
@Service
public class EntitatResourceServiceImpl
		extends BaseConfiguracioResourceService<EntitatResource, Long, EntitatDto>
		implements EntitatResourceService {

	private static final Map<String, String> CAMPS_ORDENABLES = new LinkedHashMap<>();
	static {
		for (String camp: new String[] { "codi", "nom", "cif", "unitatArrel" }) {
			CAMPS_ORDENABLES.put(camp, camp);
		}
	}

	@Autowired
	private EntitatService entitatService;

	@Override
	protected Map<String, String> getCampsOrdenables() {
		return CAMPS_ORDENABLES;
	}

	@Override
	protected String getOrdrePerDefecte() {
		return EntitatResource.Fields.codi;
	}

	@Override
	protected Collection<String> getCampsFiltrables() {
		return Arrays.asList(EntitatResource.Fields.codi, EntitatResource.Fields.nom, EntitatResource.Fields.cif, EntitatResource.Fields.unitatArrel);
	}

	@Override
	protected PaginaDto<EntitatDto> consultar(Map<String, String> filtre, String quickFilter, PaginacioParamsDto paginacio) {
		EntitatFiltreDto filtreDto = new EntitatFiltreDto();
		filtreDto.setCodi(ConfiguracioResourceHelper.valor(filtre, EntitatResource.Fields.codi));
		filtreDto.setNom(quickFilter != null ? quickFilter : ConfiguracioResourceHelper.valor(filtre, EntitatResource.Fields.nom));
		filtreDto.setCif(ConfiguracioResourceHelper.valor(filtre, EntitatResource.Fields.cif));
		filtreDto.setUnitatArrel(ConfiguracioResourceHelper.valor(filtre, EntitatResource.Fields.unitatArrel));
		return entitatService.findAllPaginat(filtreDto, paginacio);
	}

	@Override
	protected EntitatDto consultarPerId(Long id) {
		return entitatService.findById(id);
	}

	@Override
	protected void validar(EntitatResource resource, Long id) {
		EntitatDto perCodi = entitatService.findByCodi(resource.getCodi());
		comprovarNoRepetit(perCodi != null ? perCodi.getId() : null, id, "configuracio.codi.repetit", resource.getCodi());
		EntitatDto perCif = entitatService.findByCif(resource.getCif());
		comprovarNoRepetit(perCif != null ? perCif.getId() : null, id, "configuracio.cif.repetit", resource.getCif());
	}

	@Override
	protected EntitatDto crear(EntitatDto dto) {
		return entitatService.create(dto);
	}

	@Override
	protected void actualitzar(EntitatDto dto) {
		entitatService.update(dto);
	}

	@Override
	protected void esborrar(Long id) {
		entitatService.delete(id);
	}

	@Override
	protected EntitatResource toResource(EntitatDto dto) {
		EntitatResource resource = new EntitatResource();
		resource.setId(dto.getId());
		resource.setCodi(dto.getCodi());
		resource.setNom(dto.getNom());
		resource.setCif(dto.getCif());
		resource.setUnitatArrel(dto.getUnitatArrel());
		return resource;
	}

	@Override
	protected EntitatDto toDto(EntitatResource resource) {
		return EntitatDto.builder().
				id(resource.getId()).
				codi(resource.getCodi()).
				nom(resource.getNom()).
				cif(resource.getCif()).
				unitatArrel(resource.getUnitatArrel()).
				build();
	}

}
