package es.caib.emiserv.api.interna.scsp.pinbal;

import es.scsp.common.signature.XMLDSigManager;
import org.apache.wss4j.common.crypto.Merlin;
import org.springframework.ws.client.support.interceptor.ClientInterceptorAdapter;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.FaultAwareWebServiceMessage;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j2.callback.KeyStoreCallbackHandler;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Client SCSP que envia les peticions a l'emisor igual que ho fa PINBAL amb el ClienteUnico
 * de les llibreries SCSP 5 (es.scsp.client.BasicServiceClientSpring):
 *
 * - Missatge SOAP 1.1 creat amb AxiomSoapMessageFactory i enviat amb HttpComponentsMessageSender.
 * - Capçalera SOAPAction amb l'acció del servei (peticionSincrona, peticionAsincrona o solicitudRespuesta).
 * - Signatura WS-Security amb Wss4jSecurityInterceptor (acció Signature, certificat com a
 *   BinarySecurityToken (DirectReference) i mustUnderstand=false), igual que es.scsp.common.signature.ScspWssInterceptor.
 * - Spring-WS només tracta com a SOAP Fault les respostes amb HTTP 500 (com el client de PINBAL).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientScspPinbal {

	public static final String ACCIO_SINCRONA = "peticionSincrona";
	public static final String ACCIO_ASINCRONA = "peticionAsincrona";
	public static final String ACCIO_SOLICITUD_RESPUESTA = "solicitudRespuesta";

	private final ConfiguracioProva configuracio;
	private final AxiomSoapMessageFactory messageFactory;

	public ClientScspPinbal(ConfiguracioProva configuracio) {
		this.configuracio = configuracio;
		// Igual que el bean messageFactory de les llibreries SCSP 5 (sense afterPropertiesSet: SOAP 1.1 per defecte)
		this.messageFactory = new AxiomSoapMessageFactory();
	}

	/** Envia el missatge signat amb el certificat configurat (igual que PINBAL). */
	public RespostaScsp enviar(String url, String accio, Element payload) throws Exception {
		return enviar(url, accio, payload, true);
	}

	public RespostaScsp enviar(String url, String accio, Element payload, boolean signar) throws Exception {
		return enviar(url, accio, payload, signar ? crearInterceptorSignatura() : null);
	}

	/**
	 * Envia el missatge amb les dades específiques xifrades, igual que PINBAL als serveis amb
	 * xifratge (es.scsp.common.signature.ScspWssInterceptor com a requeridor): primer es xifren les
	 * parts indicades amb el certificat de l'emisor (BinarySecurityToken, DirectReference) i
	 * després se signa el missatge. La resposta es desxifra amb la clau privada del requeridor i se'n
	 * valida la signatura amb WSS4J (el document de la resposta queda desxifrat).
	 *
	 * @param aliasEmisor alias del certificat de l'emisor al keystore de les proves.
	 * @param parts parts a xifrar en el format de WSS4J ({Content}{namespace}DatosEspecificos).
	 */
	public RespostaScsp enviarXifrat(String url, String accio, Element payload, String aliasEmisor, String parts) throws Exception {
		Merlin crypto = crearCrypto();
		Wss4jSecurityInterceptor interceptor = new Wss4jSecurityInterceptor();
		interceptor.setSecurementActions("Encrypt Signature");
		interceptor.setSecurementEncryptionUser(aliasEmisor);
		interceptor.setSecurementEncryptionCrypto(crypto);
		interceptor.setSecurementEncryptionParts(parts);
		interceptor.setSecurementEncryptionKeyIdentifier("DirectReference");
		interceptor.setSecurementSignatureKeyIdentifier("DirectReference");
		interceptor.setSecurementMustUnderstand(false);
		interceptor.setSecurementUsername(configuracio.getKeystoreAlias());
		interceptor.setSecurementPassword(configuracio.getKeystoreAliasPassword());
		interceptor.setSecurementSignatureCrypto(crypto);
		interceptor.setValidateResponse(true);
		interceptor.setValidationActions("Signature Encrypt");
		interceptor.setValidationSignatureCrypto(crypto);
		interceptor.setValidationDecryptionCrypto(crypto);
		KeyStoreCallbackHandler callbackHandler = new KeyStoreCallbackHandler();
		callbackHandler.setPrivateKeyPassword(configuracio.getKeystoreAliasPassword());
		interceptor.setValidationCallbackHandler(callbackHandler);
		interceptor.afterPropertiesSet();
		return enviar(url, accio, payload, interceptor);
	}

	/**
	 * Envia el missatge amb signatura XMLSignature (serveis amb tipus de seguretat XMLSignature),
	 * signat amb la mateixa classe que fan servir les llibreries SCSP 5 com a requeridor
	 * (es.scsp.common.signature.XMLDSigManager, cridada per ScspXmlSignature).
	 */
	public RespostaScsp enviarXmlSignature(String url, String accio, Element payload) throws Exception {
		org.apache.xml.security.Init.init();
		Merlin crypto = crearCrypto();
		KeyStore keyStore = crypto.getKeyStore();
		X509Certificate certificat = (X509Certificate)keyStore.getCertificate(configuracio.getKeystoreAlias());
		PrivateKey clau = (PrivateKey)keyStore.getKey(
				configuracio.getKeystoreAlias(),
				configuracio.getKeystoreAliasPassword().toCharArray());
		ClientInterceptor interceptor = new ClientInterceptorAdapter() {
			@Override
			public boolean handleRequest(MessageContext messageContext) {
				SoapMessage soapMessage = (SoapMessage)messageContext.getRequest();
				try {
					Document document = soapMessage.getDocument();
					soapMessage.setDocument(new XMLDSigManager().generateSignature(document, certificat, clau));
				} catch (Exception ex) {
					throw new IllegalStateException("No s'ha pogut signar la petició (XMLSignature)", ex);
				}
				return true;
			}
		};
		return enviar(url, accio, payload, interceptor);
	}

	private RespostaScsp enviar(String url, String accio, Element payload, ClientInterceptor interceptor) throws Exception {
		WebServiceTemplate template = new WebServiceTemplate(messageFactory);
		HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender();
		messageSender.setConnectionTimeout(configuracio.getTimeoutSegons() * 1000);
		messageSender.setReadTimeout(configuracio.getTimeoutSegons() * 1000);
		messageSender.afterPropertiesSet();
		template.setMessageSender(messageSender);
		if (interceptor != null) {
			template.setInterceptors(new ClientInterceptor[] {interceptor});
		}
		// Spring-WS només crida el FaultMessageResolver si la resposta és un SOAP Fault amb HTTP 500.
		// Igual que ScspSoapFaultMessageResolver, el Fault es llegeix abans de tancar la connexió.
		template.setFaultMessageResolver(message -> {
			throw new FaultHttp500Exception(toDocument(message), ((SoapMessage)message).getFaultReason());
		});
		SoapActionCallback soapActionCallback = new SoapActionCallback(accio);
		try {
			WebServiceMessageCallback requestCallback = message -> {
				try {
					TransformerFactory.newInstance().newTransformer().transform(
							new DOMSource(payload),
							message.getPayloadResult());
				} catch (TransformerException ex) {
					throw new IOException("No s'ha pogut afegir la petició al missatge SOAP", ex);
				}
				soapActionCallback.doWithMessage(message);
			};
			WebServiceMessageExtractor<RespostaScsp> responseExtractor = message -> RespostaScsp.resposta(toDocument(message), isFault(message));
			return template.sendAndReceive(url, requestCallback, responseExtractor);
		} catch (FaultHttp500Exception ex) {
			return RespostaScsp.faultHttp500(ex.document, ex.getMessage());
		}
	}

	private Merlin crearCrypto() throws Exception {
		KeyStore keyStore = KeyStore.getInstance(configuracio.getKeystoreType());
		try (InputStream is = Files.newInputStream(configuracio.getKeystorePath())) {
			keyStore.load(is, configuracio.getKeystorePassword().toCharArray());
		}
		Merlin crypto = new Merlin();
		crypto.setKeyStore(keyStore);
		return crypto;
	}

	private Wss4jSecurityInterceptor crearInterceptorSignatura() throws Exception {
		Merlin crypto = crearCrypto();
		Wss4jSecurityInterceptor interceptor = new Wss4jSecurityInterceptor();
		interceptor.setSecurementActions("Signature");
		interceptor.setSecurementSignatureKeyIdentifier("DirectReference");
		interceptor.setSecurementMustUnderstand(false);
		interceptor.setSecurementUsername(configuracio.getKeystoreAlias());
		interceptor.setSecurementPassword(configuracio.getKeystoreAliasPassword());
		interceptor.setSecurementSignatureCrypto(crypto);
		// La signatura de la resposta es valida a part (SignaturaRespostaHelper)
		interceptor.setValidateResponse(false);
		interceptor.afterPropertiesSet();
		return interceptor;
	}

	private static boolean isFault(WebServiceMessage message) {
		return message instanceof FaultAwareWebServiceMessage && ((FaultAwareWebServiceMessage)message).hasFault();
	}

	private static Document toDocument(WebServiceMessage message) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		message.writeTo(baos);
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			return dbf.newDocumentBuilder().parse(new ByteArrayInputStream(baos.toByteArray()));
		} catch (Exception ex) {
			throw new IOException("No s'ha pogut processar la resposta: " + baos, ex);
		}
	}

	private static class FaultHttp500Exception extends WebServiceClientException {
		private final transient Document document;
		FaultHttp500Exception(Document document, String faultString) {
			super(faultString);
			this.document = document;
		}
	}

	/** Resposta rebuda de l'emisor SCSP. */
	public static class RespostaScsp {

		private static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";

		private final Document document;
		private final boolean fault;
		private final boolean faultHttp500;
		private final String faultString;

		private RespostaScsp(Document document, boolean fault, boolean faultHttp500, String faultString) {
			this.document = document;
			this.fault = fault;
			this.faultHttp500 = faultHttp500;
			this.faultString = faultString;
		}

		static RespostaScsp resposta(Document document, boolean fault) {
			return new RespostaScsp(document, fault, false, null);
		}
		static RespostaScsp faultHttp500(Document document, String faultString) {
			return new RespostaScsp(document, true, true, faultString);
		}

		public Document getDocument() {
			return document;
		}
		/** Primer element del Body SOAP (Respuesta, ConfirmacionPeticion o Fault). */
		public Element getPayload() {
			Node body = document.getElementsByTagNameNS(SOAP_NS, "Body").item(0);
			for (Node node = body != null ? body.getFirstChild() : null; node != null; node = node.getNextSibling()) {
				if (node instanceof Element) {
					return (Element)node;
				}
			}
			return null;
		}
		public boolean isFault() {
			return fault;
		}
		/** Indica si el SOAP Fault s'ha rebut amb HTTP 500 (el client Spring-WS de PINBAL ho necessita). */
		public boolean isFaultHttp500() {
			return faultHttp500;
		}
		public String getFaultString() {
			return faultString;
		}
		/** Valor d'un element de la resposta (cerca pel nom local, com fa ScspSoapFaultMessageResolver). */
		public String getValor(String localName) {
			Node node = document.getElementsByTagNameNS("*", localName).item(0);
			return node != null ? node.getTextContent().trim() : null;
		}
		public String toXml() {
			try {
				StringWriter writer = new StringWriter();
				TransformerFactory.newInstance().newTransformer().transform(
						new DOMSource(document),
						new StreamResult(writer));
				return writer.toString();
			} catch (Exception ex) {
				return String.valueOf(document);
			}
		}

	}

}
