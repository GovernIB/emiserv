/**
 * 
 */
package es.caib.emiserv.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import es.caib.emiserv.core.api.dto.PermisDto;
import es.caib.emiserv.core.api.dto.ServeiDto;
import es.caib.emiserv.core.api.service.ServeiService;

/**
 * Tests per al servei d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseServiceTest {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private ServeiService serveiService;



	protected void autenticarUsuari(String usuariCodi) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(usuariCodi);
		Authentication authToken = new UsernamePasswordAuthenticationToken(
				userDetails.getUsername(),
				userDetails.getPassword(),
				userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
	}

	protected void testCreantElements(
			TestAmbElementsCreats test,
			Object... elements) {
		List<Object> elementsCreats = new ArrayList<Object>();
		try {
			for (Object element: elements) {
				if (element instanceof ServeiDto) {
					autenticarUsuari("admin");
					ServeiDto serveiCreat = serveiService.create((ServeiDto)element);
					elementsCreats.add(serveiCreat);
					if (((ServeiDto)element).getPermisos() != null) {
						for (PermisDto permis: ((ServeiDto)element).getPermisos()) {
							serveiService.permisUpdate(
									serveiCreat.getId(),
									permis);
						}
					}
				}
			}
			test.executar(elementsCreats);
		} catch (Exception ex) {
			System.out.println("El test ha produït una excepció:");
			ex.printStackTrace(System.out);
		} finally {
			Collections.reverse(elementsCreats);
			for (Object element: elementsCreats) {
				autenticarUsuari("admin");
				if (element instanceof ServeiDto) {
					serveiService.delete(
							((ServeiDto)element).getId());
				}
			}
		}
	}

	public String imprimirObjecte(Object object) {
		return ReflectionToStringBuilder.toString(
				object,
				new RecursiveToStringStyle());
	}



	@SuppressWarnings("serial")
	private static class RecursiveToStringStyle extends ToStringStyle {
		private static final int INFINITE_DEPTH = -1;
		private int maxDepth;
		private int depth;
		public RecursiveToStringStyle() {
			this(INFINITE_DEPTH);
		}
		public RecursiveToStringStyle(int maxDepth) {
			setUseShortClassName(true);
			setUseIdentityHashCode(false);
			this.maxDepth = maxDepth;
		}
		@Override
		protected void appendDetail(
				StringBuffer buffer,
				String fieldName,
				Object value) {
			if (value.getClass().getName().startsWith("java.lang.") || (maxDepth != INFINITE_DEPTH && depth >= maxDepth)) {
				buffer.append(value);
			} else {
				depth++;
				buffer.append(ReflectionToStringBuilder.toString(value, this));
				depth--;
			}
		}
		@Override
		protected void appendDetail(
				StringBuffer buffer,
				String fieldName,
				Object[] array) {
			depth++;
			buffer.append(ReflectionToStringBuilder.toString(
					array,
					this,
					true,
					true));
			depth--;
		}
	}

	abstract class TestAmbElementsCreats {
		public abstract void executar(List<Object> elementsCreats) throws Exception;
	}

}
