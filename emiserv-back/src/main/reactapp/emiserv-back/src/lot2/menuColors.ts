import { alpha, createTheme, getContrastRatio, type Theme } from '@mui/material/styles';
import type { MenuColors, MuiBaseAppAppearance } from 'reactlib';
import { HEADER_HEIGHT, ICON_SIZE, MENU_WIDTH_COLLAPSED } from './layout';

/**
 * Estil del menú lateral (el `MenuEstil` de Notib):
 *  - `theme` (TEMA): fa servir directament la paleta activa;
 *  - `inverse` (TEMA_INVERTIT): pren els colors del tema contrari (clar <-> fosc), de manera que el
 *    menú es veu igual que amb `theme` a l'altre tema;
 *  - `footer` (PEU): sempre el gris fosc del peu de l'aplicació.
 * L'accent: amb `theme` i amb el menú `inverse` clar (tema fosc) és el color base de l'aplicació; amb
 * el menú `inverse` fosc (tema clar), `primary.light` (o blanc si no arriba a 4.5:1 sobre el fons); amb
 * `footer`, blanc.
 */
export type Lot2MenuAppearance = 'theme' | 'inverse' | 'footer';

/** Contrast mínim (WCAG AA) de l'accent del menú sobre el seu fons. */
const MIN_CONTRAST = 4.5;

/** Gris del peu de Drassana (fons de `DrassanaFooter` i del menú amb estil `footer`). */
export const DRASSANA_FOOTER_BACKGROUND = '#5F5D5D';

/**
 * Colors del menú lateral per a `MuiBaseAppAppearance.menuColors` (getMenuColorSet de Notib).
 * Amb `theme` no es fixa cap color: la llibreria ja fa servir els del tema actiu.
 *
 * El tema contrari es construeix amb la paleta per defecte de MUI i el mateix color base, que és
 * el que fan els temes clar i fosc de `createLot2Themes` (no en redefineixen fons, text ni divisors).
 */
export const lot2MenuColors = (theme: Theme, appearance: Lot2MenuAppearance): MenuColors => {
    const accent = theme.palette.primary.main;
    if (appearance === 'footer') {
        return {
            background: DRASSANA_FOOTER_BACKGROUND,
            textPrimary: '#F6F6F6',
            textSecondary: '#E5E5E5',
            divider: '#807D7D',
            // Blanc: l'accent del color base no té contrast amb el gris del peu (1.2:1 amb comanda) i la
            // llibreria el fa servir també per al text del títol del panell compacte.
            accent: '#FFFFFF',
            selectedBackground: 'rgba(255,255,255,0.12)',
            hoverBackground: 'rgba(255, 255, 255, 0.08)',
        };
    }
    if (appearance !== 'inverse') {
        return {};
    }
    const { palette } = createTheme({
        palette: {
            mode: theme.palette.mode === 'dark' ? 'light' : 'dark',
            primary: { main: accent },
        },
    });
    // Menú fosc (tema clar): el color base no hi té contrast (2.2:1 amb #004B99), així que s'usa el to
    // clar del tema (`primary.light`, el `light` de `createLot2Themes`) i, si tampoc arriba a 4.5:1,
    // blanc. Amb el menú clar l'accent és el color base.
    const menuAccent =
        palette.mode === 'dark'
            ? getContrastRatio(theme.palette.primary.light, palette.background.paper) >= MIN_CONTRAST
                ? theme.palette.primary.light
                : '#FFFFFF'
            : palette.primary.main;
    return {
        background: palette.background.paper,
        textPrimary: palette.text.primary,
        textSecondary: palette.action.active,
        divider: palette.divider,
        accent: menuAccent,
        selectedBackground: alpha(palette.primary.main, palette.mode === 'dark' ? 0.2 : 0.1),
        hoverBackground: palette.action.hover,
    };
};

/**
 * Mides del marc de Notib per a `MuiBaseAppAppearance`: capçalera de 74px, menú plegat de 74px
 * (icones centrades a la columna de 37px), icones de 24px i entrada seleccionada marcada amb fons.
 * `Lot2BaseApp` hi afegeix els `menuColors`.
 */
export const LOT2_APPEARANCE: MuiBaseAppAppearance = {
    headerHeight: HEADER_HEIGHT,
    menuCollapsedWidth: MENU_WIDTH_COLLAPSED,
    menuIconSize: ICON_SIZE,
    menuSelectedIndicator: 'background',
    menuListItemSemantics: true,
};
