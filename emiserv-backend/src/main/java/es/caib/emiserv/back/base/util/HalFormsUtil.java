package es.caib.emiserv.back.base.util;

import es.caib.emiserv.logic.intf.base.annotation.ResourceField;
import es.caib.emiserv.logic.intf.base.exception.ComponentNotFoundException;
import es.caib.emiserv.logic.intf.base.service.MutableResourceService;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitats per a HAL-FORMS.
 * 
 * @author Límit Tecnologies
 */
public class HalFormsUtil {

	private static final String NEW_RESOURCE_VALUES_ATTRIBUTE_PREFIX = HalFormsUtil.class.getName() + ".newResourceValues.";

	/**
	 * Crea una nova instància d'un recurs.
	 * <p>
	 * El resourceClass pot ser un recurs gestionat per l'aplicació o una classe de formulari associada a algun
	 * artefacte. Si és un recurs gestionat es crea la nova instància utilitzant el mètode newResourceInstance del
	 * servei associat al recurs. Si no és un recurs es crea la instància utilitzant el constructor per defecte.
	 *
	 * @param resourceClass
	 *            Classe del recurs (ha d'estendre de Serializable).
	 * @param resourceServiceLocator
	 *            Instància de ResourceServiceLocator per a cercar si hi ha algun service associat a resourceClass.
	 * @return un map amb els camps de la nova instància i els seus valors.
	 */
	public static Map<String, Object> getNewResourceValues(
			Class<? extends Serializable> resourceClass,
			ResourceServiceLocator resourceServiceLocator) {
		if (resourceClass == null) {
			return Collections.emptyMap();
		}
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes == null) {
			// Fora d'una petició HTTP no es pot guardar a la memòria cau
			return createNewResourceValues(resourceClass, resourceServiceLocator);
		}
		String attributeName = NEW_RESOURCE_VALUES_ATTRIBUTE_PREFIX + resourceClass.getName();
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>)requestAttributes.getAttribute(
				attributeName,
				RequestAttributes.SCOPE_REQUEST);
		if (values == null) {
			values = Collections.unmodifiableMap(createNewResourceValues(resourceClass, resourceServiceLocator));
			requestAttributes.setAttribute(attributeName, values, RequestAttributes.SCOPE_REQUEST);
		}
		return values;
	}

	public static Map<String, Object> createNewResourceValues(
			Class<? extends Serializable> resourceClass,
			ResourceServiceLocator resourceServiceLocator) {
		Map<String, Object> values = new HashMap<>();
		if (resourceServiceLocator != null) {
			try {
				MutableResourceService<?, ?> mutableResourceService = resourceServiceLocator.
						getMutableEntityResourceServiceForResourceClass(resourceClass);
				Object newInstance = mutableResourceService.newResourceInstance();
				if (newInstance != null) {
					values.putAll(toMap(newInstance));
				}
			} catch (ComponentNotFoundException ex) {
				try {
					values.putAll(toMap(resourceClass.getDeclaredConstructor().newInstance()));
				} catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
				}
			}
		}
		return values;
	}

	public static <T extends Annotation> T getFieldAnnotation(
			Class<?> resourceClass,
			String fieldName,
			Class<T> annotationClass) {
		try {
			return resourceClass.getDeclaredField(fieldName).getAnnotation(annotationClass);
		} catch (NoSuchFieldException e) {
			return null;
		}
	}

	public static boolean isOnChangeActive(Class<?> resourceClass, String fieldName) {
		Field field = ReflectionUtils.findField(resourceClass, fieldName);
		if (field != null) {
			ResourceField resourceField = field.getAnnotation(ResourceField.class);
			return resourceField != null && resourceField.onChangeActive();
		} else {
			return false;
		}
	}

	private static Map<String, Object> toMap(Object object) {
		Map<String, Object> map = new HashMap<>();
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field: fields) {
			field.setAccessible(true);
			try {
				Object value = field.get(object);
				if (value != null) {
					map.put(field.getName(), value);
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return map;
	}

}
