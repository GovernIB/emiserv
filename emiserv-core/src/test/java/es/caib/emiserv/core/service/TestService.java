/**
 * 
 */
package es.caib.emiserv.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.emiserv.core.api.service.ws.backoffice.DatosGenericos;
import es.caib.emiserv.core.api.service.ws.backoffice.EmiservBackoffice;
import es.caib.emiserv.core.api.service.ws.backoffice.SolicitudTransmision;
import es.caib.emiserv.core.entity.BackofficePeticioEntity;
import es.caib.emiserv.core.entity.ScspCorePeticionRespuestaEntity;
import es.caib.emiserv.core.entity.ScspCoreTokenDataEntity;
import es.caib.emiserv.core.entity.ScspCoreTransmisionEntity;
import es.caib.emiserv.core.entity.ScspCoreTransmisionEntity.Builder;
import es.caib.emiserv.core.helper.BackofficeHelper;
import es.caib.emiserv.core.repository.BackofficePeticioRepository;
import es.caib.emiserv.core.repository.BackofficeSolicitudRepository;
import es.caib.emiserv.core.repository.ScspCorePeticionRespuestaRepository;
import es.caib.emiserv.core.repository.ScspCoreTokenDataRepository;
import es.caib.emiserv.core.repository.ScspCoreTransmisionRepository;

/**
 * Service amb mètodes d'ajuda pels tests.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class TestService {

	@Autowired
	private BackofficeHelper backofficeHelper;

	@Autowired
	private ScspCorePeticionRespuestaRepository scspCorePeticionRespuestaRepository;
	@Autowired
	private ScspCoreTransmisionRepository scspCoreTransmisionRepository;
	@Autowired
	private ScspCoreTokenDataRepository scspCoreTokenDataRepository;
	@Autowired
	private BackofficePeticioRepository backofficePeticioRepository;
	@Autowired
	private BackofficeSolicitudRepository backofficeSolicitudRepository;



	@Transactional
	public ScspCorePeticionRespuestaEntity novaPeticionRespuesta(
			String serveiCodi,
			String peticionId) {
		ScspCorePeticionRespuestaEntity scspCorePeticionRespuesta = ScspCorePeticionRespuestaEntity.getBuilder(
				new Long(1),
				peticionId,
				"0002").build();
		return scspCorePeticionRespuestaRepository.saveAndFlush(scspCorePeticionRespuesta);
	}
	@Transactional
	public ScspCoreTransmisionEntity novaSolicitudTransmision(
			String serveiCodi,
			String peticionId,
			String transmisionId,
			SolicitudTransmision solicitud) {
		DatosGenericos datosGenericos = solicitud.getDatosGenericos();
		Builder builder = ScspCoreTransmisionEntity.getBuilder(
				serveiCodi,
				peticionId,
				transmisionId);
		if (datosGenericos.getSolicitante() != null) {
			builder.
			solicitanteId(datosGenericos.getSolicitante().getIdentificadorSolicitante()).
			solicitanteNombre(datosGenericos.getSolicitante().getNombreSolicitante()).
			unidadTramitadora(datosGenericos.getSolicitante().getUnidadTramitadora()).
			expediente(datosGenericos.getSolicitante().getIdExpediente()).
			finalidad(datosGenericos.getSolicitante().getFinalidad()).
			consentimiento(datosGenericos.getSolicitante().getConsentimiento().toString());
			if (datosGenericos.getSolicitante().getFuncionario() != null) {
				builder.
				funcionarioDocumento(datosGenericos.getSolicitante().getFuncionario().getNifFuncionario()).
				funcionarioNombre(datosGenericos.getSolicitante().getFuncionario().getNombreCompletoFuncionario());
			}
			if (datosGenericos.getSolicitante().getProcedimiento() != null) {
				builder.
				procedimientoCodigo(datosGenericos.getSolicitante().getProcedimiento().getCodProcedimiento()).
				procedimientoNombre(datosGenericos.getSolicitante().getProcedimiento().getNombreProcedimiento());
			}
		}
		if (datosGenericos.getTitular() != null) {
			builder.
			titularDocumento(datosGenericos.getTitular().getDocumentacion()).
			titularNombre(datosGenericos.getTitular().getNombre()).
			titularApellido1(datosGenericos.getTitular().getApellido1()).
			titularApellido2(datosGenericos.getTitular().getApellido2()).
			titularNombreCompleto(datosGenericos.getTitular().getNombreCompleto());
		}
		ScspCoreTransmisionEntity scspCoreTransmision = builder.build();
		return scspCoreTransmisionRepository.saveAndFlush(scspCoreTransmision);
	}
	@Transactional
	public ScspCoreTokenDataEntity nouTokenData(
			String peticionId,
			int tipoMensaje,
			String datos) {
		ScspCoreTokenDataEntity ScspCoreTokenData = ScspCoreTokenDataEntity.getBuilder(
				peticionId,
				tipoMensaje,
				datos).build();
		return scspCoreTokenDataRepository.saveAndFlush(ScspCoreTokenData);
	}
	@Transactional
	public void backofficeSolicitudDeleteAll() {
		backofficeSolicitudRepository.deleteAll();
	}
	@Transactional
	public void backofficePeticioDeleteAll() {
		backofficePeticioRepository.deleteAll();
	}

	public void configurarBackofficesPerTests(
			String xmlPeticio,
			String xmlResposta) {
		EmiservBackoffice testBackoffice = new EmiservBackofficeTest();
		backofficeHelper.configuracioTest(
				testBackoffice,
				xmlPeticio,
				xmlResposta);
	}
	@Transactional
	public void processarPeticioPendent(
			String peticioId) {
		ScspCorePeticionRespuestaEntity scspCorePeticionRespuesta = scspCorePeticionRespuestaRepository.findByPeticionId(
				peticioId);
		BackofficePeticioEntity peticio = backofficePeticioRepository.findByScspPeticionRespuesta(scspCorePeticionRespuesta);
		backofficeHelper.processarPeticioPendent(peticio.getId());
	}

}
