import { describe, it, expect, vi } from 'vitest';
import { render } from '@testing-library/react';
import { ThemeProvider } from '@mui/material/styles';
import { BaseAppContext, MuiBaseAppContext, MuiDataGrid } from 'reactlib';
import type { MuiBaseAppAppearance, MuiDataGridProps } from 'reactlib';
import { lot2DefaultMuiComponentProps, lot2ScreenGridProps } from './lot2ComponentProps';
import { createLot2Themes } from './theme';
import { titleBarSx } from './titleBarControlsSx';
import { LOT2_APPEARANCE } from './menuColors';

// Es simulen l'API de recursos i l'autenticació per renderitzar només la graella.
vi.mock('../../lib/components/ResourceApiProvider', () => ({
    useResourceApiService: () => ({
        isReady: true,
        currentActions: {},
        currentFields: [],
        find: () => Promise.resolve({ rows: [], page: {} }),
        delete: () => Promise.resolve(),
        bulkDelete: () => Promise.resolve(),
    }),
}));
vi.mock('../../lib/components/ResourceApiContext', () => ({
    useResourceApiContext: () => ({ currentLanguage: 'ca' }),
}));
vi.mock('../../lib/components/AuthContext', () => ({
    useAuthContext: () => ({ isAuthenticated: true, getUserId: () => 'u' }),
}));

const theme = createLot2Themes({ main: '#83cb41', dark: '#497e3a', soft: '#F2F9EB' }).light;

const baseApp: any = {
    t: (k: string) => k,
    goBack: () => {},
    getLinkComponent: () => 'a',
    code: 'c',
    currentLanguage: 'ca',
    topLevelRouteChanged: () => {},
    temporalMessageShow: () => {},
    messageDialogShow: () => Promise.resolve(false),
};

const renderGrid = (props: Partial<MuiDataGridProps> = {}) =>
    render(
        <ThemeProvider theme={theme}>
            <BaseAppContext.Provider value={baseApp}>
                <MuiBaseAppContext.Provider
                    value={{
                        defaultMuiComponentProps: lot2DefaultMuiComponentProps(theme),
                        appearance: {} as MuiBaseAppAppearance,
                    }}>
                    <MuiDataGrid resourceName="x" columns={[{ field: 'a' }]} title="Graella" {...props} />
                </MuiBaseAppContext.Provider>
            </BaseAppContext.Provider>
        </ThemeProvider>
    );

const toolbarEl = (c: HTMLElement) => c.querySelector('.MuiToolbar-root') as HTMLElement;
const gridEl = (c: HTMLElement) => c.querySelector('.MuiDataGrid-root') as HTMLElement;

describe('MuiDataGrid amb les props de lot2', () => {
    it('amb només les props per defecte, la barra és la de la llibreria (sense barra de títol)', () => {
        const { container } = renderGrid();
        const toolbar = getComputedStyle(toolbarEl(container));
        expect(toolbar.borderLeftStyle).not.toBe('solid');
        expect(toolbar.backgroundImage).not.toContain('linear-gradient');
        expect(getComputedStyle(gridEl(container)).margin).not.toBe('15px');
    });

    it('amb lot2ScreenGridProps, la barra superior és la barra de títol i la graella té 15px de marge', () => {
        const { container } = renderGrid(lot2ScreenGridProps(theme));
        const toolbar = getComputedStyle(toolbarEl(container));
        expect(toolbar.borderLeftStyle).toBe('solid');
        expect(toolbar.borderLeftWidth).toBe('4px');
        expect(getComputedStyle(gridEl(container)).margin).toBe('15px');
    });

    it('el títol amb subtítol no ocupa el 100% de la fila de la barra', () => {
        const { container } = renderGrid({ ...lot2ScreenGridProps(theme), title: 'Graella', subtitle: 'Sub' } as any);
        const wrapper = container.querySelector('.MuiToolbar-root h2')!.parentElement as HTMLElement;
        expect(wrapper.style.width).toBe('');
        expect(wrapper.style.minWidth).toBe('0px');
    });

    it('amb lot2ScreenGridProps, la barra es pot partir en dues files i el filtre ràpid fa 32px', () => {
        const { container } = renderGrid(lot2ScreenGridProps(theme));
        const toolbar = getComputedStyle(toolbarEl(container));
        expect(toolbar.flexWrap).toBe('wrap');
        expect(toolbar.rowGap).toBe(theme.spacing(1));
        const sx: any = titleBarSx(theme);
        expect(sx['& .MuiOutlinedInput-root'].height).toBe(32);
        expect(sx['& .MuiOutlinedInput-root']['& .MuiOutlinedInput-input']).toMatchObject({ paddingTop: 0, paddingBottom: 0 });
    });
});

describe('lot2DefaultMuiComponentProps', () => {
    const d = lot2DefaultMuiComponentProps(theme);
    it('valors per defecte de la graella', () => {
        expect(d.dataGrid).toMatchObject({
            toolbarCreateVariant: 'responsive',
            toolbarTitleComponent: 'h2',
            toolbarQuickFilterCollapsible: true,
            popupEditFormDialogComponentProps: { fullWidth: true, maxWidth: 'md' },
        });
        expect(d.dataGrid!.popupEditFormDialogOnClose!('backdropClick')).toBe(false);
        expect(d.dataGrid!.popupEditFormDialogOnClose!('escapeKeyDown')).toBe(true);
        expect(d.dataGrid!.popupEditFormDialogOnClose!(undefined)).toBe(true);
        expect(d.dataGrid).not.toHaveProperty('avoidSubmitIfAnyValidatorErrors');
    });
    it('formulari i llista: títol h2', () => {
        expect(d.form!.toolbarTitleComponent).toBe('h2');
        expect(d.dataList!.toolbarTitleComponent).toBe('h2');
    });
    it('LOT2_APPEARANCE activa la semàntica de llista del menú', () => {
        expect(LOT2_APPEARANCE.menuListItemSemantics).toBe(true);
    });
});

describe('barra de títol: separació igual entre els controls', () => {
    const sx: any = titleBarSx(theme);
    it('un sol gap de 8px entre els controls (columnGap) i cap marge propi als botons d\'icona', () => {
        expect(sx.columnGap).toBe(1);
        expect(sx['& .MuiIconButton-root'].marginLeft).toBeUndefined();
        expect(sx['& .MuiIconButton-root'].margin).toBe(0);
        expect(sx['& .MuiTextField-root'].margin).toBe(0);
    });
    it('a la graella, la barra té columnGap de 8px i els controls no tenen marges', () => {
        const { container } = renderGrid({
            ...lot2ScreenGridProps(theme),
            toolbarBackButton: true,
            toolbarHideExport: false,
            toolbarQuickFilterCollapsible: false,
        });
        const toolbar = toolbarEl(container);
        expect(getComputedStyle(toolbar).columnGap).toBe('8px');
        const iconButtons = toolbar.querySelectorAll('.MuiIconButton-root');
        expect(iconButtons.length).toBeGreaterThanOrEqual(3);
        iconButtons.forEach((b) => {
            const style = getComputedStyle(b);
            expect(style.marginLeft).toBe('0px');
            expect(style.marginRight).toBe('0px');
        });
        const quickFilter = toolbar.querySelector('.MuiTextField-root') as HTMLElement;
        expect(getComputedStyle(quickFilter).marginLeft).toBe('0px');
    });
    it('el filtre ràpid plegat tampoc no té marge', () => {
        const { container } = renderGrid(lot2ScreenGridProps(theme));
        const search = toolbarEl(container).querySelector('button[aria-label="datacommon.quickfilter.label"]')!;
        expect(getComputedStyle(search).marginLeft).toBe('0px');
    });
});

describe('barra de títol: aparença dels botons de la barra', () => {
    it('lot2 no canvia toolbarButtonsVariant (per defecte, icones)', () => {
        expect(lot2DefaultMuiComponentProps(theme).dataGrid).not.toHaveProperty('toolbarButtonsVariant');
        expect(lot2ScreenGridProps(theme)).not.toHaveProperty('toolbarButtonsVariant');
    });
    it("'icon': el refrescar té l'aspecte de botó d'icona de la barra (sense vora)", () => {
        const { container } = renderGrid(lot2ScreenGridProps(theme));
        const refresh = toolbarEl(container).querySelector('[title="datacommon.refresh.label"]') as HTMLElement;
        expect(refresh.className).toContain('MuiIconButton-root');
        expect(getComputedStyle(refresh).padding).toBe('5px');
    });
    it("'button': el refrescar té l'aspecte del botó de crear (fons del paper i vora)", () => {
        const { container } = renderGrid({ ...lot2ScreenGridProps(theme), toolbarButtonsVariant: 'button' });
        const refresh = Array.from(toolbarEl(container).querySelectorAll('.MuiButton-root')).find((b) =>
            b.textContent?.includes('datacommon.refresh.label')
        ) as HTMLElement;
        const style = getComputedStyle(refresh);
        expect(style.borderTopStyle).toBe('solid');
        expect(style.borderTopWidth).toBe('1px');
        expect(style.backgroundColor).toBe('rgb(255, 255, 255)');
        expect(style.textTransform).toBe('none');
    });
});
