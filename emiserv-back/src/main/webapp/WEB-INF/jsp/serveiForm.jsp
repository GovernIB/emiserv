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
<c:choose>
	<c:when test="${empty serveiCommand.id}"><c:set var="titol"><spring:message code="servei.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="servei.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${idioma}.js"/>"></script>
	<emi:modalHead/>
<style>
#fakeDadesEspecifiques { font-family: monospace; font-size: 12px; resize: vertical; }
</style>
<script>
// Mode fake: si la plantilla de dades específiques és buida s'omple amb la per defecte del servei
function omplirPlantillaFake() {
	var plantilla = $('#fakeDadesEspecifiques');
	var codi = $('form input#codi').val();
	if (plantilla.length == 0 || !$('form input#fake').is(':checked') || !codi || $.trim(plantilla.val()) != '') {
		return;
	}
	$.get('<c:url value="/servei/fakePlantillaPerDefecte"/>', {codi: codi}, function(resposta) {
		if (resposta && resposta.plantilla && $.trim(plantilla.val()) == '') {
			plantilla.val(resposta.plantilla);
		}
	});
}
$(document).ready(function() {
	$("form select#tipus").on('change', function() {
		var tipusVal = $(this).val();
		$('[id^=camps-]').hide();
		$('[id^=camps-] :input').attr('disabled', 'disabled');
		if (tipusVal == 'BACKOFFICE') {
			$('#camps-backoffice').show();
			$('#camps-backoffice :input').removeAttr('disabled');
		} else if (tipusVal == 'ENRUTADOR') {
			$('#camps-resolver').show();
			$('#camps-resolver :input').removeAttr('disabled');
		} else if (tipusVal == 'ENRUTADOR_MULTIPLE') {
			$('#camps-multi-resolver').show();
			$('#camps-multi-resolver :input').removeAttr('disabled');
		}
		if (tipusVal == 'ENRUTADOR' || tipusVal == 'ENRUTADOR_MULTIPLE') {
			$('#camps-rutes-backoffice').show();
			$('#camps-rutes-backoffice :input').removeAttr('disabled');
			$('#camps-asincron').show();
			$('#camps-asincron :input').removeAttr('disabled');
			$("form input#asyncSincron").trigger('change');
		}
	});
	// Ritme d'enviament als cedents: només si s'accepten peticions asíncrones processades com a síncrones
	$("form input#asyncSincron").on('change', function() {
		var actiu = $(this).is(':checked') && !$(this).is(':disabled');
		$('#asyncInterval, #asyncConcurrencia').prop('disabled', !actiu);
	});
	// Mode fake: el resultat i la plantilla només s'editen amb el mode fake actiu
	$("form input#fake").on('change', function() {
		var actiu = $(this).is(':checked');
		$('#fakeResultat, #fakeDadesEspecifiques').prop('disabled', !actiu);
		omplirPlantillaFake();
	});
	$("form input#codi").on('change', omplirPlantillaFake);
	$("form input#fake").trigger('change');
	$("form select#tipus").trigger('change');
});
</script>
</head>
<body>
	<c:set var="formAction"><emi:modalUrl value="/servei/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="serveiCommand" role="form" style="height:400px;">
		<form:hidden path="id"/>
		<c:choose>
			<c:when test="${empty serveiCommand.id}">
				<emi:inputText name="codi" textKey="servei.form.camp.codi" required="true"/>
			</c:when>
			<c:otherwise>
				<emi:inputText name="codi" textKey="servei.form.camp.codi" required="true" disabled="true"/>
				<form:hidden path="codi"/>
			</c:otherwise>
		</c:choose>
		<emi:inputText name="nom" textKey="servei.form.camp.nom" required="true"/>
		<emi:inputSelect name="tipus" textKey="servei.form.camp.tipus" required="true" optionItems="${serveiTipusEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
		<emi:inputTextarea name="descripcio" textKey="servei.form.camp.descripcio"/>
		<div id="camps-backoffice">
			<emi:inputSelect name="backofficeClass" textKey="servei.form.camp.class.back" required="true" optionItems="${classesBackoffice}" emptyOption="true"/>
			<emi:inputText name="backofficeCaibUrl" textKey="servei.form.camp.url" required="true"/>
			<emi:inputSelect name="backofficeCaibAutenticacio" textKey="servei.form.camp.autenticacio" required="true" optionItems="${backofficeAutenticacioTipusEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
			<emi:inputSelect name="backofficeCaibAsyncTipus" textKey="servei.form.camp.back.async.tipus" required="true" optionItems="${backofficeAsyncTipusEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
			<emi:inputText name="backofficeCaibAsyncTer" textKey="servei.form.camp.back.async.ter"/>
		</div>
		<div id="camps-resolver">
			<emi:inputSelect name="resolverClass" textKey="servei.form.camp.class.resolv" required="true" optionItems="${classesResolver}"/>
			<emi:inputText name="urlPerDefecte" textKey="servei.form.camp.url.defecte"/>
		</div>
		<div id="camps-multi-resolver">
			<emi:inputSelect name="responseResolverClass" textKey="servei.form.camp.class.responseResolver" required="true" optionItems="${classesResponseResolver}"/>
		</div>
		<div id="camps-rutes-backoffice">
			<emi:inputCheckbox name="rutesBackoffice" textKey="servei.form.camp.rutes.backoffice"/>
			<div class="form-group">
				<div class="col-xs-8 col-xs-offset-4">
					<p class="help-block" style="margin-top:-10px">
						<span class="fa fa-info-circle"></span>&nbsp;<spring:message code="servei.form.camp.rutes.backoffice.ajuda"/>
						<c:if test="${rutesBackofficeCount > 0}"><br/><span class="fa fa-warning"></span>&nbsp;<spring:message code="servei.form.camp.rutes.backoffice.existents" arguments="${rutesBackofficeCount}"/></c:if>
					</p>
				</div>
			</div>
		</div>
		<div id="camps-asincron">
			<emi:inputCheckbox name="asyncSincron" textKey="servei.form.camp.async.sincron"/>
			<emi:inputText name="asyncInterval" textKey="servei.form.camp.async.interval" placeholderKey="servei.form.camp.async.valor.global"/>
			<emi:inputText name="asyncConcurrencia" textKey="servei.form.camp.async.concurrencia" placeholderKey="servei.form.camp.async.valor.global"/>
		</div>
		<c:choose>
			<c:when test="${fakePermes}">
				<div id="bloc-fake">
					<emi:inputCheckbox name="fake" textKey="servei.form.camp.fake"/>
					<emi:inputSelect name="fakeResultat" textKey="servei.form.camp.fake.resultat" optionItems="${serveiFakeResultatEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" emptyOption="true"/>
					<emi:inputTextarea name="fakeDadesEspecifiques" textKey="servei.form.camp.fake.dades" files="20"/>
				</div>
			</c:when>
			<c:otherwise>
				<%-- El servei aplica sempre els camps fake en desar: s'han de tornar a enviar tal com estan --%>
				<form:hidden path="fake"/>
				<form:hidden path="fakeResultat"/>
				<form:hidden path="fakeDadesEspecifiques"/>
			</c:otherwise>
		</c:choose>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/servei"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
