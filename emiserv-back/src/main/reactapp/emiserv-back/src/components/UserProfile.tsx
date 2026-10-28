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
import { type Interficie, useSessioUsuari } from './EmiservAuthProvider';

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

/**
 * Perfil de l'usuari: les dades de la sessió i el selector de la interfície preferida, que es desa
 * amb `PUT api/sessioUsuari/interficie` (la mateixa preferència que el perfil de la JSP).
 */
export const UserProfileDialog: React.FC<{ open: boolean; onClose: () => void }> = ({ open, onClose }) => {
    const { t } = useTranslation();
    const { usuari, desarInterficiePreferida } = useSessioUsuari();
    const desada: ValorInterficie = usuari?.interficiePreferida ?? PER_DEFECTE;
    const [valor, setValor] = React.useState<ValorInterficie>(desada);
    const [desant, setDesant] = React.useState(false);
    const [error, setError] = React.useState(false);

    React.useEffect(() => {
        if (open) {
            setValor(desada);
            setError(false);
        }
    }, [open, desada]);

    const handleDesar = () => {
        setDesant(true);
        setError(false);
        desarInterficiePreferida(valor === PER_DEFECTE ? null : valor)
            .then(onClose)
            .catch((e) => {
                console.error(e);
                setError(true);
            })
            .finally(() => setDesant(false));
    };

    return (
        <Dialog open={open} onClose={desant ? undefined : onClose} fullWidth maxWidth="sm">
            <DialogTitle>{t('perfil.titol')}</DialogTitle>
            <DialogContent>
                <Stack spacing={1} sx={{ mb: 3 }}>
                    <Dada etiqueta={t('perfil.codi')} valor={usuari?.codi} />
                    <Dada etiqueta={t('perfil.nom')} valor={usuari?.nom} />
                    <Dada etiqueta={t('perfil.email')} valor={usuari?.email} />
                    <Dada etiqueta={t('perfil.rols')} valor={usuari?.rols.join(', ')} />
                </Stack>
                <FormControl fullWidth>
                    <FormLabel id="perfil-interficie-label" sx={{ mb: 1 }}>
                        {t('perfil.interficie.label')}
                    </FormLabel>
                    <ToggleButtonGroup
                        aria-labelledby="perfil-interficie-label"
                        value={valor}
                        exclusive
                        size="small"
                        disabled={desant}
                        onChange={(_e, nou: ValorInterficie | null) => nou != null && setValor(nou)}
                        sx={{ display: 'flex' }}
                    >
                        <ToggleButton value={PER_DEFECTE} sx={{ flex: 1 }}>
                            {t('perfil.interficie.defecte')}
                        </ToggleButton>
                        <ToggleButton value="JSP" sx={{ flex: 1 }}>
                            {t('perfil.interficie.jsp')}
                        </ToggleButton>
                        <ToggleButton value="REACT" sx={{ flex: 1 }}>
                            {t('perfil.interficie.react')}
                        </ToggleButton>
                    </ToggleButtonGroup>
                    <FormHelperText>{t('perfil.interficie.ajuda')}</FormHelperText>
                </FormControl>
                {error && (
                    <Alert severity="error" sx={{ mt: 2 }}>
                        {t('perfil.errorDesar')}
                    </Alert>
                )}
            </DialogContent>
            <DialogActions>
                <Button color="inherit" onClick={onClose} disabled={desant}>
                    {t('perfil.cancelar')}
                </Button>
                <Button variant="contained" onClick={handleDesar} disabled={desant || valor === desada}>
                    {t('perfil.desar')}
                </Button>
            </DialogActions>
        </Dialog>
    );
};
