package es.caib.emiserv.logic.mock;

import es.caib.emiserv.logic.helper.BackofficeHelper;
import es.caib.emiserv.logic.intf.service.ws.backoffice.ConfirmacionPeticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Respuesta;
import es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudRespuesta;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Primary
@Profile("mock")
@Component
public class MockBackofficeHelper extends BackofficeHelper {

    private final BackofficeHelper real = new BackofficeHelper();

    @Override
    public RespuestaAmbException peticioSincrona(Peticion peticion) {
        String cert = peticion != null && peticion.getAtributos() != null ? peticion.getAtributos().getCodigoCertificado() : null;
        if (cert != null && cert.endsWith("_ERR")) {
            return real.new RespuestaAmbException(null, new RuntimeException("Forçat per mock"));
        }
        return real.new RespuestaAmbException(new Respuesta(), null);
    }

    @Override
    public ConfirmacionPeticionAmbException peticioAsincrona(Peticion peticion) {
        String cert = peticion != null && peticion.getAtributos() != null ? peticion.getAtributos().getCodigoCertificado() : null;
        if (cert != null && cert.endsWith("_ERR")) {
            return real.new ConfirmacionPeticionAmbException(null, new RuntimeException("Forçat per mock"));
        }
        return real.new ConfirmacionPeticionAmbException(new ConfirmacionPeticion(), null);
    }

    @Override
    public RespuestaAmbException solicitudResposta(SolicitudRespuesta solicitudRespuesta) {
        String cert = solicitudRespuesta != null && solicitudRespuesta.getAtributos() != null ? solicitudRespuesta.getAtributos().getCodigoCertificado() : null;
        if (cert != null && cert.endsWith("_ERR")) {
            return real.new RespuestaAmbException(null, new RuntimeException("Forçat per mock"));
        }
        return real.new RespuestaAmbException(new Respuesta(), null);
    }
}
