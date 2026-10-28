/**
 * Catàleg d'icones (Material Icons) per **concepte**. L'entrada de menú, el títol de la pantalla i els
 * botons que representen el mateix concepte han de fer servir sempre la mateixa icona.
 *
 * Aquí hi ha les claus comunes a totes les aplicacions (pantalles, accions, estats, filtre i selecció). Les icones de les
 * pantalles pròpies de cada aplicació es declaren a l'aplicació, fora d'aquesta carpeta.
 */
export const icons = {
    // --- Pantalles comunes ---------------------------------------------------------------------
    inici: 'home',
    avis: 'campaign',
    mapaWeb: 'map',
    accessibilitat: 'accessibility_new',
    noTrobat: 'search_off',

    // --- Rols (insígnia del botó d'usuari de la capçalera) -------------------------------------
    rolSuper: 'shield',
    rolAdmin: 'admin_panel_settings',
    rolAdminLectura: 'visibility',
    rolOrgan: 'account_tree',
    rolAplicacio: 'apps',
    rolUsuari: 'person',

    // --- Accions -------------------------------------------------------------------------------
    crea: 'add',
    modifica: 'edit',
    esborra: 'delete',
    esborraTot: 'delete_sweep',
    desa: 'save',
    desfes: 'undo',
    cancela: 'close',
    accepta: 'check',
    activa: 'check',
    desactiva: 'close',
    executa: 'bolt',
    refresca: 'refresh',
    sincronitza: 'cached',
    copia: 'content_copy',
    descarrega: 'file_download',
    carrega: 'file_upload',
    detall: 'info',
    informe: 'description',
    llista: 'list',
    diagnostic: 'monitor_heart',
    envia: 'send',
    imprimeix: 'print',
    obreNovaFinestra: 'open_in_new',
    mou: 'turn_right',
    avanca: 'fast_forward',
    reprenExecucio: 'play_circle',
    pausaExecucio: 'pause',
    desaDefinitiu: 'edit_note',
    afegeixUsuari: 'person_add',
    bloqueja: 'lock',
    desbloqueja: 'lock_open_right',
    mostra: 'visibility',
    amunt: 'arrow_upward',
    avall: 'arrow_downward',
    anterior: 'keyboard_double_arrow_left',
    seguent: 'keyboard_double_arrow_right',
    plega: 'expand_less',
    desplega: 'expand_more',
    mesAccions: 'more_vert',
    buida: 'clear',
    interficieClassica: 'fast_rewind',

    // --- Estats i avisos -----------------------------------------------------------------------
    advertencia: 'warning',
    error: 'error',
    problema: 'report_problem',
    correcte: 'check_circle',
    incorrecte: 'cancel',
    programat: 'schedule',
    senseDades: 'block',
    favorit: 'star',
    alertes: 'note_stack',
    historial: 'history',
    comentaris: 'forum',
    documentacioFisica: 'inventory_2',
    documentacioElectronica: 'code_xml',

    // --- Objectes i vistes ---------------------------------------------------------------------
    correu: 'mail',
    rol: 'badge',
    grup: 'group',
    taula: 'table',
    llistaPunts: 'format_list_bulleted',
    detallAnotacio: 'adjust',

    // --- Filtre i cerca ------------------------------------------------------------------------
    cerca: 'search',
    filtre: 'filter_alt',
    neteja: 'filter_alt_off',
    cercaAvancadaObre: 'expand_more',
    cercaAvancadaTanca: 'expand_less',

    // --- Selecció (accions massives) -----------------------------------------------------------
    seleccionaTot: 'check_box',
    deseleccionaTot: 'check_box_outline_blank',
    opcioMarcada: 'check_box',
    opcioDesmarcada: 'check_box_outline_blank',
} as const;

export type IconName = (typeof icons)[keyof typeof icons];

/**
 * Icona d'una pantalla: una de les claus comunes o qualsevol altre nom de Material Icons (les icones
 * de les pantalles pròpies es declaren a l'aplicació).
 */
export type ScreenIconName = IconName | (string & {});
