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
pageContext.setAttribute(
		"isRolActualAdministrador",
		es.caib.emiserv.back.helper.RolHelper.isUsuariActualAdministrador(request));
pageContext.setAttribute(
		"auditoriaEstatEnumOptions",
		es.caib.emiserv.back.helper.HtmlSelectOptionHelper.getOptionsForEnum(
				es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto.class,
				"peticio.estat.enum."));
%>
<html>
<head>
	<title><spring:message code="auditoria.list.titol.enrutador"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.${idioma}.js"/>"></script>
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${idioma}.js"/>"></script>
<script>
$(document).ready(function() {
	$('#peticions').on('rowinfo.dataTable', function(e, td, rowData) {
		$.get("auditoriaEnrutador/solicituds/" + rowData.id, function(data) {
			if (data && data.length > 0) {
				$(td).append(
						'<table class="table teble-striped table-bordered"><thead><tr>' +
						'<th><spring:message code="auditoria.list.columna.num.solicitud"/></th>' +
						'<th><spring:message code="auditoria.list.columna.remitent"/></th>' +
						'<th><spring:message code="auditoria.list.columna.procediment"/></th>' +
						'<th><spring:message code="auditoria.list.columna.funcionari"/></th>' +
						'</tr></thead><tbody></tbody></table>');
				for (i = 0; i < data.length; i++) {
					$('table tbody', td).append(
							'<tr>' +
							'<td>' + data[i].solicitudId + '</td>' +
							'<td>' + data[i].solicitantNom + '</td>' +
							'<td>' + data[i].procedimentNom + '</td>' +
							'<td>' + data[i].funcionariNom + ' (NIF: ' + data[i].funcionariDocument + ')</td>' +
							'</tr>');
				}
			} else {
				$(td).append('<div class="alert well-sm alert-warning"><span class="fa fa-warning"></span> <spring:message code="auditoria.list.no.solicitud"/></div>');
			}
		});
	});
});
</script>
</head>
<body>
	<form:form action="" method="post" cssClass="well" modelAttribute="auditoriaFiltreCommand">
		<div class="row">
			<div class="col-md-4">
				<emi:inputSelect name="procediment" optionItems="${procediments}" optionValueAttribute="codi" optionTextAttribute="nom" emptyOption="true" placeholderKey="auditoria.list.filtre.procediment" inline="true"/>
			</div>
			<div class="col-md-4">
				<emi:inputSelect name="serveiCodi" optionItems="${serveis}" optionValueAttribute="codi" optionTextAttribute="nom" emptyOption="true" placeholderKey="auditoria.list.filtre.servei" inline="true"/>
			</div>
			<div class="col-md-4">
				<emi:inputSelect name="estat" optionItems="${auditoriaEstatEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" emptyOption="true" placeholderKey="auditoria.list.filtre.estat" inline="true"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-2">
				<emi:inputDate name="dataInici" inline="true" placeholderKey="auditoria.list.filtre.data.inici"/>
			</div>
			<div class="col-md-2">
				<emi:inputDate name="dataFi" inline="true" placeholderKey="auditoria.list.filtre.data.fi"/>
			</div>
<%--			<div class="col-md-2">--%>
<%--				<emi:inputText name="funcionariNom" placeholderKey="auditoria.list.filtre.funcionari.nom" inline="true"/>--%>
<%--			</div>--%>
<%--			<div class="col-md-2">--%>
<%--				<emi:inputText name="funcionariDocument" placeholderKey="auditoria.list.filtre.funcionari.document" inline="true"/>--%>
<%--			</div>--%>
			<div class="col-md-4">
				<emi:inputText name="numeroPeticio" placeholderKey="auditoria.list.filtre.numero.peticio" inline="true"/>
			</div>
			<div class="col-md-4 pull-right">
				<div class="pull-right">
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	<table id="peticions" data-toggle="datatable" data-url="auditoriaEnrutador/datatable" data-search-enabled="false" data-default-order="4" data-default-dir="desc" data-row-info="true" class="table table-striped table-bordered" style="width:100%">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false" data-orderable="false">#</th>
				<th data-col-name="estat" data-visible="false" data-orderable="false">#</th>
				<th data-col-name="error" data-visible="false" data-orderable="false">#</th>
				<th data-col-name="serveiDescripcio" data-visible="false" data-orderable="false">#</th>
				<th data-col-name="dataPeticio" data-converter="datetime" width="15%"><spring:message code="auditoria.list.columna.data"/></th>
				<th data-col-name="peticioId" width="25%"><spring:message code="auditoria.list.columna.num.peticio"/></th>
				<th data-col-name="serveiCodi" data-template="#cellCertificadoTemplate">
					<spring:message code="auditoria.list.columna.servei"/>
					<script id="cellCertificadoTemplate" type="text/x-jsrender">{{:serveiDescripcio}}</script>
				</th>
				<th data-col-name="estat" data-template="#cellEstadoTemplate">
					<spring:message code="auditoria.list.columna.estat"/>
					<script id="cellEstadoTemplate" type="text/x-jsrender">
						{{if estat == 'PENDENT'}}
							<span class="fa fa-clock-o"></span>&nbsp;<spring:message code="peticio.estat.enum.PENDENT"/>
						{{else estat == 'EN_PROCES'}}
							<span class="fa fa-cogs"></span>&nbsp;<spring:message code="peticio.estat.enum.EN_PROCES"/>
						{{else estat == 'TRAMITADA'}}
							<span class="fa fa-check"></span>&nbsp;<spring:message code="peticio.estat.enum.TRAMITADA"/>
						{{else estat == 'ERROR'}}
							<span title="{{:error}}"><span class="fa fa-warning"></span>&nbsp;<spring:message code="peticio.estat.enum.ERROR"/></span>
						{{else}}
							<span class="fa fa-question"></span>&nbsp;<spring:message code="peticio.estat.enum.DESCONEGUT"/>
						{{/if}}
					</script>
				</th>
				<th data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="auditoriaEnrutador/xmlPeticio/{{:id}}" data-toggle="modal"><span class="fa fa-arrow-circle-o-down"></span>&nbsp;&nbsp;<spring:message code="auditoria.list.accio.xmlpeticio"/></a></li>
								{{if estat == 'TRAMITADA' || estat == 'ERROR_EMISOR'}}
									<li><a href="auditoriaEnrutador/xmlResposta/{{:id}}" data-toggle="modal"><span class="fa fa-arrow-circle-o-up"></span>&nbsp;&nbsp;<spring:message code="auditoria.list.accio.xmlresposta"/></a></li>
								{{/if}}
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>