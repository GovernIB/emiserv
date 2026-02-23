package es.caib.emiserv.api.interna.controller.estadistiques.v1;

import es.caib.comanda.model.server.monitoring.DimensioDesc;
import es.caib.comanda.model.server.monitoring.EstadistiquesInfo;
import es.caib.comanda.model.server.monitoring.IndicadorDesc;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;
import es.caib.emiserv.logic.intf.service.ExplotacioService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static es.caib.emiserv.api.interna.config.OpenApiConfig.SECURITY_NAME;
import static es.caib.emiserv.api.interna.config.OpenApiConfig.SECURITY_SCHEME;


/**
 * API d'Estadístiques per COMANDA
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/estadistiques/v1")
@Tag(name = "COMANDA → APP / Estadistiques", description = "Contracte d'API d'estadístiques que COMANDA pot consultar a les APPs")
@SecurityScheme(type = SecuritySchemeType.HTTP, name = SECURITY_NAME, scheme = SECURITY_SCHEME)
public class EstadistiquesApiV1Controller {

    private final ExplotacioService explotacioService;


    @GetMapping("/info")
    @PreAuthorize("hasRole(EMS_COM)")
    @SecurityRequirement(name = SECURITY_NAME)
    @Operation(operationId = "estadistiquesInfo",
            summary = "Obtenir informació de 'estructura de les estadístiques",
            description = "Retorna el codi de l'app i el catàleg de dimensions i indicadors disponibles.",
            tags = {"COMANDA → APP / Estadistiques"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operació correcta",
                    content = @Content(schema = @Schema(implementation = EstadistiquesInfo.class))),
            @ApiResponse(responseCode = "401", description = "No autenticat"),
            @ApiResponse(responseCode = "403", description = "Prohibit"),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor")
    })
    public EstadistiquesInfo estadistiquesInfo() throws IOException {

        List<DimensioDesc> dimensions = explotacioService.getDimensions();
        List<IndicadorDesc> indicadors = explotacioService.getIndicadors();
        return new EstadistiquesInfo().codi("EMS").dimensions(dimensions).indicadors(indicadors);

    }

    @GetMapping
    @PreAuthorize("hasRole(EMS_COM)")
    @SecurityRequirement(name = SECURITY_NAME)
    @Operation(operationId = "estadistiques",
            summary = "Obtenir darreres estadístiques diàries disponibles",
            description = "Retorna registres d'estadístiques més recents disponibles (estadístiques d'ahir).",
            tags = {"COMANDA → APP / Estadistiques"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operació correcta",
                    content = @Content(schema = @Schema(implementation = RegistresEstadistics.class))),
            @ApiResponse(responseCode = "401", description = "No autenticat"),
            @ApiResponse(responseCode = "403", description = "Prohibit"),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor")
    })
    public RegistresEstadistics estadistiques(HttpServletRequest request) throws Exception {

        return explotacioService.consultaUltimesEstadistiques();
    }

    @GetMapping("/of/{data}")
    @PreAuthorize("hasRole(EMS_COM)")
    @SecurityRequirement(name = SECURITY_NAME)
    @Operation(operationId = "estadistiquesPerData",
            summary = "Obtenir estadístiques d'una data concreta",
            description = "Retorna les estadístiques corresponents a la data indicada amb format dd-MM-yyyy.",
            tags = {"COMANDA → APP / Estadistiques"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operació correcta",
                    content = @Content(schema = @Schema(implementation = RegistresEstadistics.class))),
            @ApiResponse(responseCode = "400", description = "Paràmetres invàlids (format de data incorrecte)"),
            @ApiResponse(responseCode = "401", description = "No autenticat"),
            @ApiResponse(responseCode = "403", description = "Prohibit"),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor")
    })
    public RegistresEstadistics estadistiquesPerData(
            HttpServletRequest request,
            @Parameter(name = "data", description = "Data en format dd-MM-yyyy", required = true) @PathVariable("data") String data) throws Exception {

        LocalDate date = LocalDate.parse(data, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        return explotacioService.consultaEstadistiques(date);
    }

    @GetMapping("/from/{dataInici}/to/{dataFi}")
    @PreAuthorize("hasRole(EMS_COM)")
    @SecurityRequirement(name = SECURITY_NAME)
    @Operation(operationId = "estadistiquesPerRang",
            summary = "Obtenir les estadístiques d'un interval donat",
            description = "Retorna llista d'estadístiques de tots els dies entre la dataInici i la dataFi (en format dd-MM-yyyy), ambdues incloses. " +
                    "La resposta contindrà un objecte de tipus RegistresEstadistics per a cada dia inclòs en l'intèrval.",
            tags = {"COMANDA → APP / Estadistiques"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operació correcta",
                    content = @Content(schema = @Schema(implementation = RegistresEstadistics.class))),
            @ApiResponse(responseCode = "400", description = "Paràmetre invàlid (format de data o rang)"),
            @ApiResponse(responseCode = "401", description = "No autenticat"),
            @ApiResponse(responseCode = "403", description = "Prohibit"),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor")
    })
    public List<RegistresEstadistics> estadistiquesPerRang(
            HttpServletRequest request,
            @Parameter(name = "dataInici", description = "Data d'inici en format dd-MM-yyyy", required = true) @PathVariable("dataInici") String dataInici,
            @Parameter(name = "dataFi", description = "Data de fi en format dd-MM-yyyy", required = true) @PathVariable("dataFi") String dataFi) throws Exception {

        LocalDate dataFrom = LocalDate.parse(dataInici, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate dataTo = LocalDate.parse(dataFi, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate startDate = dataFrom.isBefore(dataTo) ? dataFrom : dataTo;
        LocalDate endDate = dataFrom.isBefore(dataTo) ? dataTo : dataFrom;
        LocalDate ahir = LocalDate.now().minusDays(1);
        if (endDate.isAfter(ahir)) {
            endDate = ahir;
        }

        return explotacioService.consultaEstadistiques(startDate, endDate);
    }


}
