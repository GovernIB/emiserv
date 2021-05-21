/**
 * 
 */
package es.caib.emiserv.logic.service.ws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SOAP Handler per a imprimir l'XML de peticions i repostes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PeticioRespostaHandler implements SOAPHandler<SOAPMessageContext> {

	private ByteArrayOutputStream xmlPeticio;
	private ByteArrayOutputStream xmlResposta;

	public PeticioRespostaHandler(
			ByteArrayOutputStream xmlPeticio,
			ByteArrayOutputStream xmlResposta) {
		super();
		this.xmlPeticio = xmlPeticio;
		this.xmlResposta = xmlResposta;
	}

	public Set<QName> getHeaders() {
		return null;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		logToByteArray(smc);
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		logToByteArray(smc);
		return true;
	}

	public void close(MessageContext messageContext) {
	}

	public void actualitzarXmlMock(
			byte[] xmlPeticio,
			byte[] xmlResposta) {
		if (xmlPeticio != null) {
			try {
				this.xmlPeticio.write(xmlPeticio);
			} catch (IOException ex) {
				LOGGER.error("Error al actualitzar l'XML de la petició", ex);
			}
		}
		if (xmlResposta != null) {
			try {
				this.xmlResposta.write(xmlResposta);
			} catch (IOException ex) {
				LOGGER.error("Error al actualitzar l'XML de la resposta", ex);
			}
		}
	}

	private void logToByteArray(SOAPMessageContext smc) {
		Boolean outboundProperty = (Boolean)smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		ByteArrayOutputStream baos = (outboundProperty.booleanValue()) ? xmlPeticio : xmlResposta;
		if (baos != null) {
			SOAPMessage message = smc.getMessage();
			try {
				message.writeTo(baos);
			} catch (Exception ex) {
				String missatgeTipus = (outboundProperty.booleanValue()) ? "petició" : "resposta";
				LOGGER.error("No s'ha pogut obtenir el codi XML de la " + missatgeTipus, ex);
			}
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(PeticioRespostaHandler.class);

}
