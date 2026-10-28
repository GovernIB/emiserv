import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { ThemeProvider } from '@mui/material/styles';
import { createLot2Themes, accentFillColor } from './theme';
import { StyledBadge } from './StyledBadge';
import { icons } from './icons';
import { lot2Translations } from './i18n';

const NOTIB = { main: '#83cb41', dark: '#497e3a', light: '#a8db7a', soft: '#F2F9EB' };

describe('lot2 icones, textos i badge', () => {
    it('avis és campaign i hi ha les icones comunes', () => {
        expect(icons.avis).toBe('campaign');
        expect(icons.crea).toBe('add');
        expect(icons.modifica).toBe('edit');
        expect(icons.esborra).toBe('delete');
        expect(icons.desa).toBe('save');
        expect(icons.cancela).toBe('close');
        expect(icons.executa).toBe('bolt');
        expect(icons.refresca).toBe('refresh');
        expect(icons.descarrega).toBe('file_download');
        expect(icons.detall).toBe('info');
        expect(icons.plega).toBe('expand_less');
        expect(icons.desplega).toBe('expand_more');
        expect(icons.seleccionaTot).toBe('check_box');
        expect(icons.cercaAvancadaObre).toBe('expand_more');
    });

    it('«simple» es mostra com a Bàsic', () => {
        expect(lot2Translations.ca.simple).toBe('Bàsic');
        expect(lot2Translations.es.simple).toBe('Básico');
        expect(lot2Translations.en.simple).toBe('Basic');
    });

    it('el badge primary fa servir accentFillColor amb text de contrast', () => {
        const theme = createLot2Themes(NOTIB).light;
        render(
            <ThemeProvider theme={theme}>
                <StyledBadge badgeContent={4} data-testid="b">
                    <span>x</span>
                </StyledBadge>
            </ThemeProvider>
        );
        const badge = screen.getByText('4');
        const style = getComputedStyle(badge);
        // normalitza els colors de la manera que ho fa el navegador (jsdom)
        const normalize = (color: string) => {
            const probe = document.createElement('div');
            probe.style.color = color;
            return probe.style.color;
        };
        const fill = accentFillColor(theme);
        expect(style.backgroundColor).toBe(normalize(fill));
        expect(style.color).toBe(normalize(theme.palette.getContrastText(fill)));
    });

    const renderBadge = (props: Record<string, string>) => {
        const theme = createLot2Themes(NOTIB).light;
        render(
            <ThemeProvider theme={theme}>
                <StyledBadge badgeContent={7} {...props}>
                    <span>x</span>
                </StyledBadge>
            </ThemeProvider>
        );
        return { theme, style: getComputedStyle(screen.getByText('7')) };
    };

    it('un badge no primary conserva el seu fons i el text blanc', () => {
        const { theme, style } = renderBadge({ badgecolor: 'error' });
        const probe = document.createElement('div');
        probe.style.color = theme.palette.error.main;
        expect(style.backgroundColor).toBe(probe.style.color);
        expect(style.color).toBe('rgb(255, 255, 255)');
    });

    it('un color no analitzable no fa petar el badge', () => {
        const { style } = renderBadge({ badgecolor: 'red' });
        expect(style.backgroundColor).toBe('rgb(255, 0, 0)');
        expect(style.color).toBe('rgb(255, 255, 255)');
    });
});
