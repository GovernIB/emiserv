/**
 * 
 */
package es.caib.emiserv.backoffice.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
		/*SOAPMessage message = smc.getMessage();
		SOAPPart part = message.getSOAPPart();
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodesTransmisionDatos = part.getEnvelope().getOwnerDocument().getElementsByTagName(
				"solicitudTransmision");
		for (int i = 0; i < nodesTransmisionDatos.getLength(); i++) {
			Element transmisionDatos = (Element)nodesTransmisionDatos.item(i);
			NodeList nodesIdSolicitud = (NodeList)xPath.evaluate(
					"//datosGenericos/transmision/idSolicitud",
					transmisionDatos,
					XPathConstants.NODESET);
		    if (nodesIdSolicitud.getLength() > 0) {
		    	NodeList nl = transmisionDatos.getChildNodes();
		    	for (int j = 0; j < nl.getLength(); j++) {
		    		Node n = nl.item(j);
		    		if (n.getNodeType() == Node.ELEMENT_NODE && "datosEspecificos".equalsIgnoreCase(n.getLocalName())) {
		    			System.out.println(">>> ABANS: " + nodeToString(part.getEnvelope()));
		    			SOAPElement datosEspecificos = (SOAPElement)n;
		    			SOAPElement datosEspecificosPare = datosEspecificos.getParentElement();
		    			datosEspecificosPare.removeChild(datosEspecificos);
		    			SOAPElement datosEspecificosNou = datosEspecificosPare.addChildElement("datosEspecificos");
		    			NodeList nlde = datosEspecificos.getChildNodes();
				    	for (int k = 0; k < nlde.getLength(); k++) {
				    		Node nde = nlde.item(j);
				    		datosEspecificosNou.appendChild(nde.cloneNode(true));
				    		datosEspecificos.removeChild(nde);
				    	}
				    	System.out.println(">>> DESPRES: " + nodeToString(part.getEnvelope()));
		    			break;
		    		}
		    	}
		    }
		}*/
	}

	private void processarDatosEspecificosResposta(
			SOAPMessageContext smc) throws TransformerConfigurationException, TransformerException, SOAPException, IOException, XPathExpressionException, ParserConfigurationException {
		Document document = toDocument(smc.getMessage());
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodesTransmisionDatos = (NodeList)xPath.evaluate(
				"//transmisionDatos",
				document.getDocumentElement(),
		        XPathConstants.NODESET);
		for (int i = 0; i < nodesTransmisionDatos.getLength(); i++) {
			Element transmisionDatos = (Element)nodesTransmisionDatos.item(i);
			NodeList nodesIdSolicitud = (NodeList)xPath.evaluate(
					"//datosGenericos/transmision/idSolicitud",
					transmisionDatos,
					XPathConstants.NODESET);
		    if (nodesIdSolicitud.getLength() > 0) {
		    	String idSolicitud = nodesIdSolicitud.item(0).getTextContent();
		    	NodeList nl = transmisionDatos.getChildNodes();
		    	for (int j = 0; j < nl.getLength(); j++) {
		    		Node n = nl.item(j);
		    		if (n.getNodeType() == Node.ELEMENT_NODE && "datosEspecificos".equalsIgnoreCase(n.getLocalName())) {
		    			/*Element e = (Element)n;
		    			Map<String, String> dadesEspecifiques = new HashMap<String, String>();
		    			List<String> path = new ArrayList<String>();
		    			extreureDadesEspecifiques(
		    					e,
		    					path,
		    					dadesEspecifiques,
		    					true);
				    	afegirDatosEspecificos(
				    			idSolicitud,
				    			crearDadesEspecifiques(
				    					e.getNamespaceURI(),
				    					dadesEspecifiques));*/
		    			afegirDatosEspecificos(
				    			idSolicitud,
				    			(Element)n);
		    		}
		    	}
		    }
		}
	}

	/*private void extreureDadesEspecifiques(
			org.w3c.dom.Node node,
			List<String> path,
			Map<String, String> dades,
			boolean incloureAlPath) {
		if (incloureAlPath) {
			if (node.getPrefix() != null) {
				path.add(node.getNodeName().substring(
						node.getPrefix().length() + 1));
			} else {
				path.add(node.getNodeName());
			}
		}
		if (node.hasChildNodes()) {
			NodeList fills = node.getChildNodes();
			for (int i = 0; i < fills.getLength(); i++) {
				org.w3c.dom.Node fill = fills.item(i);
				if (fill.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					extreureDadesEspecifiques(
							fill,
							path,
							dades,
							true);
				}
				if (fill.getNodeType() == org.w3c.dom.Node.TEXT_NODE && fills.getLength() == 1) {
					dades.put(
							pathToString(path),
							node.getTextContent());
				}
			}
		} else {
			dades.put(
					pathToString(path),
					node.getTextContent());
		}
		if (incloureAlPath) {
			path.remove(path.size() - 1);
		}
	}
	private String pathToString(List<String> path) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0 ; i < path.size(); i++) {
			sb.append("/");
			sb.append(path.get(i));
		}
		return sb.toString();
	}

	private Element crearDadesEspecifiques(
			String xmlns,
			Map<String, String> dadesEspecifiques) throws ParserConfigurationException {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		Document doc = fac.newDocumentBuilder().newDocument();
		Element datosEspecificos = doc.createElement("DatosEspecificos");
		datosEspecificos.setAttribute(
				"xmlns",
				xmlns);
		if (dadesEspecifiques != null) {
			for (String path: dadesEspecifiques.keySet()) {
				String valor = dadesEspecifiques.get(path);
				String[] pathParts = path.substring("/DatosEspecificos/".length()).split("/");
				Element elementActual = datosEspecificos;
				for (String pathPart: pathParts) {
					NodeList nodeList = elementActual.getElementsByTagName(pathPart);
					Element elementTrobat = null;
					if (nodeList.getLength() > 0) {
						for (int i = 0; i < nodeList.getLength(); i++) {
							org.w3c.dom.Node n = nodeList.item(i);
							if (n.getParentNode().equals(elementActual)) {
								elementTrobat = (Element)n;
								break;
							}
						}
					}
					if (elementTrobat == null) {
						Element nou = doc.createElement(pathPart);
						elementActual.appendChild(nou);
						elementActual = nou;
					} else {
						elementActual = elementTrobat;
					}
				}
				elementActual.setTextContent(valor);
			}
		}
		doc.appendChild(datosEspecificos);
		return doc.getDocumentElement();
	}*/

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

	private Document toDocument(
			SOAPMessage soapMsg) throws TransformerConfigurationException, TransformerException, SOAPException, IOException {
		Source src = soapMsg.getSOAPPart().getContent();
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		DOMResult result = new DOMResult();
		transformer.transform(src, result);
		return (Document)result.getNode();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DatosEspecificosHandler.class);

}
