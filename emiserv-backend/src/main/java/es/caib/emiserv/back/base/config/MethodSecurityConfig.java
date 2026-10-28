package es.caib.emiserv.back.base.config;

import es.caib.emiserv.back.base.controller.BaseController;
import org.springframework.aop.Advisor;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Role;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.authorization.method.PreAuthorizeAuthorizationManager;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

/**
 * Configuració de la seguretat a nivell de mètode.
 * <p/>
 * Adaptació d'EMISERV: base-boot activa {@code @EnableGlobalMethodSecurity} per a tot el
 * context. A EMISERV això faria que s'avaluassin les anotacions {@code @PreAuthorize} dels
 * serveis existents (p. ex. {@code ServeiService}), que avui no s'avaluen a cap context
 * (la seguretat dels serveis la fan els EJB amb {@code @RolesAllowed}), i a més en el
 * context de l'aplicació Spring Boot hi ha dos {@link PermissionEvaluator} (el de les ACL
 * d'EMISERV i el {@code PermissionEvaluatorService} de base-boot). Per això aquí només es
 * registra un interceptor de {@link PreAuthorize} limitat als controladors de l'API REST
 * ({@link BaseController}), amb el seu propi gestor d'expressions que empra explícitament
 * el bean {@value #PERMISSION_EVALUATOR_BEAN_NAME}. No es registra cap
 * {@code MethodSecurityExpressionHandler} com a bean.
 *
 * @author Limit Tecnologies
 */
@Configuration
@Import(MethodSecurityConfig.AutoProxyRegistrar.class)
public class MethodSecurityConfig {

	public static final String DEFAULT_ROLE_PREFIX = "";
	public static final String PERMISSION_EVALUATOR_BEAN_NAME = "permissionEvaluatorService";

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public static Advisor resourceControllerPreAuthorizeAdvisor(ApplicationContext applicationContext) {
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setApplicationContext(applicationContext);
		expressionHandler.setPermissionEvaluator(new LazyPermissionEvaluator(applicationContext));
		expressionHandler.setDefaultRolePrefix(DEFAULT_ROLE_PREFIX);
		PreAuthorizeAuthorizationManager authorizationManager = new PreAuthorizeAuthorizationManager();
		authorizationManager.setExpressionHandler(expressionHandler);
		return new AuthorizationManagerBeforeMethodInterceptor(
				resourceControllerPreAuthorizePointcut(),
				authorizationManager);
	}

	/**
	 * Retorna el pointcut dels mètodes amb {@link PreAuthorize} (al mètode o a la classe)
	 * dels controladors de l'API REST.
	 *
	 * @return el pointcut.
	 */
	public static Pointcut resourceControllerPreAuthorizePointcut() {
		ClassFilter baseControllerClassFilter = BaseController.class::isAssignableFrom;
		ComposablePointcut preAuthorizePointcut = new ComposablePointcut(
				new AnnotationMatchingPointcut(null, PreAuthorize.class, true)).
				union(new AnnotationMatchingPointcut(PreAuthorize.class, true));
		return new ComposablePointcut(baseControllerClassFilter).intersection(preAuthorizePointcut);
	}

	/**
	 * {@link PermissionEvaluator} que delega en el bean
	 * {@value #PERMISSION_EVALUATOR_BEAN_NAME}, que es cerca per nom en el moment de
	 * l'avaluació. Així l'advisor (bean d'infraestructura) no força la creació anticipada
	 * del servei i no depèn de quants {@link PermissionEvaluator} hi hagi al context.
	 */
	static class LazyPermissionEvaluator implements PermissionEvaluator {
		private final BeanFactory beanFactory;
		private PermissionEvaluator delegate;
		LazyPermissionEvaluator(BeanFactory beanFactory) {
			this.beanFactory = beanFactory;
		}
		@Override
		public boolean hasPermission(
				Authentication authentication,
				Object targetDomainObject,
				Object permission) {
			return getDelegate().hasPermission(authentication, targetDomainObject, permission);
		}
		@Override
		public boolean hasPermission(
				Authentication authentication,
				Serializable targetId,
				String targetType,
				Object permission) {
			return getDelegate().hasPermission(authentication, targetId, targetType, permission);
		}
		private PermissionEvaluator getDelegate() {
			if (delegate == null) {
				delegate = beanFactory.getBean(PERMISSION_EVALUATOR_BEAN_NAME, PermissionEvaluator.class);
			}
			return delegate;
		}
	}

	/**
	 * Assegura que hi ha un creador de proxies automàtic que aplica els advisors
	 * d'infraestructura i que empra proxies de classe (els controladors implementen
	 * interfícies però Spring MVC necessita la classe), com fa
	 * {@code @EnableGlobalMethodSecurity}.
	 */
	static class AutoProxyRegistrar implements ImportBeanDefinitionRegistrar {
		@Override
		public void registerBeanDefinitions(
				AnnotationMetadata importingClassMetadata,
				BeanDefinitionRegistry registry) {
			AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
			AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
		}
	}

}
