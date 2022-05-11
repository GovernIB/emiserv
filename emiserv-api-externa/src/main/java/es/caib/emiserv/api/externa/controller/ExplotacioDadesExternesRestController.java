package es.caib.emiserv.api.externa.controller;

import es.caib.emiserv.client.comu.ServeiTipus;
import es.caib.emiserv.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.emiserv.logic.intf.service.ExplotacioService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
public class ExplotacioDadesExternesRestController {

    @Autowired
    private ExplotacioService explotacioService;

    @ApiOperation(value = "Dades obertes",
            notes = "Aquest servei retorna un llistat de sol·licituds realitzades a EMISERV segons el format requerit pel portal Open Data de la CAIB  \n" +
                    "Si no s'indica cap valor per a la data d'inici i la data de fi, s'assignaran els seguents valors per defecte:  \n" +
                    " - __Data inici__: Primer dia del mes anterior  \n" +
                    " - __Data fi__: Darrer dia del mes anterior.",
            response = DadesObertesRespostaConsulta.class,
            responseContainer = "List",
            tags = {"DadesObertes"})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Consulta realitzada correctament")})
    @RequestMapping(value = "/opendata", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DadesObertesRespostaConsulta>> opendata(
            @RequestParam(required = false) @ApiParam(value = "Nif de l’entitat")
            final String entitatNif,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "Data inicial de la consulta en format yyyy-MM-dd")
            final Date dataInici,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "Data final de la consulta en format yyyy-MM-dd")
            final Date dataFi,
            @RequestParam(required = false) @ApiParam(value = "Codi del procediment")
            final String procedimentCodi,
            @RequestParam(required = false) @ApiParam(value = "Codi del servei")
            final String serveiCodi,
            @RequestParam(required = false) @ApiParam(value = "Tipus del servei")
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
