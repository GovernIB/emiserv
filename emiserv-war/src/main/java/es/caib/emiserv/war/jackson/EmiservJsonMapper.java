/**
 * 
 */
package es.caib.emiserv.war.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * ObjectMapper de Jackson configurat per EMISERV.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class EmiservJsonMapper extends ObjectMapper {

	public EmiservJsonMapper() {
		super();
		this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

}
