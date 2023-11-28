package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.helper.PropertiesHelper;
import es.caib.emiserv.logic.intf.dto.ConfigDto;
import es.caib.emiserv.logic.intf.dto.ConfigGroupDto;
import es.caib.emiserv.logic.intf.dto.ConfigSourceEnumDto;
import es.caib.emiserv.logic.intf.service.ConfigService;
import es.caib.emiserv.persist.entity.ConfigEntity;
import es.caib.emiserv.persist.repository.ConfigGroupRepository;
import es.caib.emiserv.persist.repository.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Classe que implementa els metodes per consultar i editar les configuracions de l'aplicació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigGroupRepository configGroupRepository;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;
    @Autowired
    private PropertiesHelper propertiesHelper;

    @Autowired
    private Environment environment;

    @Override
    @Transactional
    public ConfigDto updateProperty(ConfigDto property) {
        log.info(String.format("Actualització valor propietat %s a %s ", property.getKey(), property.getValue()));
        ConfigEntity configEntity = configRepository.getOne(property.getKey());

        configEntity.update(property.getValue());
        propertiesHelper.reloadDbProperties();
        return conversioTipusHelper.convertir(configEntity, ConfigDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigGroupDto> findAll() {
        log.info("Consulta totes les propietats");
        List<ConfigGroupDto> configGroupDtoList =  conversioTipusHelper.convertirList(
                configGroupRepository.findByParentCodeIsNull(Sort.by(Sort.Direction.ASC, "position")),
                ConfigGroupDto.class);

        for (ConfigGroupDto cGroup: configGroupDtoList) {
            processPropertyValues(cGroup);
        }
        return configGroupDtoList;
    }

    @Override
    @Transactional
    public List<String> syncFromJBossProperties() {
        log.info("Sincronitzant les propietats amb JBoss");
        throw new RuntimeException("Function not implemented");
    }

//    @Override
//    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
//        return environment.getProperty(key, targetType, defaultValue);
//    }

    private void processPropertyValues(ConfigGroupDto cGroup) {
//        if ("SCSP".equals(cGroup.getKey())) {
//            List<ScspCoreParametroConfiguracionEntity> scspParametres = scspCoreParametroConfiguracionRepository.findAll(Sort.by(Sort.Direction.ASC, "nombre"));
//            if (scspParametres != null) {
//                List<ConfigDto> scspConfigs = scspParametres.stream()
//                        .map(p -> ConfigDto.builder()
//                                .key(p.getNombre())
//                                .value(p.getValor())
//                                .descriptionKey("propietat." + p.getNombre())
//                                .sourceProperty(ConfigSourceEnumDto.SCSP)
//                                .build())
//                        .collect(Collectors.toList());
//                cGroup.setConfigs(scspConfigs);
//            }
//        }
        for (ConfigDto config: cGroup.getConfigs()) {
            if ("PASS".equals(config.getTypeCode())){
                config.setValue("*****");
            } else if (ConfigSourceEnumDto.FILE.equals(config.getSourceProperty())) {
                config.setValue(environment.getProperty(config.getKey()));
            }
        }

        if (cGroup.getInnerConfigs() != null && !cGroup.getInnerConfigs().isEmpty()) {
            for (ConfigGroupDto child : cGroup.getInnerConfigs()) {
                processPropertyValues(child);
            }
        }
    }
}
