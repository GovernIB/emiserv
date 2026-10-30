package es.caib.emiserv.back.controller;

import es.caib.emiserv.back.helper.ReactAppHelper;
import es.caib.emiserv.logic.intf.dto.InterficieUsuariEnumDto;
import es.caib.emiserv.logic.intf.service.AplicacioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Comprova a quina interficie porta l'arrel de l'aplicacio segons la preferencia de l'usuari i si
 * el SPA esta inclos a l'aplicacio.
 */
public class IndexControllerTest {

	private AplicacioService aplicacioService;

	@BeforeEach
	public void configurar() {
		aplicacioService = mock(AplicacioService.class);
	}

	@Test
	public void interficieReactAmbSpaVaALaReact() {
		when(aplicacioService.getInterficieEfectiva()).thenReturn(InterficieUsuariEnumDto.REACT);
		assertEquals("redirect:/reactapp", controlador("META-INF/resources").root(new MockHttpServletRequest()));
	}

	@Test
	public void interficieReactSenseSpaVaALaJsp() {
		when(aplicacioService.getInterficieEfectiva()).thenReturn(InterficieUsuariEnumDto.REACT);
		assertEquals("redirect:/servei", controlador("no-hi-ha-spa").root(new MockHttpServletRequest()));
	}

	@Test
	public void interficieJspVaALaJsp() {
		when(aplicacioService.getInterficieEfectiva()).thenReturn(InterficieUsuariEnumDto.JSP);
		assertEquals("redirect:/servei", controlador("META-INF/resources").root(new MockHttpServletRequest()));
	}

	private IndexController controlador(String classpathPrefix) {
		IndexController controller = new IndexController();
		ReflectionTestUtils.setField(controller, "aplicacioService", aplicacioService);
		ReflectionTestUtils.setField(controller, "reactAppHelper", new ReactAppHelper(new MockServletContext("file:/no-existeix"), classpathPrefix));
		return controller;
	}

}
