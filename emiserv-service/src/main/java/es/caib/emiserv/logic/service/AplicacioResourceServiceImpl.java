package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.intf.dto.AplicacioDto;
import es.caib.emiserv.logic.intf.model.AplicacioResource;
import es.caib.emiserv.logic.intf.service.AplicacioResourceService;
import es.caib.emiserv.logic.intf.base.model.FieldOption;
import es.caib.emiserv.logic.intf.dto.AutoritatCertificacioDto;
import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.stream.Collectors;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.service.ScspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Servei del recurs de configuració {@link AplicacioResource} (interfície REACT). Delega a les mateixes
 * operacions que la JSP.
 *
 * @author Limit Tecnologies
 */
@Service
public class AplicacioResourceServiceImpl
		extends BaseConfiguracioResourceService<AplicacioResource, Integer, AplicacioDto>
		implements AplicacioResourceService {

	@Autowired
	private ScspService scspService;

	private static final Map<String, String> CAMPS_ORDENABLES = new LinkedHashMap<>();
	static {
		CAMPS_ORDENABLES.put("certificatNif", "nifCertificado");
		CAMPS_ORDENABLES.put("cn", "cn");
		CAMPS_ORDENABLES.put("numeroSerie", "numeroSerie");
		CAMPS_ORDENABLES.put("dataAlta", "fechaAlta");
		CAMPS_ORDENABLES.put("dataBaixa", "fechaBaja");
	}

	@Override
	protected Map<String, String> getCampsOrdenables() {
		return CAMPS_ORDENABLES;
	}

	@PostConstruct
	public void init() {
		// Opcions: les mateixes autoritats que el formulari de la JSP (AplicacioController)
		register(AplicacioResource.Fields.autoritatCertificacioId, (FieldOptionsProvider)(fieldName, params) ->
				scspService.autoridadCertificacionFindAll().stream().
						sorted(Comparator.comparing(a -> a.getNombre() != null ? a.getNombre() : "", String.CASE_INSENSITIVE_ORDER)).
						map(a -> new FieldOption(String.valueOf(a.getId()), a.getNombre() + " (" + a.getCodca() + ")")).
						collect(Collectors.toList()));
	}

	@Override
	protected String getOrdrePerDefecte() {
		return AplicacioResource.Fields.cn;
	}

	@Override
	protected PaginaDto<AplicacioDto> consultar(Map<String, String> filtre, String quickFilter, PaginacioParamsDto paginacio) {
		return scspService.aplicacioFindByFiltrePaginat(quickFilter, paginacio);
	}

	@Override
	protected AplicacioDto consultarPerId(Integer id) {
		return scspService.aplicacioFindById(id);
	}

	@Override
	protected AplicacioDto crear(AplicacioDto dto) {
		return scspService.aplicacioCreate(dto);
	}

	@Override
	protected void actualitzar(AplicacioDto dto) {
		scspService.aplicacioUpdate(dto);
	}

	@Override
	protected void esborrar(Integer id) {
		scspService.aplicacioDelete(id);
	}

	@Override
	protected AplicacioResource toResource(AplicacioDto dto) {
		AplicacioResource resource = new AplicacioResource();
		resource.setId(dto.getId());
		resource.setCertificatNif(dto.getCertificatNif());
		resource.setCn(dto.getCn());
		resource.setNumeroSerie(dto.getNumeroSerie());
		if (dto.getAutoridadCertificacio() != null) {
			resource.setAutoritatCertificacioId(String.valueOf(dto.getAutoridadCertificacio().getId()));
			resource.setAutoritatCertificacioNom(dto.getAutoridadCertificacio().getNombre());
		}
		resource.setDataAlta(dto.getDataAlta());
		resource.setDataBaixa(dto.getDataBaixa());
		return resource;
	}

	@Override
	protected AplicacioDto toDto(AplicacioResource resource) {
		AplicacioDto dto = new AplicacioDto();
		dto.setId(resource.getId());
		dto.setCertificatNif(resource.getCertificatNif());
		dto.setCn(resource.getCn());
		dto.setNumeroSerie(resource.getNumeroSerie());
		if (resource.getAutoritatCertificacioId() != null) {
			AutoritatCertificacioDto autoritat = new AutoritatCertificacioDto();
			autoritat.setId(Long.valueOf(resource.getAutoritatCertificacioId()));
			dto.setAutoridadCertificacio(autoritat);
		}
		dto.setDataAlta(resource.getDataAlta());
		dto.setDataBaixa(resource.getDataBaixa());
		return dto;
	}

}
