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
<html>
<head>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${idioma}.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.${idioma}.js"/>"></script>
	
	<script>
		var eyeEnabled = true;
		
		$(document).ready(function() {
			$('#btn-eye').click(function() {
				if(eyeEnabled) {
					eyeEnabled = false;
					$('#password').get(0).type = 'text';
					$("#icon-eye").attr('class', 'icon-eye-close');
				} else {
					eyeEnabled = true;
					$('#password').get(0).type = 'password';
					$("#icon-eye").attr('class', 'icon-eye-open');
				}
			});
			
			$('#btn-calendar-alta').click(function() {
				$('#dataAlta').focus();
			});
			
			$('#btn-calendar-baixa').click(function() {
				$('#dataBaixa').focus();
			});
		});
	</script>
	
	<title>
		<c:choose>
			<c:when test="${empty autoritzacioCaCommand.id}"><spring:message code="autoritatca.form.titol.crear"/></c:when>
			<c:otherwise><spring:message code="autoritatca.form.titol.modificar"/></c:otherwise>
		</c:choose>
	</title>
	
</head>
<body>

	<c:url value="/scsp/autoritatca/save" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="autoritzacioCaCommand">
	
		<form:hidden path="id"/>	
		<fieldset>
		<div class="container-fluid">
			<div class="row">
				<div class="col-md-12">
					<emi:inputText name="codca" required="true" labelSize="1" inline="false" textKey="autoritatca.form.camp.codi"/>
					<emi:inputText name="nombre" required="true" labelSize="1" inline="false" textKey="autoritatca.form.camp.nom"/>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div class="pull-right">
						<button type="submit" class="btn btn-primary" ><spring:message code="comu.boto.guardar" /></button>
						<a href="<c:url value="/scsp/autoritatca"/>" class="btn btn-default"><spring:message code="comu.boto.cancelar"/></a>
					</div>
				</div>	
			</div>
		</div>						
		</fieldset>		
	</form:form>

</body>
</html>
