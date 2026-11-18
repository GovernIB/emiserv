package es.caib.emiserv.scsp.fake.core;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FakeSignerTest {

	private static final String DS_NS = "http://www.w3.org/2000/09/xmldsig#";
	private static final String WSSE_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	private static final String SOAP = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
		+ "<soapenv:Body><Respuesta><x>hola</x></Respuesta></soapenv:Body></soapenv:Envelope>";

	@Test
	void signaturaXmlDsigEsValida() throws Exception {
		FakeSigner signer = signadorGenerat();

		String signat = signer.sign(SOAP, SignatureStyle.XML_SIGNATURE);

		assertTrue(valida(signat));
	}

	@Test
	void signaturaWsSecurityEsValida() throws Exception {
		FakeSigner signer = signadorGenerat();

		String signat = signer.sign(SOAP, SignatureStyle.WS_SECURITY);

		assertTrue(signat.contains("BinarySecurityToken"));
		assertTrue(valida(signat));
	}

	@Test
	void signaturaDelKeystoreIncorporat() throws Exception {
		String signat = FakeSigner.fromSystemProperties().sign(SOAP, SignatureStyle.XML_SIGNATURE);

		assertTrue(valida(signat));
	}

	@Test
	void unaSignaturaManipuladaNoEsValida() throws Exception {
		String signat = signadorGenerat().sign(SOAP, SignatureStyle.XML_SIGNATURE).replace("hola", "adeu");

		assertEquals(false, valida(signat));
	}

	/** Genera una parella de claus i un certificat autosignat amb el keytool del JDK. */
	private static FakeSigner signadorGenerat() throws Exception {
		java.nio.file.Path dir = java.nio.file.Files.createTempDirectory("fake-signer-test");
		java.nio.file.Path jks = dir.resolve("test.jks");
		try {
			Process p = new ProcessBuilder(
				System.getProperty("java.home") + "/bin/keytool", "-genkeypair", "-alias", "test",
				"-keyalg", "RSA", "-keysize", "2048", "-validity", "2", "-dname", "CN=fake-test",
				"-keystore", jks.toString(), "-storetype", "JKS", "-storepass", "changeit", "-keypass", "changeit")
				.redirectErrorStream(true).start();
			p.getInputStream().readAllBytes();
			assertEquals(0, p.waitFor());
			KeyStore ks = KeyStore.getInstance("JKS");
			try (java.io.InputStream in = java.nio.file.Files.newInputStream(jks)) {
				ks.load(in, "changeit".toCharArray());
			}
			return new FakeSigner(
				(PrivateKey) ks.getKey("test", "changeit".toCharArray()),
				(X509Certificate) ks.getCertificate("test"));
		} finally {
			java.nio.file.Files.deleteIfExists(jks);
			java.nio.file.Files.deleteIfExists(dir);
		}
	}

	private static boolean valida(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
		NodeList signatures = document.getElementsByTagNameNS(DS_NS, "Signature");
		assertEquals(1, signatures.getLength());
		// L'atribut Id del Body s'ha de registrar com a ID perquè la referència #<id> es resolgui
		Element body = (Element) document.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Body").item(0);
		if (body.hasAttribute("Id")) {
			body.setIdAttribute("Id", true);
		}
		String wsu = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
		if (body.hasAttributeNS(wsu, "Id")) {
			body.setIdAttributeNS(wsu, "Id", true);
		}
		// La clau pública s'extreu del certificat (X509Data o BinarySecurityToken)
		X509Certificate certificate = certificatDe(document);
		DOMValidateContext context = new DOMValidateContext(certificate.getPublicKey(), signatures.item(0));
		XMLSignature signature = XMLSignatureFactory.getInstance("DOM").unmarshalXMLSignature(context);
		return signature.validate(context);
	}

	private static X509Certificate certificatDe(Document document) throws Exception {
		NodeList tokens = document.getElementsByTagNameNS(WSSE_NS, "BinarySecurityToken");
		NodeList x509 = document.getElementsByTagNameNS(DS_NS, "X509Certificate");
		String base64 = tokens.getLength() > 0 ? tokens.item(0).getTextContent() : x509.item(0).getTextContent();
		byte[] der = java.util.Base64.getMimeDecoder().decode(base64.trim());
		return (X509Certificate) java.security.cert.CertificateFactory.getInstance("X.509")
			.generateCertificate(new java.io.ByteArrayInputStream(der));
	}
}
