import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import { icons } from '../../lot2';
import { type MotiuDiagnostic, rutaServeiDiagnostic } from './serveiModel';

type DiagnosticAvisProps = {
    /** Id del servei. */
    id: number | string;
    /** Motius del diagnòstic; sense cap motiu no es pinta res. */
    motius?: MotiuDiagnostic[];
};

/**
 * Avís de «servei no configurat» (millora 2 de la spec §5.1): icona d'advertència amb un tooltip que
 * llista els motius traduïts. En clicar-la, obre la finestra del servei a la pestanya del primer motiu.
 */
export const DiagnosticAvis: React.FC<DiagnosticAvisProps> = ({ id, motius }) => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    if (!motius?.length) {
        return null;
    }
    const textos = motius.map((motiu) => t(`serveis.diagnostic.motiu.${motiu}`));
    return (
        <Tooltip
            title={
                <Box>
                    <Typography variant="body2" sx={{ fontWeight: 600 }}>
                        {t('serveis.diagnostic.titol')}
                    </Typography>
                    <Box component="ul" sx={{ m: 0, pl: 2 }}>
                        {textos.map((text) => (
                            <li key={text}>{text}</li>
                        ))}
                    </Box>
                    <Typography variant="caption">{t('serveis.diagnostic.accio')}</Typography>
                </Box>
            }
        >
            <IconButton
                size="small"
                color="warning"
                aria-label={t('serveis.diagnostic.titol') + ' ' + textos.join(', ')}
                onClick={(event) => {
                    // No ha d'arribar al clic de la fila, que obre la finestra a la pestanya de dades.
                    event.stopPropagation();
                    navigate(rutaServeiDiagnostic(id, motius));
                }}
                sx={{ ml: 0.5 }}
            >
                <Icon fontSize="small">{icons.advertencia}</Icon>
            </IconButton>
        </Tooltip>
    );
};

export default DiagnosticAvis;
