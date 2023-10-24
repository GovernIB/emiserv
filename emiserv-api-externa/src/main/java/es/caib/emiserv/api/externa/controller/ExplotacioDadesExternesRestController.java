package es.caib.emiserv.api.externa.controller;

import es.caib.emiserv.client.comu.ServeiTipus;
import es.caib.emiserv.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.emiserv.logic.intf.service.ExplotacioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Tag(name="Dades obertes", description = "Servei de consulta de dades obertes")
@RestController
public class ExplotacioDadesExternesRestController {

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
                            array = @ArraySchema(schema = @Schema(implementation = DadesObertesRespostaConsulta.class,
                                    description = "Llista de consultes realitzades a Emiserv")),
                            examples = {
                                    @ExampleObject(value = "[{ \"entitatNom\": \"Entitat de test\"," +
                                            "        \"entitatCodi\": \"CODI\"," +
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
                                            "        \"titularTipusDoc\": \"NIF\"," +
                                            "        \"solicitudId\": \"PBL00000000000000000000011\"," +
                                            "        \"data\": 1621854187759," +
                                            "        \"tipus\": \"BACKOFFICE\"," +
                                            "        \"resultat\": \"ERROR\"" +
                                            "}]")
                            })
            })
    })
    @GetMapping(value = "/opendata", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DadesObertesRespostaConsulta>> opendata(
            HttpServletRequest request,
            @RequestParam(required = false) @Parameter(description = "Nif de l’entitat")
            final String entitatNif,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "Data inicial de la consulta en format yyyy-MM-dd")
            final Date dataInici,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "Data final de la consulta en format yyyy-MM-dd")
            final Date dataFi,
            @RequestParam(required = false) @Parameter(description = "Codi del procediment")
            final String procedimentCodi,
            @RequestParam(required = false) @Parameter(description = "Codi del servei")
            final String serveiCodi,
            @RequestParam(required = false) @Parameter(description = "Tipus del servei")
            final ServeiTipus tipus) {

        Date dInici = dataInici;
        Date dFi = dataFi;
        if (dataFi == null && dataInici == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1);

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            dInici = calendar.getTime();

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            dFi = calendar.getTime();
        }

        List<DadesObertesRespostaConsulta> entitats = explotacioService.findOpenData(
                entitatNif,
                dInici,
                dFi,
                procedimentCodi,
                serveiCodi);

        return new ResponseEntity<>(entitats, HttpStatus.OK);
    }
}
