package es.caib.emiserv.back.base.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.emiserv.back.base.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Protecció CSRF de les accions de l'API REST.
 * <p/>
 * L'API s'autentica amb la galeta de sessió (la mateixa que la interfície JSP) i té el CSRF de
 * Spring Security desactivat. No es pot exigir una capçalera pròpia, perquè la {@code lib/} de
 * base-react no es pot modificar i pot fer peticions POST sense cos. Per això, a les peticions que
 * no són GET/HEAD/OPTIONS es comprova d'on venen: si porten la capçalera {@code Origin} (o, si no en
 * porten, {@code Referer}), ha de ser del mateix servidor o d'un dels orígens permesos (propietat
 * {@code es.caib.emiserv.front.dev.origins}); altrament es respon 403. Sense cap de les dues
 * capçaleres es deixa passar: els navegadors les envien sempre a les peticions d'un altre lloc i
 * els clients que no són navegadors no tenen la galeta de sessió de l'usuari.
 * <p/>
 * El mateix servidor es compara pel nom del servidor, sense l'esquema ni el port: darrere d'un
 * proxy que termina el TLS, l'aplicació veu {@code http} i el port intern. Es compara amb el nom
 * del servidor de la petició, amb la capçalera {@code Host} i amb {@code X-Forwarded-Host} (el
 * proxy no sempre conserva el {@code Host}; un navegador no pot posar aquesta capçalera en una
 * petició a un altre lloc sense passar pel CORS).
 *
 * @author Limit Tecnologies
 */
@Slf4j
public class ApiOrigenFilter extends OncePerRequestFilter {

	/** Propietat amb els orígens permesos, a més del propi, separats per comes. */
	public static final String PROPIETAT_ORIGENS = "es.caib.emiserv.front.dev.origins";

	private static final Set<String> METODES_SEGURS = Set.of("GET", "HEAD", "OPTIONS", "TRACE");

	private final RequestMatcher apiMatcher;
	private final List<String> origensPermesos;
	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * @param apiMatcher
	 *            les peticions a comprovar.
	 * @param origensPermesos
	 *            els orígens permesos a més del propi, separats per comes (pot ser null).
	 */
	public ApiOrigenFilter(RequestMatcher apiMatcher, String origensPermesos) {
		this.apiMatcher = apiMatcher;
		this.origensPermesos = parseOrigens(origensPermesos);
	}

	/**
	 * Converteix la llista d'orígens separats per comes en una llista d'orígens normalitzats
	 * ({@code esquema://host[:port]}, en minúscules i sense barra final).
	 *
	 * @param origens
	 *            els orígens separats per comes (pot ser null o buit).
	 * @return la llista d'orígens.
	 */
	public static List<String> parseOrigens(String origens) {
		if (origens == null || origens.trim().isEmpty()) {
			return Collections.emptyList();
		}
		return Arrays.stream(origens.split(",")).
				map(String::trim).
				filter(o -> !o.isEmpty()).
				map(ApiOrigenFilter::normalitzarOrigen).
				collect(Collectors.toList());
	}

	/**
	 * Indica si l'origen ({@code esquema://host[:port]}) és un dels orígens indicats.
	 *
	 * @param origen
	 *            l'origen.
	 * @param origensPermesos
	 *            els orígens normalitzats ({@link #parseOrigens(String)}).
	 * @return true si hi és.
	 */
	public static boolean isOrigenPermes(String origen, List<String> origensPermesos) {
		return origen != null && origensPermesos.contains(normalitzarOrigen(origen));
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return METODES_SEGURS.contains(request.getMethod().toUpperCase(Locale.ROOT)) || !apiMatcher.matches(request);
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		String origen = request.getHeader(HttpHeaders.ORIGIN);
		String capcalera = HttpHeaders.ORIGIN;
		if (origen == null || origen.isEmpty()) {
			origen = request.getHeader(HttpHeaders.REFERER);
			capcalera = HttpHeaders.REFERER;
		}
		if (origen == null || origen.isEmpty() || isPermes(origen, request)) {
			filterChain.doFilter(request, response);
			return;
		}
		log.warn("Petició a l'API rebutjada perquè ve d'un altre lloc ({} {}, {}: {})",
				request.getMethod(),
				request.getRequestURI(),
				capcalera,
				origen);
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		objectMapper.writeValue(
				response.getOutputStream(),
				new ErrorResponse(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase()));
	}

	private boolean isPermes(String origenOReferer, HttpServletRequest request) {
		URI uri;
		try {
			uri = new URI(origenOReferer.trim());
		} catch (URISyntaxException ex) {
			return false;
		}
		if (uri.getScheme() == null || uri.getHost() == null) {
			// Inclou "Origin: null" (documents sense origen, p. ex. iframes amb sandbox).
			return false;
		}
		if (isOrigenPermes(uri.getScheme() + "://" + uri.getRawAuthority(), origensPermesos)) {
			return true;
		}
		String host = uri.getHost();
		return host.equalsIgnoreCase(request.getServerName()) ||
				host.equalsIgnoreCase(nomHost(request.getHeader(HttpHeaders.HOST))) ||
				host.equalsIgnoreCase(nomHost(primerValor(request.getHeader("X-Forwarded-Host"))));
	}

	private static String normalitzarOrigen(String origen) {
		String normalitzat = origen.trim().toLowerCase(Locale.ROOT);
		while (normalitzat.endsWith("/")) {
			normalitzat = normalitzat.substring(0, normalitzat.length() - 1);
		}
		return normalitzat;
	}

	private static String primerValor(String capcalera) {
		return capcalera != null ? capcalera.split(",")[0].trim() : null;
	}

	/** Nom del host d'una capçalera host[:port] (també amb IPv6 entre claudàtors). */
	private static String nomHost(String hostIPort) {
		if (hostIPort == null || hostIPort.isEmpty()) {
			return null;
		}
		try {
			return new URI("http://" + hostIPort).getHost();
		} catch (URISyntaxException ex) {
			return null;
		}
	}

}
