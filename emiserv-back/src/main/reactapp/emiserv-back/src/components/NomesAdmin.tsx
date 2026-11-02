import React from 'react';
import { useTranslation } from 'react-i18next';
import Alert from '@mui/material/Alert';
import { ScreenFrame } from '../lot2';
import { useEsAdmin } from './EmiservAuthProvider';

/**
 * Pantalles només de l'administrador (configuració i informes, com el menú de la JSP): amb el rol de
 * responsable (o sense el d'administrador) s'hi mostra un avís en lloc de la pantalla. El servidor
 * també ho comprova.
 */
export const NomesAdmin: React.FC<React.PropsWithChildren> = ({ children }) => {
    const { t } = useTranslation();
    const esAdmin = useEsAdmin();
    if (!esAdmin) {
        return (
            <ScreenFrame padded fitContent>
                <Alert severity="warning">{t('rol.nomesAdmin')}</Alert>
            </ScreenFrame>
        );
    }
    return <>{children}</>;
};

export default NomesAdmin;
