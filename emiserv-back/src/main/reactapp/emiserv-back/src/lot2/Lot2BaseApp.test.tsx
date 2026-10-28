import React from 'react';
import { describe, it, expect, vi, afterEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom/vitest';
import { ThemeProvider } from '@mui/material/styles';
import { ResourceApiContext, useMuiBaseAppContext } from 'reactlib';
import { Lot2BaseApp, type Lot2BaseAppProps } from './Lot2BaseApp';
import { DrassanaFooter } from './DrassanaFooter';
import { lot2MenuColors, LOT2_APPEARANCE } from './menuColors';
import { createLot2Themes } from './theme';
import { lot2HeaderLogoStyle } from './Lot2BaseApp';
import {
    APPBAR_PADDING_LEFT,
    FOOTER_HEIGHT,
    LOGO_BOX_LEFT,
    LOGO_SEPARATOR_GAP,
    MAIN_GAP,
    MAIN_PADDING,
    MENU_WIDTH,
} from './layout';

const themes = createLot2Themes({ main: '#83cb41', dark: '#497e3a', soft: '#F2F9EB' });

const resourceApiStub: any = {
    offline: false,
    isLoading: false,
    isReady: true,
    setOpenAnswerRequiredDialog: () => {},
    getOpenAnswerRequiredDialog: () => undefined,
};

let lastContext: any;
const ContextProbe: React.FC = () => {
    lastContext = useMuiBaseAppContext();
    return null;
};

const ReactiveResourceApi: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [currentLanguage, setCurrentLanguage] = React.useState<string | undefined>();
    const value = React.useMemo(
        () => ({ ...resourceApiStub, currentLanguage, setCurrentLanguage }),
        [currentLanguage]
    );
    return <ResourceApiContext.Provider value={value}>{children}</ResourceApiContext.Provider>;
};

const renderApp = (props: Partial<Lot2BaseAppProps> = {}, theme = themes.light) =>
    render(
        <ThemeProvider theme={theme}>
            <ReactiveResourceApi>
                <Lot2BaseApp
                    code="test"
                    logo="logo-clar.png"
                    headerTitle="Titol"
                    menuEntries={[{ id: 'a', title: 'A', icon: 'star', to: '/a' } as any]}
                    i18nUseTranslation={() => ({ t: (k: string) => k })}
                    routerGoBack={() => {}}
                    routerNavigate={() => {}}
                    routerAnyHistoryEntryExist={() => false}
                    routerUseLocationPath={() => '/'}
                    linkComponent="a"
                    {...props}>
                    <ContextProbe />
                </Lot2BaseApp>
            </ReactiveResourceApi>
        </ThemeProvider>
    );

const headerToolbar = () => document.querySelector('header .MuiToolbar-root') as HTMLElement;
const headerLogo = () => document.querySelector('header img') as HTMLImageElement;
const mainEl = () => document.querySelector('main') as HTMLElement;

afterEach(() => {
    lastContext = undefined;
    vi.restoreAllMocks();
});

describe('Lot2BaseApp', () => {
    it("renderitza l'AppBar de 74px, el peu de Drassana i el logo clar en tema clar", () => {
        renderApp({ logoDark: 'logo-fosc.png' });
        expect(headerToolbar().style.height).toBe('74px');
        expect(headerToolbar().style.minHeight).toBe('74px');
        // el botó de menú queda centrat a la columna d'icones del menú plegat
        expect(headerToolbar().style.paddingLeft).toBe(APPBAR_PADDING_LEFT + 'px');
        expect(headerLogo().getAttribute('src')).toBe('logo-clar.png');
        const footer = document.querySelector('footer') as HTMLElement;
        expect(footer).toBeTruthy();
        expect(footer.querySelector('img[alt="Drassana"]')).toBeTruthy();
        expect(screen.getByText('footer.sitemap')).toHaveAttribute('href', '/sitemap');
        expect(screen.getByText('footer.accessibilitat')).toHaveAttribute('href', '/accessibilitat');
    });

    it('amb tema fosc i logoDark, es mostra el logo fosc', () => {
        renderApp({ logoDark: 'logo-fosc.png' }, themes.dark);
        expect(headerLogo().getAttribute('src')).toBe('logo-fosc.png');
    });

    it('amb tema fosc sense logoDark, es mostra el logo clar', () => {
        renderApp({}, themes.dark);
        expect(headerLogo().getAttribute('src')).toBe('logo-clar.png');
    });

    it('menuAppearance="inverse" aplica el fons invers al drawer', () => {
        renderApp({ menuAppearance: 'inverse' });
        const paper = document.querySelector('.MuiDrawer-paper') as HTMLElement;
        // el tema clar té el menú amb el fons del tema fosc (paper de MUI fosc: #121212)
        expect(getComputedStyle(paper).backgroundColor).toBe('rgb(18, 18, 18)');
        expect(lastContext.appearance.menuColors).toEqual(lot2MenuColors(themes.light, 'inverse'));
    });

    it("per defecte l'aparença és LOT2_APPEARANCE amb els colors de menú del tema", () => {
        renderApp();
        expect(lastContext.appearance).toEqual({
            ...LOT2_APPEARANCE,
            menuColors: lot2MenuColors(themes.light, 'theme'),
        });
    });

    it("els defaultMuiComponentProps de l'aplicació guanyen als de lot2", () => {
        renderApp({ defaultMuiComponentProps: { dataGrid: { toolbarCreateVariant: 'icon' } } });
        expect(lastContext.defaultMuiComponentProps.dataGrid.toolbarCreateVariant).toBe('icon');
        // la resta de props de lot2 es conserven
        expect(lastContext.defaultMuiComponentProps.form.marginsSx).toEqual({ m: '15px' });
    });

    it('sense defaultMuiComponentProps, es fan servir els de lot2', () => {
        renderApp();
        expect(lastContext.defaultMuiComponentProps.dataGrid.toolbarCreateVariant).toBe('responsive');
    });

    it('el contingut té el padding i el gap de Notib, i els estils de l\'aplicació guanyen', () => {
        const first = renderApp();
        expect(mainEl().style.padding).toBe(MAIN_PADDING + 'px');
        expect(mainEl().style.gap).toBe(MAIN_GAP + 'px');
        expect(mainEl().style.overflowY).toBe('auto');
        // sense els marges de la llibreria (16px 24px)
        expect(mainEl().style.margin).toBe('');
        first.unmount();
        renderApp({ contentMainStyle: { padding: '3px' }, headerAppbarStyle: { paddingLeft: '5px', color: 'red' } });
        expect(mainEl().style.padding).toBe('3px');
        expect(mainEl().style.gap).toBe(MAIN_GAP + 'px');
        expect(headerToolbar().style.paddingLeft).toBe('5px');
        expect(headerToolbar().style.color).toBe('red');
    });

    it("amb menuAppearance 'inverse', la icona de l'entrada seleccionada fa servir l'accent del menú", () => {
        renderApp({ menuAppearance: 'inverse', routerUseLocationPath: () => '/a' });
        const icon = document.querySelector('.MuiListItemButton-root.Mui-selected .menu-item-icon') as HTMLElement;
        expect(icon).toBeTruthy();
        // accent de lot2MenuColors (el to clar, que té contrast amb el menú fosc), no el torrat del
        // tema actiu (primary.dark, #497e3a)
        expect(lot2MenuColors(themes.light, 'inverse').accent).toBe(themes.light.palette.primary.light);
        expect(getComputedStyle(icon).color).toBe('rgb(168, 218, 122)');
    });

    it('el peu per defecte pren el títol de headerTitle si és un text', () => {
        renderApp({ headerTitle: 'APLICACIO' });
        expect(document.querySelector('footer')!.textContent).toContain('APLICACIO');
    });

    it('el peu per defecte no té títol si headerTitle no és un text', () => {
        renderApp({ headerTitle: <span>Element</span> });
        expect(document.querySelector('footer')!.textContent).not.toContain('Element');
    });

    it('footer={false} no pinta cap peu', () => {
        renderApp({ footer: false });
        expect(document.querySelector('footer')).toBeNull();
    });

    it("footer personalitzat substitueix el de Drassana", () => {
        renderApp({ footer: <div data-testid="peu-propi" /> });
        expect(screen.getByTestId('peu-propi')).toBeInTheDocument();
        expect(document.querySelector('img[alt="Drassana"]')).toBeNull();
    });
});

describe('Lot2BaseApp: idioma, logo de capçalera', () => {
    afterEach(() => {
        document.documentElement.removeAttribute('lang');
    });

    it("sincronitza <html lang> amb l'idioma actual (dos primers caràcters)", () => {
        const r = renderApp({ i18nCurrentLanguage: 'ca-ES' });
        expect(document.documentElement.lang).toBe('ca');
        r.rerender(
            <ThemeProvider theme={themes.light}>
                <ReactiveResourceApi>
                    <Lot2BaseApp
                        code="test"
                        logo="logo-clar.png"
                        headerTitle="Titol"
                        menuEntries={[]}
                        i18nUseTranslation={() => ({ t: (k: string) => k })}
                        i18nCurrentLanguage="es"
                        routerGoBack={() => {}}
                        routerNavigate={() => {}}
                        routerAnyHistoryEntryExist={() => false}
                        routerUseLocationPath={() => '/'}
                        linkComponent="a"
                    />
                </ReactiveResourceApi>
            </ThemeProvider>
        );
        expect(document.documentElement.lang).toBe('es');
    });

    it('syncDocumentLang={false} no toca <html lang>', () => {
        renderApp({ i18nCurrentLanguage: 'en', syncDocumentLang: false });
        expect(document.documentElement.getAttribute('lang')).toBeNull();
    });

    it('lot2HeaderLogoStyle fixa amplada, separador i marges', () => {
        const style: any = lot2HeaderLogoStyle(themes.light);
        expect(style.width).toBe(MENU_WIDTH - LOGO_BOX_LEFT + 'px');
        expect(style.borderRight).toBe(`1px solid ${themes.light.palette.divider}`);
        expect(style.pr).toBe(LOGO_SEPARATOR_GAP + 'px');
        expect(style.alignSelf).toBe('stretch');
        expect(style.mr).toBe(0);
        expect(style.pt).toBe(0);
        expect(style['& img'].height).toBe('59px');
    });

    it("el logo de capçalera fa servir l'estil per defecte i l'app hi guanya", () => {
        const first = renderApp();
        const box = headerLogo().parentElement as HTMLElement;
        expect(getComputedStyle(box).width).toBe(MENU_WIDTH - LOGO_BOX_LEFT + 'px');
        expect(getComputedStyle(box).cursor).not.toBe('pointer');
        first.unmount();
        renderApp({ headerLogoStyle: { width: '100px' } });
        const box2 = headerLogo().parentElement as HTMLElement;
        expect(getComputedStyle(box2).width).toBe('100px');
        expect(getComputedStyle(box2).borderRightStyle).toBe('solid');
    });

    it("l'alt del logo GOIB és per defecte «Govern de les Illes Balears» i es pot canviar", () => {
        const first = renderApp();
        expect(document.querySelector('header img')!.getAttribute('alt')).toBe('Govern de les Illes Balears');
        first.unmount();
        renderApp({ headerLogoAlt: 'Altre' });
        expect(document.querySelector('header img')!.getAttribute('alt')).toBe('Altre');
    });
});

describe('lot2MenuColors', () => {
    it("'theme' no fixa cap color (es fa servir el tema actiu, com TEMA a Notib)", () => {
        expect(lot2MenuColors(themes.light, 'theme')).toEqual({});
    });
    it("'footer' fa servir el gris del peu i l'accent blanc", () => {
        const c = lot2MenuColors(themes.light, 'footer');
        expect(c.background).toBe('#5F5D5D');
        expect(c.textPrimary).toBe('#F6F6F6');
        expect(c.textSecondary).toBe('#E5E5E5');
        expect(c.divider).toBe('#807D7D');
        expect(c.accent).toBe('#FFFFFF');
        expect(c.selectedBackground).toBe('rgba(255,255,255,0.12)');
        expect(c.hoverBackground).toBe('rgba(255, 255, 255, 0.08)');
    });
    it("'inverse' pren els colors del tema contrari", () => {
        const fromLight = lot2MenuColors(themes.light, 'inverse');
        expect(fromLight.background).toBe('#121212');
        expect(fromLight.textPrimary).toBe('#fff');
        expect(fromLight.accent).toBe(themes.light.palette.primary.light);
        expect(fromLight.selectedBackground).toBe('rgba(131, 203, 65, 0.2)');
        const fromDark = lot2MenuColors(themes.dark, 'inverse');
        expect(fromDark.background).toBe('#fff');
        expect(fromDark.textPrimary).toBe('rgba(0, 0, 0, 0.87)');
        expect(fromDark.selectedBackground).toBe('rgba(131, 203, 65, 0.1)');
    });
    it('LOT2_APPEARANCE té les mides de Notib i la selecció amb fons', () => {
        expect(LOT2_APPEARANCE).toEqual({
            headerHeight: 74,
            menuCollapsedWidth: 74,
            menuIconSize: 24,
            menuSelectedIndicator: 'background',
            menuListItemSemantics: true,
        });
    });
});

describe('DrassanaFooter', () => {
    const renderFooter = (props: React.ComponentProps<typeof DrassanaFooter> = {}) =>
        render(
            <ThemeProvider theme={themes.light}>
                <DrassanaFooter {...props} />
            </ThemeProvider>
        );

    it('per defecte: fons gris, enllaços de mapa i accessibilitat i logo de Drassana', () => {
        const { container } = renderFooter();
        const toolbar = container.querySelector('.MuiToolbar-root') as HTMLElement;
        expect(toolbar.style.backgroundColor).toBe('rgb(95, 93, 93)');
        expect(getComputedStyle(toolbar).minHeight).toBe(FOOTER_HEIGHT + 'px');
        expect(screen.getByText('footer.sitemap')).toHaveAttribute('href', '/sitemap');
        expect(screen.getByText('footer.accessibilitat')).toHaveAttribute('href', '/accessibilitat');
        expect(container.querySelector('img[alt="Drassana"]')).toBeTruthy();
    });

    it('el logo no té cursor pointer i el span de data i revisió és aria-hidden', () => {
        const { container } = renderFooter({ title: 'APP' });
        const logoBox = container.querySelector('img[alt="Drassana"]')!.parentElement as HTMLElement;
        expect(getComputedStyle(logoBox).cursor).not.toBe('pointer');
        const span = container.querySelector('span[aria-hidden="true"]') as HTMLElement;
        expect(span).toBeTruthy();
        expect(span.textContent).toContain('footer.revisio');
        expect(span.style.marginLeft).toBe('16px');
    });

    it("accepta el títol, els enllaços, l'etiqueta de revisió i els logos de l'aplicació", () => {
        const consoleError = vi.spyOn(console, 'error').mockImplementation(() => {});
        const { container } = renderFooter({
            title: 'COMANDA',
            links: [{ label: 'Ajuda', to: '/ajuda' }],
            revisionLabel: 'Rev.',
            logos: ['a.png', 'b.png', 'a.png'],
        });
        expect(screen.getByText(/COMANDA/)).toBeInTheDocument();
        expect(screen.getByText(/Rev\./)).toBeInTheDocument();
        expect(screen.getByText('Ajuda')).toHaveAttribute('href', '/ajuda');
        expect(screen.queryByText('footer.sitemap')).toBeNull();
        expect(Array.from(container.querySelectorAll('img')).map((i) => i.getAttribute('src'))).toEqual([
            'a.png',
            'b.png',
            'a.png',
        ]);
        // un logo repetit no provoca claus duplicades
        expect(consoleError.mock.calls.flat().join(' ')).not.toMatch(/same key/);
    });

    it('mostra la versió i la revisió del manifest', () => {
        (window as any).__MANIFEST__ = {
            'Build-Timestamp': '2026-10-23',
            'Implementation-SCM-Revision': 'abc123',
            'Implementation-Version': '1.2.3',
        };
        try {
            renderFooter({ title: 'NOTIB' });
            expect(screen.getByText(/NOTIB v1\.2\.3/)).toBeInTheDocument();
            expect(screen.getByText(/2026-10-23 \| footer\.revisio abc123/)).toBeInTheDocument();
        } finally {
            delete (window as any).__MANIFEST__;
        }
    });
});
