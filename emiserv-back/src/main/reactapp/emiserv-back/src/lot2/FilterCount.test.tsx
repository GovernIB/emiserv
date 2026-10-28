import { describe, it, expect, beforeAll } from 'vitest';
import { render, screen } from '@testing-library/react';
import i18next from 'i18next';
import { I18nextProvider, initReactI18next } from 'react-i18next';
import { ScreenFrame } from './ScreenFrame';
import { FilterCount } from './FilterCount';
import { LOT2_I18N_NS, lot2Translations } from './i18n';

const i18n = i18next.createInstance();

beforeAll(async () => {
    await i18n.use(initReactI18next).init({
        lng: 'ca',
        resources: { ca: { [LOT2_I18N_NS]: lot2Translations.ca } },
        interpolation: { escapeValue: false },
    });
});

const renderWithI18n = (ui: React.ReactElement) => render(<I18nextProvider i18n={i18n}>{ui}</I18nextProvider>);

describe('FilterCount', () => {
    it('mostra el recompte al títol del ScreenFrame per portal', () => {
        renderWithI18n(
            <ScreenFrame icon="home" title="Llistat">
                <div data-testid="contingut">
                    <FilterCount springFilter="a:1 and b:2" />
                </div>
            </ScreenFrame>
        );
        const count = screen.getByText(/2 filtres aplicats/);
        const heading = screen.getByRole('heading', { level: 1, name: 'Llistat' });
        // Va a la barra de títol (al costat del títol), no al lloc on s'ha declarat
        expect(screen.getByTestId('contingut')).not.toContainElement(count);
        expect(heading.parentElement).toContainElement(count);
    });

    it('un sol filtre fa servir el singular', () => {
        renderWithI18n(
            <ScreenFrame icon="home" title="Llistat">
                <FilterCount springFilter="a:1" />
            </ScreenFrame>
        );
        expect(screen.getByText(/1 filtre aplicat/)).toBeInTheDocument();
    });

    it('sense filtres no pinta res', () => {
        renderWithI18n(
            <ScreenFrame icon="home" title="Llistat">
                <FilterCount springFilter="" />
            </ScreenFrame>
        );
        expect(screen.queryByText(/aplicat/)).toBeNull();
    });

    it('fora d\'un ScreenFrame no pinta res', () => {
        renderWithI18n(<FilterCount springFilter="a:1" />);
        expect(screen.queryByText(/aplicat/)).toBeNull();
    });

    it('el recompte es pot ajustar amb un nombre o una funció', () => {
        renderWithI18n(
            <ScreenFrame icon="home" title="Llistat">
                <FilterCount springFilter="a:1" count={3} />
            </ScreenFrame>
        );
        expect(screen.getByText(/3 filtres aplicats/)).toBeInTheDocument();
    });

    it('la funció rep el recompte de termes', () => {
        renderWithI18n(
            <ScreenFrame icon="home" title="Llistat">
                <FilterCount springFilter="a:1 and b:2" count={(terms) => terms + 1} />
            </ScreenFrame>
        );
        expect(screen.getByText(/3 filtres aplicats/)).toBeInTheDocument();
    });
});
