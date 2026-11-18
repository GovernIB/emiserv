package es.caib.emiserv.scsp.fake.core;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/** Factoria de parsers XML reforçada (sense DOCTYPE ni entitats externes), per a XML que ve de fora. */
final class XmlSegur {

	private XmlSegur() {
	}

	static DocumentBuilderFactory documentBuilderFactory() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			factory.setXIncludeAware(false);
			factory.setExpandEntityReferences(false);
			return factory;
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException("No s'ha pogut configurar el parser XML segur", e);
		}
	}
}
