<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%-- Mostra els errors de validació globals, però peta amb JBoss 7 --%>
<%--c:forEach var="attributeName" items="${pageContext.request.attributeNames}">
	<c:if test="${not fn:contains(attributeName, '.') && fn:contains(attributeName, 'ommand')}">
		<spring:hasBindErrors name="${attributeName}">
			<c:if test="${not empty errors.globalErrors}">
				<c:forEach var="error" items="${errors.globalErrors}">
					<div class="alert alert-danger">
						<button type="button" class="close-missatge" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
						<spring:message message="${error}"/>
					</div>
				</c:forEach>
			</c:if>
		</spring:hasBindErrors>
	</c:if>
</c:forEach--%>
<%
es.caib.emiserv.back.helper.MissatgeHelper.getGlobalErrorsFromCommands(request);
%>

<%
	request.setAttribute(
		"sessionErrors",
		es.caib.emiserv.back.helper.MissatgeHelper.getErrors(request, true));
%>
<c:forEach var="text" items="${sessionErrors}">
	<div class="alert alert-danger">
		<button type="button" class="close-missatge" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
		${text}
	</div>
</c:forEach>

<%
	request.setAttribute(
		"sessionWarnings",
		es.caib.emiserv.back.helper.MissatgeHelper.getWarnings(request, true));
%>
<c:forEach var="text" items="${sessionWarnings}">
	<div class="alert alert-warning">
		<button type="button" class="close-missatge" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
		${text}
	</div>
</c:forEach>

<%
	request.setAttribute(
		"sessionSuccesses",
		es.caib.emiserv.back.helper.MissatgeHelper.getSuccesses(request, true));
%>
<c:forEach var="text" items="${sessionSuccesses}">
	<div class="alert alert-success">
		<button type="button" class="close-missatge" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
		${text}
	</div>
</c:forEach>

<%
	request.setAttribute(
		"sessionInfos",
		es.caib.emiserv.back.helper.MissatgeHelper.getInfos(request, true));
%>
<c:forEach var="text" items="${sessionInfos}">
	<div class="alert alert-info">
		<button type="button" class="close-missatge" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
		${text}
	</div>
</c:forEach>
