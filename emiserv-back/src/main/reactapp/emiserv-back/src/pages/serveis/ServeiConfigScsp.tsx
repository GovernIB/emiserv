import React from 'react';
import { useTranslation } from 'react-i18next';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Grid from '@mui/material/Grid';
import Icon from '@mui/material/Icon';
import Typography from '@mui/material/Typography';
import { FormField, MuiForm, useBaseAppContext, useFormApiRef, useFormContext } from 'reactlib';
import { icons } from '../../lot2';
import { missatgeError } from '../../util/missatgeError';

/** Nom del recurs de l'API (ServeiConfigScspApiController). */
const RECURS = 'serveiConfigScsp';

/** Títol d'una secció del formulari (els `legend` de serveiConfigScsp.jsp). */
const Seccio: React.FC<{ titol: string }> = ({ titol }) => (
    <Grid size={12}>
        <Typography
            variant="subtitle1"
            component="h3"
            sx={{ fontWeight: 600, mt: 1, borderBottom: 1, borderColor: 'divider' }}
        >
            {titol}
        </Typography>
    </Grid>
);

/** Camps del formulari, amb les seccions i l'ordre de la JSP. */
const CampsConfigScsp: React.FC = () => {
    const { t } = useTranslation();
    const { data } = useFormContext();
    const camp = (name: string, md = 6, props: Record<string, unknown> = {}) => (
        <Grid size={{ xs: 12, md }}>
            <FormField name={name} label={t(`serveis.scsp.camp.${name}`)} {...props} />
        </Grid>
    );
    return (
        <Grid container spacing={2} sx={{ pt: 1 }}>
            {data?.configurat === false && (
                <Grid size={12}>
                    <Alert severity="info">{t('serveis.scsp.senseConfiguracio')}</Alert>
                </Grid>
            )}
            {camp('codigoCertificado', 6, { disabled: true })}
            {camp('emisorId', 6, { required: true })}
            <Seccio titol={t('serveis.scsp.seccio.validesa')} />
            {camp('fechaAlta', 4, { required: true })}
            {camp('fechaBaja', 4)}
            {camp('caducidad', 4)}
            <Seccio titol={t('serveis.scsp.seccio.ubicacio')} />
            {camp('urlSincrona', 12)}
            {camp('urlAsincrona', 12)}
            {camp('actionSincrona', 4)}
            {camp('actionAsincrona', 4)}
            {camp('actionSolicitud', 4)}
            <Seccio titol={t('serveis.scsp.seccio.xsd')} />
            {camp('xsdGestioActiva', 6)}
            {camp('versionEsquema', 6, { required: true })}
            {/* Com la JSP: amb la gestió XSD activa, l'esquema el calcula l'aplicació */}
            {camp('esquemas', 12, { disabled: data?.xsdGestioActiva === true })}
            <Seccio titol={t('serveis.scsp.seccio.xifrat')} />
            {camp('tipoSeguridad', 4, { required: true })}
            {camp('claveFirma', 4, { required: true })}
            {camp('claveCifrado', 4)}
            {camp('xpathCifradoSincrono', 6)}
            {camp('xpathCifradoAsincrono', 6)}
            {camp('algoritmoCifrado', 6)}
            {camp('validacionFirma', 6)}
            <Seccio titol={t('serveis.scsp.seccio.enviaments')} />
            {camp('prefijoPeticion', 4)}
            {camp('prefijoIdTransmision', 4)}
            {camp('timeout', 4, { required: true })}
            {camp('numeroMaximoReenvios', 6, { required: true })}
            {camp('maxSolicitudesPeticion', 6, { required: true })}
            {camp('xpathCodigoError', 6)}
            {camp('xpathLiteralError', 6)}
        </Grid>
    );
};

type ServeiConfigScspProps = {
    serveiId: number;
    /** La configuració s'ha desat (canvien el diagnòstic i la disponibilitat de les autoritzacions). */
    onDesat: () => void;
};

/**
 * Pestanya «SCSP» de la finestra del servei: la configuració SCSP del servei (serveiConfigScsp.jsp),
 * per a l'administrador i el responsable, com a la JSP. Un servei sense configuració mostra els
 * valors per defecte i es crea en desar. Els errors de validació els mostra el formulari.
 */
export const ServeiConfigScsp: React.FC<ServeiConfigScspProps> = ({ serveiId, onDesat }) => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const formApiRef = useFormApiRef();
    const [desant, setDesant] = React.useState(false);
    const desar = () => {
        setDesant(true);
        formApiRef.current
            ?.save()
            .then(() => {
                temporalMessageShow(null, t('serveis.scsp.desat'), 'success');
                onDesat();
            })
            .catch((error: unknown) => {
                // Els errors de validació ja es mostren als camps
                const e = error as { status?: number } | undefined;
                if (e?.status !== 422 && e?.status !== 400) {
                    temporalMessageShow(t('serveis.scsp.errorDesar'), missatgeError(error), 'error');
                }
            })
            .finally(() => setDesant(false));
    };
    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%', minHeight: 0 }}>
            <Box sx={{ flex: 1, minHeight: 0, overflowY: 'auto', pr: 1 }}>
                <MuiForm
                    key={serveiId}
                    resourceName={RECURS}
                    id={serveiId}
                    apiRef={formApiRef}
                    formBlockerDisabled
                    hiddenToolbar
                >
                    <CampsConfigScsp />
                </MuiForm>
            </Box>
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', pt: 1.5 }}>
                <Button variant="contained" startIcon={<Icon>{icons.desa}</Icon>} disabled={desant} onClick={desar}>
                    {t('serveis.scsp.desar')}
                </Button>
            </Box>
        </Box>
    );
};

export default ServeiConfigScsp;
