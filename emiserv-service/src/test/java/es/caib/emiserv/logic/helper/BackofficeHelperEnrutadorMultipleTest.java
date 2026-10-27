package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.helper.BackofficeHelper.RespostaBackofficeEntitat;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.exception.BackofficeException;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Estado;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Respuesta;
import es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudTransmision;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Solicitudes;
import es.caib.emiserv.logic.intf.service.ws.backoffice.TransmisionDatos;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Transmisiones;
import es.caib.emiserv.persist.entity.ServeiEntity;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tria de la resposta dels backoffices dels cedents d'un enrutador múltiple (família nombrosa).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class BackofficeHelperEnrutadorMultipleTest {

	private final BackofficeHelper backofficeHelper = new BackofficeHelper();

	private final ServeiEntity familiaNombrosa = ServeiEntity.getBuilder("SVDSCTFNWS01", "Família nombrosa", ServeiTipusEnumDto.ENRUTADOR_MULTIPLE).
			responseResolverClass("es.caib.emiserv.logic.resolver.FamiliaNombrosaResponseResolver").
			build();

	@Test
	void escullLaRespostaAmbTitolEncaraQueNoSiguiLaPrimera() throws Exception {
		RespostaBackofficeEntitat mallorca = resposta("MALLORCA", "7", null);
		RespostaBackofficeEntitat menorca = resposta("MENORCA", "0", "31/12/2030");

		RespostaBackofficeEntitat escollida = backofficeHelper.escollirRespostaEnrutadorMultiple(
				familiaNombrosa,
				Arrays.asList(mallorca, menorca));

		assertSame(menorca, escollida);
	}

	@Test
	void escullElTitolAmbLaCaducitatMesAvancada() throws Exception {
		RespostaBackofficeEntitat mallorca = resposta("MALLORCA", "0", "31/12/2030");
		RespostaBackofficeEntitat menorca = resposta("MENORCA", "0", "31/12/2035");

		assertSame(menorca, backofficeHelper.escollirRespostaEnrutadorMultiple(
				familiaNombrosa,
				Arrays.asList(mallorca, menorca)));
	}

	@Test
	void unBackofficeAmbErrorNoImpedeixEscollirLaRespostaDelAltre() throws Exception {
		RespostaBackofficeEntitat mallorca = new RespostaBackofficeEntitat("MALLORCA");
		mallorca.setException(new RuntimeException("Connection refused"));
		RespostaBackofficeEntitat menorca = resposta("MENORCA", "0", "31/12/2030");

		assertSame(menorca, backofficeHelper.escollirRespostaEnrutadorMultiple(
				familiaNombrosa,
				Arrays.asList(mallorca, menorca)));
	}

	@Test
	void totsElsBackofficesAmbErrorRetornaElPrimerError() throws Exception {
		RespostaBackofficeEntitat mallorca = new RespostaBackofficeEntitat("MALLORCA");
		mallorca.setException(new RuntimeException("Connection refused"));
		RespostaBackofficeEntitat menorca = new RespostaBackofficeEntitat("MENORCA");
		menorca.setException(new RuntimeException("Read timed out"));

		// El resolutor de família nombrosa retorna la primera resposta SOAP Fault si no n'hi ha cap altra
		assertSame(mallorca, backofficeHelper.escollirRespostaEnrutadorMultiple(
				familiaNombrosa,
				Arrays.asList(mallorca, menorca)));
	}

	@Test
	void senseResolutorEscullLaPrimeraRespostaCorrecta() throws Exception {
		ServeiEntity servei = ServeiEntity.getBuilder("SVDSCTFNWS01", "Família nombrosa", ServeiTipusEnumDto.ENRUTADOR_MULTIPLE).build();
		RespostaBackofficeEntitat mallorca = new RespostaBackofficeEntitat("MALLORCA");
		mallorca.setException(new RuntimeException("Connection refused"));
		RespostaBackofficeEntitat menorca = resposta("MENORCA", "7", null);

		assertSame(menorca, backofficeHelper.escollirRespostaEnrutadorMultiple(servei, Arrays.asList(mallorca, menorca)));
	}

	@Test
	void senseCapRespostaValidaNoEscullRes() throws Exception {
		RespostaBackofficeEntitat mallorca = resposta("MALLORCA", "7", null);

		// El codi 7 (sense dades) no és cap dels codis que considera el resolutor de família nombrosa
		assertNull(backofficeHelper.escollirRespostaEnrutadorMultiple(familiaNombrosa, Arrays.asList(mallorca)));
	}

	@Test
	void copiaDeLaPeticioAmbDadesEspecifiquesPropies() throws Exception {
		Peticion peticion = new Peticion();
		peticion.setAtributos(new Atributos());
		SolicitudTransmision solicitud = new SolicitudTransmision();
		solicitud.setDatosEspecificos(element("<datosEspecificos><Consulta>1</Consulta></datosEspecificos>"));
		Solicitudes solicitudes = new Solicitudes();
		solicitudes.setSolicitudTransmision(new ArrayList<>(Arrays.asList(solicitud)));
		peticion.setSolicitudes(solicitudes);

		Peticion copia = BackofficeHelper.copiarPeticio(peticion);

		Object datosEspecificosCopia = copia.getSolicitudes().getSolicitudTransmision().get(0).getDatosEspecificos();
		assertNotSame(solicitud.getDatosEspecificos(), datosEspecificosCopia);
		assertNotSame(((Element)solicitud.getDatosEspecificos()).getOwnerDocument(), ((Element)datosEspecificosCopia).getOwnerDocument());
		assertEquals("1", ((Element)datosEspecificosCopia).getTextContent());
	}

	@Test
	void documentDUnaRespostaAmbError() throws Exception {
		RespostaBackofficeEntitat resposta = new RespostaBackofficeEntitat("MALLORCA");
		resposta.setException(new RuntimeException("Connection refused"));

		Document document = BackofficeHelper.respostaToDocument(resposta);

		assertEquals("soapenv:Server", new XmlHelper().getTextFromFirstNode(document, "/soapenv:Envelope/soapenv:Body/soapenv:Fault/faultcode"));
	}

	@Test
	void respostaDelBackofficeSenseTransmissions() {
		Respuesta respuesta = new Respuesta();
		Atributos atributos = new Atributos();
		Estado estado = new Estado();
		estado.setCodigoEstado("0101");
		estado.setLiteralError("Error forçat pel backoffice");
		atributos.setEstado(estado);
		respuesta.setAtributos(atributos);

		BackofficeException ex = assertThrows(BackofficeException.class, () -> BackofficeHelper.comprovarTransmisionsResposta(respuesta));

		assertTrue(ex.getMessage().contains("codigoEstado=0101"), ex.getMessage());
		assertTrue(ex.getMessage().contains("Error forçat pel backoffice"), ex.getMessage());
	}

	/** Resposta d'un backoffice amb les dades específiques ja normalitzades (namespace SCSP). */
	private static RespostaBackofficeEntitat resposta(String entitat, String codiEstat, String dataCaducitat) throws Exception {
		String datosEspecificos = "<DatosEspecificos xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">" +
				"<Retorno><Estado><CodigoEstado>" + codiEstat + "</CodigoEstado></Estado>" +
				(dataCaducitat != null ? "<TituloFamiliaNumerosaRetorno><NumeroTitulo>TFN-" + entitat + "</NumeroTitulo><FechaCaducidad>" + dataCaducitat + "</FechaCaducidad></TituloFamiliaNumerosaRetorno>" : "") +
				"</Retorno></DatosEspecificos>";
		TransmisionDatos transmisionDatos = new TransmisionDatos();
		transmisionDatos.setDatosEspecificos(element(datosEspecificos));
		Transmisiones transmisiones = new Transmisiones();
		transmisiones.setTransmisionDatos(new ArrayList<>(Arrays.asList(transmisionDatos)));
		Respuesta respuesta = new Respuesta();
		respuesta.setTransmisiones(transmisiones);
		RespostaBackofficeEntitat resposta = new RespostaBackofficeEntitat(entitat);
		resposta.setRespuesta(respuesta);
		return resposta;
	}

	private static Element element(String xml) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		return dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml))).getDocumentElement();
	}

}
