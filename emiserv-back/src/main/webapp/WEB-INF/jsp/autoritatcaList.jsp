<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="autoritatca.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script type="application/javascript">
		$(document).ready(function () {
			$("form").on('keydown', function (e) {
				debugger
				if (e.key === 'Enter' || e.keyCode === 13) {
					e.preventDefault();
					if ($("#nom").is(":focus") || $("#codi").is(":focus")) {
						$("#b_filtrar").click();
					}
					return false;
				}
			})
		});
	</script>
	<style type="text/css">
		@media (min-width: 1400px) {
			.modal-lg {
				width: 1200px !important;
			}
		}
	</style>
</head>
<body>
	<form:form action="" method="post" cssClass="well" modelAttribute="autoritatCertificacioFiltreDto">
		<div class="row">
			<div class="col-md-4">
				<emi:inputText name="nom" placeholderKey="autoritatca.list.taula.columna.nom" inline="true"/>
			</div>
			<div class="col-md-8">
				<emi:inputText name="codi" placeholderKey="autoritatca.list.taula.columna.codi" inline="true"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-2 col-md-offset-10">
				<div class="pull-right">
					<button id="b_netejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button id="b_filtrar" type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	<table id="autoritatsca" data-toggle="datatable" data-url="autoritatca/datatable" data-search-enabled="false" data-botons-template="#botonsTemplate" class="table table-striped table-bordered" style="width:100%">
		<thead>
			<tr>
				<th data-col-name="nombre"><spring:message code="autoritatca.list.taula.columna.nom" /></th>
				<th data-col-name="codca" data-orderable="false"><spring:message code="autoritatca.list.taula.columna.codi" /></th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="1%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="btn-group">
							<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.accions"/>&nbsp;<span class="caret"></span></a>
							<ul class="dropdown-menu">
								<li><a href="autoritatca/{{:id}}" data-toggle="modal" data-reload-on-close="false"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="autoritatca/{{:id}}/delete" data-confirm="<spring:message code="autoritatca.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="botonsTemplate" type="text/x-jsrender">
		<a id="accio-new" class="btn btn-default pull-right" href="autoritatca/new" data-toggle="modal" data-reload-on-close="false" data-datatable-id="autoritatsca">
			<span class="fa fa-plus"></span>&nbsp;<spring:message code="autoritatca.list.boto.nou.registre"/>
		</a>
	</script>
</body>
</html>