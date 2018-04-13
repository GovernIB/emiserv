<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="missatge.xml.titol"/></title>
	<script src="<c:url value="/js/vkbeautify.js"/>"></script>
	<emi:modalHead/>
</head>
<body>

	<textarea id="missatgeXml" rows="16" class="input-xxlarge" style="width:95%">${missatgeXml}</textarea>
	<script type="text/javascript">
	$('#missatgeXml').val(vkbeautify.xml($('#missatgeXml').val()));
	</script>

</body>
</html>
