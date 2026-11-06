import { describe, it, expect } from 'vitest';
import { construirFiltreAuditoria, datesDrecera } from './auditoriaModel';

describe('auditoriaModel', () => {
    it('construeix el filtre amb el servei per id (backoffice) o per codi (enrutador)', () => {
        const dades = {
            servei: 12,
            procediment: "PRO'1",
            numeroPeticio: ' PET ',
            estat: 'ERROR',
            dataInici: '2026-10-01T00:00:00',
            dataFi: '',
        };
        expect(construirFiltreAuditoria('backoffice', dades)).toBe(
            "servei:'12' and procediment:'PRO\\'1' and numeroPeticio:'PET' and estat:'ERROR' and dataPeticio>:'2026-10-01T00:00:00'"
        );
        expect(construirFiltreAuditoria('enrutador', { servei: 'SVDX', dataFi: '2026-10-30T00:00:00' })).toBe(
            "serveiCodi:'SVDX' and dataPeticio<:'2026-10-30T00:00:00'"
        );
        expect(construirFiltreAuditoria('enrutador', {})).toBe('');
    });

    it('calcula les dreceres de dates', () => {
        const avui = new Date(2026, 9, 30, 15, 20);
        expect(datesDrecera('avui', avui)).toEqual({ dataInici: '2026-10-30T00:00:00', dataFi: '2026-10-30T00:00:00' });
        expect(datesDrecera('setDies', avui).dataInici).toBe('2026-10-24T00:00:00');
        expect(datesDrecera('aquestMes', avui).dataInici).toBe('2026-10-01T00:00:00');
    });
});
