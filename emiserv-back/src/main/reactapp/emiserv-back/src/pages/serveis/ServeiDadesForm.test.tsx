import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';

// Dades del formulari i de la sessió que fa servir cada test
const estat = vi.hoisted(() => ({
    data: {} as Record<string, unknown>,
    fakePermes: false,
}));

vi.mock('react-i18next', () => ({
    useTranslation: () => ({ t: (clau: string) => clau }),
}));
vi.mock('reactlib', () => ({
    useFormContext: () => ({ data: estat.data }),
    FormField: (props: { name: string; disabled?: boolean }) => (
        <input data-testid={`camp-${props.name}`} disabled={props.disabled} />
    ),
}));
vi.mock('../../components/EmiservAuthProvider', () => ({
    useSessioUsuari: () => ({ usuari: { fakePermes: estat.fakePermes } }),
}));

import { ServeiDadesForm } from './ServeiDadesForm';

describe('ServeiDadesForm mode fake', () => {
    it('amagaModeFakeSiNoEsPermes', () => {
        estat.data = { tipus: 'BACKOFFICE', fake: true };
        estat.fakePermes = false;
        render(<ServeiDadesForm existent />);
        expect(screen.queryByTestId('camp-fake')).toBeNull();
        expect(screen.queryByTestId('camp-fakeResultat')).toBeNull();
        expect(screen.queryByTestId('camp-fakeDadesEspecifiques')).toBeNull();
    });

    it('mostraModeFakeSiEsPermes', () => {
        for (const tipus of ['BACKOFFICE', 'ENRUTADOR', 'ENRUTADOR_MULTIPLE']) {
            estat.data = { tipus, fake: false };
            estat.fakePermes = true;
            const { unmount } = render(<ServeiDadesForm existent />);
            expect(screen.getByTestId('camp-fake')).toBeEnabled();
            // Resultat i plantilla només s'editen amb el mode fake actiu
            expect(screen.getByTestId('camp-fakeResultat')).toBeDisabled();
            expect(screen.getByTestId('camp-fakeDadesEspecifiques')).toBeDisabled();
            unmount();
        }
        estat.data = { tipus: 'BACKOFFICE', fake: true };
        render(<ServeiDadesForm existent />);
        expect(screen.getByTestId('camp-fakeResultat')).toBeEnabled();
        expect(screen.getByTestId('camp-fakeDadesEspecifiques')).toBeEnabled();
    });
});
