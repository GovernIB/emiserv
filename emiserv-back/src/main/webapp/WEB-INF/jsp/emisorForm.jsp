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
	<c:when test="${empty emisorCommand.id}"><c:set var="titol"><spring:message code="emisor.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="emisor.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.${idioma}.js"/>"></script>
	<emi:modalHead/>
</head>
<body>
	<c:set var="formAction"><emi:modalUrl value="/emisor/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="emisorCommand" role="form" style="height:400px;">
		<form:hidden path="id"/>
		<emi:inputText name="cif" textKey="emisor.form.camp.cif" required="true"/>
		<emi:inputText name="nom" textKey="emisor.form.camp.nom" required="true"/>
		<emi:inputDate name="dataAlta" textKey="emisor.form.camp.data.alta"/>
		<emi:inputDate name="dataBaixa" textKey="emisor.form.camp.data.baixa"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/emisor"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
