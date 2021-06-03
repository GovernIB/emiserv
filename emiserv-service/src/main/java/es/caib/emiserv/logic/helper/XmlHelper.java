/**
 * 
 */
package es.caib.emiserv.logic.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Helper per a operacions amb documents XML.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class XmlHelper {

	public String getTextFromFirstNode(
			Object element,
			String xpath) throws XPathExpressionException {
		return getTextFromFirstNode(
				element,
				xpath,
				false);
	}
	public String getTextFromFirstNode(
			Object element,
			String xpath,
			boolean opcional) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(
				new ScspNamespaceResolver());
		NodeList nodes = (NodeList)xPath.evaluate(
				xpath,
				element,
				XPathConstants.NODESET);
		if (nodes.getLength() == 0) {
			if (opcional)
				return null;
			else
				throw new NoSuchElementException(xpath);
		} else {
			Node node = nodes.item(0);
			return node.getTextContent();
		}
	}
	public boolean setTextToFirstNode(
			Object element,
			String xpath,
			String text) throws XPathExpressionException {
		boolean ret = false;
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(
				new ScspNamespaceResolver());
		NodeList nodes = (NodeList) xPath.evaluate(
				xpath,
				element,
				XPathConstants.NODESET);
		if (nodes.getLength() > 0) {
			Node node = nodes.item(0);
			node.setTextContent(text);
			ret = true;
		}
		return ret;
	}

	public Node getFirstNode(
			Object element,
			String xpath) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(
				new ScspNamespaceResolver());
		NodeList nodes = (NodeList)xPath.evaluate(
				xpath,
				element,
				XPathConstants.NODESET);
		if (nodes.getLength() > 0) {
			return nodes.item(0);
		} else {
			return null;
		}
	}

	public int countNodes(
			Object element,
			String xpath) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(
				new ScspNamespaceResolver());
		NodeList nodes = (NodeList)xPath.evaluate(
				xpath,
				element,
				XPathConstants.NODESET);
		return nodes.getLength();
	}

	public boolean isPeticioScspV2(
			Object element) throws XPathExpressionException {
		return countNodes(element, "//peticion2:Peticion") > 0;
	}
	public boolean isPeticioScspV3(
			Object element) throws XPathExpressionException {
		return countNodes(element, "//peticion3:Peticion") > 0;
	}

	public NodeList getNodeList(
			Object element,
			String xpath) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(
				new ScspNamespaceResolver());
		return (NodeList)xPath.evaluate(
				xpath,
				element,
				XPathConstants.NODESET);
	}

	public String nodeToString(Node node) throws TransformerFactoryConfigurationError, TransformerException {
		if (node == null)
			return null;
		StringWriter writer = new StringWriter();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(new DOMSource(node), new StreamResult(writer));
		return writer.toString();
	}

	public Document bytesToDocument(
			byte[] bytes) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(
				new ByteArrayInputStream(bytes));
	}

	static class ScspNamespaceResolver implements NamespaceContext {
		public String getNamespaceURI(String prefix) {
			if (prefix == null) {
				throw new NullPointerException("El prefix no pot ser null");
			} else if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
				return "http://intermediacion.redsara.es/scsp/esquemas/V3/peticion";
			} else if (prefix.equals("peticion2")) {
				return "http://www.map.es/scsp/esquemas/V2/peticion";
			} else if (prefix.equals("respuesta2")) {
				return "http://www.map.es/scsp/esquemas/V2/respuesta";
			} else if (prefix.equals("datesp2")) {
				return "http://www.map.es/scsp/esquemas/esquemas/datosespecificos";
			} else if (prefix.equals("soapfaultatr2")) {
				return "http://www.map.es/scsp/esquemas/atributos";
			} else if (prefix.equals("peticion3")) {
				return "http://intermediacion.redsara.es/scsp/esquemas/V3/peticion";
			} else if (prefix.equals("respuesta3")) {
				return "http://intermediacion.redsara.es/scsp/esquemas/V3/respuesta";
			} else if (prefix.equals("datesp3")) {
				return "http://intermediacion.redsara.es/scsp/esquemas/datosespecificos";
			} else if (prefix.equals("soapfaultatr3")) {
				return "http://intermediacion.redsara.es/scsp/esquemas/V3/soapfaultatributos";
			} else if (prefix.equals("soapenv")) {
				return "http://schemas.xmlsoap.org/soap/envelope/";
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
