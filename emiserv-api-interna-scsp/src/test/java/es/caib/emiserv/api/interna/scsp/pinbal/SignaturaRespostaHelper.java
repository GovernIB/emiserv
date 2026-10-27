package es.caib.emiserv.api.interna.scsp.pinbal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Validació de la signatura WS-Security de les respostes de l'emisor SCSP.
 *
 * PINBAL valida la signatura de les respostes amb el certificat que inclou el propi missatge
 * (es.scsp.common.signature.CustomSignatureProcessor). Aquí es fa la mateixa comprovació
 * criptogràfica: la signatura ha de ser vàlida amb la clau pública del certificat de la
 * capçalera Security i ha de cobrir el Body SOAP.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SignaturaRespostaHelper {

	private static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
	private static final String WSSE_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	private static final String WSU_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";

	public static boolean isSignada(Document document) {
		return document.getElementsByTagNameNS(WSSE_NS, "Security").getLength() > 0 &&
				document.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature").getLength() > 0;
	}

	/**
	 * Valida la signatura i retorna el certificat signant.
	 *
	 * @throws AssertionError si la signatura no és vàlida o no cobreix el Body.
	 */
	public static X509Certificate validar(Document document) throws Exception {
		marcarAtributsId(document);
		Element signatura = (Element)document.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature").item(0);
		if (signatura == null) {
			throw new AssertionError("La resposta no està signada");
		}
		X509Certificate certificat = getCertificat(document);
		DOMValidateContext context = new DOMValidateContext(certificat.getPublicKey(), signatura);
		XMLSignature xmlSignature = XMLSignatureFactory.getInstance("DOM").unmarshalXMLSignature(context);
		if (!xmlSignature.validate(context)) {
			List<String> errors = new ArrayList<>();
			if (!xmlSignature.getSignatureValue().validate(context)) {
				errors.add("SignatureValue");
			}
			for (Object reference: xmlSignature.getSignedInfo().getReferences()) {
				if (!((Reference)reference).validate(context)) {
					errors.add("Reference " + ((Reference)reference).getURI());
				}
			}
			throw new AssertionError("La signatura de la resposta no és vàlida (" + String.join(", ", errors) + ")");
		}
		Element body = (Element)document.getElementsByTagNameNS(SOAP_NS, "Body").item(0);
		String bodyId = body.getAttributeNS(WSU_NS, "Id");
		if (bodyId.isEmpty()) {
			bodyId = body.getAttribute("Id");
		}
		boolean bodySignat = false;
		for (Object reference: xmlSignature.getSignedInfo().getReferences()) {
			if (("#" + bodyId).equals(((Reference)reference).getURI())) {
				bodySignat = true;
			}
		}
		if (!bodySignat) {
			throw new AssertionError("La signatura de la resposta no cobreix el Body SOAP");
		}
		return certificat;
	}

	private static X509Certificate getCertificat(Document document) throws Exception {
		String base64 = null;
		NodeList tokens = document.getElementsByTagNameNS(WSSE_NS, "BinarySecurityToken");
		if (tokens.getLength() > 0) {
			base64 = tokens.item(0).getTextContent();
		} else {
			NodeList certificats = document.getElementsByTagNameNS(XMLSignature.XMLNS, "X509Certificate");
			if (certificats.getLength() > 0) {
				base64 = certificats.item(0).getTextContent();
			}
		}
		if (base64 == null) {
			throw new AssertionError("La resposta signada no inclou el certificat del signant");
		}
		return (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(
				new ByteArrayInputStream(Base64.getMimeDecoder().decode(base64.trim())));
	}

	private static void marcarAtributsId(Document document) {
		NodeList elements = document.getElementsByTagName("*");
		for (int i = 0; i < elements.getLength(); i++) {
			Element element = (Element)elements.item(i);
			if (element.hasAttributeNS(WSU_NS, "Id")) {
				element.setIdAttributeNS(WSU_NS, "Id", true);
			} else if (element.hasAttribute("Id")) {
				element.setIdAttribute("Id", true);
			}
		}
	}

}
