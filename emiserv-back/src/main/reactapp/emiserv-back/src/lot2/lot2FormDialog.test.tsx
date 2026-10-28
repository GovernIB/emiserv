import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { ThemeProvider } from '@mui/material/styles';
import { BaseAppContext } from 'reactlib';
import { useFormDialog } from '../../lib/components/mui/form/FormDialog';
import { lot2DefaultMuiComponentProps } from './lot2ComponentProps';
import { createLot2Themes } from './theme';

vi.mock('../../lib/components/mui/form/MuiForm', () => ({
    default: (props: any) => {
        props.apiRef.current = { save: () => new Promise(() => {}), getId: () => undefined, getData: () => ({}) };
        return <span data-testid="form" />;
    },
}));

const theme = createLot2Themes({ main: '#83cb41', dark: '#497e3a', soft: '#F2F9EB' }).light;
const grid = lot2DefaultMuiComponentProps(theme).dataGrid!;

const Harness: React.FC = () => {
    const [show, component] = useFormDialog(
        'x', undefined, undefined, undefined, undefined, undefined, undefined, undefined,
        grid.popupEditFormDialogComponentProps, undefined, undefined, grid.popupEditFormDialogOnClose
    );
    React.useEffect(() => {
        show(1).catch(() => {});
    }, []);
    return component;
};

const renderDialog = () =>
    render(
        <ThemeProvider theme={theme}>
            <BaseAppContext.Provider value={{ t: (k: string) => k } as any}>
                <Harness />
            </BaseAppContext.Provider>
        </ThemeProvider>
    );

describe('formulari emergent amb les props de lot2', () => {
    it('un clic al backdrop no el tanca i Escape sí', async () => {
        renderDialog();
        await screen.findByTestId('form');
        const paper = document.querySelector('.MuiDialog-paper') as HTMLElement;
        expect(paper.className).toContain('MuiDialog-paperWidthMd');
        expect(paper.className).toContain('MuiDialog-paperFullWidth');
        fireEvent.click(document.querySelector('.MuiBackdrop-root') as HTMLElement);
        expect(screen.queryByTestId('form')).not.toBeNull();
        fireEvent.keyDown(paper, { key: 'Escape', code: 'Escape' });
        await waitFor(() => expect(screen.queryByTestId('form')).toBeNull());
    });
});
