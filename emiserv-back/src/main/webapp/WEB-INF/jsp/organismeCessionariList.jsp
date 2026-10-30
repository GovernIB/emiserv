<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="organisme.cessionari.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<form:form action="" method="post" cssClass="well" modelAttribute="organismeFiltreDto">
		<div class="row">
			<div class="col-md-4">
				<emi:inputText name="cif" placeholderKey="organisme.list.filtre.cif" inline="true"/>
			</div>
			<div class="col-md-6">
				<emi:inputText name="nom" placeholderKey="organisme.list.filtre.nom" inline="true"/>
			</div>
			<div class="col-md-2">
				<div class="pull-right">
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	<table id="organismes" data-toggle="datatable" data-url="organismeCessionari/datatable" data-search-enabled="false" data-botons-template="#botonsTemplate" class="table table-striped table-bordered" style="width:100%">
		<thead>
			<tr>
				<th data-col-name="cif" width="10%"><spring:message code="organisme.list.columna.cif"/></th>
				<th data-col-name="nom" width="40%"><spring:message code="organisme.list.columna.nom"/></th>
				<th data-col-name="dataAlta" data-converter="date" width="15%"><spring:message code="organisme.list.columna.dataalta" /></th>
				<th data-col-name="dataBaixa" data-converter="date" width="15%"><spring:message code="organisme.list.columna.databaixa" /></th>
				<th data-col-name="codiUnitatTramitadora" width="10%"><spring:message code="organisme.list.columna.unitat.adm" /></th>
				<th data-col-name="bloquejat" width="10%" data-template="#cellBloquejatTemplate">
					<spring:message code="organisme.list.columna.bloquejat"/>
						<script id="cellBloquejatTemplate" type="text/x-jsrender">
							{{if bloquejat}}<span class="fa fa-check"></span>{{/if}}
						</script>
				</th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="1%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="organismeCessionari/{{:id}}" data-toggle="modal" data-reload-on-close="false"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="organismeCessionari/{{:id}}/delete" data-confirm="<spring:message code="organisme.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="botonsTemplate" type="text/x-jsrender">
		<a id="accio-new" class="btn btn-default pull-right" href="organismeCessionari/new" data-toggle="modal" data-reload-on-close="false" data-datatable-id="organismes"><span class="fa fa-plus"></span>&nbsp;<spring:message code="organisme.list.boto.nou.organisme"/></a>
	</script>
</body>