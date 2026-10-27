/**
 *
 */
package es.caib.emiserv.backoffice;

import java.util.ArrayList;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import es.caib.emiserv.logic.intf.service.BackofficeService;
import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.peticion.SolicitudTransmision;
import es.scsp.bean.common.respuesta.Consentimiento;
import es.scsp.bean.common.respuesta.DatosGenericos;
import es.scsp.bean.common.respuesta.Emisor;
import es.scsp.bean.common.respuesta.Funcionario;
import es.scsp.bean.common.respuesta.Procedimiento;
import es.scsp.bean.common.respuesta.Respuesta;
import es.scsp.bean.common.respuesta.Solicitante;
import es.scsp.bean.common.respuesta.TipoDocumentacion;
import es.scsp.bean.common.respuesta.Titular;
import es.scsp.bean.common.respuesta.Transmision;
import es.scsp.bean.common.respuesta.TransmisionDatos;
import es.scsp.bean.common.respuesta.Transmisiones;
import es.scsp.bean.common.solicitud.SolicitudRespuesta;
import es.scsp.common.backoffice.BackOffice;
import es.scsp.common.exceptions.ScspException;
import es.scsp.emiserv.backoffice.BackofficeServiceEjbLocator;
import lombok.extern.slf4j.Slf4j;

/**
 * Backoffice genèric per a accedir als backoffices emissors
 * desplegats a la CAIB.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class CaibBackoffice implements BackOffice {

	public Respuesta NotificarSincrono(
			Peticion peticion) throws ScspException {
		if (log.isDebugEnabled()) {
			log.debug("Processant petició síncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
		}
		try {
			es.caib.emiserv.logic.intf.service.ws.backoffice.Respuesta respuestaEmiserv = getBackofficeService().peticioBackofficeSincrona(
					peticionFromScspToEmiserv(peticion));
			Respuesta respuestaScsp = respuestaFromEmiservToScsp(respuestaEmiserv);
			if (log.isDebugEnabled()) {
				log.debug("Retornant resposta a petició síncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
				log.debug(
						"Contingut de la resposta que s'envia a les llibreries SCSP: " +
						ReflectionToStringBuilder.toString(
								respuestaScsp,
								new RecursiveToStringStyle()) + ".");
			}
			return respuestaScsp;
		} catch (Exception ex) {
			log.error("Error al invocar el mètode peticionSincrona del backoffice per a la petició " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()), ex);
			throw ScspException.getScspException(
					ex,
					"0227",
					new String[] {
							ex.getCause().getClass().getSimpleName() + ": " + ex.getCause().getMessage()
					});
		}
	}

	public ConfirmacionPeticion NotificarAsincrono(
			Peticion peticion) throws ScspException {
		if (log.isDebugEnabled()) {
			log.debug("Processant petició asíncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
		}
		try {
			es.caib.emiserv.logic.intf.service.ws.backoffice.ConfirmacionPeticion confirmacionPeticionEmiserv = getBackofficeService().peticioBackofficeAsincrona(
					peticionFromScspToEmiserv(peticion));
			ConfirmacionPeticion confirmacionPeticionScsp = confirmacionPeticionFromEmiservToScsp(confirmacionPeticionEmiserv);
			if (log.isDebugEnabled()) {
				log.debug("Retornant resposta a petició asíncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
				log.debug(
						"Contingut de la resposta que s'envia a les llibreries SCSP: " +
						ReflectionToStringBuilder.toString(
								confirmacionPeticionScsp,
								new RecursiveToStringStyle()) + ".");
			}
			return confirmacionPeticionScsp;
		} catch (Exception ex) {
			log.error("Error al invocar el mètode peticionAsincrona del backoffice per a la petició " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()), ex);
			throw ScspException.getScspException(
					ex,
					"0227",
					new String[] {
							ex.getCause().getClass().getSimpleName() + ": " + ex.getCause().getMessage()
					});
		}
	}

	public Respuesta SolicitudRespuesta(
			SolicitudRespuesta solicitudRespuesta) throws ScspException {
		if (log.isDebugEnabled()) {
			log.debug("Processant sol·licitud de resposta " + getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()) + ".");
		}
		try {
			es.caib.emiserv.logic.intf.service.ws.backoffice.Respuesta respuestaEmiserv = getBackofficeService().peticioBackofficeSolicitudRespuesta(
					solicitudRespuestaFromScspToEmiserv(solicitudRespuesta));
			Respuesta respuestaScsp = respuestaFromEmiservToScsp(respuestaEmiserv);
			if (log.isDebugEnabled()) {
				log.debug("Retornant resposta a sol·licitud de resposta " + getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()) + ".");
				log.debug(
						"Contingut de la resposta que s'envia a les llibreries SCSP: " +
						ReflectionToStringBuilder.toString(
								respuestaScsp,
								new RecursiveToStringStyle()) + ".");
			}
			return respuestaScsp;
		} catch (Exception ex) {
			log.error("Error al invocar el mètode solicitarRespuesta del backoffice per a la petició " + getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()), ex);
			throw ScspException.getScspException(
					ex,
					"0227",
					new String[] {
							ex.getCause().getClass().getSimpleName() + ": " + ex.getCause().getMessage()
					});
		}
	}

	private String getIdentificacioDelsAtributsScsp(es.scsp.bean.common.peticion.Atributos atributos) {
		if (atributos == null) return getIdentificacioDelsAtributsScsp(null, null);
		return getIdentificacioDelsAtributsScsp(atributos.getCodigoCertificado(), atributos.getIdPeticion());
	}

	private String getIdentificacioDelsAtributsScsp(es.scsp.bean.common.solicitud.Atributos atributos) {
		if (atributos == null) return getIdentificacioDelsAtributsScsp(null, null);
		return getIdentificacioDelsAtributsScsp(atributos.getCodigoCertificado(), atributos.getIdPeticion());
	}

	private String getIdentificacioDelsAtributsScsp(String codigoCertificado, String idPeticion) {
		return "(codigoCertificado=" + (codigoCertificado != null ? codigoCertificado : "???") +
				", idPeticion=" + (idPeticion != null ? idPeticion : "???") + ")";
	}

	public BackofficeService getBackofficeService() {
		return BackofficeServiceEjbLocator.getBackofficeService();
	}

	private es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion peticionFromScspToEmiserv(Peticion peticion) {
		if (peticion == null) return null;
		es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion peticionEmiserv = new es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion();
		es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos atributosEmiserv = null;
		if (peticion.getAtributos() != null) {
			atributosEmiserv = atributosFromScspToEmiserv(
					peticion.getAtributos().getCodigoCertificado(),
					peticion.getAtributos().getIdPeticion(),
					peticion.getAtributos().getNumElementos(),
					peticion.getAtributos().getTimeStamp());
			if (peticion.getAtributos().getEstado() != null) {
				atributosEmiserv.setEstado(estadoFromScspToEmiserv(
						peticion.getAtributos().getEstado().getCodigoEstado(),
						peticion.getAtributos().getEstado().getCodigoEstadoSecundario(),
						peticion.getAtributos().getEstado().getLiteralError(),
						peticion.getAtributos().getEstado().getTiempoEstimadoRespuesta()));
			}
		}
		peticionEmiserv.setAtributos(atributosEmiserv);
		es.caib.emiserv.logic.intf.service.ws.backoffice.Solicitudes solicitudesEmiserv = null;
		if (peticion.getSolicitudes() != null) {
			solicitudesEmiserv = new es.caib.emiserv.logic.intf.service.ws.backoffice.Solicitudes();
			ArrayList<es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudTransmision> solicitudesTransmisionEmiserv = null;
			if (peticion.getSolicitudes().getSolicitudTransmision() != null) {
				solicitudesTransmisionEmiserv = new ArrayList<es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudTransmision>();
				for (SolicitudTransmision solicitudTransmision: peticion.getSolicitudes().getSolicitudTransmision()) {
					es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudTransmision solicitudTransmisionEmiserv = new es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudTransmision();
					es.caib.emiserv.logic.intf.service.ws.backoffice.DatosGenericos datosGenericosEmiserv = null;
					if (solicitudTransmision.getDatosGenericos() != null) {
						datosGenericosEmiserv = new es.caib.emiserv.logic.intf.service.ws.backoffice.DatosGenericos();
						es.caib.emiserv.logic.intf.service.ws.backoffice.Emisor emisorEmiserv = null;
						if (solicitudTransmision.getDatosGenericos().getEmisor() != null) {
							emisorEmiserv = new es.caib.emiserv.logic.intf.service.ws.backoffice.Emisor();
							emisorEmiserv.setNifEmisor(solicitudTransmision.getDatosGenericos().getEmisor().getNifEmisor());
							emisorEmiserv.setNombreEmisor(solicitudTransmision.getDatosGenericos().getEmisor().getNombreEmisor());
						}
						datosGenericosEmiserv.setEmisor(emisorEmiserv);
						es.caib.emiserv.logic.intf.service.ws.backoffice.Solicitante solicitanteEmiserv = null;
						if (solicitudTransmision.getDatosGenericos().getSolicitante() != null) {
							solicitanteEmiserv = new es.caib.emiserv.logic.intf.service.ws.backoffice.Solicitante();
							if (solicitudTransmision.getDatosGenericos().getSolicitante().getConsentimiento() != null) {
								switch (solicitudTransmision.getDatosGenericos().getSolicitante().getConsentimiento()) {
								case LEY:
									solicitanteEmiserv.setConsentimiento(
											es.caib.emiserv.logic.intf.service.ws.backoffice.Consentimiento.Ley);
									break;
								case SI:
									solicitanteEmiserv.setConsentimiento(
											es.caib.emiserv.logic.intf.service.ws.backoffice.Consentimiento.Si);
									break;
								case NO_OPO:
									solicitanteEmiserv.setConsentimiento(
											es.caib.emiserv.logic.intf.service.ws.backoffice.Consentimiento.NoOpo);
									break;
								default:
									solicitanteEmiserv.setConsentimiento(null);
									break;
								}
							}
							solicitanteEmiserv.setFinalidad(
									solicitudTransmision.getDatosGenericos().getSolicitante().getFinalidad());
							es.caib.emiserv.logic.intf.service.ws.backoffice.Funcionario funcionarioEmiserv = null;
							if (solicitudTransmision.getDatosGenericos().getSolicitante().getFuncionario() != null) {
								funcionarioEmiserv = new es.caib.emiserv.logic.intf.service.ws.backoffice.Funcionario();
								funcionarioEmiserv.setNombreCompletoFuncionario(
										solicitudTransmision.getDatosGenericos().getSolicitante().getFuncionario().getNombreCompletoFuncionario());
								funcionarioEmiserv.setNifFuncionario(
										solicitudTransmision.getDatosGenericos().getSolicitante().getFuncionario().getNifFuncionario());
								// funcionarioEmiserv.setSeudonimo(
								//		solicitudTransmision.getDatosGenericos().getSolicitante().getFuncionario().getSeudonimoEmpleadoPublico()); // TODO
							}
							solicitanteEmiserv.setFuncionario(funcionarioEmiserv);
							solicitanteEmiserv.setNombreSolicitante(
									solicitudTransmision.getDatosGenericos().getSolicitante().getNombreSolicitante());
							solicitanteEmiserv.setIdentificadorSolicitante(
									solicitudTransmision.getDatosGenericos().getSolicitante().getIdentificadorSolicitante());
							solicitanteEmiserv.setIdExpediente(
									solicitudTransmision.getDatosGenericos().getSolicitante().getIdExpediente());
							es.caib.emiserv.logic.intf.service.ws.backoffice.Procedimiento procedimientoEmiserv = null;
							if (solicitudTransmision.getDatosGenericos().getSolicitante().getProcedimiento() != null) {
								procedimientoEmiserv = new es.caib.emiserv.logic.intf.service.ws.backoffice.Procedimiento();
								procedimientoEmiserv.setCodProcedimiento(
										solicitudTransmision.getDatosGenericos().getSolicitante().getProcedimiento().getCodProcedimiento());
								procedimientoEmiserv.setNombreProcedimiento(
										solicitudTransmision.getDatosGenericos().getSolicitante().getProcedimiento().getNombreProcedimiento());
							}
							solicitanteEmiserv.setProcedimiento(procedimientoEmiserv);
							solicitanteEmiserv.setUnidadTramitadora(
									solicitudTransmision.getDatosGenericos().getSolicitante().getUnidadTramitadora());
							//solicitanteEmiserv.setCodigoUnidadTramitadora(
							//		solicitudTransmision.getDatosGenericos().getSolicitante().getCodigoUnidadTramitadora()); // TODO
						}
						datosGenericosEmiserv.setSolicitante(solicitanteEmiserv);
						es.caib.emiserv.logic.intf.service.ws.backoffice.Titular titularEmiserv = null;
						if (solicitudTransmision.getDatosGenericos().getTitular() != null) {
							titularEmiserv = new es.caib.emiserv.logic.intf.service.ws.backoffice.Titular();
							titularEmiserv.setNombre(solicitudTransmision.getDatosGenericos().getTitular().getNombre());
							titularEmiserv.setApellido1(solicitudTransmision.getDatosGenericos().getTitular().getApellido1());
							titularEmiserv.setApellido2(solicitudTransmision.getDatosGenericos().getTitular().getApellido2());
							titularEmiserv.setNombreCompleto(solicitudTransmision.getDatosGenericos().getTitular().getNombreCompleto());
							titularEmiserv.setDocumentacion(solicitudTransmision.getDatosGenericos().getTitular().getDocumentacion());
							if (solicitudTransmision.getDatosGenericos().getTitular().getTipoDocumentacion() != null) {
								switch (solicitudTransmision.getDatosGenericos().getTitular().getTipoDocumentacion()) {
								case DNI:
									titularEmiserv.setTipoDocumentacion(
											es.caib.emiserv.logic.intf.service.ws.backoffice.TipoDocumentacion.DNI);
									break;
								case NIF:
									titularEmiserv.setTipoDocumentacion(
											es.caib.emiserv.logic.intf.service.ws.backoffice.TipoDocumentacion.NIF);
									break;
								case CIF:
									titularEmiserv.setTipoDocumentacion(
											es.caib.emiserv.logic.intf.service.ws.backoffice.TipoDocumentacion.CIF);
									break;
								case NIE:
									titularEmiserv.setTipoDocumentacion(
											es.caib.emiserv.logic.intf.service.ws.backoffice.TipoDocumentacion.NIE);
									break;
								case PASAPORTE:
									titularEmiserv.setTipoDocumentacion(
											es.caib.emiserv.logic.intf.service.ws.backoffice.TipoDocumentacion.Pasaporte);
									break;
								case OTROS:
									titularEmiserv.setTipoDocumentacion(
											es.caib.emiserv.logic.intf.service.ws.backoffice.TipoDocumentacion.Otros);
									break;
								case NUMERO_IDENTIFICACION:
									titularEmiserv.setTipoDocumentacion(
											es.caib.emiserv.logic.intf.service.ws.backoffice.TipoDocumentacion.NumeroIdentificacion);
									break;
								case CSV:
									titularEmiserv.setTipoDocumentacion(
											es.caib.emiserv.logic.intf.service.ws.backoffice.TipoDocumentacion.CSV);
									break;
								default:
									titularEmiserv.setTipoDocumentacion(null);
									break;
								}
							}
						}
						datosGenericosEmiserv.setTitular(titularEmiserv);
						es.caib.emiserv.logic.intf.service.ws.backoffice.Transmision transmisionEmiserv = null;
						if (solicitudTransmision.getDatosGenericos().getTransmision() != null) {
							transmisionEmiserv = new es.caib.emiserv.logic.intf.service.ws.backoffice.Transmision();
							transmisionEmiserv.setCodigoCertificado(solicitudTransmision.getDatosGenericos().getTransmision().getCodigoCertificado());
							transmisionEmiserv.setIdSolicitud(solicitudTransmision.getDatosGenericos().getTransmision().getIdSolicitud());
							transmisionEmiserv.setIdTransmision(solicitudTransmision.getDatosGenericos().getTransmision().getIdTransmision());
							transmisionEmiserv.setFechaGeneracion(solicitudTransmision.getDatosGenericos().getTransmision().getFechaGeneracion());
						}
						datosGenericosEmiserv.setTransmision(transmisionEmiserv);
					}
					solicitudTransmisionEmiserv.setDatosGenericos(datosGenericosEmiserv);
					if (solicitudTransmision.getDatosEspecificos() != null) {
						solicitudTransmisionEmiserv.setDatosEspecificos(solicitudTransmision.getDatosEspecificos());
					}
					solicitudesTransmisionEmiserv.add(solicitudTransmisionEmiserv);
				}
			}
			solicitudesEmiserv.setSolicitudTransmision(solicitudesTransmisionEmiserv);
		}
		peticionEmiserv.setSolicitudes(solicitudesEmiserv);
		return peticionEmiserv;
	}

	private Respuesta respuestaFromEmiservToScsp(
			es.caib.emiserv.logic.intf.service.ws.backoffice.Respuesta respuestaEmiserv) {
		if (respuestaEmiserv == null) return null;
		Respuesta respuesta = new Respuesta();
		es.scsp.bean.common.respuesta.Atributos atributos = null;
		if (respuestaEmiserv.getAtributos() != null) {
			es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos atributosEmiserv = respuestaEmiserv.getAtributos();
			atributos = new es.scsp.bean.common.respuesta.Atributos();
			atributos.setCodigoCertificado(atributosEmiserv.getCodigoCertificado());
			es.scsp.bean.common.respuesta.Estado estado = null;
			if (atributosEmiserv.getEstado() != null) {
				estado = new es.scsp.bean.common.respuesta.Estado();
				estado.setCodigoEstado(atributosEmiserv.getEstado().getCodigoEstado());
				estado.setCodigoEstadoSecundario(atributosEmiserv.getEstado().getCodigoEstadoSecundario());
				estado.setLiteralError(atributosEmiserv.getEstado().getLiteralError());
				estado.setTiempoEstimadoRespuesta(atributosEmiserv.getEstado().getTiempoEstimadoRespuesta());
			}
			atributos.setEstado(estado);
			atributos.setIdPeticion(atributosEmiserv.getIdPeticion());
			atributos.setNumElementos(numElementosFromEmiservToScsp(atributosEmiserv.getNumElementos()));
			atributos.setTimeStamp(atributosEmiserv.getTimeStamp());
		}
		respuesta.setAtributos(atributos);
		Transmisiones transmisiones = null;
		if (respuestaEmiserv.getTransmisiones() != null) {
			transmisiones = new Transmisiones();
			if (respuestaEmiserv.getTransmisiones().getTransmisionDatos() != null) {
				for (es.caib.emiserv.logic.intf.service.ws.backoffice.TransmisionDatos transmisionDatosEmiserv: respuestaEmiserv.getTransmisiones().getTransmisionDatos()) {
					TransmisionDatos transmisionDatos = new TransmisionDatos();
					DatosGenericos datosGenericos = null;
					if (transmisionDatosEmiserv.getDatosGenericos() != null) {
						datosGenericos = new DatosGenericos();
						Emisor emisor = null;
						if (transmisionDatosEmiserv.getDatosGenericos().getEmisor() != null) {
							emisor = new Emisor();
							emisor.setNifEmisor(transmisionDatosEmiserv.getDatosGenericos().getEmisor().getNifEmisor());
							emisor.setNombreEmisor(transmisionDatosEmiserv.getDatosGenericos().getEmisor().getNombreEmisor());
						}
						datosGenericos.setEmisor(emisor);
						Solicitante solicitante = null;
						if (transmisionDatosEmiserv.getDatosGenericos().getSolicitante() != null) {
							solicitante = new Solicitante();
							if (transmisionDatosEmiserv.getDatosGenericos().getSolicitante().getConsentimiento() != null) {
								switch (transmisionDatosEmiserv.getDatosGenericos().getSolicitante().getConsentimiento()) {
								case Ley:
									solicitante.setConsentimiento(
											Consentimiento.LEY);
									break;
								case Si:
									solicitante.setConsentimiento(
											Consentimiento.SI);
									break;
								case NoOpo:
									solicitante.setConsentimiento(
											Consentimiento.NO_OPO);
									break;
								}
							}
							solicitante.setFinalidad(
									transmisionDatosEmiserv.getDatosGenericos().getSolicitante().getFinalidad());
							Funcionario funcionario = null;
							if (transmisionDatosEmiserv.getDatosGenericos().getSolicitante().getFuncionario() != null) {
								funcionario = new Funcionario();
								funcionario.setNombreCompletoFuncionario(
										transmisionDatosEmiserv.getDatosGenericos().getSolicitante().getFuncionario().getNombreCompletoFuncionario());
								funcionario.setNifFuncionario(
										transmisionDatosEmiserv.getDatosGenericos().getSolicitante().getFuncionario().getNifFuncionario());
								funcionario.setSeudonimoEmpleadoPublico(null); // TODO
							}
							solicitante.setFuncionario(funcionario);
							solicitante.setNombreSolicitante(
									transmisionDatosEmiserv.getDatosGenericos().getSolicitante().getNombreSolicitante());
							solicitante.setIdentificadorSolicitante(
									transmisionDatosEmiserv.getDatosGenericos().getSolicitante().getIdentificadorSolicitante());
							solicitante.setIdExpediente(
									transmisionDatosEmiserv.getDatosGenericos().getSolicitante().getIdExpediente());
							Procedimiento procedimiento = null;
							if (transmisionDatosEmiserv.getDatosGenericos().getSolicitante().getProcedimiento() != null) {
								procedimiento = new Procedimiento();
								procedimiento.setCodProcedimiento(
										transmisionDatosEmiserv.getDatosGenericos().getSolicitante().getProcedimiento().getCodProcedimiento());
								procedimiento.setNombreProcedimiento(
										transmisionDatosEmiserv.getDatosGenericos().getSolicitante().getProcedimiento().getNombreProcedimiento());
							}
							solicitante.setProcedimiento(procedimiento);
							solicitante.setUnidadTramitadora(
									transmisionDatosEmiserv.getDatosGenericos().getSolicitante().getUnidadTramitadora());
							solicitante.setCodigoUnidadTramitadora(null); // TODO
						}
						datosGenericos.setSolicitante(solicitante);
						Titular titular = null;
						if (transmisionDatosEmiserv.getDatosGenericos().getTitular() != null) {
							titular = new Titular();
							titular.setNombre(transmisionDatosEmiserv.getDatosGenericos().getTitular().getNombre());
							titular.setApellido1(transmisionDatosEmiserv.getDatosGenericos().getTitular().getApellido1());
							titular.setApellido2(transmisionDatosEmiserv.getDatosGenericos().getTitular().getApellido2());
							titular.setNombreCompleto(transmisionDatosEmiserv.getDatosGenericos().getTitular().getNombreCompleto());
							titular.setDocumentacion(transmisionDatosEmiserv.getDatosGenericos().getTitular().getDocumentacion());
							if (transmisionDatosEmiserv.getDatosGenericos().getTitular().getTipoDocumentacion() != null) {
								switch (transmisionDatosEmiserv.getDatosGenericos().getTitular().getTipoDocumentacion()) {
								case DNI:
									titular.setTipoDocumentacion(TipoDocumentacion.DNI);
									break;
								case NIF:
									titular.setTipoDocumentacion(TipoDocumentacion.NIF);
									break;
								case CIF:
									titular.setTipoDocumentacion(TipoDocumentacion.CIF);
									break;
								case NIE:
									titular.setTipoDocumentacion(TipoDocumentacion.NIE);
									break;
								case Pasaporte:
									titular.setTipoDocumentacion(TipoDocumentacion.PASAPORTE);
									break;
								case Otros:
									titular.setTipoDocumentacion(TipoDocumentacion.OTROS);
									break;
								case NumeroIdentificacion:
									titular.setTipoDocumentacion(TipoDocumentacion.NUMERO_IDENTIFICACION);
									break;
								case CSV:
									titular.setTipoDocumentacion(TipoDocumentacion.CSV);
									break;
								default:
									titular.setTipoDocumentacion(null);
									break;
								}
							}
						}
						datosGenericos.setTitular(titular);
						Transmision transmision = null;
						if (transmisionDatosEmiserv.getDatosGenericos().getTransmision() != null) {
							transmision = new Transmision();
							transmision.setCodigoCertificado(transmisionDatosEmiserv.getDatosGenericos().getTransmision().getCodigoCertificado());
							transmision.setIdSolicitud(transmisionDatosEmiserv.getDatosGenericos().getTransmision().getIdSolicitud());
							transmision.setIdTransmision(transmisionDatosEmiserv.getDatosGenericos().getTransmision().getIdTransmision());
							transmision.setFechaGeneracion(transmisionDatosEmiserv.getDatosGenericos().getTransmision().getFechaGeneracion());
						}
						datosGenericos.setTransmision(transmision);
					}
					transmisionDatos.setDatosGenericos(datosGenericos);
					if (transmisionDatosEmiserv.getDatosEspecificos() != null) {
						transmisionDatos.setDatosEspecificos(transmisionDatosEmiserv.getDatosEspecificos());
					}
					transmisiones.getTransmisionDatos().add(transmisionDatos);
				}
			}
		}
		respuesta.setTransmisiones(transmisiones);
		return respuesta;
	}

	private ConfirmacionPeticion confirmacionPeticionFromEmiservToScsp(
			es.caib.emiserv.logic.intf.service.ws.backoffice.ConfirmacionPeticion confirmacionPeticionEmiserv) {
		if (confirmacionPeticionEmiserv == null) return null;
		ConfirmacionPeticion confirmacionPeticion = new ConfirmacionPeticion();
		es.scsp.bean.common.confirmacion.Atributos atributos = null;
		if (confirmacionPeticionEmiserv.getAtributos() != null) {
			es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos atributosEmiserv = confirmacionPeticionEmiserv.getAtributos();
			atributos = new es.scsp.bean.common.confirmacion.Atributos();
			atributos.setCodigoCertificado(atributosEmiserv.getCodigoCertificado());
			es.scsp.bean.common.confirmacion.Estado estado = null;
			if (atributosEmiserv.getEstado() != null) {
				estado = new es.scsp.bean.common.confirmacion.Estado();
				estado.setCodigoEstado(atributosEmiserv.getEstado().getCodigoEstado());
				estado.setCodigoEstadoSecundario(atributosEmiserv.getEstado().getCodigoEstadoSecundario());
				estado.setLiteralError(atributosEmiserv.getEstado().getLiteralError());
				estado.setTiempoEstimadoRespuesta(atributosEmiserv.getEstado().getTiempoEstimadoRespuesta());
			}
			atributos.setEstado(estado);
			atributos.setIdPeticion(atributosEmiserv.getIdPeticion());
			atributos.setNumElementos(numElementosFromEmiservToScsp(atributosEmiserv.getNumElementos()));
			atributos.setTimeStamp(atributosEmiserv.getTimeStamp());
		}
		confirmacionPeticion.setAtributos(atributos);
		return confirmacionPeticion;
	}

	private es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudRespuesta solicitudRespuestaFromScspToEmiserv(
			SolicitudRespuesta solicitudRespuesta) {
		if (solicitudRespuesta == null) return null;
		es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudRespuesta solicitudRespuestaEmiserv = new es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudRespuesta();
		es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos atributosEmiserv = null;
		if (solicitudRespuesta.getAtributos() != null) {
			atributosEmiserv = atributosFromScspToEmiserv(
					solicitudRespuesta.getAtributos().getCodigoCertificado(),
					solicitudRespuesta.getAtributos().getIdPeticion(),
					solicitudRespuesta.getAtributos().getNumElementos(),
					solicitudRespuesta.getAtributos().getTimeStamp());
			if (solicitudRespuesta.getAtributos().getEstado() != null) {
				atributosEmiserv.setEstado(estadoFromScspToEmiserv(
						solicitudRespuesta.getAtributos().getEstado().getCodigoEstado(),
						solicitudRespuesta.getAtributos().getEstado().getCodigoEstadoSecundario(),
						solicitudRespuesta.getAtributos().getEstado().getLiteralError(),
						solicitudRespuesta.getAtributos().getEstado().getTiempoEstimadoRespuesta()));
			}
		}
		solicitudRespuestaEmiserv.setAtributos(atributosEmiserv);
		return solicitudRespuestaEmiserv;
	}

	private es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos atributosFromScspToEmiserv(
			String codigoCertificado,
			String idPeticion,
			int numElementos,
			String timeStamp) {
		es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos atributosEmiserv = new es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos();
		atributosEmiserv.setCodigoCertificado(codigoCertificado);
		atributosEmiserv.setIdPeticion(idPeticion);
		atributosEmiserv.setNumElementos(String.valueOf(numElementos));
		atributosEmiserv.setTimeStamp(timeStamp);
		return atributosEmiserv;
	}

	private es.caib.emiserv.logic.intf.service.ws.backoffice.Estado estadoFromScspToEmiserv(
			String codigoEstado,
			String codigoEstadoSecundario,
			String literalError,
			Integer tiempoEstimadoRespuesta) {
		es.caib.emiserv.logic.intf.service.ws.backoffice.Estado estadoEmiserv = new es.caib.emiserv.logic.intf.service.ws.backoffice.Estado();
		estadoEmiserv.setCodigoEstado(codigoEstado);
		estadoEmiserv.setCodigoEstadoSecundario(codigoEstadoSecundario);
		estadoEmiserv.setLiteralError(literalError);
		estadoEmiserv.setTiempoEstimadoRespuesta(tiempoEstimadoRespuesta);
		return estadoEmiserv;
	}

	private int numElementosFromEmiservToScsp(String numElementos) {
		if (numElementos == null || numElementos.trim().isEmpty()) return 0;
		try {
			return Integer.parseInt(numElementos.trim());
		} catch (NumberFormatException ex) {
			log.warn("El valor de numElementos no és numèric: " + numElementos);
			return 0;
		}
	}

}
