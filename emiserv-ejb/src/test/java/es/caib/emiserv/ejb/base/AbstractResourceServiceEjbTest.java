package es.caib.emiserv.ejb.base;

import es.caib.emiserv.logic.intf.base.model.Resource;
import es.caib.emiserv.logic.intf.base.model.ResourceArtifactType;
import es.caib.emiserv.logic.intf.base.service.MutableResourceService;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Comprova que els EJBs de recurs deleguen en el servei, en particular els
 * mètodes genèrics, i que el servei es detecta pel primer paràmetre genèric.
 */
class AbstractResourceServiceEjbTest {

	static class TestResource implements Resource<Long> {
		private Long id;
		public Long getId() { return id; }
		public void setId(Long id) { this.id = id; }
	}

	interface TestResourceService extends MutableResourceService<TestResource, Long> {
	}

	/** Patró que hauran de seguir els EJBs de cada recurs. */
	static class TestResourceServiceEjb
			extends AbstractResourceServiceEjb<TestResourceService, TestResource, Long>
			implements TestResourceService {
		private final TestResourceService delegate;
		TestResourceServiceEjb(TestResourceService delegate) {
			this.delegate = delegate;
		}
		Class<TestResourceService> serviceClass() {
			return getServiceClass();
		}
		@Override
		protected TestResourceService getDelegateService() {
			return delegate;
		}
	}

	private final TestResourceService delegate = mock(TestResourceService.class);
	private final TestResourceServiceEjb ejb = new TestResourceServiceEjb(delegate);

	@Test
	void delegaElsMetodesGenerics() throws Exception {
		Serializable params = "p";
		when(delegate.artifactActionExec(1L, "act", params)).thenReturn("res");
		assertEquals("res", ejb.artifactActionExec(1L, "act", params));

		Map<String, Object> changes = Collections.singletonMap("a", "b");
		when(delegate.artifactOnChange(ResourceArtifactType.ACTION, "act", 1L, params, "f", "v", null)).thenReturn(changes);
		assertSame(changes, ejb.artifactOnChange(ResourceArtifactType.ACTION, "act", 1L, params, "f", "v", null));

		List<?> data = Collections.singletonList("x");
		when(delegate.artifactReportGenerateData(1L, "rep", params)).thenReturn((List) data);
		assertSame(data, ejb.artifactReportGenerateData(1L, "rep", params));
	}

	@Test
	void delegaElsMetodesDelRecurs() throws Exception {
		TestResource resource = new TestResource();
		when(delegate.getOne(5L, null)).thenReturn(resource);
		assertSame(resource, ejb.getOne(5L, null));
		when(delegate.newResourceInstance()).thenReturn(resource);
		assertSame(resource, ejb.newResourceInstance());
		ejb.delete(5L, null);
		verify(delegate).delete(5L, null);
	}

	@Test
	void detectaElServeiPelPrimerParametreGeneric() {
		assertEquals(TestResourceService.class, ejb.serviceClass());
	}

}
