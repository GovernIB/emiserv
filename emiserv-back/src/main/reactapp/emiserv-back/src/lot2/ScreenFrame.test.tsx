import { describe, it, expect } from 'vitest';
import { readFileSync } from 'fs';
import { join } from 'path';
import { render, screen } from '@testing-library/react';
import { ThemeProvider } from '@mui/material/styles';
import { ScreenFrame } from './ScreenFrame';
import { ScreenTitle } from './ScreenTitle';
import { formScreenTitle } from './formScreenTitle';
import { lot2DefaultMuiComponentProps, lot2ScreenGridProps } from './lot2ComponentProps';
import { createLot2Themes } from './theme';
import { MAIN_PADDING } from './layout';

const themes = createLot2Themes({ main: '#83cb41', dark: '#497e3a', soft: '#F2F9EB' });

describe('ScreenFrame', () => {
    it('renderitza el títol i la icona a la barra pròpia', () => {
        render(
            <ThemeProvider theme={themes.light}>
                <ScreenFrame icon="home" title="Inici" actions={<button>Acció</button>}>
                    <p>Contingut</p>
                </ScreenFrame>
            </ThemeProvider>
        );
        expect(screen.getByRole('heading', { level: 1, name: 'Inici' })).toBeInTheDocument();
        expect(screen.getByText('home')).toHaveAttribute('aria-hidden');
        expect(screen.getByRole('button', { name: 'Acció' })).toBeInTheDocument();
        expect(screen.getByText('Contingut')).toBeInTheDocument();
    });

    it('sense icona ni títol no pinta la barra pròpia', () => {
        render(
            <ScreenFrame>
                <p>Contingut</p>
            </ScreenFrame>
        );
        expect(screen.queryByRole('heading')).toBeNull();
    });

    it('no depèn de l\'estructura del DOM de la llibreria', () => {
        const source = readFileSync(join(__dirname, 'ScreenFrame.tsx'), 'utf8');
        expect(source).not.toContain(':has(');
        expect(source).not.toContain('> div');
        expect(source).not.toContain('.MuiToolbar-root');
    });
});

describe('formScreenTitle', () => {
    it('torna un ScreenTitle per al title de MuiForm', () => {
        render(formScreenTitle('home', 'Formulari'));
        expect(screen.getByRole('heading', { level: 1, name: 'Formulari' })).toBeInTheDocument();
    });

    it('ScreenTitle fora d\'un ScreenFrame també es pinta', () => {
        render(<ScreenTitle icon="map" title="Mapa" />);
        expect(screen.getByText('map')).toBeInTheDocument();
    });
});

describe('lot2DefaultMuiComponentProps', () => {
    const props = lot2DefaultMuiComponentProps(themes.light);

    it('graella: sense barra de títol ni marges (és opt-in amb lot2ScreenGridProps)', () => {
        expect(props.dataGrid).not.toHaveProperty('toolbarType');
        expect(props.dataGrid).not.toHaveProperty('toolbarSx');
        expect(props.dataGrid).not.toHaveProperty('marginsSx');
    });

    it('formulari: barra de títol enganxosa i marges de Notib', () => {
        const toolbarSx = props.form?.toolbarSx as any;
        expect(toolbarSx.top).toBe(0);
        expect(toolbarSx.zIndex).toBe(3);
        expect(toolbarSx.borderLeft).toBe(`4px solid ${themes.light.palette.primary.main}`);
        expect(toolbarSx.background).toContain('linear-gradient');
        expect(props.form?.marginsSx).toEqual({ m: `${MAIN_PADDING}px` });
    });
});

describe('lot2ScreenGridProps', () => {
    const props = lot2ScreenGridProps(themes.light);

    it('barra superior com a barra de títol, sense enganxament, i marges de 15px', () => {
        expect(props.toolbarType).toBe('upper');
        expect(props.marginsSx).toEqual({ m: `${MAIN_PADDING}px` });
        const toolbarSx = props.toolbarSx as any;
        expect(toolbarSx.borderLeft).toBe(`4px solid ${themes.light.palette.primary.main}`);
        expect(toolbarSx.background).toContain('linear-gradient');
        expect(toolbarSx).not.toHaveProperty('top');
        expect(toolbarSx).not.toHaveProperty('zIndex');
    });
});
