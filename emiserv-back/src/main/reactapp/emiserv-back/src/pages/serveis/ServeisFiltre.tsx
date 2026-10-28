import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Collapse from '@mui/material/Collapse';
import Grid from '@mui/material/Grid';
import { FormField, MuiFilter, useFilterApiRef } from 'reactlib';
import { FilterClearButton, FilterCount } from '../../lot2';
import { SERVEI_TIPUS } from './serveiModel';

/** Clau de persistència del filtre (la llibreria hi afegeix el codi de l'aplicació i el de l'usuari). */
const CLAU_PERSISTENCIA = 'serveis';

type DadesFiltre = {
    codi?: string | null;
    nom?: string | null;
    tipus?: string | null;
    actiu?: string | boolean | null;
};

/** Literal de Spring Filter entre cometes simples, amb les cometes i les barres escapades. */
const literal = (valor: string) => `'${valor.replace(/\\/g, '\\\\').replace(/'/g, "\\'")}'`;

const conte = (camp: string, valor?: string | null) =>
    valor?.trim() ? `${camp}~${literal('%' + valor.trim() + '%')}` : '';

/**
 * Filtre Spring a partir de les dades del formulari. `actiu` és un Boolean que el desplegable de tres
 * valors (buit / Sí / No) dona com a text: es compara amb null i amb el buit, perquè «No» (false) no es
 * confongui amb «sense filtrar».
 */
// eslint-disable-next-line react-refresh/only-export-components
export const construirFiltreServeis = (dades?: DadesFiltre): string => {
    const actiu = dades?.actiu;
    // No es fa servir springFilterBuilder.and: descarta els termes que contenen «null» o «undefined»,
    // que podrien ser part del text que cerca l'usuari.
    return [
        conte('codi', dades?.codi),
        conte('nom', dades?.nom),
        dades?.tipus ? `tipus:${literal(dades.tipus)}` : '',
        actiu != null && actiu !== '' ? `actiu:${String(actiu) === 'true'}` : '',
    ]
        .filter((terme) => terme !== '')
        .join(' and ');
};

type ServeisFiltreProps = {
    /** El filtre plegable està obert. Plegat continua muntat, perquè el filtre desat s'apliqui igualment. */
    obert: boolean;
};

/**
 * Filtre plegable del llistat de serveis (codi, nom, tipus i actiu). Va a la fila addicional de la
 * graella, que rep el filtre directament (MuiFilter no `detached`).
 *
 * El recurs `servei` no publica cap artefacte FILTER: els camps es declaren aquí (`customFields`), amb
 * les etiquetes traduïdes de l'aplicació. L'estat del filtre es desa per usuari
 * (`persistentStateActive`: la llibreria el desa amb el `usePersistentState` del Filter, amb la clau
 * `EMISERV_FLT_SERVEIS_<USUARI>`) al localStorage, de manera que es recorda després de recarregar.
 */
export const ServeisFiltre: React.FC<ServeisFiltreProps> = ({ obert }) => {
    const { t } = useTranslation();
    const [springFilter, setSpringFilter] = React.useState<string>();
    const filterApiRef = useFilterApiRef();
    const camps = React.useMemo(
        () => [
            { name: 'codi', type: 'text', label: t('serveis.filtre.codi') },
            { name: 'nom', type: 'text', label: t('serveis.filtre.nom') },
            {
                name: 'tipus',
                type: 'enum',
                label: t('serveis.filtre.tipus'),
                options: Object.fromEntries(SERVEI_TIPUS.map((tipus) => [tipus, t(`serveis.tipus.${tipus}`)])),
            },
            { name: 'actiu', type: 'checkbox', label: t('serveis.filtre.actiu') },
        ],
        [t]
    );
    return (
        <Collapse in={obert}>
            <MuiFilter
                resourceName="servei"
                code="FILTRE_SERVEIS"
                // Prop del Form que el Filter li passa però que el tipus de MuiFilter no declara.
                {...({ customFields: camps } as Record<string, unknown>)}
                persistentStateActive
                persistentStateKey={CLAU_PERSISTENCIA}
                persistentStateStorage="local"
                commonFieldComponentProps={{ size: 'small' }}
                apiRef={filterApiRef}
                springFilterBuilder={construirFiltreServeis}
                onSpringFilterChange={setSpringFilter}
            >
                <Grid container spacing={1} sx={{ pt: 1 }}>
                    <Grid size={{ xs: 12, sm: 6, md: 'grow' }}>
                        <FormField name="codi" />
                    </Grid>
                    <Grid size={{ xs: 12, sm: 6, md: 'grow' }}>
                        <FormField name="nom" />
                    </Grid>
                    <Grid size={{ xs: 12, sm: 6, md: 'grow' }}>
                        <FormField name="tipus" />
                    </Grid>
                    <Grid size={{ xs: 12, sm: 6, md: 'grow' }}>
                        <FormField name="actiu" />
                    </Grid>
                    {/*
                     * El «Neteja» de la llibreria (clear) torna a les dades inicials del formulari, que
                     * amb la persistència són les del filtre desat: després de recarregar no el buidaria.
                     * Es captura el clic (el botó no el rep) i es buida el formulari amb reset({}), que
                     * aplica i desa el filtre buit.
                     */}
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

export default ServeisFiltre;
