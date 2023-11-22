<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="auditoria.backoffice.peticio.detall.titol"/></title>
	<script src="<c:url value="/js/vkbeautify.js"/>"></script>
	<emi:modalHead/>
<script>
$(document).ready(function() {
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
			$.get("xmlPeticioRest", function(data) {
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
			$.get("xmlRespostaRest", function(data) {
				$('#xmlRespostaScsp textarea').val(data);
				$('#xmlRespostaScsp textarea').val(vkbeautify.xml($('#xmlRespostaScsp textarea').val()));
				$('#xmlRespostaScsp').data('loaded', 'true');
			});
		}
	});
	$('#xmlRespostaScsp').on('hide.bs.collapse', function () {
		$('#xmlRespostaScspBtn span').attr('class', 'fa fa-chevron-down');
	});
});
</script>
</head>
<body>
	<ul class="nav nav-tabs" role="tablist">
		<li role="presentation" class="active"><a href="#detalls" aria-controls="detalls" role="tab" data-toggle="tab">Detalls</a></li>
		<li role="presentation"><a href="#xmlScsp" aria-controls="xmlScsp" role="tab" data-toggle="tab">Comunicació</a></li>
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
	</div>
</body>
</html>
