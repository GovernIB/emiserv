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

</script>
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
	<form:form method="post" modelAttribute="serveiRutaDestiCommand" role="form">
		<table id="rutes" 
			data-toggle="datatable" 
			data-url="<c:url value="/servei/${servei.id}/rutes/datatable"/>" 
			data-editable="true" 
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
					<th data-col-name="entitatCodi" data-orderable="true" width="20%"><spring:message code="servei.config.rutes.camp.entitat.codi"/></th>
					<th data-col-name="url" data-orderable="true" width="70%"><spring:message code="servei.config.rutes.camp.url"/></th>
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
						<emi:inputText name="url" textKey="servei.config.rutes.camp.url" inline="true" required="true"/>
					</th>
				</tr>
			</tfoot>
		</table>
		<div id="modal-botons" class="well">
			<a href="<c:url value="/servei"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
		</div>
	</form:form>
</body>
</html>
