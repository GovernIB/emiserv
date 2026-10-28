package es.caib.emiserv.back.base;

import es.caib.emiserv.back.base.config.MethodSecurityConfig;
import es.caib.emiserv.back.base.controller.BaseController;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AopUtils;
import org.springframework.hateoas.Link;
import org.springframework.security.access.prepost.PreAuthorize;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprova que la seguretat a nivell de mètode de l'API REST només s'aplica als
 * controladors de l'API i no a la resta de beans (p. ex. els serveis d'EMISERV amb
 * {@code @PreAuthorize}, que avui no s'avaluen).
 *
 * @author Limit Tecnologies
 */
class MethodSecurityConfigTest {

	private final Pointcut pointcut = MethodSecurityConfig.resourceControllerPreAuthorizePointcut();

	@Test
	void s_aplicaAlsControladorsDeLApi() {
		assertTrue(AopUtils.canApply(pointcut, ControladorApiFals.class));
	}

	@Test
	void noS_aplicaALaRestaDeBeans() {
		assertFalse(AopUtils.canApply(pointcut, ServeiFals.class));
		assertFalse(AopUtils.canApply(pointcut, ServeiFalsImpl.class));
	}

	static class ControladorApiFals extends BaseController {
		@PreAuthorize("hasPermission(null, 'x', 'FIND')")
		public void find() {
		}
		@Override
		protected Link getIndexLink() {
			return null;
		}
	}

	interface ServeiFals {
		@PreAuthorize("hasRole('EMS_ADMIN')")
		void create();
	}

	static class ServeiFalsImpl implements ServeiFals {
		@Override
		public void create() {
		}
	}

}
