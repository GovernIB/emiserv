<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/emiserv" prefix="emi"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="titol"><spring:message code="servei.config.rutes.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/datatables.net-scroller/1.4.0/js/dataTables.scroller.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.0/js/dataTables.select.min.js"/>"></script>
	<script src="<c:url value="/js/datatables/keytable-2.1.0/js/dataTables.keyTable.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.0/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.tablednd.js"/>"></script>
	<emi:modalHead/>
<script>
	$(document).ready(function() {
		var adjustModalHeight = function() {
			webutilModalAdjustHeight();
		};
		$('#rutes').on('draw.dt', function() {
			adjustModalHeight();
			// Posa la taula com a ordenable
			$("#rutes").tableDnD({
		    	onDragClass: "drag",
		    	onDrop: function(table, row) {	        	
		        	var pos = row.rowIndex - 1;
		        	if (pos != filaMovem) {
			        	var id= obtenirId(pos);
			        	canviarPosicioRuta(id,pos);
		        	}
		    	},
		    	onDragStart: function(table, row) {
		    			filaMovem = row.rowIndex-1;
				}
		    });
		    $("#rutes tr").hover(function() {
		        $(this.cells[0]).addClass('showDragHandle');
		    }, function() {
		        $(this.cells[0]).removeClass('showDragHandle');
		    });	
		});
	});
	
	function obtenirId(pos){
		if(filaMovem==pos){
			var fila = filaMovem + 2;
		}
		else{
			if( filaMovem < pos){	//baixam elements
				var fila = filaMovem + (pos-filaMovem)+2;
			}else{					//pujam elements
				var fila = filaMovem - (filaMovem-pos)+2;
			}
		}
		id = $("#rutes tr:eq("+fila+")").attr("id");	
		id2 = id.split("_");
		return id2[1] ;
	}
	
	function canviarPosicioRuta( id, pos) {
	  	// Canvia la ordenació sempre amb ordre ascendent
		$('#rutes').DataTable().order([1, 'asc']);
		var getUrl = '<c:url value="/servei/${servei.id}/rutes/"/>'+id+'/moure/'+pos;
		$.ajax({
			type: 'GET',
			url: getUrl,
			async: true,
			success: function(result) {
			},
			error: function(e) {
				console.log("Error canviant l'ordre: " + e);
			},
			complete: function() {
				refrescaTaula();
			}
		});	
	}
	
	function refrescaTaula() {
		$('#rutes').webutilDatatable('refresh');
	}

	// Prova de connexió amb el destí de la ruta (emisor SCSP o backoffice del cedent)
	$(document).on('click', '#rutes .btn-provar', function(e) {
		e.preventDefault();
		e.stopPropagation();
		var $boto = $(this);
		var $resultat = $('#prova-resultat');
		$boto.prop('disabled', true);
		$resultat.attr('class', 'alert alert-info').text('<spring:message code="servei.config.rutes.provar.enviant" javaScriptEscape="true"/>').show();
		$.ajax({
			type: 'GET',
			url: '<c:url value="/servei/${servei.id}/rutes/"/>' + $boto.data('ruta-id') + '/provar',
			dataType: 'json',
			success: function(prova) {
				var text = '[' + $boto.data('entitat') + '] ' + prova.missatge + ' (' + prova.url + ', ' + prova.tempsMs + ' ms)';
				var classe = !prova.correcta ? 'alert-danger' : (prova.credencialsPendents ? 'alert-warning' : 'alert-success');
				$resultat.attr('class', 'alert ' + classe).text(text);
			},
			error: function() {
				$resultat.attr('class', 'alert alert-danger').text('<spring:message code="servei.config.rutes.provar.error" javaScriptEscape="true"/>');
			},
			complete: function() {
				$boto.prop('disabled', false);
				webutilModalAdjustHeight();
			}
		});
	});

</script>
<style>
#rutes .label {
	font-size: 90%;
}
#rutes td:last-child,
#rutes th:last-child {
	white-space: nowrap;
	width: 96px;
	min-width: 96px;
}
</style>
</head>
<body>
	<c:if test="${not empty servei.urlPerDefecte}">
	<form class="form-horizontal well well-sm" style="padding:0;margin-bottom:6px">
		<div class="form-group" style="margin-bottom:0">
			<label class="control-label col-xs-4"><spring:message code="servei.config.rutes.url.defecte"/>:</label>
			<div class="controls col-xs-8">
				<p class="form-control-static">${servei.urlPerDefecte}</p>
			</div>
		</div>
	</form>
	</c:if>
	<div id="prova-resultat" class="alert" style="display:none;margin-bottom:6px;word-break:break-all"></div>
	<form:form method="post" modelAttribute="serveiRutaDestiCommand" role="form">
		<table id="rutes"
			data-toggle="datatable"
			data-url="<c:url value="/servei/${servei.id}/rutes/datatable"/>" 
			data-editable="true" 
			data-editable-button-mode="true"
			data-updatable="true"
			data-editable-auto="true" 
			data-editable-sample-row=".ruta-nova" 
			data-paging-enabled="true" 
			data-ordering="true"
			data-default-order="1" 
			data-length-menu="5" 
			class="table table-striped table-bordered" 
			style="width:100%">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="ordre" data-orderable="true" width="10%"><spring:message code="servei.config.rutes.ordre"/></th>
					<th data-col-name="entitatCodi" data-orderable="true" width="12%"><spring:message code="servei.config.rutes.camp.entitat.codi"/></th>
					<th data-col-name="destiTipus" data-orderable="true" data-template="#cellDestiTipusTemplate" width="14%">
						<spring:message code="servei.config.rutes.camp.desti.tipus"/>
						<script id="cellDestiTipusTemplate" type="text/x-jsrender">
							{{if destiTipus == 'BACKOFFICE'}}<span class="label label-primary"><spring:message code="servei.ruta.desti.tipus.enum.BACKOFFICE"/></span>{{else}}<span class="label label-default"><spring:message code="servei.ruta.desti.tipus.enum.EMISERV"/></span>{{/if}}
						</script>
					</th>
					<th data-col-name="url" data-orderable="true" width="46%"><spring:message code="servei.config.rutes.camp.url"/></th>
					<th data-col-name="backofficeAutenticacio" data-orderable="false" data-template="#cellAutenticacioTemplate" width="12%">
						<spring:message code="servei.config.rutes.camp.autenticacio"/>
						<script id="cellAutenticacioTemplate" type="text/x-jsrender">
							{{if destiTipus == 'BACKOFFICE'}}
								{{if backofficeAutenticacio == 'TOKEN_CAIB'}}<spring:message code="backoffice.auth.tipus.enum.TOKEN_CAIB"/>{{else backofficeAutenticacio == 'TEXT_CLAR'}}<spring:message code="backoffice.auth.tipus.enum.TEXT_CLAR"/>{{else}}<spring:message code="backoffice.auth.tipus.enum.NINGUNA"/>{{/if}}
								{{if credencialsConfigurades === true}}
									<span class="fa fa-check text-success" title="<spring:message code="servei.config.rutes.credencials.configurades"/>: {{:credencialsPropietats}}username/password"></span>
								{{else credencialsConfigurades === false}}
									<span class="fa fa-warning text-danger" title="<spring:message code="servei.config.rutes.credencials.pendents"/>: {{:credencialsPropietats}}username/password"></span>
								{{/if}}
							{{/if}}
						</script>
					</th>
					<th data-col-name="credencialsPropietats" data-visible="false"></th>
					<th data-col-name="credencialsConfigurades" data-visible="false"></th>
					<th data-col-name="id" data-orderable="false" data-template="#cellProvarTemplate" width="1%">
						<script id="cellProvarTemplate" type="text/x-jsrender">
							<button type="button" class="btn btn-default btn-sm btn-provar" data-ruta-id="{{:id}}" data-entitat="{{:entitatCodi}}" title="<spring:message code="servei.config.rutes.provar"/>"><span class="fa fa-plug"></span></button>
						</script>
					</th>
				</tr>
			</thead>
			<tfoot>
				<tr class="ruta-nova">
					<th>
						<emi:inputHidden name="id"/>
					</th>
					<th>
						<emi:inputText name="ordre" disabled="true" inline="true"/>
					</th>
					<th>
						<emi:inputText name="entitatCodi" textKey="servei.config.rutes.camp.entitat.codi" inline="true" required="true"/>
					</th>
					<th>
						<%-- Selectors sense select2: la taula editable clona la fila de mostra per editar cada ruta --%>
						<form:select path="destiTipus" cssClass="form-control">
							<c:forEach var="opcio" items="${destiTipusEnumOptions}">
								<form:option value="${opcio.value}"><spring:message code="${opcio.text}"/></form:option>
							</c:forEach>
						</form:select>
					</th>
					<th>
						<emi:inputText name="url" textKey="servei.config.rutes.camp.url" inline="true" required="true"/>
					</th>
					<th>
						<form:select path="backofficeAutenticacio" cssClass="form-control">
							<form:option value=""/>
							<c:forEach var="opcio" items="${backofficeAutenticacioTipusEnumOptions}">
								<form:option value="${opcio.value}"><spring:message code="${opcio.text}"/></form:option>
							</c:forEach>
						</form:select>
					</th>
					<th></th>
					<th></th>
					<th></th>
				</tr>
			</tfoot>
		</table>
		<div id="modal-botons" class="well">
			<a href="<c:url value="/servei"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
		</div>
	</form:form>
</body>
</html>
