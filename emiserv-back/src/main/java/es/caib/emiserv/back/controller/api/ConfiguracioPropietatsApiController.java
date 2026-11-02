package es.caib.emiserv.back.controller.api;

import es.caib.emiserv.logic.intf.dto.ConfigDto;
import es.caib.emiserv.logic.intf.dto.ConfigGroupDto;
import es.caib.emiserv.logic.intf.dto.ConfigSourceEnumDto;
import es.caib.emiserv.logic.intf.service.ConfigService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Propietats de configuració de l'aplicació (taula ems_config) per a la interfície REACT, com la
 * pantalla config.jsp (ConfigController): grups de propietats amb les descripcions traduïdes i
 * modificació d'una propietat. Només EMS_ADMIN.
 * <p/>
 * No és un recurs HAL-FORMS: és un arbre de grups i la descripció de cada propietat és una clau dels
 * missatges de la JSP, que es tradueix aquí. Les propietats de contrasenya (tipus PASS o
 * CREDENTIALS) no envien mai el valor a la interfície; en desar-les buides no es modifiquen. Les
 * propietats definides al fitxer (origen FILE) no es poden modificar.
 *
 * @author Limit Tecnologies
 */
@Slf4j
@RestController
@RequestMapping("/api/configuracio/propietats")
public class ConfiguracioPropietatsApiController {

	private static final String ROL_ADMIN = "EMS_ADMIN";
	private static final Set<String> TIPUS_CONTRASENYA = Set.of("PASS", "CREDENTIALS");

	@Autowired
	private ConfigService configService;
	@Autowired
	private MessageSource messageSource;

	@GetMapping
	public ResponseEntity<List<Grup>> grups() {
		if (!esAdmin()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		return ResponseEntity.ok(configService.findAll().stream().map(this::toGrup).collect(Collectors.toList()));
	}

	@PostMapping
	public ResponseEntity<Map<String, String>> desar(@RequestBody Canvi canvi) {
		if (!esAdmin()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		ConfigDto propietat = cercar(configService.findAll(), canvi.getKey());
		if (propietat == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", traduir("config.controller.edit.error")));
		}
		if (ConfigSourceEnumDto.FILE.equals(propietat.getSourceProperty())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", traduir("configuracio.propietat.fitxer")));
		}
		if (TIPUS_CONTRASENYA.contains(propietat.getTypeCode()) && (canvi.getValue() == null || canvi.getValue().isEmpty())) {
			// Contrasenya buida: no es canvia (el valor actual no s'envia mai a la interfície)
			return ResponseEntity.ok(Map.of("message", traduir("config.controller.edit.ok")));
		}
		try {
			configService.updateProperty(ConfigDto.builder().key(canvi.getKey()).value(canvi.getValue()).build());
		} catch (Exception ex) {
			log.error("Error modificant la propietat {}", canvi.getKey(), ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", traduir("config.controller.edit.error")));
		}
		return ResponseEntity.ok(Map.of("message", traduir("config.controller.edit.ok")));
	}

	private Grup toGrup(ConfigGroupDto grup) {
		return new Grup(
				grup.getKey(),
				traduir(grup.getDescriptionKey()),
				grup.getConfigs() != null ?
						grup.getConfigs().stream().map(this::toPropietat).collect(Collectors.toList()) :
						Collections.emptyList(),
				grup.getInnerConfigs() != null ?
						grup.getInnerConfigs().stream().map(this::toGrup).collect(Collectors.toList()) :
						Collections.emptyList());
	}

	private Propietat toPropietat(ConfigDto config) {
		boolean contrasenya = TIPUS_CONTRASENYA.contains(config.getTypeCode());
		return new Propietat(
				config.getKey(),
				traduir(config.getDescriptionKey()),
				contrasenya ? null : config.getValue(),
				config.getTypeCode(),
				config.getSourceProperty() != null ? config.getSourceProperty().name() : null,
				config.isRequereixReinici(),
				config.getValidValues(),
				contrasenya,
				ConfigSourceEnumDto.FILE.equals(config.getSourceProperty()));
	}

	private static ConfigDto cercar(List<ConfigGroupDto> grups, String key) {
		if (grups == null || key == null) {
			return null;
		}
		for (ConfigGroupDto grup: grups) {
			if (grup.getConfigs() != null) {
				for (ConfigDto config: grup.getConfigs()) {
					if (key.equals(config.getKey())) {
						return config;
					}
				}
			}
			ConfigDto trobada = cercar(grup.getInnerConfigs(), key);
			if (trobada != null) {
				return trobada;
			}
		}
		return null;
	}

	private String traduir(String clau) {
		if (clau == null) {
			return null;
		}
		return messageSource.getMessage(clau, null, clau, LocaleContextHolder.getLocale());
	}

	private static boolean esAdmin() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth != null && auth.getAuthorities().stream().
				map(GrantedAuthority::getAuthority).
				anyMatch(ROL_ADMIN::equals);
	}

	@Getter
	@AllArgsConstructor
	public static class Grup {
		private final String key;
		private final String descripcio;
		private final List<Propietat> propietats;
		private final List<Grup> grups;
	}

	@Getter
	@AllArgsConstructor
	public static class Propietat {
		private final String key;
		private final String descripcio;
		/** Valor actual (null a les contrasenyes). */
		private final String value;
		/** Tipus (BOOL, INT, FLOAT, TEXT, PASS, CREDENTIALS...). */
		private final String typeCode;
		/** Origen: DATABASE, FILE... */
		private final String origen;
		private final boolean requereixReinici;
		private final List<String> validValues;
		private final boolean contrasenya;
		/** Definida al fitxer de propietats: no es pot modificar. */
		private final boolean nomesLectura;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class Canvi {
		private String key;
		private String value;
	}

}
