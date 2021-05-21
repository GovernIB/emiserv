/**
 * 
 */
package es.caib.emiserv.persist.repository.scsp;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.emiserv.logic.intf.dto.InformeGeneralEstatDto;
import es.caib.emiserv.persist.entity.scsp.ScspCoreTransmisionEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades de la taula SCSP CORE_TRANSMISION.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCoreTransmisionRepository extends JpaRepository<ScspCoreTransmisionEntity, String> {

	ScspCoreTransmisionEntity findByPeticionIdAndSolicitudId(
			String peticionId,
			String solicitudId);

	List<ScspCoreTransmisionEntity> findByPeticionIdOrderBySolicitudIdAsc(
			String peticionId);

	@Query(	"select " +
			"    distinct sct.procedimientoCodigo, " +
			"    sct.procedimientoNombre " +
			"from " +
			"    ScspCoreTransmisionEntity sct " +
			"where " +
			"    sct.peticionRespuesta.servicio.codigoCertificado in (:codisServeisPermesos)")
	List<Object[]> findProcedimentDistintPerServeisPermesos(
			@Param("codisServeisPermesos") List<String> codisServeisPermesos);

	/** Consulta les peticions als backoffices per a l'informe general d'estat
	 * 
	 * @param dataInici
	 * @param dataFi
	 * @return
	 */
	@Query( "select new es.caib.emiserv.logic.intf.dto.InformeGeneralEstatDto( " +
			"		ct.solicitanteNombre, " +
			"		ct.solicitanteId, " +
			"		ct.unidadTramitadora, " +
			"		ct.procedimientoCodigo, " +
			"		ct.procedimientoNombre, " +
			"		cs.codigoCertificado, " +
			"		cs.descripcion, " +
			"		cs.emisor.cif, " +
			"		sum(case " +
			"			when substring(cpr.estado,0,2) = '00' then 1 " +	
			"			else 0 " +	
			"		end) as correcte, " +
			"		sum(case " +
			"			when substring(cpr.estado,0,2) = '00' then 0 " +	
			"			else 1 " +	
			"		end) as error " +
			"	) " +
			"	from " +
			"		ScspCoreTransmisionEntity ct " + 
			"			inner join ct.peticionRespuesta cpr, " + 
			"		ScspCoreServicioEntity cs " +
			"	where cpr.certificado = cs.id " +
			"		and cpr.fechaPeticion >= :dataInici " +
			"		and cpr.fechaPeticion < :dataFi " +
			"group by " + 
			"	ct.solicitanteNombre, " +
			"	ct.solicitanteId, " +
			"	ct.unidadTramitadora, " +
			"	ct.procedimientoCodigo, " +
			"	ct.procedimientoNombre, " +
			"	cs.codigoCertificado, " +
			"	cs.descripcion, " + 
			"	cs.emisor.cif " +
			"order by " +
			"	ct.solicitanteNombre, " +
			"	ct.procedimientoCodigo, " +
			"	cs.codigoCertificado "
		    )
	List<InformeGeneralEstatDto> informeGeneralEstat(
			@Param("dataInici") Date dataInici, 
			@Param("dataFi") Date dataFi);
}
