<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%
pageContext.setAttribute(
		"isRolActualAdministrador",
		es.caib.emiserv.war.helper.RolHelper.isUsuariActualAdministrador(request));
%>
<html>
<head>
	<title><spring:message code="informe.list.titol"/></title>
		<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.min.js"/>"></script>	

</head>
<body>
	<c:if test="${isRolActualAdministrador}">
		<div class="well well-sm">
			<h4>
				<spring:message code="informe.list.informe.generalEstat"/>
				<a class="btn btn-default pull-right" href="<c:url value="informe/generalEstat"/>" data-toggle="modal"><span class="fa fa-download"></span>&nbsp;<spring:message code="informe.list.generar"/></a>
			</h4>
		</div>
	</c:if>
</body>