package es.scsp.modules;

import java.security.cert.X509Certificate;
import java.util.List;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.JaxenException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.w3c.dom.Element;

import es.caib.emiserv.core.scsp.ApplicationContextProvider;
import es.scsp.bean.common.Peticion;
import es.scsp.common.core.ServiceManager;
import es.scsp.common.dao.PeticionRespuestaDao;
import es.scsp.common.dao.TransmisionDao;
import es.scsp.common.domain.core.Modulo;
import es.scsp.common.domain.core.ParametroConfiguracion;
import es.scsp.common.domain.core.PeticionRespuesta;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.domain.core.Transmision;
import es.scsp.common.exceptions.ScspException;
import es.scsp.common.security.X509ContextInspector;
import es.scsp.common.utils.StaticContextSupport;
import es.scsp.common.utils.xpath.XpathManager;

public abstract class ScspHandler
  extends AbstractHandler
{
  private static final Log LOG = LogFactory.getLog(ScspHandler.class);
//  private static final String XPathCodigoCertificado = "//*[local-name()='Atributos']/*[local-name()='CodigoCertificado']/text()";
//  private static final String XPathCodigoCertificadoAlternativo = "//*[local-name()='Atributos']/*[local-name()='CodCertificado']/text()";
//  private static final String XPathTransmisionCodigoCertificado = "//*[local-name()='Transmision']/*[local-name()='CodigoCertificado']";
//  private static final String XPathNifSolicitante = "//*[local-name()='Solicitante']/*[local-name()='IdentificadorSolicitante']/text()";
//  private static final String XPathCodigoEstado = "//*[local-name()='Atributos']/*[local-name()='CodigoEstado']/text()";
//  private static final String XPathIdPeticion = "//*[local-name()='Atributos']/*[local-name()='IdPeticion']/text()";
//  private static final String XPathTer = "//*[local-name()='TiempoEstimadoRespuesta']/text()";
//  private static final String XPathNumElementos = "//*[local-name()='Atributos']/*[local-name()='NumElementos']/text()";
//  private static final String XPathSolicitudTransmision = "//*[local-name()='SolicitudTransmision']";
//  private static final String MsgErrReadX509 = "No se ha podido recuperar el certificado X.509 utilizado para firmar el mensaje. %s";
//  private static final String MsgErrReadIdPeticion = "Error al obtener el elemento IdPeticion del mensaje. %s";
//  private static final String MsgErrReadCodCertificado = "Error al obtener el elemento CodigoCertificado del mensaje.";
//  private static final String MsgErrReadNifsolicitante = "Error al obtener el elemento IdentificadorSolicitante del mensaje.";
//  private static final String MsgMissingModule = "No se encuentra la informacion de configuracion relativa al modulo %s. Revise la configuracion.";
//  private static final String MsgModuleInactive = "No se realiza el procesamiento del modulo %s. Dicho modulo esta desactivado en la configuracion.";
//  private static final String MsgErrReadService = "Error al determinar el servicio asociado al mensaje. %s";
//  private static final String MsgErrReadTer = "Error al leer el tiempo estimado de respuesta. %s";
//  private static final String MsgErrConfiguracionModulos = "Error no se encuentra la configuración de los módulos en el contexto";
  
  protected Object getBean(String name)
  {
    ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
    if (ctx == null) {
		ctx = ApplicationContextProvider.getApplicationContext();
    }
    if (ctx == null) {
      ctx = StaticContextSupport.getContextInstance();
    }
    return ctx.getBean(name);
    
  }
  
  protected boolean isInflow(MessageContext message)
  {
    return 1 == message.getFLOW();
  }
  
  protected boolean isOutflow(MessageContext message)
  {
    return 2 == message.getFLOW();
  }
  
  protected boolean isRequirente(MessageContext messageContext)
  {
    Parameter paramRequirente = messageContext.getConfigurationContext().getAxisConfiguration().getParameter("requirente");
    return "true".equalsIgnoreCase(paramRequirente.getParameterElement().getText());
  }
  
  protected boolean isEmisor(MessageContext messageContext)
  {
    return !isRequirente(messageContext);
  }
  
  protected MessageContext getInflowMessageContext(MessageContext messageContext)
    throws AxisFault
  {
    Validate.notNull(messageContext);
    OperationContext opCtx = messageContext.getOperationContext();
    if (opCtx == null) {
      return null;
    }
    return opCtx.getMessageContext("In");
  }
  
  protected MessageContext getOutflowMessageContext(MessageContext messageContext)
    throws AxisFault
  {
    Validate.notNull(messageContext);
    OperationContext opCtx = messageContext.getOperationContext();
    if (opCtx == null) {
      return null;
    }
    return opCtx.getMessageContext("Out");
  }
  
  protected X509Certificate getSignerCertificate(MessageContext message)
    throws ScspException
  {
    try
    {
      if (message.getProperty("SIGNER_CERTIFICATE") != null) {
        return (X509Certificate)message.getProperty("SIGNER_CERTIFICATE");
      }
      X509ContextInspector inspector = new X509ContextInspector();
      X509Certificate x509 = inspector.getSignerCertificate(message);
      message.setProperty("SIGNER_CERTIFICATE", x509);
      return x509;
    }
    catch (ScspException e)
    {
      if ((e.getScspCode() != null) && (e.getScspCode().equals("0504"))) {
        throw e;
      }
      LOG.error("No se puede obtener el certificado firmante del contexto del mensaje.");
      LOG.error("Message.Properties:");
      for (String key : message.getProperties().keySet())
      {
        Object value = message.getProperties().get(key);
        LOG.error("  [" + key + "]=" + value);
      }
      OMElement idPeticionNode = getChildrenWithNameRecursive(message.getEnvelope(), "IdPeticion");
      OMElement codCertificadoNode = getChildrenWithNameRecursive(message.getEnvelope(), "CodigoCertificado");
      if ((idPeticionNode == null) || (codCertificadoNode == null))
      {
        LOG.error("No se puede recuperar la informacion de identificación de la petición.");
        LOG.error("No se ha encontrado el id de peticion o  el codigo de certificado en el mensaje " + message.getEnvelope());
        throw new RuntimeException("0311", e);
      }
      String msg = String.format("No se ha podido recuperar el certificado X.509 utilizado para firmar el mensaje. %s", new Object[] { e.getMessage() });
      String[] arg = { msg };
      throw ScspException.getScspException(e, "0311", arg, idPeticionNode.getText(), codCertificadoNode.getText());
    }
  }
  
  protected String extractCodigoCertificado(OMElement message)
    throws AxisFault
  {
    try
    {
      return evalXPathExp("//*[local-name()='Atributos']/*[local-name()='CodigoCertificado']/text()", message);
    }
    catch (Exception ex)
    {
      String[] args = { "Error al obtener el elemento CodigoCertificado del mensaje.", "" };
      throw ScspException.getScspException("0402", args);
    }
  }
  
  protected String extractCodCertificado(OMElement message)
    throws AxisFault
  {
    try
    {
      return evalXPathExp("//*[local-name()='Atributos']/*[local-name()='CodCertificado']/text()", message);
    }
    catch (Exception ex)
    {
      String[] args = { "Error al obtener el elemento CodigoCertificado del mensaje.", "" };
      throw ScspException.getScspException("0402", args);
    }
  }
  
  protected List<OMElement> extractCodigoCertificadoSolicitudes(OMElement message)
    throws AxisFault
  {
    try
    {
      return evalListXPathExp("//*[local-name()='Transmision']/*[local-name()='CodigoCertificado']", message);
    }
    catch (Exception ex)
    {
      throw new RuntimeException("Error al obtener el elemento CodigoCertificado del mensaje.", ex);
    }
  }
  
  protected String extractNifSolicitante(OMElement message)
    throws AxisFault
  {
    try
    {
      return evalXPathExp("//*[local-name()='Solicitante']/*[local-name()='IdentificadorSolicitante']/text()", message);
    }
    catch (Exception ex)
    {
      throw new RuntimeException("Error al obtener el elemento IdentificadorSolicitante del mensaje.", ex);
    }
  }
  
  protected List<OMElement> extractSolitiudesTransmision(OMElement message)
    throws AxisFault, JaxenException
  {
    XpathManager manager = new XpathManager();
    return manager.evalListXPathExp("//*[local-name()='SolicitudTransmision']", message);
  }
  
  protected String extractIdPeticionFromMessage(OMElement message)
    throws AxisFault
  {
    try
    {
      return evalXPathExp("//*[local-name()='Atributos']/*[local-name()='IdPeticion']/text()", message);
    }
    catch (Exception ex)
    {
      throw new RuntimeException(String.format("Error al obtener el elemento IdPeticion del mensaje. %s", new Object[] { ex.getMessage() }), ex);
    }
  }
  
  protected Integer extractNumElementosFromMessage(OMElement message)
    throws AxisFault
  {
    try
    {
      String str = evalXPathExp("//*[local-name()='Atributos']/*[local-name()='NumElementos']/text()", message);
      return (str == null) || ("".equals(str)) ? null : Integer.valueOf(Integer.parseInt(str));
    }
    catch (Exception ex)
    {
      throw new RuntimeException(String.format("Error al obtener el elemento IdPeticion del mensaje. %s", new Object[] { ex.getMessage() }), ex);
    }
  }
  
  protected String extractCodigoEstadoFromMessage(OMElement message)
    throws AxisFault
  {
    try
    {
      return evalXPathExp("//*[local-name()='Atributos']/*[local-name()='CodigoEstado']/text()", message);
    }
    catch (Exception ex)
    {
      throw new RuntimeException(String.format("Error al obtener el elemento IdPeticion del mensaje. %s", new Object[] { ex.getMessage() }), ex);
    }
  }
  
  protected String extractTerFromMessage(OMElement message)
    throws AxisFault
  {
    try
    {
      return evalXPathExp("//*[local-name()='TiempoEstimadoRespuesta']/text()", message);
    }
    catch (Exception ex)
    {
      throw new RuntimeException(String.format("Error al leer el tiempo estimado de respuesta. %s", new Object[] { ex.getMessage() }), ex);
    }
  }
  
  protected String evalXPathExp(String exp, OMElement message)
    throws JaxenException
  {
    XpathManager manager = new XpathManager();
    return manager.evalXPathExp(exp, message);
  }
  
  protected String evalXPathExp(String exp, Element message)
  {
    XpathManager manager = new XpathManager();
    return manager.evalXPathExp(exp, message);
  }
  
  private List<OMElement> evalListXPathExp(String exp, OMElement message)
  {
    XpathManager manager = new XpathManager();
    return manager.evalListXPathExp(exp, message);
  }
  
  protected Servicio getService(OMElement message)
  {
    try
    {
      String codigoCertificado = extractCodigoCertificado(message);
      ServiceManager serviceManager = (ServiceManager)getBean("serviceManager");
      return serviceManager.selectServicio(codigoCertificado);
    }
    catch (Exception ex)
    {
      throw new RuntimeException(String.format("Error al determinar el servicio asociado al mensaje. %s", new Object[] { ex.getMessage() }), ex);
    }
  }
  
  protected static OMElement getChildWithName(OMElement parent, String name)
  {
    XpathManager manager = new XpathManager();
    return manager.getChildWithName(parent, name);
  }
  
  protected static OMElement getChildrenWithNameRecursive(OMElement parent, String name)
  {
    XpathManager manager = new XpathManager();
    return manager.getChildrenWithNameRecursive(parent, name);
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected boolean isModuleActive(MessageContext messageCtx, String moduleName, boolean inflow)
    throws RuntimeException
  {
	List<Modulo> modulos = (List)messageCtx.getProperty("ACTIVACION_MODULOS");
    if (modulos == null) {
      throw new RuntimeException(String.format("Error no se encuentra la configuración de los módulos en el contexto", new Object[0]));
    }
    for (Modulo modulo : modulos) {
      if (modulo.getNombre().equals(moduleName))
      {
        if (inflow) {
          return modulo.getActivoEntrada() == 1;
        }
        return modulo.getActivoSalida() == 1;
      }
    }
    throw new RuntimeException(String.format("No se encuentra la informacion de configuracion relativa al modulo %s. Revise la configuracion.", new Object[] { moduleName }));
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected String readParameter(String parameter, MessageContext messageCtx)
  {
	List<ParametroConfiguracion> parametros = (List)messageCtx.getProperty("PARAMETROS_GLOBALES");
    for (ParametroConfiguracion parametro : parametros) {
      if (parametro.getNombre().equals(parameter)) {
        return parametro.getValor();
      }
    }
    LOG.error("No existe en el contexto un parametro de configuración con el nombre : '" + parameter + "'");
    return "";
  }
  
  protected void checkNumeroSolicitudes(Peticion request, Servicio servicio, int sincrona)
    throws ScspException
  {
    String numeroElementosStr = request.getAtributos().getNumElementos();
    int numElementos;
    try
    {
      numElementos = Integer.parseInt(numeroElementosStr);
    }
    catch (NumberFormatException e)
    {
      String[] arg = { "Tag NumElementos inválido.Debe ser un valor entero" };
      throw ScspException.getScspException((Throwable)null, "0237", arg, request.getAtributos().getIdPeticion(), request.getAtributos().getCodigoCertificado());
    }
    if (request.getSolicitudes().getSolicitudTransmision().size() != numElementos)
    {
      String[] arg = { "Tag NumElementos inválido. No coincide con el numero de solicitudes de transmisión." };
      throw ScspException.getScspException((Throwable)null, "0237", arg, request.getAtributos().getIdPeticion(), request.getAtributos().getCodigoCertificado());
    }
    if ((sincrona == 1) && (numElementos > 1))
    {
      String[] arg = { "Una peticion síncrona, solo debe enviar una solicitud de transmisión." };
      throw ScspException.getScspException((Throwable)null, "0237", arg, request.getAtributos().getIdPeticion(), request.getAtributos().getCodigoCertificado());
    }
    if ((servicio.getMaxSolicitudesPeticion() > 0) && (numElementos > servicio.getMaxSolicitudesPeticion()))
    {
      String[] arg = { "El numero de solicitudes de transmisión enviados,supera al valor máximo configurado para el servicio." };
      throw ScspException.getScspException((Throwable)null, "0237", arg, request.getAtributos().getIdPeticion(), request.getAtributos().getCodigoCertificado());
    }
  }
  
  protected void checkNumeroReintentos(MessageContext message)
    throws AxisFault, OMException
  {
    PeticionRespuestaDao daoPeticion = (PeticionRespuestaDao)getBean("peticionRespuestaDao");
    Servicio servicio = (Servicio)message.getProperty("CONFIGURACION_SERVICIO");
    String idPeticion = extractIdPeticionFromMessage(message.getEnvelope().getBody());
    PeticionRespuesta peticionControl = daoPeticion.select(idPeticion);
    if (peticionControl == null)
    {
      String[] arg = { "La petición con identificador " + idPeticion + ", no existe" };
      
      throw ScspException.getScspException((Throwable)null, "0244", arg, idPeticion, servicio.getCodCertificado());
    }
    int numeroEnviosActual = peticionControl.getNumeroEnvios().intValue();
    if ((servicio.getNumeroMaximoReenvios() > 0) && (numeroEnviosActual >= servicio.getNumeroMaximoReenvios()))
    {
      String[] arg = { "Se ha alcanzado el número máximo de respuestas para la petición " + peticionControl.getIdPeticion() };
      throw ScspException.getScspException((Throwable)null, "0225", arg, idPeticion, servicio.getCodCertificado());
    }
  }
  
  protected void checkRespuestaProcesada(MessageContext message)
    throws AxisFault, OMException
  {
    PeticionRespuestaDao daoPeticion = (PeticionRespuestaDao)getBean("peticionRespuestaDao");
    Servicio servicio = (Servicio)message.getProperty("CONFIGURACION_SERVICIO");
    String idPeticion = extractIdPeticionFromMessage(message.getEnvelope().getBody());
    PeticionRespuesta peticionControl = daoPeticion.select(idPeticion);
    if (peticionControl == null)
    {
      String[] arg = { "La petición con identificador " + idPeticion + ", no existe" };
      
      throw ScspException.getScspException((Throwable)null, "0244", arg, idPeticion, servicio.getCodCertificado());
    }
    if ((peticionControl.getEstado() != null) && (peticionControl.getEstado().equals("0003")))
    {
      String[] arg = { "La petición ya posee un estado 0003 'Procesada', no se debe reenviar una solicitud de respuesta" };
      
      throw ScspException.getScspException((Throwable)null, "0225", arg, idPeticion, servicio.getCodCertificado());
    }
  }
  
  protected String getNifSolicitante(MessageContext message)
    throws AxisFault, OMException
  {
    String nifSolicitante = extractNifSolicitante(message.getEnvelope().getBody());
    if (nifSolicitante == null)
    {
      TransmisionDao transmisionDao = (TransmisionDao)getBean("transmisionDao");
      PeticionRespuestaDao peticionRespuestaDao = (PeticionRespuestaDao)getBean("peticionRespuestaDao");
      String idpeticion = extractIdPeticionFromMessage(message.getEnvelope().getBody());
      PeticionRespuesta peticion = peticionRespuestaDao.select(idpeticion);
      if (peticion == null)
      {
        LOG.warn("No existe en bbdd una peticion con el identificador : " + idpeticion);
        return null;
      }
      List<Transmision> transmisiones = transmisionDao.select(peticion);
      if ((transmisiones == null) || (transmisiones.size() < 0))
      {
        LOG.warn("No se encontraron transmisiones asociadas al petición por la que se requiere una solicitud de respuesta");
        return null;
      }
      return ((Transmision)transmisiones.get(0)).getIdSolicitante();
    }
    return nifSolicitante;
  }
}
