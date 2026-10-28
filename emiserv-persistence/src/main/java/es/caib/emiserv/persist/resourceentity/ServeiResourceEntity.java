package es.caib.emiserv.persist.resourceentity;

import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.model.ServeiResource;
import es.caib.emiserv.persist.base.entity.BaseResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entitat de base de dades del recurs {@link ServeiResource}.
 * <p/>
 * Mapeja la mateixa taula que l'entitat de negoci ServeiEntity ({@code ems_servei}) i només
 * s'empra per a les consultes del recurs REST: és immutable perquè les modificacions es fan
 * sempre a través de ServeiService.
 * <p/>
 * Els comptadors es calculen amb subconsultes (sense carregar col·leccions):
 * <ul>
 *   <li>{@code permisosCount}: principals diferents (usuaris o rols) amb entrades ACL sobre el
 *   servei, igual que ObjecteAmbPermisosDto.getPermisosCount() a la JSP (PermisosHelper agrupa
 *   les entrades per principal);</li>
 *   <li>{@code rutesCount}: rutes de destí del servei.</li>
 * </ul>
 *
 * @author Limit Tecnologies
 */
@Entity
@Immutable
@Table(name = "ems_servei")
@Getter
@Setter
@NoArgsConstructor
public class ServeiResourceEntity extends BaseResourceEntity<ServeiResource, Long> {

	/** Classe de les ACL dels serveis (ha de coincidir amb ServeiEntity.class.getName()). */
	public static final String ACL_CLASS = "es.caib.emiserv.persist.entity.ServeiEntity";

	@Id
	@Column(name = "id")
	private Long id;
	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "tipus", nullable = false)
	private ServeiTipusEnumDto tipus;
	@Column(name = "actiu")
	private boolean actiu;
	@Column(name = "configurat")
	private boolean configurat;
	@Column(name = "xsd_activa")
	private boolean xsdGestioActiva;
	@Formula("(select count(distinct ace.sid) from ems_acl_entry ace " +
			"join ems_acl_object_identity aoi on ace.acl_object_identity = aoi.id " +
			"join ems_acl_class acl on aoi.object_id_class = acl.id " +
			"where acl.class = '" + ACL_CLASS + "' and aoi.object_id_identity = id)")
	private long permisosCount;
	@Formula("(select count(*) from ems_servei_ruta_desti srd where srd.servei_id = id)")
	private long rutesCount;

}
