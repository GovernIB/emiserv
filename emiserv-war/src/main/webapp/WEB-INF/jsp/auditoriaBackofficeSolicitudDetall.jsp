<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="auditoria.backoffice.solicitud.detall.titol"/></title>
	<script src="<c:url value="/js/vkbeautify.js"/>"></script>
	<emi:modalHead/>
<script>
$(document).ready(function() {

	$('#solicitudError').on('show.bs.collapse', function () {
		$('#solicitudErrorBtn span').attr('class', 'fa fa-chevron-up');
	});
	$('#solicitudError').on('hide.bs.collapse', function () {
		$('#solicitudErrorBtn span').attr('class', 'fa fa-chevron-down');
	});

	$('#xmlPeticioBackoffice').on('show.bs.collapse', function () {
		$('#xmlPeticioBackofficeBtn span').attr('class', 'fa fa-chevron-up');
		var loaded = $('#xmlPeticioBackoffice').data('loaded');
		if (!loaded) {
			$.get("xmlPeticioBackoffice", function(data) {
				$('#xmlPeticioBackoffice textarea').val(data);
				$('#xmlPeticioBackoffice textarea').val(vkbeautify.xml($('#xmlPeticioBackoffice textarea').val()));
				$('#xmlPeticioBackoffice').data('loaded', 'true');
			});
		}
	});
	$('#xmlPeticioBackoffice').on('hide.bs.collapse', function () {
		$('#xmlPeticioBackofficeBtn span').attr('class', 'fa fa-chevron-down');
	});

	$('#xmlRespostaBackoffice').on('show.bs.collapse', function () {
		$('#xmlRespostaBackofficeBtn span').attr('class', 'fa fa-chevron-up');
		var loaded = $('#xmlRespostaBackoffice').data('loaded');
		if (!loaded) {
			$.get("xmlRespostaBackoffice", function(data) {
				$('#xmlRespostaBackoffice textarea').val(data);
				$('#xmlRespostaBackoffice textarea').val(vkbeautify.xml($('#xmlRespostaBackoffice textarea').val()));
				$('#xmlRespostaBackoffice').data('loaded', 'true');
			});
		}
	});
	$('#xmlRespostaBackoffice').on('hide.bs.collapse', function () {
		$('#xmlRespostaBackofficeBtn span').attr('class', 'fa fa-chevron-down');
	});

	$('#errorComunicacioBackoffice').on('show.bs.collapse', function () {
		$('#errorComunicacioBackofficeBtn span').attr('class', 'fa fa-chevron-up');
		var loaded = $('#errorComunicacioBackoffice').data('loaded');
		if (!loaded) {
			$.get("errorComunicacioBackoffice", function(data) {
				$('#errorComunicacioBackoffice textarea').val(data);
				$('#errorComunicacioBackoffice textarea').val(vkbeautify.xml($('#errorComunicacioBackoffice textarea').val()));
				$('#errorComunicacioBackoffice').data('loaded', 'true');
			});
		}
	});
	$('#errorComunicacioBackoffice').on('hide.bs.collapse', function () {
		$('#errorComunicacioBackofficeBtn span').attr('class', 'fa fa-chevron-down');
	});

});
</script>
</head>
<body>
	<ul class="nav nav-tabs" role="tablist">
		<li role="presentation" class="active"><a href="#detalls" aria-controls="detalls" role="tab" data-toggle="tab">Detalls</a></li>
		<c:if test="${solicitud.comunicacioBackofficeDisponible}">
			<li role="presentation"><a href="#xmlBackoffice" aria-controls="xmlBackoffice" role="tab" data-toggle="tab">Comunicació backoffice</a></li>
		</c:if>
	</ul>
	<br/>
	<div class="tab-content">
		<div role="tabpanel" class="tab-pane active" id="detalls">
			<ul class="list-group">
				<li class="list-group-item">
					<spring:message code="auditoria.backoffice.solicitud.detall.peticio.id"/>:
					${solicitud.peticionId}
				</li>
				<li class="list-group-item">
					<spring:message code="auditoria.backoffice.solicitud.detall.solicitud.id"/>:
					${solicitud.solicitudId}
				</li>
				<c:if test="${not empty solicitud.transmisionId}">
					<li class="list-group-item">
						<spring:message code="auditoria.backoffice.solicitud.detall.transmissio.id"/>:
						${solicitud.transmisionId}
					</li>
				</c:if>
				<c:if test="${not empty solicitud.fechaGeneracion}">
					<li class="list-group-item">
						<spring:message code="auditoria.backoffice.solicitud.detall.generacio.data"/>: 
						<fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${solicitud.fechaGeneracion}"/>
					</li>
				</c:if>
				<c:if test="${not empty solicitud.solicitanteId}">
					<li class="list-group-item">
						<spring:message code="auditoria.backoffice.solicitud.detall.solicitant"/>:
						[${solicitud.solicitanteId}] ${solicitud.solicitanteNombre}
					</li>
				</c:if>
				<c:if test="${not empty solicitud.procedimientoCodigo}">
					<li class="list-group-item">
						<spring:message code="auditoria.backoffice.solicitud.detall.procediment"/>:
						[${solicitud.procedimientoCodigo}] ${solicitud.procedimientoNombre}
					</li>
				</c:if>
				<c:if test="${not empty solicitud.unidadTramitadora}">
					<li class="list-group-item">
						<spring:message code="auditoria.backoffice.solicitud.detall.unitat"/>:
						${solicitud.unidadTramitadora}
					</li>
				</c:if>
				<c:if test="${not empty solicitud.funcionarioDocumento}">
					<li class="list-group-item">
						<spring:message code="auditoria.backoffice.solicitud.detall.funcionari"/>:
						[NIF: ${solicitud.funcionarioDocumento}] ${solicitud.funcionarioNombre}
					</li>
				</c:if>
				<c:if test="${not empty solicitud.backofficeEstat}">
					<li class="list-group-item">
						<spring:message code="auditoria.backoffice.solicitud.detall.estat"/>:
						<spring:message code="peticio.estat.enum.${solicitud.backofficeEstat}"/>
						<c:if test="${not empty solicitud.error}">
							<button id="solicitudErrorBtn" class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#solicitudError" aria-expanded="false" aria-controls="solicitudError"><span class="fa fa-chevron-down"></span></button>
						</c:if>
						<div id="solicitudError" class="collapse" style="margin-top:1em">
							<textarea class="form-control" rows="3">${solicitud.error}</textarea>
						</div>
					</li>
				</c:if>
			</ul>
		</div>
		<c:if test="${solicitud.comunicacioBackofficeDisponible}">
			<div role="tabpanel" class="tab-pane" id="xmlBackoffice">
				<div class="well well-sm">
					<h5>
						<spring:message code="auditoria.backoffice.solicitud.detall.xml.peticio"/>
						<button id="xmlPeticioBackofficeBtn" class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#xmlPeticioBackoffice" aria-expanded="false" aria-controls="xmlPeticioBackoffice"><span class="fa fa-chevron-down"></span></button>
					</h5>
					<div id="xmlPeticioBackoffice" class="collapse" style="margin-top:1em">
						<textarea class="form-control" rows="10"></textarea>
					</div>
				</div>
				<c:choose>
					<c:when test="${not solicitud.comunicacioBackofficeError}">
						<div class="well well-sm">
							<h5>
								<spring:message code="auditoria.backoffice.solicitud.detall.xml.resposta"/>
								<button id="xmlRespostaBackofficeBtn" class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#xmlRespostaBackoffice" aria-expanded="false" aria-controls="xmlRespostaBackoffice"><span class="fa fa-chevron-down"></span></button>
							</h5>
							<div id="xmlRespostaBackoffice" class="collapse" style="margin-top:1em">
								<textarea class="form-control" rows="10"></textarea>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<div class="well well-sm">
							<h5>
								<spring:message code="auditoria.backoffice.solicitud.detall.error"/>
								<button id="errorComunicacioBackofficeBtn" class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#errorComunicacioBackoffice" aria-expanded="false" aria-controls="errorComunicacioBackoffice"><span class="fa fa-chevron-down"></span></button>
							</h5>
							<div id="errorComunicacioBackoffice" class="collapse" style="margin-top:1em">
								<textarea class="form-control" rows="10"></textarea>
							</div>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</c:if>
	</div>
</body>
</html>
