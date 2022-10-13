package es.caib.emiserv.api.externa.controller;

import es.caib.emiserv.client.comu.ServeiTipus;
import es.caib.emiserv.client.dadesobertes.DadesObertesResposta;
import es.caib.emiserv.logic.intf.dto.ConsultaOpenDataDto;
import es.caib.emiserv.logic.intf.service.ExplotacioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Tag(name="Dades obertes v2", description = "Servei de consulta de dades obertes")
@RestController
@RequestMapping("/v2")
public class ExplotacioDadesExternesV2RestController {

    @Autowired
    private ExplotacioService explotacioService;

    @Operation(summary = "Dades obertes",
            description = "Aquest servei retorna un llistat de sol·licituds realitzades a EMISERV segons el format requerit pel portal Open Data de la CAIB  \n" +
                    "Si no s'indica cap valor per a la data d'inici i la data de fi, s'assignaran els seguents valors per defecte:  \n" +
                    " - __Data inici__: Primer dia del mes anterior  \n" +
                    " - __Data fi__: Darrer dia del mes anterior.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realitzada correctament", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DadesObertesResposta.class,
                                    description = "Llista de consultes realitzades a Emiserv"),
                            examples = {
                                    @ExampleObject(value = "{ \"totalElements\": 39," +
                                            "\"paginaActual\": 0," +
                                            "\"totalPagines\": 20," +
                                            "\"properaPagina\": \"http://server/emiservapi/externa/opendata?dataFi=2022-04-28&dataInici=2022-01-01&entitatNif=12345678Z&pagina=3&mida=10\"," +
                                            "\"dades\": [{ \"entitatNom\": \"Entitat de test\"," +
                                            "        \"entitatNif\": \"12345678Z\"," +
                                            "        \"departamentCodi\": null," +
                                            "        \"departamentNom\": \"Programari\"," +
                                            "        \"procedimentCodi\": \"TEST\"," +
                                            "        \"procedimentNom\": \"Procediment de test\"," +
                                            "        \"serveiCodi\": \"EMISERVTEST\"," +
                                            "        \"serveiNom\": \"Test EMISOR\"," +
                                            "        \"emissor\": \"Emisor de test\"," +
                                            "        \"emissorNif\": \"12345678A\"," +
                                            "        \"consentiment\": \"Si\"," +
                                            "        \"finalitat\": \"test\"," +
                                            "        \"solicitudId\": \"PBL00000000000000000000011\"," +
                                            "        \"data\": \"2022-01-12T08:10:24.227+0000\"," +
                                            "        \"tipus\": \"BACKOFFICE\"," +
                                            "        \"resultat\": \"ERROR\"" +
                                            "       }," +
                                            "       { \"entitatNom\": \"Entitat de test\"," +
                                            "        \"entitatNif\": \"12345678Z\"," +
                                            "        \"departamentCodi\": null," +
                                            "        \"departamentNom\": \"Programari\"," +
                                            "        \"procedimentCodi\": \"TEST\"," +
                                            "        \"procedimentNom\": \"Procediment de test\"," +
                                            "        \"serveiCodi\": \"EMISERVTEST\"," +
                                            "        \"serveiNom\": \"Test EMISOR\"," +
                                            "        \"emissor\": \"Emisor de test\"," +
                                            "        \"emissorNif\": \"12345678A\"," +
                                            "        \"consentiment\": \"Si\"," +
                                            "        \"finalitat\": \"test\"," +
                                            "        \"solicitudId\": \"PBL00000000000000000000012\"," +
                                            "        \"data\": \"2022-01-12T08:10:40.403+0000\"," +
                                            "        \"tipus\": \"BACKOFFICE\"," +
                                            "        \"resultat\": \"ERROR\"" +
                                            "       }]" +
                                            "}")
                            })
            })
    })
    @GetMapping(value = "/opendata", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DadesObertesResposta> opendata(
            HttpServletRequest request,
            @RequestParam(required = false) @Parameter(description = "Nif de l’entitat")
            final String entitatNif,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "Data inicial de la consulta en format yyyy-MM-dd. Si no s'informa s'afagarà el primer dia del mes anterior")
            final Date dataInici,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "Data final de la consulta en format yyyy-MM-dd. Si no s'informa s'agafarà l'últim dia del més anterior")
            final Date dataFi,
            @RequestParam(required = false) @Parameter(description = "Codi del procediment")
            final String procedimentCodi,
            @RequestParam(required = false) @Parameter(description = "Codi del servei")
            final String serveiCodi,
            @RequestParam(required = false) @Parameter(description = "Tipus del servei")
            final ServeiTipus tipus,
            @RequestParam(required = false) @Parameter(description = "Número de pàgina a retornar, començant a contar des de 0. Si no s'informa es retornarà la primera pàgina")
            final Integer pagina,
            @RequestParam(required = false) @Parameter(description = "Mida de la pàgina a retornar. Si no s'informa s'agafarà el valor 50")
            Integer mida) {


        String myHostName = request.getServerName();
        if (request.getServerPort() != 80) {
            myHostName += ":" + request.getServerPort();
        }
        myHostName += request.getContextPath();

        var consultaOpenDataDto = ConsultaOpenDataDto.builder()
                .entitatNif(entitatNif)
                .dataInici(dataInici)
                .dataFi(dataFi)
                .procedimentCodi(procedimentCodi)
                .serveiCodi(serveiCodi)
                .tipus(tipus)
                .pagina(pagina)
                .mida(mida)
                .appPath(request.getRequestURL().toString())
                .build();

        var resposta = explotacioService.findOpenDataV2(consultaOpenDataDto);
        return new ResponseEntity<>(resposta, HttpStatus.OK);
    }
}
