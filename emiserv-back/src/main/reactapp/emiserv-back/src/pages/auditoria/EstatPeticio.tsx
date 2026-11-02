import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Popover from '@mui/material/Popover';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import { ESTAT_ASPECTE, ESTATS_PETICIO, type EstatPeticio as Estat } from './auditoriaModel';

/** Xip de l'estat d'una petició, amb el color i la icona de l'estat (DESCONEGUT si no en té). */
export const EstatXip: React.FC<{ estat?: Estat; sufix?: string; title?: string }> = ({ estat, sufix, title }) => {
    const { t } = useTranslation();
    const e: Estat = estat && ESTAT_ASPECTE[estat] ? estat : 'DESCONEGUT';
    const xip = (
        <Chip
            size="small"
            variant="outlined"
            color={ESTAT_ASPECTE[e].color}
            icon={<Icon fontSize="small">{ESTAT_ASPECTE[e].icona}</Icon>}
            label={t(`auditoria.estat.${e}`) + (sufix ? ` · ${sufix}` : '')}
        />
    );
    return title ? (
        <Tooltip title={<Box sx={{ whiteSpace: 'pre-wrap', maxHeight: 300, overflow: 'auto' }}>{title}</Box>}>
            {xip}
        </Tooltip>
    ) : (
        xip
    );
};

/** Botó de la barra de títol que mostra la llegenda dels estats. */
export const EstatLlegenda: React.FC = () => {
    const { t } = useTranslation();
    const [anchor, setAnchor] = React.useState<HTMLElement | null>(null);
    return (
        <>
            <Tooltip title={t('auditoria.llegenda.titol')}>
                <IconButton
                    aria-label={t('auditoria.llegenda.titol')}
                    onClick={(event) => setAnchor(event.currentTarget)}
                >
                    <Icon>help_outline</Icon>
                </IconButton>
            </Tooltip>
            <Popover
                open={anchor != null}
                anchorEl={anchor}
                onClose={() => setAnchor(null)}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                transformOrigin={{ vertical: 'top', horizontal: 'right' }}
            >
                <Box sx={{ p: 2, maxWidth: 420 }}>
                    <Typography variant="subtitle2" sx={{ mb: 1 }}>
                        {t('auditoria.llegenda.titol')}
                    </Typography>
                    {ESTATS_PETICIO.map((estat) => (
                        <Box key={estat} sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                            <Box sx={{ flex: '0 0 130px' }}>
                                <EstatXip estat={estat} />
                            </Box>
                            <Typography variant="body2" color="text.secondary">
                                {t(`auditoria.llegenda.${estat}`)}
                            </Typography>
                        </Box>
                    ))}
                </Box>
            </Popover>
        </>
    );
};
