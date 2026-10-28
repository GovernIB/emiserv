import React from 'react';
import Button from '@mui/material/Button';
import Grid from '@mui/material/Grid';
import Icon from '@mui/material/Icon';
import { alpha, type Theme } from '@mui/material/styles';
import type { GridSize } from '@mui/material/Grid';
import { useFilterApiContext } from 'reactlib';
import { accentColor } from './theme';
import { icons } from './icons';
import { useLot2Translation } from './i18n';

/**
 * Botons dels filtres dels llistats: mateixa alçada que els camps (ocupen l'alçada de la fila de la
 * graella del filtre), text a 14px i colors de la barra de títol (text en el color d'accent i vora
 * suau), perquè el color base de l'aplicació pot no tenir prou contrast com a text sobre blanc.
 */
const filterButtonSx = (theme: Theme) => ({
    height: '100%',
    minHeight: '40px',
    margin: 0,
    fontSize: '14px',
    fontWeight: 400,
    letterSpacing: '.4px',
    textTransform: 'none' as const,
    color: theme.palette.mode === 'dark' ? theme.palette.text.primary : accentColor(theme),
    borderColor: alpha(accentColor(theme), 0.4),
    '&:hover': { borderColor: accentColor(theme) },
});

type FilterButtonGridProps = {
    /** Mida de la cel·la a la graella del filtre (per defecte, l'amplada del botó). */
    size?: GridSize;
};

/**
 * Botó de netejar el filtre: només la icona (vareta), amb el nom accessible «Neteja». Ha d'anar dins
 * el contingut d'un MuiFilter.
 */
export const FilterClearButton: React.FC<FilterButtonGridProps> = (props) => {
    const { size = 'auto' } = props;
    const { t } = useLot2Translation();
    const filterApiRef = useFilterApiContext();
    return (
        <Grid size={size}>
            <Button
                variant="outlined"
                onClick={() => filterApiRef.current?.clear()}
                title={t('neteja')}
                aria-label={t('neteja')}
                sx={(theme) => ({ ...filterButtonSx(theme), minWidth: 0, px: 1 })}
            >
                <Icon aria-hidden>{icons.neteja}</Icon>
            </Button>
        </Grid>
    );
};

type FilterAdvancedButtonProps = FilterButtonGridProps & {
    /** El filtre avançat està obert. */
    open: boolean;
    onClick: () => void;
};

/** Botó d'obrir/tancar el filtre avançat: chevron i l'etiqueta «Avançat». */
export const FilterAdvancedButton: React.FC<FilterAdvancedButtonProps> = (props) => {
    const { size = 'auto', open, onClick } = props;
    const { t } = useLot2Translation();
    return (
        <Grid size={size}>
            <Button
                variant="outlined"
                onClick={onClick}
                aria-expanded={open}
                title={t(open ? 'tancarFiltreAvancat' : 'obrirFiltreAvancat')}
                startIcon={<Icon aria-hidden>{open ? icons.cercaAvancadaTanca : icons.cercaAvancadaObre}</Icon>}
                sx={(theme) => ({ ...filterButtonSx(theme), whiteSpace: 'nowrap' })}
            >
                {t(open ? 'simple' : 'avancat')}
            </Button>
        </Grid>
    );
};
