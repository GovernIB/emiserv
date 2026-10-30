package es.caib.emiserv.back.controller.mock;

import es.caib.emiserv.logic.intf.service.BackofficeService;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos;
import es.caib.emiserv.logic.intf.service.ws.backoffice.ConfirmacionPeticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Respuesta;
import es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudRespuesta;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("mock")
@RestController
@RequestMapping(path = "/mock/backoffice")
public class MockBackofficeRestController {

    private final BackofficeService backofficeService;

    public MockBackofficeRestController(BackofficeService backofficeService) {
        this.backofficeService = backofficeService;
    }

    private static Atributos attrs(String codi) {
        Atributos a = new Atributos();
        a.setCodigoCertificado(codi);
        a.setIdPeticion("PET1");
        a.setTimeStamp("TS");
        return a;
    }

    @PostMapping(path = "/sincrona", consumes = MediaType.TEXT_PLAIN_VALUE, produces = {MediaType.TEXT_XML_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Respuesta sincrona(@RequestBody String codi) {
        Peticion p = new Peticion();
        p.setAtributos(attrs(codi));
        return backofficeService.peticioBackofficeSincrona(p);
    }

    @PostMapping(path = "/asincrona", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ConfirmacionPeticion asincrona(@RequestBody String codi) {
        Peticion p = new Peticion();
        p.setAtributos(attrs(codi));
        return backofficeService.peticioBackofficeAsincrona(p);
    }

    @PostMapping(path = "/solicitud", consumes = MediaType.TEXT_PLAIN_VALUE, produces = {MediaType.TEXT_XML_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Respuesta solicitud(@RequestBody String codi) {
        SolicitudRespuesta s = new SolicitudRespuesta();
        s.setAtributos(attrs(codi));
        return backofficeService.peticioBackofficeSolicitudRespuesta(s);
    }
}
