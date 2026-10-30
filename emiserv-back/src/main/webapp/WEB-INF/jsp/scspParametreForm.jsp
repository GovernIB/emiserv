<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi" %>
<%
pageContext.setAttribute(
		"idioma",
		org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage());
%>
<c:choose>
	<c:when test="${empty autoritzacioCaCommand.id}"><c:set var="titol"><spring:message code="parametres.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="autoritatca.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${idioma}.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.${idioma}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<emi:modalHead/>
	<script type="application/javascript">
		$(document).ready(function() {

			var adjustModalHeight = function() {
				webutilModalAdjustHeight();
			};

			$("#nombre").on("change paste keyup", function() {
				if ($(this).val().toUpperCase().includes("PASS")) {
					$("#pass").show();
					$("#no_pass").hide();
					adjustModalHeight();
				} else {
					$("#pass").hide();
					$("#no_pass").show();
				}
			});
			$("#nombre").trigger("change");

			$('#passw, #passw_rep').on('keyup', function () {
				if ($('#passw').val() == $('#passw_rep').val()) {
					$('#valid').removeClass("fa-times").addClass("fa-check").css('color', 'green');
				} else
					$('#valid').removeClass("fa-check").addClass("fa-times").css('color', 'red');
				$("#valor").val($('#passw').val());
				$("#passw_rep").removeClass("error");
			});

			$(".input-group-addon").on('click', function(event) {
				event.preventDefault();
				if($('#show_hide_passw input').attr("type") == "text"){
					$('#show_hide_passw input').attr('type', 'password');
					$('#show_hide_passw i').addClass( "fa-eye-slash" );
					$('#show_hide_passw i').removeClass( "fa-eye" );
					$('#show_hide_passw_rep input').attr('type', 'password');
					$('#show_hide_passw_rep i').addClass( "fa-eye-slash" );
					$('#show_hide_passw_rep i').removeClass( "fa-eye" );
				}else if($('#show_hide_passw input').attr("type") == "password"){
					$('#show_hide_passw input').attr('type', 'text');
					$('#show_hide_passw i').removeClass( "fa-eye-slash" );
					$('#show_hide_passw i').addClass( "fa-eye" );
					$('#show_hide_passw_rep input').attr('type', 'text');
					$('#show_hide_passw_rep i').removeClass( "fa-eye-slash" );
					$('#show_hide_passw_rep i').addClass( "fa-eye" );
				}
			});

			if ($("#nombre").val().toUpperCase().includes("PASS")) {
				$('#passw').val($('#valor').val());
				$('#passw_rep').val($('#valor').val());
			}

			$('form').on("submit", function() {
				if ($("#nombre").val().toUpperCase().includes("PASS") && $('#passw').val() != $('#passw_rep').val()) {
					$('#passw_rep').addClass("error");
					return false;
				}
				return true;
			});
		});
	</script>
	<style type="text/css">
		input.error {
			border-color: red;
		}
	</style>
</head>
<body>
<c:set var="formAction"><emi:modalUrl value="/scsp/parametres/save"/></c:set>
<div class="alert alert-info">Si el nom del paràmetre conté la cadena <b>pass</b>, s'interpretarà el paràmetre com una contrasenya</div>
<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="scspParametreCommand">
	<form:hidden path="nou"/>
	<c:if test="${not scspParametreCommand.nou}">
		<form:hidden path="nombre"/>
	</c:if>
	<emi:inputText name="nombre" required="true" inline="false" textKey="parametres.form.camp.nom" labelSize="2" disabled="${not scspParametreCommand.nou}"/>
	<emi:inputText name="descripcion" textKey="parametres.form.camp.descripcio" labelSize="2"/>
	<div id="no_pass">
	<emi:inputText name="valor" textKey="parametres.form.camp.valor" labelSize="2"/>
	</div>
	<div id="pass">
		<div class="form-group">
			<label class="control-label col-xs-2" for="valor">Valor</label>
			<div class="col-xs-10">
				<div class="input-group" id="show_hide_passw">
					<input id="passw" name="passw" class="form-control" type="password" value="">
					<div class="input-group-addon">
						<a href=""><i class="fa fa-eye-slash" aria-hidden="true"></i></a>
					</div>
				</div>
			</div>
		</div>
		<div class="form-group">
			<label class="control-label col-xs-2" for="valor">Valor rep.</label>
			<div class="col-xs-10">
				<div class="input-group" id="show_hide_passw_rep">
					<input id="passw_rep" name="passw_rep" class="form-control" type="password" value="">
					<div class="input-group-addon">
						<a href=""><i class="fa fa-eye-slash" aria-hidden="true"></i></a>
					</div>
				</div>
			</div>
		</div>
		<span class="fa" id='valid' style="position: relative; top: -40px; left: calc(100% + 20px);"></span>
	</div>
	<div id="modal-botons" class="well">
		<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
		<a href="<c:url value="/scsp/parametres"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
	</div>
</form:form>
</body>
</html>
