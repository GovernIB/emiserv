import { useTranslation } from 'react-i18next';

/** Espai de noms i18next dels textos comuns de `lot2`. */
export const LOT2_I18N_NS = 'lot2';

/**
 * Textos comuns de `lot2` (filtres, peu). Les claus són les `comu.*` i `page.*` de Notib, sense el
 * prefix de pàgina, perquè els components de `lot2` les demanen amb `t('clau', { ns: LOT2_I18N_NS })`.
 */
export const lot2Translations: Record<'ca' | 'es' | 'en', Record<string, any>> = {
    ca: {
        neteja: 'Neteja',
        netejaFiltre: 'Netejar filtre',
        obrirFiltreAvancat: 'Obrir filtre avançat',
        tancarFiltreAvancat: 'Tancar filtre avançat',
        avancat: 'Avançat',
        simple: 'Bàsic',
        filtresAplicats_one: '{{count}} filtre aplicat',
        filtresAplicats_other: '{{count}} filtres aplicats',
        footer: { sitemap: 'Mapa del lloc web', accessibilitat: 'Accessibilitat', revisio: 'Revisió:' },
    },
    es: {
        neteja: 'Limpia',
        netejaFiltre: 'Limpiar filtro',
        obrirFiltreAvancat: 'Abrir filtro avanzado',
        tancarFiltreAvancat: 'Cerrar filtro avanzado',
        avancat: 'Avanzado',
        simple: 'Básico',
        filtresAplicats_one: '{{count}} filtro aplicado',
        filtresAplicats_other: '{{count}} filtros aplicados',
        footer: { sitemap: 'Mapa del sitio web', accessibilitat: 'Accesibilidad', revisio: 'Revisión:' },
    },
    en: {
        neteja: 'Clear',
        netejaFiltre: 'Clear filter',
        obrirFiltreAvancat: 'Open advanced filter',
        tancarFiltreAvancat: 'Close advanced filter',
        avancat: 'Advanced',
        simple: 'Basic',
        filtresAplicats_one: '{{count}} filter applied',
        filtresAplicats_other: '{{count}} filters applied',
        footer: { sitemap: 'Site map', accessibilitat: 'Accessibility', revisio: 'Revision:' },
    },
};

/** Funció de traducció de `lot2`, independent dels tipus de recursos i18next que declari l'aplicació. */
export type Lot2TFunction = (key: string, options?: Record<string, unknown>) => string;

/** `useTranslation` sobre l'espai de noms de `lot2`, sense dependre del `CustomTypeOptions` de l'aplicació. */
export const useLot2Translation = (): { t: Lot2TFunction } => {
    const { t } = (useTranslation as unknown as (ns: string) => { t: Lot2TFunction })(LOT2_I18N_NS);
    return { t };
};
