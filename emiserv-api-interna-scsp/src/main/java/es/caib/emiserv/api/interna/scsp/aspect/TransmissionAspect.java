package es.caib.emiserv.api.interna.scsp.aspect;

import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Procedimiento;
import es.scsp.bean.common.Respuesta;
import es.scsp.bean.common.SolicitudTransmision;
import es.scsp.bean.common.TipoDocumentacion;
import es.scsp.common.dao.PeticionRespuestaDao;
import es.scsp.common.dao.TransmisionDao;
import es.scsp.common.domain.core.PeticionRespuesta;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.utils.StaticContextSupport;
import es.scsp.common.utils.xpath.XpathManager;
import org.apache.axiom.om.OMElement;
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

import java.math.BigDecimal;
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

    @AfterReturning(pointcut = "execution(* es.scsp.modules.AlmacenarMensajeUpdate.invoke(..))", returning = "result")
    public void afegirDadesEstat(JoinPoint joinPoint, Object result) {
        System.out.println("[ASPJ] After returning AlmacenarMensajeUpdate");
        MessageContext messageCtx = (MessageContext)joinPoint.getArgs()[0];
        Object request = messageCtx.getProperty("CURRENT_OBJECT_CONTEXT");
        try {
            PeticionRespuestaDao dbPeticion = getBean(es.scsp.common.dao.PeticionRespuestaDao.class);
            String idPeticion = null;

            if (request == null) {
                OMElement idPeticionNode = getChildrenWithNameRecursive(messageCtx.getEnvelope(), "IdPeticion");
                if (idPeticionNode != null) {
                    idPeticion = idPeticionNode.getText();
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

    private static OMElement getChildrenWithNameRecursive(OMElement parent, String name) {
        XpathManager manager = new XpathManager();
        return manager.getChildrenWithNameRecursive(parent, name);
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

    private Integer getEstatBackoficeByPeticioId(SessionFactory sessionFactory, String peticioId) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.beginTransaction();
        SQLQuery query = session.createSQLQuery("SELECT estat FROM ems_backoffice_pet WHERE peticio_id = :peticioId");
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
        SQLQuery query = session.createSQLQuery("UPDATE core_peticion_respuesta " +
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
        SQLQuery query = session.createSQLQuery("UPDATE core_peticion_respuesta " +
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
