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
	<title><spring:message code="modul.form.titol"/></title>
	<emi:modalHead/>
</head>
<body>
	<c:set var="formAction"><emi:modalUrl value="/scsp/modul/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="scspModulCommand" role="form" style="height:400px;">
		<form:hidden path="nom"/>
		<emi:inputText name="nom" textKey="modul.form.modul" disabled="true"/>
		<emi:inputText name="descripcio" textKey="modul.form.descripcio" disabled="true"/>
		<emi:inputCheckbox name="actiuEntrada" textKey="modul.form.actiu.entrada"/>
		<emi:inputCheckbox name="actiuSortida" textKey="modul.form.actiu.sortida"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/scsp/modul"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
