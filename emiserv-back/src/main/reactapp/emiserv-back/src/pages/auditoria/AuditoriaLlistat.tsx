import React from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Link from '@mui/material/Link';
import Tooltip from '@mui/material/Tooltip';
import { useTheme } from '@mui/material/styles';
import type { GridRenderCellParams } from '@mui/x-data-grid-pro';
import { MuiDataGrid, type MuiDataGridColDef, useMuiDataGridApiRef, useMuiDataGridContext } from 'reactlib';
import { icons, lot2ScreenGridProps, ScreenFrame, ScreenTitle } from '../../lot2';
import { emiservIcons } from '../../components/EmiservBaseApp';
import ExportarBoto from '../../components/ExportarBoto';
import { formatDataHora } from '../../util/dates';
import { rutaServei } from '../serveis/serveiModel';
import AuditoriaDetall, { type NavegacioDetall } from './AuditoriaDetall';
import AuditoriaFiltre from './AuditoriaFiltre';
import { EstatLlegenda, EstatXip } from './EstatPeticio';
import { type PeticioAuditoria, RECURS_AUDITORIA, rutaAuditoria, type TipusAuditoria } from './auditoriaModel';

/** Botó de la barra de títol que obre i tanca el filtre plegable. */
const FiltreBoto: React.FC<{ obert: boolean; onClick: () => void }> = ({ obert, onClick }) => {
    const { t } = useTranslation();
    const text = obert ? t('auditoria.filtre.amaga') : t('auditoria.filtre.mostra');
    return (
        <Tooltip title={text}>
            <IconButton aria-label={text} aria-expanded={obert} onClick={onClick}>
                <Icon>{obert ? icons.cercaAvancadaTanca : icons.filtre}</Icon>
            </IconButton>
        </Tooltip>
    );
};

/** Desa els arguments de la consulta actual de la graella (per a l'anterior i el següent del detall). */
const CapturaFindArgs: React.FC<{ findArgsRef: React.MutableRefObject<Record<string, unknown> | undefined> }> = ({
    findArgsRef,
}) => {
    const { findArgs } = useMuiDataGridContext();
    findArgsRef.current = findArgs;
    return null;
};

/** Estat de la navegació del detall que es passa amb la ruta (posició dins el llistat). */
type EstatRuta = { posicio?: number } | null;

/**
 * Llistat d'una auditoria (spec §5.3, rèplica d'`auditoriaBackofficeList.jsp` i
 * `auditoriaEnrutadorList.jsp`): paginació i ordenació al servidor, filtre ràpid pel número de petició i
 * filtre plegable desat per usuari, estat amb color i llegenda, exportació a CSV o Excel amb el mateix
 * filtre i ordre. Clicar una fila obre el detall de la petició (`/auditoria/<tipus>/<id>`), que permet
 * anar a l'anterior i a la següent del llistat.
 */
export const AuditoriaLlistat: React.FC<{ tipus: TipusAuditoria }> = ({ tipus }) => {
    const { t } = useTranslation();
    const theme = useTheme();
    const navigate = useNavigate();
    const location = useLocation();
    const { id: idParam } = useParams();
    const apiRef = useMuiDataGridApiRef();
    const [filtreObert, setFiltreObert] = React.useState(false);
    const findArgsRef = React.useRef<Record<string, unknown> | undefined>(undefined);
    const rowsRef = React.useRef<PeticioAuditoria[]>([]);
    const recurs = RECURS_AUDITORIA[tipus];

    const obrir = React.useCallback(
        (id: string | number, posicio?: number) => navigate(rutaAuditoria(tipus, id), { state: { posicio } }),
        [navigate, tipus]
    );
    const obrirFila = (id: string | number) => {
        const index = rowsRef.current.findIndex((r) => r.id === id);
        const { page = 0, size = 0 } = (findArgsRef.current ?? {}) as { page?: number; size?: number };
        obrir(id, index >= 0 ? page * size + index : undefined);
    };

    const columns = React.useMemo<MuiDataGridColDef[]>(() => {
        const columnes: MuiDataGridColDef[] = [
            {
                field: 'dataPeticio',
                headerName: t('auditoria.columna.data'),
                width: 170,
                valueFormatter: (valor: string) => formatDataHora(valor),
            },
            { field: 'peticioId', headerName: t('auditoria.columna.peticioId'), flex: 1.2, minWidth: 200 },
            { field: 'procedimentCodiNom', headerName: t('auditoria.columna.procediment'), flex: 1.5, minWidth: 180 },
            {
                field: 'serveiCodi',
                headerName: t('auditoria.columna.servei'),
                flex: 1,
                minWidth: 150,
                renderCell: (params: GridRenderCellParams<PeticioAuditoria>) =>
                    params.row.serveiId != null ? (
                        <Tooltip title={params.row.serveiDescripcio ?? ''}>
                            <Link
                                component="button"
                                type="button"
                                underline="hover"
                                onClick={(event: React.MouseEvent) => {
                                    // No ha d'arribar al clic de la fila, que obre el detall
                                    event.stopPropagation();
                                    navigate(rutaServei(String(params.row.serveiId)));
                                }}
                            >
                                {params.row.serveiCodi}
                            </Link>
                        </Tooltip>
                    ) : (
                        params.row.serveiCodi
                    ),
            },
        ];
        if (tipus === 'enrutador') {
            columnes.push({
                field: 'entitatCodi',
                headerName: t('auditoria.columna.entitat'),
                width: 130,
                sortable: false,
            });
        }
        columnes.push(
            {
                field: 'estat',
                headerName: t('auditoria.columna.estat'),
                width: 210,
                renderCell: (params: GridRenderCellParams<PeticioAuditoria>) => {
                    const p = params.row;
                    let sufix: string | undefined;
                    if (tipus === 'backoffice' && p.estat === 'ERROR') {
                        sufix = t(p.estatScspError ? 'auditoria.origenError.scsp' : 'auditoria.origenError.backoffice');
                    } else if (tipus === 'backoffice' && p.sincrona === false && p.estat !== 'ERROR') {
                        sufix = `${p.processadesPercent ?? 0}%`;
                    }
                    return (
                        <Box sx={{ display: 'flex', alignItems: 'center', height: '100%' }}>
                            <EstatXip estat={p.estat} sufix={sufix} title={p.error} />
                        </Box>
                    );
                },
            },
            {
                field: 'error',
                headerName: t('auditoria.columna.error'),
                flex: 1.5,
                minWidth: 160,
                sortable: false,
                // Resum: la primera línia; el text sencer, al detall
                valueFormatter: (valor?: string) => valor?.split('\n')[0] ?? '',
            }
        );
        if (tipus === 'backoffice') {
            columnes.push({
                field: 'sincrona',
                headerName: t('auditoria.columna.sincrona'),
                width: 100,
                sortable: false,
                valueFormatter: (valor?: boolean) => (valor ? t('auditoria.si') : t('auditoria.no')),
            });
        } else {
            columnes.push({
                field: 'serveiTipus',
                headerName: t('auditoria.columna.serveiTipus'),
                width: 150,
                sortable: false,
                valueFormatter: (valor?: string) => (valor ? t(`serveis.tipus.${valor}`) : ''),
            });
        }
        return columnes;
    }, [t, tipus, navigate]);

    const toolbarElements = [
        { position: 2, element: <CapturaFindArgs findArgsRef={findArgsRef} /> },
        { position: 2, element: <FiltreBoto obert={filtreObert} onClick={() => setFiltreObert((obert) => !obert)} /> },
        { position: 2, element: <EstatLlegenda /> },
        { position: 2, element: <ExportarBoto resourceName={recurs} /> },
    ];

    const posicio = (location.state as EstatRuta)?.posicio;
    const navegacio: NavegacioDetall | undefined =
        posicio != null && findArgsRef.current != null
            ? {
                  posicio,
                  findArgs: findArgsRef.current,
                  onNavegar: (id, p) => navigate(rutaAuditoria(tipus, id), { replace: true, state: { posicio: p } }),
              }
            : undefined;

    return (
        <ScreenFrame>
            <MuiDataGrid
                key={tipus}
                title={
                    <ScreenTitle
                        icon={
                            tipus === 'backoffice' ? emiservIcons.auditoriaBackoffice : emiservIcons.auditoriaEnrutador
                        }
                        title={t(`auditoria.titol.${tipus}`)}
                    />
                }
                {...lot2ScreenGridProps(theme)}
                resourceName={recurs}
                apiRef={apiRef}
                columns={columns}
                readOnly
                toolbarHideCreate
                paginationActive
                persistentStateActive
                persistentStateKey={recurs}
                toolbarElementsWithPositions={toolbarElements}
                toolbarAdditionalRow={<AuditoriaFiltre tipus={tipus} obert={filtreObert} />}
                onRowsChange={(rows) => {
                    rowsRef.current = rows as PeticioAuditoria[];
                }}
                rowAdditionalActions={[
                    {
                        label: t('auditoria.accio.detall'),
                        icon: icons.detall,
                        onClick: (id: unknown) => obrirFila(id as string | number),
                    },
                ]}
                onRowClick={(params) => obrirFila(params.id as string | number)}
            />
            {idParam != null && (
                <AuditoriaDetall
                    tipus={tipus}
                    id={tipus === 'enrutador' ? Number(idParam) : idParam}
                    navegacio={navegacio}
                    onClose={() => navigate(rutaAuditoria(tipus))}
                />
            )}
        </ScreenFrame>
    );
};

export default AuditoriaLlistat;
