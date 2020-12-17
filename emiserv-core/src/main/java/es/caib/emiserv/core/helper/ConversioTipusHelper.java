/**
 * 
 */
package es.caib.emiserv.core.helper;

import java.util.Date;
import java.util.List;
import java.util.Set;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.Type;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import es.caib.emiserv.core.api.dto.ClauPrivadaDto;
import es.caib.emiserv.core.entity.scsp.ClauPrivadaEntity;

/**
 * Helper per a convertir entre diferents formats de'objectes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ConversioTipusHelper {

	private MapperFactory mapperFactory;

	public ConversioTipusHelper() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DateTime, Date>() {
					public Date convert(DateTime source, Type<? extends Date> destinationClass) {
						return source.toDate();
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DateTime, Date>() {
					public Date convert(DateTime source, Type<? extends Date> destinationClass) {
						return source.toDate();
					}
				});
		mapperFactory.registerClassMap(
				ClassMapBuilder.map(ClauPrivadaEntity.class, ClauPrivadaDto.class)
				.field("organisme.id", "organisme")
				.byDefault().toClassMap());
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



	private MapperFacade getMapperFacade() {
		return mapperFactory.getMapperFacade();
	}

	public <S, D> Page<D> pageEntities2pageDto(Page<S> pageEntities, Class<D> destinationClass, Pageable pageable) {
		return new PageImpl<D>(
				this.getMapperFacade().mapAsList(pageEntities.getContent(), destinationClass),
				pageable,
				pageEntities.getTotalElements());
	}
}
