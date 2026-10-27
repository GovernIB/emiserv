package es.caib.emiserv.api.interna.scsp.pinbal;

import org.apache.wss4j.common.crypto.Merlin;
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
		WebServiceTemplate template = new WebServiceTemplate(messageFactory);
		HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender();
		messageSender.setConnectionTimeout(configuracio.getTimeoutSegons() * 1000);
		messageSender.setReadTimeout(configuracio.getTimeoutSegons() * 1000);
		messageSender.afterPropertiesSet();
		template.setMessageSender(messageSender);
		if (signar) {
			template.setInterceptors(new ClientInterceptor[] {crearInterceptorSignatura()});
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

	private Wss4jSecurityInterceptor crearInterceptorSignatura() throws Exception {
		KeyStore keyStore = KeyStore.getInstance(configuracio.getKeystoreType());
		try (InputStream is = Files.newInputStream(configuracio.getKeystorePath())) {
			keyStore.load(is, configuracio.getKeystorePassword().toCharArray());
		}
		Merlin crypto = new Merlin();
		crypto.setKeyStore(keyStore);
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
