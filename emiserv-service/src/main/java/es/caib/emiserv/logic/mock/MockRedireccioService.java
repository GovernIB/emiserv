package es.caib.emiserv.logic.mock;

import es.caib.emiserv.logic.intf.dto.RedireccioProcessarResultatDto;
import es.caib.emiserv.logic.intf.service.RedireccioService;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Primary
@Profile("mock")
@Service
public class MockRedireccioService implements RedireccioService {

    @Override
    public RedireccioProcessarResultatDto processarPeticio(byte[] xml) {
        String xmlStr = xml != null ? new String(xml, java.nio.charset.StandardCharsets.UTF_8) : "";
        String xmlLower = xmlStr.toLowerCase();
        
        // Decidir OK/KO segons contingut de l'XML (molt simple): si conté <error>, forçar KO
        // Fem una cerca més robusta
        boolean error = xmlLower.contains("<error>") || xmlLower.contains("<error/>");
        
        // Per cobrir simple vs múltiple, si conté <multiple>, retornem ENRUTADOR_MULTIPLE
        boolean multiple = xmlLower.contains("<multiple>") || xmlLower.contains("<multiple/>");
        
        String servei = xmlLower.contains("cert") ? "CERT1" : "MOCK_CERT";
        
        if (error) {
            RedireccioProcessarResultatDto resErr = new RedireccioProcessarResultatDto(
                    "0502",
                    "Error forçat per mock (trobat <error>)",
                    RedireccioProcessarResultatDto.FaultCodeEnum.SERVER,
                    "0502",
                    "Error de processament mock");
            resErr.setAtributCodigoCertificado(servei);
            resErr.setAtributPeticioId("PET_ERR");
            return resErr;
        }

        if (!multiple) {
            return new RedireccioProcessarResultatDto(
                    "http://localhost:8080/emiservback/mock/desti/ok",
                    1,
                    "PET1",
                    "TS",
                    servei);
        }

        Map<String, String> destins = new HashMap<>();
        destins.put("E1", "http://localhost:8080/emiservback/mock/desti/ok");
        destins.put("E2", "http://localhost:8080/emiservback/mock/desti/ok");
        return new RedireccioProcessarResultatDto(
                destins,
                1,
                "PET1",
                "TS",
                servei);
    }

    // Mètodes utilitzats per ScspRoutingController en el flux d’enrutador múltiple i errors
    @Override
    public String escollirResposta(RedireccioProcessarResultatDto resultat, Map<String, byte[]> xmlsPerEscollir) {
        // Tria el primer destí amb resposta (o E1 per defecte)
        return resultat.getUrlDestins().keySet().stream().findFirst().orElse("E1");
    }

    @Override
    public void saveRespostesPerEntitat(Map<String, String> respostesPerEntitat, String peticioId, String serveiCodi) {
        // No-op en mock
    }

    @Override
    public void processarResposta(String peticioId, String serveiCodi, byte[] resposta, String entitatCodiRedireccio) {
        // No-op en mock
    }

    @Override
    public String generarSoapFault(RedireccioProcessarResultatDto resultat) {
        return "<?xml version='1.0' encoding='UTF-8'?>\n" +
               "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'>\n" +
               "  <soapenv:Body>\n" +
               "    <soapenv:Fault>\n" +
               "      <faultcode>soapenv:" + (resultat.getFaultCode() != null ? resultat.getFaultCode().getValue() : "Server") + "</faultcode>\n" +
               "      <faultstring>" + (resultat.getFaultErrorString() != null ? resultat.getFaultErrorString() : resultat.getErrorDescripcio()) + "</faultstring>\n" +
               "      <detail>\n" +
               "        <ErrorCodi>" + resultat.getErrorCodi() + "</ErrorCodi>\n" +
               "      </detail>\n" +
               "    </soapenv:Fault>\n" +
               "  </soapenv:Body>\n" +
               "</soapenv:Envelope>";
    }

    // Altres mètodes de la interfície no utilitzats per aquests tests poden quedar sense implementar amb UnsupportedOperation
    @Override public java.util.List<es.caib.emiserv.logic.intf.dto.ProcedimentDto> procedimentFindAll() { return java.util.List.of(); }
    @Override public java.util.List<es.caib.emiserv.logic.intf.dto.ServeiDto> serveiFindAll() { return java.util.List.of(); }
    @Override public es.caib.emiserv.logic.intf.dto.PaginaDto<es.caib.emiserv.logic.intf.dto.AuditoriaPeticioDto> peticioFindByFiltrePaginat(es.caib.emiserv.logic.intf.dto.AuditoriaFiltreDto filtre, es.caib.emiserv.logic.intf.dto.PaginacioParamsDto paginacioParams) { return null; }
    @Override public es.caib.emiserv.logic.intf.dto.AuditoriaPeticioDto peticioFindById(Long idPeticio) { return null; }
    @Override public java.util.List<es.caib.emiserv.logic.intf.dto.AuditoriaSolicitudDto> solicitudFindByPeticioId(Long peticioId) { return java.util.List.of(); }
    @Override public String peticioXmlPeticio(Long peticioId) { return null; }
    @Override public String peticioXmlResposta(Long peticioId) { return null; }
    @Override public java.util.List<es.caib.emiserv.logic.intf.dto.RedireccioRespostaDto> peticioXmlRespostes(Long peticioId) { return java.util.List.of(); }
}
