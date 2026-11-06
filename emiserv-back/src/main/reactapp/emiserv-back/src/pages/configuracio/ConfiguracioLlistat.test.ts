import { describe, it, expect } from 'vitest';
import { construirFiltreConfiguracio } from './ConfiguracioLlistat';
import { MANTENIMENTS, mantenimentPerClau } from './manteniments';

describe('configuracio', () => {
    it('construeix el filtre «conté» amb els camps amb valor', () => {
        expect(construirFiltreConfiguracio({ cif: ' B07 ', nom: "l'Illa", codi: '', altre: null })).toBe(
            "cif~'%B07%' and nom~'%l\\'Illa%'"
        );
        expect(construirFiltreConfiguracio({})).toBe('');
    });

    it('té els deu manteniments de la JSP amb claus úniques', () => {
        expect(MANTENIMENTS.map((m) => m.clau)).toEqual([
            'aplicacio',
            'emisor',
            'organisme',
            'organismeCessionari',
            'clauPublica',
            'clauPrivada',
            'autoritatCertificacio',
            'scspModul',
            'scspParametre',
            'entitat',
        ]);
        expect(mantenimentPerClau('entitat')?.recurs).toBe('entitat');
        expect(mantenimentPerClau('propietats')).toBeUndefined();
    });

    it('només els organismes (SCSP i cessionaris) tenen esborrat múltiple', () => {
        expect(MANTENIMENTS.filter((m) => m.esborratMultiple).map((m) => m.clau)).toEqual([
            'organisme',
            'organismeCessionari',
        ]);
    });
});
