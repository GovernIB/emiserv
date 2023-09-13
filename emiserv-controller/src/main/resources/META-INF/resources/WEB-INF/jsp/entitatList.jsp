<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%
	pageContext.setAttribute("isRolActualAdministrador", es.caib.emiserv.back.helper.RolHelper.isUsuariActualAdministrador(request));
	pageContext.setAttribute("idioma", org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage());
%>
<html>
<head>
	<title><spring:message code="entitat.list.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${idioma}.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<form:form action="" method="post" cssClass="well" modelAttribute="entitatFiltreDto">
		<div class="row">
			<div class="col-md-4">
				<emi:inputText name="codi" placeholderKey="entitat.list.filtre.codi" inline="true"/>
			</div>
			<div class="col-md-4">
				<emi:inputText name="nom" placeholderKey="entitat.list.filtre.nom" inline="true"/>
			</div>
			<div class="col-md-4">
				<emi:inputText name="cif" placeholderKey="entitat.list.filtre.cif" inline="true"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-2 col-md-offset-10">
				<div class="pull-right">
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	<table id="entitats" data-toggle="datatable" data-url="entitat/datatable" data-search-enabled="false"<c:if test="${isRolActualAdministrador}"> data-botons-template="#botonsTemplate"</c:if> class="table table-striped table-bordered" style="width:100%">
		<thead>
			<tr>
				<th data-col-name="codi"><spring:message code="entitat.list.columna.codi"/></th>
				<th data-col-name="nom"><spring:message code="entitat.list.columna.nom"/></th>
				<th data-col-name="cif"><spring:message code="entitat.list.columna.cif"/></th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="1%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<c:if test="${isRolActualAdministrador}">
								<li><a href="entitat/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="entitat/{{:id}}/delete" data-confirm="<spring:message code="entitat.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
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
			<a id="accio-new" class="btn btn-default pull-right" href="entitat/new" data-toggle="modal" data-datatable-id="entitats"><span class="fa fa-plus"></span>&nbsp;<spring:message code="entitat.list.boto.nou"/></a>
			<a id="accio-new" class="btn btn-default pull-right" href="entitat/sincronitzar"><span class="fa fa-sync"></span>&nbsp;<spring:message code="entitat.list.boto.sincronitzar"/></a>
		</script>
	</c:if>
</body>