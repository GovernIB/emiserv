<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholder" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholderKey" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelText"><c:choose><c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when><c:when test="${not empty text}">${text}</c:when><c:otherwise>${campPath}</c:otherwise></c:choose><c:if test="${required}">*</c:if></c:set>
<c:set var="campPlaceholder"><c:choose><c:when test="${not empty placeholderKey}"><spring:message code="${placeholderKey}"/></c:when><c:otherwise>${placeholder}</c:otherwise></c:choose></c:set>
<c:set var="campLabelSize" value="${4}"/><c:if test="${not empty labelSize}"><c:set var="campLabelSize" value="${labelSize}"/></c:if>
<c:choose>
	<c:when test="${not inline}">
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
			<label class="control-label col-xs-${campLabelSize}" for="${campPath}">${campLabelText}</label>
			<div class="col-xs-${12 - campLabelSize}">
				<div class="fileinput fileinput-new input-group" data-provides="fileinput">
					<div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
					<span class="input-group-addon btn btn-default btn-file" style="width:auto"><span class="fileinput-new">Seleccionar</span><span class="fileinput-exists">Canviar</span><input type="file" id="${campPath}" name="${campPath}"></span>
					<a href="#" class="input-group-addon btn btn-default fileinput-exists" style="width:auto" data-dismiss="fileinput">Netejar</a>
				</div>
				<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
    		<label class="sr-only" for="${campPath}">${campLabelText}</label>
    		<div class="fileinput fileinput-new input-group" data-provides="fileinput">
				<div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
				<span class="input-group-addon btn btn-default btn-file" style="width:auto"><span class="fileinput-new">Seleccionar</span><span class="fileinput-exists">Canviar</span><input type="file" id="${campPath}" name="${campPath}"></span>
				<a href="#" class="input-group-addon btn btn-default fileinput-exists" style="width:auto" data-dismiss="fileinput">Netejar</a>
			</div>
  		</div>
	</c:otherwise>
</c:choose>
