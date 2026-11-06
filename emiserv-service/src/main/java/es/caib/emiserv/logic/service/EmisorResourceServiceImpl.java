package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.intf.dto.EmisorDto;
import es.caib.emiserv.logic.intf.model.EmisorResource;
import es.caib.emiserv.logic.intf.service.EmisorResourceService;
import es.caib.emiserv.logic.intf.dto.EmisorFiltreDto;
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
 * Servei del recurs de configuració {@link EmisorResource} (interfície REACT). Delega a les mateixes
 * operacions que la JSP.
 *
 * @author Limit Tecnologies
 */
@Service
public class EmisorResourceServiceImpl
		extends BaseConfiguracioResourceService<EmisorResource, Long, EmisorDto>
		implements EmisorResourceService {

	@Autowired
	private ScspService scspService;

	private static final Map<String, String> CAMPS_ORDENABLES = new LinkedHashMap<>();
	static {
		CAMPS_ORDENABLES.put("cif", "cif");
		CAMPS_ORDENABLES.put("nom", "nombre");
		CAMPS_ORDENABLES.put("dataAlta", "fechaAlta");
		CAMPS_ORDENABLES.put("dataBaixa", "fechaBaja");
	}

	@Override
	protected Map<String, String> getCampsOrdenables() {
		return CAMPS_ORDENABLES;
	}

	@Override
	protected String getOrdrePerDefecte() {
		return EmisorResource.Fields.nom;
	}

	@Override
	protected Collection<String> getCampsFiltrables() {
		return Arrays.asList(EmisorResource.Fields.nom, EmisorResource.Fields.cif);
	}

	@Override
	protected PaginaDto<EmisorDto> consultar(Map<String, String> filtre, String quickFilter, PaginacioParamsDto paginacio) {
		EmisorFiltreDto filtreDto = new EmisorFiltreDto();
		// El filtre ràpid cerca pel nom
		filtreDto.setNom(quickFilter != null ? quickFilter : valor(filtre, EmisorResource.Fields.nom));
		filtreDto.setCif(valor(filtre, EmisorResource.Fields.cif));
		return scspService.emisorFindByFiltrePaginat(filtreDto, paginacio);
	}

	@Override
	protected EmisorDto consultarPerId(Long id) {
		return scspService.emisorFindById(id);
	}

	@Override
	protected void validar(EmisorResource resource, Long id) {
		// Com la validació @CifEmisorNoRepetit de la JSP
		EmisorDto existent = scspService.emisorFindByCif(resource.getCif());
		comprovarNoRepetit(existent != null ? existent.getId() : null, id, "configuracio.cif.repetit", resource.getCif());
	}

	@Override
	protected EmisorDto crear(EmisorDto dto) {
		return scspService.emisorCreate(dto);
	}

	@Override
	protected void actualitzar(EmisorDto dto) {
		scspService.emisorUpdate(dto);
	}

	@Override
	protected void esborrar(Long id) {
		scspService.emisorDelete(id);
	}

	@Override
	protected EmisorResource toResource(EmisorDto dto) {
		EmisorResource resource = new EmisorResource();
		resource.setId(dto.getId());
		resource.setNom(dto.getNom());
		resource.setCif(dto.getCif());
		resource.setDataAlta(dto.getDataAlta());
		resource.setDataBaixa(dto.getDataBaixa());
		return resource;
	}

	@Override
	protected EmisorDto toDto(EmisorResource resource) {
		EmisorDto dto = new EmisorDto();
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
