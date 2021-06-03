/**
 * 
 */
package es.caib.emiserv.logic.resolver;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * EntitatResolver per al servei d'empadronament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EmpadronamentEntitatResolver implements EntitatResolver {

	@Override
	public String resolve(Element firstElement) throws EntitatResolverException {
		try {
			String provincia = getTextFromFirstNode(
					firstElement,
					"//scsp:Peticion/scsp:Solicitudes/scsp:SolicitudTransmision/datosespecificos:DatosEspecificos/datosespecificos:Solicitud/datosespecificos:ProvinciaSolicitud");
			String municipio = getTextFromFirstNode(
					firstElement,
					"//scsp:Peticion/scsp:Solicitudes/scsp:SolicitudTransmision/datosespecificos:DatosEspecificos/datosespecificos:Solicitud/datosespecificos:MunicipioSolicitud");
			return provincia + municipio;
		} catch (Exception ex) {
			throw new EntitatResolverException(
					"No s'ha pogut resoldre l'entitat",
					ex);
		}
	}

	private String getTextFromFirstNode(
			Element element,
			String xpath) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(
				new ScspNamespaceResolver());
		NodeList nodes = (NodeList)xPath.evaluate(
				xpath,
				element,
				XPathConstants.NODESET);
		if (nodes.getLength() == 0) {
			throw new NoSuchElementException(xpath);
		} else {
			Node node = nodes.item(0);
			return node.getTextContent();
		}
	}

	static class ScspNamespaceResolver implements NamespaceContext {
		public String getNamespaceURI(String prefix) {
			if (prefix == null) {
				throw new NullPointerException("El prefix no pot ser null");
			} else if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
				return "http://intermediacion.redsara.es/scsp/esquemas/V3/peticion";
			} else if (prefix.equals("scsp")) {
				return "http://intermediacion.redsara.es/scsp/esquemas/V3/peticion";
			} else if (prefix.equals("datosespecificos")) {
				return "http://intermediacion.redsara.es/scsp/esquemas/datosespecificos";
			} else {
				return XMLConstants.NULL_NS_URI;
			}
		}
		public String getPrefix(String namespaceURI) {
			return null;
		}
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Iterator getPrefixes(String namespaceURI) {
			return null;
		}
	}

}
