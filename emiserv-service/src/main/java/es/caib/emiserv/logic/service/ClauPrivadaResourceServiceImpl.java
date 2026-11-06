package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.intf.base.model.FieldOption;
import es.caib.emiserv.logic.intf.dto.ClauPrivadaDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.logic.intf.model.ClauPrivadaResource;
import es.caib.emiserv.logic.intf.service.ClauPrivadaResourceService;
import es.caib.emiserv.logic.intf.service.ScspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servei del recurs de configuració {@link ClauPrivadaResource} (interfície REACT). Delega a les
 * mateixes operacions que la JSP (ClauPrivadaController).
 * <p/>
 * La contrasenya de la clau no surt mai cap a la interfície: el recurs la torna buida i, en
 * modificar, una contrasenya buida conserva l'actual. A l'alta és obligatòria.
 *
 * @author Limit Tecnologies
 */
@Service
public class ClauPrivadaResourceServiceImpl
		extends BaseConfiguracioResourceService<ClauPrivadaResource, Long, ClauPrivadaDto>
		implements ClauPrivadaResourceService {

	private static final Map<String, String> CAMPS_ORDENABLES = new LinkedHashMap<>();
	static {
		for (String camp: new String[] { "alies", "nom", "numSerie", "dataAlta", "dataBaixa" }) {
			CAMPS_ORDENABLES.put(camp, camp);
		}
	}

	@Autowired
	private ScspService scspService;

	@PostConstruct
	public void init() {
		// Opcions: els organismes cessionaris del formulari de la JSP
		register(ClauPrivadaResource.Fields.organisme, (FieldOptionsProvider)(fieldName, params) ->
				scspService.findAllOrganismeCessionari().stream().
						sorted(Comparator.comparing(o -> o.getNom() != null ? o.getNom() : "", String.CASE_INSENSITIVE_ORDER)).
						map(o -> new FieldOption(String.valueOf(o.getId()), o.getNom() + " (" + o.getCif() + ")")).
						collect(Collectors.toList()));
	}

	@Override
	protected Map<String, String> getCampsOrdenables() {
		return CAMPS_ORDENABLES;
	}

	@Override
	protected String getOrdrePerDefecte() {
		return ClauPrivadaResource.Fields.alies;
	}

	@Override
	protected PaginaDto<ClauPrivadaDto> consultar(Map<String, String> filtre, String quickFilter, PaginacioParamsDto paginacio) {
		return scspService.clauPrivadaFindByFiltrePaginat(paginacio);
	}

	@Override
	protected ClauPrivadaDto consultarPerId(Long id) {
		return scspService.findClauPrivadaById(id);
	}

	@Override
	protected void validar(ClauPrivadaResource resource, Long id) {
		if (id == null && (resource.getPassword() == null || resource.getPassword().isEmpty())) {
			throw new ValidationException(configuracioResourceHelper.missatge("configuracio.password.obligatori"));
		}
	}

	@Override
	protected ClauPrivadaDto crear(ClauPrivadaDto dto) {
		return scspService.clauPrivadaCreate(dto);
	}

	@Override
	protected void actualitzar(ClauPrivadaDto dto) {
		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			dto.setPassword(scspService.findClauPrivadaById(dto.getId()).getPassword());
		}
		scspService.clauPrivadaUpdate(dto);
	}

	@Override
	protected void esborrar(Long id) {
		scspService.clauPrivadaDelete(id);
	}

	@Override
	protected ClauPrivadaResource toResource(ClauPrivadaDto dto) {
		ClauPrivadaResource resource = new ClauPrivadaResource();
		resource.setId(dto.getId());
		resource.setAlies(dto.getAlies());
		resource.setNom(dto.getNom());
		resource.setNumSerie(dto.getNumSerie());
		resource.setDataAlta(dto.getDataAlta());
		resource.setDataBaixa(dto.getDataBaixa());
		resource.setInteroperabilitat(dto.getInteroperabilitat());
		resource.setOrganisme(dto.getOrganisme() != null ? String.valueOf(dto.getOrganisme()) : null);
		// La contrasenya no es torna mai
		return resource;
	}

	@Override
	protected ClauPrivadaDto toDto(ClauPrivadaResource resource) {
		ClauPrivadaDto dto = new ClauPrivadaDto();
		dto.setId(resource.getId());
		dto.setAlies(resource.getAlies());
		dto.setNom(resource.getNom());
		dto.setPassword(resource.getPassword());
		dto.setNumSerie(resource.getNumSerie());
		dto.setDataAlta(resource.getDataAlta());
		dto.setDataBaixa(resource.getDataBaixa());
		dto.setInteroperabilitat(resource.isInteroperabilitat());
		dto.setOrganisme(resource.getOrganisme() != null ? Long.valueOf(resource.getOrganisme()) : null);
		return dto;
	}

}
