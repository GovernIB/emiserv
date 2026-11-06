import React from 'react';
import { useTranslation } from 'react-i18next';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Checkbox from '@mui/material/Checkbox';
import Chip from '@mui/material/Chip';
import CircularProgress from '@mui/material/CircularProgress';
import FormControlLabel from '@mui/material/FormControlLabel';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import InputAdornment from '@mui/material/InputAdornment';
import List from '@mui/material/List';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import MenuItem from '@mui/material/MenuItem';
import Paper from '@mui/material/Paper';
import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import TextField from '@mui/material/TextField';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import { useBaseAppContext, useDebounce, useResourceApiContext } from 'reactlib';
import { icons, MAIN_GAP, ScreenFrame, titleBarControlsSx } from '../../lot2';
import {
    comptarPropietats,
    filtrarGrups,
    type GrupPropietats,
    type Propietat,
    trossosRessaltats,
} from './propietatsModel';

/** Crides a /api/configuracio/propietats (ConfiguracioPropietatsApiController), amb la galeta de sessió. */
const useApiPropietats = () => {
    const { apiUrl } = useResourceApiContext();
    return React.useMemo(() => {
        const url = `${apiUrl.replace(/\/+$/, '')}/configuracio/propietats`;
        return {
            grups: async (): Promise<GrupPropietats[]> => {
                const resposta = await fetch(url, { credentials: 'include', headers: { Accept: 'application/json' } });
                if (!resposta.ok) {
                    throw new Error(`HTTP ${resposta.status}`);
                }
                return resposta.json();
            },
            desar: async (key: string, value: string): Promise<string | undefined> => {
                const resposta = await fetch(url, {
                    method: 'POST',
                    credentials: 'include',
                    headers: { Accept: 'application/json', 'Content-Type': 'application/json' },
                    body: JSON.stringify({ key, value }),
                });
                const cos = await resposta.json().catch(() => ({}));
                if (!resposta.ok) {
                    throw new Error(cos?.message ?? `HTTP ${resposta.status}`);
                }
                return cos?.message;
            },
        };
    }, [apiUrl]);
};

/** Text amb les coincidències del cercador marcades (`<mark>`). */
const TextRessaltat: React.FC<{ text?: string | null; cerca?: string }> = ({ text, cerca }) => (
    <>
        {trossosRessaltats(text ?? '', cerca).map((tros, index) =>
            tros.coincident ? (
                <mark key={index}>{tros.text}</mark>
            ) : (
                <React.Fragment key={index}>{tros.text}</React.Fragment>
            )
        )}
    </>
);

/** Cercador de la barra de títol (alçada dels botons de la barra), com a Distribució. */
const Cercador: React.FC<{ onChange: (cerca: string) => void }> = ({ onChange }) => {
    const { t } = useTranslation();
    const [cerca, setCerca] = React.useState('');
    const cercaDebounced = useDebounce(cerca);
    React.useEffect(() => onChange(cercaDebounced ?? ''), [cercaDebounced, onChange]);
    return (
        <TextField
            value={cerca}
            onChange={(event) => setCerca(event.target.value)}
            size="small"
            placeholder={t('configuracio.propietats.cerca')}
            slotProps={{
                htmlInput: { 'aria-label': t('configuracio.propietats.cerca') },
                input: {
                    startAdornment: (
                        <InputAdornment position="start">
                            <Icon fontSize="small">{icons.cerca}</Icon>
                        </InputAdornment>
                    ),
                    endAdornment: cerca && (
                        <InputAdornment position="end">
                            <IconButton
                                size="small"
                                aria-label={t('configuracio.propietats.buidaCerca')}
                                onClick={() => setCerca('')}
                            >
                                <Icon fontSize="inherit">{icons.buida}</Icon>
                            </IconButton>
                        </InputAdornment>
                    ),
                },
            }}
        />
    );
};

/** Editor del valor segons el tipus, com configGroup.jsp. */
const EditorValor: React.FC<{ propietat: Propietat; valor: string; onChange: (valor: string) => void }> = ({
    propietat: p,
    valor,
    onChange,
}) => {
    const { t } = useTranslation();
    const disabled = p.nomesLectura;
    if (p.typeCode === 'BOOL') {
        return (
            <FormControlLabel
                control={
                    <Checkbox
                        checked={valor === 'true'}
                        disabled={disabled}
                        onChange={(e) => onChange(String(e.target.checked))}
                    />
                }
                label={valor === 'true' ? t('configuracio.propietats.si') : t('configuracio.propietats.no')}
            />
        );
    }
    if (p.validValues && p.validValues.length === 2) {
        return (
            <RadioGroup row value={valor} onChange={(e) => onChange(e.target.value)}>
                {p.validValues.map((v) => (
                    <FormControlLabel
                        key={v}
                        value={v}
                        control={<Radio size="small" />}
                        label={v}
                        disabled={disabled}
                    />
                ))}
            </RadioGroup>
        );
    }
    if (p.validValues && p.validValues.length > 2) {
        return (
            <TextField
                select
                size="small"
                fullWidth
                value={valor}
                disabled={disabled}
                onChange={(e) => onChange(e.target.value)}
            >
                {p.validValues.map((v) => (
                    <MenuItem key={v} value={v}>
                        {v}
                    </MenuItem>
                ))}
            </TextField>
        );
    }
    const numeric = p.typeCode === 'INT' || p.typeCode === 'FLOAT';
    return (
        <TextField
            size="small"
            fullWidth
            value={valor}
            disabled={disabled}
            type={p.contrasenya ? 'password' : numeric ? 'number' : 'text'}
            placeholder={p.contrasenya ? t('configuracio.propietats.contrasenyaSenseCanvis') : undefined}
            autoComplete={p.contrasenya ? 'new-password' : undefined}
            slotProps={{ htmlInput: { maxLength: 2048, ...(p.typeCode === 'FLOAT' ? { step: 0.01 } : {}) } }}
            onChange={(e) => onChange(e.target.value)}
        />
    );
};

/** Una propietat: descripció i clau (amb les coincidències marcades), editor i botó de desar. */
const FilaPropietat: React.FC<{ propietat: Propietat; cerca: string; onDesada: () => void }> = ({
    propietat: p,
    cerca,
    onDesada,
}) => {
    const { t } = useTranslation();
    const api = useApiPropietats();
    const { temporalMessageShow } = useBaseAppContext();
    const inicial = p.contrasenya ? '' : (p.value ?? '');
    const [valor, setValor] = React.useState(inicial);
    const [desant, setDesant] = React.useState(false);
    React.useEffect(() => setValor(inicial), [inicial]);
    const modificat = valor !== inicial;
    const desar = () => {
        setDesant(true);
        api.desar(p.key, valor)
            .then((missatge) => {
                temporalMessageShow(null, missatge ?? t('configuracio.propietats.desada'), 'success');
                onDesada();
            })
            .catch((error: Error) =>
                temporalMessageShow(t('configuracio.propietats.errorDesar'), error.message, 'error')
            )
            .finally(() => setDesant(false));
    };
    return (
        <Box
            component="li"
            sx={{
                listStyle: 'none',
                display: 'grid',
                gridTemplateColumns: { xs: '1fr', md: 'minmax(200px, 2fr) 3fr auto' },
                gap: 1.5,
                alignItems: 'center',
                px: 2,
                py: 1.25,
                borderBottom: 1,
                borderColor: 'divider',
            }}
        >
            <Box sx={{ minWidth: 0 }}>
                <Typography variant="body2" sx={{ wordBreak: 'break-word' }}>
                    <TextRessaltat text={p.descripcio ?? p.key} cerca={cerca} />
                    {p.requereixReinici && (
                        <Tooltip title={t('configuracio.propietats.requereixReinici')}>
                            <Icon
                                fontSize="small"
                                color="warning"
                                aria-label={t('configuracio.propietats.requereixReinici')}
                                sx={{ verticalAlign: 'middle', ml: 0.5 }}
                            >
                                restart_alt
                            </Icon>
                        </Tooltip>
                    )}
                    {p.nomesLectura && (
                        <Tooltip title={t('configuracio.propietats.fitxer')}>
                            <Icon
                                fontSize="small"
                                color="disabled"
                                aria-label={t('configuracio.propietats.fitxer')}
                                sx={{ verticalAlign: 'middle', ml: 0.5 }}
                            >
                                lock
                            </Icon>
                        </Tooltip>
                    )}
                </Typography>
                <Typography
                    variant="caption"
                    color="text.secondary"
                    component="div"
                    sx={{ fontFamily: 'monospace', wordBreak: 'break-all' }}
                >
                    <TextRessaltat text={p.key} cerca={cerca} />
                </Typography>
            </Box>
            <Box sx={{ minWidth: 0 }}>
                <EditorValor propietat={p} valor={valor} onChange={setValor} />
            </Box>
            <Box sx={{ justifySelf: { xs: 'end', md: 'auto' }, width: 40 }}>
                {!p.nomesLectura && (
                    <Tooltip title={t('configuracio.propietats.desar')}>
                        <span>
                            <IconButton
                                color="primary"
                                aria-label={`${t('configuracio.propietats.desar')} ${p.key}`}
                                disabled={!modificat || desant}
                                onClick={desar}
                            >
                                <Icon>{icons.desa}</Icon>
                            </IconButton>
                        </span>
                    </Tooltip>
                )}
            </Box>
        </Box>
    );
};

/** Propietats d'un grup i, a sota, les dels subgrups amb el seu subtítol. */
const PropietatsGrup: React.FC<{ grup: GrupPropietats; cerca: string; nivell: number; onDesada: () => void }> = ({
    grup,
    cerca,
    nivell,
    onDesada,
}) => (
    <>
        {nivell > 0 && (
            <Typography
                variant="subtitle2"
                component="h3"
                sx={{ px: 2, py: 1, bgcolor: 'action.hover', borderBottom: 1, borderColor: 'divider' }}
            >
                <TextRessaltat text={grup.descripcio ?? grup.key} cerca={cerca} />
            </Typography>
        )}
        <Box component="ul" sx={{ m: 0, p: 0 }}>
            {grup.propietats.map((p) => (
                <FilaPropietat key={p.key} propietat={p} cerca={cerca} onDesada={onDesada} />
            ))}
        </Box>
        {grup.grups.map((g) => (
            <PropietatsGrup key={g.key} grup={g} cerca={cerca} nivell={nivell + 1} onDesada={onDesada} />
        ))}
    </>
);

/**
 * Propietats de configuració de l'aplicació (config.jsp), amb la disposició de la resta
 * d'aplicacions (Distribució, Notib, Ripea, Comanda): barra de títol de la pantalla amb el cercador;
 * a l'esquerra els grups i a la dreta les propietats del grup triat, amb la capçalera del grup.
 *
 * El cercador filtra per descripció, clau o valor (mai el d'una contrasenya): només queden els grups
 * amb alguna propietat que coincideix, amb el nombre de coincidències, i el text coincident es marca.
 * Les propietats que requereixen reiniciar l'aplicació porten una icona; les definides al fitxer no
 * es poden modificar; les contrasenyes no es mostren mai (deixar-les buides no les canvia).
 */
export const PropietatsConfiguracio: React.FC = () => {
    const { t } = useTranslation();
    const api = useApiPropietats();
    const [estat, setEstat] = React.useState<{ grups?: GrupPropietats[]; error?: string }>({});
    const [cerca, setCerca] = React.useState('');
    const [grupActiu, setGrupActiu] = React.useState<string>();
    const carregar = React.useCallback(() => {
        api.grups()
            .then((grups) => setEstat({ grups }))
            .catch((error: Error) => setEstat({ error: error.message }));
    }, [api]);
    React.useEffect(() => carregar(), [carregar]);
    const grups = React.useMemo(() => filtrarGrups(estat.grups ?? [], cerca), [estat.grups, cerca]);
    // Si el grup triat no té coincidències, es mostra el primer que en té
    const actiu = grups.find((g) => g.key === grupActiu) ?? grups[0];
    return (
        <ScreenFrame
            icon="settings_applications"
            title={t('configuracio.propietats.titol')}
            actions={
                <Box sx={titleBarControlsSx}>
                    <Cercador onChange={setCerca} />
                </Box>
            }
            padded
        >
            <Alert severity="info" icon={<Icon>restart_alt</Icon>} sx={{ flex: '0 0 auto' }}>
                {t('configuracio.propietats.avisReinici')}
            </Alert>
            {estat.error && (
                <Alert severity="error">{t('configuracio.propietats.errorCarregar', { error: estat.error })}</Alert>
            )}
            {!estat.error && estat.grups == null && <CircularProgress size={28} />}
            {estat.grups != null && grups.length === 0 && (
                <Box sx={{ textAlign: 'center', py: 4 }}>
                    <Icon fontSize="large" color="disabled">
                        {icons.senseDades}
                    </Icon>
                    <Typography variant="h6" color="text.secondary">
                        {t('configuracio.propietats.senseResultats')}
                    </Typography>
                </Box>
            )}
            {actiu && (
                <Box
                    sx={{
                        display: 'flex',
                        flexDirection: { xs: 'column', md: 'row' },
                        gap: MAIN_GAP + 'px',
                        flex: '1 1 0',
                        minHeight: 300,
                    }}
                >
                    <Paper variant="outlined" sx={{ flex: { md: '0 0 25%' }, minWidth: { md: 220 }, overflow: 'auto' }}>
                        <List dense aria-label={t('configuracio.propietats.grups')}>
                            {grups.map((g) => (
                                <ListItemButton
                                    key={g.key}
                                    selected={g.key === actiu.key}
                                    onClick={() => setGrupActiu(g.key)}
                                >
                                    <ListItemText
                                        primary={<TextRessaltat text={g.descripcio ?? g.key} cerca={cerca} />}
                                    />
                                    {cerca.trim() && <Chip size="small" label={comptarPropietats(g)} />}
                                </ListItemButton>
                            ))}
                        </List>
                    </Paper>
                    <Paper
                        variant="outlined"
                        sx={{
                            flex: '1 1 0',
                            minWidth: 0,
                            display: 'flex',
                            flexDirection: 'column',
                            overflow: 'hidden',
                        }}
                    >
                        {/* Capçalera del grup: penja del títol de la pantalla (h1) */}
                        <Typography
                            variant="h6"
                            component="h2"
                            sx={{
                                px: 2,
                                py: 1,
                                color: 'primary.contrastText',
                                bgcolor: 'primary.main',
                                flex: '0 0 auto',
                            }}
                        >
                            {actiu.descripcio ?? actiu.key}
                        </Typography>
                        <Box sx={{ flex: '1 1 0', minHeight: 0, overflow: 'auto' }}>
                            <PropietatsGrup key={actiu.key} grup={actiu} cerca={cerca} nivell={0} onDesada={carregar} />
                        </Box>
                    </Paper>
                </Box>
            )}
        </ScreenFrame>
    );
};

export default PropietatsConfiguracio;
