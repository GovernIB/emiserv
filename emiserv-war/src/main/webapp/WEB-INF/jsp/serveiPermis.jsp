<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%
pageContext.setAttribute(
		"principalTipus",
		es.caib.emiserv.core.api.dto.PrincipalTipusEnumDto.values());
%>
<html>
<head>
	<title><spring:message code="servei.permis.titol"/></title>
	<meta name="subtitle" content="${servei.nom}"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<script>
var principalTipusEnumText = new Array();
<c:forEach var="principalTipus" items="${principalTipus}">
principalTipusEnumText["${principalTipus}"] = "<spring:message code="principal.tipus.enum.${principalTipus}"/>";
</c:forEach>
</script>
</head>
<body>

	<table id="permisos" data-toggle="datatable" data-url="permis/datatable" data-search-enabled="false" data-botons-template="#botonsTemplate" class="table table-striped table-bordered" style="width:100%">
		<thead>
			<tr>
				<th data-col-name="principalTipus"><spring:message code="servei.permis.columna.tipus"/></th>
				<th data-col-name="principalNom"><spring:message code="servei.permis.columna.principal"/></th>
				<th data-col-name="administration" data-template="#cellAdministrationTemplate">
					<spring:message code="servei.permis.columna.administracio"/>
					<script id="cellAdministrationTemplate" type="text/x-jsrender">
						{{if administration}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<c:if test="${isRolActualAdministrador}">
					<th data-col-name="actiu" data-template="#cellActiuTemplate">
						<spring:message code="servei.list.columna.actiu"/>
						<script id="cellActiuTemplate" type="text/x-jsrender">
						{{if actiu}}<span class="fa fa-check"></span>{{/if}}
					</script>
					</th>
					<th data-col-name="permisosCount" data-orderable="false" data-template="#cellPermisosTemplate" width="10%">
						<script id="cellPermisosTemplate" type="text/x-jsrender">
						<a href="servei/{{:id}}/permis" class="btn btn-default"><span class="fa fa-key"></span>&nbsp;<spring:message code="servei.list.boto.permisos"/>&nbsp;<span class="badge">{{:permisosCount}}</span></a>
					</script>
					</th>
				</c:if>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="../../servei/${servei.id}/permis/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="../../servei/${servei.id}/permis/{{:id}}/delete" data-confirm="<spring:message code="servei.permis.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="botonsTemplate" type="text/x-jsrender">
		<a href="permis/new" data-toggle="modal" class="btn btn-default pull-right"><span class="fa fa-plus"></span>&nbsp;<spring:message code="servei.permis.boto.nou.permis"/></a>
	</script>

	<a href="<c:url value="/servei"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span> <spring:message code="comu.boto.tornar"/></a>

</body>
</html>
