import { createLot2Themes, type Lot2Colors } from './lot2/theme';

/**
 * Identitat de colors d'EMISERV per als temes de `lot2`, presa de la capçalera de la interfície JSP
 * (`.navbar-app` a static/css/estils.css: taronja `#ff9523`).
 *  - `main`: el taronja de la JSP. Amb text negre (MUI el tria per contrast) dona 9.6:1.
 *  - `dark`: taronja torrat amb contrast amb blanc de 4.7:1 (≥ 4.5), per al text i les icones
 *    d'accent sobre fons blanc.
 *  - `light`: to clar per als temes foscos.
 *  - `soft`: atenuat per a la capçalera de les graelles.
 */
export const EMISERV_COLORS: Lot2Colors = {
    main: '#ff9523',
    dark: '#b35c00',
    light: '#ffb565',
    soft: '#fff3e6',
};

const emiservThemes = createLot2Themes(EMISERV_COLORS);

export const lightTheme = emiservThemes.light;
export const darkTheme = emiservThemes.dark;
