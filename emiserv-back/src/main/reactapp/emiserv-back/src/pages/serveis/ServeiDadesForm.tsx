import React from 'react';
import { useTranslation } from 'react-i18next';
import Alert from '@mui/material/Alert';
import Grid from '@mui/material/Grid';
import Link from '@mui/material/Link';
import Typography from '@mui/material/Typography';
import { FormField, useFormContext } from 'reactlib';
import {
    BACKOFFICE_ASYNC_TIPUS,
    BACKOFFICE_AUTENTICACIO,
    esEnrutador,
    SERVEI_TIPUS,
    type MotiuDiagnostic,
    type ServeiTipus,
} from './serveiModel';

/** Opcions traduïdes d'un enumerat per a un FormField. */
// eslint-disable-next-line react-refresh/only-export-components
export const useOpcionsEnum = (valors: readonly string[], prefix: string) => {
    const { t } = useTranslation();
    return React.useMemo(
        () => valors.map((valor) => ({ value: valor, description: t(`${prefix}.${valor}`) })),
        [t, valors, prefix]
    );
};

type ServeiDadesFormProps = {
    /** El servei ja existeix: el codi no es pot canviar. */
    existent: boolean;
    /** Motius del diagnòstic del servei (per avisar de la configuració SCSP pendent). */
    motius?: MotiuDiagnostic[];
    /** Obre la pestanya de configuració SCSP (enllaç de l'avís de configuració pendent). */
    onAnarScsp?: () => void;
};

/**
 * Camps del formulari d'un servei (`serveiForm.jsp`). Només es mostren els camps del tipus triat, com
 * a la JSP; el servidor comprova els obligatoris de cada tipus.
 */
export const ServeiDadesForm: React.FC<ServeiDadesFormProps> = ({ existent, motius, onAnarScsp }) => {
    const { t } = useTranslation();
    const { data } = useFormContext();
    const tipus = data?.tipus as ServeiTipus | undefined;
    const opcionsTipus = useOpcionsEnum(SERVEI_TIPUS, 'serveis.tipus');
    const opcionsAutenticacio = useOpcionsEnum(BACKOFFICE_AUTENTICACIO, 'serveis.enum.autenticacio');
    const opcionsAsync = useOpcionsEnum(BACKOFFICE_ASYNC_TIPUS, 'serveis.enum.asyncTipus');
    const faltaConfigScsp = motius?.some((m) => m === 'FALTA_CONFIG_SCSP' || m === 'FALTA_EMISOR');
    return (
        <Grid container spacing={2} sx={{ pt: 1 }}>
            {faltaConfigScsp && (
                <Grid size={12}>
                    <Alert severity="warning">
                        {t('serveis.form.avisConfigScsp')}{' '}
                        <Link component="button" type="button" onClick={onAnarScsp} sx={{ verticalAlign: 'baseline' }}>
                            {t('serveis.form.anarScsp')}
                        </Link>
                    </Alert>
                </Grid>
            )}
            <Grid size={{ xs: 12, md: 4 }}>
                <FormField name="codi" label={t('serveis.form.codi')} required disabled={existent} />
            </Grid>
            <Grid size={{ xs: 12, md: 8 }}>
                <FormField name="nom" label={t('serveis.form.nom')} required />
            </Grid>
            <Grid size={{ xs: 12, md: 4 }}>
                <FormField name="tipus" label={t('serveis.form.tipus')} required options={opcionsTipus} />
            </Grid>
            <Grid size={{ xs: 12, md: 8 }}>
                <FormField name="descripcio" label={t('serveis.form.descripcio')} />
            </Grid>
            {tipus === 'BACKOFFICE' && (
                <>
                    <Grid size={12}>
                        <FormField name="backofficeClass" label={t('serveis.form.backofficeClass')} required />
                    </Grid>
                    <Grid size={12}>
                        <FormField name="backofficeCaibUrl" label={t('serveis.form.backofficeCaibUrl')} required />
                    </Grid>
                    <Grid size={{ xs: 12, md: 4 }}>
                        <FormField
                            name="backofficeCaibAutenticacio"
                            label={t('serveis.form.backofficeCaibAutenticacio')}
                            required
                            options={opcionsAutenticacio}
                        />
                    </Grid>
                    <Grid size={{ xs: 12, md: 4 }}>
                        <FormField
                            name="backofficeCaibAsyncTipus"
                            label={t('serveis.form.backofficeCaibAsyncTipus')}
                            required
                            options={opcionsAsync}
                        />
                    </Grid>
                    <Grid size={{ xs: 12, md: 4 }}>
                        <FormField name="backofficeCaibAsyncTer" label={t('serveis.form.backofficeCaibAsyncTer')} />
                    </Grid>
                </>
            )}
            {tipus === 'ENRUTADOR' && (
                <>
                    <Grid size={12}>
                        <FormField name="resolverClass" label={t('serveis.form.resolverClass')} required />
                    </Grid>
                    <Grid size={12}>
                        <FormField name="urlPerDefecte" label={t('serveis.form.urlPerDefecte')} />
                    </Grid>
                </>
            )}
            {tipus === 'ENRUTADOR_MULTIPLE' && (
                <Grid size={12}>
                    <FormField name="responseResolverClass" label={t('serveis.form.responseResolverClass')} required />
                </Grid>
            )}
            {esEnrutador(tipus) && (
                <>
                    <Grid size={12}>
                        <Typography variant="subtitle2" color="text.secondary">
                            {t('serveis.form.asincron')}
                        </Typography>
                        <FormField name="asyncSincron" label={t('serveis.form.asyncSincron')} />
                    </Grid>
                    <Grid size={{ xs: 12, md: 6 }}>
                        <FormField
                            name="asyncInterval"
                            label={t('serveis.form.asyncInterval')}
                            disabled={!data?.asyncSincron}
                            componentProps={{ placeholder: t('serveis.form.valorGlobal') }}
                        />
                    </Grid>
                    <Grid size={{ xs: 12, md: 6 }}>
                        <FormField
                            name="asyncConcurrencia"
                            label={t('serveis.form.asyncConcurrencia')}
                            disabled={!data?.asyncSincron}
                            componentProps={{ placeholder: t('serveis.form.valorGlobal') }}
                        />
                    </Grid>
                </>
            )}
        </Grid>
    );
};

export default ServeiDadesForm;
