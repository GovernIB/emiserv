import { alpha, darken, lighten, type Theme } from '@mui/material/styles';
import { accentColor } from './theme';
import { SCREEN_TITLE_MIN_HEIGHT, SCREEN_TITLE_PADDING_X } from './layout';

/**
 * Vora dels controls de la barra de títol: el color d'accent amb transparència, perquè es vegin
 * sobre el blanc (zona clara del degradat) i separin els botons dels grups. El gris neutre del tema
 * (`divider`) hi quedava gairebé invisible.
 */
const borderColor = (theme: Theme) => alpha(accentColor(theme), 0.4);

const hoverBackground = (theme: Theme) =>
    theme.palette.mode === 'dark'
        ? lighten(theme.palette.background.paper, 0.15)
        : darken(theme.palette.background.paper, 0.06);

/**
 * Vora dels botons outlined que van fora de la barra de títol (netejar el filtre, cerca avançada...)
 * perquè s'assemblin als de la barra: als temes foscos el tema els dona la vora blanca plena.
 */
export const outlinedControlBorderSx = (theme: Theme) =>
    theme.palette.mode === 'dark' ? { borderColor: borderColor(theme), '&:hover': { borderColor: borderColor(theme) } } : {};

/**
 * Estil dels controls (botons, botons d'icona, xips, filtre ràpid) que cauen sobre la barra de títol
 * d'una pantalla: la barra d'eines de la llibreria quan fa de barra de títol i les `actions` del
 * ScreenFrame. Es posa com a `sx` del contenidor dels controls.
 *
 * Les eines cauen sobre la part del degradat que arriba al color principal: es pinten amb el fons del
 * paper (blanc al tema clar, fosc als altres) i el text en el color d'accent, que és el «torrat» als
 * temes clars i el verd clar als foscos.
 */
export const titleBarControlsSx = {
    '& .MuiButton-root': {
        backgroundColor: (theme: Theme) => theme.palette.background.paper,
        textTransform: 'none',
        // Als temes foscos el text és blanc (més contrast sobre el fons fosc del botó que el verd)
        color: (theme: Theme) => (theme.palette.mode === 'dark' ? theme.palette.text.primary : accentColor(theme)),
        // Vora visible: els botons poden caure sobre la part clara del degradat.
        border: '1px solid',
        borderColor,
        boxShadow: 'none',
        '&:hover': {
            backgroundColor: hoverBackground,
            borderColor,
            boxShadow: 'none',
        },
        // Desactivat: el fons és el de la resta; només el text s'aclareix.
        '&.Mui-disabled': {
            // Sense l'opacitat que el tema o MUI donen al botó desactivat: aclaria també el fons,
            // que s'ha de veure igual que a la resta de botons.
            opacity: 1,
            // backgroundColor: (theme: Theme) => theme.palette.background.paper,
            color: (theme: Theme) => theme.palette.action.disabled,
            // borderColor,
        },
    },
    // Botons només d'icona de la llibreria (exportar, refrescar...): mateix fons i vora que els altres
    // botons, perquè es llegeixin sobre el color de la barra.
    '& .MuiIconButton-root': {
        // backgroundColor: (theme: Theme) => theme.palette.background.paper,
        // border: '1px solid',
        borderColor,
        // borderRadius: '4px',
        padding: '5px',
        // Sense marges propis (la llibreria en posa al botó de tornar, a l'avís d'error...): la
        // separació entre controls és el gap del contenidor (columnGap de la barra de títol).
        margin: 0,
        '&:hover': { backgroundColor: hoverBackground },
        '&.Mui-disabled': {
            // backgroundColor: (theme: Theme) => theme.palette.background.paper,
            color: (theme: Theme) => theme.palette.action.disabled,
        },
    },
    // Comptador del selector d'accions massives desactivat (0 files): el xip no s'aclareix sencer,
    // només en perd el color.
    '& .MuiChip-root.Mui-disabled': {
        opacity: 1,
        color: (theme: Theme) => theme.palette.action.disabled,
    },
    // Separació entre els botons d'un grup (selector d'accions massives).
    '& .MuiButtonGroup-grouped:not(:last-of-type)': {
        borderRightColor: borderColor,
    },
    // Filtre ràpid sense el marge esquerre que li posa la llibreria (la separació és el gap).
    '& .MuiTextField-root': { margin: 0 },
    // Camps de text (filtre ràpid)
    '& .MuiOutlinedInput-root': {
        backgroundColor: (theme: Theme) => theme.palette.background.paper,
        '& fieldset': { borderColor },
        // Alçada dels botons de la barra (32px)
        height: 32,
        '& .MuiOutlinedInput-input': { paddingTop: 0, paddingBottom: 0 },
    },
} as const;

/**
 * Aspecte de la barra de títol d'una pantalla: vora esquerra en el color base i degradat del fons del
 * paper al color base. El fa servir la barra pròpia del ScreenFrame i la barra d'eines de la llibreria
 * (graelles i formularis) a través de `lot2DefaultMuiComponentProps` (veure lot2ComponentProps.ts).
 */
export const titleBarSx = (theme: Theme) => ({
    minHeight: SCREEN_TITLE_MIN_HEIGHT + 'px',
    boxSizing: 'border-box' as const,
    // La barra pot passar a dues files en pantalles estretes. Entre tots els elements hi ha un sol
    // gap de 8px (columnGap), sense marges propis als controls (titleBarControlsSx). El separador
    // `flexGrow` no té amplada: entre el títol i el primer control suma dos gaps, però el separador
    // creix i aquest espai ja és variable; no genera cap fila buida perquè sempre cap al costat
    // d'un altre element. El contenidor del títol no ocupa el 100% (veure Toolbar). Els controls que
    // cauen a la segona fila queden alineats a l'esquerra (acceptat); si el separador també hi cau,
    // els empeny a la dreta.
    flexWrap: 'wrap' as const,
    rowGap: 1,
    columnGap: 1,
    paddingLeft: SCREEN_TITLE_PADDING_X + 'px',
    paddingRight: SCREEN_TITLE_PADDING_X + 'px',
    borderLeft: `4px solid ${theme.palette.primary.main}`,
    borderBottom: `1px solid ${theme.palette.divider}`,
    background: `linear-gradient(90deg, ${theme.palette.background.paper} 25%, ${theme.palette.primary.main} 75%)`,
    ...titleBarControlsSx,
});
