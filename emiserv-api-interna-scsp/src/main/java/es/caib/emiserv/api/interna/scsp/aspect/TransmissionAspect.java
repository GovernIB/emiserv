package es.caib.emiserv.api.interna.scsp.aspect;

import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.SolicitudTransmision;
import es.scsp.bean.common.TipoDocumentacion;
import es.scsp.common.dao.TransmisionDao;
import es.scsp.common.utils.StaticContextSupport;
import org.apache.axis2.context.MessageContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import java.util.List;

@Aspect
public class TransmissionAspect {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(TransmissionAspect.class);

    @AfterReturning(pointcut = "execution(* es.scsp.modules.AlmacenarMensajeSave.invoke(..))", returning = "result")
    public void afegirDadesTransmissio(JoinPoint joinPoint, Object result) {
        System.out.println("[ASPJ] After returning AlmacenarMensajeSave");
        MessageContext messageCtx = (MessageContext)joinPoint.getArgs()[0];
        Object request = messageCtx.getProperty("CURRENT_OBJECT_CONTEXT");
        if (request instanceof Peticion) {
            System.out.println("[ASPJ] Petició");

            try {
                Peticion peticio = (Peticion) request;

                TransmisionDao dbTransmisiones = getBean(es.scsp.common.dao.TransmisionDao.class);
                SessionFactory sessionFactory = dbTransmisiones.getSessionFactory();

                String idPeticion = peticio.getAtributos().getIdPeticion();

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
                            interessatTipusDoc = tipoDocumentacion != null ? tipoDocumentacion.name() : null;
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
                }
            } catch (Exception ex) {
                log.error("[ASPJ] No s'han pogut actualitzar les dades de la transmissio", ex);
            }
        }
    }

    private String getEntitatCodiByCif(SessionFactory sessionFactory, String cif) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.beginTransaction();
        SQLQuery query = session.createSQLQuery("SELECT codi FROM ems_entitat WHERE cif = :cif");
        query.setParameter("cif", cif);
        String entitatCodi = (String) query.uniqueResult();
        tx.commit();
        return entitatCodi;
    }

    private void updateTransmissio(SessionFactory sessionFactory,
                                   String idSolicitud,
                                   String idPeticion,
                                   String idTransmision,
                                   String interessatTipusDoc,
                                   String entitatCodi) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.beginTransaction();
        SQLQuery query = session.createSQLQuery("UPDATE core_transmision " +
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
