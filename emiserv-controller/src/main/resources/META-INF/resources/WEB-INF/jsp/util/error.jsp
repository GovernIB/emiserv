<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="title" value="Error HTTP ${errorObject.statusCode}"/>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>Emiserv - ${title}</title>
	<link href="<c:url value="/webjars/bootstrap/3.3.6/dist/css/bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/font-awesome/4.5.0/css/font-awesome.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/estils.css"/>" rel="stylesheet">
	<link rel="shortcut icon" href="<c:url value="/img/favicon.png"/>" type="image/x-icon" />
	<script src="<c:url value="/webjars/jquery/1.12.0/dist/jquery.min.js"/>"></script>
	<!-- Llibreria per a compatibilitat amb HTML5 -->
	<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->
	<script src="<c:url value="/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"/>"></script>
<style>
body {
	background-image:url(<c:url value="/img/background-pattern.png"/>);
	color:#666666;
	padding-top: 120px;
}
#app-logo {
	font-size: 12px;
}
#app-logo-icon strong {
	font-size: 30px;
}
#app-logo-text {
	font-size: 40px;
	position: relative;
	top: 0.2em;
	color: white;
	font-weight: bold;
}
</style>
<script>
$(document).ready(function() {
	$('#refresh').on('click', () => {
		window.location.reload();
	});
});
</script>
</head>
<body>
<div class="navbar navbar-default navbar-fixed-top navbar-app" role="navigation">
		<div class="container container-caib">
			<div class="navbar-header">
				<div class="navbar-brand">
					<div id="govern-logo" class="pull-left">
						<img src="<c:url value="/img/govern-logo.png"/>" height="65" alt="<spring:message code="decorator.govern"/>" />
					</div>
					<div id="app-logo" class="pull-left">
						<span id="app-logo-icon" class="fa-stack fa-3x">
							<i class="fa fa-square fa-stack-2x"></i>
							<strong class="fa-stack-1x fa-inverse">Em</strong>
						</span>
						<span id="app-logo-text">Emiserv</span>
					</div>
				</div>
			</div>
			<div class="navbar-collapse collapse">
				<div class="nav navbar-nav navbar-right">
					<ul class="list-inline pull-right">
						<li></li>
					</ul>
					<div class="clearfix"></div>
					<div class="btn-group navbar-btn navbar-right">
						<a href="<c:url value="/"/>" class="btn btn-primary">Anar a la pàgina principal</a>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="container container-main container-caib">
		<div class="panel panel-default">
			<div class="panel-heading">
				<h2>${title}</h2>
			</div>
			<div class="panel-body">
				<c:if test="${not empty errorObject}">
					<dl class="dl-horizontal">
						<dt>Recurs</dt>
						<dd>${errorObject.requestUri}</dd>
						<dt>Missatge</dt>
						<dd>${errorObject.exceptionMessage}</dd>
					</dl>
					<div class="panel panel-default" id="traca-panel">
						<div class="panel-heading">
							<h4 class="panel-title">
								<a class="accordion-toggle" data-toggle="collapse" data-parent="#traca-panel" href="#traca-stack">Traça de l'error</a>
							</h4>
						</div>
						<div id="traca-stack" class="panel-collapse collapse">
							<div class="panel-body" >
								<pre>${errorObject.fullStackTrace}</pre>
							</div>
						</div>
					</div>
					<div>
						<button id="refresh" class="btn btn-default btn-sm pull-right">
							<span class="fa fa-refresh"></span>&nbsp;Tornar a intentar
						</button>
					</div>
				</c:if>
			</div>
		</div>
	</div>
	<div class="container container-foot container-caib">
		<div class="pull-left app-version"><p>EMISERV <emi:versio/></p></div>
		<div class="pull-right govern-footer">
			<p>
				<img src="<c:url value="/img/govern-logo-neg.png"/>" hspace="5" height="30" alt="<spring:message code='decorator.govern'/>" />
				<img src="<c:url value="/img/una_manera.png"/>" 	 hspace="5" height="30" alt="<spring:message code='decorator.logo.manera'/>" />
				<img src="<c:url value="/img/feder7.png"/>" 	     hspace="5" height="35" alt="<spring:message code='decorator.logo.feder'/>" />
				<img src="<c:url value="/img/uenegroma.png"/>"	     hspace="5" height="50" alt="<spring:message code='decorator.logo.ue'/>" />
			</p>
		</div>
	</div>
</body>
</html>
