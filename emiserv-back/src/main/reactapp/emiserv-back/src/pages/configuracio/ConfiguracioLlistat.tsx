import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Collapse from '@mui/material/Collapse';
import Grid from '@mui/material/Grid';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import { useTheme } from '@mui/material/styles';
import { FormField, MuiDataGrid, MuiFilter, useFilterApiRef, useMuiDataGridApiRef } from 'reactlib';
import { FilterClearButton, FilterCount, icons, lot2ScreenGridProps, ScreenFrame, ScreenTitle } from '../../lot2';
import type { Manteniment } from './manteniments';

/** Literal de Spring Filter entre cometes simples, amb les cometes i les barres escapades. */
const literal = (valor: string) => `'${valor.replace(/\\/g, '\\\\').replace(/'/g, "\\'")}'`;

/**
 * Filtre Spring del filtre plegable: «conté» per a cada camp amb valor, units amb `and`. El servidor
 * (ConfiguracioResourceHelper) només entén aquesta forma.
 */
// eslint-disable-next-line react-refresh/only-export-components
export const construirFiltreConfiguracio = (dades?: Record<string, unknown>): string =>
    Object.entries(dades ?? {})
        .filter(([, valor]) => valor != null && String(valor).trim() !== '')
        .map(([camp, valor]) => `${camp}~${literal('%' + String(valor).trim() + '%')}`)
        .join(' and ');

/** Filtre plegable (els camps del filtre de la JSP), desat per usuari al localStorage. */
const Filtre: React.FC<{ manteniment: Manteniment; obert: boolean }> = ({ manteniment, obert }) => {
    const { t } = useTranslation();
    const [springFilter, setSpringFilter] = React.useState<string>();
    const filterApiRef = useFilterApiRef();
    const camps = React.useMemo(
        () =>
            (manteniment.filtre ?? []).map((name) => ({
                name,
                type: 'text',
                label: t(`configuracio.${manteniment.clau}.camp.${name}`),
            })),
        [t, manteniment]
    );
    return (
        <Collapse in={obert}>
            <MuiFilter
                resourceName={manteniment.recurs}
                code={`FILTRE_${manteniment.recurs.toUpperCase()}`}
                {...({ customFields: camps } as Record<string, unknown>)}
                persistentStateActive
                persistentStateKey={manteniment.recurs}
                persistentStateStorage="local"
                commonFieldComponentProps={{ size: 'small' }}
                apiRef={filterApiRef}
                springFilterBuilder={construirFiltreConfiguracio}
                onSpringFilterChange={setSpringFilter}
            >
                <Grid container spacing={1} sx={{ pt: 1 }}>
                    {(manteniment.filtre ?? []).map((name) => (
                        <Grid key={name} size={{ xs: 12, sm: 6, md: 'grow' }}>
                            <FormField name={name} />
                        </Grid>
                    ))}
                    {/* Com al filtre dels serveis: «Neteja» buida el formulari (no torna al filtre desat). */}
                    <Box
                        sx={{ display: 'contents' }}
                        onClickCapture={(event) => {
                            event.stopPropagation();
                            filterApiRef.current?.reset({});
                        }}
                    >
                        <FilterClearButton />
                    </Box>
                </Grid>
            </MuiFilter>
            <FilterCount springFilter={springFilter} />
        </Collapse>
    );
};

/**
 * Pantalla d'un manteniment de configuració (aplicacions, emissors, organismes, claus...): graella
 * amb paginació i ordenació al servidor, filtre plegable o ràpid (si la JSP en tenia) i alta,
 * modificació i esborrat en una finestra emergent. El servidor decideix què es pot fer (enllaços
 * HAL-FORMS de cada fila); les regles de negoci i els errors (CIF repetit, registre en ús) són els
 * del servei de la JSP.
 */
export const ConfiguracioLlistat: React.FC<{ manteniment: Manteniment }> = ({ manteniment }) => {
    const { t } = useTranslation();
    const theme = useTheme();
    const apiRef = useMuiDataGridApiRef();
    const [filtreObert, setFiltreObert] = React.useState(false);
    const columns = React.useMemo(() => manteniment.columnes(t), [manteniment, t]);
    const textFiltre = filtreObert ? t('configuracio.filtre.amaga') : t('configuracio.filtre.mostra');
    const Formulari = manteniment.formulari;
    const toolbarElements = [
        ...(manteniment.filtre?.length
            ? [
                  {
                      position: 2,
                      element: (
                          <Tooltip title={textFiltre}>
                              <IconButton
                                  aria-label={textFiltre}
                                  aria-expanded={filtreObert}
                                  onClick={() => setFiltreObert((o) => !o)}
                              >
                                  <Icon>{filtreObert ? icons.cercaAvancadaTanca : icons.filtre}</Icon>
                              </IconButton>
                          </Tooltip>
                      ),
                  },
              ]
            : []),
        ...(manteniment.accionsBarra
            ? [{ position: 2, element: <>{manteniment.accionsBarra(() => apiRef.current?.refresh?.())}</> }]
            : []),
    ];
    return (
        <ScreenFrame>
            <MuiDataGrid
                key={manteniment.recurs}
                title={<ScreenTitle icon={manteniment.icona} title={t(`configuracio.${manteniment.clau}.titol`)} />}
                {...lot2ScreenGridProps(theme)}
                resourceName={manteniment.recurs}
                apiRef={apiRef}
                columns={columns}
                paginationActive
                persistentStateActive
                persistentStateKey={manteniment.recurs}
                {...(manteniment.filtreRapid ? {} : { toolbarHideQuickFilter: true as const })}
                {...(manteniment.senseAlta ? { toolbarHideCreate: true as const } : {})}
                {...(manteniment.esborratMultiple ? { toolbarBulkDelete: true as const } : {})}
                toolbarCreateLabel={t('configuracio.nou')}
                toolbarElementsWithPositions={toolbarElements}
                toolbarAdditionalRow={
                    manteniment.filtre?.length ? <Filtre manteniment={manteniment} obert={filtreObert} /> : undefined
                }
                popupEditActive
                popupEditFormContent={<Formulari />}
                popupEditFormDialogResourceTitle={t(`configuracio.${manteniment.clau}.recurs`)}
                popupEditFormDialogComponentProps={{ maxWidth: 'md', fullWidth: true }}
            />
        </ScreenFrame>
    );
};

export default ConfiguracioLlistat;
