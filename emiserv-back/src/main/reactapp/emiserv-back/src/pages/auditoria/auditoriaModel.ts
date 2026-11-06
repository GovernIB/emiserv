/** Les dues auditories: peticions de serveis backoffice i peticions redirigides per l'enrutador. */
export type TipusAuditoria = 'backoffice' | 'enrutador';

/** Nom del recurs de l'API (AuditoriaBackofficeApiController i AuditoriaEnrutadorApiController). */
export const RECURS_AUDITORIA: Record<TipusAuditoria, string> = {
    backoffice: 'auditoriaBackoffice',
    enrutador: 'auditoriaEnrutador',
};

/** Ruta del llistat i, amb un id, de la finestra de detall d'una petició. */
export const rutaAuditoria = (tipus: TipusAuditoria, id?: string | number) =>
    `/auditoria/${tipus}` + (id != null ? '/' + encodeURIComponent(String(id)) : '');

/** Estats d'una petició (PeticioEstatEnumDto), en l'ordre de la llegenda. */
export const ESTATS_PETICIO = ['PENDENT', 'EN_PROCES', 'POLLING', 'TRAMITADA', 'ERROR', 'DESCONEGUT'] as const;
export type EstatPeticio = (typeof ESTATS_PETICIO)[number];

/** Color (paleta MUI) i icona de cada estat. */
export const ESTAT_ASPECTE: Record<
    EstatPeticio,
    { color: 'default' | 'info' | 'warning' | 'success' | 'error' | 'secondary'; icona: string }
> = {
    PENDENT: { color: 'default', icona: 'schedule' },
    EN_PROCES: { color: 'info', icona: 'settings' },
    POLLING: { color: 'secondary', icona: 'sync_alt' },
    TRAMITADA: { color: 'success', icona: 'check_circle' },
    ERROR: { color: 'error', icona: 'error' },
    DESCONEGUT: { color: 'warning', icona: 'help' },
};

/** Petició d'auditoria tal com la torna l'API (els camps comuns i els de cada auditoria). */
export type PeticioAuditoria = {
    id: string | number;
    peticioId: string;
    dataPeticio?: string;
    dataResposta?: string;
    procedimentCodi?: string;
    procedimentNom?: string;
    procedimentCodiNom?: string;
    serveiCodi?: string;
    serveiDescripcio?: string;
    serveiId?: number;
    estat?: EstatPeticio;
    error?: string;
    // Backoffice
    estatScsp?: string;
    estatScspError?: boolean;
    sincrona?: boolean;
    ter?: string;
    numEnviaments?: number;
    numTransmissions?: number;
    dataDarreraComprovacio?: string;
    processadesTotal?: number;
    processadesError?: number;
    processadesPercent?: number;
    comunicacioBackofficeDisponible?: boolean;
    comunicacioBackofficeError?: boolean;
    // Enrutador
    serveiTipus?: 'BACKOFFICE' | 'ENRUTADOR' | 'ENRUTADOR_MULTIPLE';
    entitatCodi?: string;
    teRespostes?: boolean;
};

/** Dades del filtre plegable. */
export type DadesFiltreAuditoria = {
    servei?: string | number | null;
    procediment?: string | null;
    numeroPeticio?: string | null;
    estat?: string | null;
    dataInici?: string | null;
    dataFi?: string | null;
};

/** Literal de Spring Filter entre cometes simples, amb les cometes i les barres escapades. */
const literal = (valor: string) => `'${valor.replace(/\\/g, '\\\\').replace(/'/g, "\\'")}'`;

const buit = (valor: unknown) => valor == null || String(valor).trim() === '';

/**
 * Filtre Spring a partir del filtre plegable. El servidor (AuditoriaResourceHelper) només entén
 * `camp:'valor'` units amb `and`, i `dataPeticio>:` / `dataPeticio<:` per a l'interval de dates. El
 * servei és l'id a l'auditoria de backoffice i el codi a la de l'enrutador (com les consultes de la JSP).
 */
export const construirFiltreAuditoria = (tipus: TipusAuditoria, dades?: DadesFiltreAuditoria): string =>
    [
        !buit(dades?.servei)
            ? `${tipus === 'backoffice' ? 'servei' : 'serveiCodi'}:${literal(String(dades!.servei))}`
            : '',
        !buit(dades?.procediment) ? `procediment:${literal(String(dades!.procediment))}` : '',
        !buit(dades?.numeroPeticio) ? `numeroPeticio:${literal(String(dades!.numeroPeticio).trim())}` : '',
        !buit(dades?.estat) ? `estat:${literal(String(dades!.estat))}` : '',
        !buit(dades?.dataInici) ? `dataPeticio>:${literal(String(dades!.dataInici))}` : '',
        !buit(dades?.dataFi) ? `dataPeticio<:${literal(String(dades!.dataFi))}` : '',
    ]
        .filter((terme) => terme !== '')
        .join(' and ');

/** Dreceres de dates del filtre. */
export const DRECERES_DATES = ['avui', 'setDies', 'aquestMes'] as const;
export type DreceraDates = (typeof DRECERES_DATES)[number];

/** Data local en el format dels camps de data del formulari (aaaa-mm-ddT00:00:00). */
const formatData = (data: Date) => {
    const dosDigits = (n: number) => String(n).padStart(2, '0');
    return `${data.getFullYear()}-${dosDigits(data.getMonth() + 1)}-${dosDigits(data.getDate())}T00:00:00`;
};

/** Interval de dates (inclòs) d'una drecera; la data final sempre és avui. */
export const datesDrecera = (drecera: DreceraDates, avui = new Date()): { dataInici: string; dataFi: string } => {
    const inici = new Date(avui.getFullYear(), avui.getMonth(), avui.getDate());
    if (drecera === 'setDies') {
        inici.setDate(inici.getDate() - 6);
    } else if (drecera === 'aquestMes') {
        inici.setDate(1);
    }
    return { dataInici: formatData(inici), dataFi: formatData(avui) };
};

/** Sol·licitud (transmissió) d'una petició, amb els camps de les dues auditories normalitzats. */
export type SolicitudAuditoria = {
    solicitudId: string;
    transmisioId?: string;
    solicitantId?: string;
    solicitantNom?: string;
    titularDocument?: string;
    titularNom?: string;
    funcionariDocument?: string;
    funcionariNom?: string;
    dataGeneracio?: string;
    procediment?: string;
    unitatTramitadora?: string;
    expedient?: string;
    finalitat?: string;
    consentiment?: string;
    estat?: string;
    error?: string;
    // Només backoffice
    backofficeEstat?: string;
    comunicacioBackofficeDisponible?: boolean;
    comunicacioBackofficeError?: boolean;
};

const unir = (...parts: (string | undefined | null)[]) =>
    parts.filter((p) => p != null && p.trim() !== '').join(' ') || undefined;

/**
 * Sol·licitud de l'API: AuditoriaTransmisionDto (backoffice, noms de les taules SCSP) o
 * AuditoriaSolicitudDto (enrutador).
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- dos DTO diferents, amb camps opcionals
type SolicitudApi = Record<string, any>;

export const normalitzarSolicitud = (tipus: TipusAuditoria, s: SolicitudApi): SolicitudAuditoria =>
    tipus === 'backoffice'
        ? {
              solicitudId: s.solicitudId,
              transmisioId: s.transmisionId,
              solicitantId: s.solicitanteId,
              solicitantNom: s.solicitanteNombre,
              titularDocument: s.titularDocumento,
              titularNom: s.titularNombreCompleto ?? unir(s.titularNombre, s.titularApellido1, s.titularApellido2),
              funcionariDocument: s.funcionarioDocumento,
              funcionariNom: s.funcionarioNombre,
              dataGeneracio: s.fechaGeneracion,
              procediment: s.procedimientoCodigoNombre ?? unir(s.procedimientoCodigo, s.procedimientoNombre),
              unitatTramitadora: s.unidadTramitadora,
              expedient: s.expediente,
              finalitat: s.finalidad,
              consentiment: s.consentimiento,
              estat: s.estado,
              error: s.error,
              backofficeEstat: s.backofficeEstat,
              comunicacioBackofficeDisponible: s.comunicacioBackofficeDisponible,
              comunicacioBackofficeError: s.comunicacioBackofficeError,
          }
        : {
              solicitudId: s.solicitudId,
              transmisioId: s.transmisioId,
              solicitantId: s.solicitantId,
              solicitantNom: s.solicitantNom,
              titularDocument: s.titularDocument,
              titularNom: s.titularNomSencer ?? unir(s.titularNom, s.titularLlinatge1, s.titularLlinatge2),
              funcionariDocument: s.funcionariDocument,
              funcionariNom: s.funcionariNom,
              dataGeneracio: s.dataGeneracio,
              procediment: s.procedimentCodiNom ?? unir(s.procedimentCodi, s.procedimentNom),
              unitatTramitadora: s.unitatTramitadora,
              finalitat: s.finalitat,
              consentiment: s.consentiment,
              estat: s.estat,
              error: s.error,
          };
