import i18next from 'i18next';
import { initReactI18next } from 'react-i18next';
import translationCa from './translationCa';
import translationEs from './translationEs';
import { LOT2_I18N_NS, lot2Translations } from '../lot2/i18n';

/** Idiomes de l'aplicació, els mateixos que la interfície JSP. */
export const IDIOMES = ['ca', 'es'] as const;

const resources = {
    ca: { translation: translationCa, [LOT2_I18N_NS]: lot2Translations.ca },
    es: { translation: translationEs, [LOT2_I18N_NS]: lot2Translations.es },
};

/** Idioma del navegador si és un dels de l'aplicació; si no, el català. */
export const idiomaNavegador = (): string => {
    const preferits = navigator.languages?.length ? navigator.languages : [navigator.language];
    for (const idioma of preferits) {
        const codi = idioma?.substring(0, 2).toLowerCase();
        if ((IDIOMES as readonly string[]).includes(codi)) {
            return codi;
        }
    }
    return 'ca';
};

i18next.use(initReactI18next).init({
    resources,
    lng: idiomaNavegador(),
    fallbackLng: 'ca',
    interpolation: {
        escapeValue: false,
    },
});

export default i18next;
