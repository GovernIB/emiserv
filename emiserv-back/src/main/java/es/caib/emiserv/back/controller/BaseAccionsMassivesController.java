package es.caib.emiserv.back.controller;

import es.caib.emiserv.back.helper.MissatgeHelper;
import es.caib.emiserv.back.helper.RequestSessionHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Controlador base per als llistats (datatable) amb selecció de files i accions massives, com el
 * de Notib (TableAccionsMassivesController). La selecció es desa a la sessió (atribut
 * {@link #getSessionAttributeSeleccio()}) i la mantenen les peticions de la pàgina
 * (js/datatable.accions-massives.js):
 * <ul>
 *   <li>GET {base}/select?ids[]=...: afegeix files a la selecció.</li>
 *   <li>GET {base}/deselect?ids[]=...: les treu (sense ids, la buida).</li>
 *   <li>GET {base}/seleccionar/all: selecciona tots els elements del filtre actual.</li>
 * </ul>
 * Totes retornen el nombre d'elements seleccionats.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public abstract class BaseAccionsMassivesController extends BaseController {

	/** Nom de l'atribut de sessió on es desa la selecció (un {@code Set<Long>}). */
	protected abstract String getSessionAttributeSeleccio();

	/** Identificadors de tots els elements que compleixen el filtre actual del llistat. */
	protected abstract List<Long> getIdsElementsFiltrats(HttpServletRequest request);

	@RequestMapping(value = "/select", method = RequestMethod.GET)
	@ResponseBody
	public int select(
			HttpServletRequest request,
			@RequestParam(value = "ids[]", required = false) Long[] ids) {
		Set<Long> seleccio = getSeleccio(request);
		if (ids != null) {
			seleccio.addAll(Arrays.asList(ids));
		}
		return seleccio.size();
	}

	@RequestMapping(value = "/deselect", method = RequestMethod.GET)
	@ResponseBody
	public int deselect(
			HttpServletRequest request,
			@RequestParam(value = "ids[]", required = false) Long[] ids) {
		Set<Long> seleccio = getSeleccio(request);
		if (ids == null) {
			seleccio.clear();
		} else {
			seleccio.removeAll(Arrays.asList(ids));
		}
		return seleccio.size();
	}

	@RequestMapping(value = "/seleccionar/all", method = RequestMethod.GET)
	@ResponseBody
	public int selectAll(HttpServletRequest request) {
		Set<Long> seleccio = getSeleccio(request);
		seleccio.addAll(getIdsElementsFiltrats(request));
		return seleccio.size();
	}

	/** Còpia dels identificadors seleccionats (buida si no n'hi ha cap). */
	protected Set<Long> getIdsSeleccionats(HttpServletRequest request) {
		return new LinkedHashSet<>(getSeleccio(request));
	}

	/** Buida la selecció (p. ex. en canviar el filtre). */
	protected void netejarSeleccio(HttpServletRequest request) {
		RequestSessionHelper.esborrarObjecteSessio(request, getSessionAttributeSeleccio());
	}

	/** Treu de la selecció els identificadors indicats (p. ex. els esborrats). */
	protected void treureDeSeleccio(HttpServletRequest request, Set<Long> ids) {
		getSeleccio(request).removeAll(ids);
	}

	/**
	 * Executa una acció per a cada element seleccionat. Els elements amb què l'acció falla queden
	 * seleccionats; els altres se'n treuen. Mostra un missatge amb el resultat
	 * (clauOk/clauError reben com a paràmetre el nombre d'elements) i retorna la redirecció.
	 *
	 * @return la vista de redirecció (redirect).
	 */
	protected String executarAccioMassiva(
			HttpServletRequest request,
			AccioElement accio,
			String redireccio,
			String clauOk,
			String clauError) {
		Set<Long> seleccio = getIdsSeleccionats(request);
		if (seleccio.isEmpty()) {
			MissatgeHelper.warning(request, getMessage(request, "comu.accio.massiva.seleccio.buida"));
			return redireccio;
		}
		Set<Long> correctes = new HashSet<>();
		int errors = 0;
		for (Long id: seleccio) {
			try {
				accio.executar(id);
				correctes.add(id);
			} catch (Exception ex) {
				log.warn("Error executant l'acció massiva sobre l'element " + id, ex);
				errors++;
			}
		}
		treureDeSeleccio(request, correctes);
		if (!correctes.isEmpty()) {
			MissatgeHelper.success(request, getMessage(request, clauOk, new Object[] {correctes.size()}));
		}
		if (errors > 0) {
			MissatgeHelper.error(request, getMessage(request, clauError, new Object[] {errors}));
		}
		return redireccio;
	}

	/** Acció sobre un element de la selecció. */
	@FunctionalInterface
	protected interface AccioElement {
		void executar(Long id) throws Exception;
	}

	@SuppressWarnings("unchecked")
	private Set<Long> getSeleccio(HttpServletRequest request) {
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				getSessionAttributeSeleccio());
		if (seleccio == null) {
			seleccio = new HashSet<>();
			RequestSessionHelper.actualitzarObjecteSessio(request, getSessionAttributeSeleccio(), seleccio);
		}
		return seleccio;
	}

}
