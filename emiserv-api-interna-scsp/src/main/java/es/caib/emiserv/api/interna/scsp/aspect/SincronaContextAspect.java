package es.caib.emiserv.api.interna.scsp.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.springframework.ws.context.MessageContext;

/**
 * Corregeix una incompatibilitat de les llibreries SCSP 5.0.12:
 *
 * L'endpoint de l'emisor (es.scsp.emisor.ws.impl.EmisorServiceImpl del war scsp-emisor) guarda
 * la propietat "sincrona" del MessageContext com a Long, però els interceptors de scsp-core
 * (ScspWssInterceptor, AlmacenarMensajeSave, ...) la llegeixen com a Integer. Sense aquesta
 * correcció la signatura WS-Security de totes les respostes falla amb un ClassCastException
 * i el requeridor (PINBAL) rep un SOAP Fault 0306 "Error al generar la firma del mensaje".
 *
 * Abans d'executar el flux de resposta de l'emisor es converteix el valor a Integer.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Aspect
public class SincronaContextAspect {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(SincronaContextAspect.class);

    private static final String PROPIETAT_SINCRONA = "sincrona";

    @Before("execution(* es.scsp.common.interceptors.EmisorFlowWrapper.handleResponse(org.springframework.ws.context.MessageContext, ..)) || " +
            "execution(* es.scsp.common.interceptors.EmisorFlowWrapper.handleFault(org.springframework.ws.context.MessageContext, ..))")
    public void normalitzarSincrona(JoinPoint joinPoint) {
        MessageContext messageCtx = (MessageContext)joinPoint.getArgs()[0];
        Object sincrona = messageCtx.getProperty(PROPIETAT_SINCRONA);
        if (sincrona instanceof Number && !(sincrona instanceof Integer)) {
            log.debug("Es converteix la propietat {} del MessageContext de {} a Integer", PROPIETAT_SINCRONA, sincrona.getClass().getName());
            messageCtx.setProperty(PROPIETAT_SINCRONA, ((Number)sincrona).intValue());
        }
    }

}
