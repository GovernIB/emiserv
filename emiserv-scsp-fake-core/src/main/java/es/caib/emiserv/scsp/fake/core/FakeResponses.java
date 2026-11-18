package es.caib.emiserv.scsp.fake.core;

import static es.caib.emiserv.scsp.fake.core.FakeNs.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Generador de respostes SCSP i de backoffice fake a partir d'una {@link FakeRequest}.
 */
public final class FakeResponses {

	private FakeResponses() {
	}

	public static String backofficeSyncResponse(FakeRequest requestInfo) {
		return backofficeSyncResponse(requestInfo, false);
	}

	public static String backofficeSyncResponse(FakeRequest requestInfo, boolean error) {
		return envelope(
			"<tns:peticionSincronaResponse xmlns:tns=\"" + BACKOFFICE_NS + "\">"
				+ "<respuesta>"
				+ backofficeRespuestaBody(requestInfo, error)
				+ "</respuesta>"
				+ "</tns:peticionSincronaResponse>");
	}

	public static String backofficeSolicitudResponse(FakeRequest requestInfo) {
		return backofficeSolicitudResponse(requestInfo, false);
	}

	public static String backofficeSolicitudResponse(FakeRequest requestInfo, boolean error) {
		return envelope(
			"<tns:solicitarRespuestaResponse xmlns:tns=\"" + BACKOFFICE_NS + "\">"
				+ "<respuesta>"
				+ backofficeRespuestaBody(requestInfo, error)
				+ "</respuesta>"
				+ "</tns:solicitarRespuestaResponse>");
	}

	public static String backofficeAsyncResponse(FakeRequest requestInfo) {
		return backofficeAsyncResponse(requestInfo, false);
	}

	public static String backofficeAsyncResponse(FakeRequest requestInfo, boolean error) {
		return envelope(
			"<tns:peticionAsincronaResponse xmlns:tns=\"" + BACKOFFICE_NS + "\">"
				+ "<respuesta>"
				+ backofficeAtributos(requestInfo, error)
				+ "</respuesta>"
				+ "</tns:peticionAsincronaResponse>");
	}

	/**
	 * Sobre SOAP amb la {@code Respuesta} SCSP (estat 0003 amb el literal indicat). Hi ha una
	 * {@code TransmisionDatos} per cada sol·licitud i totes porten les mateixes dades específiques.
	 */
	public static String scspResponse(FakeRequest requestInfo, String datosEspecificosXml, String literalEstat) {
		String ns = requestInfo.scspResponseNamespace();
		StringBuilder transmissions = new StringBuilder();
		int counter = 1;
		for (String idSolicitud : requestInfo.effectiveSolicitudIds()) {
			transmissions.append("<TransmisionDatos>")
				.append("<DatosGenericos>")
				.append("<Emisor>")
				.append("<NifEmisor>").append(esc(scspNifEmisor(requestInfo))).append("</NifEmisor>")
				.append("<NombreEmisor>").append(esc(defaultIfBlank(requestInfo.nomEmisor, "Govern de les Illes Balears"))).append("</NombreEmisor>")
				.append("</Emisor>")
				.append("<Solicitante>")
				.append("<IdentificadorSolicitante>").append(esc(defaultIfBlank(requestInfo.identificadorSolicitant, "S2816015H"))).append("</IdentificadorSolicitante>")
				.append("<NombreSolicitante>").append(esc(defaultIfBlank(requestInfo.nomSolicitant, "Límit Tecnologies"))).append("</NombreSolicitante>")
				.append("<UnidadTramitadora>").append(esc(defaultIfBlank(requestInfo.unitatTramitadora, "Dept."))).append("</UnidadTramitadora>")
				.append("<Procedimiento>")
				.append("<CodProcedimiento>").append(esc(defaultIfBlank(requestInfo.codiProcediment, "TEST"))).append("</CodProcedimiento>")
				.append("<NombreProcedimiento>").append(esc(defaultIfBlank(requestInfo.nomProcediment, "Procediment de test"))).append("</NombreProcedimiento>")
				.append("</Procedimiento>")
				.append("<Finalidad>").append(esc(defaultIfBlank(requestInfo.finalitat, "Finalitat de prova"))).append("</Finalidad>")
				.append("<Consentimiento>").append(esc(defaultIfBlank(requestInfo.consentiment, "Si"))).append("</Consentimiento>")
				.append("<Funcionario>")
				.append("<NombreCompletoFuncionario>").append(esc(defaultIfBlank(requestInfo.nomCompletFuncionari, "Admin"))).append("</NombreCompletoFuncionario>")
				.append("<NifFuncionario>").append(esc(defaultIfBlank(requestInfo.nifFuncionari, "12345678Z"))).append("</NifFuncionario>")
				.append("</Funcionario>")
				.append("</Solicitante>")
				.append("<Titular>")
				.append(optionalTag("TipoDocumentacion", requestInfo.tipusDocumentacio))
				.append(optionalTag("Documentacion", requestInfo.titularDocumentacio))
				.append(optionalTag("Nombre", requestInfo.titularNom))
				.append(optionalTag("Apellido1", requestInfo.titularLlinatge1))
				.append(optionalTag("Apellido2", requestInfo.titularLlinatge2))
				.append("</Titular>")
				.append("<Transmision>")
				.append("<CodigoCertificado>").append(esc(requestInfo.serviceCode)).append("</CodigoCertificado>")
				.append("<IdSolicitud>").append(esc(idSolicitud)).append("</IdSolicitud>")
				.append("<IdTransmision>").append(esc(requestInfo.idPeticion)).append("-").append(counter).append("</IdTransmision>")
				.append("<FechaGeneracion>2026-04-27T10:00:00+02:00</FechaGeneracion>")
				.append("</Transmision>")
				.append("</DatosGenericos>")
				.append(datosEspecificosXml)
				.append("</TransmisionDatos>");
			counter++;
		}
		return envelope(
			"<Respuesta xmlns=\"" + ns + "\">"
				+ "<Atributos>"
				+ "<CodigoCertificado>" + esc(requestInfo.serviceCode) + "</CodigoCertificado>"
				+ "<IdPeticion>" + esc(requestInfo.idPeticion) + "</IdPeticion>"
				+ "<NumElementos>" + esc(requestInfo.numElementos) + "</NumElementos>"
				+ "<TimeStamp>2026-04-27T10:00:00+02:00</TimeStamp>"
				+ "<Estado><CodigoEstado>0003</CodigoEstado><LiteralError>" + esc(literalEstat) + "</LiteralError></Estado>"
				+ "</Atributos>"
				+ "<Transmisiones>" + transmissions + "</Transmisiones>"
				+ "</Respuesta>");
	}

	/** Element {@code DatosEspecificos} complet segons el servei de la petició. */
	public static String datosEspecificos(FakeRequest requestInfo, boolean noData) {
		if ("SVDSCTFNWS01".equalsIgnoreCase(requestInfo.serviceCode)) {
			return familiaNombrosaDatosEspecificos(requestInfo, noData);
		}
		if ("SCDCPAJU".equalsIgnoreCase(requestInfo.serviceCode)) {
			return padroDatosEspecificos(requestInfo, noData);
		}
		if ("SCDHPAJU".equalsIgnoreCase(requestInfo.serviceCode)) {
			return padroHistoricDatosEspecificos(requestInfo, noData);
		}
		return "<DatosEspecificos xmlns=\"" + DATOS_ESPECIFICOS_NS + "\">"
			+ "<Retorno>"
			+ "<Estado>"
			+ "<CodigoEstado>0</CodigoEstado>"
			+ "<LiteralError>Existe el titulo de familia numerosa</LiteralError>"
			+ "</Estado>"
			+ "<TituloFamiliaNumerosaRetorno>"
			+ "<CodigoComunidadAutonoma>" + esc(defaultIfBlank(requestInfo.comunitatAutonoma, "04")) + "</CodigoComunidadAutonoma>"
			+ "<NumeroTitulo>FAKE-" + esc(defaultIfBlank(requestInfo.idPeticion, "PET")) + "</NumeroTitulo>"
			+ "<Categoria>G</Categoria>"
			+ "<TituloVigente>S</TituloVigente>"
			+ "<FechaCaducidad>31/12/2035</FechaCaducidad>"
			+ "<NumeroHijos>2</NumeroHijos>"
			+ "</TituloFamiliaNumerosaRetorno>"
			+ "</Retorno>"
			+ "</DatosEspecificos>";
	}

	/**
	 * Volant de padró (SCDCPAJU) amb dades inventades, vàlid segons l'XSD del servei: la persona
	 * empadronada és el titular consultat, al municipi de la sol·licitud.
	 */
	private static String padroDatosEspecificos(FakeRequest requestInfo, boolean noData) {
		String provincia = defaultIfBlank(requestInfo.provinciaSolicitud, "07");
		String municipi = defaultIfBlank(requestInfo.municipioSolicitud, "040");
		String document = padroDocument(requestInfo);
		if (noData) {
			return padroSenseDades(requestInfo, provincia, municipi, document);
		}
		return "<DatosEspecificos xmlns=\"" + DATOS_ESPECIFICOS_NS + "\">"
			+ padroEstatISolicitud(requestInfo, provincia, municipi, document)
			+ "<Resultado>"
			+ "<ClaveHojaPadronal><Distrito>01</Distrito><Seccion>001</Seccion><Hoja>0000001</Hoja></ClaveHojaPadronal>"
			+ "<Domicilio>"
			+ "<Via><Codigo>00001</Codigo><Tipo>CL</Tipo><Nombre>CARRER DE PROVES</Nombre></Via>"
			+ "<Numero><Valor>0001</Valor></Numero>"
			+ "<CodPostal>07" + esc(sanitizeDigits(municipi, "000")) + "</CodPostal>"
			+ "</Domicilio>"
			+ "<Personas><Persona>"
			+ "<Nombre>" + esc(defaultIfBlank(requestInfo.titularNom, "PERSONA")) + "</Nombre>"
			+ "<Apellido1>" + esc(defaultIfBlank(requestInfo.titularLlinatge1, "DE PROVES")) + "</Apellido1>"
			+ "<FechaNacimiento>1980-01-01</FechaNacimiento>"
			+ "<Documentacion><Tipo>NIF</Tipo><Valor>" + esc(document) + "</Valor></Documentacion>"
			+ "<NIA>" + esc(provincia + municipi) + "0000000001</NIA>"
			+ "<PeriodoInscripcion><Desde>2020-01-01</Desde>"
			+ "<MotivoInscripcion><CodigoVariacion>A</CodigoVariacion><CausaVariacion>CR</CausaVariacion>"
			+ "<Descripcion>Alta por cambio de residencia</Descripcion></MotivoInscripcion>"
			+ "</PeriodoInscripcion>"
			+ "</Persona></Personas>"
			+ "<FechaExpedicion>2026-01-01</FechaExpedicion>"
			+ "</Resultado>"
			+ "</DatosEspecificos>";
	}

	/**
	 * Padró històric (SCDHPAJU) amb dades inventades, vàlid segons l'XSD del servei: el titular
	 * consultat amb un sol domicili, al municipi de la sol·licitud.
	 */
	private static String padroHistoricDatosEspecificos(FakeRequest requestInfo, boolean noData) {
		String provincia = defaultIfBlank(requestInfo.provinciaSolicitud, "07");
		String municipi = defaultIfBlank(requestInfo.municipioSolicitud, "040");
		String document = padroDocument(requestInfo);
		if (noData) {
			return padroSenseDades(requestInfo, provincia, municipi, document);
		}
		return "<DatosEspecificos xmlns=\"" + DATOS_ESPECIFICOS_NS + "\">"
			+ padroEstatISolicitud(requestInfo, provincia, municipi, document)
			+ "<Resultado>"
			+ "<Nombre>" + esc(defaultIfBlank(requestInfo.titularNom, "PERSONA")) + "</Nombre>"
			+ "<Apellido1>" + esc(defaultIfBlank(requestInfo.titularLlinatge1, "DE PROVES")) + "</Apellido1>"
			+ "<FechaNacimiento>1980-01-01</FechaNacimiento>"
			+ "<Documentacion><Tipo>NIF</Tipo><Valor>" + esc(document) + "</Valor></Documentacion>"
			+ "<HistoricoDomicilios><Domicilio>"
			+ "<ClaveHojaPadronal><Distrito>01</Distrito><Seccion>001</Seccion><Hoja>0000001</Hoja></ClaveHojaPadronal>"
			+ "<ProvinciaRespuesta><Codigo>" + esc(provincia) + "</Codigo><Nombre>ILLES BALEARS</Nombre></ProvinciaRespuesta>"
			+ "<MunicipioRespuesta><Codigo>" + esc(municipi) + "</Codigo><Nombre>MUNICIPI DE PROVES</Nombre></MunicipioRespuesta>"
			+ "<Direccion>"
			+ "<Via><Codigo>00001</Codigo><Tipo>CL</Tipo><Nombre>CARRER DE PROVES</Nombre></Via>"
			+ "<Numero><Valor>0001</Valor></Numero>"
			+ "<CodPostal>07" + esc(sanitizeDigits(municipi, "000")) + "</CodPostal>"
			+ "</Direccion>"
			+ "<Desde>2020-01-01</Desde>"
			+ "<MotivoInscripcion><CodigoVariacion>A</CodigoVariacion><CausaVariacion>CR</CausaVariacion>"
			+ "<Descripcion>Alta por cambio de residencia</Descripcion></MotivoInscripcion>"
			+ "</Domicilio></HistoricoDomicilios>"
			+ "<FechaExpedicion>2026-01-01</FechaExpedicion>"
			+ "</Resultado>"
			+ "</DatosEspecificos>";
	}

	/** Resposta de padró sense cap inscripció: l'estat i l'eco de la sol·licitud, sense {@code Resultado}. */
	private static String padroSenseDades(FakeRequest requestInfo, String provincia, String municipi, String document) {
		return "<DatosEspecificos xmlns=\"" + DATOS_ESPECIFICOS_NS + "\">"
			+ "<Estado>"
			+ "<CodigoEstado>0003</CodigoEstado>"
			+ "<LiteralError>No s&apos;ha trobat cap inscripció</LiteralError>"
			+ "</Estado>"
			+ padroSolicitud(requestInfo, provincia, municipi, document)
			+ "</DatosEspecificos>";
	}

	private static String padroDocument(FakeRequest requestInfo) {
		return defaultIfBlank(requestInfo.documentSolicitud, defaultIfBlank(requestInfo.titularDocumentacio, "12345678Z"));
	}

	/** Estat i sol·licitud (eco de la petició) comuns als dos serveis de padró. */
	private static String padroEstatISolicitud(FakeRequest requestInfo, String provincia, String municipi, String document) {
		return "<Estado>"
			+ "<CodigoEstado>0003</CodigoEstado>"
			+ "<LiteralError>TRAMITADA</LiteralError>"
			+ "</Estado>"
			+ padroSolicitud(requestInfo, provincia, municipi, document);
	}

	private static String padroSolicitud(FakeRequest requestInfo, String provincia, String municipi, String document) {
		return "<Solicitud>"
			+ "<ProvinciaSolicitud>" + esc(provincia) + "</ProvinciaSolicitud>"
			+ "<MunicipioSolicitud>" + esc(municipi) + "</MunicipioSolicitud>"
			+ "<Titular><Documentacion>"
			+ "<Tipo>" + esc(defaultIfBlank(requestInfo.tipusDocumentacio, "NIF")) + "</Tipo>"
			+ "<Valor>" + esc(document) + "</Valor>"
			+ "</Documentacion></Titular>"
			+ "</Solicitud>";
	}

	private static String familiaNombrosaDatosEspecificos(FakeRequest requestInfo, boolean noData) {
		if (noData) {
			return "<DatosEspecificos xmlns=\"" + DATOS_ESPECIFICOS_NS + "\">"
				+ "<Retorno>"
				+ "<Estado>"
				+ "<CodigoEstado>7</CodigoEstado>"
				+ "<LiteralError>No s&apos;ha trobat informació</LiteralError>"
				+ "</Estado>"
				+ "</Retorno>"
				+ "</DatosEspecificos>";
		}
		String entitat = defaultIfBlank(requestInfo.entityCode, "MALLORCA").toUpperCase();
		String codiTitol = "TFN-" + entitat + "-" + sanitizeDigits(requestInfo.titularDocumentacio, "18225486") + "-2026";
		return "<DatosEspecificos xmlns=\"" + DATOS_ESPECIFICOS_NS + "\">"
			+ "<Retorno>"
			+ "<Estado>"
			+ "<CodigoEstado>0</CodigoEstado>"
			+ "<LiteralError>Existe el titulo de familia numerosa</LiteralError>"
			+ "</Estado>"
			+ "<TituloFamiliaNumerosaRetorno>"
			+ "<CodigoComunidadAutonoma>" + esc(defaultIfBlank(requestInfo.comunitatAutonoma, "04")) + "</CodigoComunidadAutonoma>"
			+ "<NumeroTitulo>" + esc(codiTitol) + "</NumeroTitulo>"
			+ "<Categoria>G</Categoria>"
			+ "<TituloVigente>S</TituloVigente>"
			+ "<FechaExpedicion>01/01/2024</FechaExpedicion>"
			+ "<FechaCaducidad>31/12/2035</FechaCaducidad>"
			+ "<NumeroHijos>3</NumeroHijos>"
			+ "</TituloFamiliaNumerosaRetorno>"
			+ "<ListaBeneficiariosRetorno>"
			+ "<BeneficiarioRetorno>"
			+ optionalTag("TipoDocumentacion", requestInfo.tipusDocumentacio)
			+ optionalTag("Documentacion", defaultIfBlank(requestInfo.titularDocumentacio, "18225486X"))
			+ optionalTag("FechaNacimiento", requestInfo.titularDataNaixement)
			+ optionalTag("Nombre", defaultIfBlank(requestInfo.titularNom, "Sion"))
			+ optionalTag("Apellido1", defaultIfBlank(requestInfo.titularLlinatge1, "Andreu"))
			+ optionalTag("Apellido2", requestInfo.titularLlinatge2)
			+ "<Titular>S</Titular>"
			+ "</BeneficiarioRetorno>"
			+ "</ListaBeneficiariosRetorno>"
			+ "</Retorno>"
			+ "</DatosEspecificos>";
	}

	private static String scspNifEmisor(FakeRequest requestInfo) {
		if ("SVDSCTFNWS01".equalsIgnoreCase(requestInfo.serviceCode)) {
			return "S2833002E";
		}
		return defaultIfBlank(requestInfo.nifEmisor, "S2833002E");
	}

	private static String scspSoapFaultAtributosNs(FakeRequest requestInfo) {
		if (requestInfo != null && SCSP_V3_RESPUESTA_NS.equals(requestInfo.scspResponseNamespace())) {
			return SCSP_V3_SOAPFAULT_ATRIBUTOS_NS;
		}
		return SCSP_V3_SOAPFAULT_ATRIBUTOS_NS;
	}

	public static String soapFault(String faultCode, String faultString) {
		return envelope(
			"<soapenv:Fault>"
				+ "<faultcode>soapenv:" + esc(faultCode) + "</faultcode>"
				+ "<faultstring>" + esc(faultString) + "</faultstring>"
				+ "</soapenv:Fault>");
	}

	/** SOAP Fault SCSP amb els atributs de la petició, el codi d'estat i el missatge indicats. */
	public static String scspFault(FakeRequest requestInfo, String codigoEstado, String literalError) {
		String faultCode = "Server";
		return envelope(
			"<soapenv:Fault>"
				+ "<faultcode>soapenv:" + esc(faultCode) + "</faultcode>"
				+ "<faultstring>" + esc(defaultIfBlank(literalError, "Error forcat pel fake SCSP")) + "</faultstring>"
				+ "<detail>"
				+ "<a:Atributos xmlns:a=\"" + scspSoapFaultAtributosNs(requestInfo) + "\">"
				+ "<a:IdPeticion>" + esc(defaultIfBlank(requestInfo.idPeticion, "FAKE-PET")) + "</a:IdPeticion>"
				+ "<a:NumElementos>" + esc(defaultIfBlank(requestInfo.numElementos, "1")) + "</a:NumElementos>"
				+ "<a:TimeStamp>2026-04-27T10:00:00+02:00</a:TimeStamp>"
				+ "<a:CodigoCertificado>" + esc(defaultIfBlank(requestInfo.serviceCode, "FAKE_CERT")) + "</a:CodigoCertificado>"
				+ "<a:Estado>"
				+ "<a:CodigoEstado>" + esc(defaultIfBlank(codigoEstado, "0101")) + "</a:CodigoEstado>"
				+ "<a:LiteralError>" + esc(defaultIfBlank(literalError, "Error forcat pel fake SCSP")) + "</a:LiteralError>"
				+ "</a:Estado>"
				+ "</a:Atributos>"
				+ "</detail>"
				+ "</soapenv:Fault>");
	}

	private static String backofficeRespuestaBody(FakeRequest requestInfo, boolean error) {
		if (error) {
			return backofficeAtributos(requestInfo, true);
		}
		StringBuilder transmissions = new StringBuilder();
		int counter = 1;
		for (String idSolicitud : requestInfo.effectiveSolicitudIds()) {
			transmissions.append("<transmisionDatos>")
				.append("<datosGenericos>")
				.append("<emisor>")
				.append("<nifEmisor>").append(esc(backofficeNifEmisor(requestInfo))).append("</nifEmisor>")
				.append("<nombreEmisor>").append(esc(backofficeNombreEmisor(requestInfo))).append("</nombreEmisor>")
				.append("</emisor>")
				.append("<solicitante>")
				.append("<identificadorSolicitante>").append(esc(backofficeIdentificadorSolicitante(requestInfo))).append("</identificadorSolicitante>")
				.append("<nombreSolicitante>").append(esc(shortDefault(requestInfo.nomSolicitant, "Limit Tecnologies", 50))).append("</nombreSolicitante>")
				.append("<unidadTramitadora>").append(esc(shortDefault(requestInfo.unitatTramitadora, "Dept.", 250))).append("</unidadTramitadora>")
				.append("<procedimiento>")
				.append("<codProcedimiento>").append(esc(shortDefault(requestInfo.codiProcediment, "TEST", 20))).append("</codProcedimiento>")
				.append("<nombreProcedimiento>").append(esc(shortDefault(requestInfo.nomProcediment, "Procediment de test", 100))).append("</nombreProcedimiento>")
				.append("</procedimiento>")
				.append("<finalidad>").append(esc(shortDefault(requestInfo.finalitat, "Finalitat de prova", 250))).append("</finalidad>")
				.append("<consentimiento>").append(esc(backofficeConsentimiento(requestInfo))).append("</consentimiento>")
				.append("<funcionario>")
				.append("<nombreCompletoFuncionario>").append(esc(shortDefault(requestInfo.nomCompletFuncionari, "Admin Test", 122))).append("</nombreCompletoFuncionario>")
				.append("<nifFuncionario>").append(esc(backofficeNifFuncionario(requestInfo))).append("</nifFuncionario>")
				.append("</funcionario>")
				.append(optionalLowerTag("idExpediente", shortDefault(null, "EXP-FAKE", 25)))
				.append("</solicitante>")
				.append("<titular>")
				.append("<tipoDocumentacion>").append(esc(backofficeTipoDocumentacion(requestInfo))).append("</tipoDocumentacion>")
				.append("<documentacion>").append(esc(shortDefault(FakeRequest.firstNonBlank(requestInfo.documentSolicitud, requestInfo.titularDocumentacio), "12345678Z", 14))).append("</documentacion>")
				.append(optionalLowerTag("nombre", shortDefault(requestInfo.titularNom, "Sion", 40)))
				.append(optionalLowerTag("apellido1", shortDefault(requestInfo.titularLlinatge1, "Andreu", 40)))
				.append(optionalLowerTag("apellido2", shortDefault(requestInfo.titularLlinatge2, "Test", 40)))
				.append("</titular>")
				.append("<transmision>")
				.append("<codigoCertificado>").append(esc(requestInfo.serviceCode)).append("</codigoCertificado>")
				.append("<fechaGeneracion>2026-04-27T10:00:00+02:00</fechaGeneracion>")
				.append("<idSolicitud>").append(esc(idSolicitud)).append("</idSolicitud>")
				.append("<idTransmision>").append(esc(requestInfo.idPeticion)).append("-").append(counter).append("</idTransmision>")
				.append("</transmision>")
				.append("</datosGenericos>")
				.append(backofficeDatosEspecificos(requestInfo))
				.append("</transmisionDatos>");
			counter++;
		}
		return backofficeAtributos(requestInfo, false)
			+ "<transmisiones>" + transmissions + "</transmisiones>";
	}

	private static String backofficeAtributos(FakeRequest requestInfo, boolean error) {
		String codigoEstado = error ? "0101" : "0003";
		String literalError = error ? "Error forcat pel fake backoffice" : "OK";
		String numElementos = error ? "0" : requestInfo.numElementos;
		return "<atributos>"
			+ "<codigoCertificado>" + esc(requestInfo.serviceCode) + "</codigoCertificado>"
			+ "<estado>"
			+ "<codigoEstado>" + esc(codigoEstado) + "</codigoEstado>"
			+ "<literalError>" + esc(literalError) + "</literalError>"
			+ "<tiempoEstimadoRespuesta>0</tiempoEstimadoRespuesta>"
			+ "</estado>"
			+ "<idPeticion>" + esc(requestInfo.idPeticion) + "</idPeticion>"
			+ "<numElementos>" + esc(numElementos) + "</numElementos>"
			+ "<timeStamp>2026-04-27T10:00:00+02:00</timeStamp>"
			+ "</atributos>";
	}

	private static String backofficeDatosEspecificos(FakeRequest requestInfo) {
		if ("SVDSCTFNWS01".equalsIgnoreCase(requestInfo.serviceCode)
				|| "SCDCPAJU".equalsIgnoreCase(requestInfo.serviceCode)
				|| "SCDHPAJU".equalsIgnoreCase(requestInfo.serviceCode)) {
			// Mateixes dades que el servei SCSP, dins l'element datosEspecificos del backoffice (sense namespace)
			String datosEspecificos = datosEspecificos(requestInfo, requestInfo.noData);
			return "<datosEspecificos>"
				+ datosEspecificos.substring(datosEspecificos.indexOf('>') + 1, datosEspecificos.lastIndexOf("</DatosEspecificos>"))
				+ "</datosEspecificos>";
		}
		return "<datosEspecificos>"
			+ "<Retorno>"
			+ "<Estado>"
			+ "<CodigoEstado>0</CodigoEstado>"
			+ "<LiteralError>Resposta fake backoffice</LiteralError>"
			+ "</Estado>"
			+ "</Retorno>"
			+ "</datosEspecificos>";
	}

	private static String backofficeNifEmisor(FakeRequest requestInfo) {
		if ("SVDCCAACPASWS01".equalsIgnoreCase(requestInfo.serviceCode)
				|| "SVDCCAACPCWS01".equalsIgnoreCase(requestInfo.serviceCode)) {
			return "S2833002E";
		}
		return shortDefault(requestInfo.nifEmisor, "S2833002E", 10);
	}

	private static String backofficeNombreEmisor(FakeRequest requestInfo) {
		if ("SVDCCAACPASWS01".equalsIgnoreCase(requestInfo.serviceCode)
				|| "SVDCCAACPCWS01".equalsIgnoreCase(requestInfo.serviceCode)) {
			return "MINHAP";
		}
		return shortDefault(requestInfo.nomEmisor, "Govern de les Illes Balears", 50);
	}

	private static String backofficeIdentificadorSolicitante(FakeRequest requestInfo) {
		String value = defaultIfBlank(requestInfo.identificadorSolicitant, "S2816015H");
		if (value.matches("[A-Za-z][0-9]{7}[0-9A-Za-z]")) {
			return value;
		}
		return "S2816015H";
	}

	private static String backofficeConsentimiento(FakeRequest requestInfo) {
		String value = defaultIfBlank(requestInfo.consentiment, "Si");
		if ("Ley".equals(value) || "Si".equals(value) || "NoOpo".equals(value)) {
			return value;
		}
		return "Si";
	}

	private static String backofficeTipoDocumentacion(FakeRequest requestInfo) {
		String value = defaultIfBlank(requestInfo.tipusDocumentacio, "NIF");
		if ("CIF".equals(value) || "NIF".equals(value) || "DNI".equals(value) || "NIE".equals(value)
				|| "Pasaporte".equals(value) || "NumeroIdentificacion".equals(value) || "Otros".equals(value) || "CSV".equals(value)) {
			return value;
		}
		return "NIF";
	}

	private static String backofficeNifFuncionario(FakeRequest requestInfo) {
		String value = defaultIfBlank(requestInfo.nifFuncionari, "12345678Z");
		if (value.matches("(([XxYyZz][0-9]{7}[A-Za-z])|([0-9]{8}[A-Za-z]))")) {
			return value;
		}
		return "12345678Z";
	}

	private static String optionalLowerTag(String tagName, String value) {
		if (value == null || value.isBlank()) {
			return "";
		}
		return "<" + tagName + ">" + esc(value) + "</" + tagName + ">";
	}

	private static String shortDefault(String value, String defaultValue, int maxLength) {
		String result = defaultIfBlank(value, defaultValue);
		if (result.length() <= maxLength) {
			return result;
		}
		return result.substring(0, maxLength);
	}

	private static String envelope(String body) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<soapenv:Envelope xmlns:soapenv=\"" + SOAP_NS + "\">"
			+ "<soapenv:Body>"
			+ body
			+ "</soapenv:Body>"
			+ "</soapenv:Envelope>";
	}

	private static String esc(String value) {
		if (value == null) {
			return "";
		}
		return value
			.replace("&", "&amp;")
			.replace("<", "&lt;")
			.replace(">", "&gt;")
			.replace("\"", "&quot;")
			.replace("'", "&apos;");
	}

	private static String defaultIfBlank(String value, String defaultValue) {
		return value == null || value.isBlank() ? defaultValue : value;
	}

	private static String optionalTag(String tagName, String value) {
		if (value == null || value.isBlank()) {
			return "";
		}
		return "<" + tagName + ">" + esc(value) + "</" + tagName + ">";
	}

	private static String sanitizeDigits(String value, String defaultValue) {
		String source = defaultIfBlank(value, defaultValue);
		StringBuilder digits = new StringBuilder();
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			if (Character.isLetterOrDigit(c)) {
				digits.append(c);
			}
		}
		return digits.length() == 0 ? defaultValue : digits.toString();
	}

}
