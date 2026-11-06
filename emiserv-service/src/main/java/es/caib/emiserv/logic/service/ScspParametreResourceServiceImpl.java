package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.helper.ConfiguracioResourceHelper;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.dto.ScspParametreDto;
import es.caib.emiserv.logic.intf.dto.ScspParametreFiltreDto;
import es.caib.emiserv.logic.intf.model.ScspParametreResource;
import es.caib.emiserv.logic.intf.service.ScspParametreResourceService;
import es.caib.emiserv.logic.intf.service.ScspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Servei del recurs de configuració {@link ScspParametreResource} (interfície REACT). Delega a les
 * mateixes operacions que la JSP (ScspParametresController). L'identificador és el nom, que no es
 * pot canviar; a l'alta no pot estar repetit (com @NomParametreNoRepetit).
 *
 * @author Limit Tecnologies
 */
@Service
public class ScspParametreResourceServiceImpl
		extends BaseConfiguracioResourceService<ScspParametreResource, String, ScspParametreDto>
		implements ScspParametreResourceService {

	private static final Map<String, String> CAMPS_ORDENABLES = new LinkedHashMap<>();
	static {
		for (String camp: new String[] { "nombre", "valor", "descripcion" }) {
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
		return ScspParametreResource.Fields.nombre;
	}

	@Override
	protected Collection<String> getCampsFiltrables() {
		return Arrays.asList(ScspParametreResource.Fields.nombre, ScspParametreResource.Fields.descripcion, ScspParametreResource.Fields.valor);
	}

	@Override
	protected PaginaDto<ScspParametreDto> consultar(Map<String, String> filtre, String quickFilter, PaginacioParamsDto paginacio) {
		ScspParametreFiltreDto filtreDto = new ScspParametreFiltreDto();
		filtreDto.setNombre(quickFilter != null ? quickFilter : ConfiguracioResourceHelper.valor(filtre, ScspParametreResource.Fields.nombre));
		filtreDto.setDescripcion(ConfiguracioResourceHelper.valor(filtre, ScspParametreResource.Fields.descripcion));
		filtreDto.setValor(ConfiguracioResourceHelper.valor(filtre, ScspParametreResource.Fields.valor));
		return scspService.getScspParametres(paginacio, filtreDto);
	}

	@Override
	protected ScspParametreDto consultarPerId(String id) {
		return scspService.getOptionalScspParametre(id).orElse(null);
	}

	@Override
	protected void validar(ScspParametreResource resource, String id) {
		if (id == null && scspService.getOptionalScspParametre(resource.getNombre()).isPresent()) {
			comprovarNoRepetit(resource.getNombre(), null, "configuracio.nom.repetit", resource.getNombre());
		}
	}

	@Override
	protected ScspParametreDto crear(ScspParametreDto dto) {
		scspService.createScspParametre(dto);
		return null;
	}

	@Override
	protected String getIdCreat(ScspParametreResource resource) {
		return resource.getNombre();
	}

	@Override
	protected void actualitzar(ScspParametreDto dto) {
		scspService.updateScspParametre(dto);
	}

	@Override
	protected void esborrar(String id) {
		scspService.deleteParametre(id);
	}

	@Override
	protected ScspParametreResource toResource(ScspParametreDto dto) {
		ScspParametreResource resource = new ScspParametreResource();
		resource.setId(dto.getNombre());
		resource.setNombre(dto.getNombre());
		resource.setValor(dto.getValor());
		resource.setDescripcion(dto.getDescripcion());
		return resource;
	}

	@Override
	protected ScspParametreDto toDto(ScspParametreResource resource) {
		// En modificar, el nom és l'identificador (no es pot canviar)
		return ScspParametreDto.builder().
				nombre(resource.getId() != null ? resource.getId() : resource.getNombre()).
				valor(resource.getValor()).
				descripcion(resource.getDescripcion()).
				build();
	}

}
