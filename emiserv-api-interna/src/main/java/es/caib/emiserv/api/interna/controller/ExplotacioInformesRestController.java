package es.caib.emiserv.api.interna.controller;

import es.caib.emiserv.client.comu.Departament;
import es.caib.emiserv.client.comu.Entitat;
import es.caib.emiserv.client.comu.EntitatsActuals;
import es.caib.emiserv.client.comu.Procediment;
import es.caib.emiserv.client.comu.Servei;
import es.caib.emiserv.client.comu.ServeiTipus;
import es.caib.emiserv.logic.intf.dto.InformeGeneralEstatDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.service.ExplotacioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ExplotacioInformesRestController {

    @Autowired
    private ExplotacioService explotacioService;

    @Operation(summary = "Informe general",
            description = "Aquest servei retorna l’acumulat de peticions realitzades entre dues dates distingint entre les peticions processades correctament o amb errors.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realitzada correctament", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Entitat.class,
                                    description = "Llista de peticions realitzades a Emiserv entre dues dates")),
                            examples = {
                                    @ExampleObject(value = "[\n" +
                                            "    {\n" +
                                            "        \"nom\": \"Límit Tecnologies\",\n" +
                                            "        \"nif\": \"12345678Z\",\n" +
                                            "        \"departaments\": [\n" +
                                            "            {\n" +
                                            "                \"codi\": null,\n" +
                                            "                \"nom\": \"Programari\",\n" +
                                            "                \"procediments\": [\n" +
                                            "                    {\n" +
                                            "                        \"codi\": \"TEST\",\n" +
                                            "                        \"nom\": \"Procediment de test\",\n" +
                                            "                        \"actiu\": false,\n" +
                                            "                        \"consultesBackoffice\": null,\n" +
                                            "                        \"consultesEnrutador\": null,\n" +
                                            "                        \"consultesTotal\": null,\n" +
                                            "                        \"totalBackoffice\": null,\n" +
                                            "                        \"totalEnrutador\": null,\n" +
                                            "                        \"serveis\": [\n" +
                                            "                            {\n" +
                                            "                                \"codi\": \"SVDCCAACPASWS01\",\n" +
                                            "                                \"nom\": \"Estar al corriente de obligaciones tributarias para solicitud de subvenciones y ayudas de la CCAA\",\n" +
                                            "                                \"emisorNom\": \"Límit Tecnologies\",\n" +
                                            "                                \"emisorNif\": \"12345678Z\",\n" +
                                            "                                \"consultesOk\": 22,\n" +
                                            "                                \"consultesError\": 56,\n" +
                                            "                                \"tipus\": \"BACKOFFICE\",\n" +
                                            "                                \"consultesTotal\": null\n" +
                                            "                            },\n" +
                                            "                            {\n" +
                                            "                                \"codi\": \"SVDSCDDWS01\",\n" +
                                            "                                \"nom\": \"Servei de consulta de dades de discapacitat\",\n" +
                                            "                                \"emisorNom\": \"Límit Tecnologies\",\n" +
                                            "                                \"emisorNif\": \"12345678Z\",\n" +
                                            "                                \"consultesOk\": 0,\n" +
                                            "                                \"consultesError\": 5,\n" +
                                            "                                \"tipus\": \"BACKOFFICE\",\n" +
                                            "                                \"consultesTotal\": null\n" +
                                            "                            }\n" +
                                            "                        ]\n" +
                                            "                    }\n" +
                                            "                ],\n" +
                                            "                \"totalBackoffice\": null,\n" +
                                            "                \"totalEnrutador\": null\n" +
                                            "            }\n" +
                                            "        ],\n" +
                                            "        \"totalBackoffice\": null,\n" +
                                            "        \"totalEnrutador\": null\n" +
                                            "    }\n" +
                                            "]")
                            })
            })
    })
    @GetMapping(value= "/general", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Entitat>> general(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "Data inicial de la consulta en format yyyy-MM-dd")
            final Date dataInici,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "Data final de la consulta en format yyyy-MM-dd")
            final Date dataFi,
            @RequestParam(required = false) final ServeiTipus tipus) {

        List<Entitat> entitats = new ArrayList<>();
        List<InformeGeneralEstatDto> informeGeneralFiles = explotacioService.informeGeneralEstat(
                dataInici,
                dataFi,
                getServeiTipusDto(tipus));

        EntitatsActuals entitatsActuals = new EntitatsActuals();
        informeGeneralFiles.forEach(informeGeneralFila -> {
            // Servei
            Servei servei = Servei.builder()
                    .codi(informeGeneralFila.getServeiCodi())
                    .nom(informeGeneralFila.getServeiNom())
                    .emisorNom(informeGeneralFila.getEmissorNom())
                    .emisorNif(informeGeneralFila.getEmissorCif())
                    .consultesOk(informeGeneralFila.getPeticionsCorrectes())
                    .consultesError(informeGeneralFila.getPeticionsErronies())
//                    .tipus(ServeiTipus.valueOf(informeGeneralFila.getPeticioTipus().name()))
                    .tipus(ServeiTipus.valueOf("ENRUTADOR_MULTIPLE".equals(informeGeneralFila.getPeticioTipus().name()) ? "ENRUTADOR" : informeGeneralFila.getPeticioTipus().name()))
                    .build();

            addServeiToProcediment(servei, informeGeneralFila, entitatsActuals, entitats);

        });
        return new ResponseEntity<List<Entitat>>(entitats, HttpStatus.OK);
    }
    
    private void addServeiToProcediment(
            Servei servei,
            InformeGeneralEstatDto informeGeneralFila,
            EntitatsActuals entitatsActuals,
            List<Entitat> entitats) {


        // Procediment
        if (entitatsActuals.getProcedimentActual() == null || !entitatsActuals.getProcedimentActual().getCodi().equals(informeGeneralFila.getProcedimentCodi())) {
            entitatsActuals.setProcedimentActual(Procediment.builder()
                    .codi(informeGeneralFila.getProcedimentCodi())
                    .nom(informeGeneralFila.getProcedimentNom())
                    .serveis(new ArrayList<>(List.of(servei)))
                    .build());

            addProcedimentToDepartament(informeGeneralFila, entitatsActuals, entitats);
        } else {
            entitatsActuals.getProcedimentActual().getServeis().add(servei);
        }
    }

    private void addProcedimentToDepartament(
            InformeGeneralEstatDto informeGeneralFila,
            EntitatsActuals entitatsActuals,
            List<Entitat> entitats) {

        // Departament
        if (entitatsActuals.getDepartamentActual() == null || !entitatsActuals.getDepartamentActual().getNom().equals(informeGeneralFila.getDepartament())) {
            entitatsActuals.setDepartamentActual(Departament.builder()
                    .nom(informeGeneralFila.getDepartament())
                    .procediments(new ArrayList<>(List.of(entitatsActuals.getProcedimentActual())))
                    .build());

            addDepartamentToEntitat(informeGeneralFila, entitatsActuals, entitats);

        } else {
            entitatsActuals.getDepartamentActual().getProcediments().add(entitatsActuals.getProcedimentActual());
        }
    }

    private void addDepartamentToEntitat(
            InformeGeneralEstatDto informeGeneralFila,
            EntitatsActuals entitatsActuals,
            List<Entitat> entitats) {

        // Entitat
        if (entitatsActuals.getEntitatActual() == null || !entitatsActuals.getEntitatActual().getNif().equals(informeGeneralFila.getEntitatCif())) {
            entitatsActuals.setEntitatActual(Entitat.builder()
                    .nif(informeGeneralFila.getEntitatCif())
                    .nom(informeGeneralFila.getEntitatNom())
                    .departaments(new ArrayList<>(List.of(entitatsActuals.getDepartamentActual())))
                    .build());
            entitats.add(entitatsActuals.getEntitatActual());
        } else {
            entitatsActuals.getEntitatActual().getDepartaments().add(entitatsActuals.getDepartamentActual());
        }
    }

    private ServeiTipusEnumDto getServeiTipusDto(ServeiTipus tipus) {
        if (tipus == null)
            return null;

        switch (tipus) {
            case BACKOFFICE:    return ServeiTipusEnumDto.BACKOFFICE;
            case ENRUTADOR:     return ServeiTipusEnumDto.ENRUTADOR;
            default:            return null;
        }
    }

}
