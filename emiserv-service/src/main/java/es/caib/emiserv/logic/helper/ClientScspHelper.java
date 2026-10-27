/**
 *
 */
package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.persist.entity.scsp.ScspCoreClavePrivadaEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreParametroConfiguracionEntity;
import es.caib.emiserv.persist.repository.scsp.ScspCoreClavePrivadaRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreParametroConfiguracionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.KeySelector;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;

/**
 * Client SCSP per enviar peticions síncrones als emisors dels cedents (rutes de tipus EMISERV de les
 * consultes asíncrones processades com a síncrones). La petició es signa amb WS-Security
 * (BinarySecurityToken amb referència directa, com els requeridors SCSP) amb la clau privada del
 * servei a l'emisor SCSP d'EMISERV (certificat de la CAIB) i se'n valida la signatura de la resposta.
 *
 * Fa servir només l'API XML Digital Signature de Java: les llibreries de seguretat de l'emisor
 * (WSS4J) només són a l'aplicació de l'emisor SCSP.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class ClientScspHelper {

	static final String NS_SOAPENV = "http://schemas.xmlsoap.org/soap/envelope/";
	static final String NS_WSSE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	static final String NS_WSU = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
	static final String X509_V3 = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3";
	static final String BASE64_BINARY = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary";
	static final String RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";

	@Autowired
	private ScspCoreParametroConfiguracionRepository scspCoreParametroConfiguracionRepository;
	@Autowired
	private ScspCoreClavePrivadaRepository scspCoreClavePrivadaRepository;

	/**
	 * Clau privada i certificat amb què es signen les peticions: els de la clau de signatura del servei
	 * a l'emisor SCSP (core_servicio.clavefirma) i el magatzem de claus de l'emisor (keystoreFile).
	 */
	public Credencials getCredencials(Long clauPrivadaId) {
		if (clauPrivadaId == null) {
			throw new ValidationException("El servei no té configurada cap clau de signatura a l'emisor SCSP");
		}
		ScspCoreClavePrivadaEntity clau = scspCoreClavePrivadaRepository.findById(clauPrivadaId).orElseThrow(
				() -> new ValidationException("No s'ha trobat la clau de signatura del servei (id=" + clauPrivadaId + ")"));
		String fitxer = getParametre("keystoreFile");
		String tipus = scspCoreParametroConfiguracionRepository.findById("keystoreType").map(ScspCoreParametroConfiguracionEntity::getValor).orElse("JKS");
		String password = getParametre("keystorePass");
		try (InputStream is = new FileInputStream(new File(fitxer.startsWith("file:") ? fitxer.substring("file:".length()) : fitxer))) {
			KeyStore keyStore = KeyStore.getInstance(tipus);
			keyStore.load(is, password.toCharArray());
			PrivateKey privateKey = (PrivateKey)keyStore.getKey(clau.getAlies(), clau.getPassword().toCharArray());
			X509Certificate certificat = (X509Certificate)keyStore.getCertificate(clau.getAlies());
			if (privateKey == null || certificat == null) {
				throw new ValidationException("No s'ha trobat la clau privada de l'àlies " + clau.getAlies() + " al magatzem de claus de l'emisor");
			}
			return new Credencials(privateKey, certificat);
		} catch (ValidationException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ValidationException("No s'ha pogut obtenir la clau de signatura del magatzem de claus de l'emisor (" + fitxer + "): " + ex.getMessage());
		}
	}

	/**
	 * Envia una petició SCSP síncrona signada i retorna la resposta. Els SOAP Fault, les respostes
	 * sense signatura vàlida i les respostes que no són Respuesta generen una ScspCedentException.
	 *
	 * @param peticion element Peticion (SCSP) que s'envia al Body.
	 */
	public RespostaCedent enviarPeticioSincrona(
			String url,
			String soapAction,
			Element peticion,
			Credencials credencials,
			int timeoutSegons,
			boolean validarSignatura) throws Exception {
		Document document = crearPeticioSignada(peticion, credencials);
		String xmlPeticio = toXml(document);
		HttpURLConnection connexio = (HttpURLConnection)new URL(url).openConnection();
		connexio.setConnectTimeout(timeoutSegons * 1000);
		connexio.setReadTimeout(timeoutSegons * 1000);
		connexio.setDoOutput(true);
		connexio.setRequestMethod("POST");
		connexio.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
		connexio.setRequestProperty("SOAPAction", "\"" + (soapAction != null ? soapAction : "") + "\"");
		try (OutputStream os = connexio.getOutputStream()) {
			os.write(xmlPeticio.getBytes(StandardCharsets.UTF_8));
		}
		int codiHttp = connexio.getResponseCode();
		byte[] cos;
		try (InputStream is = codiHttp >= 400 ? connexio.getErrorStream() : connexio.getInputStream()) {
			cos = is != null ? is.readAllBytes() : new byte[0];
		}
		String xmlResposta = new String(cos, StandardCharsets.UTF_8);
		Document resposta;
		try {
			resposta = parse(cos);
		} catch (Exception ex) {
			throw new ScspCedentException(null, "Resposta no vàlida de l'emisor del cedent (HTTP " + codiHttp + "): " + abreujar(xmlResposta), xmlPeticio, xmlResposta);
		}
		Element fault = primerElement(resposta, NS_SOAPENV, "Fault");
		if (fault != null) {
			String codi = textPrimer(fault, "CodigoEstado");
			String literal = textPrimer(fault, "LiteralError");
			if (literal == null) {
				literal = textPrimer(fault, "faultstring");
			}
			throw new ScspCedentException(codi, "SOAP Fault de l'emisor del cedent: " + literal, xmlPeticio, xmlResposta);
		}
		if (validarSignatura) {
			validarSignatura(resposta, xmlPeticio, xmlResposta);
		}
		Element body = primerElement(resposta, NS_SOAPENV, "Body");
		Element respuesta = body != null ? primerFill(body, "Respuesta") : null;
		if (respuesta == null) {
			throw new ScspCedentException(null, "L'emisor del cedent no ha retornat cap Respuesta (HTTP " + codiHttp + ")", xmlPeticio, xmlResposta);
		}
		return new RespostaCedent(xmlPeticio, xmlResposta, respuesta);
	}

	Document crearPeticioSignada(Element peticion, Credencials credencials) throws Exception {
		Document document = nouDocument();
		Element envelope = document.createElementNS(NS_SOAPENV, "soapenv:Envelope");
		document.appendChild(envelope);
		Element header = document.createElementNS(NS_SOAPENV, "soapenv:Header");
		envelope.appendChild(header);
		Element body = document.createElementNS(NS_SOAPENV, "soapenv:Body");
		envelope.appendChild(body);
		body.appendChild(document.importNode(peticion, true));
		String id = UUID.randomUUID().toString().replace("-", "").toUpperCase();
		String bodyId = "id-" + id;
		body.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:wsu", NS_WSU);
		body.setAttributeNS(NS_WSU, "wsu:Id", bodyId);
		body.setIdAttributeNS(NS_WSU, "Id", true);
		Element security = document.createElementNS(NS_WSSE, "wsse:Security");
		security.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:wsu", NS_WSU);
		header.appendChild(security);
		String tokenId = "X509-" + id;
		Element token = document.createElementNS(NS_WSSE, "wsse:BinarySecurityToken");
		token.setAttribute("EncodingType", BASE64_BINARY);
		token.setAttribute("ValueType", X509_V3);
		token.setAttributeNS(NS_WSU, "wsu:Id", tokenId);
		token.setTextContent(Base64.getEncoder().encodeToString(credencials.getCertificat().getEncoded()));
		security.appendChild(token);
		XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");
		Reference reference = factory.newReference(
				"#" + bodyId,
				factory.newDigestMethod(DigestMethod.SHA256, null),
				Collections.singletonList(factory.newTransform(CanonicalizationMethod.EXCLUSIVE, (TransformParameterSpec)null)),
				null,
				null);
		SignedInfo signedInfo = factory.newSignedInfo(
				factory.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec)null),
				factory.newSignatureMethod(RSA_SHA256, null),
				Collections.singletonList(reference));
		Element str = document.createElementNS(NS_WSSE, "wsse:SecurityTokenReference");
		Element strReference = document.createElementNS(NS_WSSE, "wsse:Reference");
		strReference.setAttribute("URI", "#" + tokenId);
		strReference.setAttribute("ValueType", X509_V3);
		str.appendChild(strReference);
		KeyInfoFactory keyInfoFactory = factory.getKeyInfoFactory();
		KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(new DOMStructure(str)), "KI-" + id);
		XMLSignature signature = factory.newXMLSignature(signedInfo, keyInfo, null, "SIG-" + id, null);
		DOMSignContext signContext = new DOMSignContext(credencials.getClauPrivada(), security);
		signContext.setDefaultNamespacePrefix("ds");
		signature.sign(signContext);
		return document;
	}

	/**
	 * Valida la signatura WS-Security (o XML Signature a la capçalera) de la resposta: ha de signar el
	 * Body i el certificat ha de ser vigent.
	 */
	void validarSignatura(Document resposta, String xmlPeticio, String xmlResposta) {
		try {
			NodeList signatures = resposta.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
			if (signatures.getLength() == 0) {
				throw new ScspCedentException(null, "La resposta de l'emisor del cedent no està signada", xmlPeticio, xmlResposta);
			}
			marcarIds(resposta.getDocumentElement());
			Element signature = (Element)signatures.item(0);
			X509Certificate certificat = getCertificatSignatura(resposta, signature);
			certificat.checkValidity();
			Element body = primerElement(resposta, NS_SOAPENV, "Body");
			String bodyId = body != null ? getId(body) : null;
			boolean bodySignat = false;
			NodeList references = signature.getElementsByTagNameNS(XMLSignature.XMLNS, "Reference");
			for (int i = 0; i < references.getLength(); i++) {
				String uri = ((Element)references.item(i)).getAttribute("URI");
				if (bodyId != null && ("#" + bodyId).equals(uri)) {
					bodySignat = true;
				}
			}
			if (!bodySignat) {
				throw new ScspCedentException(null, "La signatura de la resposta de l'emisor del cedent no inclou el Body", xmlPeticio, xmlResposta);
			}
			DOMValidateContext validateContext = new DOMValidateContext(KeySelector.singletonKeySelector(certificat.getPublicKey()), signature);
			XMLSignature xmlSignature = XMLSignatureFactory.getInstance("DOM").unmarshalXMLSignature(validateContext);
			if (!xmlSignature.validate(validateContext)) {
				throw new ScspCedentException(null, "La signatura de la resposta de l'emisor del cedent no és vàlida", xmlPeticio, xmlResposta);
			}
			log.debug("Signatura de la resposta de l'emisor del cedent vàlida (certificat=" + certificat.getSubjectX500Principal() + ")");
		} catch (ScspCedentException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ScspCedentException(null, "No s'ha pogut validar la signatura de la resposta de l'emisor del cedent: " + ex.getMessage(), xmlPeticio, xmlResposta);
		}
	}

	private X509Certificate getCertificatSignatura(Document document, Element signature) throws Exception {
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		NodeList x509 = signature.getElementsByTagNameNS(XMLSignature.XMLNS, "X509Certificate");
		if (x509.getLength() > 0) {
			return (X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(
					Base64.getMimeDecoder().decode(x509.item(0).getTextContent().trim())));
		}
		NodeList referencies = signature.getElementsByTagNameNS(NS_WSSE, "Reference");
		Element token = null;
		if (referencies.getLength() > 0) {
			String uri = ((Element)referencies.item(0)).getAttribute("URI");
			token = document.getElementById(uri.startsWith("#") ? uri.substring(1) : uri);
		}
		if (token == null) {
			NodeList tokens = document.getElementsByTagNameNS(NS_WSSE, "BinarySecurityToken");
			token = tokens.getLength() == 1 ? (Element)tokens.item(0) : null;
		}
		if (token == null) {
			throw new IllegalStateException("No s'ha trobat el certificat de la signatura");
		}
		return (X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(
				Base64.getMimeDecoder().decode(token.getTextContent().trim())));
	}

	/** Els atributs Id (wsu:Id, Id, ID) es marquen com a identificadors per resoldre les referències. */
	private static void marcarIds(Element element) {
		if (element.hasAttributeNS(NS_WSU, "Id")) {
			element.setIdAttributeNS(NS_WSU, "Id", true);
		} else if (element.hasAttribute("Id")) {
			element.setIdAttribute("Id", true);
		} else if (element.hasAttribute("ID")) {
			element.setIdAttribute("ID", true);
		}
		for (Node fill = element.getFirstChild(); fill != null; fill = fill.getNextSibling()) {
			if (fill instanceof Element) {
				marcarIds((Element)fill);
			}
		}
	}

	private static String getId(Element element) {
		if (element.hasAttributeNS(NS_WSU, "Id")) {
			return element.getAttributeNS(NS_WSU, "Id");
		} else if (element.hasAttribute("Id")) {
			return element.getAttribute("Id");
		}
		return element.hasAttribute("ID") ? element.getAttribute("ID") : null;
	}

	private String getParametre(String nom) {
		return scspCoreParametroConfiguracionRepository.findById(nom).map(ScspCoreParametroConfiguracionEntity::getValor).orElseThrow(
				() -> new ValidationException("No s'ha configurat el paràmetre " + nom + " de l'emisor SCSP"));
	}

	static Document nouDocument() throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		return dbf.newDocumentBuilder().newDocument();
	}

	static Document parse(byte[] xml) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		return dbf.newDocumentBuilder().parse(new InputSource(new ByteArrayInputStream(xml)));
	}

	static Document parse(String xml) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		return dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
	}

	static String toXml(Node node) throws Exception {
		javax.xml.transform.Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(node), new StreamResult(writer));
		return writer.toString();
	}

	private static Element primerElement(Document document, String namespace, String nom) {
		NodeList nodes = document.getElementsByTagNameNS(namespace, nom);
		return nodes.getLength() > 0 ? (Element)nodes.item(0) : null;
	}

	private static Element primerFill(Element pare, String nom) {
		for (Node fill = pare.getFirstChild(); fill != null; fill = fill.getNextSibling()) {
			if (fill instanceof Element && nom.equals(fill.getLocalName())) {
				return (Element)fill;
			}
		}
		return null;
	}

	private static String textPrimer(Element element, String nom) {
		NodeList nodes = element.getElementsByTagNameNS("*", nom);
		if (nodes.getLength() == 0) {
			nodes = element.getElementsByTagName(nom);
		}
		return nodes.getLength() > 0 ? nodes.item(0).getTextContent().trim() : null;
	}

	private static String abreujar(String text) {
		return text.length() > 300 ? text.substring(0, 300) + "..." : text;
	}

	static void escriure(ByteArrayOutputStream os, String text) {
		if (text != null) {
			os.writeBytes(text.getBytes(StandardCharsets.UTF_8));
		}
	}

	public static class Credencials {
		private final PrivateKey clauPrivada;
		private final X509Certificate certificat;
		public Credencials(PrivateKey clauPrivada, X509Certificate certificat) {
			this.clauPrivada = clauPrivada;
			this.certificat = certificat;
		}
		PrivateKey getClauPrivada() {
			return clauPrivada;
		}
		X509Certificate getCertificat() {
			return certificat;
		}
	}

	public static class RespostaCedent {
		private final String xmlPeticio;
		private final String xmlResposta;
		private final Element respuesta;
		RespostaCedent(String xmlPeticio, String xmlResposta, Element respuesta) {
			this.xmlPeticio = xmlPeticio;
			this.xmlResposta = xmlResposta;
			this.respuesta = respuesta;
		}
		public String getXmlPeticio() {
			return xmlPeticio;
		}
		public String getXmlResposta() {
			return xmlResposta;
		}
		public Element getRespuesta() {
			return respuesta;
		}
	}

	/** Error retornat per l'emisor del cedent (SOAP Fault, resposta no vàlida o signatura incorrecta). */
	public static class ScspCedentException extends RuntimeException {
		private final String codiEstat;
		private final String xmlPeticio;
		private final String xmlResposta;
		public ScspCedentException(String codiEstat, String missatge, String xmlPeticio, String xmlResposta) {
			super((codiEstat != null ? "[" + codiEstat + "] " : "") + missatge);
			this.codiEstat = codiEstat;
			this.xmlPeticio = xmlPeticio;
			this.xmlResposta = xmlResposta;
		}
		public String getCodiEstat() {
			return codiEstat;
		}
		public String getXmlPeticio() {
			return xmlPeticio;
		}
		public String getXmlResposta() {
			return xmlResposta;
		}
		/**
		 * Errors de la petició o d'autorització (03xx, 04xx): tornar-la a enviar donaria el mateix
		 * resultat. La resta (errors del servidor, de comunicació o de signatura) es reintenten.
		 */
		public boolean isDefinitiu() {
			return codiEstat != null && (codiEstat.startsWith("03") || codiEstat.startsWith("04"));
		}
	}

}
