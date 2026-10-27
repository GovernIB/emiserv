package es.caib.emiserv.api.interna.scsp.aspect;

import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.peticion.Procedimiento;
import es.scsp.bean.common.peticion.SolicitudTransmision;
import es.scsp.bean.common.peticion.TipoDocumentacion;
import es.scsp.bean.common.respuesta.Respuesta;
import es.scsp.common.dao.PeticionRespuestaDao;
import es.scsp.common.dao.TransmisionDao;
import es.scsp.common.domain.core.PeticionRespuesta;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.utils.StaticContextSupport;
import es.scsp.common.utils.xpath.XpathManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.util.List;

@Aspect
public class TransmissionAspect {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(TransmissionAspect.class);

    private static final String XPATH_ID_PETICION = "//*[local-name()='IdPeticion']";

    // A les llibreries SCSP 5 el registre de la petició el fa l'interceptor Spring-WS AlmacenarMensajeSave
    // (abans el mòdul Axis2 es.scsp.modules.AlmacenarMensajeSave)
    @AfterReturning(pointcut = "execution(* es.scsp.common.interceptors.AlmacenarMensajeSave.handleRequest(org.springframework.ws.context.MessageContext))", returning = "result")
    public void afegirDadesTransmissio(JoinPoint joinPoint, Object result) {
        System.out.println("[ASPJ] After returning AlmacenarMensajeSave.handleRequest");
        MessageContext messageCtx = (MessageContext)joinPoint.getArgs()[0];
        Object request = messageCtx.getProperty("CURRENT_OBJECT_CONTEXT");
        if (request instanceof Peticion) {
            System.out.println("[ASPJ] Petició");

            try {
                Peticion peticio = (Peticion) request;

                TransmisionDao dbTransmisiones = getBean(es.scsp.common.dao.TransmisionDao.class);
                PeticionRespuestaDao dbPeticionRespuesta = getBean(es.scsp.common.dao.PeticionRespuestaDao.class);
                SessionFactory sessionFactory = dbTransmisiones.getSessionFactory();

                String idPeticion = peticio.getAtributos().getIdPeticion();
                PeticionRespuesta peticionRespuesta = dbPeticionRespuesta.select(idPeticion);
                Procedimiento procedimiento = null;

                List<SolicitudTransmision> solicitudsTransmissio = peticio.getSolicitudes().getSolicitudTransmision();
                for (SolicitudTransmision solicitudTransmisio : solicitudsTransmissio) {

                    String interessatTipusDoc = null;
                    String entitatCodi = null;

                    // Obtenir identificadors de la transmissio
                    String idSolicitud = solicitudTransmisio.getDatosGenericos().getTransmision().getIdSolicitud();
                    String idTransmision = solicitudTransmisio.getDatosGenericos().getTransmision().getIdTransmision();

                    // Obtenir el tipus de document del titular
                    if (solicitudTransmisio.getDatosGenericos().getTitular() != null) {
                        TipoDocumentacion tipoDocumentacion = solicitudTransmisio.getDatosGenericos().getTitular().getTipoDocumentacion();
                        if (tipoDocumentacion != null) {
                            // value() retorna el valor de l'esquema (DNI, NIF, Pasaporte...), igual que el name() de les llibreries SCSP 4
                            interessatTipusDoc = tipoDocumentacion != null ? tipoDocumentacion.value() : null;
                        }
                    }

                    // Obtenir el codi d'entitat
                    if (solicitudTransmisio.getDatosGenericos().getSolicitante() != null) {
                        String cifEntitat = solicitudTransmisio.getDatosGenericos().getSolicitante().getIdentificadorSolicitante();
                        if (cifEntitat != null) {
                            entitatCodi = getEntitatCodiByCif(sessionFactory, cifEntitat);
                        }
                    }

                    // Actualitzar els valors de la transmissio
                    updateTransmissio(sessionFactory, idSolicitud, idPeticion, idTransmision, interessatTipusDoc, entitatCodi);

                    // Obtenir procediment
                    if (procedimiento == null && solicitudTransmisio.getDatosGenericos().getSolicitante() != null) {
                        procedimiento = solicitudTransmisio.getDatosGenericos().getSolicitante().getProcedimiento();
                    }
                }

                // Obtenir servei
                Servicio servicio = peticionRespuesta.getServicio();

                // Actualitzar els valors de la peticioResposta
                updateProcedimentServei(
                        sessionFactory,
                        idPeticion,
                        procedimiento != null ? procedimiento.getCodProcedimiento() : null,
                        procedimiento != null ? procedimiento.getNombreProcedimiento() : null,
                        servicio != null ? servicio.getCodCertificado() : null,
                        servicio != null ? servicio.getDescripcion() : null,
                        getPeticioEstat(peticionRespuesta.getEstado()));

            } catch (Exception ex) {
                log.error("[ASPJ] No s'han pogut actualitzar les dades de la transmissio", ex);
            }
        }
    }

    // A les llibreries SCSP 5 el registre de la resposta el fa l'interceptor Spring-WS AlmacenarMensajeSave
    // (abans el mòdul Axis2 es.scsp.modules.AlmacenarMensajeUpdate)
    @AfterReturning(pointcut = "execution(* es.scsp.common.interceptors.AlmacenarMensajeSave.handleResponse(org.springframework.ws.context.MessageContext))", returning = "result")
    public void afegirDadesEstatResposta(JoinPoint joinPoint, Object result) {
        System.out.println("[ASPJ] After returning AlmacenarMensajeSave.handleResponse");
        afegirDadesEstat((MessageContext)joinPoint.getArgs()[0]);
    }

    @AfterReturning(pointcut = "execution(* es.scsp.common.interceptors.AlmacenarMensajeSave.handleFault(org.springframework.ws.context.MessageContext, Object))", returning = "result")
    public void afegirDadesEstatFault(JoinPoint joinPoint, Object result) {
        System.out.println("[ASPJ] After returning AlmacenarMensajeSave.handleFault");
        afegirDadesEstat((MessageContext)joinPoint.getArgs()[0]);
    }

    private void afegirDadesEstat(MessageContext messageCtx) {
        Object request = messageCtx.getProperty("CURRENT_OBJECT_CONTEXT");
        try {
            PeticionRespuestaDao dbPeticion = getBean(es.scsp.common.dao.PeticionRespuestaDao.class);
            String idPeticion = null;

            if (request == null) {
                idPeticion = getIdPeticion(messageCtx.hasResponse() ? messageCtx.getResponse() : null);
                if (idPeticion == null || idPeticion.isBlank()) {
                    idPeticion = getIdPeticion(messageCtx.getRequest());
                }
            }
            if (request instanceof Respuesta) {
                Respuesta resposta = (Respuesta) request;
                idPeticion = resposta.getAtributos().getIdPeticion();
            }

            if (idPeticion != null && !"".equals(idPeticion)) {
                PeticionRespuesta peticionResposta = dbPeticion.select(idPeticion);
                Integer estatBackoffice = getEstatBackoficeByPeticioId(dbPeticion.getSessionFactory(), peticionResposta.getIdPeticion());
                PeticioEstatEnumDto estat = null;
                if (estatBackoffice != null && estatBackoffice != 4) {
                    estat = PeticioEstatEnumDto.values()[estatBackoffice];
                } else {
                    estat = getPeticioEstat(peticionResposta.getEstado());
                }
                updateEstat(dbPeticion.getSessionFactory(), peticionResposta.getIdPeticion(), estat);

            }
        } catch (Exception ex) {
            log.error("[ASPJ] No s'han pogut actualitzar les dades de la peticioResposta", ex);
        }
    }

    private static String getIdPeticion(WebServiceMessage message) {
        if (!(message instanceof SoapMessage)) {
            return null;
        }
        Element element = ((SoapMessage) message).getDocument().getDocumentElement();
        String idPeticion = new XpathManager().evalXPathExp(XPATH_ID_PETICION, element);
        return idPeticion != null ? idPeticion.trim() : null;
    }

    private String getEntitatCodiByCif(SessionFactory sessionFactory, String cif) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.beginTransaction();
        NativeQuery<?> query = session.createNativeQuery("SELECT codi FROM ems_entitat WHERE cif = :cif");
        query.setParameter("cif", cif);
        String entitatCodi = (String) query.uniqueResult();
        tx.commit();
        return entitatCodi;
    }

    private Integer getEstatBackoficeByPeticioId(SessionFactory sessionFactory, String peticioId) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.beginTransaction();
        NativeQuery<?> query = session.createNativeQuery("SELECT estat FROM ems_backoffice_pet WHERE peticio_id = :peticioId");
        query.setParameter("peticioId", peticioId);
        var estatBackoffice = query.uniqueResult();
        tx.commit();
        if (estatBackoffice instanceof BigDecimal) {
            return ((BigDecimal)estatBackoffice).intValue();
        } else {
            return (Integer)estatBackoffice;
        }
    }

    private void updateTransmissio(SessionFactory sessionFactory,
                                   String idSolicitud,
                                   String idPeticion,
                                   String idTransmision,
                                   String interessatTipusDoc,
                                   String entitatCodi) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.beginTransaction();
        NativeQuery<?> query = session.createNativeQuery("UPDATE core_transmision " +
                "SET codigosolicitante = :entitatCodi, tipodoctitular = :interessatTipusDoc " +
                "WHERE idsolicitud = :idSolicitud " +
                "AND idpeticion = :idPeticion " +
                "AND idtransmision " + (idTransmision == null ? "is null" : "= :idTransmision"));
        query.setParameter("entitatCodi", entitatCodi);
        query.setParameter("interessatTipusDoc", interessatTipusDoc);
        query.setParameter("idSolicitud", idSolicitud);
        query.setParameter("idPeticion", idPeticion);
        if (idTransmision != null) {
            query.setParameter("idTransmision", idTransmision);
        }
        int updated = query.executeUpdate();
        tx.commit();

        if (updated < 1) {
            log.error("[ASPJ] No s'ha actualitzat cap transmissio (idSol=" + idSolicitud + ", idPet=" + idPeticion + ", idTrans=" + idTransmision + ")");
        } else {
            log.debug("[ASPJ] Actualitzada transmissio (idSol=" + idSolicitud + ", idPet=" + idPeticion + ", idTrans=" + idTransmision + ", entCod=" + entitatCodi + ", tipDoc=" + interessatTipusDoc + ")");
        }
    }

    private PeticioEstatEnumDto getPeticioEstat(String estatTransmissio) {
        if (estatTransmissio != null) {
            switch (estatTransmissio) {
                case "0001":
                    return PeticioEstatEnumDto.PENDENT;
                case "0002":
                    return PeticioEstatEnumDto.EN_PROCES;
                case "0003":
                    return PeticioEstatEnumDto.TRAMITADA;
                case "0004":
                    return PeticioEstatEnumDto.POLLING;
            }
        }
        return PeticioEstatEnumDto.ERROR;
    }

    private void updateProcedimentServei(SessionFactory sessionFactory,
                                         String idPeticion,
                                         String codiProcediment,
                                         String nomProcediment,
                                         String codiServei,
                                         String nomServei,
                                         PeticioEstatEnumDto estat) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.beginTransaction();
        NativeQuery<?> query = session.createNativeQuery("UPDATE core_peticion_respuesta " +
                "SET emscodiprocediment = :codiProcediment, " +
                "   emsnomprocediment = :nomProcediment, " +
                "   emscodiservei = :codiServei, " +
                "   emsnomservei = :nomServei, " +
                "   emsestat = :estat " +
                "WHERE idpeticion = :idPeticion ");
        query.setParameter("codiProcediment", codiProcediment);
        query.setParameter("nomProcediment", nomProcediment);
        query.setParameter("codiServei", codiServei);
        query.setParameter("nomServei", nomServei);
        query.setParameter("estat", estat.ordinal());
        query.setParameter("idPeticion", idPeticion);
        int updated = query.executeUpdate();
        tx.commit();
    }

    private void updateEstat(SessionFactory sessionFactory,
                             String idPeticion,
                             PeticioEstatEnumDto estat) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.beginTransaction();
        NativeQuery<?> query = session.createNativeQuery("UPDATE core_peticion_respuesta " +
                "SET emsestat = :estat " +
                "WHERE idpeticion = :idPeticion ");
        query.setParameter("estat", estat.ordinal());
        query.setParameter("idPeticion", idPeticion);
        int updated = query.executeUpdate();
        tx.commit();
    }

    private <T> T getBean(Class<T> requiredType) {
        T bean = null;
        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        if (ctx == null) {
            ctx = StaticContextSupport.getContextInstance();
        }
        try {
            bean = ((ApplicationContext) ctx).getBean(requiredType);
        } catch (Exception ex) {
            log.error("No s'ha pogut carregar la classe " + requiredType.getName(), ex);
        }
        return bean;
    }

}
