<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%
pageContext.setAttribute(
		"idioma",
		org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage());
%>
<c:set var="titol"><spring:message code="servei.config.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${idioma}.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.${idioma}.js"/>"></script>
	<emi:modalHead/>
<script>
$(document).ready(function() {
	$("form input#xsdGestioActiva").on('change', function() {
		$('form input#esquemas').prop(
				"disabled",
				$(this).prop('checked'));
	});
	$("form input#xsdGestioActiva").trigger('change');
});
</script>
</head>
<body>
	<c:set var="formAction"><emi:modalUrl value="/servei/${servei.id}/configScsp"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="serveiConfigScspCommand" role="form">
		<form:hidden path="codigoCertificado"/>
		<emi:inputSelect name="emisorId" textKey="servei.config.camp.emisor" optionItems="${emissors}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true" emptyOptionTextKey="comu.opcio.sense.definir" labelSize="2"/>
		<fieldset>
			<legend><spring:message code="servei.config.agrupacio.validesa"/></legend>
			<div class="row">
				<div class="col-sm-6">
					<emi:inputDate name="fechaAlta" textKey="servei.config.camp.fechaAlta" required="true"/>
				</div>
				<div class="col-sm-6">
					<emi:inputDate name="fechaBaja" textKey="servei.config.camp.fechaBaja"/>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-6">
					<emi:inputText name="caducidad" textKey="servei.config.camp.caducidad"/>
				</div>
				<div class="col-sm-6">
				</div>
			</div>
		</fieldset>
		<fieldset>
			<legend><spring:message code="servei.config.agrupacio.ubicacio"/></legend>
			<emi:inputText name="urlSincrona" textKey="servei.config.camp.urlSincrona" labelSize="2"/>
			<emi:inputText name="urlAsincrona" textKey="servei.config.camp.urlAsincrona" labelSize="2"/>
			<div class="row">
				<div class="col-sm-6">
					<emi:inputText name="actionSincrona" textKey="servei.config.camp.actionSincrona"/>
				</div>
				<div class="col-sm-6">
					<emi:inputText name="actionAsincrona" textKey="servei.config.camp.actionAsincrona"/>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-6">
					<emi:inputText name="actionSolicitud" textKey="servei.config.camp.actionSolicitud"/>
				</div>
				<div class="col-sm-6">
				</div>
			</div>
		</fieldset>
		<fieldset>
			<legend><spring:message code="servei.config.agrupacio.xsd"/></legend>
			<div class="row">
				<div class="col-sm-6">
					<emi:inputCheckbox name="xsdGestioActiva" textKey="servei.config.camp.mant.xsd"/>
				</div>
				<div class="col-sm-6">
					<emi:inputText name="versionEsquema" textKey="servei.config.camp.versionEsquema" required="true"/>
				</div>
			</div>
			<emi:inputText name="esquemas" textKey="servei.config.camp.esquemas" labelSize="2"/>
		</fieldset>
		<fieldset>
			<legend><spring:message code="servei.config.agrupacio.xifrat"/></legend>
			<div class="row">
				<div class="col-sm-6">
					<emi:inputSelect name="tipoSeguridad" textKey="servei.config.camp.tipoSeguridad" required="true" optionItems="${tipoSeguridadItems}" emptyOption="true" emptyOptionTextKey="comu.opcio.sense.definir"/>
				</div>
				<div class="col-sm-6">
				</div>
			</div>
			<div class="row">
				<div class="col-sm-6">
					<emi:inputSelect name="claveFirma" textKey="servei.config.camp.claveFirma" required="true" optionItems="${clausPrivades}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true" emptyOptionTextKey="comu.opcio.sense.definir"/>
				</div>
				<div class="col-sm-6">
					<emi:inputSelect name="claveCifrado" textKey="servei.config.camp.claveCifrado" optionItems="${clausPubliques}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true" emptyOptionTextKey="comu.opcio.sense.definir"/>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-6">
					<emi:inputText name="xpathCifradoSincrono" textKey="servei.config.camp.xpathCifradoSincrono"/>
				</div>
				<div class="col-sm-6">
					<emi:inputText name="xpathCifradoAsincrono" textKey="servei.config.camp.xpathCifradoAsincrono"/>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-6">
					<emi:inputText name="algoritmoCifrado" textKey="servei.config.camp.algoritmoCifrado"/>
				</div>
				<div class="col-sm-6">
					<emi:inputText name="validacionFirma" textKey="servei.config.camp.validacionFirma"/>
				</div>
			</div>
		</fieldset>
		<fieldset>
			<legend><spring:message code="servei.config.agrupacio.params"/></legend>
			<div class="row">
				<div class="col-sm-6">
					<emi:inputText name="prefijoPeticion" textKey="servei.config.camp.prefijoPeticion"/>
				</div>
	  			<div class="col-sm-6">
					<emi:inputText name="prefijoIdTransmision" textKey="servei.config.camp.prefijoIdTransmision"/>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-6">
					<emi:inputText name="numeroMaximoReenvios" textKey="servei.config.camp.numeroMaximoReenvios"/>
				</div>
	  			<div class="col-sm-6">
					<emi:inputText name="maxSolicitudesPeticion" textKey="servei.config.camp.maxSolicitudesPeticion"/>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-6">
					<emi:inputText name="xpathCodigoError" textKey="servei.config.camp.xpathCodigoError"/>
				</div>
	  			<div class="col-sm-6">
					<emi:inputText name="xpathLiteralError" textKey="servei.config.camp.xpathLiteralError"/>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-6">
					<emi:inputText name="timeout" textKey="servei.config.camp.timeout"/>
				</div>
	  			<div class="col-sm-6">
				</div>
			</div>
		</fieldset>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/servei"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
