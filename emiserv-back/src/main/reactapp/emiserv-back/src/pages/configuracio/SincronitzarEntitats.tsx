import React from 'react';
import { useTranslation } from 'react-i18next';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import { useBaseAppContext, useResourceApiContext } from 'reactlib';
import { icons } from '../../lot2';

/** Botó «Sincronitzar» de les entitats (amb PINBAL), com la JSP. */
export const SincronitzarEntitats: React.FC<{ refrescar: () => void }> = ({ refrescar }) => {
    const { t } = useTranslation();
    const { apiUrl } = useResourceApiContext();
    const { temporalMessageShow } = useBaseAppContext();
    const [executant, setExecutant] = React.useState(false);
    const sincronitzar = () => {
        setExecutant(true);
        fetch(`${apiUrl.replace(/\/+$/, '')}/entitat/sincronitzar`, {
            method: 'POST',
            credentials: 'include',
            headers: { Accept: 'application/json' },
        })
            .then(async (resposta) => {
                const cos = await resposta.json().catch(() => ({}));
                if (resposta.ok) {
                    refrescar();
                    temporalMessageShow(null, cos?.message ?? t('configuracio.entitat.sincronitzarOk'), 'success');
                } else {
                    temporalMessageShow(t('configuracio.entitat.sincronitzarError'), cos?.message ?? '', 'error');
                }
            })
            .catch(() => temporalMessageShow(null, t('configuracio.entitat.sincronitzarError'), 'error'))
            .finally(() => setExecutant(false));
    };
    return (
        <Tooltip title={t('configuracio.entitat.sincronitzar')}>
            <span>
                <IconButton
                    aria-label={t('configuracio.entitat.sincronitzar')}
                    disabled={executant}
                    onClick={sincronitzar}
                >
                    <Icon>{icons.sincronitza}</Icon>
                </IconButton>
            </span>
        </Tooltip>
    );
};

export default SincronitzarEntitats;
