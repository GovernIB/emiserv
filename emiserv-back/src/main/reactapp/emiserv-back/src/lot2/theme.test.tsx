import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { ThemeProvider } from '@mui/material/styles';
import DialogActions from '@mui/material/DialogActions';
import { readdirSync, readFileSync, statSync } from 'fs';
import { join } from 'path';
import { lighten } from '@mui/material/styles';
import { createLot2Themes, accentColor } from './theme';
import { lot2MenuColors } from './menuColors';

const NOTIB = { main: '#83cb41', dark: '#497e3a', light: '#a8db7a', soft: '#F2F9EB' };

describe('createLot2Themes', () => {
    it('aplica el color base i els modes', () => {
        const themes = createLot2Themes(NOTIB);
        expect(themes.light.palette.primary.main).toBe('#83cb41');
        expect(themes.light.palette.mode).toBe('light');
        expect(themes.dark.palette.mode).toBe('dark');
        expect(themes.dracula.palette.mode).toBe('dark');
    });

    it('accentColor coincideix amb el de Notib', () => {
        const themes = createLot2Themes(NOTIB);
        // valors de Notib: clar = primary.dark, fosc = primary.light
        expect(accentColor(themes.light)).toBe('#497e3a');
        expect(accentColor(themes.dark)).toBe('#a8db7a');
    });

    it('light per defecte és lighten(main, 0.3)', () => {
        const themes = createLot2Themes({ main: NOTIB.main, dark: NOTIB.dark, soft: NOTIB.soft });
        expect(themes.dark.palette.primary.light).toBe(lighten('#83cb41', 0.3));
    });
});

describe('menú lateral al tema', () => {
    const themes = createLot2Themes(NOTIB);
    const drawerPaper = themes.light.components?.MuiDrawer?.styleOverrides?.paper as Record<string, unknown>;
    const serialize = (value: unknown) => JSON.stringify(value);

    it('el contenidor de la consulta de mida és la llista i no el paper', () => {
        // Al paper, el contenidor tallaria el panell flotant (position: fixed) dels submenús del menú plegat
        expect(drawerPaper).not.toHaveProperty('containerType');
        expect(drawerPaper['& .MuiList-root']).toEqual({ containerType: 'inline-size' });
    });

    it('MuiDrawer no té selectors d\'estructura del DOM de la llibreria', () => {
        const drawer = serialize(themes.light.components?.MuiDrawer);
        expect(drawer).not.toContain(':has(');
        expect(drawer).not.toContain('> div >');
        expect(drawer).not.toContain('> .MuiList-root');
        expect(drawer).not.toContain('first-of-type');
        // ni el marge de la icona plegada, que ara centra la llibreria (menuCollapsedWidth)
        expect(drawer).not.toContain('max-width');
    });

    it("l'única excepció d'estructura és el grup desplegat de MuiListItemButton", () => {
        const root = themes.light.components?.MuiListItemButton?.styleOverrides?.root as (
            props: { theme: typeof themes.light }
        ) => Record<string, unknown>;
        const keys = Object.keys(root({ theme: themes.light }));
        expect(keys.filter(k => k.includes(':has('))).toEqual(['&.Mui-selected:has(+ .MuiBox-root)']);
        expect(keys.some(k => k.includes('::before'))).toBe(false);
        // Al codi font (els overrides amb funció no es poden serialitzar)
        const source = readFileSync(join(__dirname, 'theme.ts'), 'utf8');
        expect(source.match(/'[^'\n]*:has\(/g)).toEqual(["'&.Mui-selected:has("]);
        expect(source).not.toMatch(/'[^'\n]*> div >/);
        expect(source).not.toMatch(/'[^'\n]*first-of-type/);
    });
});

describe('carpeta lot2', () => {
    const walk = (dir: string): string[] =>
        readdirSync(dir).flatMap(f => {
            const p = join(dir, f);
            return statSync(p).isDirectory() ? walk(p) : [p];
        });

    it('cap fitxer no importa de fora de lot2', () => {
        // Els tests s'exclouen a propòsit: poden importar de ../../lib (lot2 sempre és a <app>/src/lot2,
        // al costat de <app>/lib), p.ex. per a components que reactlib no exporta.
        const files = walk(__dirname).filter(f => /\.(ts|tsx)$/.test(f) && !/\.test\.tsx?$/.test(f));
        expect(files.length).toBeGreaterThan(0);
        for (const f of files) {
            expect(readFileSync(f, 'utf8'), f).not.toMatch(/from\s+['"]\.\.\//);
            expect(readFileSync(f, 'utf8'), f).not.toMatch(/@programari-limit\/base-react/);
        }
    });
});

// Contrast WCAG 2.x real: luminància relativa amb linealització sRGB
const channel = (v: number) => {
    const c = v / 255;
    return c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
};
const luminance = (hex: string) => {
    const h = hex.replace('#', '');
    const [r, g, b] = [0, 2, 4].map(i => parseInt(h.slice(i, i + 2), 16));
    return 0.2126 * channel(r) + 0.7152 * channel(g) + 0.0722 * channel(b);
};
const contrast = (a: string, b: string) => {
    const [hi, lo] = [luminance(a), luminance(b)].sort((x, y) => y - x);
    return (hi + 0.05) / (lo + 0.05);
};
const toHex = (c: string) => {
    if (c.startsWith('#')) return c.length === 4 ? '#' + [...c.slice(1)].map(x => x + x).join('') : c;
    const [r, g, b] = c.match(/\d+/g)!.map(Number);
    return '#' + [r, g, b].map(v => v.toString(16).padStart(2, '0')).join('');
};

describe('contrast de l\'accent del menú', () => {
    const IDENTITATS = {
        comanda: { main: '#A8456A', dark: '#7a2f4c', soft: '#f6e9ee' },
        ripea: { main: '#004B99', dark: '#003670', soft: '#e5eef7' },
    };
    for (const [nom, colors] of Object.entries(IDENTITATS)) {
        const themes = createLot2Themes(colors);
        it(`footer (${nom}): accent >= 4.5:1 sobre el fons`, () => {
            const c = lot2MenuColors(themes.light, 'footer');
            expect(c.accent).toBe('#FFFFFF');
            expect(c.selectedBackground).toBe('rgba(255,255,255,0.12)');
            expect(contrast(toHex(c.accent!), toHex(c.background!))).toBeGreaterThanOrEqual(4.5);
        });
        it(`inverse fosc (${nom}): accent >= 4.5:1 sobre el fons`, () => {
            const c = lot2MenuColors(themes.light, 'inverse');
            expect(contrast(toHex(c.accent!), toHex(c.background!))).toBeGreaterThanOrEqual(4.5);
        });
        it(`inverse clar (${nom}): l'accent es queda al color base`, () => {
            const c = lot2MenuColors(themes.dark, 'inverse');
            expect(c.accent).toBe(colors.main);
        });
    }
    it('inverse fosc usa colors.light quan arriba a 4.5:1', () => {
        const themes = createLot2Themes(NOTIB);
        expect(lot2MenuColors(themes.light, 'inverse').accent).toBe('#a8db7a');
    });
});

describe('retocs dels temes', () => {
    const themes = createLot2Themes(NOTIB);
    const LIGHT = NOTIB.light;
    const outlined = (t: typeof themes.dark) => {
        const root = t.components?.MuiButton?.styleOverrides?.root as any;
        return root({ theme: t }).variants[0];
    };
    for (const nom of ['dark', 'dracula'] as const) {
        const t = themes[nom];
        it(`${nom}: outlined primary amb text.primary i vora LIGHT`, () => {
            const v = outlined(t);
            expect(v.props({ variant: 'outlined', color: 'primary' })).toBe(true);
            expect(v.props({ variant: 'contained', color: 'primary' })).toBe(false);
            expect(v.style.borderColor).toBe(LIGHT);
            expect(v.style.color).toBe(t.palette.text.primary);
        });
        it(`${nom}: focus dels camps amb vora LIGHT de 2px`, () => {
            const o = JSON.stringify(t.components?.MuiOutlinedInput?.styleOverrides);
            expect(o).toContain('Mui-focused');
            expect(o).toContain(LIGHT);
            expect(o).toContain('2px');
        });
        it(`${nom}: casella marcada d'una fila seleccionada amb SOFT`, () => {
            const o = JSON.stringify(t.components?.MuiDataGrid?.styleOverrides);
            expect(o).toContain('.MuiCheckbox-root.Mui-checked');
            expect(o).toContain(NOTIB.soft);
        });
    }
    for (const nom of ['light', 'dark', 'dracula'] as const) {
        it(`${nom}: l'autofill de Chrome pinta el text amb el text.primary del tema`, () => {
            const t = themes[nom];
            const o = (t.components as any).MuiCssBaseline.styleOverrides;
            const key = Object.keys(o).find((k) => k.includes('-webkit-autofill'))!;
            const regla = o[key];
            expect(regla.WebkitTextFillColor).toBe(`${t.palette.text.primary} !important`);
            expect(regla.caretColor).toBe(`${t.palette.text.primary} !important`);
            expect(regla.WebkitBoxShadow).toBeUndefined();
            expect(regla.transition).toContain('background-color 5000s');
        });
    }
    it('el tema clar no té outlined ni focus especials', () => {
        expect(themes.light.components?.MuiOutlinedInput).toBeUndefined();
    });
    for (const nom of ['light', 'dark', 'dracula'] as const) {
        it(`${nom}: textTransform none, autofill i gap dels diàlegs`, () => {
            const c = themes[nom].components as any;
            const btn = c.MuiButton.styleOverrides.root;
            expect((typeof btn === 'function' ? btn({ theme: themes[nom] }) : btn).textTransform).toBe('none');
            const tab = c.MuiTab.styleOverrides.root;
            const tabStyle = typeof tab === 'function' ? tab({ theme: themes[nom] }) : tab;
            expect(tabStyle.textTransform).toBe('none');
            expect(JSON.stringify(c.MuiCssBaseline.styleOverrides)).toContain('-webkit-autofill');
            expect(c.MuiDialogActions.styleOverrides.root.gap).toBe('8px');
            expect(c.MuiDialogActions.defaultProps.disableSpacing).toBe(true);
            expect(JSON.stringify(c.MuiButton)).not.toContain('marginLeft');
            expect(JSON.stringify(c.MuiButton)).not.toContain('not-allowed');
        });
    }
});

describe('separació dels botons dels diàlegs', () => {
    it('renderitzat: gap 8px i cap marginLeft al segon botó', () => {
        const themes = createLot2Themes(NOTIB);
        render(
            <ThemeProvider theme={themes.light}>
                <DialogActions data-testid="da">
                    <button data-testid="a">A</button>
                    <button data-testid="b">B</button>
                </DialogActions>
            </ThemeProvider>
        );
        expect(getComputedStyle(screen.getByTestId('da')).gap).toBe('8px');
        expect(getComputedStyle(screen.getByTestId('b')).marginLeft).not.toBe('8px');
    });
});

describe('accent del menú inverse fosc: blanc de reserva', () => {
    it("si primary.light no arriba a 4.5:1, l'accent és blanc", () => {
        const themes = createLot2Themes({ main: '#000000', dark: '#000000', light: '#111111', soft: '#ffffff' });
        const c = lot2MenuColors(themes.light, 'inverse');
        expect(c.accent).toBe('#FFFFFF');
        expect(contrast(toHex(c.accent!), toHex(c.background!))).toBeGreaterThanOrEqual(4.5);
    });
});
