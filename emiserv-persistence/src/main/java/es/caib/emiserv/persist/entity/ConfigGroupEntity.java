package es.caib.emiserv.persist.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(	name = AbstractAuditableEntity.TABLE_PREFIX + "_config_group")
public class ConfigGroupEntity {

    @Id
    @Column(name = "code", length = 128, nullable = false)
    private String key;

    @Column(name = "description_key", length = 512, nullable = true)
    private String descriptionKey;

    @Column(name = "position")
    private int position;

    @Column(name = "parent_code")
    private String parentCode;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_code")
    @OrderBy("position ASC")
    private Set<ConfigEntity> configs;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_code")
    @OrderBy("position ASC")
    private Set<ConfigGroupEntity> innerConfigs;
}
