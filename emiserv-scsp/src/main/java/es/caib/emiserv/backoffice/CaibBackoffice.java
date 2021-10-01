/**
 * 
 */
package es.caib.emiserv.backoffice;

import java.util.ArrayList;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import es.caib.emiserv.logic.intf.service.BackofficeService;
import es.scsp.bean.common.Atributos;
import es.scsp.bean.common.ConfirmacionPeticion;
import es.scsp.bean.common.Consentimiento;
import es.scsp.bean.common.DatosGenericos;
import es.scsp.bean.common.Emisor;
import es.scsp.bean.common.Estado;
import es.scsp.bean.common.Funcionario;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Procedimiento;
import es.scsp.bean.common.Respuesta;
import es.scsp.bean.common.Solicitante;
import es.scsp.bean.common.SolicitudRespuesta;
import es.scsp.bean.common.SolicitudTransmision;
import es.scsp.bean.common.TipoDocumentacion;
import es.scsp.bean.common.Titular;
import es.scsp.bean.common.Transmision;
import es.scsp.bean.common.TransmisionDatos;
import es.scsp.bean.common.Transmisiones;
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

	private String getIdentificacioDelsAtributsScsp(Atributos atributos) {
		String codigoCertificado = "???";
		String idPeticion = "???";
		if (atributos != null) {
			codigoCertificado = atributos.getCodigoCertificado();
			idPeticion = atributos.getIdPeticion();
		}
		return "(codigoCertificado=" + codigoCertificado + ", idPeticion=" + idPeticion + ")";
	}

	public BackofficeService getBackofficeService() {
		return BackofficeServiceEjbLocator.getBackofficeService();
	}

	private es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion peticionFromScspToEmiserv(Peticion peticion) {
		if (peticion == null) return null;
		es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion peticionEmiserv = new es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion();
		peticionEmiserv.setAtributos(atributosFromScspToEmiserv(peticion.getAtributos()));
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
								case Ley:
									solicitanteEmiserv.setConsentimiento(
											es.caib.emiserv.logic.intf.service.ws.backoffice.Consentimiento.Ley);
									break;
								case Si:
									solicitanteEmiserv.setConsentimiento(
											es.caib.emiserv.logic.intf.service.ws.backoffice.Consentimiento.Si);
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
								//		solicitudTransmision.getDatosGenericos().getSolicitante().getFuncionario().getSeudonimo()); // TODO
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
								case Pasaporte:
									titularEmiserv.setTipoDocumentacion(
											es.caib.emiserv.logic.intf.service.ws.backoffice.TipoDocumentacion.Pasaporte);
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
					solicitudTransmisionEmiserv.setId(solicitudTransmision.getId());
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
		respuesta.setAtributos(atributosFromEmiservToScsp(respuestaEmiserv.getAtributos()));
		Transmisiones transmisiones = null;
		if (respuestaEmiserv.getTransmisiones() != null) {
			transmisiones = new Transmisiones();
			ArrayList<TransmisionDatos> transmisionesDatos = null;
			if (respuestaEmiserv.getTransmisiones().getTransmisionDatos() != null) {
				transmisionesDatos = new ArrayList<TransmisionDatos>();
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
											Consentimiento.Ley);
									break;
								case Si:
									solicitante.setConsentimiento(
											Consentimiento.Si);
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
								funcionario.setSeudonimo(null); // TODO
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
									titular.setTipoDocumentacion(TipoDocumentacion.Pasaporte);
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
					transmisionDatos.setId(transmisionDatosEmiserv.getId());
					transmisionesDatos.add(transmisionDatos);
				}
			}
			transmisiones.setTransmisionDatos(transmisionesDatos);
		}
		respuesta.setTransmisiones(transmisiones);
		return respuesta;
	}

	private ConfirmacionPeticion confirmacionPeticionFromEmiservToScsp(
			es.caib.emiserv.logic.intf.service.ws.backoffice.ConfirmacionPeticion confirmacionPeticionEmiserv) {
		if (confirmacionPeticionEmiserv == null) return null;
		ConfirmacionPeticion confirmacionPeticion = new ConfirmacionPeticion();
		confirmacionPeticion.setAtributos(atributosFromEmiservToScsp(confirmacionPeticionEmiserv.getAtributos()));
		return confirmacionPeticion;
	}

	private es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudRespuesta solicitudRespuestaFromScspToEmiserv(
			SolicitudRespuesta solicitudRespuesta) {
		if (solicitudRespuesta == null) return null;
		es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudRespuesta solicitudRespuestaEmiserv = new es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudRespuesta();
		solicitudRespuestaEmiserv.setAtributos(atributosFromScspToEmiserv(solicitudRespuesta.getAtributos()));
		return solicitudRespuestaEmiserv;
	}

	private es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos atributosFromScspToEmiserv(Atributos atributos) {
		es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos atributosEmiserv = null;
		if (atributos != null) {
			atributosEmiserv = new es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos();
			atributosEmiserv.setCodigoCertificado(atributos.getCodigoCertificado());
			es.caib.emiserv.logic.intf.service.ws.backoffice.Estado estadoEmiserv = null;
			if (atributos.getEstado() != null) {
				estadoEmiserv = new es.caib.emiserv.logic.intf.service.ws.backoffice.Estado();
				estadoEmiserv.setCodigoEstado(atributos.getEstado().getCodigoEstado());
				estadoEmiserv.setCodigoEstadoSecundario(atributos.getEstado().getCodigoEstadoSecundario());
				estadoEmiserv.setLiteralError(atributos.getEstado().getLiteralError());
				//estadoEmiserv.setLiteralErrorSec(atributos.getEstado().getLiteralErrorSec()); // TODO
				estadoEmiserv.setTiempoEstimadoRespuesta(atributos.getEstado().getTiempoEstimadoRespuesta());
			}
			atributosEmiserv.setEstado(estadoEmiserv);
			atributosEmiserv.setIdPeticion(atributos.getIdPeticion());
			atributosEmiserv.setNumElementos(atributos.getNumElementos());
			atributosEmiserv.setTimeStamp(atributos.getTimeStamp());
		}
		return atributosEmiserv;
	}

	private Atributos atributosFromEmiservToScsp(es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos atributosEmiserv) {
		Atributos atributos = null;
		if (atributosEmiserv != null) {
			atributos = new Atributos();
			atributos.setCodigoCertificado(atributosEmiserv.getCodigoCertificado());
			Estado estado = null;
			if (atributosEmiserv.getEstado() != null) {
				estado = new Estado();
				estado.setCodigoEstado(atributosEmiserv.getEstado().getCodigoEstado());
				estado.setCodigoEstadoSecundario(atributosEmiserv.getEstado().getCodigoEstadoSecundario());
				estado.setLiteralError(atributosEmiserv.getEstado().getLiteralError());
				estado.setLiteralErrorSec(null); // TODO
				estado.setTiempoEstimadoRespuesta(atributosEmiserv.getEstado().getTiempoEstimadoRespuesta());
			}
			atributos.setEstado(estado);
			atributos.setIdPeticion(atributosEmiserv.getIdPeticion());
			atributos.setNumElementos(atributosEmiserv.getNumElementos());
			atributos.setTimeStamp(atributosEmiserv.getTimeStamp());
		}
		return atributos;
	}

}
