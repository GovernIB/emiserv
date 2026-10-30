/**
 * 
 */
package es.caib.emiserv.back.config;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolverSupport;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.opensymphony.sitemesh.webapp.SiteMeshFilter;

import es.caib.emiserv.back.base.config.BaseWebMvcConfig;
import es.caib.emiserv.back.base.controller.BaseController;
import es.caib.emiserv.back.interceptor.AjaxInterceptor;
import es.caib.emiserv.back.interceptor.AplicacioInterceptor;
import es.caib.emiserv.back.interceptor.CanviRolInterceptor;
import es.caib.emiserv.back.interceptor.ModalInterceptor;
import es.caib.emiserv.back.interceptor.NodecoInterceptor;

/**
 * Configuració de Spring web MVC.
 * 
 * @author Limit Tecnologies
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private AjaxInterceptor ajaxInterceptor;
	@Autowired
	private AplicacioInterceptor aplicacioInterceptor;
	@Autowired
	private CanviRolInterceptor canviRolInterceptor;
	@Autowired
	private ModalInterceptor modalInterceptor;
	@Autowired
	private NodecoInterceptor nodecoInterceptor;

	@Bean
	public LocaleResolver localeResolver() {
		CustomLocaleResolver localeResolver = new CustomLocaleResolver(
				Arrays.asList(
						Locale.forLanguageTag("ca"),
						Locale.forLanguageTag("es")));
		localeResolver.setDefaultLocale(Locale.forLanguageTag("ca"));
		return localeResolver;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

	@Bean
	public FilterRegistrationBean<SiteMeshFilter> sitemeshFilter() {
		FilterRegistrationBean<SiteMeshFilter> registrationBean = new FilterRegistrationBean<SiteMeshFilter>();
		registrationBean.setFilter(new SiteMeshFilter());
		registrationBean.addUrlPatterns("*");
		return registrationBean;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(aplicacioInterceptor);
		registry.addInterceptor(canviRolInterceptor);
		registry.addInterceptor(modalInterceptor);
		registry.addInterceptor(nodecoInterceptor);
		registry.addInterceptor(ajaxInterceptor);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		// Controladors de l'API REST (base-boot): sense paràmetres de paginació ni d'ordenació el
		// Pageable ha de ser null, perquè el find torni només els enllaços del recurs (find, getOne,
		// artifacts...), que és el que consulta la interfície REACT (base-react).
		resolvers.add(new ApiPageableHandlerMethodArgumentResolver());
		CustomPageableHandlerMethodArgumentResolver resolver = new CustomPageableHandlerMethodArgumentResolver();
		resolver.setFallbackPageable(Pageable.unpaged());
		resolvers.add(resolver);
		WebMvcConfigurer.super.addArgumentResolvers(resolvers);
	}

	public static class CustomPageableHandlerMethodArgumentResolver extends PageableHandlerMethodArgumentResolverSupport implements PageableArgumentResolver {
		private static final SortHandlerMethodArgumentResolver DEFAULT_SORT_RESOLVER = new SortHandlerMethodArgumentResolver();
		private SortArgumentResolver sortResolver;
		public CustomPageableHandlerMethodArgumentResolver() {
			this((SortArgumentResolver) null);
		}
		public CustomPageableHandlerMethodArgumentResolver(SortHandlerMethodArgumentResolver sortResolver) {
			this((SortArgumentResolver) sortResolver);
		}
		public CustomPageableHandlerMethodArgumentResolver(@Nullable SortArgumentResolver sortResolver) {
			this.sortResolver = sortResolver == null ? DEFAULT_SORT_RESOLVER : sortResolver;
		}
		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			return Pageable.class.equals(parameter.getParameterType()) && !isApiController(parameter);
		}
		@Override
		public Pageable resolveArgument(
				MethodParameter methodParameter,
				@Nullable ModelAndViewContainer mavContainer,
				NativeWebRequest webRequest,
				@Nullable WebDataBinderFactory binderFactory) {
			String page = webRequest.getParameter(getParameterNameToUse(getPageParameterName(), methodParameter));
			String pageSize = webRequest.getParameter(getParameterNameToUse(getSizeParameterName(), methodParameter));
			Sort sort = sortResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
			Pageable pageable = getPageable(methodParameter, page, pageSize);
			if (pageable.isPaged() && sort.isSorted()) {
				return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
			}
			return pageable;
		}
	}

	/**
	 * Resolució del Pageable dels controladors de l'API REST: la de base-boot
	 * (BaseWebMvcConfig, que l'aplicació no registra perquè ja té aquesta configuració).
	 */
	public static class ApiPageableHandlerMethodArgumentResolver extends BaseWebMvcConfig.CustomPageableHandlerMethodArgumentResolver {
		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			return super.supportsParameter(parameter) && isApiController(parameter);
		}
	}

	private static boolean isApiController(MethodParameter parameter) {
		return BaseController.class.isAssignableFrom(parameter.getContainingClass());
	}

	public static class CustomLocaleResolver extends SessionLocaleResolver {
		private AcceptHeaderLocaleResolver acceptHeaderLocaleResolver;
		public CustomLocaleResolver(List<Locale> supportedLocales) {
			acceptHeaderLocaleResolver = new AcceptHeaderLocaleResolver();
			acceptHeaderLocaleResolver.setSupportedLocales(supportedLocales);
		}
		@Override
		protected Locale determineDefaultLocale(HttpServletRequest request) {
			Locale acceptHeaderLocale = acceptHeaderLocaleResolver.resolveLocale(request);
			if (acceptHeaderLocale == null) {
				Locale defaultLocale = getDefaultLocale();
				if (defaultLocale == null) {
					defaultLocale = request.getLocale();
				}
				return defaultLocale;
			} else {
				return acceptHeaderLocale;
			}
		}
	}

}
