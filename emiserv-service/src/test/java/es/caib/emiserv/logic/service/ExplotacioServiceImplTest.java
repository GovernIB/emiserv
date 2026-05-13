package es.caib.emiserv.logic.service;

import es.caib.comanda.model.server.monitoring.DimensioDesc;
import es.caib.emiserv.logic.intf.dto.EstadisticaDto;
import es.caib.emiserv.persist.repository.EntitatRepository;
import es.caib.emiserv.persist.repository.RedireccioPeticioRepository;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreEmisorCertificadoRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreTransmisionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExplotacioServiceImplTest {

	@Test
	void getDimensionsInclouValorsNullsIOrdenaSenseError() {
		ExplotacioServiceImpl service = new ExplotacioServiceImpl();
		EntitatRepository entitatRepository = mock(EntitatRepository.class);
		ServeiRepository serveiRepository = mock(ServeiRepository.class);
		ScspCoreTransmisionRepository scspCoreTransmisionRepository = mock(ScspCoreTransmisionRepository.class);
		ScspCoreEmisorCertificadoRepository scspCoreEmisorCertificadoRepository = mock(ScspCoreEmisorCertificadoRepository.class);
		RedireccioPeticioRepository redireccioPeticioRepository = mock(RedireccioPeticioRepository.class);

		ReflectionTestUtils.setField(service, "entitatRepository", entitatRepository);
		ReflectionTestUtils.setField(service, "serveiRepository", serveiRepository);
		ReflectionTestUtils.setField(service, "scspCoreTransmisionRepository", scspCoreTransmisionRepository);
		ReflectionTestUtils.setField(service, "scspCoreEmisorCertificadoRepository", scspCoreEmisorCertificadoRepository);
		ReflectionTestUtils.setField(service, "redireccioPeticioRepository", redireccioPeticioRepository);

		when(entitatRepository.findAllNoms()).thenReturn(Arrays.asList("Entitat B", null, "Entitat A"));
		when(serveiRepository.findAllCodis()).thenReturn(Arrays.asList("SRV2", null, "SRV1"));
		when(scspCoreTransmisionRepository.findAllProcediments()).thenReturn(Arrays.asList("PRC2", null));
		when(redireccioPeticioRepository.findAllProcediments()).thenReturn(Arrays.asList("PRC1", null, "PRC2"));
		when(scspCoreTransmisionRepository.findAllDepartaments()).thenReturn(Arrays.asList(null, "DEP2"));
		when(redireccioPeticioRepository.findAllDepartaments()).thenReturn(Arrays.asList("DEP1", null));
		when(scspCoreEmisorCertificadoRepository.findAllEmisors()).thenReturn(Arrays.asList(null, "EMI2"));
		when(redireccioPeticioRepository.findAllEmisors()).thenReturn(Arrays.asList("EMI1", null));

		List<DimensioDesc> dimensions = service.getDimensions();

		assertEquals(Arrays.asList("Entitat A", "Entitat B", null), valorsDe(dimensions, "ENT"));
		assertEquals(Arrays.asList("PRC1", "PRC2", null), valorsDe(dimensions, "PRC"));
		assertEquals(Arrays.asList("SRV1", "SRV2", null), valorsDe(dimensions, "SRV"));
		assertEquals(Arrays.asList("DEP1", "DEP2", null), valorsDe(dimensions, "DEP"));
		assertEquals(Arrays.asList("EMI1", "EMI2", null), valorsDe(dimensions, "EMI"));
	}

	@Test
	void dimFuncInclouTipusNullSenseError() {
		ExplotacioServiceImpl service = new ExplotacioServiceImpl();
		EstadisticaDto estadistica = new EstadisticaDto();

		Map<String, String> dimensions = service.dimFunc.apply(estadistica);

		assertNull(dimensions.get("TIP"));
	}

	private List<String> valorsDe(List<DimensioDesc> dimensions, String codi) {
		return dimensions.stream()
				.filter(dimensio -> codi.equals(dimensio.getCodi()))
				.findFirst()
				.orElseThrow()
				.getValors();
	}
}
