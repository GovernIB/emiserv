<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<% pageContext.setAttribute("idioma", org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()); %>
<html>
<head>
	<title><spring:message code="missatge.xml.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${idioma}.js"/>"></script>
	<script src="<c:url value="/js/vkbeautify.js"/>"></script>
	<script>
		var missatges = new Map();
		var entitatSeleccionada = null;
		var msg;
		<c:forEach items="${missatgesXml}" var="missatge">
		missatges.set('${missatge.entitat}', `${missatge.xmlResposta}`);
		if (${missatge.respostaEscollida}) {
			entitatSeleccionada = '${missatge.entitat}';
		}
		</c:forEach>
		$(document).ready(function() {
			$("#entitat").select2({
				theme: "bootstrap",
				allowClear: false,
				minimumResultsForSearch: 10
			});
			$("#entitat").on('select2-open', function() {
				var iframe = $('.modal-body iframe', window.parent.document);
				var height = $('html').height() + 30;
				iframe.height(height + 'px');
			});
			$("#entitat").on('select2-close', function() {
				var iframe = $('.modal-body iframe', window.parent.document);
				var height = $('html').height();
				iframe.height(height + 'px');
			});
			$("#entitat").on('change', function() {
				// $("#missatgeXml").val(missatges.get($(this).val()));
				$('#missatgeXml').val(vkbeautify.xml(missatges.get($(this).val())));
				$("#msgSeleccionat").css("display", ($(this).val() == entitatSeleccionada ? "block" : "none"));
			});
			if (entitatSeleccionada != null) {
				$("#entitat").val(entitatSeleccionada);
				$("#entitat").trigger("change");
			}
		});

		function escape(htmlStr) {
			return htmlStr.replace(/&/g, "&amp;")
					// .replace(/</g, "&lt;")
					// .replace(/>/g, "&gt;")
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
	<emi:modalHead/>
</head>
<body>
	<div id="msgSeleccionat" class="alert alert-info"><spring:message code="auditoria.list.missatges.seleccionat"/></div>
	<div class="form-group">
		<label class="control-label col-xs-4" for="entitat"><spring:message code="auditoria.list.missatges.entitat"/></label>
		<select id="entitat" class="form-control">
			<c:forEach var="missatge" items="${missatgesXml}">
				<option value="${missatge.entitat}">${missatge.entitat}</option>
			</c:forEach>
		</select>
	</div>
	<textarea id="missatgeXml" rows="16" class="input-xxlarge" style="width:100%"></textarea>
</body>
</html>
