import React from 'react';
import { useTranslation } from 'react-i18next';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Icon from '@mui/material/Icon';
import Tooltip from '@mui/material/Tooltip';
import type { GridRenderCellParams, GridRowOrderChangeParams } from '@mui/x-data-grid-pro';
import {
    FormField,
    MuiDataGrid,
    type MuiDataGridColDef,
    useBaseAppContext,
    useFormContext,
    useMuiDataGridApiRef,
    useResourceApiService,
} from 'reactlib';
import { BACKOFFICE_AUTENTICACIO, RUTA_DESTI_TIPUS } from './serveiModel';
import { useOpcionsEnum } from './ServeiDadesForm';
import { missatgeError } from '../../util/missatgeError';

/** Nom del recurs de l'API (ServeiRutaApiController). */
export const RECURS_RUTA = 'serveiRuta';
const ACCIO_MOURE = 'MOURE';
const ACCIO_PROVAR = 'PROVAR';

/** Fila del recurs `serveiRuta` (`ServeiRutaResource`). */
type Ruta = {
    id: number;
    serveiId: number;
    ordre?: number;
    entitatCodi: string;
    url: string;
    destiTipus: 'EMISERV' | 'BACKOFFICE';
    backofficeAutenticacio?: string;
    credencialsPropietats?: string;
    credencialsConfigurades?: boolean | null;
    pausada?: boolean;
};

type ProvaResultat = {
    correcta: boolean;
    url?: string;
    codiHttp?: number;
    missatge?: string;
    tempsMs: number;
    credencialsPendents: boolean;
};

/** Formulari d'una ruta (fila d'edició de `serveiRutes.jsp`). */
const ServeiRutaForm: React.FC = () => {
    const { t } = useTranslation();
    const { data } = useFormContext();
    const opcionsDesti = useOpcionsEnum(RUTA_DESTI_TIPUS, 'serveis.enum.destiTipus');
    const opcionsAutenticacio = useOpcionsEnum(BACKOFFICE_AUTENTICACIO, 'serveis.enum.autenticacio');
    return (
        <Grid container spacing={2} sx={{ pt: 1 }}>
            <Grid size={{ xs: 12, md: 4 }}>
                <FormField name="entitatCodi" label={t('serveis.rutes.entitatCodi')} required />
            </Grid>
            <Grid size={{ xs: 12, md: 8 }}>
                <FormField name="destiTipus" label={t('serveis.rutes.destiTipus')} required options={opcionsDesti} />
            </Grid>
            <Grid size={12}>
                <FormField name="url" label={t('serveis.rutes.url')} required />
            </Grid>
            {data?.destiTipus === 'BACKOFFICE' && (
                <Grid size={12}>
                    <FormField
                        name="backofficeAutenticacio"
                        label={t('serveis.rutes.autenticacio')}
                        options={opcionsAutenticacio}
                    />
                </Grid>
            )}
            {data?.credencialsPropietats && (
                <Grid size={12}>
                    <Alert severity={data?.credencialsConfigurades ? 'success' : 'warning'}>
                        {t(
                            data?.credencialsConfigurades
                                ? 'serveis.rutes.credencialsConfigurades'
                                : 'serveis.rutes.credencialsPendents',
                            {
                                propietats: data.credencialsPropietats + 'username/password',
                            }
                        )}
                    </Alert>
                </Grid>
            )}
        </Grid>
    );
};

/** Text de l'autenticació d'una ruta, amb l'estat de les credencials (com la JSP). */
const AutenticacioRuta: React.FC<{ ruta: Ruta }> = ({ ruta }) => {
    const { t } = useTranslation();
    if (ruta.destiTipus !== 'BACKOFFICE' || ruta.backofficeAutenticacio == null) {
        return null;
    }
    const text = t(`serveis.enum.autenticacio.${ruta.backofficeAutenticacio}`);
    if (ruta.credencialsConfigurades == null) {
        return <>{text}</>;
    }
    const propietats = (ruta.credencialsPropietats ?? '') + 'username/password';
    return (
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, height: '100%' }}>
            {text}
            <Tooltip
                title={t(
                    ruta.credencialsConfigurades
                        ? 'serveis.rutes.credencialsConfigurades'
                        : 'serveis.rutes.credencialsPendents',
                    { propietats }
                )}
            >
                <Icon fontSize="small" color={ruta.credencialsConfigurades ? 'success' : 'error'}>
                    {ruta.credencialsConfigurades ? 'check' : 'warning'}
                </Icon>
            </Tooltip>
        </Box>
    );
};

/**
 * Pestanya «Rutes» d'un servei enrutador (`serveiRutes.jsp`): alta, modificació i esborrat en una
 * finestra, reordenació arrossegant les files (l'ordre decideix la ruta quan n'hi ha més d'una per a
 * una entitat) i prova de connexió de cada ruta.
 */
export const ServeiRutes: React.FC<{ serveiId: number }> = ({ serveiId }) => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const { isReady, artifactAction } = useResourceApiService(RECURS_RUTA);
    const apiRef = useMuiDataGridApiRef();
    const refresh = () => apiRef.current?.refresh?.();
    const [nombreRutes, setNombreRutes] = React.useState(0);

    const provar = (id: unknown) => {
        if (!isReady) {
            return;
        }
        temporalMessageShow(null, t('serveis.rutes.provant'), 'info');
        artifactAction(id, { code: ACCIO_PROVAR })
            .then((resultat: ProvaResultat) => {
                const detall = [
                    resultat.url,
                    resultat.codiHttp != null ? 'HTTP ' + resultat.codiHttp : null,
                    resultat.tempsMs + ' ms',
                    resultat.missatge,
                ]
                    .filter((d) => d)
                    .join(' · ');
                if (!resultat.correcta) {
                    temporalMessageShow(t('serveis.rutes.provaError'), detall, 'error');
                } else if (resultat.credencialsPendents) {
                    temporalMessageShow(t('serveis.rutes.provaCredencials'), detall, 'warning');
                } else {
                    temporalMessageShow(t('serveis.rutes.provaOk'), detall, 'success');
                }
            })
            .catch((error: unknown) =>
                temporalMessageShow(t('serveis.rutes.provaNoFeta'), missatgeError(error), 'error')
            );
    };

    /** Canvia la posició d'una ruta (començant per 0) i torna a llegir l'ordre del servidor. */
    const moureA = (id: unknown, posicio: number) => {
        if (!isReady) {
            return;
        }
        artifactAction(id, { code: ACCIO_MOURE, data: { posicio } })
            .catch((error: unknown) =>
                temporalMessageShow(t('serveis.rutes.moureError'), missatgeError(error), 'error')
            )
            // També si ha fallat: la graella ha de reflectir l'ordre real
            .finally(refresh);
    };

    const moure = (params: GridRowOrderChangeParams) => {
        if (params.oldIndex !== params.targetIndex) {
            moureA(params.row.id, params.targetIndex);
        }
    };

    const columns = React.useMemo<MuiDataGridColDef[]>(
        () => [
            {
                field: 'entitatCodi',
                headerName: t('serveis.rutes.entitatCodi'),
                flex: 1,
                minWidth: 110,
                sortable: false,
            },
            {
                field: 'destiTipus',
                headerName: t('serveis.rutes.destiTipus'),
                flex: 1.2,
                minWidth: 150,
                sortable: false,
                renderCell: (params: GridRenderCellParams<Ruta>) =>
                    t(`serveis.enum.destiTipus.${params.row.destiTipus}`),
            },
            { field: 'url', headerName: t('serveis.rutes.url'), flex: 3, minWidth: 220, sortable: false },
            {
                field: 'backofficeAutenticacio',
                headerName: t('serveis.rutes.autenticacio'),
                flex: 1.2,
                minWidth: 150,
                sortable: false,
                renderCell: (params: GridRenderCellParams<Ruta>) => <AutenticacioRuta ruta={params.row} />,
            },
        ],
        [t]
    );

    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%', minHeight: 0 }}>
            <Alert severity="info" sx={{ mb: 1, flexShrink: 0 }}>
                {t('serveis.rutes.ajudaOrdre')}
            </Alert>
            <Box sx={{ flex: 1, minHeight: 0 }}>
                <MuiDataGrid
                    resourceName={RECURS_RUTA}
                    titleDisabled
                    apiRef={apiRef}
                    columns={columns}
                    fixedFilter={`serveiId:${serveiId}`}
                    formAdditionalData={{ serveiId }}
                    toolbarHideQuickFilter
                    toolbarCreateLabel={t('serveis.rutes.nova')}
                    popupEditActive
                    popupEditFormContent={<ServeiRutaForm />}
                    popupEditFormDialogResourceTitle={t('serveis.rutes.recurs')}
                    popupEditFormDialogComponentProps={{ maxWidth: 'md', fullWidth: true }}
                    rowReordering
                    onRowOrderChange={moure}
                    onRowsChange={(rows) => setNombreRutes(rows.length)}
                    rowAdditionalActions={[
                        {
                            label: t('serveis.rutes.provar'),
                            icon: 'cable',
                            onClick: (id: unknown) => provar(id),
                        },
                        // Alternativa a arrossegar (accessible amb el teclat)
                        {
                            label: t('serveis.rutes.amunt'),
                            icon: 'arrow_upward',
                            showInMenu: true,
                            hidden: (row: Ruta) => (row?.ordre ?? 0) <= 0,
                            onClick: (id: unknown, row: Ruta) => moureA(id, (row.ordre ?? 0) - 1),
                        },
                        {
                            label: t('serveis.rutes.avall'),
                            icon: 'arrow_downward',
                            showInMenu: true,
                            hidden: (row: Ruta) => (row?.ordre ?? 0) >= nombreRutes - 1,
                            onClick: (id: unknown, row: Ruta) => moureA(id, (row.ordre ?? 0) + 1),
                        },
                    ]}
                />
            </Box>
        </Box>
    );
};

export default ServeiRutes;
