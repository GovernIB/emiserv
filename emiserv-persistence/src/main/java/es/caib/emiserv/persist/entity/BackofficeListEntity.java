package es.caib.emiserv.persist.entity;

import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@Entity
@Subselect( "select distinct cpr.idpeticion     as peticioid, " +
            "        cs.codcertificado          as serveicodi, " +
            "        cs.descripcion             as serveidescripcio, " +
            "        cpr.estado                 as estatscsp, " +
            "        cpr.fechapeticion          as datapeticio, " +
            "        cpr.transmisionsincrona    as sincrona, " +
            "        cpr.numerotransmisiones    as numtransmissions, " +
            "        cpr.error                  as error, " +
            "        bp.estat                   as backofficeestat, " +
            "        bp.processades_total       as processadestotal, " +
            "        ct.codigoprocedimiento     as procedimentcodi, " +
            "        ct.nombreprocedimiento     as procedimentnom, " +
            "        case " +
            "            when (bp.id is not null and cpr.estado like '00%') then bp.estat " +
            "            when cpr.estado like '00%' then " +
            "                case " +
            "                    when cpr.estado = '0001' then 0 " +
            "                    when cpr.estado = '0002' then 1 " +
            "                    when cpr.estado = '0003' then 2 " +
            "                    when cpr.estado = '0004' then 3 " +
            "                end " +
            "            else 4 " +
            "        end                        as estat " +
            "       from core_peticion_respuesta cpr " +
            "            left outer join core_transmision ct on ct.idpeticion = cpr.idpeticion " +
            "            left outer join core_servicio cs on cpr.certificado = cs.id " +
            "            left outer join core_emisor_certificado ec on cs.emisor = ec.id " +
            "            left outer join ems_backoffice_pet bp on bp.peticio_id = cpr.idpeticion " +
            "       where 0 = (select count(idsolicitud) from core_transmision where idpeticion=cpr.idpeticion) or ct.idsolicitud = (select max(idsolicitud) from core_transmision where idpeticion=cpr.idpeticion)")
@Immutable
public class BackofficeListEntity {
    @Id
    @Column(name = "peticioid")
    private String peticioId;
    @Column(name = "serveicodi")
    private String serveiCodi;
    @Column(name = "serveidescripcio")
    private String serveiDescripcio;
    @Column(name = "estat")
    private PeticioEstatEnumDto estat;
    @Column(name = "estatscsp")
    private String estatScsp;
    @Column(name = "datapeticio")
    private Date dataPeticio;
    @Column(name = "sincrona")
    private boolean sincrona;
    @Column(name = "numtransmissions")
    private Integer numTransmissions;
    @Column(name = "error")
    private String error;
    @Column(name = "backofficeestat")
    private PeticioEstatEnumDto backofficeEstat;
    @Column(name = "processadestotal")
    private Integer processadesTotal;
    @Column(name = "procedimentcodi")
    private String procedimentCodi;
    @Column(name = "procedimentnom")
    private String procedimentNom;

//    public PeticioEstatEnumDto getEstat() {
//        PeticioEstatEnumDto estat = PeticioEstatEnumDto.ERROR;
//        if (estatScsp.startsWith("00")) {
//            if (estatScsp.equals("0001"))
//                estat = PeticioEstatEnumDto.PENDENT;
//            else if (estatScsp.equals("0002"))
//                estat = PeticioEstatEnumDto.EN_PROCES;
//            else if (estatScsp.equals("0003"))
//                estat = PeticioEstatEnumDto.TRAMITADA;
//            else if (estatScsp.equals("0004"))
//                estat = PeticioEstatEnumDto.POLLING;
//        }
//
//        if (backofficeEstat != null && !PeticioEstatEnumDto.ERROR.equals(estat)) {
//            estat = backofficeEstat;
//        }
//
//        return estat;
//    }

    public String getPRocedimentCodiNom() {
        if ((procedimentCodi == null || procedimentCodi.isBlank()) && (procedimentNom == null || procedimentNom.isBlank()))
            return null;
        if (procedimentCodi == null || procedimentCodi.isBlank())
            return procedimentNom;
        if ((procedimentNom == null || procedimentNom.isBlank()))
            return procedimentCodi;
        return procedimentCodi + " - " + procedimentNom;
    }

    public Integer getNumTransmissions() {
        if (numTransmissions == null)
            return 0;
        return numTransmissions;
    }

    public Integer getProcessadesTotal() {
        if (processadesTotal == null)
            return 0;
        return processadesTotal;
    }
}

