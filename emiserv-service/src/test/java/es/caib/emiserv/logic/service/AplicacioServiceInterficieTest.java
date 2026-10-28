package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.intf.dto.InterficieUsuariEnumDto;
import es.caib.emiserv.persist.entity.UsuariEntity;
import es.caib.emiserv.persist.repository.UsuariRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Proves de la preferència d'interfície (JSP o REACT) de l'usuari. S'empren mocks del repositori
 * i de l'entorn perquè emiserv-service no disposa d'infraestructura de proves amb context Spring i BD.
 */
class AplicacioServiceInterficieTest {

	private static final String PROPIETAT = "es.caib.emiserv.interface.defecte";

	private final Map<String, UsuariEntity> usuaris = new HashMap<>();
	private final Environment environment = mock(Environment.class);
	private AplicacioServiceImpl service;

	@BeforeEach
	void setUp() {
		UsuariRepository repository = mock(UsuariRepository.class);
		when(repository.findById(anyString())).thenAnswer(i -> Optional.ofNullable(usuaris.get((String) i.getArgument(0))));
		when(repository.save(any(UsuariEntity.class))).thenAnswer(i -> {
			UsuariEntity u = i.getArgument(0);
			usuaris.put(u.getCodi(), u);
			return u;
		});
		service = new AplicacioServiceImpl();
		ReflectionTestUtils.setField(service, "usuariRepository", repository);
		ReflectionTestUtils.setField(service, "environment", environment);
		SecurityContextHolder.getContext().setAuthentication(new UsuariAuth("usuari1"));
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	private void propietat(String valor) {
		when(environment.getProperty(PROPIETAT)).thenReturn(valor);
	}

	@Test
	void senseFilaUsuariManaLaPropietat() {
		propietat("REACT");
		assertEquals(InterficieUsuariEnumDto.REACT, service.getInterficieEfectiva());
	}

	@Test
	void interficieNullaManaLaPropietat() {
		propietat(null);
		usuaris.put("usuari1", UsuariEntity.getBuilder("usuari1").build());
		assertEquals(InterficieUsuariEnumDto.JSP, service.getInterficieEfectiva());
		propietat("INVALID");
		assertEquals(InterficieUsuariEnumDto.JSP, service.getInterficieEfectiva());
		propietat("react");
		assertEquals(InterficieUsuariEnumDto.REACT, service.getInterficieEfectiva());
	}

	@Test
	void preferenciaUsuariGuanya() {
		propietat("REACT");
		usuaris.put("usuari1", UsuariEntity.getBuilder("usuari1").interficie(InterficieUsuariEnumDto.JSP).build());
		assertEquals(InterficieUsuariEnumDto.JSP, service.getInterficieEfectiva());
	}

	@Test
	void updateInterficieCreaFila() {
		propietat(null);
		service.updateInterficie(InterficieUsuariEnumDto.REACT);
		assertEquals(InterficieUsuariEnumDto.REACT, service.getInterficieEfectiva());
		service.updateInterficie(null);
		assertEquals(InterficieUsuariEnumDto.JSP, service.getInterficieEfectiva());
	}

	private static class UsuariAuth extends UsernamePasswordAuthenticationToken {
		UsuariAuth(String nom) {
			super(nom, "n/a");
		}
	}

}
