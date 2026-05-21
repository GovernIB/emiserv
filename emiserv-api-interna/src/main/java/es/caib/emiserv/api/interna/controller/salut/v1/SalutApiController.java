package es.caib.emiserv.api.interna.controller.salut.v1;

import es.caib.comanda.model.server.monitoring.*;
import es.caib.comanda.ms.salut.helper.MonitorHelper;
import es.caib.comanda.ms.salut.helper.SalutHelper;
import es.caib.emiserv.logic.intf.dto.SubsistemesEnum;
import es.caib.emiserv.logic.intf.service.AplicacioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.List;

import static es.caib.emiserv.api.interna.config.OpenApiConfig.SECURITY_NAME;
import static es.caib.emiserv.api.interna.config.OpenApiConfig.SECURITY_SCHEME;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

/**
 * API de Salut per COMANDA
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/salut/v1")
@Tag(name = "Salut", description = "API de salut")
@SecurityScheme(type = SecuritySchemeType.HTTP, name = SECURITY_NAME, scheme = SECURITY_SCHEME)
public class SalutApiController {

    private final AplicacioService aplicacioService;

    @GetMapping("/info")
    @PreAuthorize("hasRole('EMS_COM')")
    @SecurityRequirement(name = SECURITY_NAME)
    @Operation(operationId = "salutInfo",
            summary = "Obtenir informació de l'aplicació",
            description = "Retorna dades bàsiques de l'aplicació (codi, nom, versió, data de build, etc.) i contextos exposats.",
            tags = {"Salut"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operació correcta", content = @Content(schema = @Schema(implementation = AppInfo.class))),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor")
    })
    public AppInfo salutInfo(HttpServletRequest request) throws java.io.IOException {
        SalutHelper.BuildInfo buildInfo = SalutHelper.getBuildInfo();

        return new AppInfo()
                .codi("EMS")
                .nom("Emiserv")
                .data(buildInfo.getBuildDate())
                .versio(buildInfo.getVersion())
                .revisio(buildInfo.getCommitId())
                .jdkVersion(buildInfo.getBuildJDK())
                .versioJboss(MonitorHelper.getApplicationServerInfo())
                .contexts(aplicacioService.getContextsInfo(getBaseUrl(request)))
                .integracions(aplicacioService.getIntegracionsInfo())
                .subsistemes(aplicacioService.getSubsistemesInfo());
    }

    private String getBaseUrl(HttpServletRequest request) {
        return ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null) // elimina el context path "/comandaapi/..."
                .build()
                .toUriString();
    }

    @GetMapping
    @Operation(operationId = "salut",
            summary = "Obtenir informació de l'estat de salut de l'aplicació",
            description = "Retorna l'estat de salut funcional i integracions, amb metadades de versió.",
            tags = {"Salut"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operació correcta",
                    content = @Content(schema = @Schema(implementation = SalutInfo.class))),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor"),
    })
    public SalutInfo salut(
            HttpServletRequest request,
            @DateTimeFormat(iso = DATE_TIME) @Parameter(name = "dataPeriode", description = "Data mínima de la que es demana informació per període", required = false) @RequestParam(required = false) OffsetDateTime dataPeriode,
            @DateTimeFormat(iso = DATE_TIME) @Parameter(name = "dataTotal", description = "Data mínima de la que demana informació per totals", required = false) @RequestParam(required = false) OffsetDateTime dataTotal) throws java.io.IOException {

        long startTime = System.currentTimeMillis();
        InformacioSistema infoSistema = null;
        try {
            es.caib.comanda.model.server.monitoring.InformacioSistema infoServer = MonitorHelper.getInfoSistema();
            if (infoServer != null) {
                infoSistema = new InformacioSistema()
                        .processadors(infoServer.getProcessadors())
                        .carregaSistema(infoServer.getCarregaSistema())
                        .cpuSistema(infoServer.getCpuSistema())
                        .memoriaTotal(infoServer.getMemoriaTotal())
                        .memoriaDisponible(infoServer.getMemoriaDisponible())
                        .espaiDiscTotal(infoServer.getEspaiDiscTotal())
                        .espaiDiscLliure(infoServer.getEspaiDiscLliure())
                        .sistemaOperatiu(infoServer.getSistemaOperatiu())
                        .dataArrencada(infoServer.getDataArrencada())
                        .tempsFuncionant(infoServer.getTempsFuncionant());
            }
        } catch (Exception e) {
            // Ignorar errors en obtenir info sistema
        }
        SalutHelper.BuildInfo buildInfo = SalutHelper.getBuildInfo();
        Integer latenciaDb = aplicacioService.measureDbLatencyMs();
        Integer latencia = (int) (System.currentTimeMillis() - startTime);

        EstatSalutEnum estatDb = latenciaDb != null ? EstatSalutEnum.UP : EstatSalutEnum.DOWN;
        EstatSalutEnum estat = EstatSalutEnum.DOWN.equals(estatDb) ? EstatSalutEnum.ERROR : EstatSalutEnum.UP;

        EstatSalut estatSalutDb = new EstatSalut().estat(estatDb).latencia(latenciaDb);
        EstatSalut estatSalut = new EstatSalut().estat(estat).latencia(latencia);

        // Si l'estat dels subsistemes no és UP, llavors l'estat de l'aplicació tampoc ho serà
        List<SubsistemaSalut> subsistemesSalut = aplicacioService.getSubsistemesSalut();
        EstatSalutEnum estatSubsistemes = calculaEstatSubsistemes(subsistemesSalut);
        if (EstatSalutEnum.UP.equals(estat) && !EstatSalutEnum.UP.equals(estatSubsistemes) && !EstatSalutEnum.UNKNOWN.equals(estatSubsistemes)) {
            estatSalut.setEstat(estatSubsistemes);
        }

        return new SalutInfo()
                .codi("EMS")
                .data(buildInfo.getBuildDate())
                .versio(buildInfo.getVersion())
                .estatGlobal(estatSalut)
                .estatBaseDeDades(estatSalutDb)
                .informacioSistema(infoSistema)
                .integracions(aplicacioService.getIntegracionsSalut(dataPeriode, dataTotal))
                .missatges(aplicacioService.getMissatgesSalut())
                .subsistemes(subsistemesSalut);
    }

    private EstatSalutEnum calculaEstatSubsistemes(List<SubsistemaSalut> subsistemesSalut) {

        EstatSalutEnum estatSubsistemes = EstatSalutEnum.UNKNOWN;
        boolean anyDown = false;
        boolean anyError = false;
        boolean anyDegraded = false;
        boolean anyWarn = false;
        boolean anyUp = false;

        for (SubsistemaSalut subsistemaSalut : subsistemesSalut) {
            SubsistemesEnum subsistemaEnum = SubsistemesEnum.valueOfCodi(subsistemaSalut.getCodi());
            if (subsistemaEnum == null) {
                continue;
            }
            boolean critic = subsistemaEnum.isSistemaCritic();

            switch (subsistemaSalut.getEstat()) {
                case UP:
                    anyUp = true;
                    break;
                case WARN:
                    anyWarn = true;
                    break;
                case DEGRADED:
                    if (critic) anyDegraded = true;
                    else anyWarn = true;
                    break;
                case ERROR:
                    if (critic) anyError = true;
                    else anyWarn = true;
                    break;
                case DOWN:
                    if (critic) anyDown = true;
                    else anyWarn = true;
                    break;
                default:
                    // UNKNOWN o altres
            }

            if (anyDown || anyError) {
                estatSubsistemes = EstatSalutEnum.ERROR;
            } else if(anyDegraded) {
                estatSubsistemes = EstatSalutEnum.DEGRADED;
            } else if (anyWarn) {
                estatSubsistemes = EstatSalutEnum.WARN;
            } else if (anyUp) {
                estatSubsistemes = EstatSalutEnum.UP;
            } else {
                estatSubsistemes = EstatSalutEnum.UNKNOWN;
            }
        }
        return estatSubsistemes;
    }

}
