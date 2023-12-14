<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="auditoria.backoffice.peticio.detall.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${idioma}.js"/>"></script>
	<script src="<c:url value="/js/vkbeautify.js"/>"></script>
	<emi:modalHead/>
	<script>

	<c:if test="${peticio.teRespostes}">
		var missatges = new Map();
		var entitatSeleccionada = null;
		var msg;
		<c:forEach items="${missatgesXml}" var="missatge">
		missatges.set('${missatge.entitat}', `${missatge.xmlResposta}`);
		if (${missatge.respostaEscollida}) {
			entitatSeleccionada = '${missatge.entitat}';
		}
		</c:forEach>
		var primeraEntitat = "${missatgesXml[0].entitat}";
	</c:if>

		$(document).ready(function () {
			$('#peticioError').on('show.bs.collapse', function () {
				$('#peticioErrorBtn span').attr('class', 'fa fa-chevron-up');
			});
			$('#peticioError').on('hide.bs.collapse', function () {
				$('#peticioErrorBtn span').attr('class', 'fa fa-chevron-down');
			});
			$('#xmlPeticioScsp').on('show.bs.collapse', function () {
				$('#xmlPeticioScspBtn span').attr('class', 'fa fa-chevron-up');
				var loaded = $('#xmlPeticioScsp').data('loaded');
				if (!loaded) {
					$.get("xmlPeticioRest", function (data) {
						$('#xmlPeticioScsp textarea').val(data);
						$('#xmlPeticioScsp textarea').val(vkbeautify.xml($('#xmlPeticioScsp textarea').val()));
						$('#xmlPeticioScsp').data('loaded', 'true');
					});
				}
			});
			$('#xmlPeticioScsp').on('hide.bs.collapse', function () {
				$('#xmlPeticioScspBtn span').attr('class', 'fa fa-chevron-down');
			});
			$('#xmlRespostaScsp').on('show.bs.collapse', function () {
				$('#xmlRespostaScspBtn span').attr('class', 'fa fa-chevron-up');
				var loaded = $('#xmlRespostaScsp').data('loaded');
				if (!loaded) {
					$.get("xmlRespostaRest", function (data) {
						$('#xmlRespostaScsp textarea').val(data);
						$('#xmlRespostaScsp textarea').val(vkbeautify.xml($('#xmlRespostaScsp textarea').val()));
						$('#xmlRespostaScsp').data('loaded', 'true');
					});
				}
			});
			$('#xmlRespostaScsp').on('hide.bs.collapse', function () {
				$('#xmlRespostaScspBtn span').attr('class', 'fa fa-chevron-down');
			});

		<c:if test="${peticio.teRespostes}">
			$("#entitat").select2({
				theme: "bootstrap",
				allowClear: false,
				minimumResultsForSearch: 10
			});
			$("#entitat").on('change', function() {
				$('#missatgeXml').val(vkbeautify.xml(missatges.get($(this).val())));
				$("#msgSeleccionat").css("display", ($(this).val() == entitatSeleccionada ? "block" : "none"));
			});
			if (entitatSeleccionada != null) {
				$("#entitat").val(entitatSeleccionada);
				$("#entitat").trigger("change");
			} else {
				$("#entitat").val(primeraEntitat);
				$("#entitat").trigger("change");
				$("#msgSeleccionat").css("display", "none");
			}
		</c:if>
		});

		function escape(htmlStr) {
			return htmlStr.replace(/&/g, "&amp;")
					.replace(/"/g, "&quot;")
					.replace(/'/g, "&#39;");
		}
	</script>
	<style type="text/css">
		.select2-selection__rendered {
			width: 95%;
			top: 10px;
			position: absolute;
		}
	</style>
</head>
<body>
<ul class="nav nav-tabs" role="tablist">
		<li role="presentation" class="active"><a href="#detalls" aria-controls="detalls" role="tab" data-toggle="tab">Detalls</a></li>
		<li role="presentation"><a href="#xmlScsp" aria-controls="xmlScsp" role="tab" data-toggle="tab">Comunicació</a></li>
		<c:if test="${peticio.teRespostes}">
			<li role="presentation"><a href="#xmlRespostes" aria-controls="xmlRespostes" role="tab" data-toggle="tab">Respostes</a></li>
		</c:if>
	</ul>
	<br/>
	<div class="tab-content">
		<div role="tabpanel" class="tab-pane active" id="detalls">
			<ul class="list-group">
				<li class="list-group-item">
					<spring:message code="auditoria.backoffice.peticio.detall.peticio.id"/>:
					${peticio.peticioId}
					<c:if test="${not peticio.sincrona}"><span class="label label-default pull-right">A</span></c:if>
				</li>
				<li class="list-group-item">
					<spring:message code="auditoria.backoffice.peticio.detall.peticio.data"/>: 
					<fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${peticio.dataPeticio}"/>
				</li>
				<c:if test="${not empty peticio.ter}">
					<li class="list-group-item">
						<spring:message code="auditoria.backoffice.peticio.detall.data.est"/>:
						<fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${peticio.ter}"/>
					</li>
				</c:if>
				<c:if test="${not empty peticio.dataDarreraComprovacio}">
					<li class="list-group-item">
						<spring:message code="auditoria.backoffice.peticio.detall.resposta.darrera.comp"/>:
						<fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${peticio.dataDarreraComprovacio}"/>
					</li>
				</c:if>
				<c:if test="${not empty peticio.dataResposta}">
					<li class="list-group-item">
						<spring:message code="auditoria.backoffice.peticio.detall.resposta.data"/>:
						<fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${peticio.dataResposta}"/>
					</li>
				</c:if>
				<li class="list-group-item">
					<spring:message code="auditoria.backoffice.peticio.detall.servei"/>:
					[${peticio.serveiCodi}] ${peticio.serveiDescripcio}
				</li>
				<li class="list-group-item">
					<spring:message code="auditoria.backoffice.peticio.detall.estat"/>:
					<spring:message code="peticio.estat.enum.${peticio.estat}"/>
					<c:if test="${not empty peticio.error}">
						<button id="peticioErrorBtn" class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#peticioError" aria-expanded="false" aria-controls="peticioError"><span class="fa fa-chevron-down"></span></button>
					</c:if>
					<div id="peticioError" class="collapse" style="margin-top:1em">
						<textarea class="form-control" rows="3">${peticio.error}</textarea>
					</div>
				</li>
				<li class="list-group-item">
					<spring:message code="auditoria.backoffice.peticio.detall.estat.scsp"/>: ${peticio.estatScsp}
				</li>
				<li class="list-group-item">
					<spring:message code="auditoria.backoffice.peticio.detall.num.env"/>:
					${peticio.numEnviaments}
				</li>
				<li class="list-group-item">
					<spring:message code="auditoria.backoffice.peticio.detall.num.trans"/>:
					${peticio.numTransmissions}
				</li>
			</ul>
		</div>
		<div role="tabpanel" class="tab-pane" id="xmlScsp">
			<div class="well well-sm">
				<h5>
					<spring:message code="auditoria.backoffice.peticio.detall.xml.peticio"/>
					<button id="xmlPeticioScspBtn" class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#xmlPeticioScsp" aria-expanded="false" aria-controls="xmlPeticioScsp"><span class="fa fa-chevron-down"></span></button>
				</h5>
				<div id="xmlPeticioScsp" class="collapse" style="margin-top:1em">
					<textarea class="form-control" rows="10"></textarea>
				</div>
			</div>
			<div class="well well-sm">
				<h5>
					<spring:message code="auditoria.backoffice.peticio.detall.xml.resposta"/>
					<button id="xmlRespostaScspBtn" class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#xmlRespostaScsp" aria-expanded="false" aria-controls="xmlRespostaScsp"><span class="fa fa-chevron-down"></span></button>
				</h5>
				<div id="xmlRespostaScsp" class="collapse" style="margin-top:1em">
					<textarea class="form-control" rows="10"></textarea>
				</div>
			</div>
		</div>
		<div role="tabpanel" class="tab-pane" id="xmlRespostes">
			<div id="msgSeleccionat" class="alert alert-info"><spring:message code="auditoria.list.missatges.seleccionat"/></div>
			<div class="form-group">
				<label class="control-label col-xs-4" for="entitat"><spring:message code="auditoria.list.missatges.entitat"/></label>
				<select id="entitat" class="form-control" style="width: 100%;">
					<c:forEach var="missatge" items="${missatgesXml}">
						<option value="${missatge.entitat}">${missatge.entitat}</option>
					</c:forEach>
				</select>
			</div>
			<textarea id="missatgeXml" rows="16" class="input-xxlarge" style="width:100%"></textarea>
		</div>
	</div>
</body>
</html>
