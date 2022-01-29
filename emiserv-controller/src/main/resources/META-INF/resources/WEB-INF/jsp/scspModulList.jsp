<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="modul.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<table id="moduls" data-toggle="datatable" data-url="modul/datatable" data-search-enabled="false" class="table table-striped table-bordered" style="width:100%">
		<thead>
			<tr>
				<th data-col-name="nom" width="20%"><spring:message code="modul.list.modul"/></th>
				<th data-col-name="descripcio"><spring:message code="modul.list.descripcio"/></th>
				<th data-col-name="actiuEntrada" width="10%" data-template="#cellActiuEntradaTemplate">
					<spring:message code="modul.list.actiu.entrada"/>
					<script id="cellActiuEntradaTemplate" type="text/x-jsrender">
						{{if actiuEntrada}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="actiuSortida" width="10%" data-template="#cellActiuSortidaTemplate">
					<spring:message code="modul.list.actiu.sortida"/>
					<script id="cellActiuSortidaTemplate" type="text/x-jsrender">
						{{if actiuSortida}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="nom" data-orderable="false" data-template="#cellAccionsTemplate" width="1%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<button href="modul/{{:nom}}" class="btn btn-primary" data-toggle="modal" data-reload-on-close="false"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/></button>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>