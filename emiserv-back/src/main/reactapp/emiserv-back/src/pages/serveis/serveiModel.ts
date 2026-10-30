/** Tipus d'un servei (`ServeiTipusEnumDto`). */
export const SERVEI_TIPUS = ['BACKOFFICE', 'ENRUTADOR', 'ENRUTADOR_MULTIPLE'] as const;
export type ServeiTipus = (typeof SERVEI_TIPUS)[number];

/** Autenticació amb un backoffice (`BackofficeAutenticacioTipusEnumDto`). */
export const BACKOFFICE_AUTENTICACIO = ['NINGUNA', 'TOKEN_CAIB', 'TEXT_CLAR'] as const;
/** Tractament de les peticions asíncrones d'un backoffice (`BackofficeAsyncTipusEnumDto`). */
export const BACKOFFICE_ASYNC_TIPUS = ['REENVIAR_BACKOFFICE', 'PROCESSAR_SINCRONES'] as const;
/** Destí d'una ruta (`ServeiRutaDestiTipusEnumDto`). */
export const RUTA_DESTI_TIPUS = ['EMISERV', 'BACKOFFICE'] as const;
/** Tipus de principal d'un permís (`PrincipalTipusEnumDto`). */
export const PRINCIPAL_TIPUS = ['USUARI', 'ROL', 'GRUP'] as const;

/** Motius del diagnòstic de configuració d'un servei (`ServeiDiagnosticEnumDto`). */
export type MotiuDiagnostic =
    'FALTA_CONFIG_SCSP' | 'FALTA_EMISOR' | 'ENRUTADOR_SENSE_RUTES' | 'RUTA_BACKOFFICE_SENSE_CREDENCIALS';

/** Pestanyes de la finestra d'un servei. */
export const PESTANYES_SERVEI = ['dades', 'rutes', 'permisos', 'autoritzacions'] as const;
export type PestanyaServei = (typeof PESTANYES_SERVEI)[number];

/**
 * Pestanya on es corregeix cada motiu del diagnòstic. La configuració SCSP encara es fa a la
 * interfície clàssica: la pestanya de dades ho indica.
 */
export const PESTANYA_PER_MOTIU: Record<MotiuDiagnostic, PestanyaServei> = {
    FALTA_CONFIG_SCSP: 'dades',
    FALTA_EMISOR: 'dades',
    ENRUTADOR_SENSE_RUTES: 'rutes',
    RUTA_BACKOFFICE_SENSE_CREDENCIALS: 'rutes',
};

/** Fila del recurs `servei` (`ServeiResource`). */
export type Servei = {
    id: number;
    codi: string;
    nom: string;
    tipus: ServeiTipus;
    actiu: boolean;
    configurat?: boolean;
    configuratScsp?: boolean;
    xsdGestioActiva?: boolean;
    permisosCount?: number;
    rutesCount?: number;
    diagnostic?: MotiuDiagnostic[];
};

/** Els enrutadors (simples o múltiples) tenen rutes; els backoffices, no. */
export const esEnrutador = (tipus?: ServeiTipus | null): boolean =>
    tipus === 'ENRUTADOR' || tipus === 'ENRUTADOR_MULTIPLE';

/** Id del servei nou a la URL de la finestra (`/serveis/nou`). */
export const ID_NOU = 'nou';

/** Ruta de la finestra d'un servei, opcionalment oberta a una pestanya. */
export const rutaServei = (id: number | string, pestanya?: PestanyaServei): string =>
    `/serveis/${id}` + (pestanya != null && pestanya !== 'dades' ? `?pestanya=${pestanya}` : '');

/**
 * Ruta de la finestra per a un diagnòstic: la pestanya del primer motiu (els motius arriben en l'ordre
 * en què el servidor els comprova).
 */
export const rutaServeiDiagnostic = (id: number | string, motius?: MotiuDiagnostic[]): string => {
    const primer = motius?.[0];
    return rutaServei(id, primer != null ? PESTANYA_PER_MOTIU[primer] : undefined);
};
