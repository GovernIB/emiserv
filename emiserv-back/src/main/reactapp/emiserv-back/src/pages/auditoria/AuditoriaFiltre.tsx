import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Collapse from '@mui/material/Collapse';
import Grid from '@mui/material/Grid';
import { FormField, MuiFilter, useFilterApiRef, useResourceApiService } from 'reactlib';
import { FilterClearButton, FilterCount } from '../../lot2';
import {
    construirFiltreAuditoria,
    type DadesFiltreAuditoria,
    datesDrecera,
    DRECERES_DATES,
    ESTATS_PETICIO,
    RECURS_AUDITORIA,
    type TipusAuditoria,
} from './auditoriaModel';
import { useApiAuditoria } from './useApiAuditoria';

/** Serveis del filtre: els backoffice (per id) o els enrutadors (per codi), com la JSP. */
const FILTRE_SERVEIS: Record<TipusAuditoria, string> = {
    backoffice: "tipus:'BACKOFFICE'",
    enrutador: "tipus:'ENRUTADOR' or tipus:'ENRUTADOR_MULTIPLE'",
};

type Opcions = Record<string, string>;

/** Opcions dels desplegables de servei i procediment (els serveis que l'usuari pot veure). */
const useOpcionsFiltre = (tipus: TipusAuditoria) => {
    const { isReady, find } = useResourceApiService('servei');
    const api = useApiAuditoria(tipus);
    const [serveis, setServeis] = React.useState<Opcions>({});
    const [procediments, setProcediments] = React.useState<Opcions>({});
    React.useEffect(() => {
        if (!isReady) {
            return;
        }
        find({ unpaged: true, filter: FILTRE_SERVEIS[tipus], sorts: ['codi,asc'] })
            .then(({ rows }) =>
                setServeis(
                    Object.fromEntries(
                        rows.map((s: { id: number; codi: string; nom?: string }) => [
                            tipus === 'backoffice' ? String(s.id) : s.codi,
                            s.nom ? `${s.codi} - ${s.nom}` : s.codi,
                        ])
                    )
                )
            )
            .catch(() => setServeis({}));
    }, [isReady, find, tipus]);
    React.useEffect(() => {
        api.json<{ codi: string; nom?: string }[]>('procediments')
            .then((llista) =>
                setProcediments(
                    Object.fromEntries(llista.map((p) => [p.codi, p.nom ? `${p.codi} - ${p.nom}` : p.codi]))
                )
            )
            .catch(() => setProcediments({}));
    }, [api]);
    return { serveis, procediments };
};

type AuditoriaFiltreProps = {
    tipus: TipusAuditoria;
    /** El filtre plegable està obert. Plegat continua muntat, perquè el filtre desat s'apliqui igualment. */
    obert: boolean;
};

/**
 * Filtre plegable dels llistats d'auditoria (spec §5.3): servei, procediment, número de petició, estat
 * i interval de dates, amb les dreceres «Avui», «Darrers 7 dies» i «Aquest mes». Com el filtre dels
 * serveis, els camps es declaren aquí (`customFields`) i l'estat es desa per usuari al localStorage.
 * El document i el nom del funcionari no hi són: la consulta de la JSP no els fa servir.
 */
export const AuditoriaFiltre: React.FC<AuditoriaFiltreProps> = ({ tipus, obert }) => {
    const { t } = useTranslation();
    const [springFilter, setSpringFilter] = React.useState<string>();
    const filterApiRef = useFilterApiRef();
    const { serveis, procediments } = useOpcionsFiltre(tipus);
    const camps = React.useMemo(
        () => [
            { name: 'servei', type: 'enum', label: t('auditoria.filtre.servei'), options: serveis },
            { name: 'procediment', type: 'enum', label: t('auditoria.filtre.procediment'), options: procediments },
            { name: 'numeroPeticio', type: 'text', label: t('auditoria.filtre.numeroPeticio') },
            {
                name: 'estat',
                type: 'enum',
                label: t('auditoria.filtre.estat'),
                options: Object.fromEntries(ESTATS_PETICIO.map((e) => [e, t(`auditoria.estat.${e}`)])),
            },
            { name: 'dataInici', type: 'date', label: t('auditoria.filtre.dataInici') },
            { name: 'dataFi', type: 'date', label: t('auditoria.filtre.dataFi') },
        ],
        [t, serveis, procediments]
    );
    const construir = React.useCallback(
        (dades?: DadesFiltreAuditoria) => construirFiltreAuditoria(tipus, dades),
        [tipus]
    );
    return (
        <Collapse in={obert}>
            <MuiFilter
                resourceName={RECURS_AUDITORIA[tipus]}
                code={`FILTRE_${RECURS_AUDITORIA[tipus].toUpperCase()}`}
                // Prop del Form que el Filter li passa però que el tipus de MuiFilter no declara.
                {...({ customFields: camps } as Record<string, unknown>)}
                persistentStateActive
                persistentStateKey={RECURS_AUDITORIA[tipus]}
                persistentStateStorage="local"
                commonFieldComponentProps={{ size: 'small' }}
                apiRef={filterApiRef}
                springFilterBuilder={construir}
                onSpringFilterChange={setSpringFilter}
            >
                <Grid container spacing={1} sx={{ pt: 1 }}>
                    <Grid size={{ xs: 12, sm: 6, md: 4 }}>
                        <FormField name="servei" />
                    </Grid>
                    <Grid size={{ xs: 12, sm: 6, md: 4 }}>
                        <FormField name="procediment" />
                    </Grid>
                    <Grid size={{ xs: 12, sm: 6, md: 4 }}>
                        <FormField name="numeroPeticio" />
                    </Grid>
                    <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                        <FormField name="estat" />
                    </Grid>
                    <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                        <FormField name="dataInici" />
                    </Grid>
                    <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                        <FormField name="dataFi" />
                    </Grid>
                    <Grid
                        size={{ xs: 12, md: 'grow' }}
                        sx={{ display: 'flex', alignItems: 'center', gap: 0.5, flexWrap: 'wrap' }}
                    >
                        {DRECERES_DATES.map((drecera) => (
                            <Button
                                key={drecera}
                                size="small"
                                variant="outlined"
                                onClick={() =>
                                    filterApiRef.current?.reset({
                                        ...(filterApiRef.current?.getData() ?? {}),
                                        ...datesDrecera(drecera),
                                    })
                                }
                            >
                                {t(`auditoria.filtre.drecera.${drecera}`)}
                            </Button>
                        ))}
                    </Grid>
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

export default AuditoriaFiltre;
