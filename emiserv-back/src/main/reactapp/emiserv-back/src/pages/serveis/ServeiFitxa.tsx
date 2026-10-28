import React from 'react';
import { Link, useParams, useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Icon from '@mui/material/Icon';
import Typography from '@mui/material/Typography';
import { useResourceApiService } from 'reactlib';
import { icons, ScreenFrame } from '../../lot2';
import { emiservIcons } from '../../components/EmiservBaseApp';
import { useEsAdmin } from '../../components/EmiservAuthProvider';
import DiagnosticAvis from './DiagnosticAvis';
import type { PestanyaFitxa, Servei } from './serveiModel';

const PESTANYES: PestanyaFitxa[] = ['configScsp', 'rutes'];

const Camp: React.FC<React.PropsWithChildren<{ etiqueta: string }>> = ({ etiqueta, children }) => (
    <>
        <Typography component="dt" variant="body2" color="text.secondary">
            {etiqueta}
        </Typography>
        <Typography component="dd" sx={{ m: 0 }}>
            {children}
        </Typography>
    </>
);

/**
 * Fitxa d'un servei, provisional i de només lectura. La fitxa completa (amb les pestanyes de
 * configuració SCSP, rutes...) és de la fase 2 (spec §5.2); mentrestant aquesta ruta existeix perquè
 * el clic de la fila i l'avís de diagnòstic del llistat no acabin en «pàgina no trobada». Si arriba
 * amb `?pestanya=...`, ho indica.
 */
export const ServeiFitxa: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    const [searchParams] = useSearchParams();
    const esAdmin = useEsAdmin();
    const { isReady, getOne } = useResourceApiService('servei');
    const [servei, setServei] = React.useState<Servei>();
    const [error, setError] = React.useState(false);
    const pestanyaParam = searchParams.get('pestanya');
    const pestanya = PESTANYES.find((p) => p === pestanyaParam);

    React.useEffect(() => {
        if (isReady && id != null) {
            setError(false);
            getOne(id)
                .then((resposta: Servei) => setServei(resposta))
                .catch(() => setError(true));
        }
    }, [isReady, getOne, id]);

    const si = (valor?: boolean) => (valor ? t('serveis.fitxa.si') : t('serveis.fitxa.no'));

    return (
        <ScreenFrame
            icon={emiservIcons.servei}
            title={t('serveis.fitxa.titol', { codi: servei?.codi ?? '' })}
            actions={
                <Button
                    component={Link}
                    to="/serveis"
                    variant="outlined"
                    size="small"
                    startIcon={<Icon>arrow_back</Icon>}
                >
                    {t('serveis.fitxa.tornar')}
                </Button>
            }
            padded
            fitContent
        >
            <Alert severity="info" icon={<Icon>{icons.detall}</Icon>}>
                {t('serveis.fitxa.avis')}
                {pestanya != null && (
                    <Box component="span" sx={{ display: 'block', mt: 0.5, fontWeight: 600 }}>
                        {t('serveis.fitxa.pestanya', { pestanya: t(`serveis.fitxa.pestanyes.${pestanya}`) })}
                    </Box>
                )}
            </Alert>
            {error && <Alert severity="error">{t('serveis.fitxa.errorCarrega')}</Alert>}
            {servei != null && (
                <Box
                    component="dl"
                    sx={{ display: 'grid', gridTemplateColumns: 'max-content 1fr', columnGap: 3, rowGap: 1, m: 0 }}
                >
                    <Camp etiqueta={t('serveis.columna.codi')}>
                        <Box component="span" sx={{ display: 'inline-flex', alignItems: 'center' }}>
                            {servei.codi}
                            <DiagnosticAvis id={servei.id} motius={servei.diagnostic} />
                        </Box>
                    </Camp>
                    <Camp etiqueta={t('serveis.columna.nom')}>{servei.nom}</Camp>
                    <Camp etiqueta={t('serveis.columna.tipus')}>{t(`serveis.tipus.${servei.tipus}`)}</Camp>
                    <Camp etiqueta={t('serveis.fitxa.configuratScsp')}>{si(servei.configuratScsp)}</Camp>
                    {esAdmin && (
                        <>
                            <Camp etiqueta={t('serveis.columna.actiu')}>
                                <Chip
                                    size="small"
                                    color={servei.actiu ? 'success' : 'default'}
                                    label={servei.actiu ? t('serveis.estat.actiu') : t('serveis.estat.inactiu')}
                                />
                            </Camp>
                            <Camp etiqueta={t('serveis.columna.permisos')}>{servei.permisosCount ?? 0}</Camp>
                            <Camp etiqueta={t('serveis.columna.rutes')}>{servei.rutesCount ?? 0}</Camp>
                        </>
                    )}
                </Box>
            )}
        </ScreenFrame>
    );
};

export default ServeiFitxa;
