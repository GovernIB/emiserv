<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%
pageContext.setAttribute(
		"isRolActualAdministrador",
		es.caib.emiserv.back.helper.RolHelper.isUsuariActualAdministrador(request));
%>
<html>
<head>
	<title><spring:message code="servei.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<table id="serveis" data-toggle="datatable" data-url="servei/datatable" data-search-enabled="false"<c:if test="${isRolActualAdministrador}"> data-botons-template="#botonsTemplate"</c:if> class="table table-striped table-bordered" style="width:100%">
		<thead>
			<tr>
				<th data-col-name="codi" data-template="#cellCodiTemplate">
					<spring:message code="servei.list.columna.codi"/>
					<script id="cellCodiTemplate" type="text/x-jsrender">
						{{:codi}} {{if !configurat}}<span class="fa fa-warning text-danger" title="<spring:message code="servei.list.alerta.configuracio"/>"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="nom"><spring:message code="servei.list.columna.nom"/></th>
				<th data-col-name="tipus"><spring:message code="servei.list.columna.tipus"/></th>
				<th data-col-name="configurat" data-visible="false" ><spring:message code="servei.list.columna.configurat"/></th>
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
					<th data-template="#cellAutoritzacionsTemplate" width="10%">
						<script id="cellAutoritzacionsTemplate" type="text/x-jsrender">
							{{if tipus == 'BACKOFFICE'}}<a href="servei/{{:id}}/autoritzacio" class="btn btn-default"><span class="fa fa-thumbs-o-up"></span>&nbsp;<spring:message code="servei.list.boto.autoritzacions"/></a>{{/if}}
						</script>
					</th>
				</c:if>
				<th data-col-name="xsdGestioActiva" data-visible="false"></th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="servei/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								{{if tipus == 'BACKOFFICE'}}
									<li><a href="servei/{{:id}}/configScsp" data-toggle="modal"><span class="fa fa-wrench"></span>&nbsp;&nbsp;<spring:message code="servei.list.accio.config.scsp"/></a></li>
									{{if xsdGestioActiva}}
										<li><a href="servei/{{:id}}/xsd" data-toggle="modal"><span class="fa fa-file-excel-o"></span>&nbsp;&nbsp;<spring:message code="servei.list.accio.config.xsds"/></a></li>
									{{/if}}
								{{else}}
									<li><a href="servei/{{:id}}/rutes" data-toggle="modal"><span class="fa fa-wrench"></span>&nbsp;&nbsp;<spring:message code="servei.list.accio.config.rutes"/></a></li>
								{{/if}}
								<c:if test="${isRolActualAdministrador}">
									{{if !actiu}}
									<li><a href="servei/{{:id}}/enable"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
									{{else}}
									<li><a href="servei/{{:id}}/disable"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
									{{/if}}
									<li><a href="servei/{{:id}}/delete" data-confirm="<spring:message code="servei.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								</c:if>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<c:if test="${isRolActualAdministrador}">
		<script id="botonsTemplate" type="text/x-jsrender">
			<a id="accio-new" class="btn btn-default pull-right" href="servei/new" data-toggle="modal" data-datatable-id="serveis"><span class="fa fa-plus"></span>&nbsp;<spring:message code="servei.list.boto.nou.servei"/></a>
		</script>
	</c:if>
</body>