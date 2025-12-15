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
    <div id="modal-botons" class="well">
        <button id="cbcopy" class="btn btn-default"><span class="fa fa-clipboard"></span> <spring:message code="comu.boto.copiar"/></button>
        <a href="<c:url value="/"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
        <script type="text/javascript">
            function copyToClipboard(text) {
                if (navigator.clipboard && window.isSecureContext) {
                    navigator.clipboard.writeText(text).then(() => {})
                    .catch(err => {fallbackCopyText(text);});
                } else {
                    fallbackCopyText(text);
                }
            }

            function fallbackCopyText(text) {
                let $temp = $("<textarea>");
                $("body").append($temp);
                $temp.val(text);
                $temp[0].select();
                $temp[0].setSelectionRange(0, 99999);
                try {
                    if (!document.execCommand('copy')) {
                        console.error("No s'ha pogut copiar amb fallback.");
                    }
                } catch (err) {
                    console.error("Error en fallback: ", err);
                }
                $temp.remove();
            }

            $('#cbcopy').click(() => {
                copyToClipboard($('#missatgeXml').val());
            });
        </script>
    </div>
</body>
</html>