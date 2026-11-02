import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Accordion from '@mui/material/Accordion';
import AccordionDetails from '@mui/material/AccordionDetails';
import AccordionSummary from '@mui/material/AccordionSummary';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import CircularProgress from '@mui/material/CircularProgress';
import Collapse from '@mui/material/Collapse';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Link from '@mui/material/Link';
import Tab from '@mui/material/Tab';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Tabs from '@mui/material/Tabs';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import { MuiDialog, useBaseAppContext, useResourceApiService } from 'reactlib';
import { icons } from '../../lot2';
import { formatDataHora } from '../../util/dates';
import { missatgeError } from '../../util/missatgeError';
import { rutaServei } from '../serveis/serveiModel';
import {
    normalitzarSolicitud,
    type PeticioAuditoria,
    RECURS_AUDITORIA,
    type SolicitudAuditoria,
    type TipusAuditoria,
} from './auditoriaModel';
import { EstatXip } from './EstatPeticio';
import { useApiAuditoria } from './useApiAuditoria';
import XmlVisor from './XmlVisor';

/** Navegació a l'anterior i la següent petició del llistat (mateix filtre i ordre). */
export type NavegacioDetall = {
    /** Posició (des de 0) de la petició dins el resultat del llistat. */
    posicio: number;
    /** Arguments de la consulta del llistat. */
    findArgs: Record<string, unknown>;
    onNavegar: (id: string | number, posicio: number) => void;
};

type Pestanya = 'scsp' | 'backoffice' | 'errorComunicacio' | 'missatges' | 'respostes' | 'solicituds';

const pestanyesDisponibles = (tipus: TipusAuditoria, p?: PeticioAuditoria): Pestanya[] => {
    if (p == null) {
        return [];
    }
    if (tipus === 'backoffice') {
        return [
            'scsp',
            ...(p.comunicacioBackofficeDisponible ? (['backoffice'] as const) : []),
            ...(p.comunicacioBackofficeError ? (['errorComunicacio'] as const) : []),
            'solicituds',
        ];
    }
    return [
        'missatges',
        ...(p.teRespostes && p.serveiTipus === 'ENRUTADOR_MULTIPLE' ? (['respostes'] as const) : []),
        'solicituds',
    ];
};

/** Camí d'un recurs de l'API dins la petició (l'id de backoffice és el número de petició, amb qualsevol caràcter). */
const cami = (id: string | number, ...parts: string[]) =>
    [encodeURIComponent(String(id)), ...parts.map(encodeURIComponent)].join('/');

/** Un parell etiqueta-valor de la capçalera. */
const Camp: React.FC<React.PropsWithChildren<{ etiqueta: string; ample?: boolean }>> = ({
    etiqueta,
    ample,
    children,
}) => (
    <Box sx={{ minWidth: 0, gridColumn: ample ? '1 / -1' : undefined }}>
        <Typography variant="caption" color="text.secondary" component="div">
            {etiqueta}
        </Typography>
        <Typography variant="body2" component="div" sx={{ wordBreak: 'break-word' }}>
            {children ?? '—'}
        </Typography>
    </Box>
);

/** Text d'error complet, amb botó de copiar i altura limitada. */
const TextError: React.FC<{ text: string }> = ({ text }) => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    return (
        <Alert
            severity="error"
            sx={{ '& .MuiAlert-message': { minWidth: 0, flex: 1 } }}
            action={
                <Tooltip title={t('auditoria.xml.copiar')}>
                    <IconButton
                        size="small"
                        aria-label={t('auditoria.xml.copiar')}
                        onClick={() =>
                            navigator.clipboard
                                .writeText(text)
                                .then(() => temporalMessageShow(null, t('auditoria.xml.copiat'), 'success'))
                                .catch(() => temporalMessageShow(null, t('auditoria.xml.errorCopiar'), 'error'))
                        }
                    >
                        <Icon fontSize="small">{icons.copia}</Icon>
                    </IconButton>
                </Tooltip>
            }
        >
            <Box
                sx={{
                    whiteSpace: 'pre-wrap',
                    wordBreak: 'break-word',
                    maxHeight: 120,
                    overflow: 'auto',
                    fontFamily: 'monospace',
                    fontSize: 12,
                }}
            >
                {text}
            </Box>
        </Alert>
    );
};

/** Dos visors de missatges un al costat de l'altre (un sota l'altre en pantalles estretes). */
const Parell: React.FC<React.PropsWithChildren<{ altura?: number | string }>> = ({ altura = '100%', children }) => (
    <Box sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, gap: 1, height: altura, minHeight: 0 }}>
        {children}
    </Box>
);

/** Capçalera de la petició (spec §5.4), amb els camps de la JSP de detall. */
const Capcalera: React.FC<{ tipus: TipusAuditoria; peticio: PeticioAuditoria; onServei: (id: number) => void }> = ({
    tipus,
    peticio: p,
    onServei,
}) => {
    const { t } = useTranslation();
    const servei = p.serveiCodi ? `${p.serveiCodi}${p.serveiDescripcio ? ' - ' + p.serveiDescripcio : ''}` : undefined;
    return (
        <Box sx={{ mb: 1 }}>
            <Box
                sx={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fill, minmax(190px, 1fr))',
                    gap: 1.5,
                    mb: p.error ? 1.5 : 0,
                }}
            >
                <Camp etiqueta={t('auditoria.detall.peticioId')}>{p.peticioId}</Camp>
                <Camp etiqueta={t('auditoria.detall.dataPeticio')}>{formatDataHora(p.dataPeticio) || undefined}</Camp>
                <Camp etiqueta={t('auditoria.detall.dataResposta')}>{formatDataHora(p.dataResposta) || undefined}</Camp>
                <Camp etiqueta={t('auditoria.detall.estat')}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, flexWrap: 'wrap' }}>
                        <EstatXip estat={p.estat} />
                        {tipus === 'backoffice' && p.estatScsp && <Chip size="small" label={`SCSP ${p.estatScsp}`} />}
                    </Box>
                </Camp>
                <Camp etiqueta={t('auditoria.detall.servei')}>
                    {servei && p.serveiId != null ? (
                        <Link
                            component="button"
                            type="button"
                            underline="hover"
                            sx={{ textAlign: 'left' }}
                            onClick={() => onServei(p.serveiId!)}
                        >
                            {servei}
                        </Link>
                    ) : (
                        servei
                    )}
                </Camp>
                <Camp etiqueta={t('auditoria.detall.procediment')}>{p.procedimentCodiNom}</Camp>
                {tipus === 'backoffice' ? (
                    <>
                        <Camp etiqueta={t('auditoria.detall.sincrona')}>
                            {p.sincrona ? t('auditoria.si') : t('auditoria.no')}
                        </Camp>
                        <Camp etiqueta={t('auditoria.detall.solicituds')}>
                            {t('auditoria.detall.processades', {
                                total: p.numTransmissions ?? 0,
                                processades: p.processadesTotal ?? 0,
                                errors: p.processadesError ?? 0,
                                percent: p.processadesPercent ?? 0,
                            })}
                        </Camp>
                        <Camp etiqueta={t('auditoria.detall.enviaments')}>{p.numEnviaments}</Camp>
                        {!p.sincrona && (
                            <>
                                <Camp etiqueta={t('auditoria.detall.ter')}>{formatDataHora(p.ter) || undefined}</Camp>
                                <Camp etiqueta={t('auditoria.detall.darreraComprovacio')}>
                                    {formatDataHora(p.dataDarreraComprovacio) || undefined}
                                </Camp>
                            </>
                        )}
                    </>
                ) : (
                    <>
                        <Camp etiqueta={t('auditoria.detall.serveiTipus')}>
                            {p.serveiTipus ? t(`serveis.tipus.${p.serveiTipus}`) : undefined}
                        </Camp>
                        <Camp etiqueta={t('auditoria.detall.entitat')}>{p.entitatCodi}</Camp>
                    </>
                )}
            </Box>
            {p.error && <TextError text={p.error} />}
        </Box>
    );
};

/** Funcionari com a la JSP: «Nom (NIF: document)». */
const funcionari = (s: SolicitudAuditoria) =>
    [s.funcionariNom, s.funcionariDocument ? `(NIF: ${s.funcionariDocument})` : null].filter(Boolean).join(' ');

/**
 * Fila d'una sol·licitud, amb les columnes de la JSP (sol·licitud, remitent, procediment, funcionari i, al
 * backoffice, l'estat) més el titular. En desplegar-la, la resta de dades del detall de la JSP
 * (`auditoriaBackofficeSolicitudDetall.jsp`) i, al backoffice, els missatges de la sol·licitud.
 */
const FilaSolicitud: React.FC<{ tipus: TipusAuditoria; peticioId: string | number; s: SolicitudAuditoria }> = ({
    tipus,
    peticioId,
    s,
}) => {
    const { t } = useTranslation();
    const api = useApiAuditoria(tipus);
    const [obert, setObert] = React.useState(false);
    const missatge = (codi: string) => () => api.text(cami(peticioId, 'solicituds', s.solicitudId, 'missatges', codi));
    return (
        <>
            <TableRow
                hover
                sx={{ '& > td': { borderBottom: obert ? 'unset' : undefined }, cursor: 'pointer' }}
                onClick={() => setObert((o) => !o)}
            >
                <TableCell padding="checkbox">
                    <IconButton size="small" aria-label={t('auditoria.solicituds.desplega')} aria-expanded={obert}>
                        <Icon fontSize="small">{obert ? icons.plega : icons.desplega}</Icon>
                    </IconButton>
                </TableCell>
                <TableCell>{s.solicitudId}</TableCell>
                <TableCell>{s.solicitantNom}</TableCell>
                <TableCell>{s.procediment}</TableCell>
                <TableCell>{funcionari(s)}</TableCell>
                <TableCell>{[s.titularDocument, s.titularNom].filter(Boolean).join(' - ')}</TableCell>
                {tipus === 'backoffice' && (
                    <TableCell>
                        {s.backofficeEstat ? t(`auditoria.solicituds.backofficeEstatEnum.${s.backofficeEstat}`) : ''}
                    </TableCell>
                )}
            </TableRow>
            <TableRow>
                <TableCell sx={{ py: 0 }} colSpan={tipus === 'backoffice' ? 7 : 6}>
                    <Collapse in={obert} timeout="auto" unmountOnExit>
                        <Box sx={{ py: 1.5 }}>
                            <Box
                                sx={{
                                    display: 'grid',
                                    gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))',
                                    gap: 1.5,
                                    mb: 1.5,
                                }}
                            >
                                <Camp etiqueta={t('auditoria.solicituds.transmisioId')}>{s.transmisioId}</Camp>
                                <Camp etiqueta={t('auditoria.solicituds.dataGeneracio')}>
                                    {formatDataHora(s.dataGeneracio) || undefined}
                                </Camp>
                                <Camp etiqueta={t('auditoria.solicituds.solicitant')}>
                                    {s.solicitantId || s.solicitantNom
                                        ? [s.solicitantId ? `[${s.solicitantId}]` : null, s.solicitantNom]
                                              .filter(Boolean)
                                              .join(' ')
                                        : undefined}
                                </Camp>
                                <Camp etiqueta={t('auditoria.solicituds.unitatTramitadora')}>
                                    {s.unitatTramitadora}
                                </Camp>
                                {tipus === 'backoffice' && (
                                    <Camp etiqueta={t('auditoria.solicituds.expedient')}>{s.expedient}</Camp>
                                )}
                                <Camp etiqueta={t('auditoria.solicituds.finalitat')}>{s.finalitat}</Camp>
                                <Camp etiqueta={t('auditoria.solicituds.consentiment')}>{s.consentiment}</Camp>
                                <Camp etiqueta={t('auditoria.solicituds.estatScsp')}>{s.estat}</Camp>
                            </Box>
                            {s.error && <TextError text={s.error} />}
                            {/* Com la JSP: la petició al backoffice i, al costat, la resposta o l'error de comunicació */}
                            {tipus === 'backoffice' && s.comunicacioBackofficeDisponible && (
                                <Box sx={{ mt: 1.5 }}>
                                    <Parell altura={360}>
                                        <XmlVisor
                                            titol={t('auditoria.missatge.peticioBackoffice')}
                                            carregar={missatge('PETICIO_BACKOFFICE')}
                                            nomFitxer={`${s.solicitudId}-peticio-backoffice`}
                                        />
                                        {s.comunicacioBackofficeError ? (
                                            <XmlVisor
                                                titol={t('auditoria.missatge.errorComunicacio')}
                                                carregar={missatge('ERROR_COMUNICACIO')}
                                                nomFitxer={`${s.solicitudId}-error-comunicacio`}
                                            />
                                        ) : (
                                            <XmlVisor
                                                titol={t('auditoria.missatge.respostaBackoffice')}
                                                carregar={missatge('RESPOSTA_BACKOFFICE')}
                                                nomFitxer={`${s.solicitudId}-resposta-backoffice`}
                                            />
                                        )}
                                    </Parell>
                                </Box>
                            )}
                        </Box>
                    </Collapse>
                </TableCell>
            </TableRow>
        </>
    );
};

/** Sol·licituds de la petició (es carreguen en obrir la pestanya). */
const Solicituds: React.FC<{ tipus: TipusAuditoria; peticioId: string | number }> = ({ tipus, peticioId }) => {
    const { t } = useTranslation();
    const api = useApiAuditoria(tipus);
    const [estat, setEstat] = React.useState<{ solicituds?: SolicitudAuditoria[]; error?: string }>({});
    React.useEffect(() => {
        let actiu = true;
        api.json<Record<string, unknown>[]>(cami(peticioId, 'solicituds'))
            .then((llista) => actiu && setEstat({ solicituds: llista.map((s) => normalitzarSolicitud(tipus, s)) }))
            .catch((error: unknown) => actiu && setEstat({ error: missatgeError(error) }));
        return () => {
            actiu = false;
        };
    }, [api, tipus, peticioId]);
    if (estat.error) {
        return <Alert severity="error">{estat.error}</Alert>;
    }
    if (estat.solicituds == null) {
        return <CircularProgress size={24} />;
    }
    if (estat.solicituds.length === 0) {
        return <Typography color="text.secondary">{t('auditoria.solicituds.cap')}</Typography>;
    }
    return (
        <Table size="small" stickyHeader>
            <TableHead>
                <TableRow>
                    <TableCell padding="checkbox" />
                    <TableCell>{t('auditoria.solicituds.solicitudId')}</TableCell>
                    <TableCell>{t('auditoria.solicituds.solicitant')}</TableCell>
                    <TableCell>{t('auditoria.solicituds.procediment')}</TableCell>
                    <TableCell>{t('auditoria.solicituds.funcionari')}</TableCell>
                    <TableCell>{t('auditoria.solicituds.titular')}</TableCell>
                    {tipus === 'backoffice' && <TableCell>{t('auditoria.solicituds.backofficeEstat')}</TableCell>}
                </TableRow>
            </TableHead>
            <TableBody>
                {estat.solicituds.map((s) => (
                    <FilaSolicitud key={s.solicitudId} tipus={tipus} peticioId={peticioId} s={s} />
                ))}
            </TableBody>
        </Table>
    );
};

type RespostaEntitat = { entitat: string; urlResposta?: string; xmlResposta?: string; respostaEscollida?: boolean };

/** Respostes de cada entitat d'un enrutador múltiple; l'escollida, oberta i marcada. */
const Respostes: React.FC<{ peticioId: string | number }> = ({ peticioId }) => {
    const { t } = useTranslation();
    const api = useApiAuditoria('enrutador');
    const [estat, setEstat] = React.useState<{ respostes?: RespostaEntitat[]; error?: string }>({});
    React.useEffect(() => {
        let actiu = true;
        api.json<RespostaEntitat[]>(cami(peticioId, 'respostes'))
            .then((respostes) => actiu && setEstat({ respostes }))
            .catch((error: unknown) => actiu && setEstat({ error: missatgeError(error) }));
        return () => {
            actiu = false;
        };
    }, [api, peticioId]);
    if (estat.error) {
        return <Alert severity="error">{estat.error}</Alert>;
    }
    if (estat.respostes == null) {
        return <CircularProgress size={24} />;
    }
    if (estat.respostes.length === 0) {
        return <Typography color="text.secondary">{t('auditoria.respostes.cap')}</Typography>;
    }
    return (
        <Box>
            {estat.respostes.map((r) => (
                <Accordion
                    key={r.entitat}
                    defaultExpanded={r.respostaEscollida}
                    slotProps={{ transition: { unmountOnExit: true } }}
                >
                    <AccordionSummary expandIcon={<Icon>{icons.desplega}</Icon>}>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, minWidth: 0 }}>
                            <Typography variant="subtitle2">{r.entitat}</Typography>
                            {r.respostaEscollida && (
                                <Chip size="small" color="success" label={t('auditoria.respostes.escollida')} />
                            )}
                            <Typography variant="body2" color="text.secondary" noWrap>
                                {r.urlResposta}
                            </Typography>
                        </Box>
                    </AccordionSummary>
                    <AccordionDetails sx={{ height: 360, display: 'flex' }}>
                        <XmlVisor
                            titol={t('auditoria.missatge.resposta')}
                            carregar={() => Promise.resolve(r.xmlResposta ?? null)}
                            nomFitxer={`${r.entitat}-resposta`}
                        />
                    </AccordionDetails>
                </Accordion>
            ))}
        </Box>
    );
};

type AuditoriaDetallProps = {
    tipus: TipusAuditoria;
    id: string | number;
    /** Sense navegació (p. ex. un enllaç directe), no hi ha anterior ni següent. */
    navegacio?: NavegacioDetall;
    onClose: () => void;
};

/**
 * Finestra de detall d'una petició (spec §5.4): capçalera, missatges (petició i resposta un al costat
 * de l'altre, amb el visor XML), sol·licituds amb el seu detall, respostes de les entitats (enrutador
 * múltiple) i errors de comunicació amb el backoffice. Els missatges es carreguen en obrir la pestanya.
 * Amb `navegacio`, els botons «Anterior» i «Següent» recorren el resultat del llistat (mateix filtre i
 * ordre), consultant la petició de la posició anterior o següent.
 */
export const AuditoriaDetall: React.FC<AuditoriaDetallProps> = ({ tipus, id, navegacio, onClose }) => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const api = useApiAuditoria(tipus);
    const { temporalMessageShow } = useBaseAppContext();
    const { isReady, getOne, find } = useResourceApiService(RECURS_AUDITORIA[tipus]);
    const [estat, setEstat] = React.useState<{ peticio?: PeticioAuditoria; error?: string }>({});
    const [pestanya, setPestanya] = React.useState<Pestanya>();
    const [total, setTotal] = React.useState<number>();
    const [navegant, setNavegant] = React.useState(false);

    React.useEffect(() => {
        if (!isReady) {
            return;
        }
        let actiu = true;
        setEstat({});
        getOne(id)
            .then((peticio: PeticioAuditoria) => actiu && setEstat({ peticio }))
            .catch((error: unknown) => actiu && setEstat({ error: missatgeError(error) }));
        return () => {
            actiu = false;
        };
    }, [isReady, getOne, id]);

    // Total del llistat (per desactivar «Següent» a la darrera petició)
    React.useEffect(() => {
        if (!isReady || navegacio == null) {
            return;
        }
        find({ ...navegacio.findArgs, page: navegacio.posicio, size: 1 })
            .then(({ page }) => setTotal(page?.totalElements))
            .catch(() => setTotal(undefined));
        // Només en obrir: el total no canvia en navegar
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isReady]);

    const disponibles = pestanyesDisponibles(tipus, estat.peticio);
    const activa = pestanya != null && disponibles.includes(pestanya) ? pestanya : disponibles[0];

    const navegar = (desplacament: number) => {
        if (navegacio == null) {
            return;
        }
        const posicio = navegacio.posicio + desplacament;
        setNavegant(true);
        find({ ...navegacio.findArgs, page: posicio, size: 1 })
            .then(({ rows, page }) => {
                setTotal(page?.totalElements);
                if (rows.length > 0) {
                    navegacio.onNavegar(rows[0].id, posicio);
                } else {
                    temporalMessageShow(null, t('auditoria.detall.senseMes'), 'info');
                }
            })
            .catch((error: unknown) => temporalMessageShow(null, missatgeError(error), 'error'))
            .finally(() => setNavegant(false));
    };

    const missatge = (codi: string) => () => api.text(cami(id, 'missatges', codi));
    const nom = (sufix: string) => `${estat.peticio?.peticioId ?? id}-${sufix}`;

    const botonsNavegacio =
        navegacio != null ? (
            <Box sx={{ display: 'flex', gap: 1, mr: 'auto' }}>
                <Button
                    startIcon={<Icon>{icons.anterior}</Icon>}
                    disabled={navegant || navegacio.posicio <= 0}
                    onClick={() => navegar(-1)}
                >
                    {t('auditoria.detall.anterior')}
                </Button>
                <Button
                    endIcon={<Icon>{icons.seguent}</Icon>}
                    disabled={navegant || (total != null && navegacio.posicio >= total - 1)}
                    onClick={() => navegar(1)}
                >
                    {t('auditoria.detall.seguent')}
                </Button>
                {total != null && (
                    <Typography variant="body2" color="text.secondary" sx={{ alignSelf: 'center' }}>
                        {t('auditoria.detall.posicio', { posicio: navegacio.posicio + 1, total })}
                    </Typography>
                )}
            </Box>
        ) : undefined;

    return (
        <MuiDialog
            open
            title={t('auditoria.detall.titol', { peticioId: estat.peticio?.peticioId ?? String(id) })}
            buttons={[{ value: false, text: t('auditoria.detall.tancar'), componentProps: { variant: 'contained' } }]}
            buttonCallback={() => onClose()}
            closeCallback={() => onClose()}
            extraActions={botonsNavegacio}
            componentProps={{ maxWidth: 'xl', fullWidth: true, slotProps: { paper: { sx: { height: '90vh' } } } }}
        >
            <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%', minHeight: 0 }}>
                {estat.error && <Alert severity="error">{estat.error}</Alert>}
                {!estat.error && estat.peticio == null && <CircularProgress size={28} />}
                {estat.peticio != null && (
                    <>
                        <Capcalera
                            tipus={tipus}
                            peticio={estat.peticio}
                            onServei={(serveiId) => navigate(rutaServei(String(serveiId)))}
                        />
                        <Tabs
                            value={activa}
                            onChange={(_event, valor: Pestanya) => setPestanya(valor)}
                            variant="scrollable"
                            sx={{ mb: 1, borderBottom: 1, borderColor: 'divider', flexShrink: 0 }}
                        >
                            {disponibles.map((p) => (
                                <Tab key={p} value={p} label={t(`auditoria.detall.pestanya.${p}`)} />
                            ))}
                        </Tabs>
                        {/* Clau amb l'id: en navegar a una altra petició, els visors es tornen a carregar */}
                        <Box
                            key={String(id)}
                            sx={{
                                flex: 1,
                                minHeight: 280,
                                overflow: activa === 'solicituds' || activa === 'respostes' ? 'auto' : 'hidden',
                            }}
                        >
                            {activa === 'scsp' && (
                                <Parell>
                                    <XmlVisor
                                        titol={t('auditoria.missatge.peticioScsp')}
                                        carregar={missatge('PETICIO_SCSP')}
                                        nomFitxer={nom('peticio-scsp')}
                                    />
                                    <XmlVisor
                                        titol={t('auditoria.missatge.respostaScsp')}
                                        carregar={missatge('RESPOSTA_SCSP')}
                                        nomFitxer={nom('resposta-scsp')}
                                    />
                                </Parell>
                            )}
                            {activa === 'backoffice' && (
                                <Parell>
                                    <XmlVisor
                                        titol={t('auditoria.missatge.peticioBackoffice')}
                                        carregar={missatge('PETICIO_BACKOFFICE')}
                                        nomFitxer={nom('peticio-backoffice')}
                                    />
                                    <XmlVisor
                                        titol={t('auditoria.missatge.respostaBackoffice')}
                                        carregar={missatge('RESPOSTA_BACKOFFICE')}
                                        nomFitxer={nom('resposta-backoffice')}
                                    />
                                </Parell>
                            )}
                            {activa === 'errorComunicacio' && (
                                <Box sx={{ height: '100%', display: 'flex' }}>
                                    <XmlVisor
                                        titol={t('auditoria.missatge.errorComunicacio')}
                                        carregar={missatge('ERROR_COMUNICACIO')}
                                        nomFitxer={nom('error-comunicacio')}
                                    />
                                </Box>
                            )}
                            {activa === 'missatges' && (
                                <Parell>
                                    <XmlVisor
                                        titol={t('auditoria.missatge.peticio')}
                                        carregar={missatge('PETICIO')}
                                        nomFitxer={nom('peticio')}
                                    />
                                    <XmlVisor
                                        titol={t('auditoria.missatge.resposta')}
                                        carregar={missatge('RESPOSTA')}
                                        nomFitxer={nom('resposta')}
                                    />
                                </Parell>
                            )}
                            {activa === 'respostes' && <Respostes peticioId={id} />}
                            {activa === 'solicituds' && <Solicituds tipus={tipus} peticioId={id} />}
                        </Box>
                    </>
                )}
            </Box>
        </MuiDialog>
    );
};

export default AuditoriaDetall;
