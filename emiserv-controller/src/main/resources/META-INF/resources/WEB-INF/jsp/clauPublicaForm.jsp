<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi" %>
<%
pageContext.setAttribute(
		"idioma",
		org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage());
%>
<c:choose>
	<c:when test="${empty clauPublicaCommand.id}"><c:set var="titol"><spring:message code="claupublica.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="claupublica.form.titol.modificar"/></c:set></c:otherwise>
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
</head>
<body>
	<c:set var="formAction"><emi:modalUrl value="/scsp/claupublica/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="clauPublicaCommand" role="form" style="height:400px;">
		<form:hidden path="id"/>
		<emi:inputText name="alies" required="true" inline="false" textKey="claupublica.form.camp.alies"/>
		<emi:inputText name="nom" required="true" inline="false" textKey="claupublica.form.camp.nom"/>
		<emi:inputText name="numSerie" required="true" inline="false" textKey="claupublica.form.camp.numserie"/>
		<emi:inputDate name="dataAlta" required="true" inline="false" textKey="claupublica.form.camp.dataalta"/>
		<emi:inputDate name="dataBaixa" required="false" inline="false" textKey="claupublica.form.camp.databaixa"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/scsp/claupublica"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
