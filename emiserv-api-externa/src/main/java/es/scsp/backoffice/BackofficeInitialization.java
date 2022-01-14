package es.scsp.backoffice;

import es.caib.emiserv.logic.intf.service.ScspService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Classe que al instanciar-se carrega totes les classes que implementen la interfície es.scsp.common.backoffice.BackOffice
 * de la llibreria backoffice.jar
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
@PropertySource(ignoreResourceNotFound = true, value = {
//        "file://${" + ScspService.APP_PROPERTIES + "}",
        "file://${" + ScspService.APP_SYSTEM_PROPERTIES + "}"})
public class BackofficeInitialization {

    @Autowired
    private Environment env;

    BackofficeClassloader backofficeClassloader;

    @PostConstruct
    public void loadBackofficeClasses() throws IOException {
        log.info("BKO - Inicialitzant classes del backoffice...");
        backofficeClassloader = new BackofficeClassloader();
        String backofficeJarPath = env.getProperty("es.caib.emiserv.backoffice.jar.path");
        if (backofficeJarPath != null && !backofficeJarPath.isEmpty())
            backofficeClassloader.setBackofficeJarPath(backofficeJarPath);
        List<String> backofficeClassNames = getBackofficeClasses();
        Set<String> backofficeJarClassNames = backofficeClassloader.getClassNamesFromBackofficeJarFile();
        backofficeClassNames.retainAll(backofficeJarClassNames);
        backofficeClassNames.forEach(c -> {
            try {
                backofficeClassloader.findClass(c);
                log.info("BKO - Classe de backoffice {} carregada correctament", c);
            } catch (ClassNotFoundException e) {
                log.error("BKO - No ha estat possible carregar la classe '{}' del backoffice", e);
            }
        });
        log.info("BKO - Inicialització finalitzada");
    }

    private List<String> getBackofficeClasses() {

        List<String> resposta = new ArrayList<>();
        try {
            ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
            provider.addIncludeFilter(new AbstractClassTestingTypeFilter() {
                @Override
                protected boolean match(ClassMetadata metadata) {
                    for (String interfaceName : metadata.getInterfaceNames()) {
                        if ("es.scsp.common.backoffice.BackOffice".equals(interfaceName))
                            return true;
                    }
                    return false;
                }
            });
            provider.setResourceLoader(new PathMatchingResourcePatternResolver(backofficeClassloader));
            Set<BeanDefinition> components = provider.findCandidateComponents("es/caib/emiserv/backoffice");
            for (BeanDefinition component : components) {
                resposta.add(component.getBeanClassName());
            }
        } catch (Exception e) {
            log.error("No ha estat possible carregar les classes de backoffice", e);
        }
        return resposta;
    }
}
