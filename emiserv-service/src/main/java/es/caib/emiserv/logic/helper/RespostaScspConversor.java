/**
 *
 */
package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.service.ws.backoffice.Respuesta;
import es.caib.emiserv.logic.intf.service.ws.backoffice.TransmisionDatos;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.bind.JAXB;
import javax.xml.transform.dom.DOMSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Conversió de les respostes SCSP dels emisors dels cedents al format del WS de backoffice d'EMISERV:
 * els beans del backoffice tenen els mateixos camps que l'esquema SCSP, amb els noms dels elements
 * començats en minúscula i sense namespace. Les dades específiques es mantenen tal com les ha
 * retornat el cedent.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaScspConversor {

	private static final String DATOS_ESPECIFICOS = "DatosEspecificos";

	private RespostaScspConversor() {
	}

	/** Resposta SCSP (element Respuesta) en el format del backoffice (element respuesta). */
	public static Document toFormatBackoffice(Element respuesta) throws Exception {
		Document document = ClientScspHelper.nouDocument();
		document.appendChild(convertir(document, respuesta));
		return document;
	}

	/** Bean de resposta del backoffice, amb les dades específiques de cada transmissió com a Element. */
	public static Respuesta toRespuesta(Document formatBackoffice) {
		Respuesta respuesta = JAXB.unmarshal(new DOMSource(formatBackoffice), Respuesta.class);
		List<Element> datosEspecificos = new ArrayList<Element>();
		Element arrel = formatBackoffice.getDocumentElement();
		for (Element transmisiones: fills(arrel, "transmisiones")) {
			for (Element transmisionDatos: fills(transmisiones, "transmisionDatos")) {
				List<Element> de = fills(transmisionDatos, DATOS_ESPECIFICOS);
				datosEspecificos.add(de.isEmpty() ? null : de.get(0));
			}
		}
		if (respuesta.getTransmisiones() != null && respuesta.getTransmisiones().getTransmisionDatos() != null) {
			List<TransmisionDatos> transmisions = respuesta.getTransmisiones().getTransmisionDatos();
			for (int i = 0; i < transmisions.size() && i < datosEspecificos.size(); i++) {
				transmisions.get(i).setDatosEspecificos(datosEspecificos.get(i));
			}
		}
		return respuesta;
	}

	private static Node convertir(Document document, Element element) {
		if (DATOS_ESPECIFICOS.equals(element.getLocalName())) {
			return document.importNode(element, true);
		}
		String nom = element.getLocalName();
		Element convertit = document.createElement(Character.toLowerCase(nom.charAt(0)) + nom.substring(1));
		for (Node fill = element.getFirstChild(); fill != null; fill = fill.getNextSibling()) {
			if (fill instanceof Element) {
				convertit.appendChild(convertir(document, (Element)fill));
			} else if (fill.getNodeType() == Node.TEXT_NODE || fill.getNodeType() == Node.CDATA_SECTION_NODE) {
				convertit.appendChild(document.createTextNode(fill.getNodeValue()));
			}
		}
		return convertit;
	}

	private static List<Element> fills(Element pare, String nom) {
		List<Element> fills = new ArrayList<Element>();
		for (Node fill = pare.getFirstChild(); fill != null; fill = fill.getNextSibling()) {
			if (fill instanceof Element && nom.equals(fill.getLocalName() != null ? fill.getLocalName() : fill.getNodeName())) {
				fills.add((Element)fill);
			}
		}
		return fills;
	}

}
