/**
 * 
 */
package es.caib.emiserv.persist.repository.scsp;

import es.caib.emiserv.client.comu.EstatTipus;
import es.caib.emiserv.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.emiserv.logic.intf.dto.CarregaDto;
import es.caib.emiserv.logic.intf.dto.EstadisticaDto;
import es.caib.emiserv.logic.intf.dto.InformeGeneralEstatDto;
import es.caib.emiserv.persist.entity.scsp.ScspCoreTransmisionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

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
			"		cs.emisor.nombre, " +
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
			"	cs.emisor.cif, " +
			"	cs.emisor.nombre " +
			"order by " +
			"	ct.solicitanteNombre, " +
			"	ct.procedimientoCodigo, " +
			"	cs.codigoCertificado "
		    )
	List<InformeGeneralEstatDto> informeGeneralEstat(
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi);

	/** Consulta les peticions als backoffices per a la consulta de dades obertes
	 *
	 * @param dataInici
	 * @param dataFi
	 * @return
	 */
	@Query( "select new es.caib.emiserv.client.dadesobertes.DadesObertesRespostaConsulta( " +
			"		ct.solicitanteCodigo, " +
			"		ct.solicitanteNombre, " +
			"		ct.solicitanteId, " +
			"		ct.unidadTramitadora, " +
			"		ct.procedimientoCodigo, " +
			"		ct.procedimientoNombre, " +
			"		cs.codigoCertificado, " +
			"		cs.descripcion, " +
			"		cs.emisor.nombre, " +
			"		cs.emisor.cif, " +
			"		ct.consentimiento, " +
			"		ct.finalidad, " +
			"		ct.titularTipoDoc, " +
			"		ct.solicitudId, " +
			"		cpr.fechaPeticion, " +
			"		'BACKOFFICE', " +
			"		cpr.estado " +
			"	) " +
			"	from " +
			"		ScspCoreTransmisionEntity ct " +
			"			inner join ct.peticionRespuesta cpr, " +
			"		ScspCoreServicioEntity cs " +
			"	where cpr.certificado = cs.id " +
			"    	and (:esNullEntitatNif = true or ct.solicitanteId = :entitatNif) " +
			"		and (:esNullProcedimentId = true or ct.procedimientoCodigo = :procedimentId) " +
			"		and (:esNullServeiCodi = true or cs.codigoCertificado = :serveiCodi) " +
			"		and (:esNullDataInici = true or cpr.fechaPeticion >= :dataInici) " +
			"		and (:esNullDataFi = true or cpr.fechaPeticion <= :dataFi) " +
			"order by " +
			"	cpr.fechaPeticion "
	)
	List<DadesObertesRespostaConsulta> findByOpendata(
			@Param("esNullEntitatNif") boolean esNullEntitatNif,
			@Param("entitatNif") String entitatNif,
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") String procedimentId,
			@Param("esNullServeiCodi") boolean esNullServeiCodi,
			@Param("serveiCodi") String serveiCodi,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi);

	@Query(	"select " +
			"	new es.caib.emiserv.logic.intf.dto.CarregaDto( " +
			"		count(ct.solicitudId), " +
			"		ct.solicitanteNombre, " +
			"		ct.solicitanteId, " +
			"		ct.unidadTramitadora, " +
			"		ct.procedimientoCodigo, " +
			"		ct.procedimientoNombre, " +
			"		cs.codigoCertificado, " +
			"		cs.descripcion, " +
			"		cs.emisor.cif) " +
			"	from " +
			"		ScspCoreTransmisionEntity ct " +
			"			inner join ct.peticionRespuesta cpr, " +
			"		ScspCoreServicioEntity cs " +
			"	where cpr.certificado = cs.id " +
			"	 	and cpr.fechaPeticion >= :dataInici " +
			"group by " +
			"		ct.solicitanteNombre, " +
			"		ct.solicitanteId, " +
			"		ct.unidadTramitadora, " +
			"		ct.procedimientoCodigo, " +
			"		ct.procedimientoNombre, " +
			"		cs.codigoCertificado, " +
			"		cs.descripcion, " +
			"		cs.emisor.cif " +
			"order by " +
			"	ct.solicitanteNombre, " +
			"	ct.unidadTramitadora, " +
			"	ct.procedimientoNombre, " +
			"	cs.descripcion")
	List<CarregaDto> findCarrega(@Param("dataInici") Date dataInici);

	@Query(	"select " +
			"	new es.caib.emiserv.logic.intf.dto.EstadisticaDto( " +
			"		count(ct.solicitudId), " +
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
			"		end) as error) " +
			"	from " +
			"		ScspCoreTransmisionEntity ct " +
			"			inner join ct.peticionRespuesta cpr, " +
			"		ScspCoreServicioEntity cs " +
			"	where cpr.certificado = cs.id " +
			"    	and ct.solicitanteId = :entitatNif " +
			"		and (:esNullProcedimentCodi = true or ct.procedimientoCodigo = :procedimentCodi) " +
			"		and (:esNullServeiCodi = true or cs.codigoCertificado = :serveiCodi) " +
			"		and (:esNullEstat = true " +
			"			or (:esPendent = true and cpr.estado = '0001') " +
			"			or (:esProcessant = true and (cpr.estado = '0002' or cpr.estado = '0004')) " +
			"			or (:esTramitada = true and cpr.estado = '0003') " +
			"			or (:esError = true and substring(cpr.estado,0,2) != '00')) " +
			"		and (:esNullDataInici = true or cpr.fechaPeticion >= :dataInici) " +
			"		and (:esNullDataFi = true or cpr.fechaPeticion <= :dataFi) " +
			"group by " +
			"		ct.solicitanteNombre, " +
			"		ct.solicitanteId, " +
			"		ct.unidadTramitadora, " +
			"		ct.procedimientoCodigo, " +
			"		ct.procedimientoNombre, " +
			"		cs.codigoCertificado, " +
			"		cs.descripcion, " +
			"		cs.emisor.cif " +
			"order by " +
			"	ct.solicitanteNombre, " +
			"	ct.unidadTramitadora, " +
			"	ct.procedimientoNombre, " +
			"	cs.descripcion")
	List<EstadisticaDto> findEstadistiques(
			@Param("entitatNif") String entitatNif,
			@Param("esNullProcedimentCodi") boolean esNullProcedimentCodi,
			@Param("procedimentCodi") String procedimentCodi,
			@Param("esNullServeiCodi") boolean esNullServeiCodi,
			@Param("serveiCodi") String serveiCodi,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("esPendent") boolean esPendent,
			@Param("esProcessant") boolean esProcessant,
			@Param("esTramitada") boolean esTramitada,
			@Param("esError") boolean esError,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi);
}
