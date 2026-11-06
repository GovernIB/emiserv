import React from 'react';
import type { TFunction } from 'i18next';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { FormField, type MuiDataGridColDef, useFormContext } from 'reactlib';
import { formatDataHora } from '../../util/dates';
import SincronitzarEntitats from './SincronitzarEntitats';

/** Columna de la graella: camp del recurs; el títol és la traducció `configuracio.<clau>.camp.<camp>`. */
type Columna = { field: string; flex?: number; tipus?: 'data' | 'boolean' };

/** Camp del formulari emergent. */
type Camp = {
    name: string;
    /** Amplada a pantalla mitjana (de 12). Per defecte, 6. */
    md?: number;
    /** No es pot modificar un cop creat (l'identificador). */
    nomesAlta?: boolean;
    /** Sempre deshabilitat (informatiu). */
    nomesLectura?: boolean;
    password?: boolean;
    required?: boolean;
    /** Text d'ajuda (clau `configuracio.<clau>.ajuda.<camp>`). */
    ajuda?: boolean;
};

export type Manteniment = {
    /** Clau de les traduccions (`configuracio.<clau>.*`) i de la ruta (`/configuracio/<clau>`). */
    clau: string;
    /** Nom del recurs de l'API. */
    recurs: string;
    icona: string;
    columnesDef: Columna[];
    camps: Camp[];
    /** Camps del filtre plegable (els del filtre de la JSP). */
    filtre?: string[];
    /** Filtre ràpid a la barra de títol. */
    filtreRapid?: boolean;
    /** Sense alta (només modificació). */
    senseAlta?: boolean;
    /** Selecció de files amb caselles i esborrat dels seleccionats (endpoint `bulk` del recurs). */
    esborratMultiple?: boolean;
    /** Botons addicionals de la barra de títol. */
    accionsBarra?: (refrescar: () => void) => React.ReactNode;
    columnes: (t: TFunction) => MuiDataGridColDef[];
    formulari: React.FC;
};

/** Data sense hora (dd/mm/aaaa). */
const formatData = (valor?: string | null) => formatDataHora(valor).slice(0, 10);

const crearColumnes =
    (clau: string, columnes: Columna[]) =>
    (t: TFunction): MuiDataGridColDef[] =>
        columnes.map(({ field, flex, tipus }) => ({
            field,
            headerName: t(`configuracio.${clau}.camp.${field}`),
            flex: flex ?? 1,
            minWidth: tipus ? 110 : 140,
            ...(tipus === 'data' ? { valueFormatter: (valor: string) => formatData(valor) } : {}),
            ...(tipus === 'boolean' ? { type: 'boolean' } : {}),
        }));

const crearFormulari = (clau: string, camps: Camp[]): React.FC => {
    const Formulari: React.FC = () => {
        const { t } = useTranslation();
        const { id } = useFormContext();
        return (
            <Grid container spacing={2} sx={{ pt: 1 }}>
                {camps.map((camp) => (
                    <Grid key={camp.name} size={{ xs: 12, md: camp.md ?? 6 }}>
                        <FormField
                            name={camp.name}
                            label={t(`configuracio.${clau}.camp.${camp.name}`)}
                            required={camp.required}
                            disabled={camp.nomesLectura || (camp.nomesAlta && id != null)}
                            {...(camp.ajuda ? { helperText: t(`configuracio.${clau}.ajuda.${camp.name}`) } : {})}
                            {...((camp.password ? { password: true } : {}) as Record<string, unknown>)}
                        />
                    </Grid>
                ))}
            </Grid>
        );
    };
    Formulari.displayName = `Formulari_${clau}`;
    return Formulari;
};

const manteniment = (m: Omit<Manteniment, 'columnes' | 'formulari'>): Manteniment => ({
    ...m,
    columnes: crearColumnes(m.clau, m.columnesDef),
    formulari: crearFormulari(m.clau, m.camps),
});

const DATES: Columna[] = [
    { field: 'dataAlta', tipus: 'data' },
    { field: 'dataBaixa', tipus: 'data' },
];
const CAMPS_DATES: Camp[] = [{ name: 'dataAlta' }, { name: 'dataBaixa' }];

/** Manteniments de l'apartat de configuració, en l'ordre del menú de la JSP. */
export const MANTENIMENTS: Manteniment[] = [
    manteniment({
        clau: 'aplicacio',
        recurs: 'aplicacio',
        icona: 'apps',
        columnesDef: [
            { field: 'certificatNif' },
            { field: 'cn', flex: 3 },
            { field: 'autoritatCertificacioNom', flex: 2 },
        ],
        camps: [
            { name: 'certificatNif', required: true },
            { name: 'numeroSerie', required: true },
            { name: 'cn', md: 12, required: true },
            { name: 'autoritatCertificacioId', md: 12, required: true },
            ...CAMPS_DATES,
        ],
        filtreRapid: true,
    }),
    manteniment({
        clau: 'emisor',
        recurs: 'emisor',
        icona: 'outbox',
        columnesDef: [{ field: 'cif' }, { field: 'nom', flex: 3 }, ...DATES],
        camps: [{ name: 'cif', required: true }, { name: 'nom', required: true }, ...CAMPS_DATES],
        filtre: ['cif', 'nom'],
    }),
    manteniment({
        clau: 'organisme',
        recurs: 'organisme',
        icona: 'account_balance',
        columnesDef: [{ field: 'cif' }, { field: 'nom', flex: 3 }, ...DATES],
        camps: [{ name: 'cif', required: true }, { name: 'nom', required: true }, ...CAMPS_DATES],
        filtre: ['cif', 'nom'],
        esborratMultiple: true,
    }),
    manteniment({
        clau: 'organismeCessionari',
        recurs: 'organismeCessionari',
        icona: 'corporate_fare',
        columnesDef: [
            { field: 'cif' },
            { field: 'nom', flex: 3 },
            ...DATES,
            { field: 'codiUnitatTramitadora' },
            { field: 'bloquejat', tipus: 'boolean' },
        ],
        camps: [
            { name: 'cif', required: true },
            { name: 'nom', required: true },
            ...CAMPS_DATES,
            { name: 'codiUnitatTramitadora' },
            { name: 'bloquejat' },
        ],
        filtre: ['cif', 'nom'],
        esborratMultiple: true,
    }),
    manteniment({
        clau: 'clauPublica',
        recurs: 'clauPublica',
        icona: 'key',
        columnesDef: [{ field: 'alies' }, { field: 'nom', flex: 2 }, { field: 'numSerie', flex: 2 }, ...DATES],
        camps: [
            { name: 'alies', required: true },
            { name: 'nom', required: true },
            { name: 'numSerie', md: 12, required: true },
            ...CAMPS_DATES,
        ],
    }),
    manteniment({
        clau: 'clauPrivada',
        recurs: 'clauPrivada',
        icona: 'vpn_key',
        columnesDef: [{ field: 'alies' }, { field: 'nom', flex: 2 }, { field: 'numSerie', flex: 2 }, ...DATES],
        camps: [
            { name: 'alies', required: true },
            { name: 'nom', required: true },
            { name: 'password', password: true, ajuda: true },
            { name: 'numSerie', required: true },
            { name: 'organisme', md: 12, required: true },
            ...CAMPS_DATES,
            { name: 'interoperabilitat', md: 12 },
        ],
    }),
    manteniment({
        clau: 'autoritatCertificacio',
        recurs: 'autoritatCertificacio',
        icona: 'verified_user',
        columnesDef: [
            { field: 'nombre', flex: 3 },
            { field: 'codca', flex: 2 },
        ],
        camps: [
            { name: 'nombre', md: 12, required: true },
            { name: 'codca', md: 12, required: true },
        ],
        filtre: ['nombre', 'codca'],
    }),
    manteniment({
        clau: 'scspModul',
        recurs: 'scspModul',
        icona: 'extension',
        columnesDef: [
            { field: 'nom', flex: 2 },
            { field: 'descripcio', flex: 3 },
            { field: 'actiuEntrada', tipus: 'boolean' },
            { field: 'actiuSortida', tipus: 'boolean' },
        ],
        camps: [
            { name: 'nom', md: 12, nomesLectura: true },
            { name: 'descripcio', md: 12, nomesLectura: true },
            { name: 'actiuEntrada' },
            { name: 'actiuSortida' },
        ],
        senseAlta: true,
    }),
    manteniment({
        clau: 'scspParametre',
        recurs: 'scspParametre',
        icona: 'tune',
        columnesDef: [
            { field: 'nombre', flex: 2 },
            { field: 'descripcion', flex: 3 },
            { field: 'valor', flex: 2 },
        ],
        camps: [
            { name: 'nombre', md: 12, required: true, nomesAlta: true },
            { name: 'valor', md: 12 },
            { name: 'descripcion', md: 12 },
        ],
        filtre: ['nombre', 'descripcion'],
    }),
    manteniment({
        clau: 'entitat',
        recurs: 'entitat',
        icona: 'domain',
        columnesDef: [{ field: 'codi' }, { field: 'nom', flex: 3 }, { field: 'cif' }, { field: 'unitatArrel' }],
        camps: [
            { name: 'codi', required: true },
            { name: 'cif', required: true },
            { name: 'nom', md: 12, required: true },
            { name: 'unitatArrel' },
        ],
        filtre: ['codi', 'nom', 'cif', 'unitatArrel'],
        accionsBarra: (refrescar) => <SincronitzarEntitats refrescar={refrescar} />,
    }),
];

/** Manteniment per la clau de la ruta. */
export const mantenimentPerClau = (clau?: string) => MANTENIMENTS.find((m) => m.clau === clau);
