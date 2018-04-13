/**
 * 
 */
package es.caib.emiserv.backoffice.util;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import es.caib.emiserv.core.api.service.ws.backoffice.Consentimiento;
import es.caib.emiserv.core.api.service.ws.backoffice.TipoDocumentacion;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;

/**
 * Helper per a convertir entre diferents formats de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ConversioTipusHelper {

	private static MapperFactory mapperFactory;

	public static <T> T convertir(Object source, Class<T> targetType) {
		if (source == null)
			return null;
		return getMapperFacade().map(source, targetType);
	}
	public static <T> List<T> convertirList(List<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsList(items, targetType);
	}
	public static <T> Set<T> convertirSet(Set<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsSet(items, targetType);
	}



	private static MapperFacade getMapperFacade() {
		if (mapperFactory == null) {
			mapperFactory = new DefaultMapperFactory.Builder().build();
			ConverterFactory converterFactory = mapperFactory.getConverterFactory();
			converterFactory.registerConverter(new BidirectionalConverter<es.scsp.bean.common.Consentimiento, Consentimiento>() {
				@Override
				public Consentimiento convertTo(
						es.scsp.bean.common.Consentimiento source,
						Type<Consentimiento> destinationType) {
					if (source.equals(es.scsp.bean.common.Consentimiento.Si)) {
						return Consentimiento.Si;
					} else {
						return Consentimiento.Ley;
					}
				}
				@Override
				public es.scsp.bean.common.Consentimiento convertFrom(
						Consentimiento source,
						Type<es.scsp.bean.common.Consentimiento> destinationType) {
					if (source.equals(Consentimiento.Si)) {
						return es.scsp.bean.common.Consentimiento.Si;
					} else {
						return es.scsp.bean.common.Consentimiento.Ley;
					}
				}
			});
			converterFactory.registerConverter(new BidirectionalConverter<es.scsp.bean.common.TipoDocumentacion, TipoDocumentacion>() {
				@Override
				public TipoDocumentacion convertTo(
						es.scsp.bean.common.TipoDocumentacion source,
						Type<TipoDocumentacion> destinationType) {
					if (source.equals(es.scsp.bean.common.TipoDocumentacion.DNI)) {
						return TipoDocumentacion.DNI;
					} else if (source.equals(es.scsp.bean.common.TipoDocumentacion.NIF)) {
						return TipoDocumentacion.NIF;
					} else if (source.equals(es.scsp.bean.common.TipoDocumentacion.CIF)) {
						return TipoDocumentacion.CIF;
					} else if (source.equals(es.scsp.bean.common.TipoDocumentacion.NIE)) {
						return TipoDocumentacion.NIE;
					} else {
						return TipoDocumentacion.Pasaporte;
					}
				}
				@Override
				public es.scsp.bean.common.TipoDocumentacion convertFrom(
						TipoDocumentacion source,
						Type<es.scsp.bean.common.TipoDocumentacion> destinationType) {
					if (source.equals(TipoDocumentacion.DNI)) {
						return es.scsp.bean.common.TipoDocumentacion.DNI;
					} else if (source.equals(TipoDocumentacion.NIF)) {
						return es.scsp.bean.common.TipoDocumentacion.NIF;
					} else if (source.equals(TipoDocumentacion.CIF)) {
						return es.scsp.bean.common.TipoDocumentacion.CIF;
					} else if (source.equals(TipoDocumentacion.NIE)) {
						return es.scsp.bean.common.TipoDocumentacion.NIE;
					} else {
						return es.scsp.bean.common.TipoDocumentacion.Pasaporte;
					}
				}
			});
		}
		return mapperFactory.getMapperFacade();
	}

}
