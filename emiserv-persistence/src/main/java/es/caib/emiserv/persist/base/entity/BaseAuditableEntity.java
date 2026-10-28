package es.caib.emiserv.persist.base.entity;

import es.caib.emiserv.logic.intf.base.model.Resource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base per a definir les demés entitats de l'aplicació.
 *
 * @param <R> classe del recurs associat.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAuditableEntity<R extends Resource<?>, PK extends Serializable>
		extends BaseResourceEntity<R, PK>
		implements AuditableEntity {

	@CreatedBy
	@Column(name = "created_by", length = 64, nullable = false)
	private String createdBy;
	@CreatedDate
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate;
	@LastModifiedBy
	@Column(name = "lastmodified_by", length = 64)
	private String lastModifiedBy;
	@LastModifiedDate
	@Column(name = "lastmodified_date")
	private LocalDateTime lastModifiedDate;

	@Override
	public void updateCreated(
			String createdBy,
			LocalDateTime createdDate) {
		this.createdBy = createdBy;
		this.createdDate = (createdDate != null) ? createdDate : LocalDateTime.now();
	}

	@Override
	public void updateLastModified(
			String lastModifiedBy,
			LocalDateTime lastModifiedDate) {
		this.lastModifiedBy = lastModifiedBy;
		this.lastModifiedDate = (lastModifiedDate != null) ? lastModifiedDate : LocalDateTime.now();
	}

}