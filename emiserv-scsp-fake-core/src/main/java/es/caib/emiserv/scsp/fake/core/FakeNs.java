package es.caib.emiserv.scsp.fake.core;

/** Espais de noms i constants comunes del generador SCSP fake. */
final class FakeNs {

	static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
	static final String BACKOFFICE_NS = "http://caib.es/emiserv/backoffice";
	static final String DEFAULT_SCSP_NS = "http://intermediacion.redsara.es/scsp/esquemas/solicitudrespuesta";
	static final String SCSP_V3_PETICION_NS = "http://intermediacion.redsara.es/scsp/esquemas/V3/peticion";
	static final String SCSP_V3_RESPUESTA_NS = "http://intermediacion.redsara.es/scsp/esquemas/V3/respuesta";
	static final String SCSP_V3_SOAPFAULT_ATRIBUTOS_NS = "http://intermediacion.redsara.es/scsp/esquemas/V3/soapfaultatributos";
	static final String DATOS_ESPECIFICOS_NS = "http://intermediacion.redsara.es/scsp/esquemas/datosespecificos";
	static final String WSSE_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	static final String WSU_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
	static final String DS_NS = "http://www.w3.org/2000/09/xmldsig#";
	static final String X509_V3_TYPE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3";
	static final String BASE64_BINARY_TYPE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary";

	private FakeNs() {
	}
}
