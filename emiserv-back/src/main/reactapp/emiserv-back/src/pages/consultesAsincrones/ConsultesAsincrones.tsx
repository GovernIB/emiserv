import React from 'react';
import { useTranslation } from 'react-i18next';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import Chip from '@mui/material/Chip';
import Collapse from '@mui/material/Collapse';
import FormControlLabel from '@mui/material/FormControlLabel';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import LinearProgress from '@mui/material/LinearProgress';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import Paper from '@mui/material/Paper';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TablePagination from '@mui/material/TablePagination';
import TableRow from '@mui/material/TableRow';
import TableSortLabel from '@mui/material/TableSortLabel';
import TextField from '@mui/material/TextField';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import { useBaseAppContext, useConfirmDialogButtons, useDebounce, useResourceApiContext } from 'reactlib';
import { icons, MAIN_GAP, ScreenFrame } from '../../lot2';
import {
    type Cedent,
    type Consulta,
    esperantReintent,
    type FiltreEstat,
    formatData,
    hiHaEnCurs,
    INTERVAL_ACTUALITZACIO_MS,
    intentsRestants,
    type Pagina,
    type ServeiFiltre,
    type Solicitud,
    solicitudReintentable,
    tempsFins,
    trams,
} from './consultesAsincronesModel';

type Ordre = { camp: 'dataPeticio' | 'peticioId' | 'terData'; dir: 'asc' | 'desc' };
type Filtre = { estat: FiltreEstat | ''; servei: number | ''; peticioId: string };

/** Crides a /api/consultesAsincrones (ConsultaAsincronaApiController), amb la galeta de sessió. */
const useApiConsultes = () => {
    const { apiUrl } = useResourceApiContext();
    return React.useMemo(() => {
        const base = `${apiUrl.replace(/\/+$/, '')}/consultesAsincrones`;
        const get = async <T,>(path: string): Promise<T> => {
            const resposta = await fetch(base + path, {
                credentials: 'include',
                headers: { Accept: 'application/json' },
            });
            if (!resposta.ok) {
                const cos = await resposta.json().catch(() => ({}));
                throw new Error(cos?.message ?? `HTTP ${resposta.status}`);
            }
            return resposta.json();
        };
        return {
            consultes: (filtre: Filtre, ordre: Ordre, page: number, size: number) => {
                const params = new URLSearchParams({
                    page: String(page),
                    size: String(size),
                    sort: ordre.camp,
                    dir: ordre.dir,
                });
                if (filtre.estat) params.set('estat', filtre.estat);
                if (filtre.servei !== '') params.set('servei', String(filtre.servei));
                if (filtre.peticioId.trim()) params.set('peticioId', filtre.peticioId.trim());
                return get<Pagina<Consulta>>(`?${params}`);
            },
            solicituds: (id: number) => get<Solicitud[]>(`/${id}/solicituds`),
            cedents: () => get<Cedent[]>('/cedents'),
            serveis: () => get<ServeiFiltre[]>('/serveis'),
            /** Acció (POST): retorna el missatge del servidor o llança l'error amb el seu missatge. */
            accio: async (path: string): Promise<string> => {
                const resposta = await fetch(base + path, {
                    method: 'POST',
                    credentials: 'include',
                    headers: { Accept: 'application/json' },
                });
                const cos = await resposta.json().catch(() => ({}));
                if (!resposta.ok) {
                    throw new Error(cos?.message ?? `HTTP ${resposta.status}`);
                }
                return cos?.message ?? '';
            },
        };
    }, [apiUrl]);
};

type Api = ReturnType<typeof useApiConsultes>;
type ExecutarAccio = (path: string, confirmacio?: string) => void;

/** Barra de progrés amb els trams de tramitades (verd), amb error (vermell) i enviant-se (blau). */
const BarraProgres: React.FC<{ consulta: Consulta }> = ({ consulta: c }) => {
    const { t } = useTranslation();
    const amplada = trams(c);
    return (
        <Box sx={{ minWidth: 160 }}>
            <Box
                role="progressbar"
                aria-valuemin={0}
                aria-valuemax={100}
                aria-valuenow={c.progres}
                aria-label={t('consultesAsincrones.columna.progres')}
                sx={{ display: 'flex', height: 10, borderRadius: 5, overflow: 'hidden', bgcolor: 'action.hover' }}
            >
                <Box sx={{ width: `${amplada.tramitades}%`, bgcolor: 'success.main', transition: 'width .5s' }} />
                <Box sx={{ width: `${amplada.errors}%`, bgcolor: 'error.main', transition: 'width .5s' }} />
                {amplada.enviant > 0 && (
                    <Box sx={{ width: `${amplada.enviant}%`, transition: 'width .5s' }}>
                        <LinearProgress color="info" sx={{ height: '100%' }} />
                    </Box>
                )}
            </Box>
            <Typography variant="caption" color="text.secondary" component="div">
                {c.processades}/{c.total} ({c.progres}%)
                {c.errors > 0 && (
                    <Box component="span" sx={{ color: 'error.main' }}>
                        {' · '}
                        {t('consultesAsincrones.ambError', { count: c.errors })}
                    </Box>
                )}
                {c.reintents > 0 && (
                    <Box component="span" sx={{ color: 'warning.main' }}>
                        {' · '}
                        {t('consultesAsincrones.pendentsReintent', { count: c.reintents })}
                    </Box>
                )}
            </Typography>
        </Box>
    );
};

const EstatConsultaCell: React.FC<{ consulta: Consulta }> = ({ consulta: c }) => {
    const { t } = useTranslation();
    if (c.estat === 'PENDENT') {
        return c.pausada ? (
            <Chip
                size="small"
                color="warning"
                icon={<Icon>pause</Icon>}
                label={t('consultesAsincrones.estat.pausada')}
            />
        ) : (
            <Chip size="small" color="info" icon={<Icon>sync</Icon>} label={t('consultesAsincrones.estat.enCurs')} />
        );
    }
    return c.estat === 'TRAMITADA' ? (
        <Chip
            size="small"
            color="success"
            icon={<Icon>check</Icon>}
            label={t('consultesAsincrones.estat.finalitzada')}
        />
    ) : (
        <Chip
            size="small"
            color="error"
            icon={<Icon>warning</Icon>}
            label={t('consultesAsincrones.estat.finalitzadaErrors')}
        />
    );
};

const COLOR_ESTAT_SOLICITUD = { PENDENT: 'default', ENVIANT: 'info', TRAMITADA: 'success', ERROR: 'error' } as const;

/** Intents, proper reintent i intents restants d'una sol·licitud. */
const ReintentsSolicitud: React.FC<{ solicitud: Solicitud }> = ({ solicitud: s }) => {
    const { t } = useTranslation();
    const restants = intentsRestants(s);
    const falta = tempsFins(s.dataProperIntent);
    return (
        <Box>
            <Typography variant="body2" component="div">
                {s.intentsMaxim ? `${s.intents}/${s.intentsMaxim}` : s.intents}
            </Typography>
            {esperantReintent(s) && (
                <Typography variant="caption" color="warning.main" component="div">
                    {falta
                        ? t(`consultesAsincrones.solicitud.properReintent_${falta.unitat}`, { valor: falta.valor })
                        : t('consultesAsincrones.solicitud.reintentImminent')}
                </Typography>
            )}
            {restants != null && s.intents > 0 && (
                <Typography variant="caption" color="text.secondary" component="div">
                    {t('consultesAsincrones.solicitud.intentsRestants', { count: restants })}
                </Typography>
            )}
        </Box>
    );
};

/** Sol·licituds (peticions simples) d'una consulta desplegada; es tornen a carregar a cada actualització. */
const Solicituds: React.FC<{ consulta: Consulta; api: Api; refresc: number; executar: ExecutarAccio }> = ({
    consulta,
    api,
    refresc,
    executar,
}) => {
    const { t } = useTranslation();
    const [solicituds, setSolicituds] = React.useState<Solicitud[]>();
    const [error, setError] = React.useState<string>();
    React.useEffect(() => {
        let actual = true;
        api.solicituds(consulta.id)
            .then((s) => actual && (setSolicituds(s), setError(undefined)))
            .catch((e: Error) => actual && setError(e.message));
        return () => {
            actual = false;
        };
    }, [api, consulta.id, refresc]);
    if (error) {
        return <Alert severity="error">{error}</Alert>;
    }
    if (!solicituds) {
        return <LinearProgress />;
    }
    return (
        <Table size="small" aria-label={t('consultesAsincrones.solicitud.titol')}>
            <TableHead>
                <TableRow>
                    <TableCell>{t('consultesAsincrones.solicitud.id')}</TableCell>
                    <TableCell>{t('consultesAsincrones.solicitud.cedent')}</TableCell>
                    <TableCell>{t('consultesAsincrones.solicitud.estat')}</TableCell>
                    <TableCell>{t('consultesAsincrones.solicitud.intents')}</TableCell>
                    <TableCell>{t('consultesAsincrones.solicitud.enviament')}</TableCell>
                    <TableCell>{t('consultesAsincrones.solicitud.properIntent')}</TableCell>
                    <TableCell>{t('consultesAsincrones.solicitud.peticioDesti')}</TableCell>
                    <TableCell>{t('consultesAsincrones.solicitud.error')}</TableCell>
                    <TableCell padding="checkbox" />
                </TableRow>
            </TableHead>
            <TableBody>
                {solicituds.map((s) => (
                    <TableRow key={s.id} hover>
                        <TableCell>{s.solicitudId}</TableCell>
                        <TableCell>{s.entitatCodi}</TableCell>
                        <TableCell>
                            <Chip
                                size="small"
                                variant={s.estat === 'PENDENT' ? 'outlined' : 'filled'}
                                color={COLOR_ESTAT_SOLICITUD[s.estat]}
                                label={t(`consultesAsincrones.solicitud.estats.${s.estat}`)}
                            />
                        </TableCell>
                        <TableCell>
                            <ReintentsSolicitud solicitud={s} />
                        </TableCell>
                        <TableCell sx={{ whiteSpace: 'nowrap' }}>{formatData(s.dataEnviament)}</TableCell>
                        <TableCell sx={{ whiteSpace: 'nowrap' }}>{formatData(s.dataProperIntent)}</TableCell>
                        <TableCell>{s.peticioDestiId}</TableCell>
                        <TableCell sx={{ maxWidth: 360 }}>
                            {(s.errorCodi || s.darrerError) && (
                                <Tooltip title={s.darrerError ?? ''}>
                                    <Box sx={{ display: 'flex', gap: 0.5, alignItems: 'center', minWidth: 0 }}>
                                        {s.errorCodi && <Chip size="small" color="error" label={s.errorCodi} />}
                                        <Typography variant="body2" noWrap>
                                            {s.darrerError}
                                        </Typography>
                                    </Box>
                                </Tooltip>
                            )}
                        </TableCell>
                        <TableCell padding="checkbox">
                            {solicitudReintentable(consulta, s) && (
                                <Tooltip title={t('consultesAsincrones.accio.reintentar')}>
                                    <IconButton
                                        size="small"
                                        onClick={() => executar(`/${consulta.id}/solicituds/${s.id}/reintentar`)}
                                    >
                                        <Icon fontSize="small">replay</Icon>
                                    </IconButton>
                                </Tooltip>
                            )}
                        </TableCell>
                    </TableRow>
                ))}
            </TableBody>
        </Table>
    );
};

/** Menú d'accions d'una consulta en curs, com la JSP. */
const AccionsConsulta: React.FC<{ consulta: Consulta; executar: ExecutarAccio }> = ({ consulta: c, executar }) => {
    const { t } = useTranslation();
    const [ancora, setAncora] = React.useState<HTMLElement | null>(null);
    if (c.finalitzada) {
        return null;
    }
    const opcio = (icona: string, text: string, path: string, confirmacio?: string) => (
        <MenuItem
            onClick={() => {
                setAncora(null);
                executar(`/${c.id}/${path}`, confirmacio);
            }}
        >
            <ListItemIcon>
                <Icon fontSize="small">{icona}</Icon>
            </ListItemIcon>
            <ListItemText>{text}</ListItemText>
        </MenuItem>
    );
    return (
        <>
            <IconButton
                size="small"
                aria-label={t('consultesAsincrones.accions')}
                onClick={(e) => setAncora(e.currentTarget)}
            >
                <Icon>more_vert</Icon>
            </IconButton>
            <Menu anchorEl={ancora} open={ancora != null} onClose={() => setAncora(null)}>
                {c.pausada
                    ? opcio('play_arrow', t('consultesAsincrones.accio.reprendre'), 'reprendre')
                    : opcio('pause', t('consultesAsincrones.accio.pausar'), 'pausar')}
                {(c.errors > 0 || c.reintents > 0) &&
                    opcio('replay', t('consultesAsincrones.accio.reintentar'), 'reintentar')}
                {opcio(
                    'close',
                    t('consultesAsincrones.accio.cancelar'),
                    'cancelar',
                    t('consultesAsincrones.confirmacio.cancelar')
                )}
            </Menu>
        </>
    );
};

const FilaConsulta: React.FC<{
    consulta: Consulta;
    api: Api;
    refresc: number;
    executar: ExecutarAccio;
    desplegada: boolean;
    onToggle: () => void;
}> = ({ consulta: c, api, refresc, executar, desplegada, onToggle }) => {
    const { t } = useTranslation();
    return (
        <>
            <TableRow hover sx={{ '& > td': { borderBottom: desplegada ? 'unset' : undefined } }}>
                <TableCell padding="checkbox">
                    <IconButton
                        size="small"
                        aria-expanded={desplegada}
                        aria-label={t(desplegada ? 'consultesAsincrones.plega' : 'consultesAsincrones.desplega')}
                        onClick={onToggle}
                    >
                        <Icon>{desplegada ? icons.plega : icons.desplega}</Icon>
                    </IconButton>
                </TableCell>
                <TableCell sx={{ whiteSpace: 'nowrap' }}>{formatData(c.dataPeticio)}</TableCell>
                <TableCell>
                    <Box sx={{ display: 'flex', gap: 0.5, alignItems: 'center', flexWrap: 'wrap' }}>
                        {c.peticioId}
                        {c.destinsPausats && (
                            <Tooltip title={t('consultesAsincrones.destinsPausatsInfo')}>
                                <Chip
                                    size="small"
                                    color="warning"
                                    variant="outlined"
                                    label={t('consultesAsincrones.destinsPausats')}
                                />
                            </Tooltip>
                        )}
                    </Box>
                </TableCell>
                <TableCell>
                    <Tooltip title={c.serveiNom ?? ''}>
                        <span>{c.serveiCodi}</span>
                    </Tooltip>
                </TableCell>
                <TableCell sx={{ width: '24%' }}>
                    <BarraProgres consulta={c} />
                </TableCell>
                <TableCell sx={{ whiteSpace: 'nowrap' }}>{formatData(c.terData)}</TableCell>
                <TableCell sx={{ whiteSpace: 'nowrap' }}>
                    {c.dataFiPrevista && (
                        <Tooltip title={c.retardada ? t('consultesAsincrones.retardada') : ''}>
                            <Box
                                component="span"
                                sx={{
                                    color: c.retardada ? 'error.main' : undefined,
                                    display: 'inline-flex',
                                    gap: 0.5,
                                    alignItems: 'center',
                                }}
                            >
                                {formatData(c.dataFiPrevista)}
                                {c.retardada && <Icon fontSize="small">warning</Icon>}
                            </Box>
                        </Tooltip>
                    )}
                </TableCell>
                <TableCell>
                    <EstatConsultaCell consulta={c} />
                </TableCell>
                <TableCell padding="checkbox">
                    <AccionsConsulta consulta={c} executar={executar} />
                </TableCell>
            </TableRow>
            <TableRow>
                <TableCell colSpan={9} sx={{ py: 0, ...(desplegada ? {} : { borderBottom: 'none' }) }}>
                    <Collapse in={desplegada} timeout="auto" unmountOnExit>
                        <Box sx={{ py: 1, pl: 6 }}>
                            <Solicituds consulta={c} api={api} refresc={refresc} executar={executar} />
                        </Box>
                    </Collapse>
                </TableCell>
            </TableRow>
        </>
    );
};

/** Cedents i backoffices amb sol·licituds pendents (o pausables), com el panell de la JSP. */
const Cedents: React.FC<{ cedents?: Cedent[]; executar: ExecutarAccio }> = ({ cedents, executar }) => {
    const { t } = useTranslation();
    const [obert, setObert] = React.useState(true);
    const [tots, setTots] = React.useState(false);
    const visibles = (cedents ?? []).filter((c) => tots || c.pendents + c.enviant > 0 || c.pausada);
    return (
        <Paper variant="outlined">
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, px: 1, py: 0.5 }}>
                <IconButton size="small" onClick={() => setObert((o) => !o)} aria-expanded={obert}>
                    <Icon>{obert ? icons.plega : icons.desplega}</Icon>
                </IconButton>
                <Typography variant="subtitle1">{t('consultesAsincrones.cedents.titol')}</Typography>
                <Chip size="small" label={cedents?.length ?? 0} />
                <FormControlLabel
                    sx={{ ml: 'auto' }}
                    control={<Checkbox size="small" checked={tots} onChange={(e) => setTots(e.target.checked)} />}
                    label={t('consultesAsincrones.cedents.mostrarTots')}
                />
            </Box>
            <Collapse in={obert}>
                {visibles.length === 0 ? (
                    <Typography variant="body2" color="text.secondary" sx={{ px: 2, pb: 1.5 }}>
                        {t('consultesAsincrones.cedents.buit')}
                    </Typography>
                ) : (
                    <TableContainer>
                        <Table size="small">
                            <TableHead>
                                <TableRow>
                                    <TableCell>{t('consultesAsincrones.cedents.servei')}</TableCell>
                                    <TableCell>{t('consultesAsincrones.cedents.cedent')}</TableCell>
                                    <TableCell>{t('consultesAsincrones.cedents.desti')}</TableCell>
                                    <TableCell>{t('consultesAsincrones.cedents.ritme')}</TableCell>
                                    <TableCell>{t('consultesAsincrones.cedents.pendents')}</TableCell>
                                    <TableCell>{t('consultesAsincrones.cedents.enviant')}</TableCell>
                                    <TableCell>{t('consultesAsincrones.cedents.fiPrevista')}</TableCell>
                                    <TableCell />
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {visibles.map((c) => (
                                    <TableRow
                                        key={`${c.serveiCodi}-${c.entitatCodi}-${c.rutaId}`}
                                        sx={c.pausada ? { bgcolor: 'warning.light' } : undefined}
                                    >
                                        <TableCell>
                                            <Tooltip title={c.serveiNom ?? ''}>
                                                <span>{c.serveiCodi}</span>
                                            </Tooltip>
                                        </TableCell>
                                        <TableCell>
                                            {c.entitatCodi}
                                            {c.pausada && (
                                                <Chip
                                                    size="small"
                                                    color="warning"
                                                    sx={{ ml: 1 }}
                                                    label={t('consultesAsincrones.cedents.pausat')}
                                                />
                                            )}
                                        </TableCell>
                                        <TableCell>
                                            {c.destiTipus
                                                ? t(
                                                      `consultesAsincrones.cedents.destiTipus.${c.destiTipus}`,
                                                      c.destiTipus
                                                  )
                                                : ''}
                                        </TableCell>
                                        <TableCell>
                                            {t('consultesAsincrones.cedents.ritmeValor', {
                                                interval: c.intervalMs,
                                                concurrencia: c.concurrencia,
                                            })}
                                        </TableCell>
                                        <TableCell>{c.pendents}</TableCell>
                                        <TableCell>{c.enviant}</TableCell>
                                        <TableCell sx={{ whiteSpace: 'nowrap' }}>
                                            {formatData(c.dataFiPrevista)}
                                        </TableCell>
                                        <TableCell align="right">
                                            {c.rutaId != null &&
                                                (c.pausada ? (
                                                    <Button
                                                        size="small"
                                                        color="success"
                                                        startIcon={<Icon>play_arrow</Icon>}
                                                        onClick={() => executar(`/cedents/${c.rutaId}/reprendre`)}
                                                    >
                                                        {t('consultesAsincrones.accio.reprendre')}
                                                    </Button>
                                                ) : (
                                                    <Button
                                                        size="small"
                                                        color="warning"
                                                        startIcon={<Icon>pause</Icon>}
                                                        onClick={() =>
                                                            executar(
                                                                `/cedents/${c.rutaId}/pausar`,
                                                                t('consultesAsincrones.cedents.confirmacioPausar', {
                                                                    cedent: c.entitatCodi,
                                                                })
                                                            )
                                                        }
                                                    >
                                                        {t('consultesAsincrones.accio.pausar')}
                                                    </Button>
                                                ))}
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                )}
            </Collapse>
        </Paper>
    );
};

/**
 * Consultes asíncrones processades com a peticions síncrones a cada destí (consultaAsincronaList.jsp):
 * progrés de cada consulta, que s'actualitza periòdicament mentre n'hi hagi alguna en curs, i
 * les seves sol·licituds (estat, intents i proper reintent) en desplegar-la. Només administradors.
 */
export const ConsultesAsincrones: React.FC = () => {
    const { t } = useTranslation();
    const api = useApiConsultes();
    const { temporalMessageShow, messageDialogShow } = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const [filtre, setFiltre] = React.useState<Filtre>({ estat: '', servei: '', peticioId: '' });
    const peticioIdDebounced = useDebounce(filtre.peticioId);
    const [ordre, setOrdre] = React.useState<Ordre>({ camp: 'dataPeticio', dir: 'desc' });
    const [page, setPage] = React.useState(0);
    const [size, setSize] = React.useState(20);
    const [pagina, setPagina] = React.useState<Pagina<Consulta>>();
    const [cedents, setCedents] = React.useState<Cedent[]>();
    const [serveis, setServeis] = React.useState<ServeiFiltre[]>([]);
    const [error, setError] = React.useState<string>();
    const [carregant, setCarregant] = React.useState(false);
    const [actualitzar, setActualitzar] = React.useState(true);
    const [refresc, setRefresc] = React.useState(0);
    const [desplegades, setDesplegades] = React.useState<Set<number>>(new Set());
    const [darreraActualitzacio, setDarreraActualitzacio] = React.useState<Date>();

    React.useEffect(() => {
        api.serveis()
            .then(setServeis)
            .catch(() => setServeis([]));
    }, [api]);

    const filtreEfectiu = React.useMemo(
        () => ({ ...filtre, peticioId: peticioIdDebounced ?? '' }),
        // eslint-disable-next-line react-hooks/exhaustive-deps
        [filtre.estat, filtre.servei, peticioIdDebounced]
    );

    React.useEffect(() => {
        let actual = true;
        setCarregant(true);
        Promise.all([api.consultes(filtreEfectiu, ordre, page, size), api.cedents()])
            .then(([p, c]) => {
                if (!actual) return;
                setPagina(p);
                setCedents(c);
                setError(undefined);
                setDarreraActualitzacio(new Date());
            })
            .catch((e: Error) => actual && setError(e.message))
            .finally(() => actual && setCarregant(false));
        return () => {
            actual = false;
        };
    }, [api, filtreEfectiu, ordre, page, size, refresc]);

    // Actualització periòdica mentre hi hagi consultes en curs a la pàgina
    const enCurs = hiHaEnCurs(pagina?.contingut);
    React.useEffect(() => {
        if (!actualitzar || !enCurs) return;
        const interval = window.setInterval(() => setRefresc((r) => r + 1), INTERVAL_ACTUALITZACIO_MS);
        return () => window.clearInterval(interval);
    }, [actualitzar, enCurs]);

    const executar: ExecutarAccio = React.useCallback(
        (path, confirmacio) => {
            const fer = () =>
                api
                    .accio(path)
                    .then((missatge) => temporalMessageShow(null, missatge, 'success'))
                    .catch((e: Error) => temporalMessageShow(null, e.message, 'error'))
                    .finally(() => setRefresc((r) => r + 1));
            if (!confirmacio) {
                fer();
                return;
            }
            messageDialogShow(t('consultesAsincrones.confirmacio.titol'), confirmacio, confirmDialogButtons, {
                maxWidth: 'sm',
                fullWidth: true,
            }).then((confirmat: unknown) => {
                if (confirmat === true) {
                    fer();
                }
            });
        },
        [api, confirmDialogButtons, messageDialogShow, t, temporalMessageShow]
    );

    const canviarFiltre = (canvi: Partial<Filtre>) => {
        setFiltre((f) => ({ ...f, ...canvi }));
        setPage(0);
    };
    const ordenar = (camp: Ordre['camp']) =>
        setOrdre((o) => ({ camp, dir: o.camp === camp && o.dir === 'desc' ? 'asc' : 'desc' }));
    const capOrdenable = (camp: Ordre['camp'], text: string) => (
        <TableSortLabel
            active={ordre.camp === camp}
            direction={ordre.camp === camp ? ordre.dir : 'desc'}
            onClick={() => ordenar(camp)}
        >
            {text}
        </TableSortLabel>
    );
    const toggle = (id: number) =>
        setDesplegades((d) => {
            const nou = new Set(d);
            if (!nou.delete(id)) nou.add(id);
            return nou;
        });

    return (
        <ScreenFrame
            icon="pending_actions"
            title={t('consultesAsincrones.titol')}
            padded
            actions={
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    {darreraActualitzacio && (
                        <Typography
                            variant="caption"
                            color="text.secondary"
                            sx={{ display: { xs: 'none', md: 'block' } }}
                        >
                            {t('consultesAsincrones.actualitzat', {
                                hora: formatData(darreraActualitzacio.getTime()).slice(11),
                            })}
                        </Typography>
                    )}
                    <FormControlLabel
                        control={
                            <Checkbox
                                size="small"
                                checked={actualitzar}
                                onChange={(e) => setActualitzar(e.target.checked)}
                            />
                        }
                        label={t('consultesAsincrones.actualitzar')}
                    />
                    <Tooltip title={t('consultesAsincrones.refresca')}>
                        <IconButton onClick={() => setRefresc((r) => r + 1)} disabled={carregant}>
                            <Icon>{icons.refresca}</Icon>
                        </IconButton>
                    </Tooltip>
                </Box>
            }
        >
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: MAIN_GAP + 'px', minHeight: 0 }}>
                <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
                    <TextField
                        select
                        size="small"
                        label={t('consultesAsincrones.filtre.estat')}
                        value={filtre.estat}
                        onChange={(e) => canviarFiltre({ estat: e.target.value as Filtre['estat'] })}
                        sx={{ minWidth: 180 }}
                    >
                        <MenuItem value="">{t('consultesAsincrones.filtre.totes')}</MenuItem>
                        {(['EN_CURS', 'PAUSADA', 'FINALITZADA'] as const).map((e) => (
                            <MenuItem key={e} value={e}>
                                {t(`consultesAsincrones.filtre.estats.${e}`)}
                            </MenuItem>
                        ))}
                    </TextField>
                    <TextField
                        select
                        size="small"
                        label={t('consultesAsincrones.filtre.servei')}
                        value={filtre.servei}
                        onChange={(e) => canviarFiltre({ servei: e.target.value === '' ? '' : Number(e.target.value) })}
                        sx={{ minWidth: 260 }}
                    >
                        <MenuItem value="">{t('consultesAsincrones.filtre.tots')}</MenuItem>
                        {serveis.map((s) => (
                            <MenuItem key={s.id} value={s.id}>
                                {s.codi}
                                {s.nom ? ` - ${s.nom}` : ''}
                            </MenuItem>
                        ))}
                    </TextField>
                    <TextField
                        size="small"
                        label={t('consultesAsincrones.filtre.peticio')}
                        value={filtre.peticioId}
                        onChange={(e) => canviarFiltre({ peticioId: e.target.value })}
                    />
                </Box>
                <Cedents cedents={cedents} executar={executar} />
                {error && <Alert severity="error">{error}</Alert>}
                <Paper variant="outlined">
                    {carregant && <LinearProgress sx={{ position: 'absolute', left: 0, right: 0 }} />}
                    <TableContainer>
                        <Table size="small" aria-label={t('consultesAsincrones.titol')}>
                            <TableHead>
                                <TableRow>
                                    <TableCell padding="checkbox" />
                                    <TableCell>
                                        {capOrdenable('dataPeticio', t('consultesAsincrones.columna.data'))}
                                    </TableCell>
                                    <TableCell>
                                        {capOrdenable('peticioId', t('consultesAsincrones.columna.peticio'))}
                                    </TableCell>
                                    <TableCell>{t('consultesAsincrones.columna.servei')}</TableCell>
                                    <TableCell>{t('consultesAsincrones.columna.progres')}</TableCell>
                                    <TableCell>
                                        {capOrdenable('terData', t('consultesAsincrones.columna.ter'))}
                                    </TableCell>
                                    <TableCell>{t('consultesAsincrones.columna.fiPrevista')}</TableCell>
                                    <TableCell>{t('consultesAsincrones.columna.estat')}</TableCell>
                                    <TableCell padding="checkbox" />
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {pagina?.contingut.map((c) => (
                                    <FilaConsulta
                                        key={c.id}
                                        consulta={c}
                                        api={api}
                                        refresc={refresc}
                                        executar={executar}
                                        desplegada={desplegades.has(c.id)}
                                        onToggle={() => toggle(c.id)}
                                    />
                                ))}
                                {pagina && pagina.contingut.length === 0 && (
                                    <TableRow>
                                        <TableCell colSpan={9}>
                                            <Typography
                                                variant="body2"
                                                color="text.secondary"
                                                align="center"
                                                sx={{ py: 2 }}
                                            >
                                                {t('consultesAsincrones.buit')}
                                            </Typography>
                                        </TableCell>
                                    </TableRow>
                                )}
                            </TableBody>
                        </Table>
                    </TableContainer>
                    <TablePagination
                        component="div"
                        count={pagina?.elementsTotal ?? 0}
                        page={page}
                        rowsPerPage={size}
                        rowsPerPageOptions={[10, 20, 50, 100]}
                        onPageChange={(_, p) => setPage(p)}
                        onRowsPerPageChange={(e) => {
                            setSize(Number(e.target.value));
                            setPage(0);
                        }}
                        labelRowsPerPage={t('consultesAsincrones.filesPerPagina')}
                    />
                </Paper>
            </Box>
        </ScreenFrame>
    );
};

export default ConsultesAsincrones;
