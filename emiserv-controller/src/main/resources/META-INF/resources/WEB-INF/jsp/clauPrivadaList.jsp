<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="clau.privada.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<table id="clausprivades" data-toggle="datatable" data-url="clauprivada/datatable" data-search-enabled="false" data-botons-template="#botonsTemplate" class="table table-striped table-bordered" style="width:100%">
		<thead>
			<tr>
				<th data-col-name="alies" width="15%"><spring:message code="clau.privada.list.taula.columna.alias" /></th>
				<th data-col-name="nom" width="40%"><spring:message code="clau.privada.list.taula.columna.nom" /></th>
				<th data-col-name="numSerie" width="15%"><spring:message code="clau.privada.list.taula.columna.numeroserie" /></th>
				<th data-col-name="dataAlta" data-converter="date" width="10%"><spring:message code="clau.privada.list.taula.columna.dataalta" /></th>
				<th data-col-name="dataBaixa" data-converter="date" width="10%"><spring:message code="claupublica.list.taula.columna.databaixa" /></th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="1%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="btn-group">
							<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.accions"/>&nbsp;<span class="caret"></span></a>
							<ul class="dropdown-menu">
								<li><a href="clauprivada/{{:id}}" data-toggle="modal" data-reload-on-close="false"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="clauprivada/{{:id}}/delete" data-confirm="<spring:message code="clau.privada.list.confirmacio.esborrar"/>"><i class="fa fa-trash-o"></i>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="botonsTemplate" type="text/x-jsrender">
		<a id="accio-new" class="btn btn-default pull-right" href="clauprivada/new" data-toggle="modal" data-reload-on-close="false" data-datatable-id="clausprivades">
			<span class="fa fa-plus"></span>&nbsp;<spring:message code="clau.privada.list.boto.nou.registre"/>
		</a>
	</script>
</body>
</html>
