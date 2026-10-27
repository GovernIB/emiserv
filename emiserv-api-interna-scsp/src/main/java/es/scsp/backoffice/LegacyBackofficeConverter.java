package es.scsp.backoffice;

import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.respuesta.Respuesta;
import es.scsp.bean.common.solicitud.SolicitudRespuesta;

import java.util.ArrayList;

/**
 * Conversió entre els beans de les llibreries SCSP 5 (es.scsp.bean.common.peticion, .respuesta,
 * .confirmacion i .solicitud) i els beans de les llibreries SCSP 4 (es.scsp.bean.common).
 *
 * L'utilitzen els mètodes pont que {@link LegacyBackofficeBridge} afegeix a les classes dels
 * backoffice.jar compilats contra les llibreries SCSP 4. Per això els mètodes públics no es
 * poden reanomenar sense modificar també {@link LegacyBackofficeBridge}.
 *
 * Valors sense equivalent a les llibreries SCSP 4:
 * - Consentimiento NoOpo: arriba al backoffice com a null.
 * - Procedimiento automatizado i claseTramite: no arriben al backoffice.
 * - L'atribut Id de SolicitudTransmision i TransmisionDatos: no existeix a l'esquema SCSP V3.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class LegacyBackofficeConverter {

    // Petició (SCSP 5 -> SCSP 4)

    public static es.scsp.bean.common.Peticion toLegacy(Peticion peticion) {
        if (peticion == null) return null;
        es.scsp.bean.common.Peticion legacy = new es.scsp.bean.common.Peticion();
        legacy.setAtributos(toLegacy(peticion.getAtributos()));
        if (peticion.getSolicitudes() != null) {
            ArrayList<es.scsp.bean.common.SolicitudTransmision> solicitudesTransmision = new ArrayList<>();
            for (es.scsp.bean.common.peticion.SolicitudTransmision solicitudTransmision: peticion.getSolicitudes().getSolicitudTransmision()) {
                es.scsp.bean.common.SolicitudTransmision legacySolicitudTransmision = new es.scsp.bean.common.SolicitudTransmision();
                legacySolicitudTransmision.setDatosGenericos(toLegacy(solicitudTransmision.getDatosGenericos()));
                // Tant a SCSP 4 (JiBX DomElementMapper) com a SCSP 5 (JAXB) les dades específiques són un org.w3c.dom.Element
                legacySolicitudTransmision.setDatosEspecificos(solicitudTransmision.getDatosEspecificos());
                solicitudesTransmision.add(legacySolicitudTransmision);
            }
            es.scsp.bean.common.Solicitudes legacySolicitudes = new es.scsp.bean.common.Solicitudes();
            legacySolicitudes.setSolicitudTransmision(solicitudesTransmision);
            legacy.setSolicitudes(legacySolicitudes);
        }
        return legacy;
    }

    private static es.scsp.bean.common.Atributos toLegacy(es.scsp.bean.common.peticion.Atributos atributos) {
        if (atributos == null) return null;
        es.scsp.bean.common.Atributos legacy = new es.scsp.bean.common.Atributos();
        legacy.setIdPeticion(atributos.getIdPeticion());
        legacy.setNumElementos(String.valueOf(atributos.getNumElementos()));
        legacy.setTimeStamp(atributos.getTimeStamp());
        legacy.setCodigoCertificado(atributos.getCodigoCertificado());
        es.scsp.bean.common.peticion.Estado estado = atributos.getEstado();
        if (estado != null) {
            legacy.setEstado(toLegacyEstado(
                    estado.getCodigoEstado(),
                    estado.getCodigoEstadoSecundario(),
                    estado.getLiteralError(),
                    null,
                    estado.getTiempoEstimadoRespuesta()));
        }
        return legacy;
    }

    private static es.scsp.bean.common.DatosGenericos toLegacy(es.scsp.bean.common.peticion.DatosGenericos datosGenericos) {
        if (datosGenericos == null) return null;
        es.scsp.bean.common.DatosGenericos legacy = new es.scsp.bean.common.DatosGenericos();
        if (datosGenericos.getEmisor() != null) {
            es.scsp.bean.common.Emisor emisor = new es.scsp.bean.common.Emisor();
            emisor.setNifEmisor(datosGenericos.getEmisor().getNifEmisor());
            emisor.setNombreEmisor(datosGenericos.getEmisor().getNombreEmisor());
            legacy.setEmisor(emisor);
        }
        es.scsp.bean.common.peticion.Solicitante solicitante = datosGenericos.getSolicitante();
        if (solicitante != null) {
            es.scsp.bean.common.Solicitante legacySolicitante = new es.scsp.bean.common.Solicitante();
            legacySolicitante.setIdentificadorSolicitante(solicitante.getIdentificadorSolicitante());
            legacySolicitante.setNombreSolicitante(solicitante.getNombreSolicitante());
            legacySolicitante.setUnidadTramitadora(solicitante.getUnidadTramitadora());
            legacySolicitante.setCodigoUnidadTramitadora(solicitante.getCodigoUnidadTramitadora());
            if (solicitante.getProcedimiento() != null) {
                es.scsp.bean.common.Procedimiento legacyProcedimiento = new es.scsp.bean.common.Procedimiento();
                legacyProcedimiento.setCodProcedimiento(solicitante.getProcedimiento().getCodProcedimiento());
                legacyProcedimiento.setNombreProcedimiento(solicitante.getProcedimiento().getNombreProcedimiento());
                legacySolicitante.setProcedimiento(legacyProcedimiento);
            }
            legacySolicitante.setFinalidad(solicitante.getFinalidad());
            if (solicitante.getConsentimiento() != null) {
                legacySolicitante.setConsentimiento(toLegacyConsentimiento(solicitante.getConsentimiento().value()));
            }
            if (solicitante.getFuncionario() != null) {
                legacySolicitante.setFuncionario(toLegacyFuncionario(
                        solicitante.getFuncionario().getNombreCompletoFuncionario(),
                        solicitante.getFuncionario().getNifFuncionario(),
                        solicitante.getFuncionario().getSeudonimoEmpleadoPublico()));
            }
            legacySolicitante.setIdExpediente(solicitante.getIdExpediente());
            legacy.setSolicitante(legacySolicitante);
        }
        es.scsp.bean.common.peticion.Titular titular = datosGenericos.getTitular();
        if (titular != null) {
            es.scsp.bean.common.Titular legacyTitular = new es.scsp.bean.common.Titular();
            if (titular.getTipoDocumentacion() != null) {
                legacyTitular.setTipoDocumentacion(toLegacyTipoDocumentacion(titular.getTipoDocumentacion().value()));
            }
            legacyTitular.setDocumentacion(titular.getDocumentacion());
            legacyTitular.setNombreCompleto(titular.getNombreCompleto());
            legacyTitular.setNombre(titular.getNombre());
            legacyTitular.setApellido1(titular.getApellido1());
            legacyTitular.setApellido2(titular.getApellido2());
            legacy.setTitular(legacyTitular);
        }
        es.scsp.bean.common.peticion.Transmision transmision = datosGenericos.getTransmision();
        if (transmision != null) {
            legacy.setTransmision(toLegacyTransmision(
                    transmision.getCodigoCertificado(),
                    transmision.getIdSolicitud(),
                    transmision.getIdTransmision(),
                    transmision.getFechaGeneracion()));
        }
        return legacy;
    }

    // Sol·licitud de resposta (SCSP 5 -> SCSP 4)

    public static es.scsp.bean.common.SolicitudRespuesta toLegacy(SolicitudRespuesta solicitudRespuesta) {
        if (solicitudRespuesta == null) return null;
        es.scsp.bean.common.SolicitudRespuesta legacy = new es.scsp.bean.common.SolicitudRespuesta();
        es.scsp.bean.common.solicitud.Atributos atributos = solicitudRespuesta.getAtributos();
        if (atributos != null) {
            es.scsp.bean.common.Atributos legacyAtributos = new es.scsp.bean.common.Atributos();
            legacyAtributos.setIdPeticion(atributos.getIdPeticion());
            legacyAtributos.setNumElementos(String.valueOf(atributos.getNumElementos()));
            legacyAtributos.setTimeStamp(atributos.getTimeStamp());
            legacyAtributos.setCodigoCertificado(atributos.getCodigoCertificado());
            es.scsp.bean.common.solicitud.Estado estado = atributos.getEstado();
            if (estado != null) {
                legacyAtributos.setEstado(toLegacyEstado(
                        estado.getCodigoEstado(),
                        estado.getCodigoEstadoSecundario(),
                        estado.getLiteralError(),
                        null,
                        estado.getTiempoEstimadoRespuesta()));
            }
            if (atributos.getSolicitante() != null) {
                es.scsp.bean.common.Solicitante legacySolicitante = new es.scsp.bean.common.Solicitante();
                legacySolicitante.setIdentificadorSolicitante(atributos.getSolicitante().getIdentificadorSolicitante());
                legacySolicitante.setNombreSolicitante(atributos.getSolicitante().getNombreSolicitante());
                legacyAtributos.setSolicitante(legacySolicitante);
            }
            legacy.setAtributos(legacyAtributos);
        }
        return legacy;
    }

    // Resposta (SCSP 4 -> SCSP 5)

    public static Respuesta fromLegacy(es.scsp.bean.common.Respuesta legacy) {
        if (legacy == null) return null;
        Respuesta respuesta = new Respuesta();
        es.scsp.bean.common.Atributos legacyAtributos = legacy.getAtributos();
        if (legacyAtributos != null) {
            es.scsp.bean.common.respuesta.Atributos atributos = new es.scsp.bean.common.respuesta.Atributos();
            atributos.setIdPeticion(legacyAtributos.getIdPeticion());
            atributos.setNumElementos(toNumElementos(legacyAtributos.getNumElementos()));
            atributos.setTimeStamp(legacyAtributos.getTimeStamp());
            atributos.setCodigoCertificado(legacyAtributos.getCodigoCertificado());
            es.scsp.bean.common.Estado legacyEstado = legacyAtributos.getEstado();
            if (legacyEstado != null) {
                es.scsp.bean.common.respuesta.Estado estado = new es.scsp.bean.common.respuesta.Estado();
                estado.setCodigoEstado(legacyEstado.getCodigoEstado());
                estado.setCodigoEstadoSecundario(legacyEstado.getCodigoEstadoSecundario());
                estado.setLiteralError(legacyEstado.getLiteralError());
                estado.setLiteralErrorSecundario(legacyEstado.getLiteralErrorSec());
                estado.setTiempoEstimadoRespuesta(legacyEstado.getTiempoEstimadoRespuesta());
                atributos.setEstado(estado);
            }
            respuesta.setAtributos(atributos);
        }
        if (legacy.getTransmisiones() != null) {
            es.scsp.bean.common.respuesta.Transmisiones transmisiones = new es.scsp.bean.common.respuesta.Transmisiones();
            if (legacy.getTransmisiones().getTransmisionDatos() != null) {
                for (es.scsp.bean.common.TransmisionDatos legacyTransmisionDatos: legacy.getTransmisiones().getTransmisionDatos()) {
                    es.scsp.bean.common.respuesta.TransmisionDatos transmisionDatos = new es.scsp.bean.common.respuesta.TransmisionDatos();
                    transmisionDatos.setDatosGenericos(fromLegacy(legacyTransmisionDatos.getDatosGenericos()));
                    transmisionDatos.setDatosEspecificos(legacyTransmisionDatos.getDatosEspecificos());
                    transmisiones.getTransmisionDatos().add(transmisionDatos);
                }
            }
            respuesta.setTransmisiones(transmisiones);
        }
        return respuesta;
    }

    private static es.scsp.bean.common.respuesta.DatosGenericos fromLegacy(es.scsp.bean.common.DatosGenericos legacy) {
        if (legacy == null) return null;
        es.scsp.bean.common.respuesta.DatosGenericos datosGenericos = new es.scsp.bean.common.respuesta.DatosGenericos();
        if (legacy.getEmisor() != null) {
            es.scsp.bean.common.respuesta.Emisor emisor = new es.scsp.bean.common.respuesta.Emisor();
            emisor.setNifEmisor(legacy.getEmisor().getNifEmisor());
            emisor.setNombreEmisor(legacy.getEmisor().getNombreEmisor());
            datosGenericos.setEmisor(emisor);
        }
        es.scsp.bean.common.Solicitante legacySolicitante = legacy.getSolicitante();
        if (legacySolicitante != null) {
            es.scsp.bean.common.respuesta.Solicitante solicitante = new es.scsp.bean.common.respuesta.Solicitante();
            solicitante.setIdentificadorSolicitante(legacySolicitante.getIdentificadorSolicitante());
            solicitante.setNombreSolicitante(legacySolicitante.getNombreSolicitante());
            solicitante.setUnidadTramitadora(legacySolicitante.getUnidadTramitadora());
            solicitante.setCodigoUnidadTramitadora(legacySolicitante.getCodigoUnidadTramitadora());
            if (legacySolicitante.getProcedimiento() != null) {
                es.scsp.bean.common.respuesta.Procedimiento procedimiento = new es.scsp.bean.common.respuesta.Procedimiento();
                procedimiento.setCodProcedimiento(legacySolicitante.getProcedimiento().getCodProcedimiento());
                procedimiento.setNombreProcedimiento(legacySolicitante.getProcedimiento().getNombreProcedimiento());
                solicitante.setProcedimiento(procedimiento);
            }
            solicitante.setFinalidad(legacySolicitante.getFinalidad());
            if (legacySolicitante.getConsentimiento() != null) {
                solicitante.setConsentimiento(es.scsp.bean.common.respuesta.Consentimiento.fromValue(
                        legacySolicitante.getConsentimiento().name()));
            }
            if (legacySolicitante.getFuncionario() != null) {
                es.scsp.bean.common.respuesta.Funcionario funcionario = new es.scsp.bean.common.respuesta.Funcionario();
                funcionario.setNombreCompletoFuncionario(legacySolicitante.getFuncionario().getNombreCompletoFuncionario());
                funcionario.setNifFuncionario(legacySolicitante.getFuncionario().getNifFuncionario());
                funcionario.setSeudonimoEmpleadoPublico(legacySolicitante.getFuncionario().getSeudonimo());
                solicitante.setFuncionario(funcionario);
            }
            solicitante.setIdExpediente(legacySolicitante.getIdExpediente());
            datosGenericos.setSolicitante(solicitante);
        }
        es.scsp.bean.common.Titular legacyTitular = legacy.getTitular();
        if (legacyTitular != null) {
            es.scsp.bean.common.respuesta.Titular titular = new es.scsp.bean.common.respuesta.Titular();
            if (legacyTitular.getTipoDocumentacion() != null) {
                titular.setTipoDocumentacion(es.scsp.bean.common.respuesta.TipoDocumentacion.fromValue(
                        legacyTitular.getTipoDocumentacion().name()));
            }
            titular.setDocumentacion(legacyTitular.getDocumentacion());
            titular.setNombreCompleto(legacyTitular.getNombreCompleto());
            titular.setNombre(legacyTitular.getNombre());
            titular.setApellido1(legacyTitular.getApellido1());
            titular.setApellido2(legacyTitular.getApellido2());
            datosGenericos.setTitular(titular);
        }
        es.scsp.bean.common.Transmision legacyTransmision = legacy.getTransmision();
        if (legacyTransmision != null) {
            es.scsp.bean.common.respuesta.Transmision transmision = new es.scsp.bean.common.respuesta.Transmision();
            transmision.setCodigoCertificado(legacyTransmision.getCodigoCertificado());
            transmision.setIdSolicitud(legacyTransmision.getIdSolicitud());
            transmision.setIdTransmision(legacyTransmision.getIdTransmision());
            transmision.setFechaGeneracion(legacyTransmision.getFechaGeneracion());
            datosGenericos.setTransmision(transmision);
        }
        return datosGenericos;
    }

    // Confirmació de petició (SCSP 4 -> SCSP 5)

    public static ConfirmacionPeticion fromLegacy(es.scsp.bean.common.ConfirmacionPeticion legacy) {
        if (legacy == null) return null;
        ConfirmacionPeticion confirmacionPeticion = new ConfirmacionPeticion();
        es.scsp.bean.common.Atributos legacyAtributos = legacy.getAtributos();
        if (legacyAtributos != null) {
            es.scsp.bean.common.confirmacion.Atributos atributos = new es.scsp.bean.common.confirmacion.Atributos();
            atributos.setIdPeticion(legacyAtributos.getIdPeticion());
            atributos.setNumElementos(toNumElementos(legacyAtributos.getNumElementos()));
            atributos.setTimeStamp(legacyAtributos.getTimeStamp());
            atributos.setCodigoCertificado(legacyAtributos.getCodigoCertificado());
            es.scsp.bean.common.Estado legacyEstado = legacyAtributos.getEstado();
            if (legacyEstado != null) {
                es.scsp.bean.common.confirmacion.Estado estado = new es.scsp.bean.common.confirmacion.Estado();
                estado.setCodigoEstado(legacyEstado.getCodigoEstado());
                estado.setCodigoEstadoSecundario(legacyEstado.getCodigoEstadoSecundario());
                estado.setLiteralError(legacyEstado.getLiteralError());
                estado.setTiempoEstimadoRespuesta(legacyEstado.getTiempoEstimadoRespuesta());
                atributos.setEstado(estado);
            }
            confirmacionPeticion.setAtributos(atributos);
        }
        return confirmacionPeticion;
    }

    // Mètodes auxiliars

    private static es.scsp.bean.common.Estado toLegacyEstado(
            String codigoEstado,
            String codigoEstadoSecundario,
            String literalError,
            String literalErrorSecundario,
            Integer tiempoEstimadoRespuesta) {
        es.scsp.bean.common.Estado legacy = new es.scsp.bean.common.Estado();
        legacy.setCodigoEstado(codigoEstado);
        legacy.setCodigoEstadoSecundario(codigoEstadoSecundario);
        legacy.setLiteralError(literalError);
        legacy.setLiteralErrorSec(literalErrorSecundario);
        legacy.setTiempoEstimadoRespuesta(tiempoEstimadoRespuesta);
        return legacy;
    }

    private static es.scsp.bean.common.Funcionario toLegacyFuncionario(
            String nombreCompletoFuncionario,
            String nifFuncionario,
            String seudonimo) {
        es.scsp.bean.common.Funcionario legacy = new es.scsp.bean.common.Funcionario();
        legacy.setNombreCompletoFuncionario(nombreCompletoFuncionario);
        legacy.setNifFuncionario(nifFuncionario);
        legacy.setSeudonimo(seudonimo);
        return legacy;
    }

    private static es.scsp.bean.common.Transmision toLegacyTransmision(
            String codigoCertificado,
            String idSolicitud,
            String idTransmision,
            String fechaGeneracion) {
        es.scsp.bean.common.Transmision legacy = new es.scsp.bean.common.Transmision();
        legacy.setCodigoCertificado(codigoCertificado);
        legacy.setIdSolicitud(idSolicitud);
        legacy.setIdTransmision(idTransmision);
        legacy.setFechaGeneracion(fechaGeneracion);
        return legacy;
    }

    // Els noms de les constants dels enums SCSP 4 coincideixen amb els valors XML dels enums SCSP 5

    private static es.scsp.bean.common.Consentimiento toLegacyConsentimiento(String value) {
        for (es.scsp.bean.common.Consentimiento consentimiento: es.scsp.bean.common.Consentimiento.values()) {
            if (consentimiento.name().equals(value)) return consentimiento;
        }
        return null;
    }

    private static es.scsp.bean.common.TipoDocumentacion toLegacyTipoDocumentacion(String value) {
        for (es.scsp.bean.common.TipoDocumentacion tipoDocumentacion: es.scsp.bean.common.TipoDocumentacion.values()) {
            if (tipoDocumentacion.name().equals(value)) return tipoDocumentacion;
        }
        return null;
    }

    private static int toNumElementos(String numElementos) {
        if (numElementos == null) return 0;
        try {
            return Integer.parseInt(numElementos.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

}
