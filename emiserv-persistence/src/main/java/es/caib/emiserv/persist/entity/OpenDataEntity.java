package es.caib.emiserv.persist.entity;

import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@Entity
@Subselect(
        "select rownum as id, " +
        "       solicitantCodi, " +
        "       solicitantNom, " +
        "       solicitantId, " +
        "       unitatTramitadora, " +
        "       procedimentCodi, " +
        "       procedimentNom, " +
        "       serveiCodi, " +
        "       serveiNom, " +
        "       emissorNom, " +
        "       emissorCodi, " +
        "       consentiment, " +
        "       finalitat, " +
        "       titularTipusDoc, " +
        "       solicitudId, " +
        "       dataPeticio, " +
        "       tipus, " +
        "       estat " +
        "from (select rs.solicitant_codi    as solicitantCodi, " +
        "             rs.solicitant_nom     as solicitantNom, " +
        "             rs.solicitant_id      as solicitantId, " +
        "             rs.unitat_tramitadora as unitatTramitadora, " +
        "             rs.procediment_codi   as procedimentCodi, " +
        "             rs.procediment_nom    as procedimentNom, " +
        "             rp.servei_codi        as serveiCodi, " +
        "             s.nom                 as serveiNom, " +
        "             ''                    as emissorNom, " +
        "             rp.emissor_codi       as emissorCodi, " +
        "             rs.consentiment       as consentiment, " +
        "             rs.finalitat          as finalitat, " +
        "             rs.titular_tipus_doc  as titularTipusDoc, " +
        "             rs.solicitud_id       as solicitudId, " +
        "             rp.data_peticio       as dataPeticio, " +
        "             s.tipus               as tipus, " +
        "             rp.estat              as estat " +
        "      from ems_redir_solicitud rs " +
        "               inner join ems_redir_peticio rp on rs.peticio_id = rp.id " +
        "               left outer join ems_servei s on rp.servei_codi = s.codi " +
        "      union " +
        "      select ct.codigosolicitante   as solicitantCodi, " +
        "             ct.nombresolicitante   as solicitantNom, " +
        "             ct.idsolicitante       as solicitantId, " +
        "             ct.unidadtramitadora   as unitatTramitadora, " +
        "             ct.codigoprocedimiento as procedimentCodi, " +
        "             ct.nombreprocedimiento as procedimentNom, " +
        "             cs.codcertificado      as serveiCodi, " +
        "             cs.descripcion         as serveiNom, " +
        "             ec.nombre              as emissorNom, " +
        "             ec.cif                 as emissorCodi, " +
        "             ct.consentimiento      as consentiment, " +
        "             ct.finalidad           as finalitat, " +
        "             ct.tipodoctitular      as titularTipusDoc, " +
        "             ct.idsolicitud         as solicitudId, " +
        "             cpr.fechapeticion      as dataPeticio, " +
        "             0                      as tipus, " +
        "             cpr.estado             as estat " +
        "      from core_transmision ct " +
        "               inner join core_peticion_respuesta cpr on ct.idpeticion = cpr.idpeticion " +
        "               left outer join core_servicio cs on cpr.certificado = cs.id " +
        "               left outer join core_emisor_certificado ec on cs.emisor = ec.id) " +
        "order by dataPeticio")
@Immutable
public class OpenDataEntity {
    @Id
    @Column
    private Long id;
    @Column
    private String solicitantCodi;
    @Column
    private String solicitantNom;
    @Column
    private String solicitantId;
    @Column
    private String unitatTramitadora;
    @Column
    private String procedimentCodi;
    @Column
    private String procedimentNom;
    @Column
    private String serveiCodi;
    @Column
    private String serveiNom;
    @Column
    private String emissorNom;
    @Column
    private String emissorCodi;
    @Column
    private String consentiment;
    @Column
    private String finalitat;
    @Column
    private String titularTipusDoc;
    @Column
    private String solicitudId;
    @Column
    private Date dataPeticio;
    @Column
    @Enumerated(EnumType.ORDINAL)
    private ServeiTipusEnumDto tipus;
    @Column
    private String estat;
}
