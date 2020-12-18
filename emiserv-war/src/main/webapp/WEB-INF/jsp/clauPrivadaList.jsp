<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi" %>

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
	
	<script>
	$(document).ready(function() {
		$('.confirm-esborrar').click(function() {
			  return confirm("<spring:message code="clau.privada.list.confirmacio.esborrar"/>");
		});
	});
	</script>

</head>
<body>
	
	<table id="table-claus" data-toggle="datatable" data-url="clauprivada/datatable" data-search-enabled="false" data-botons-template="#botonsTemplate" class="table table-striped table-bordered" style="width:100%">
		<thead>
			<tr>
				<th data-col-name="alies"><spring:message code="clau.privada.list.taula.columna.alias" /></th>
				<th data-col-name="nom"><spring:message code="clau.privada.list.taula.columna.nom" /></th>
				<th data-col-name="numSerie"><spring:message code="clau.privada.list.taula.columna.numeroserie" /></th> 
				<th data-col-name="dataAlta"><spring:message code="clau.privada.list.taula.columna.dataalta" /></th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="1%"> 
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="btn-group">
							<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><i class="fas fa-cog"></i>&nbsp;<spring:message code="comu.accions"/>&nbsp;<span class="caret"></span></a>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/scsp/clauprivada/{{:id}}"/>" ><i class="fas fa-pen"></i></i>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="<c:url value="/scsp/clauprivada/{{:id}}/delete"/>" class="confirm-esborrar"><i class="fas fa-trash-alt"></i>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>

	<script id="botonsTemplate" type="text/x-jsrender">
		<a class="btn btn-primary pull-right" href="<c:url value="/scsp/clauprivada/new"/>"><i class="fa fa-plus"></i>&nbsp;<spring:message code="clau.privada.list.boto.nou.registre"/></a>
	</script>

</body>
</html>
