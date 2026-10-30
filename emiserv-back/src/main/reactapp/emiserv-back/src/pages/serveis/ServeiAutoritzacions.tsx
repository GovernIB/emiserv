import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import { FormField, MuiDataGrid, type MuiDataGridColDef } from 'reactlib';

/** Nom del recurs de l'API (ServeiAutoritzacioApiController). */
const RECURS_AUTORITZACIO = 'serveiAutoritzacio';

/** Formulari d'una autorització (`serveiAutoritzacioForm.jsp`). */
const ServeiAutoritzacioForm: React.FC = () => {
    const { t } = useTranslation();
    return (
        <Grid container spacing={2} sx={{ pt: 1 }}>
            <Grid size={12}>
                <FormField name="aplicacioId" label={t('serveis.autoritzacions.aplicacio')} required autocomplete />
            </Grid>
            <Grid size={12}>
                <FormField name="organismeId" label={t('serveis.autoritzacions.organisme')} required autocomplete />
            </Grid>
            <Grid size={{ xs: 12, md: 6 }}>
                <FormField name="dataAlta" label={t('serveis.autoritzacions.dataAlta')} type="date" />
            </Grid>
            <Grid size={{ xs: 12, md: 6 }}>
                <FormField name="dataBaixa" label={t('serveis.autoritzacions.dataBaixa')} type="date" />
            </Grid>
        </Grid>
    );
};

/**
 * Pestanya «Autoritzacions» d'un servei configurat a l'emissor SCSP (`serveiAutoritzacioList.jsp`),
 * només per a administradors. El filtre ràpid cerca per aplicació, com el filtre de la JSP.
 */
export const ServeiAutoritzacions: React.FC<{ serveiId: number }> = ({ serveiId }) => {
    const { t } = useTranslation();
    const columns = React.useMemo<MuiDataGridColDef[]>(
        () => [
            { field: 'aplicacioNom', headerName: t('serveis.autoritzacions.aplicacio'), flex: 2, sortable: false },
            { field: 'organismeNom', headerName: t('serveis.autoritzacions.organisme'), flex: 2, sortable: false },
            {
                field: 'dataAlta',
                headerName: t('serveis.autoritzacions.dataAlta'),
                flex: 1,
                sortable: false,
                type: 'date',
            },
            {
                field: 'dataBaixa',
                headerName: t('serveis.autoritzacions.dataBaixa'),
                flex: 1,
                sortable: false,
                type: 'date',
            },
        ],
        [t]
    );
    return (
        <Box sx={{ flex: 1, minHeight: 0 }}>
            <MuiDataGrid
                resourceName={RECURS_AUTORITZACIO}
                titleDisabled
                columns={columns}
                fixedFilter={`serveiId:${serveiId}`}
                formAdditionalData={{ serveiId }}
                paginationActive
                toolbarCreateLabel={t('serveis.autoritzacions.nova')}
                popupEditActive
                popupEditFormContent={<ServeiAutoritzacioForm />}
                popupEditFormDialogResourceTitle={t('serveis.autoritzacions.recurs')}
                popupEditFormDialogComponentProps={{ maxWidth: 'sm', fullWidth: true }}
            />
        </Box>
    );
};

export default ServeiAutoritzacions;
