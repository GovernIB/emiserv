/**
 * 
 */
package es.caib.emiserv.core.backoffice;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.caib.emiserv.core.helper.XmlHelper;

/**
 * Classe de prova per a obtenir una SolicitudTransmision determinada
 * d'una petició SCSP.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ObtenirSolicitudTransmisionPeticioScsp {

	private XmlHelper xmlHelper = new XmlHelper();



	public static void main(String[] args) {
		try {
			new ObtenirSolicitudTransmisionPeticioScsp().test();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	
	private void test() throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
		String xmlPeticion = 
				IOUtils.toString(new ClassPathResource("/es/caib/emiserv/core/backoffice/ObtenirSolicitudTransmisionPeticioScsp.xml").getInputStream());
		Element element = getElementSolicitudTransmision(
				xmlPeticion,
				"PBL00000000000000000000693");
		if (element == null)
			System.out.println("No trobat! :'(");
		else
			System.out.println("Trobat! :-D");
	}

	private Element getElementSolicitudTransmision(
			String xml,
			String solicitudId) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		Document document = xmlHelper.bytesToDocument(xml.getBytes());
		boolean isV2 = xmlHelper.countNodes(
				document,
				"//peticion2:Peticion") > 0;
		boolean isV3 = xmlHelper.countNodes(
				document,
				"//peticion3:Peticion") > 0;
		if (isV2 || isV3) {
			String nsprefix = (isV2) ? "peticion2" : "peticion3";
			NodeList solicitudTransmisionNodes = xmlHelper.getNodeList(
					document,
					"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision");
			for (int i = 0; i < solicitudTransmisionNodes.getLength(); i++) {
				Element solicitudTransmision = (Element)solicitudTransmisionNodes.item(i);
				String idSolicitud = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":DatosGenericos/" + nsprefix + ":Transmision/" + nsprefix + ":IdSolicitud");
				if (solicitudId.equals(idSolicitud)) {
					return solicitudTransmision;
				}
			}
		}
		return null;
	}

}
