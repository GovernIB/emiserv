/**
 *
 */
package es.caib.emiserv.backoffice;

import java.text.SimpleDateFormat;
import java.util.Date;

import es.scsp.bean.common.respuesta.Atributos;
import es.scsp.bean.common.respuesta.Consentimiento;
import es.scsp.bean.common.respuesta.DatosGenericos;
import es.scsp.bean.common.respuesta.Emisor;
import es.scsp.bean.common.respuesta.Estado;
import es.scsp.bean.common.respuesta.Funcionario;
import es.scsp.bean.common.respuesta.Procedimiento;
import es.scsp.bean.common.respuesta.Solicitante;
import es.scsp.bean.common.respuesta.TipoDocumentacion;
import es.scsp.bean.common.respuesta.Titular;
import es.scsp.bean.common.respuesta.Transmision;

/**
 * Mètodes comuns dels backoffices d'exemple per a construir les respostes
 * a partir de la informació de la petició.
 *
 * Amb les llibreries SCSP 5 cada missatge (petició, resposta, confirmació i
 * sol·licitud de resposta) té les seves pròpies classes i ja no és possible
 * reutilitzar els objectes de la petició a dins la resposta.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class TestBackofficeHelper {

	static Atributos toRespuesta(es.scsp.bean.common.peticion.Atributos atributosPeticion) {
		if (atributosPeticion == null) return null;
		Atributos atributos = new Atributos();
		atributos.setIdPeticion(atributosPeticion.getIdPeticion());
		atributos.setNumElementos(atributosPeticion.getNumElementos());
		atributos.setTimeStamp(atributosPeticion.getTimeStamp());
		atributos.setCodigoCertificado(atributosPeticion.getCodigoCertificado());
		return atributos;
	}

	static Atributos toRespuesta(es.scsp.bean.common.solicitud.Atributos atributosSolicitud) {
		if (atributosSolicitud == null) return null;
		Atributos atributos = new Atributos();
		atributos.setIdPeticion(atributosSolicitud.getIdPeticion());
		atributos.setNumElementos(atributosSolicitud.getNumElementos());
		atributos.setTimeStamp(atributosSolicitud.getTimeStamp());
		atributos.setCodigoCertificado(atributosSolicitud.getCodigoCertificado());
		return atributos;
	}

	static es.scsp.bean.common.confirmacion.Atributos toConfirmacion(es.scsp.bean.common.peticion.Atributos atributosPeticion) {
		if (atributosPeticion == null) return null;
		es.scsp.bean.common.confirmacion.Atributos atributos = new es.scsp.bean.common.confirmacion.Atributos();
		atributos.setIdPeticion(atributosPeticion.getIdPeticion());
		atributos.setNumElementos(atributosPeticion.getNumElementos());
		atributos.setTimeStamp(atributosPeticion.getTimeStamp());
		atributos.setCodigoCertificado(atributosPeticion.getCodigoCertificado());
		return atributos;
	}

	static Estado estado(
			String codigoEstado,
			String codigoEstadoSecundario,
			String literalError,
			Integer tiempoEstimadoRespuesta) {
		Estado estado = new Estado();
		estado.setCodigoEstado(codigoEstado);
		estado.setCodigoEstadoSecundario(codigoEstadoSecundario);
		estado.setLiteralError(literalError);
		estado.setTiempoEstimadoRespuesta(tiempoEstimadoRespuesta);
		return estado;
	}

	static DatosGenericos toRespuesta(es.scsp.bean.common.peticion.DatosGenericos datosGenericosPeticion) {
		if (datosGenericosPeticion == null) return null;
		DatosGenericos datosGenericos = new DatosGenericos();
		if (datosGenericosPeticion.getEmisor() != null) {
			Emisor emisor = new Emisor();
			emisor.setNifEmisor(datosGenericosPeticion.getEmisor().getNifEmisor());
			emisor.setNombreEmisor(datosGenericosPeticion.getEmisor().getNombreEmisor());
			datosGenericos.setEmisor(emisor);
		}
		es.scsp.bean.common.peticion.Solicitante solicitantePeticion = datosGenericosPeticion.getSolicitante();
		if (solicitantePeticion != null) {
			Solicitante solicitante = new Solicitante();
			solicitante.setIdentificadorSolicitante(solicitantePeticion.getIdentificadorSolicitante());
			solicitante.setNombreSolicitante(solicitantePeticion.getNombreSolicitante());
			solicitante.setUnidadTramitadora(solicitantePeticion.getUnidadTramitadora());
			solicitante.setCodigoUnidadTramitadora(solicitantePeticion.getCodigoUnidadTramitadora());
			if (solicitantePeticion.getProcedimiento() != null) {
				Procedimiento procedimiento = new Procedimiento();
				procedimiento.setCodProcedimiento(solicitantePeticion.getProcedimiento().getCodProcedimiento());
				procedimiento.setNombreProcedimiento(solicitantePeticion.getProcedimiento().getNombreProcedimiento());
				procedimiento.setAutomatizado(solicitantePeticion.getProcedimiento().getAutomatizado());
				procedimiento.setClaseTramite(solicitantePeticion.getProcedimiento().getClaseTramite());
				solicitante.setProcedimiento(procedimiento);
			}
			solicitante.setFinalidad(solicitantePeticion.getFinalidad());
			if (solicitantePeticion.getConsentimiento() != null) {
				solicitante.setConsentimiento(Consentimiento.fromValue(solicitantePeticion.getConsentimiento().value()));
			}
			if (solicitantePeticion.getFuncionario() != null) {
				Funcionario funcionario = new Funcionario();
				funcionario.setNombreCompletoFuncionario(solicitantePeticion.getFuncionario().getNombreCompletoFuncionario());
				funcionario.setNifFuncionario(solicitantePeticion.getFuncionario().getNifFuncionario());
				funcionario.setSeudonimoEmpleadoPublico(solicitantePeticion.getFuncionario().getSeudonimoEmpleadoPublico());
				solicitante.setFuncionario(funcionario);
			}
			solicitante.setIdExpediente(solicitantePeticion.getIdExpediente());
			datosGenericos.setSolicitante(solicitante);
		}
		es.scsp.bean.common.peticion.Titular titularPeticion = datosGenericosPeticion.getTitular();
		if (titularPeticion != null) {
			Titular titular = new Titular();
			if (titularPeticion.getTipoDocumentacion() != null) {
				titular.setTipoDocumentacion(TipoDocumentacion.fromValue(titularPeticion.getTipoDocumentacion().value()));
			}
			titular.setDocumentacion(titularPeticion.getDocumentacion());
			titular.setNombreCompleto(titularPeticion.getNombreCompleto());
			titular.setNombre(titularPeticion.getNombre());
			titular.setApellido1(titularPeticion.getApellido1());
			titular.setApellido2(titularPeticion.getApellido2());
			datosGenericos.setTitular(titular);
		}
		if (datosGenericosPeticion.getTransmision() != null) {
			Transmision transmision = new Transmision();
			transmision.setCodigoCertificado(datosGenericosPeticion.getTransmision().getCodigoCertificado());
			transmision.setIdSolicitud(datosGenericosPeticion.getTransmision().getIdSolicitud());
			transmision.setIdTransmision(datosGenericosPeticion.getTransmision().getIdTransmision());
			transmision.setFechaGeneracion(datosGenericosPeticion.getTransmision().getFechaGeneracion());
			datosGenericos.setTransmision(transmision);
		}
		return datosGenericos;
	}

	static String getIdentificacioDelsAtributsScsp(String codigoCertificado, String idPeticion) {
		return "(codigoCertificado=" + (codigoCertificado != null ? codigoCertificado : "???") +
				", idPeticion=" + (idPeticion != null ? idPeticion : "???") + ")";
	}

	static String getIdentificacioDelsAtributsScsp(es.scsp.bean.common.peticion.Atributos atributos) {
		if (atributos == null) return getIdentificacioDelsAtributsScsp(null, null);
		return getIdentificacioDelsAtributsScsp(atributos.getCodigoCertificado(), atributos.getIdPeticion());
	}

	static String getIdentificacioDelsAtributsScsp(es.scsp.bean.common.solicitud.Atributos atributos) {
		if (atributos == null) return getIdentificacioDelsAtributsScsp(null, null);
		return getIdentificacioDelsAtributsScsp(atributos.getCodigoCertificado(), atributos.getIdPeticion());
	}

	static String generateSemilla() {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("ddMMHHss");
		String fechaString = formatoDeFecha.format(new Date());
		return "T" + fechaString;
	}

}
