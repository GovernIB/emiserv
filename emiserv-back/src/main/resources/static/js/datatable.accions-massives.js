/**
 * Selecció de files i accions massives d'una datatable (webutil.datatable.js amb
 * data-selection-enabled="true"), com als llistats de Notib i Ripea. La selecció es desa a la
 * sessió del servidor (BaseAccionsMassivesController), així es manté en canviar de pàgina.
 *
 * Elements de la pàgina (normalment dins el data-botons-template de la taula):
 *  - [data-seleccio="tots"]: selecciona tots els elements del filtre actual.
 *  - [data-seleccio="cap"]: buida la selecció.
 *  - .seleccioCount: nombre d'elements seleccionats.
 *  - a[data-accio-massiva]: acció sobre la selecció (href); si té data-confirmacio, es demana
 *    confirmació abans d'executar-la.
 *
 * @param $taula taula de la datatable.
 * @param urlBase URL base del controlador (sense barra final), p. ex. /emiservback/organisme.
 * @param missatges { seleccioBuida: '...' }
 */
function webutilAccionsMassives($taula, urlBase, missatges) {
	var actualitzarComptador = function(numSeleccionats) {
		$('.seleccioCount').text(numSeleccionats);
	};
	$taula.on('selectionchange.dataTable', function(e, accio, idsAfectats) {
		// 'restore' és la selecció que torna el servidor en pintar la pàgina: ja hi és desada
		if ((accio === 'select' || accio === 'deselect') && idsAfectats && idsAfectats.length) {
			$.get(urlBase + '/' + accio, {ids: idsAfectats}, actualitzarComptador);
		}
	});
	$(document).on('click', '[data-seleccio="tots"]', function() {
		var $botons = $('[data-seleccio]').prop('disabled', true);
		$.get(urlBase + '/seleccionar/all', function(numSeleccionats) {
			actualitzarComptador(numSeleccionats);
			$taula.webutilDatatable('select-all');
		}).always(function() {
			$botons.prop('disabled', false);
		});
		return false;
	});
	$(document).on('click', '[data-seleccio="cap"]', function() {
		var $botons = $('[data-seleccio]').prop('disabled', true);
		$.get(urlBase + '/deselect', function(numSeleccionats) {
			actualitzarComptador(numSeleccionats);
			$taula.webutilDatatable('select-none');
		}).always(function() {
			$botons.prop('disabled', false);
		});
		return false;
	});
	$(document).on('click', 'a[data-accio-massiva]', function() {
		var numSeleccionats = Number($('.seleccioCount').first().text());
		if (!numSeleccionats) {
			alert(missatges.seleccioBuida);
			return false;
		}
		var confirmacio = $(this).data('confirmacio');
		if (confirmacio && !confirm(confirmacio.replace('{0}', numSeleccionats))) {
			return false;
		}
		window.location.href = $(this).attr('href');
		return false;
	});
}
