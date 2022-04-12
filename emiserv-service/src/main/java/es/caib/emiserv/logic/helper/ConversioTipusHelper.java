/**
 * 
 */
package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.dto.ClauPrivadaDto;
import es.caib.emiserv.logic.intf.dto.EmisorDto;
import es.caib.emiserv.logic.intf.dto.OrganismeDto;
import es.caib.emiserv.persist.entity.scsp.ScspCoreClavePrivadaEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAutorizacionOrganismoEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmisorCertificadoEntity;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Helper per a convertir entre diferents formats de'objectes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component("logicConversioTipusHelper")
public class ConversioTipusHelper {

	private MapperFactory mapperFactory;

	public ConversioTipusHelper() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DateTime, Date>() {
					@Override
					public Date convert(
							DateTime source,
							Type<? extends Date> destinationType,
							MappingContext mappingContext) {
						return source.toDate();
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DateTime, Date>() {
					@Override
					public Date convert(
							DateTime source,
							Type<? extends Date> destinationType,
							MappingContext mappingContext) {
						return source.toDate();
					}
				});
		mapperFactory.registerClassMap(
				mapperFactory.classMap(
						ScspCoreClavePrivadaEntity.class,
						ClauPrivadaDto.class).
				field("organisme.id", "organisme").
				byDefault().
				toClassMap());

		mapperFactory.classMap(ScspCoreEmisorCertificadoEntity.class, EmisorDto.class)
				.field("nombre", "nom")
				.field("fechaBaja", "dataBaixa")
				.field("fechaAlta", "dataAlta")
				.byDefault()
				.register();

		mapperFactory.classMap(ScspCoreEmAutorizacionOrganismoEntity.class, OrganismeDto.class)
				.field("idorganismo", "cif")
				.field("fechaAlta", "dataAlta")
				.field("fechaBaja", "dataBaixa")
				.field("nombreOrganismo", "nom")
				.byDefault()
				.register();

		/*mapperFactory.getConverterFactory().registerConverter(new BidirectionalConverter<es.scsp.bean.common.Consentimiento, Consentimiento>() {
			@Override
			public Consentimiento convertTo(
					es.scsp.bean.common.Consentimiento source,
					Type<Consentimiento> destinationType,
					MappingContext mappingContext) {
				if (source.equals(es.scsp.bean.common.Consentimiento.Si)) {
					return Consentimiento.Si;
				} else {
					return Consentimiento.Ley;
				}
			}
			@Override
			public es.scsp.bean.common.Consentimiento convertFrom(
					Consentimiento source,
					Type<es.scsp.bean.common.Consentimiento> destinationType,
					MappingContext mappingContext) {
				if (source.equals(Consentimiento.Si)) {
					return es.scsp.bean.common.Consentimiento.Si;
				} else {
					return es.scsp.bean.common.Consentimiento.Ley;
				}
			}
		});
		mapperFactory.getConverterFactory().registerConverter(new BidirectionalConverter<es.scsp.bean.common.TipoDocumentacion, TipoDocumentacion>() {
			@Override
			public TipoDocumentacion convertTo(
					es.scsp.bean.common.TipoDocumentacion source,
					Type<TipoDocumentacion> destinationType,
					MappingContext mappingContext) {
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
					Type<es.scsp.bean.common.TipoDocumentacion> destinationType,
					MappingContext mappingContext) {
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
		});*/
	}

	public <T> T convertir(Object source, Class<T> targetType) {
		if (source == null)
			return null;
		return getMapperFacade().map(source, targetType);
	}
	public <T> List<T> convertirList(List<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsList(items, targetType);
	}
	public <T> Set<T> convertirSet(Set<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsSet(items, targetType);
	}

	public <S, D> Page<D> pageEntities2pageDto(Page<S> pageEntities, Class<D> destinationClass, Pageable pageable) {
		return new PageImpl<D>(
				this.getMapperFacade().mapAsList(pageEntities.getContent(), destinationClass),
				pageable,
				pageEntities.getTotalElements());
	}

	private MapperFacade getMapperFacade() {
		return mapperFactory.getMapperFacade();
	}

}
