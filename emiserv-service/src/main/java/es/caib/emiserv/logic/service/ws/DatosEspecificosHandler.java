/**
 * 
 */
package es.caib.emiserv.logic.service.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * SOAP Handler per a extreure els datosEspecificos de la resposta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DatosEspecificosHandler implements SOAPHandler<SOAPMessageContext> {

	private Map<String, Element> datosEspecificosMap;

	public Set<QName> getHeaders() {
		return null;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		try {
			Boolean outboundProperty = (Boolean)smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outboundProperty.booleanValue()) {
				processarDatosEspecificosPeticio(smc);
			} else {
				processarDatosEspecificosResposta(smc);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error al extreure els datosEspecificos de la resposta",
					ex);
		}
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		return true;
	}

	public void close(MessageContext messageContext) {
	}

	public Map<String, Element> getDatosEspecificosMap() {
		return datosEspecificosMap;
	}
	public Element getDatosEspecificosRespostaComElement(
			String idSolicitud) {
		if (datosEspecificosMap == null)
			return null;
		else 
			return datosEspecificosMap.get(idSolicitud);
	}
	public String getDatosEspecificosRespostaComString(
			String idSolicitud) throws TransformerFactoryConfigurationError, TransformerException {
		return nodeToString(
				getDatosEspecificosRespostaComElement(idSolicitud));
	}

	private void processarDatosEspecificosPeticio(
			SOAPMessageContext smc) throws TransformerConfigurationException, TransformerException, SOAPException, IOException, XPathExpressionException {
	}

	private void processarDatosEspecificosResposta(
			SOAPMessageContext smc) throws TransformerConfigurationException, TransformerException, SOAPException, IOException, XPathExpressionException, ParserConfigurationException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodesTransmisionDatos = (NodeList)xPath.evaluate(
				"//transmisionDatos",
				smc.getMessage().getSOAPBody().getParentElement(),
				XPathConstants.NODESET);
		for (int i = 0; i < nodesTransmisionDatos.getLength(); i++) {
			Element transmisionDatos = (Element)nodesTransmisionDatos.item(i);
			NodeList nodesIdSolicitud = (NodeList)xPath.evaluate(
					"//datosGenericos/transmision/idSolicitud",
					transmisionDatos,
					XPathConstants.NODESET);
			if (nodesIdSolicitud.getLength() > 0) {
				String idSolicitud = nodesIdSolicitud.item(0).getTextContent();
				SOAPElement datosEspecificosElement = null;
				SOAPElement idElement = null;
				NodeList nl = transmisionDatos.getChildNodes();
				for (int j = 0; j < nl.getLength(); j++) {
					Node n = nl.item(j);
					if (n.getNodeType() == Node.ELEMENT_NODE && "datosEspecificos".equalsIgnoreCase(n.getLocalName())) {
						datosEspecificosElement = (SOAPElement)n;
					} else if (n.getNodeType() == Node.ELEMENT_NODE && "id".equalsIgnoreCase(n.getLocalName())) {
						idElement = (SOAPElement)n;
					}
				}
				if (datosEspecificosElement != null) {
					SOAPElement datosEspecificosPare = (SOAPElement)(datosEspecificosElement.getParentNode());
					SOAPElement datosEspecificosNou = datosEspecificosPare.addChildElement("datosEspecificos");
					NodeList nlde = datosEspecificosElement.getChildNodes();
					for (int k = 0; k < nlde.getLength(); k++) {
						Node nde = nlde.item(k);
						if (nde != null) {
							datosEspecificosNou.appendChild(nde.cloneNode(true));
//							datosEspecificosElement.removeChild(nde);
						}
					}
					afegirDatosEspecificos(
							idSolicitud,
							datosEspecificosNou);
					datosEspecificosElement.getParentNode().removeChild(datosEspecificosElement);
				}
				if (idElement != null) {
					SOAPElement idPare = (SOAPElement)(idElement.getParentNode());
					SOAPElement idNou = idPare.addChildElement("id");
					idNou.setTextContent(idElement.getTextContent());
					idElement.getParentNode().removeChild(idElement);
				}
			}
		}
	}

	private void afegirDatosEspecificos(
			String idSolicitud,
			Element datosEspecificos) {
		if (datosEspecificosMap == null) {
			datosEspecificosMap = new HashMap<String, Element>();
		}
		datosEspecificosMap.put(idSolicitud, datosEspecificos);
	}

	private String nodeToString(Node node) throws TransformerFactoryConfigurationError, TransformerException {
		if (node == null)
			return null;
		StringWriter writer = new StringWriter();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(node), new StreamResult(writer));
		return writer.toString();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DatosEspecificosHandler.class);

}
