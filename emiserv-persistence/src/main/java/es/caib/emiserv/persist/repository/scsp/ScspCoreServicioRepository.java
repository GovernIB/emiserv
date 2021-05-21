/**
 * 
 */
package es.caib.emiserv.persist.repository.scsp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;

/**
 *  Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades de la taula SCSP CORE_SERVICIO.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCoreServicioRepository extends JpaRepository<ScspCoreServicioEntity, Long> {

	public ScspCoreServicioEntity findByCodigoCertificado(String codigoCertificado);
	
	public List<ScspCoreServicioEntity> findByCodigoCertificadoIn(List<String> codigosCertificado);

	@Query( value = "select id, cif, nombre, fechabaja from core_emisor_certificado",
			nativeQuery = true)
	public List<Object[]> findScspEmisorCertificadoAll();

	@Query( value = "select id, alias, nombre, numeroserie from core_clave_publica",
			nativeQuery = true)
	public List<Object[]> findScspClavePublicaAll();

	@Query( value = "select id, alias, nombre, numeroserie, password from core_clave_privada",
			nativeQuery = true)
	public List<Object[]> findScspClavePrivadaAll();

}
