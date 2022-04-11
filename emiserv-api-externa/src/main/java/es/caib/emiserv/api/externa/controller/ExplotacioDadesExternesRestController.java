package es.caib.emiserv.api.externa.controller;

import es.caib.emiserv.client.comu.ServeiTipus;
import es.caib.emiserv.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.emiserv.logic.intf.service.ExplotacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/opendata")
public class ExplotacioDadesExternesRestController {

    @Autowired
    private ExplotacioService explotacioService;

//    @Operation(summary = "Dades obertes",
//            description = "Aquest servei retorna un llistat de sol·licituds realitzades a EMISERV segons el format requerit pel portal Open Data de la CAIB  \n" +
//                    "Si no s'indica cap valor per a la data d'inici i la data de fi, s'assignaran els seguents valors per defecte:  \n" +
//                    " - __Data inici__: Primer dia del mes anterior  \n" +
//                    " - __Data fi__: Darrer dia del mes anterior.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Consulta realitzada correctament", content = {
//                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
//                            array = @ArraySchema(schema = @Schema(implementation = DadesObertesRespostaConsulta.class,
//                                    description = "Llista de consultes realitzades a Emiserv")),
//                            examples = {
//                                    @ExampleObject(value = "[{ \"entitatNom\": \"Límit Tecnologies\"," +
//                                            "        \"entitatNif\": \"12345678Z\"," +
//                                            "        \"departamentCodi\": null," +
//                                            "        \"departamentNom\": \"Programari\"," +
//                                            "        \"procedimentCodi\": \"TEST\"," +
//                                            "        \"procedimentNom\": \"Procediment de test\"," +
//                                            "        \"serveiCodi\": \"EMISERVTEST\"," +
//                                            "        \"serveiNom\": \"Test EMISOR\"," +
//                                            "        \"emissor\": \"Límit Tecnologies\"," +
//                                            "        \"emissorNif\": \"12345678Z\"," +
//                                            "        \"consentiment\": \"Si\"," +
//                                            "        \"finalitat\": \"test\"," +
//                                            "        \"solicitudId\": \"PBL00000000000000000000011\"," +
//                                            "        \"data\": 1621854187759," +
//                                            "        \"tipus\": \"BACKOFFICE\"," +
//                                            "        \"resultat\": \"ERROR\"" +
//                                            "}]")
//                            })
//            })
//    })
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DadesObertesRespostaConsulta>> opendata(
//            HttpServletRequest request,
            @RequestParam(required = false) //@Parameter(description = "Nif de l’entitat")
            final String entitatCodi,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) //@Parameter(description = "Data inicial de la consulta en format yyyy-MM-dd")
            final Date dataInici,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) //@Parameter(description = "Data final de la consulta en format yyyy-MM-dd")
            final Date dataFi,
            @RequestParam(required = false) //@Parameter(description = "Codi del procediment")
            final String procedimentCodi,
            @RequestParam(required = false) //@Parameter(description = "Codi del servei")
            final String serveiCodi,
            @RequestParam(required = false) //@Parameter(description = "Tipus del servei")
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
                entitatCodi,
                dInici,
                dFi,
                procedimentCodi,
                serveiCodi);

        return new ResponseEntity<>(entitats, HttpStatus.OK);
    }
}
