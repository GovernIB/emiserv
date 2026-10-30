import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import type { GridRenderCellParams } from '@mui/x-data-grid-pro';
import { FormField, MuiDataGrid, type MuiDataGridColDef, useFormContext } from 'reactlib';
import { PRINCIPAL_TIPUS } from './serveiModel';
import { useOpcionsEnum } from './ServeiDadesForm';

/** Nom del recurs de l'API (ServeiPermisApiController). */
const RECURS_PERMIS = 'serveiPermis';

type Permis = { id: string; principalTipus: string; principalNom: string; administration: boolean };

/** Formulari d'un permís (`serveiPermisForm.jsp`): el principal no es pot canviar un cop creat. */
const ServeiPermisForm: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useFormContext();
    const opcionsTipus = useOpcionsEnum(PRINCIPAL_TIPUS, 'serveis.enum.principalTipus');
    return (
        <Grid container spacing={2} sx={{ pt: 1 }}>
            <Grid size={{ xs: 12, md: 4 }}>
                <FormField
                    name="principalTipus"
                    label={t('serveis.permisos.tipus')}
                    required
                    disabled={id != null}
                    options={opcionsTipus}
                />
            </Grid>
            <Grid size={{ xs: 12, md: 8 }}>
                <FormField name="principalNom" label={t('serveis.permisos.principal')} required disabled={id != null} />
            </Grid>
            <Grid size={12}>
                <FormField name="administration" label={t('serveis.permisos.administracio')} />
            </Grid>
        </Grid>
    );
};

/** Pestanya «Permisos» d'un servei (`serveiPermis.jsp`), només per a administradors. */
export const ServeiPermisos: React.FC<{ serveiId: number }> = ({ serveiId }) => {
    const { t } = useTranslation();
    const columns = React.useMemo<MuiDataGridColDef[]>(
        () => [
            {
                field: 'principalTipus',
                headerName: t('serveis.permisos.tipus'),
                flex: 1,
                sortable: false,
                renderCell: (params: GridRenderCellParams<Permis>) =>
                    t(`serveis.enum.principalTipus.${params.row.principalTipus}`),
            },
            { field: 'principalNom', headerName: t('serveis.permisos.principal'), flex: 3, sortable: false },
            {
                field: 'administration',
                headerName: t('serveis.permisos.administracio'),
                flex: 1,
                sortable: false,
                type: 'boolean',
            },
        ],
        [t]
    );
    return (
        <Box sx={{ flex: 1, minHeight: 0 }}>
            <MuiDataGrid
                resourceName={RECURS_PERMIS}
                titleDisabled
                columns={columns}
                fixedFilter={`serveiId:${serveiId}`}
                formAdditionalData={{ serveiId }}
                toolbarHideQuickFilter
                toolbarCreateLabel={t('serveis.permisos.nou')}
                popupEditActive
                popupEditFormContent={<ServeiPermisForm />}
                popupEditFormDialogResourceTitle={t('serveis.permisos.recurs')}
                popupEditFormDialogComponentProps={{ maxWidth: 'sm', fullWidth: true }}
            />
        </Box>
    );
};

export default ServeiPermisos;
