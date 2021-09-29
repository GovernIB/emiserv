/**
 * 
 */
package es.caib.emiserv.logic.service.ws;

import java.io.ByteArrayOutputStream;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import lombok.extern.slf4j.Slf4j;

/**
 * SOAP Handler per a imprimir l'XML de peticions i repostes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext> {

	public Set<QName> getHeaders() {
		return null;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		logToSystemOut(smc);
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		logToSystemOut(smc);
		return true;
	}

	public void close(MessageContext messageContext) {
	}

	private void logToSystemOut(SOAPMessageContext smc) {
		boolean processLog = log.isDebugEnabled();
		if (processLog) {
			StringBuilder sb = new StringBuilder();
			Boolean outboundProperty = (Boolean)smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outboundProperty.booleanValue())
				sb.append("Missatge sortint: ");
			else
				sb.append("Missatge entrant: ");
			SOAPMessage message = smc.getMessage();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				message.writeTo(baos);
				sb.append(baos.toString());
			} catch (Exception ex) {
				sb.append("Error al imprimir el missatge XML: " + ex.getMessage());
			}
			log.debug(sb.toString());
		}
	}

}
