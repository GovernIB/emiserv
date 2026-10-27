<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%
pageContext.setAttribute(
		"idioma",
		org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage());
pageContext.setAttribute(
		"estatEnumOptions",
		es.caib.emiserv.back.helper.HtmlSelectOptionHelper.getOptionsForEnum(
				es.caib.emiserv.logic.intf.dto.ConsultaAsincronaFiltreEstatEnumDto.class,
				"consulta.asincrona.filtre.estat.enum."));
%>
<html>
<head>
	<title><spring:message code="consulta.asincrona.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
<style>
	.progress { margin-bottom: 2px; min-width: 120px; }
	.progres-text { font-size: 85%; color: #666; }
	#cedents td, #cedents th { vertical-align: middle; }
	.solicituds td { font-size: 90%; }
</style>
<script>
var estatsSolicitud = {
	PENDENT: '<spring:message code="consulta.asincrona.solicitud.estat.PENDENT"/>',
	ENVIANT: '<spring:message code="consulta.asincrona.solicitud.estat.ENVIANT"/>',
	TRAMITADA: '<spring:message code="consulta.asincrona.solicitud.estat.TRAMITADA"/>',
	ERROR: '<spring:message code="consulta.asincrona.solicitud.estat.ERROR"/>'
};
function escapar(text) {
	return (text == null) ? '' : $('<div/>').text(text).html();
}
function formatData(valor) {
	if (!valor) return '';
	var d = new Date(valor);
	function dd(n) { return (n < 10 ? '0' : '') + n; }
	return dd(d.getDate()) + '/' + dd(d.getMonth() + 1) + '/' + d.getFullYear() + ' ' + dd(d.getHours()) + ':' + dd(d.getMinutes()) + ':' + dd(d.getSeconds());
}
$(document).ready(function() {
	$('#consultes').on('rowinfo.dataTable', function(e, td, rowData) {
		$.get('consultaAsincrona/' + rowData.id + '/solicituds', function(data) {
			var $taula = $('<table class="table table-condensed table-bordered solicituds"><thead><tr>' +
					'<th><spring:message code="consulta.asincrona.solicitud.columna.id"/></th>' +
					'<th><spring:message code="consulta.asincrona.solicitud.columna.entitat"/></th>' +
					'<th><spring:message code="consulta.asincrona.solicitud.columna.estat"/></th>' +
					'<th><spring:message code="consulta.asincrona.solicitud.columna.intents"/></th>' +
					'<th><spring:message code="consulta.asincrona.solicitud.columna.enviament"/></th>' +
					'<th><spring:message code="consulta.asincrona.solicitud.columna.proper.intent"/></th>' +
					'<th><spring:message code="consulta.asincrona.solicitud.columna.peticio.desti"/></th>' +
					'<th><spring:message code="consulta.asincrona.solicitud.columna.error"/></th>' +
					'<th width="1%"></th>' +
					'</tr></thead><tbody></tbody></table>');
			for (var i = 0; i < data.length; i++) {
				var s = data[i];
				var reintentable = !rowData.finalitzada && (s.estat == 'ERROR' || (s.estat == 'PENDENT' && s.dataProperIntent));
				var classe = (s.estat == 'ERROR') ? 'danger' : (s.estat == 'TRAMITADA') ? 'success' : (s.estat == 'ENVIANT') ? 'info' : '';
				$('tbody', $taula).append(
						'<tr class="' + classe + '">' +
						'<td>' + escapar(s.solicitudId) + '</td>' +
						'<td>' + escapar(s.entitatCodi) + '</td>' +
						'<td>' + estatsSolicitud[s.estat] + '</td>' +
						'<td>' + s.intents + '</td>' +
						'<td>' + formatData(s.dataEnviament) + '</td>' +
						'<td>' + formatData(s.dataProperIntent) + '</td>' +
						'<td>' + escapar(s.peticioDestiId) + '</td>' +
						'<td title="' + escapar(s.darrerError) + '">' + (s.errorCodi ? '<span class="label label-danger">' + escapar(s.errorCodi) + '</span> ' : '') +
							escapar(s.darrerError && s.darrerError.length > 80 ? s.darrerError.substring(0, 80) + '…' : s.darrerError) + '</td>' +
						'<td>' + (reintentable ? '<a href="consultaAsincrona/' + rowData.id + '/solicitud/' + s.id + '/reintentar" class="btn btn-default btn-xs" title="<spring:message code="consulta.asincrona.accio.reintentar"/>"><span class="fa fa-redo"></span></a>' : '') + '</td>' +
						'</tr>');
			}
			$(td).append($taula);
		});
	});
	// Per defecte només es mostren els destins amb sol·licituds per enviar o pausats
	$('#mostrar-cedents').click(function() {
		$('tr.cedent-inactiu').toggle();
		return false;
	});
	// Actualització periòdica del progrés
	setInterval(function() {
		if ($('#actualitzar').is(':checked') && $('tr[data-row-info]', '#consultes').length == 0) {
			$('#consultes').DataTable().ajax.reload(null, false);
		}
	}, 15000);
});
</script>
</head>
<body>
	<form:form action="" method="post" cssClass="well" modelAttribute="consultaAsincronaFiltreCommand">
		<div class="row">
			<div class="col-md-3">
				<emi:inputSelect name="estat" optionItems="${estatEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" emptyOption="true" emptyOptionTextKey="consulta.asincrona.filtre.estat.totes" inline="true"/>
			</div>
			<div class="col-md-5">
				<emi:inputSelect name="servei" optionMinimumResultsForSearch="0" optionItems="${serveis}" optionValueAttribute="id" optionTextAttribute="codiNom" emptyOption="true" placeholderKey="consulta.asincrona.filtre.servei" inline="true"/>
			</div>
			<div class="col-md-4">
				<emi:inputText name="peticioId" placeholderKey="consulta.asincrona.filtre.peticio" inline="true"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6">
				<label class="checkbox-inline"><input type="checkbox" id="actualitzar" checked="checked"/> <spring:message code="consulta.asincrona.list.actualitzar"/></label>
			</div>
			<div class="col-md-6">
				<div class="pull-right">
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>

	<div class="panel panel-default">
		<div class="panel-heading">
			<a data-toggle="collapse" href="#panel-cedents"><span class="fa fa-server"></span> <spring:message code="consulta.asincrona.cedents.titol"/></a>
			<span class="badge">${fn:length(cedents)}</span>
			<a href="#" id="mostrar-cedents" class="pull-right"><spring:message code="consulta.asincrona.cedents.mostrar.tots"/></a>
		</div>
		<div id="panel-cedents" class="panel-collapse collapse in">
			<c:choose>
				<c:when test="${empty cedents}">
					<div class="panel-body"><spring:message code="consulta.asincrona.cedents.buit"/></div>
				</c:when>
				<c:otherwise>
					<table id="cedents" class="table table-condensed table-striped" style="margin-bottom: 0">
						<thead>
							<tr>
								<th><spring:message code="consulta.asincrona.cedents.columna.servei"/></th>
								<th><spring:message code="consulta.asincrona.cedents.columna.entitat"/></th>
								<th><spring:message code="consulta.asincrona.cedents.columna.desti"/></th>
								<th><spring:message code="consulta.asincrona.cedents.columna.ritme"/></th>
								<th><spring:message code="consulta.asincrona.cedents.columna.pendents"/></th>
								<th><spring:message code="consulta.asincrona.cedents.columna.enviant"/></th>
								<th><spring:message code="consulta.asincrona.cedents.columna.fi.prevista"/></th>
								<th width="1%"></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="cedent" items="${cedents}">
								<c:set var="cedentInactiu" value="${cedent.pendents + cedent.enviant == 0 and not cedent.pausada}"/>
								<tr class="${cedent.pausada ? 'warning' : ''}${cedentInactiu ? ' cedent-inactiu' : ''}"<c:if test="${cedentInactiu}"> style="display: none"</c:if>>
									<td title="${cedent.serveiNom}">${cedent.serveiCodi}</td>
									<td>${cedent.entitatCodi}<c:if test="${cedent.pausada}"> <span class="label label-warning"><spring:message code="consulta.asincrona.pausat"/></span></c:if></td>
									<td><c:if test="${not empty cedent.destiTipus}"><spring:message code="servei.ruta.desti.tipus.enum.${cedent.destiTipus}"/></c:if></td>
									<td><spring:message code="consulta.asincrona.cedents.ritme" arguments="${cedent.intervalMs},${cedent.concurrencia}"/></td>
									<td>${cedent.pendents}</td>
									<td>${cedent.enviant}</td>
									<td><fmt:formatDate value="${cedent.dataFiPrevista}" pattern="dd/MM/yyyy HH:mm"/></td>
									<td>
										<c:if test="${not empty cedent.rutaId}">
											<c:choose>
												<c:when test="${cedent.pausada}">
													<a href="<c:url value="/consultaAsincrona/cedent/${cedent.rutaId}/reprendre"/>" class="btn btn-success btn-xs"><span class="fa fa-play"></span> <spring:message code="consulta.asincrona.accio.reprendre"/></a>
												</c:when>
												<c:otherwise>
													<a href="<c:url value="/consultaAsincrona/cedent/${cedent.rutaId}/pausar"/>" class="btn btn-warning btn-xs" onclick="return confirm('<spring:message code="consulta.asincrona.cedents.confirmacio.pausar" arguments="${cedent.entitatCodi}" javaScriptEscape="true"/>')"><span class="fa fa-pause"></span> <spring:message code="consulta.asincrona.accio.pausar"/></a>
												</c:otherwise>
											</c:choose>
										</c:if>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

	<table id="consultes" data-toggle="datatable" data-url="consultaAsincrona/datatable" data-search-enabled="false" data-default-order="12" data-default-dir="desc" data-row-info="true" class="table table-striped table-bordered" style="width:100%">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false">#</th>
				<th data-col-name="finalitzada" data-visible="false" data-orderable="false">#</th>
				<th data-col-name="serveiNom" data-visible="false" data-orderable="false">#</th>
				<th data-col-name="pausada" data-visible="false" data-orderable="false">#</th>
				<th data-col-name="destinsPausats" data-visible="false" data-orderable="false">#</th>
				<th data-col-name="retardada" data-visible="false" data-orderable="false">#</th>
				<th data-col-name="total" data-visible="false" data-orderable="false">#</th>
				<th data-col-name="tramitades" data-visible="false" data-orderable="false">#</th>
				<th data-col-name="errors" data-visible="false" data-orderable="false">#</th>
				<th data-col-name="enviant" data-visible="false" data-orderable="false">#</th>
				<th data-col-name="processades" data-visible="false" data-orderable="false">#</th>
				<th data-col-name="reintents" data-visible="false" data-orderable="false">#</th>
				<th data-col-name="dataPeticio" data-converter="datetime" width="12%"><spring:message code="consulta.asincrona.list.columna.data"/></th>
				<th data-col-name="peticioId" data-template="#cellPeticioTemplate" width="16%">
					<spring:message code="consulta.asincrona.list.columna.peticio"/>
					<script id="cellPeticioTemplate" type="text/x-jsrender">
						{{:peticioId}}
						{{if pausada}}<span class="label label-warning"><spring:message code="consulta.asincrona.pausada"/></span>{{/if}}
						{{if destinsPausats}}<span class="label label-warning" title="<spring:message code="consulta.asincrona.destins.pausats.info"/>"><spring:message code="consulta.asincrona.destins.pausats"/></span>{{/if}}
					</script>
				</th>
				<th data-col-name="serveiCodi" data-orderable="false" data-template="#cellServeiTemplate">
					<spring:message code="consulta.asincrona.list.columna.servei"/>
					<script id="cellServeiTemplate" type="text/x-jsrender"><span title="{{>serveiNom}}">{{:serveiCodi}}</span></script>
				</th>
				<th data-col-name="progres" data-orderable="false" data-template="#cellProgresTemplate" width="20%">
					<spring:message code="consulta.asincrona.list.columna.progres"/>
					<script id="cellProgresTemplate" type="text/x-jsrender">
						<div class="progress">
							<div class="progress-bar progress-bar-success" style="width: {{:total > 0 ? 100 * tramitades / total : 0}}%"></div>
							<div class="progress-bar progress-bar-danger" style="width: {{:total > 0 ? 100 * errors / total : 0}}%"></div>
							<div class="progress-bar progress-bar-info progress-bar-striped active" style="width: {{:total > 0 ? 100 * enviant / total : 0}}%"></div>
						</div>
						<span class="progres-text">
							{{:processades}}/{{:total}} ({{:progres}}%)
							{{if errors > 0}} · <span class="text-danger">{{:errors}} <spring:message code="consulta.asincrona.list.errors"/></span>{{/if}}
							{{if reintents > 0}} · <span class="text-warning">{{:reintents}} <spring:message code="consulta.asincrona.list.reintents"/></span>{{/if}}
						</span>
					</script>
				</th>
				<th data-col-name="terData" data-converter="datetime" width="11%"><spring:message code="consulta.asincrona.list.columna.ter"/></th>
				<th data-col-name="dataFiPrevista" data-orderable="false" data-template="#cellFiTemplate" width="11%">
					<spring:message code="consulta.asincrona.list.columna.fi.prevista"/>
					<script id="cellFiTemplate" type="text/x-jsrender">
						{{if dataFiPrevista}}
							<span class="{{if retardada}}text-danger{{/if}}" title="{{if retardada}}<spring:message code="consulta.asincrona.list.retardada"/>{{/if}}">
								{{:~formatData(dataFiPrevista)}}{{if retardada}} <span class="fa fa-exclamation-triangle"></span>{{/if}}
							</span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="estat" data-orderable="false" data-template="#cellEstatTemplate" width="9%">
					<spring:message code="consulta.asincrona.list.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if estat == 'PENDENT'}}
							{{if pausada}}<span class="fa fa-pause"></span>&nbsp;<spring:message code="consulta.asincrona.pausada"/>
							{{else}}<span class="fa fa-cogs"></span>&nbsp;<spring:message code="consulta.asincrona.estat.en.curs"/>{{/if}}
						{{else estat == 'TRAMITADA'}}
							<span class="fa fa-check"></span>&nbsp;<spring:message code="consulta.asincrona.estat.finalitzada"/>
						{{else}}
							<span class="fa fa-warning"></span>&nbsp;<spring:message code="consulta.asincrona.estat.finalitzada.errors"/>
						{{/if}}
					</script>
				</th>
				<th data-template="#cellAccionsTemplate" data-orderable="false" width="1%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						{{if !finalitzada}}
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu dropdown-menu-right">
								{{if pausada}}
									<li><a href="consultaAsincrona/{{:id}}/reprendre"><span class="fa fa-play"></span>&nbsp;&nbsp;<spring:message code="consulta.asincrona.accio.reprendre"/></a></li>
								{{else}}
									<li><a href="consultaAsincrona/{{:id}}/pausar"><span class="fa fa-pause"></span>&nbsp;&nbsp;<spring:message code="consulta.asincrona.accio.pausar"/></a></li>
								{{/if}}
								{{if errors > 0 || reintents > 0}}
									<li><a href="consultaAsincrona/{{:id}}/reintentar"><span class="fa fa-redo"></span>&nbsp;&nbsp;<spring:message code="consulta.asincrona.accio.reintentar"/></a></li>
								{{/if}}
								<li><a href="consultaAsincrona/{{:id}}/cancelar" data-confirm="<spring:message code="consulta.asincrona.confirmacio.cancelar"/>"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="consulta.asincrona.accio.cancelar"/></a></li>
							</ul>
						</div>
						{{/if}}
					</script>
				</th>
			</tr>
		</thead>
	</table>
<script>
$.views.helpers({formatData: formatData});
</script>
</body>
</html>
