import React from 'react';
import { useTranslation } from 'react-i18next';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import FormControl from '@mui/material/FormControl';
import FormHelperText from '@mui/material/FormHelperText';
import FormLabel from '@mui/material/FormLabel';
import Icon from '@mui/material/Icon';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import MenuItem from '@mui/material/MenuItem';
import Stack from '@mui/material/Stack';
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import Typography from '@mui/material/Typography';
import { type Idioma, type Interficie, useSessioUsuari } from './EmiservAuthProvider';
import { type Tema, TEMES, useSetTema } from './TemaProvider';

/** Valor del selector per a "sense preferència" (la interfície per defecte de l'aplicació). */
const PER_DEFECTE = 'DEFECTE';
type ValorInterficie = Interficie | typeof PER_DEFECTE;

/** Opció "Perfil" del menú d'usuari de la capçalera. */
export const UserProfileMenuItem: React.FC<{ onClick: () => void }> = ({ onClick }) => {
    const { t } = useTranslation();
    return (
        <MenuItem onClick={onClick}>
            <ListItemIcon>
                <Icon fontSize="small">account_circle</Icon>
            </ListItemIcon>
            <ListItemText>{t('perfil.menu')}</ListItemText>
        </MenuItem>
    );
};

const Dada: React.FC<{ etiqueta: string; valor?: string }> = ({ etiqueta, valor }) => (
    <Stack direction="row" spacing={1}>
        <Typography variant="body2" color="text.secondary" sx={{ minWidth: '9em' }}>
            {etiqueta}
        </Typography>
        <Typography variant="body2">{valor || '-'}</Typography>
    </Stack>
);

/** Grup d'opcions del perfil (etiqueta, botons i ajuda), com el de la interfície. */
const Opcions = <T extends string>({
    id,
    etiqueta,
    ajuda,
    valor,
    opcions,
    disabled,
    onChange,
}: {
    id: string;
    etiqueta: string;
    ajuda?: string;
    valor: T;
    opcions: { valor: T; text: string; icona?: string }[];
    disabled: boolean;
    onChange: (valor: T) => void;
}) => (
    <FormControl fullWidth>
        <FormLabel id={id} sx={{ mb: 1 }}>
            {etiqueta}
        </FormLabel>
        <ToggleButtonGroup
            aria-labelledby={id}
            value={valor}
            exclusive
            size="small"
            disabled={disabled}
            onChange={(_e, nou: T | null) => nou != null && onChange(nou)}
            sx={{ display: 'flex', flexWrap: 'wrap' }}
        >
            {opcions.map((o) => (
                <ToggleButton key={o.valor} value={o.valor} sx={{ flex: 1, gap: 0.75, textTransform: 'none' }}>
                    {o.icona && <Icon fontSize="small">{o.icona}</Icon>}
                    {o.text}
                </ToggleButton>
            ))}
        </ToggleButtonGroup>
        {ajuda && <FormHelperText>{ajuda}</FormHelperText>}
    </FormControl>
);

const ICONES_TEMA: Record<Tema, string> = {
    CLAR: 'light_mode',
    OBSCUR: 'dark_mode',
    DRACULA: 'auto_awesome',
    SISTEMA: 'settings_brightness',
};

/**
 * Perfil de l'usuari, com a les altres aplicacions (Distribució, Notib, Ripea, Comanda): les dades de
 * la sessió i les preferències:
 * <ul>
 *   <li>idioma (català o castellà): el mateix del perfil de la JSP; s'aplica en desar;</li>
 *   <li>tema (clar, obscur, Dracula o el del sistema): es previsualitza mentre s'edita i, si es
 *   cancel·la, torna el desat;</li>
 *   <li>interfície preferida (JSP o REACT), amb `PUT api/sessioUsuari/interficie`.</li>
 * </ul>
 * L'idioma i el tema es desen amb `PUT api/sessioUsuari/preferencies`.
 */
export const UserProfileDialog: React.FC<{ open: boolean; onClose: () => void }> = ({ open, onClose }) => {
    const { t, i18n } = useTranslation();
    const { usuari, desarInterficiePreferida, desarPreferencies } = useSessioUsuari();
    const setTema = useSetTema();
    const desada: ValorInterficie = usuari?.interficiePreferida ?? PER_DEFECTE;
    // Sense idioma al perfil, el que fa servir ara l'aplicació (el del navegador)
    const idiomaDesat: Idioma = usuari?.idioma ?? (i18n.language?.startsWith('es') ? 'es' : 'ca');
    const temaDesat: Tema = usuari?.tema ?? 'SISTEMA';
    const [valor, setValor] = React.useState<ValorInterficie>(desada);
    const [idioma, setIdioma] = React.useState<Idioma>(idiomaDesat);
    const [tema, setTemaValor] = React.useState<Tema>(temaDesat);
    const [desant, setDesant] = React.useState(false);
    const [error, setError] = React.useState(false);

    React.useEffect(() => {
        if (open) {
            setValor(desada);
            setIdioma(idiomaDesat);
            setTemaValor(temaDesat);
            setError(false);
        }
        // Només en obrir el diàleg
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [open]);

    // Previsualització del tema mentre el diàleg és obert
    React.useEffect(() => {
        if (open) {
            setTema(tema);
        }
    }, [open, tema, setTema]);

    const tancar = () => {
        // Cancel·lar descarta la previsualització
        setTema(usuari?.tema ?? null);
        onClose();
    };

    const canviPreferencies = idioma !== idiomaDesat || tema !== temaDesat;
    const canviInterficie = valor !== desada;

    const handleDesar = () => {
        setDesant(true);
        setError(false);
        Promise.resolve()
            .then(() => (canviInterficie ? desarInterficiePreferida(valor === PER_DEFECTE ? null : valor) : undefined))
            .then(() => (canviPreferencies ? desarPreferencies({ idioma, tema }) : undefined))
            .then(onClose)
            .catch((e) => {
                console.error(e);
                setError(true);
            })
            .finally(() => setDesant(false));
    };

    return (
        <Dialog open={open} onClose={desant ? undefined : tancar} fullWidth maxWidth="sm">
            <DialogTitle>{t('perfil.titol')}</DialogTitle>
            <DialogContent>
                <Stack spacing={1} sx={{ mb: 3 }}>
                    <Dada etiqueta={t('perfil.codi')} valor={usuari?.codi} />
                    <Dada etiqueta={t('perfil.nom')} valor={usuari?.nom} />
                    <Dada etiqueta={t('perfil.email')} valor={usuari?.email} />
                    <Dada etiqueta={t('perfil.rols')} valor={usuari?.rols.join(', ')} />
                </Stack>
                <Stack spacing={3}>
                    <Opcions<Idioma>
                        id="perfil-idioma-label"
                        etiqueta={t('perfil.idioma.label')}
                        ajuda={t('perfil.idioma.ajuda')}
                        valor={idioma}
                        opcions={[
                            { valor: 'ca', text: 'Català' },
                            { valor: 'es', text: 'Castellano' },
                        ]}
                        disabled={desant}
                        onChange={setIdioma}
                    />
                    <Opcions<Tema>
                        id="perfil-tema-label"
                        etiqueta={t('perfil.tema.label')}
                        valor={tema}
                        opcions={TEMES.map((tm) => ({
                            valor: tm,
                            text: t(`perfil.tema.${tm}`),
                            icona: ICONES_TEMA[tm],
                        }))}
                        disabled={desant}
                        onChange={setTemaValor}
                    />
                    <Opcions<ValorInterficie>
                        id="perfil-interficie-label"
                        etiqueta={t('perfil.interficie.label')}
                        ajuda={t('perfil.interficie.ajuda')}
                        valor={valor}
                        opcions={[
                            { valor: PER_DEFECTE, text: t('perfil.interficie.defecte') },
                            { valor: 'JSP', text: t('perfil.interficie.jsp') },
                            { valor: 'REACT', text: t('perfil.interficie.react') },
                        ]}
                        disabled={desant}
                        onChange={setValor}
                    />
                </Stack>
                {error && (
                    <Alert severity="error" sx={{ mt: 2 }}>
                        {t('perfil.errorDesar')}
                    </Alert>
                )}
            </DialogContent>
            <DialogActions>
                <Button color="inherit" onClick={tancar} disabled={desant}>
                    {t('perfil.cancelar')}
                </Button>
                <Button
                    variant="contained"
                    onClick={handleDesar}
                    disabled={desant || (!canviInterficie && !canviPreferencies)}
                >
                    {t('perfil.desar')}
                </Button>
            </DialogActions>
        </Dialog>
    );
};
