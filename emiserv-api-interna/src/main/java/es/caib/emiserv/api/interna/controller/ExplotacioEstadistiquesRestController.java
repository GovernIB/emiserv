package es.caib.emiserv.api.interna.controller;

import es.caib.emiserv.client.comu.ConsultesOkError;
import es.caib.emiserv.client.comu.Departament;
import es.caib.emiserv.client.comu.Entitat;
import es.caib.emiserv.client.comu.EntitatsActuals;
import es.caib.emiserv.client.comu.EstatTipus;
import es.caib.emiserv.client.comu.Procediment;
import es.caib.emiserv.client.comu.Servei;
import es.caib.emiserv.client.comu.ServeiTipus;
import es.caib.emiserv.client.comu.TotalAcumulat;
import es.caib.emiserv.logic.intf.dto.CarregaDto;
import es.caib.emiserv.logic.intf.dto.EstadisticaDto;
import es.caib.emiserv.logic.intf.dto.EstadistiquesFiltreDto;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Tag(name = "Estadístiques")
@RestController
@RequestMapping("/stats")
public class ExplotacioEstadistiquesRestController {

    @Autowired
    private ExplotacioService explotacioService;

    // ESTADÍSTIQUES DE CONSULTES
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Operation(summary = "Estadístiques de consultes SCSP realitzades",
            description = "Aquest servei retorna una l’acumulat de sol·licituds agrupades per procediment, servei i si hi ha hagut errors o no.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realitzada correctament", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Entitat.class, description = "Acumular de consultes realitzades a Emiserv"),
                            examples = {
                                    @ExampleObject(value = "{\n" +
                                            "    \"nom\": \"Límit Tecnologies\",\n" +
                                            "    \"nif\": \"12345678Z\",\n" +
                                            "    \"departaments\": [\n" +
                                            "        {\n" +
                                            "            \"codi\": null,\n" +
                                            "            \"nom\": \"Programari\",\n" +
                                            "            \"procediments\": [\n" +
                                            "                {\n" +
                                            "                    \"codi\": \"TEST\",\n" +
                                            "                    \"nom\": \"Procediment de test\",\n" +
                                            "                    \"actiu\": false,\n" +
                                            "                    \"consultesBackoffice\": {\n" +
                                            "                        \"ok\": 22,\n" +
                                            "                        \"error\": 61\n" +
                                            "                    },\n" +
                                            "                    \"consultesEnrutador\": {\n" +
                                            "                        \"ok\": 0,\n" +
                                            "                        \"error\": 6\n" +
                                            "                    },\n" +
                                            "                    \"consultesTotal\": {\n" +
                                            "                        \"ok\": 22,\n" +
                                            "                        \"error\": 67\n" +
                                            "                    },\n" +
                                            "                    \"totalBackoffice\": null,\n" +
                                            "                    \"totalEnrutador\": null,\n" +
                                            "                    \"serveis\": [\n" +
                                            "                        {\n" +
                                            "                            \"codi\": \"SVDCCAACPASWS01\",\n" +
                                            "                            \"nom\": \"Estar al corriente de obligaciones tributarias para solicitud de subvenciones y ayudas de la CCAA\",\n" +
                                            "                            \"emisorNom\": null,\n" +
                                            "                            \"emisorNif\": \"12345678Z\",\n" +
                                            "                            \"consultesOk\": 22,\n" +
                                            "                            \"consultesError\": 56,\n" +
                                            "                            \"tipus\": \"BACKOFFICE\",\n" +
                                            "                            \"consultesTotal\": null\n" +
                                            "                        },\n" +
                                            "                        {\n" +
                                            "                            \"codi\": \"SVDSCDDWS01\",\n" +
                                            "                            \"nom\": \"Servei de consulta de dades de discapacitat\",\n" +
                                            "                            \"emisorNom\": null,\n" +
                                            "                            \"emisorNif\": \"12345678Z\",\n" +
                                            "                            \"consultesOk\": 0,\n" +
                                            "                            \"consultesError\": 5,\n" +
                                            "                            \"tipus\": \"BACKOFFICE\",\n" +
                                            "                            \"consultesTotal\": null\n" +
                                            "                        },\n" +
                                            "                        {\n" +
                                            "                            \"codi\": \"TESTENRUTA\",\n" +
                                            "                            \"nom\": \"Test Enrutador\",\n" +
                                            "                            \"emisorNom\": null,\n" +
                                            "                            \"emisorNif\": \"12345678Z\",\n" +
                                            "                            \"consultesOk\": 0,\n" +
                                            "                            \"consultesError\": 6,\n" +
                                            "                            \"tipus\": \"ENRUTADOR\",\n" +
                                            "                            \"consultesTotal\": null\n" +
                                            "                        }\n" +
                                            "                    ]\n" +
                                            "                }\n" +
                                            "            ],\n" +
                                            "            \"consultesBackoffice\": {\n" +
                                            "                \"ok\": 22,\n" +
                                            "                \"error\": 61\n" +
                                            "            },\n" +
                                            "            \"consultesEnrutador\": {\n" +
                                            "                \"ok\": 0,\n" +
                                            "                \"error\": 6\n" +
                                            "            },\n" +
                                            "            \"consultesTotal\": {\n" +
                                            "                \"ok\": 22,\n" +
                                            "                \"error\": 67\n" +
                                            "            },\n" +
                                            "            \"totalBackoffice\": null,\n" +
                                            "            \"totalEnrutador\": null\n" +
                                            "        }\n" +
                                            "    ],\n" +
                                            "    \"consultesBackoffice\": {\n" +
                                            "        \"ok\": 22,\n" +
                                            "        \"error\": 61\n" +
                                            "    },\n" +
                                            "    \"consultesEnrutador\": {\n" +
                                            "        \"ok\": 0,\n" +
                                            "        \"error\": 6\n" +
                                            "    },\n" +
                                            "    \"consultesTotal\": {\n" +
                                            "        \"ok\": 22,\n" +
                                            "        \"error\": 67\n" +
                                            "    },\n" +
                                            "    \"totalBackoffice\": null,\n" +
                                            "    \"totalEnrutador\": null\n" +
                                            "}")
                            })
            })
    })
    @GetMapping(value= "/consultes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Entitat> consultes(
            HttpServletRequest request,
            @RequestParam final String entitatNif,
            @RequestParam(required = false) @Parameter(description = "Codi del procediment") final String procedimentCodi,
            @RequestParam(required = false) @Parameter(description = "Codi del servei") final String serveiCodi,
            @RequestParam(required = false) @Parameter(description = "Estat de la consulta") final EstatTipus estat,
            @RequestParam(required = false) @Parameter(description = "Data inicial de la consulta en format yyyy-MM-dd") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final Date dataInici,
            @RequestParam(required = false) @Parameter(description = "Data final de la consulta en format yyyy-MM-dd") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final Date dataFi,
            @RequestParam(required = false) @Parameter(description = "Tipus del servei") final ServeiTipus tipus) {

        List<EstadisticaDto> estadistiques = explotacioService.findEstadistiquesByFiltre(
                EstadistiquesFiltreDto.builder()
                        .entitatNif(entitatNif)
                        .procedimentCodi(procedimentCodi)
                        .serveiCodi(serveiCodi)
                        .estat(estat)
                        .dataInici(dataInici)
                        .dataFi(dataFi)
                        .build());

        if (estadistiques == null || estadistiques.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }

        EntitatsActuals entitatsActuals = new EntitatsActuals();
        estadistiques.forEach(e -> {
            Servei servei = Servei.builder()
                    .codi(e.getServeiCodi())
                    .nom(e.getServeiNom())
                    .emisorNif(e.getEmisor())
                    .tipus(ServeiTipus.valueOf("ENRUTADOR_MULTIPLE".equals(e.getServeiTipus().name()) ? "ENRUTADOR" : e.getServeiTipus().name()))
                    .consultesOk(e.getSumatoriNumOk())
                    .consultesError(e.getSumatoriNumError())
                    .build();

            addServeiToProcedimentEstadistica(servei, e, entitatsActuals);
        });

        return new ResponseEntity<>(entitatsActuals.getEntitatActual(), HttpStatus.OK);

    }

    private void addServeiToProcedimentEstadistica(
            Servei servei,
            EstadisticaDto estadistica,
            EntitatsActuals entitatsActuals) {


        // Procediment
        if (entitatsActuals.getProcedimentActual() == null || !entitatsActuals.getProcedimentActual().getCodi().equals(estadistica.getProcedimentCodi())) {
            entitatsActuals.setProcedimentActual(Procediment.builder()
                    .codi(estadistica.getProcedimentCodi())
                    .nom(estadistica.getProcedimentNom())
                    .serveis(new ArrayList<>(List.of(servei)))
                    .consultesBackoffice(ConsultesOkError.builder().build())
                    .consultesEnrutador(ConsultesOkError.builder().build())
                    .consultesTotal(ConsultesOkError.builder().build())
                    .build());

            addProcedimentToDepartamentEstadistica(estadistica, entitatsActuals);
        } else {
            entitatsActuals.getProcedimentActual().getServeis().add(servei);
        }
        updateProcedimentCount(entitatsActuals, servei);
    }

    private void addProcedimentToDepartamentEstadistica(
            EstadisticaDto estadistica,
            EntitatsActuals entitatsActuals) {

        // Departament
        if (entitatsActuals.getDepartamentActual() == null || !entitatsActuals.getDepartamentActual().getNom().equals(estadistica.getDepartamentNom())) {
            entitatsActuals.setDepartamentActual(Departament.builder()
                    .nom(estadistica.getDepartamentNom())
                    .procediments(new ArrayList<>(List.of(entitatsActuals.getProcedimentActual())))
                    .consultesBackoffice(ConsultesOkError.builder().build())
                    .consultesEnrutador(ConsultesOkError.builder().build())
                    .consultesTotal(ConsultesOkError.builder().build())
                    .build());

            addDepartamentToEntitatEstadistica(estadistica, entitatsActuals);

        } else {
            entitatsActuals.getDepartamentActual().getProcediments().add(entitatsActuals.getProcedimentActual());
        }
    }

    private void addDepartamentToEntitatEstadistica(
            EstadisticaDto estadistica,
            EntitatsActuals entitatsActuals) {

        // Entitat
        if (entitatsActuals.getEntitatActual() == null || !entitatsActuals.getEntitatActual().getNif().equals(estadistica.getEntitatCif())) {
            entitatsActuals.setEntitatActual(Entitat.builder()
                    .nif(estadistica.getEntitatCif())
                    .nom(estadistica.getEntitatNom())
                    .departaments(new ArrayList<>(List.of(entitatsActuals.getDepartamentActual())))
                    .consultesBackoffice(ConsultesOkError.builder().build())
                    .consultesEnrutador(ConsultesOkError.builder().build())
                    .consultesTotal(ConsultesOkError.builder().build())
                    .build());
        } else {
            entitatsActuals.getEntitatActual().getDepartaments().add(entitatsActuals.getDepartamentActual());
        }
    }

    private void updateProcedimentCount(EntitatsActuals entitatsActuals, Servei servei) {
        if (ServeiTipus.BACKOFFICE.equals(servei.getTipus())) {
            entitatsActuals.getProcedimentActual().getConsultesBackoffice().addOk(servei.getConsultesOk());
            entitatsActuals.getProcedimentActual().getConsultesBackoffice().addError(servei.getConsultesError());
        } else if (ServeiTipus.ENRUTADOR.equals(servei.getTipus())) {
            entitatsActuals.getProcedimentActual().getConsultesEnrutador().addOk(servei.getConsultesOk());
            entitatsActuals.getProcedimentActual().getConsultesEnrutador().addError(servei.getConsultesError());
        }
        entitatsActuals.getProcedimentActual().getConsultesTotal().addOk(servei.getConsultesOk());
        entitatsActuals.getProcedimentActual().getConsultesTotal().addError(servei.getConsultesError());
        updateDepartamentCount(entitatsActuals, servei);
    }

    private void updateDepartamentCount(EntitatsActuals entitatsActuals, Servei servei) {
        if (ServeiTipus.BACKOFFICE.equals(servei.getTipus())) {
            entitatsActuals.getDepartamentActual().getConsultesBackoffice().addOk(servei.getConsultesOk());
            entitatsActuals.getDepartamentActual().getConsultesBackoffice().addError(servei.getConsultesError());
        } else if (ServeiTipus.ENRUTADOR.equals(servei.getTipus())) {
            entitatsActuals.getDepartamentActual().getConsultesEnrutador().addOk(servei.getConsultesOk());
            entitatsActuals.getDepartamentActual().getConsultesEnrutador().addError(servei.getConsultesError());
        }
        entitatsActuals.getDepartamentActual().getConsultesTotal().addOk(servei.getConsultesOk());
        entitatsActuals.getDepartamentActual().getConsultesTotal().addError(servei.getConsultesError());
        updateEntitatCount(entitatsActuals, servei);
    }

    private void updateEntitatCount(EntitatsActuals entitatsActuals, Servei servei) {
        if (ServeiTipus.BACKOFFICE.equals(servei.getTipus())) {
            entitatsActuals.getEntitatActual().getConsultesBackoffice().addOk(servei.getConsultesOk());
            entitatsActuals.getEntitatActual().getConsultesBackoffice().addError(servei.getConsultesError());
        } else if (ServeiTipus.ENRUTADOR.equals(servei.getTipus())) {
            entitatsActuals.getEntitatActual().getConsultesEnrutador().addOk(servei.getConsultesOk());
            entitatsActuals.getEntitatActual().getConsultesEnrutador().addError(servei.getConsultesError());
        }
        entitatsActuals.getEntitatActual().getConsultesTotal().addOk(servei.getConsultesOk());
        entitatsActuals.getEntitatActual().getConsultesTotal().addError(servei.getConsultesError());
    }


    // ESTADÍSTIQUES DE CÀRREGA
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Operation(summary = "Estadístiques de càrrega",
            description = "Aquest servei retorna un l’acumulat de sol·licituds agrupades per procediment \n" +
                    "i servei distingint entre les peticions realitzades via interfície web o via \n" +
                    "recobriment i també si hi ha hagut errors o no.")
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
                                            "                        \"nom\": \"Procediment de proves\",\n" +
                                            "                        \"actiu\": false,\n" +
                                            "                        \"consultesBackoffice\": null,\n" +
                                            "                        \"consultesEnrutador\": null,\n" +
                                            "                        \"consultesTotal\": null,\n" +
                                            "                        \"totalBackoffice\": {\n" +
                                            "                            \"any\": 40,\n" +
                                            "                            \"mes\": 1,\n" +
                                            "                            \"dia\": 0,\n" +
                                            "                            \"hora\": 0,\n" +
                                            "                            \"minut\": 0\n" +
                                            "                        },\n" +
                                            "                        \"totalEnrutador\": {\n" +
                                            "                            \"any\": 0,\n" +
                                            "                            \"mes\": 0,\n" +
                                            "                            \"dia\": 0,\n" +
                                            "                            \"hora\": 0,\n" +
                                            "                            \"minut\": 0\n" +
                                            "                        },\n" +
                                            "                        \"serveis\": [\n" +
                                            "                            {\n" +
                                            "                                \"codi\": \"SVDSCDDWS01\",\n" +
                                            "                                \"nom\": \"Servei de consulta de dades de discapacitat\",\n" +
                                            "                                \"emisorNom\": null,\n" +
                                            "                                \"emisorNif\": \"12345678Z\",\n" +
                                            "                                \"consultesOk\": null,\n" +
                                            "                                \"consultesError\": null,\n" +
                                            "                                \"tipus\": \"BACKOFFICE\",\n" +
                                            "                                \"consultesTotal\": {\n" +
                                            "                                    \"any\": 40,\n" +
                                            "                                    \"mes\": 1,\n" +
                                            "                                    \"dia\": 0,\n" +
                                            "                                    \"hora\": 0,\n" +
                                            "                                    \"minut\": 0\n" +
                                            "                                }\n" +
                                            "                            }\n" +
                                            "                        ]\n" +
                                            "                    }\n" +
                                            "                ],\n" +
                                            "                \"consultesBackoffice\": null,\n" +
                                            "                \"consultesEnrutador\": null,\n" +
                                            "                \"consultesTotal\": null,\n" +
                                            "                \"totalBackoffice\": {\n" +
                                            "                    \"any\": 40,\n" +
                                            "                    \"mes\": 1,\n" +
                                            "                    \"dia\": 0,\n" +
                                            "                    \"hora\": 0,\n" +
                                            "                    \"minut\": 0\n" +
                                            "                },\n" +
                                            "                \"totalEnrutador\": {\n" +
                                            "                    \"any\": 0,\n" +
                                            "                    \"mes\": 0,\n" +
                                            "                    \"dia\": 0,\n" +
                                            "                    \"hora\": 0,\n" +
                                            "                    \"minut\": 0\n" +
                                            "                }\n" +
                                            "            }\n" +
                                            "        ],\n" +
                                            "        \"consultesBackoffice\": null,\n" +
                                            "        \"consultesEnrutador\": null,\n" +
                                            "        \"consultesTotal\": null,\n" +
                                            "        \"totalBackoffice\": {\n" +
                                            "            \"any\": 40,\n" +
                                            "            \"mes\": 1,\n" +
                                            "            \"dia\": 0,\n" +
                                            "            \"hora\": 0,\n" +
                                            "            \"minut\": 0\n" +
                                            "        },\n" +
                                            "        \"totalEnrutador\": {\n" +
                                            "            \"any\": 0,\n" +
                                            "            \"mes\": 0,\n" +
                                            "            \"dia\": 0,\n" +
                                            "            \"hora\": 0,\n" +
                                            "            \"minut\": 0\n" +
                                            "        }\n" +
                                            "    }\n" +
                                            "]")
                            })
            })
    })
    @GetMapping(value= "/carrega", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Entitat>> carrega(
            HttpServletRequest request) {

        List<Entitat> entitats = new ArrayList<>();

        List<CarregaDto> carregues = explotacioService.findEstadistiquesCarrega();

        EntitatsActuals entitatsActuals = new EntitatsActuals();
        carregues.forEach(c -> {
            // Servei
            Servei servei = Servei.builder()
                    .codi(c.getServeiCodi())
                    .nom(c.getServeiNom())
                    .emisorNif(c.getEmisor())
                    .tipus(ServeiTipus.valueOf("ENRUTADOR_MULTIPLE".equals(c.getServeiTipus().name()) ? "ENRUTADOR" : c.getServeiTipus().name()))
                    .consultesTotal(TotalAcumulat.builder()
                            .any(c.getDetailedCount().getAny())
                            .mes(c.getDetailedCount().getMes())
                            .dia(c.getDetailedCount().getDia())
                            .hora(c.getDetailedCount().getHora())
                            .minut(c.getDetailedCount().getMinut())
                            .build())
                    .build();

            addServeiToProcedimentCarrega(servei, c, entitatsActuals, entitats);
        });

        return new ResponseEntity<>(entitats, HttpStatus.OK);

    }

    private void addServeiToProcedimentCarrega(
            Servei servei,
            CarregaDto carrega,
            EntitatsActuals entitatsActuals,
            List<Entitat> entitats) {


        // Procediment
        if (entitatsActuals.getProcedimentActual() == null || !entitatsActuals.getProcedimentActual().getCodi().equals(carrega.getProcedimentCodi())) {
            entitatsActuals.setProcedimentActual(Procediment.builder()
                    .codi(carrega.getProcedimentCodi())
                    .nom(carrega.getProcedimentNom())
                    .serveis(new ArrayList<>(List.of(servei)))
                    .totalBackoffice(TotalAcumulat.builder().build())
                    .totalEnrutador(TotalAcumulat.builder().build())
                    .build());

            addProcedimentToDepartamentCarrega(carrega, entitatsActuals, entitats);
        } else {
            entitatsActuals.getProcedimentActual().getServeis().add(servei);
        }
        updateProcedimentAcumulat(entitatsActuals, servei);
    }

    private void addProcedimentToDepartamentCarrega(
            CarregaDto carrega,
            EntitatsActuals entitatsActuals,
            List<Entitat> entitats) {

        // Departament
        if (entitatsActuals.getDepartamentActual() == null || !entitatsActuals.getDepartamentActual().getNom().equals(carrega.getDepartamentNom())) {
            entitatsActuals.setDepartamentActual(Departament.builder()
                    .nom(carrega.getDepartamentNom())
                    .procediments(new ArrayList<>(List.of(entitatsActuals.getProcedimentActual())))
                    .totalBackoffice(TotalAcumulat.builder().build())
                    .totalEnrutador(TotalAcumulat.builder().build())
                    .build());

            addDepartamentToEntitatCarrega(carrega, entitatsActuals, entitats);

        } else {
            entitatsActuals.getDepartamentActual().getProcediments().add(entitatsActuals.getProcedimentActual());
        }
    }

    private void addDepartamentToEntitatCarrega(
            CarregaDto carrega,
            EntitatsActuals entitatsActuals,
            List<Entitat> entitats) {

        // Entitat
        if (entitatsActuals.getEntitatActual() == null || !entitatsActuals.getEntitatActual().getNif().equals(carrega.getEntitatCif())) {
            entitatsActuals.setEntitatActual(Entitat.builder()
                    .nif(carrega.getEntitatCif())
                    .nom(carrega.getEntitatNom())
                    .departaments(new ArrayList<>(List.of(entitatsActuals.getDepartamentActual())))
                    .totalBackoffice(TotalAcumulat.builder().build())
                    .totalEnrutador(TotalAcumulat.builder().build())
                    .build());
            entitats.add(entitatsActuals.getEntitatActual());
        } else {
            entitatsActuals.getEntitatActual().getDepartaments().add(entitatsActuals.getDepartamentActual());
        }
    }

    private void updateProcedimentAcumulat(EntitatsActuals entitatsActuals, Servei servei) {
        if (ServeiTipus.BACKOFFICE.equals(servei.getTipus()))
            entitatsActuals.getProcedimentActual().getTotalBackoffice().incrementarAcumulat(servei.getConsultesTotal());
        else if (ServeiTipus.ENRUTADOR.equals(servei.getTipus()))
            entitatsActuals.getProcedimentActual().getTotalEnrutador().incrementarAcumulat(servei.getConsultesTotal());

        updateDepartamentAcumulat(entitatsActuals, servei);
    }

    private void updateDepartamentAcumulat(EntitatsActuals entitatsActuals, Servei servei) {
        if (ServeiTipus.BACKOFFICE.equals(servei.getTipus()))
            entitatsActuals.getDepartamentActual().getTotalBackoffice().incrementarAcumulat(servei.getConsultesTotal());
        else if (ServeiTipus.ENRUTADOR.equals(servei.getTipus()))
            entitatsActuals.getDepartamentActual().getTotalEnrutador().incrementarAcumulat(servei.getConsultesTotal());

        updateEntitatAcumulat(entitatsActuals, servei);
    }

    private void updateEntitatAcumulat(EntitatsActuals entitatsActuals, Servei servei) {
        if (ServeiTipus.BACKOFFICE.equals(servei.getTipus()))
            entitatsActuals.getEntitatActual().getTotalBackoffice().incrementarAcumulat(servei.getConsultesTotal());
        else if (ServeiTipus.ENRUTADOR.equals(servei.getTipus()))
            entitatsActuals.getEntitatActual().getTotalEnrutador().incrementarAcumulat(servei.getConsultesTotal());
    }

}
