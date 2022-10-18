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
@Subselect( "select distinct rp.id      as id, " +
        "        rp.peticio_id          as peticioid, " +
        "        rp.servei_codi         as serveicodi, " +
        "        s.nom                  as serveidescripcio, " +
        "        rp.data_peticio        as datapeticio, " +
        "        rp.sincrona            as sincrona, " +
        "        rp.num_transmissions   as numtransmissions, " +
        "        rp.estat               as estatscsp, " +
        "        to_char(rp.error)      as error, " +
        "        rs.procediment_codi    as procedimentcodi, " +
        "        rs.procediment_nom     as procedimentnom, " +
        "        case " +
        "            when rp.estat like '00%' then " +
        "                case " +
        "                    when rp.estat = '0001' then 0 " +
        "                    when rp.estat = '0002' then 1 " +
        "                    when rp.estat = '0003' then 2 " +
        "                    when rp.estat = '0004' then 3 " +
        "                end " +
        "            else 4 " +
        "        end as estat " +
        "       from ems_redir_peticio rp " +
        "            left outer join ems_redir_solicitud rs on rs.peticio_id = rp.id " +
        "            left outer join ems_servei s on rp.servei_codi = s.codi " +
        "       where rs.id = (select max(id) from ems_redir_solicitud where peticio_id=rp.id)")
@Immutable
public class RedireccioListEntity {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "peticioid")
    private String peticioId;
    @Column(name = "serveicodi")
    private String serveiCodi;
    @Column(name = "serveidescripcio")
    private String serveiDescripcio;
    @Column(name = "estatscsp")
    private String estatScsp;
    @Column(name = "estat")
    private PeticioEstatEnumDto estat;
    @Column(name = "datapeticio")
    private Date dataPeticio;
    @Column(name = "sincrona")
    private boolean sincrona;
    @Column(name = "numtransmissions")
    private Integer numTransmissions;
//    @Lob
    @Column(name = "error")
    private String error;
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

}

