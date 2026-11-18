package es.caib.emiserv.logic.service.ws;

import es.caib.emiserv.logic.helper.ServeiFakeHelper;
import es.caib.emiserv.logic.intf.dto.ServeiFakeResultatEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.exception.BackofficeException;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos;
import es.caib.emiserv.logic.intf.service.ws.backoffice.DatosGenericos;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Respuesta;
import es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudTransmision;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Solicitudes;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Titular;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Transmision;
import es.caib.emiserv.logic.intf.service.ws.backoffice.TransmisionDatos;
import es.caib.emiserv.persist.entity.ServeiEntity;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Backoffice fake: resposta simulada de les peticions als serveis backoffice en mode fake.
 */
class FakeEmiservBackofficeTest {

	private static final String NS_DATESP = "http://intermediacion.redsara.es/scsp/esquemas/datosespecificos";

	private final ServeiFakeHelper serveiFakeHelper = new ServeiFakeHelper(
			new MockEnvironment().withProperty(ServeiFakeHelper.PROPIETAT_PERMES, "true"));

	@Test
	void retornaUnaTransmissioPerSolicitud() throws Exception {
		Peticion peticion = peticio("PBLFAKE0001",
				solicitud("000001", "11111111H"),
				solicitud("000002", "22222222J"));

		Respuesta respuesta = new FakeEmiservBackoffice(servei(ServeiFakeResultatEnumDto.OK), serveiFakeHelper).peticionSincrona(peticion);

		assertEquals("PBLFAKE0001", respuesta.getAtributos().getIdPeticion());
		assertEquals("SCDCPAJU", respuesta.getAtributos().getCodigoCertificado());
		assertEquals("0003", respuesta.getAtributos().getEstado().getCodigoEstado());
		assertEquals("2", respuesta.getAtributos().getNumElementos());
		List<TransmisionDatos> transmisions = respuesta.getTransmisiones().getTransmisionDatos();
		assertEquals(2, transmisions.size());
		assertEquals("000001", transmisions.get(0).getDatosGenericos().getTransmision().getIdSolicitud());
		assertEquals("000002", transmisions.get(1).getDatosGenericos().getTransmision().getIdSolicitud());
		for (TransmisionDatos transmision: transmisions) {
			assertTrue(transmision.getDatosEspecificos() instanceof Element);
			Element datosEspecificos = (Element)transmision.getDatosEspecificos();
			assertEquals("DatosEspecificos", datosEspecificos.getLocalName());
			assertEquals(NS_DATESP, datosEspecificos.getNamespaceURI());
		}
		// Les dades específiques són les de cada sol·licitud (el titular de cada una)
		assertTrue(((Element)transmisions.get(0).getDatosEspecificos()).getTextContent().contains("11111111H"));
		assertTrue(((Element)transmisions.get(1).getDatosEspecificos()).getTextContent().contains("22222222J"));
		// La petició no es modifica (les dades genèriques de la resposta són una còpia)
		assertNull(peticion.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTransmision().getIdTransmision());
	}

	@Test
	void errorLlancaBackofficeException() {
		Peticion peticion = peticio("PBLFAKE0002", solicitud("000001", "11111111H"));

		BackofficeException ex = assertThrows(
				BackofficeException.class,
				() -> new FakeEmiservBackoffice(servei(ServeiFakeResultatEnumDto.ERROR), serveiFakeHelper).peticionSincrona(peticion));

		assertEquals("[FAKE] Error simulat", ex.getMessage());
	}

	@Test
	void literalPortaPrefixFake() {
		Respuesta ok = new FakeEmiservBackoffice(servei(ServeiFakeResultatEnumDto.OK), serveiFakeHelper).peticionSincrona(
				peticio("PBLFAKE0003", solicitud("000001", "11111111H")));
		Respuesta senseDades = new FakeEmiservBackoffice(servei(ServeiFakeResultatEnumDto.SENSE_DADES), serveiFakeHelper).peticionSincrona(
				peticio("PBLFAKE0004", solicitud("000001", "11111111H")));

		assertEquals("[FAKE] Peticion procesada correctamente.", ok.getAtributos().getEstado().getLiteralError());
		assertEquals("[FAKE] Sin datos.", senseDades.getAtributos().getEstado().getLiteralError());
		// Padró sense dades: no hi ha cap persona
		Element datosEspecificos = (Element)senseDades.getTransmisiones().getTransmisionDatos().get(0).getDatosEspecificos();
		assertEquals(0, datosEspecificos.getElementsByTagNameNS("*", "Persona").getLength());
	}

	private static ServeiEntity servei(ServeiFakeResultatEnumDto resultat) {
		ServeiEntity servei = ServeiEntity.getBuilder("SCDCPAJU", "Padró", ServeiTipusEnumDto.BACKOFFICE).build();
		servei.updateFake(true, resultat, null);
		return servei;
	}

	static Peticion peticio(String idPeticion, SolicitudTransmision... solicituds) {
		Peticion peticion = new Peticion();
		Atributos atributos = new Atributos();
		atributos.setIdPeticion(idPeticion);
		atributos.setCodigoCertificado("SCDCPAJU");
		atributos.setNumElementos(String.valueOf(solicituds.length));
		peticion.setAtributos(atributos);
		Solicitudes solicitudes = new Solicitudes();
		solicitudes.setSolicitudTransmision(new ArrayList<SolicitudTransmision>());
		for (SolicitudTransmision solicitud: solicituds) {
			solicitudes.getSolicitudTransmision().add(solicitud);
		}
		peticion.setSolicitudes(solicitudes);
		return peticion;
	}

	static SolicitudTransmision solicitud(String idSolicitud, String document) {
		SolicitudTransmision solicitud = new SolicitudTransmision();
		DatosGenericos datosGenericos = new DatosGenericos();
		Titular titular = new Titular();
		titular.setDocumentacion(document);
		titular.setNombre("PERSONA");
		datosGenericos.setTitular(titular);
		Transmision transmision = new Transmision();
		transmision.setCodigoCertificado("SCDCPAJU");
		transmision.setIdSolicitud(idSolicitud);
		datosGenericos.setTransmision(transmision);
		solicitud.setDatosGenericos(datosGenericos);
		solicitud.setDatosEspecificos(element(
				"<DatosEspecificos xmlns=\"" + NS_DATESP + "\"><Solicitud>" +
				"<ProvinciaSolicitud>07</ProvinciaSolicitud><MunicipioSolicitud>040</MunicipioSolicitud>" +
				"</Solicitud></DatosEspecificos>"));
		return solicitud;
	}

	private static Element element(String xml) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			return dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml))).getDocumentElement();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

}
