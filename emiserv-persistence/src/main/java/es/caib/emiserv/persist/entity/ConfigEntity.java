package es.caib.emiserv.persist.entity;

import es.caib.emiserv.logic.intf.dto.ConfigSourceEnumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


/**
 * Classe del model de dades que representa una alerta d'error en segón pla.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(	name = AbstractAuditableEntity.TABLE_PREFIX + "_config")
public class ConfigEntity {

    @Id
    @Column(name = "key", length = 256, nullable = false)
    private String key;

    @Column(name = "value", length = 2048, nullable = true)
    private String value;

    @Column(name = "description_key", length = 2048, nullable = true)
    private String descriptionKey;

    @Column(name = "source_property", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConfigSourceEnumDto sourceProperty;

    @Column(name = "group_code", length = 2048, nullable = true)
    private String groupCode;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_code", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "not_config_type_fk"))
    private ConfigTypeEntity type;

    @Column(name = "position")
    private int position;

    @Column(name = "requereix_reinici")
    private boolean requereixReinici;

    @Column(name = "lastmodifiedby", length = 256, nullable = true)
    private String lastModifiedCodi;

    @Column(name = "lastmodifieddate", length = 2048, nullable = true)
    private LocalDateTime lastModifiedDate;

    public ConfigEntity(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Per a mapejar el Dto de la vista.
     *
     * @return El llistat de possibles valors que pot prendre la propietat
     */
    public List<String> getValidValues() {
        return type == null ? Collections.emptyList() : type.getValidValues();
    }
    public String getTypeCode() {
        return type == null ? "" : type.getCode();
    }

    public void update(String value) {
        this.value = value;
        this.lastModifiedDate = LocalDateTime.now();
        if (SecurityContextHolder.getContext().getAuthentication() != null)
            this.lastModifiedCodi = SecurityContextHolder.getContext().getAuthentication().getName();

    }

    public static ConfigEntity from(String key, String groupCode, ConfigSourceEnumDto sourceProperty) {
        return ConfigEntity.builder().key(key).groupCode(groupCode).sourceProperty(sourceProperty).build();
    }

}
