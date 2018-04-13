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
<c:choose>
	<c:when test="${empty serveiCommand.id}"><c:set var="titol"><spring:message code="aplicacio.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="aplicacio.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
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

});
</script>
</head>
<body>
	<c:set var="formAction"><emi:modalUrl value="/aplicacio/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="aplicacioCommand" role="form" style="height:400px;">
		<form:hidden path="id"/>
		<emi:inputText name="certificatNif" textKey="aplicacio.form.camp.cif" required="true"/>
		<emi:inputText name="cn" textKey="aplicacio.form.camp.nom" required="true"/>
		<emi:inputSelect name="codiCa" textKey="aplicacio.form.camp.ca" required="true" optionItems="${autoritatsCertificacio}" optionValueAttribute="codi" optionTextAttribute="nom"/>
		<emi:inputText name="numeroSerie" textKey="aplicacio.form.camp.num.serie" required="true"/>
		<emi:inputDate name="dataAlta" textKey="organisme.form.camp.data.alta"/>
		<emi:inputDate name="dataBaixa" textKey="organisme.form.camp.data.baixa"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/aplicacio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
