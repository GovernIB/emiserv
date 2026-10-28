import React from 'react';
import { renderHook } from '@testing-library/react';
import i18next from 'i18next';
import { I18nextProvider, initReactI18next } from 'react-i18next';
import { describe, expect, it, beforeAll } from 'vitest';
import { LOT2_I18N_NS, lot2Translations, useLot2Translation } from './i18n';

const i18n = i18next.createInstance();

beforeAll(async () => {
    await i18n.use(initReactI18next).init({
        lng: 'ca',
        resources: {
            ca: { translation: { neteja: 'Clau de l\'aplicació' }, [LOT2_I18N_NS]: lot2Translations.ca },
        },
        interpolation: { escapeValue: false },
    });
});

const wrapper = ({ children }: { children: React.ReactNode }) => (
    <I18nextProvider i18n={i18n}>{children}</I18nextProvider>
);

describe('useLot2Translation', () => {
    it("tradueix amb l'espai de noms lot2, no amb el de l'aplicació", () => {
        const { result } = renderHook(() => useLot2Translation(), { wrapper });
        expect(result.current.t('neteja')).toBe('Neteja');
        expect(result.current.t('footer.sitemap')).toBe('Mapa del lloc web');
    });

    it('aplica la interpolació i els plurals', () => {
        const { result } = renderHook(() => useLot2Translation(), { wrapper });
        expect(result.current.t('filtresAplicats', { count: 1 })).toBe('1 filtre aplicat');
        expect(result.current.t('filtresAplicats', { count: 3 })).toBe('3 filtres aplicats');
    });
});
