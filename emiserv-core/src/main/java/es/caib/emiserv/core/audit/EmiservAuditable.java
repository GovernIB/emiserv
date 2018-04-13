/**
 * 
 */
package es.caib.emiserv.core.audit;

import java.io.Serializable;

import org.springframework.data.jpa.domain.AbstractAuditable;

import es.caib.emiserv.core.entity.UsuariEntity;

/**
 * Classe basse de on extendre per a activar les auditories.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
//@EntityListeners(EmiservAuditingEntityListener.class)
public class EmiservAuditable<PK extends Serializable> extends AbstractAuditable<UsuariEntity, PK> {

	private static final long serialVersionUID = 5373083799666869320L;

}
