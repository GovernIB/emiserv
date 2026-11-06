import React from 'react';
import { useTranslation } from 'react-i18next';
import { useLocation, useNavigate } from 'react-router-dom';
import Box from '@mui/material/Box';
import Icon from '@mui/material/Icon';
import InputAdornment from '@mui/material/InputAdornment';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import MenuItem from '@mui/material/MenuItem';
import TextField from '@mui/material/TextField';
import { useAuthButtonContext, useBaseAppContext } from 'reactlib';
import { icons } from '../lot2';
import { ROL_ADMIN, ROL_RESP, type Rol, useSessioUsuari } from './EmiservAuthProvider';

/** Icona de cada rol (selector i insígnia del botó d'usuari de la capçalera). */
// eslint-disable-next-line react-refresh/only-export-components
export const ICONES_ROL: Record<Rol, string> = {
    [ROL_ADMIN]: icons.rolAdmin,
    [ROL_RESP]: icons.rolUsuari,
};

/** Rutes que només veu l'administrador (com els menús de la JSP). */
const RUTES_ADMIN = ['/configuracio', '/informes'];

/**
 * Selector del rol actual al menú d'usuari de la capçalera, com el de la JSP i el de Notib. Només
 * apareix si l'usuari té els dos rols. El rol es desa a la sessió (el comparteix la JSP); si la
 * pantalla actual no és del nou rol, es torna a la de serveis.
 */
export const SelectorRol: React.FC = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const location = useLocation();
    const { usuari, canviarRol } = useSessioUsuari();
    const { temporalMessageShow } = useBaseAppContext();
    const { apiRef: authButtonApiRef } = useAuthButtonContext();
    const rols = (usuari?.rols ?? []).filter((r): r is Rol => r === ROL_ADMIN || r === ROL_RESP);
    if (rols.length < 2 || usuari?.rolActual == null) {
        return null;
    }
    const canviar = (rol: Rol) => {
        authButtonApiRef.current?.close();
        canviarRol(rol)
            .then(() => {
                if (rol !== ROL_ADMIN && RUTES_ADMIN.some((r) => location.pathname.startsWith(r))) {
                    navigate('/serveis', { replace: true });
                }
            })
            .catch(() => temporalMessageShow(null, t('rol.errorCanvi'), 'error'));
    };
    return (
        <Box sx={{ px: 2, pt: 1.5, pb: 1 }}>
            <TextField
                value={usuari.rolActual}
                onChange={(event) => canviar(event.target.value as Rol)}
                size="small"
                select
                label={t('rol.label')}
                fullWidth
                slotProps={{
                    input: {
                        startAdornment: (
                            <InputAdornment position="start">
                                <Icon fontSize="small">{ICONES_ROL[usuari.rolActual]}</Icon>
                            </InputAdornment>
                        ),
                    },
                    select: { renderValue: (valor: unknown) => t(`rol.${valor}`) },
                }}
            >
                {rols.map((rol) => (
                    <MenuItem key={rol} value={rol}>
                        <ListItemIcon>
                            <Icon fontSize="small">{ICONES_ROL[rol]}</Icon>
                        </ListItemIcon>
                        <ListItemText>{t(`rol.${rol}`)}</ListItemText>
                    </MenuItem>
                ))}
            </TextField>
        </Box>
    );
};

export default SelectorRol;
