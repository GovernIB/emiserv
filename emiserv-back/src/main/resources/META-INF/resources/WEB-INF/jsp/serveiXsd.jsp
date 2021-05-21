<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%
pageContext.setAttribute(
		"idioma",
		org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage());
%>
<c:set var="titol"><spring:message code="servei.xsd.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/datatables.net-scroller/1.4.0/js/dataTables.scroller.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.0/js/dataTables.select.min.js"/>"></script>
	<script src="<c:url value="/js/datatables/keytable-2.1.0/js/dataTables.keyTable.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.0/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${idioma}.js"/>"></script>
	<link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<emi:modalHead/>
<script>
$(document).ready(function() {
	$('#xsds').on('draw.dt', function() {
		webutilModalAdjustHeight();
	});
});
</script>
<style>
tfoot th {
	font-weight: normal;
}
</style>
</head>
<body>
	<form:form method="post" modelAttribute="serveiXsdCommand" role="form" enctype="multipart/form-data">
		<table
			id="xsds"
			data-toggle="datatable"
			data-url="<c:url value="/servei/${servei.id}/xsd/datatable"/>"
			data-editable="true"
			data-updatable="false"
			data-editable-sample-row="#xsd-nou"
			data-paging-enabled="false"
			data-ordering="false"
			class="table table-striped table-bordered"
			style="width:100%">
			<thead>
				<tr>
					<th data-col-name="tipus" width="30%"><spring:message code="servei.xsd.camp.tipus"/></th>
					<th data-col-name="arxiuNom" data-template="#cellArxiuTemplate" width="70%">
						<spring:message code="servei.xsd.camp.arxiu"/>
						<script id="cellArxiuTemplate" type="text/x-jsrender">
							{{:arxiuNom}}
							<a href="xsd/{{:tipus}}/download" class="btn btn-default btn-sm pull-right"><span class="fa fa-download"></span></a>
						</script>
					</th>
				</tr>
			</thead>
			<tfoot>
				<tr id="xsd-nou">
					<th>
						<emi:inputSelect name="tipus" textKey="servei.xsd.camp.tipus" inline="true" required="true" optionItems="${xsdTipusEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
					</th>
					<th>
						<emi:inputFile name="contingut" textKey="servei.xsd.camp.arxiu" inline="true" required="true"/>
					</th>
				</tr>
			</tfoot>
		</table>
		<div id="modal-botons" class="well">
			<a href="<c:url value="/servei"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
		</div>
	</form:form>
</body>
</html>
