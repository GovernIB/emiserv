/** Tipus d'un servei (`ServeiTipusEnumDto`). */
export const SERVEI_TIPUS = ['BACKOFFICE', 'ENRUTADOR', 'ENRUTADOR_MULTIPLE'] as const;
export type ServeiTipus = (typeof SERVEI_TIPUS)[number];

/** Motius del diagnòstic de configuració d'un servei (`ServeiDiagnosticEnumDto`). */
export type MotiuDiagnostic =
    'FALTA_CONFIG_SCSP' | 'FALTA_EMISOR' | 'ENRUTADOR_SENSE_RUTES' | 'RUTA_BACKOFFICE_SENSE_CREDENCIALS';

/** Pestanyes de la fitxa del servei a les quals pot dur l'avís de diagnòstic. */
export type PestanyaFitxa = 'configScsp' | 'rutes';

/** Pestanya de la fitxa on es corregeix cada motiu del diagnòstic (spec §5.1). */
export const PESTANYA_PER_MOTIU: Record<MotiuDiagnostic, PestanyaFitxa> = {
    FALTA_CONFIG_SCSP: 'configScsp',
    FALTA_EMISOR: 'configScsp',
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

/** Ruta de la fitxa d'un servei, opcionalment oberta a una pestanya. */
export const rutaFitxaServei = (id: number | string, pestanya?: PestanyaFitxa): string =>
    `/serveis/${id}` + (pestanya != null ? `?pestanya=${pestanya}` : '');

/**
 * Ruta de la fitxa per a un diagnòstic: la pestanya del primer motiu (els motius arriben en l'ordre en
 * què el servidor els comprova). Sense motius, la fitxa sense pestanya.
 */
export const rutaFitxaDiagnostic = (id: number | string, motius?: MotiuDiagnostic[]): string => {
    const primer = motius?.[0];
    return rutaFitxaServei(id, primer != null ? PESTANYA_PER_MOTIU[primer] : undefined);
};
