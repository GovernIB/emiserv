import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import Tooltip from '@mui/material/Tooltip';
import { useTheme } from '@mui/material/styles';
import type { GridRenderCellParams } from '@mui/x-data-grid-pro';
import {
    MuiDataGrid,
    type MuiDataGridColDef,
    type MuiDataGridProps,
    useBaseAppContext,
    useConfirmDialogButtons,
    useMuiDataGridApiRef,
    useMuiDataGridContext,
    useResourceApiService,
} from 'reactlib';
import { icons, lot2ScreenGridProps, ScreenFrame, ScreenTitle } from '../../lot2';
import { emiservIcons } from '../../components/EmiservBaseApp';
import { useEsAdmin } from '../../components/EmiservAuthProvider';
import { descarregarFitxer } from '../../util/descarregarFitxer';
import DiagnosticAvis from './DiagnosticAvis';
import ServeisFiltre from './ServeisFiltre';
import { rutaFitxaServei, type Servei } from './serveiModel';

/** Nom del recurs de l'API (rel de l'índex, veure ServeiApiController). */
const RECURS = 'servei';

/** Codis dels artefactes de ServeiResource. */
const ACCIO_ACTIVAR = 'ACTIVAR';
const ACCIO_DESACTIVAR = 'DESACTIVAR';
const ACCIO_ESBORRAR = 'ESBORRAR';
const INFORME_EXPORTAR = 'EXPORTAR';

type FormatExportacio = 'CSV' | 'XLSX';

type ErrorApi = { description?: string; message?: string };

/** Missatge d'un error de l'API: el del servidor (409, 422...) si n'hi ha, si no el genèric. */
const missatgeError = (error: unknown): string => {
    const e = error as ErrorApi | undefined;
    return e?.description ?? e?.message ?? String(error);
};

/**
 * Botó d'exportar de la barra de títol: CSV o Excel amb el filtre ràpid, el filtre plegable i
 * l'ordenació actuals de la graella (els `findArgs` del context de la graella). Fa servir l'informe
 * `EXPORTAR` del recurs amb el paràmetre `fileType`; si se supera el màxim de files, el servidor torna
 * un 422 amb el missatge traduït, que es mostra tal qual.
 */
const ServeisExportar: React.FC = () => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const { findArgs } = useMuiDataGridContext();
    const { isReady, artifactReport } = useResourceApiService(RECURS);
    const [anchor, setAnchor] = React.useState<HTMLElement | null>(null);
    const [exportant, setExportant] = React.useState(false);
    const exportar = (fileType: FormatExportacio) => {
        setAnchor(null);
        setExportant(true);
        artifactReport(null, {
            code: INFORME_EXPORTAR,
            fileType,
            data: {
                quickFilter: findArgs?.quickFilter,
                filter: findArgs?.filter,
                sort: findArgs?.sorts,
            },
        })
            .then((resposta) => {
                const { blob, fileName } = resposta as { blob: Blob; fileName?: string };
                descarregarFitxer(blob, fileName);
            })
            .catch((error: unknown) => temporalMessageShow(t('serveis.exportar.error'), missatgeError(error), 'error'))
            .finally(() => setExportant(false));
    };
    return (
        <>
            <Tooltip title={t('serveis.exportar.boto')}>
                <span>
                    <IconButton
                        aria-label={t('serveis.exportar.boto')}
                        aria-haspopup="menu"
                        disabled={!isReady || exportant}
                        onClick={(event) => setAnchor(event.currentTarget)}
                    >
                        <Icon>{icons.descarrega}</Icon>
                    </IconButton>
                </span>
            </Tooltip>
            <Menu anchorEl={anchor} open={anchor != null} onClose={() => setAnchor(null)}>
                <MenuItem onClick={() => exportar('CSV')}>
                    <ListItemIcon>
                        <Icon fontSize="small">description</Icon>
                    </ListItemIcon>
                    <ListItemText>{t('serveis.exportar.csv')}</ListItemText>
                </MenuItem>
                <MenuItem onClick={() => exportar('XLSX')}>
                    <ListItemIcon>
                        <Icon fontSize="small">table_chart</Icon>
                    </ListItemIcon>
                    <ListItemText>{t('serveis.exportar.xlsx')}</ListItemText>
                </MenuItem>
            </Menu>
        </>
    );
};

/** Botó de la barra de títol que obre i tanca el filtre plegable. */
const FiltreBoto: React.FC<{ obert: boolean; onClick: () => void }> = ({ obert, onClick }) => {
    const { t } = useTranslation();
    const text = obert ? t('serveis.filtre.amaga') : t('serveis.filtre.mostra');
    return (
        <Tooltip title={text}>
            <IconButton aria-label={text} aria-expanded={obert} onClick={onClick}>
                <Icon>{obert ? icons.cercaAvancadaTanca : icons.filtre}</Icon>
            </IconButton>
        </Tooltip>
    );
};

/** Xip amb un comptador (permisos, rutes). */
const XipComptador: React.FC<{ icona: string; valor?: number; title: string }> = ({ icona, valor, title }) => (
    <Tooltip title={title}>
        <Chip
            size="small"
            variant="outlined"
            icon={<Icon fontSize="small">{icona}</Icon>}
            label={valor ?? 0}
            aria-label={title}
        />
    </Tooltip>
);

/**
 * Accions de cada fila, només per a administradors: activar o desactivar (segons l'estat) i esborrar
 * amb confirmació. Totes són accions del recurs (`ACTIVAR`, `DESACTIVAR`, `ESBORRAR`): la graella
 * només les mostra si el servidor publica l'artefacte per a l'usuari, i el servidor torna a comprovar
 * el rol en executar-les. Els errors de negoci (409, p. ex. esborrar un servei amb dependències) es
 * mostren amb el missatge del servidor.
 */
const useAccionsFila = (
    esAdmin: boolean,
    refresh: () => void
): NonNullable<MuiDataGridProps['rowAdditionalActions']> => {
    const { t } = useTranslation();
    const { temporalMessageShow, messageDialogShow } = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const { isReady, artifactAction } = useResourceApiService(RECURS);
    if (!esAdmin) {
        return [];
    }
    const executar = (id: unknown, code: string, clauOk: string) => {
        if (!isReady) {
            return;
        }
        artifactAction(id, { code })
            .then(() => {
                refresh();
                temporalMessageShow(null, t(clauOk), 'success');
            })
            .catch((error: unknown) => temporalMessageShow(t('serveis.accio.error'), missatgeError(error), 'error'));
    };
    const esborrar = (id: unknown) => {
        messageDialogShow(
            t('serveis.accio.confirmacioEsborrarTitol'),
            t('serveis.accio.confirmacioEsborrar'),
            confirmDialogButtons,
            { maxWidth: 'sm', fullWidth: true }
        )
            .then((confirmat: unknown) => {
                if (confirmat === true) {
                    executar(id, ACCIO_ESBORRAR, 'serveis.accio.esborrarOk');
                }
            })
            // Cancel·lar o tancar el diàleg rebutja la promesa: no s'esborra res.
            .catch(() => undefined);
    };
    return [
        {
            label: t('serveis.accio.activar'),
            icon: icons.activa,
            showInMenu: true,
            action: ACCIO_ACTIVAR,
            hidden: (row: Servei) => row?.actiu,
            onClick: (id: unknown) => executar(id, ACCIO_ACTIVAR, 'serveis.accio.activarOk'),
        },
        {
            label: t('serveis.accio.desactivar'),
            icon: icons.desactiva,
            showInMenu: true,
            action: ACCIO_DESACTIVAR,
            hidden: (row: Servei) => !row?.actiu,
            onClick: (id: unknown) => executar(id, ACCIO_DESACTIVAR, 'serveis.accio.desactivarOk'),
        },
        {
            label: t('serveis.accio.esborrar'),
            icon: icons.esborra,
            showInMenu: true,
            action: ACCIO_ESBORRAR,
            onClick: (id: unknown) => esborrar(id),
        },
    ];
};

/**
 * Llistat de serveis (spec §5.1, rèplica de `serveiList.jsp`).
 *
 * - Columnes: codi (amb l'avís de diagnòstic), nom i tipus; només per a administradors, també actiu,
 *   permisos i rutes. El recurs torna aquests valors també als responsables: s'amaguen a la interfície.
 * - Filtre ràpid (codi/nom) a la barra de títol i filtre plegable (`ServeisFiltre`), desat per usuari.
 * - Clicar una fila obre la fitxa del servei (`/serveis/:id`). Fins a la fase 2 és una fitxa de
 *   només lectura; per això no hi ha botó de crear (el recurs no permet la creació genèrica).
 */
export const ServeisLlistat: React.FC = () => {
    const { t } = useTranslation();
    const theme = useTheme();
    const navigate = useNavigate();
    const esAdmin = useEsAdmin();
    const apiRef = useMuiDataGridApiRef();
    const [filtreObert, setFiltreObert] = React.useState(false);
    const refresh = React.useCallback(() => apiRef.current?.refresh?.(), [apiRef]);
    const accions = useAccionsFila(esAdmin, refresh);

    const columns = React.useMemo<MuiDataGridColDef[]>(() => {
        const comunes: MuiDataGridColDef[] = [
            {
                field: 'codi',
                headerName: t('serveis.columna.codi'),
                flex: 1,
                minWidth: 160,
                renderCell: (params: GridRenderCellParams<Servei>) => (
                    <Box sx={{ display: 'flex', alignItems: 'center', height: '100%', minWidth: 0 }}>
                        <Box component="span" sx={{ overflow: 'hidden', textOverflow: 'ellipsis' }}>
                            {params.row.codi}
                        </Box>
                        <DiagnosticAvis id={params.row.id} motius={params.row.diagnostic} />
                    </Box>
                ),
            },
            { field: 'nom', headerName: t('serveis.columna.nom'), flex: 3, minWidth: 200 },
            {
                field: 'tipus',
                headerName: t('serveis.columna.tipus'),
                flex: 1,
                minWidth: 140,
                renderCell: (params: GridRenderCellParams<Servei>) =>
                    params.row.tipus != null ? t(`serveis.tipus.${params.row.tipus}`) : '',
            },
        ];
        if (!esAdmin) {
            return comunes;
        }
        return [
            ...comunes,
            {
                field: 'actiu',
                headerName: t('serveis.columna.actiu'),
                width: 110,
                renderCell: (params: GridRenderCellParams<Servei>) => (
                    <Chip
                        size="small"
                        color={params.row.actiu ? 'success' : 'default'}
                        label={params.row.actiu ? t('serveis.estat.actiu') : t('serveis.estat.inactiu')}
                    />
                ),
            },
            {
                field: 'permisosCount',
                headerName: t('serveis.columna.permisos'),
                width: 110,
                align: 'center',
                headerAlign: 'center',
                renderCell: (params: GridRenderCellParams<Servei>) => (
                    <XipComptador
                        icona="key"
                        valor={params.row.permisosCount}
                        title={t('serveis.permisos.titol', { count: params.row.permisosCount ?? 0 })}
                    />
                ),
            },
            {
                field: 'rutesCount',
                headerName: t('serveis.columna.rutes'),
                width: 100,
                align: 'center',
                headerAlign: 'center',
                // Els serveis backoffice no tenen rutes: només es mostra el xip als enrutadors.
                renderCell: (params: GridRenderCellParams<Servei>) =>
                    params.row.tipus !== 'BACKOFFICE' && (
                        <XipComptador
                            icona={emiservIcons.auditoriaEnrutador}
                            valor={params.row.rutesCount}
                            title={t('serveis.rutes.titol', { count: params.row.rutesCount ?? 0 })}
                        />
                    ),
            },
        ];
    }, [t, esAdmin]);

    const toolbarElements = [
        {
            position: 2,
            element: <FiltreBoto obert={filtreObert} onClick={() => setFiltreObert((obert) => !obert)} />,
        },
        ...(esAdmin ? [{ position: 2, element: <ServeisExportar /> }] : []),
    ];

    return (
        <ScreenFrame>
            <MuiDataGrid
                title={<ScreenTitle icon={emiservIcons.servei} title={t('serveis.titol')} />}
                {...lot2ScreenGridProps(theme)}
                resourceName={RECURS}
                apiRef={apiRef}
                columns={columns}
                readOnly
                toolbarHideCreate
                paginationActive
                persistentStateActive
                persistentStateKey="serveis"
                toolbarElementsWithPositions={toolbarElements}
                toolbarAdditionalRow={<ServeisFiltre obert={filtreObert} />}
                rowAdditionalActions={accions}
                onRowClick={(params) => navigate(rutaFitxaServei(params.id as number))}
            />
        </ScreenFrame>
    );
};

export default ServeisLlistat;
