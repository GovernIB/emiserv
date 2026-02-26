<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="parametres.list.titol"/></title>
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
                    if ($("#nombre").is(":focus") || $("#descripcion").is(":focus")) {
                        $("#b_filtrar").click();
                    }
                    return false;
                }
            })
        });
    </script>
</head>
<body>
    <form:form action="" method="post" cssClass="well" modelAttribute="scspParametreFiltreDto">
		<div class="row">
			<div class="col-md-4">
				<emi:inputText name="nombre" placeholderKey="parametres.list.taula.columna.nom" inline="true"/>
			</div>
			<div class="col-md-6">
				<emi:inputText name="descripcion" placeholderKey="parametres.list.taula.columna.descripcio" inline="true"/>
			</div>
            <div class="col-md-2">
                <div class="pull-right">
                    <button id="b_netejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
                    <button id="b_filtrar" type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
                </div>
            </div>
        </div>
	</form:form>
	<table id="parametres" data-toggle="datatable" data-url="parametres/datatable" data-search-enabled="false" data-botons-template="#botonsTemplate" class="table table-striped table-bordered" style="width:100%">
		<thead>
			<tr>
				<th data-col-name="nombre" style="width:20%;"><spring:message code="parametres.list.taula.columna.nom"/></th>
				<th data-col-name="descripcion"><spring:message code="parametres.list.taula.columna.descripcio" /></th>
				<th data-col-name="valor" style="width:30%;"><spring:message code="parametres.list.taula.columna.valor" /></th>
				<th data-col-name="nombre" data-orderable="false" data-template="#cellAccionsTemplate" width="1%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="btn-group">
							<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.accions"/>&nbsp;<span class="caret"></span></a>
							<ul class="dropdown-menu">
								<li><a href="parametres/{{:nombre}}" data-toggle="modal" data-refresh-missatges="false" data-reload-on-close="true"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="parametres/{{:nombre}}/delete" data-confirm="<spring:message code="parametres.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="botonsTemplate" type="text/x-jsrender">
		<a id="accio-new" class="btn btn-default pull-right" href="parametres/new" data-toggle="modal" data-refresh-missatges="false" data-reload-on-close="true" data-datatable-id="parametres">
			<span class="fa fa-plus"></span>&nbsp;<spring:message code="parametres.list.boto.nou.registre"/>
		</a>
	</script>
</body>
</html>