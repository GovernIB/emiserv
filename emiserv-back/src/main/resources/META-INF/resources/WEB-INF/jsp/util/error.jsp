<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<title>Error ${errorObject.statusCode}</title>
</head>
<body>
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
			<div id="traca-stack" class="panel-collapse collapse in">
				<div class="panel-body" >
					<pre>${errorObject.fullStackTrace}</pre>
				</div>
			</div>
		</div>
	</c:if>
</body>
</html>
