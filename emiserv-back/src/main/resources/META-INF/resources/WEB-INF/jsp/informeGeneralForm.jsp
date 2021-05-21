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
<html>
<head>
	<title><spring:message code="informe.general.estat.filtre.dates.titol"/></title>
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
	$('#formGenerar').submit(function(){
		$(this).find('div.has-error').removeClass('has-error');
		$(this).find('p.help-block').remove();
	});
});
</script>
</head>
<body>
	<c:set var="formAction"><emi:modalUrl value="/informe/generalEstat"/></c:set>
	<form:form  id="formGenerar" action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="informeCommand" role="form" style="height:400px;">
		<emi:inputDate name="dataInici" textKey="auditoria.list.filtre.data.inici" required="true"/>
		<emi:inputDate name="dataFi" textKey="auditoria.list.filtre.data.fi" required="true"/>
		<emi:inputSelect name="tipusPeticio" textKey="informe.general.estat.filtre.tipus.peticio" emptyOption="true" emptyOptionTextKey="informe.general.estat.filtre.tipus.peticio.tots" placeholderKey="informe.general.estat.filtre.tipus.peticio.tots" optionItems="${tipusPeticioOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-download"></span> <spring:message code="informe.list.generar"/></button>			
			<a href="<c:url value="/informe"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
