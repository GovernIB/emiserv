/**
 * Dades de la pantalla de consultes asíncrones (ConsultaAsincronaApiController): les mateixes que
 * consultaAsincronaList.jsp, amb els DTO del servei (ConsultaAsincronaDto, ...SolicitudDto i
 * ...CedentDto). Les dates arriben com a mil·lisegons o com a text ISO.
 */

export type Data = number | string | null | undefined;

export type EstatConsulta = 'PENDENT' | 'TRAMITADA' | 'ERROR';
export type EstatSolicitud = 'PENDENT' | 'ENVIANT' | 'TRAMITADA' | 'ERROR';
export type FiltreEstat = 'EN_CURS' | 'PAUSADA' | 'FINALITZADA';

export type Consulta = {
    id: number;
    peticioId: string;
    serveiCodi: string;
    serveiNom?: string;
    dataPeticio?: Data;
    terData?: Data;
    estat: EstatConsulta;
    pausada: boolean;
    total: number;
    tramitades: number;
    errors: number;
    enviant: number;
    pendents: number;
    /** Sol·licituds pendents amb algun intent fallit (esperant el proper reintent). */
    reintents: number;
    destinsPausats: boolean;
    dataFiPrevista?: Data;
    darrerError?: string;
    finalitzada: boolean;
    processades: number;
    progres: number;
    retardada: boolean;
};

export type Solicitud = {
    id: number;
    solicitudId: string;
    entitatCodi?: string;
    estat: EstatSolicitud;
    intents: number;
    intentsMaxim?: number;
    dataEnviament?: Data;
    dataProperIntent?: Data;
    peticioDestiId?: string;
    errorCodi?: string;
    darrerError?: string;
};

export type Cedent = {
    rutaId?: number;
    serveiCodi: string;
    serveiNom?: string;
    entitatCodi?: string;
    destiTipus?: string;
    pausada: boolean;
    pendents: number;
    enviant: number;
    intervalMs: number;
    concurrencia: number;
    dataFiPrevista?: Data;
};

export type Pagina<T> = {
    numero: number;
    tamany: number;
    total: number;
    elementsTotal: number;
    contingut: T[];
};

export type ServeiFiltre = { id: number; codi: string; nom?: string };

/** Interval de l'actualització automàtica (la JSP en fa cada 15 s). */
export const INTERVAL_ACTUALITZACIO_MS = 10000;

/** Amplada (%) de cada tram de la barra: tramitades, amb error i enviant-se. */
export const trams = (c: Pick<Consulta, 'total' | 'tramitades' | 'errors' | 'enviant'>) => {
    const pct = (n: number) => (c.total > 0 ? (100 * n) / c.total : 0);
    return { tramitades: pct(c.tramitades), errors: pct(c.errors), enviant: pct(c.enviant) };
};

/** S'ha de continuar actualitzant: hi ha alguna consulta visible que no ha acabat. */
export const hiHaEnCurs = (consultes: Pick<Consulta, 'finalitzada'>[] | undefined) =>
    !!consultes?.some((c) => !c.finalitzada);

/**
 * Es pot reintentar ara una sol·licitud (com la JSP): la consulta no ha acabat i la sol·licitud ha
 * fallat definitivament o espera un reintent.
 */
export const solicitudReintentable = (
    consulta: Pick<Consulta, 'finalitzada'>,
    s: Pick<Solicitud, 'estat' | 'dataProperIntent'>
) => !consulta.finalitzada && (s.estat === 'ERROR' || (s.estat === 'PENDENT' && !!s.dataProperIntent));

/** Una sol·licitud està esperant un reintent (ha fallat i encara li queden intents). */
export const esperantReintent = (s: Pick<Solicitud, 'estat' | 'dataProperIntent' | 'intents'>) =>
    s.estat === 'PENDENT' && !!s.dataProperIntent && s.intents > 0;

/** Intents restants d'una sol·licitud pendent (null si no es coneix el màxim o ja ha acabat). */
export const intentsRestants = (s: Pick<Solicitud, 'estat' | 'intents' | 'intentsMaxim'>) =>
    s.intentsMaxim && (s.estat === 'PENDENT' || s.estat === 'ENVIANT') ? Math.max(s.intentsMaxim - s.intents, 0) : null;

export const aDate = (valor: Data): Date | undefined => {
    if (valor == null || valor === '') return undefined;
    const data = new Date(valor);
    return isNaN(data.getTime()) ? undefined : data;
};

const dd = (n: number) => String(n).padStart(2, '0');

/** dd/mm/aaaa hh:mm:ss, com la JSP. */
export const formatData = (valor: Data) => {
    const d = aDate(valor);
    return d
        ? `${dd(d.getDate())}/${dd(d.getMonth() + 1)}/${d.getFullYear()} ${dd(d.getHours())}:${dd(d.getMinutes())}:${dd(d.getSeconds())}`
        : '';
};

/** Temps que falta fins a la data (en minuts o segons), o null si ja ha passat. */
export const tempsFins = (valor: Data, ara: number = Date.now()): { valor: number; unitat: 'min' | 's' } | null => {
    const d = aDate(valor);
    if (!d) return null;
    const segons = Math.round((d.getTime() - ara) / 1000);
    if (segons <= 0) return null;
    return segons >= 60 ? { valor: Math.ceil(segons / 60), unitat: 'min' } : { valor: segons, unitat: 's' };
};
