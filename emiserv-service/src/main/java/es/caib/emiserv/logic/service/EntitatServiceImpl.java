package es.caib.emiserv.logic.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.emiserv.logic.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.helper.PaginacioHelper;
import es.caib.emiserv.logic.intf.dto.EntitatDto;
import es.caib.emiserv.logic.intf.dto.EntitatFiltreDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.exception.SistemaExternException;
import es.caib.emiserv.logic.intf.service.EntitatService;
import es.caib.emiserv.persist.entity.EntitatEntity;
import es.caib.emiserv.persist.repository.EntitatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class EntitatServiceImpl implements EntitatService {

    private final EntitatRepository entitatRepository;
    private final ConversioTipusHelper conversioTipusHelper;
    private final PaginacioHelper paginacioHelper;
    private final Environment environment;

    @Override
    @Transactional
    public EntitatDto create(EntitatDto entitat) {
        log.debug("Creant una nova entitat (entitat=" + entitat + ")");
        EntitatEntity entity = EntitatEntity.builder()
                .codi(entitat.getCodi())
                .nom(entitat.getNom())
                .cif(entitat.getCif())
                .build();
        return conversioTipusHelper.convertir(
                entitatRepository.save(entity),
                EntitatDto.class);
    }

    @Override
    @Transactional
    public EntitatDto update(EntitatDto entitat) throws NotFoundException {
        log.debug("Modificant nova entitat (entitat=" + entitat + ")");
        EntitatEntity entity = entitatRepository.findById(entitat.getId()).orElseThrow(() -> new NotFoundException(entitat.getId(), EntitatEntity.class));
        entity.update(entitat.getCodi(), entitat.getNom(), entitat.getCif());
        return conversioTipusHelper.convertir(
                entitatRepository.save(entity),
                EntitatDto.class);
    }

    @Override
    @Transactional
    public void delete(Long id) throws NotFoundException {
        log.debug("Esborrant entitat (id=" + id + ")");
        EntitatEntity entity = entitatRepository.findById(id).orElseThrow(() -> new NotFoundException(id, EntitatEntity.class));
        entitatRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public EntitatDto findById(Long id) {
        log.debug("Consulta entitat amb id (id=" + id + ")");
        EntitatEntity entity = entitatRepository.findById(id).orElseThrow(() -> new NotFoundException(id, EntitatEntity.class));
        return conversioTipusHelper.convertir(entity, EntitatDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public EntitatDto findByCodi(String codi) {
        log.debug("Consulta entitat amb codi (codi=" + codi + ")");
        EntitatEntity entity = entitatRepository.findByCodi(codi).orElse(null);
        return conversioTipusHelper.convertir(entity, EntitatDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaDto<EntitatDto> findAllPaginat(EntitatFiltreDto filtre, PaginacioParamsDto paginacioParams) {
        log.debug("Consulta de les entitats paginades (paginacioParams=" + paginacioParams + ")");
        return paginacioHelper.toPaginaDto(
                entitatRepository.findPermesosPaginat(
                        (filtre == null || Strings.isBlank(filtre.getCodi())),
                        filtre != null && filtre.getCodi() != null ? filtre.getCodi() : "",
                        (filtre == null || Strings.isBlank(filtre.getNom())),
                        filtre != null && filtre.getNom() != null ? filtre.getNom() : "",
                        (filtre == null || Strings.isBlank(filtre.getCif())),
                        filtre != null && filtre.getCif() != null ? filtre.getCif() : "",
                        paginacioHelper.toSpringDataPageable(paginacioParams)),
                EntitatDto.class);
    }

    @Override
    @Transactional
    public void sincronitzar() throws SistemaExternException {
        String url = getPinbalPropertyUrl();
        String usuari = getPinbalPropertyUsername();
        String contrasenya = getPinbalPropertyPassword();

        List<EntitatDto> entitats = null;

        if (Strings.isBlank(url) || Strings.isBlank(usuari) || Strings.isBlank(contrasenya)) {
            throw new SistemaExternException("entitat.controller.sincronitzar.propietats");
        }
        try {
            var urlAmbMetode = url + (url.endsWith("/") ? "" : "/") + "interna/api/entitats";
            Client jerseyClient = crearClient(usuari, contrasenya);
            WebTarget webTarget = jerseyClient.target(urlAmbMetode);
            String json = webTarget.request(MediaType.APPLICATION_JSON).get(String.class);

            entitats = getMapper().readValue(json, new TypeReference<List<EntitatDto>>() {});
        } catch (Exception e) {
           log.error("No s'han pogut obtenir les entitats de Pinbal", e);
            throw new SistemaExternException("entitat.controller.sincronitzar.error", e);
        }

        entitats.forEach(e -> {
            entitatRepository.findByCodi(e.getCodi()).ifPresentOrElse(
                    (entitat) -> {
                        entitat.update(e.getCodi(), e.getNom(), e.getCif());
                        entitatRepository.save(entitat);
                    },
                    () -> {
                        entitatRepository.save(EntitatEntity.builder()
                            .codi(e.getCodi())
                            .nom(e.getNom())
                            .cif(e.getCif()).build());
                    }
            );
        });
    }

    private Client crearClient(String usuari, String contrasenya) {

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JacksonFeature.class);
        clientConfig.register(HttpAuthenticationFeature.basic(usuari, contrasenya));

        ClientBuilder clientBuilder = ClientBuilder.newBuilder().withConfig(clientConfig);
        clientBuilder.connectTimeout(5000, TimeUnit.MILLISECONDS);
        clientBuilder.readTimeout(10000, TimeUnit.MILLISECONDS);

        return clientBuilder.build();
    }

    private ObjectMapper getMapper() {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private String getPinbalPropertyUrl() {
        return environment.getProperty("es.caib.emiserv.pinbal.url");
    }

    private String getPinbalPropertyUsername() {
        return environment.getProperty("es.caib.emiserv.pinbal.username");
    }

    private String getPinbalPropertyPassword() {
        return environment.getProperty("es.caib.emiserv.pinbal.password");
    }
}
