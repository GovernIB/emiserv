package es.caib.emiserv.scsp.fake.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static es.caib.emiserv.scsp.fake.core.FakeNs.*;

/**
 * Signa les respostes SOAP fake (XML-DSig a la capçalera o WS-Security).
 */
public final class FakeSigner {
	private static final String DEFAULT_KEYSTORE_RESOURCE = "interoperabilitat.jks";
	private static final String DEFAULT_KEYSTORE_PASSWORD = "tecnologies";
	private static final String DEFAULT_KEY_ALIAS = "limit_pinbal";

	private final PrivateKey privateKey;
	private final X509Certificate certificate;

	public FakeSigner(PrivateKey privateKey, X509Certificate certificate) {
		this.privateKey = privateKey;
		this.certificate = certificate;
	}

	/**
	 * Signador amb el keystore i l'alias per defecte (el JKS del classpath), canviables amb les
	 * propietats de sistema {@code fake.scsp.keystoreResource}, {@code fake.scsp.keystorePass},
	 * {@code fake.scsp.keyAlias} i {@code fake.scsp.keyPassword}.
	 */
	public static FakeSigner fromSystemProperties() {
		try {
			String keyAlias = System.getProperty("fake.scsp.keyAlias", DEFAULT_KEY_ALIAS);
			String keystorePassword = System.getProperty("fake.scsp.keystorePass", DEFAULT_KEYSTORE_PASSWORD);
			String keyPassword = System.getProperty("fake.scsp.keyPassword", keystorePassword);
			KeyStore keyStore = loadKeyStore(keystorePassword);
			PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias, keyPassword.toCharArray());
			X509Certificate certificate = (X509Certificate) keyStore.getCertificate(keyAlias);
			if (privateKey == null || certificate == null) {
				throw new IllegalStateException("No s'ha trobat la clau privada o el certificat per a l'alias " + keyAlias);
			}
			return new FakeSigner(privateKey, certificate);
		} catch (Exception e) {
			throw new IllegalStateException("No s'ha pogut inicialitzar la firma del fake SCSP", e);
		}
	}

	public String sign(String xml, SignatureStyle signatureStyle) {
		try {
			Document document = parse(xml);
			if (signatureStyle == SignatureStyle.WS_SECURITY) {
				removeSecurityHeader(document);
				Element securityHeader = ensureSecurityHeader(document);
				String idSeed = UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ROOT);
				String tokenId = "CertId-" + idSeed;
				String keyInfoId = "KeyId-" + idSeed;
				String securityTokenReferenceId = "STRId-" + idSeed;
				appendBinarySecurityToken(document, securityHeader, tokenId);
				appendWsSecuritySignature(document, securityHeader, tokenId, keyInfoId, securityTokenReferenceId);
			} else {
				removePlainSignature(document);
				Element header = ensureSoapHeader(document);
				appendXmlSignature(document, header);
			}
			return toXml(document);
		} catch (Exception e) {
			throw new IllegalStateException("No s'ha pogut firmar la resposta SOAP fake", e);
		}
	}

	private static KeyStore loadKeyStore(String keystorePassword) throws Exception {
		String resource = System.getProperty("fake.scsp.keystoreResource", DEFAULT_KEYSTORE_RESOURCE);
		InputStream input = FakeSigner.class.getClassLoader().getResourceAsStream(resource);
		if (input == null) {
			throw new IllegalStateException("No s'ha trobat el keystore al classpath: " + resource);
		}
		try {
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(input, keystorePassword.toCharArray());
			return keyStore;
		} finally {
			input.close();
		}
	}

	private Document parse(String xml) throws Exception {
		DocumentBuilderFactory factory = XmlSegur.documentBuilderFactory();
		return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
	}

	private Element ensureSoapHeader(Document document) {
		Element envelope = document.getDocumentElement();
		Element header = findFirstByLocalName(envelope, "Header");
		if (header == null) {
			header = document.createElementNS(SOAP_NS, qualifyName(envelope.getPrefix(), "Header"));
			Element body = findFirstByLocalName(envelope, "Body");
			envelope.insertBefore(header, body);
		}
		return header;
	}

	private Element ensureSecurityHeader(Document document) {
		Element header = ensureSoapHeader(document);
		Element security = document.createElementNS(WSSE_NS, "wsse:Security");
		header.appendChild(security);
		return security;
	}

	private void appendBinarySecurityToken(Document document, Element securityHeader, String tokenId) throws Exception {
		Element binarySecurityToken = document.createElementNS(WSSE_NS, "wsse:BinarySecurityToken");
		binarySecurityToken.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:wsu", WSU_NS);
		binarySecurityToken.setAttribute("EncodingType", BASE64_BINARY_TYPE);
		binarySecurityToken.setAttribute("ValueType", X509_V3_TYPE);
		binarySecurityToken.setAttributeNS(WSU_NS, "wsu:Id", tokenId);
		binarySecurityToken.setTextContent(Base64.getEncoder().encodeToString(certificate.getEncoded()));
		securityHeader.appendChild(binarySecurityToken);
	}

	private void appendWsSecuritySignature(
		Document document,
		Element securityHeader,
		String tokenId,
		String keyInfoId,
		String securityTokenReferenceId) throws Exception {
		Element body = findFirstByLocalName(document.getDocumentElement(), "Body");
		if (body == null) {
			throw new IllegalStateException("No s'ha trobat el Body del SOAP");
		}
		body.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:wsu", WSU_NS);
		if (!body.hasAttribute("Id")) {
			body.setAttribute("Id", "Body");
		}
		String bodyId = body.getAttributeNS(WSU_NS, "Id");
		if (bodyId == null || bodyId.isEmpty()) {
			bodyId = "id-" + positiveRandomNumber();
			body.setAttributeNS(WSU_NS, "wsu:Id", bodyId);
		}
		body.setIdAttributeNS(WSU_NS, "Id", true);
		body.setIdAttribute("Id", true);

		XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");
		List<Transform> transforms = Arrays.asList(
			signatureFactory.newTransform(CanonicalizationMethod.EXCLUSIVE, (javax.xml.crypto.dsig.spec.TransformParameterSpec) null)
		);
		Reference reference = signatureFactory.newReference(
			"#" + bodyId,
			signatureFactory.newDigestMethod(DigestMethod.SHA1, null),
			transforms,
			null,
			null
		);
		SignedInfo signedInfo = signatureFactory.newSignedInfo(
			signatureFactory.newCanonicalizationMethod(
				CanonicalizationMethod.EXCLUSIVE,
				(javax.xml.crypto.dsig.spec.C14NMethodParameterSpec) null),
			signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
			Collections.singletonList(reference)
		);

		KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
		Element securityTokenReference = createSecurityTokenReference(document, tokenId, securityTokenReferenceId);
		KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(new DOMStructure(securityTokenReference)), keyInfoId);

		DOMSignContext signContext = new DOMSignContext(privateKey, securityHeader);
		signContext.setDefaultNamespacePrefix("ds");
		signContext.putNamespacePrefix(WSSE_NS, "wsse");
		signContext.putNamespacePrefix(WSU_NS, "wsu");
		XMLSignature signature = signatureFactory.newXMLSignature(
			signedInfo,
			keyInfo,
			null,
			"Signature-" + positiveRandomNumber(),
			null);
		signature.sign(signContext);
	}

	private void appendXmlSignature(Document document, Element header) throws Exception {
		Element body = findFirstByLocalName(document.getDocumentElement(), "Body");
		if (body == null) {
			throw new IllegalStateException("No s'ha trobat el Body del SOAP");
		}
		String bodyId = body.getAttribute("Id");
		if (bodyId == null || bodyId.isEmpty()) {
			bodyId = "MsgBody";
			body.setAttribute("Id", bodyId);
		}
		body.setIdAttribute("Id", true);

		XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");
		List<Transform> transforms = Arrays.asList(
			signatureFactory.newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments",
				(javax.xml.crypto.dsig.spec.TransformParameterSpec) null)
		);
		Reference reference = signatureFactory.newReference(
			"#" + bodyId,
			signatureFactory.newDigestMethod(DigestMethod.SHA1, null),
			transforms,
			null,
			null
		);
		SignedInfo signedInfo = signatureFactory.newSignedInfo(
			signatureFactory.newCanonicalizationMethod(
				"http://www.w3.org/TR/2001/REC-xml-c14n-20010315",
				(javax.xml.crypto.dsig.spec.C14NMethodParameterSpec) null),
			signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
			Collections.singletonList(reference)
		);

		KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
		KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(
			keyInfoFactory.newX509Data(Collections.singletonList(certificate)))
		);

		DOMSignContext signContext = new DOMSignContext(privateKey, header);
		signContext.setDefaultNamespacePrefix("ds");
		XMLSignature signature = signatureFactory.newXMLSignature(signedInfo, keyInfo);
		signature.sign(signContext);
	}

	private Element createSecurityTokenReference(Document document, String tokenId, String securityTokenReferenceId) {
		Element securityTokenReference = document.createElementNS(WSSE_NS, "wsse:SecurityTokenReference");
		securityTokenReference.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:wsu", WSU_NS);
		securityTokenReference.setAttributeNS(WSU_NS, "wsu:Id", securityTokenReferenceId);
		Element reference = document.createElementNS(WSSE_NS, "wsse:Reference");
		reference.setAttribute("URI", "#" + tokenId);
		reference.setAttribute("ValueType", X509_V3_TYPE);
		securityTokenReference.appendChild(reference);
		return securityTokenReference;
	}

	private void removeSecurityHeader(Document document) {
		Element envelope = document.getDocumentElement();
		Element header = findFirstByLocalName(envelope, "Header");
		if (header == null) {
			return;
		}
		List<Element> securityHeaders = findAllByNamespaceAndLocalName(header, WSSE_NS, "Security");
		for (Element securityHeader : securityHeaders) {
			header.removeChild(securityHeader);
		}
	}

	private void removePlainSignature(Document document) {
		Element envelope = document.getDocumentElement();
		Element header = findFirstByLocalName(envelope, "Header");
		if (header == null) {
			return;
		}
		List<Element> signatures = findAllByNamespaceAndLocalName(header, DS_NS, "Signature");
		for (Element signature : signatures) {
			header.removeChild(signature);
		}
	}

	private String toXml(Document document) throws Exception {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(writer));
		return writer.toString();
	}

	private Element findFirstByLocalName(Element root, String localName) {
		if (root == null) {
			return null;
		}
		if (localName.equals(root.getLocalName())) {
			return root;
		}
		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) {
				Element found = findFirstByLocalName((Element) child, localName);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}

	private List<Element> findAllByNamespaceAndLocalName(Element root, String namespace, String localName) {
		List<Element> elements = new ArrayList<>();
		collectByNamespaceAndLocalName(root, namespace, localName, elements);
		return elements;
	}

	private void collectByNamespaceAndLocalName(Element root, String namespace, String localName, List<Element> elements) {
		if (root == null) {
			return;
		}
		if (localName.equals(root.getLocalName()) && namespace.equals(root.getNamespaceURI())) {
			elements.add(root);
		}
		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) {
				collectByNamespaceAndLocalName((Element) child, namespace, localName, elements);
			}
		}
	}

	private String qualifyName(String prefix, String localName) {
		return prefix == null || prefix.isEmpty() ? localName : prefix + ":" + localName;
	}

	private long positiveRandomNumber() {
		return Math.abs(UUID.randomUUID().getMostSignificantBits());
	}
}
