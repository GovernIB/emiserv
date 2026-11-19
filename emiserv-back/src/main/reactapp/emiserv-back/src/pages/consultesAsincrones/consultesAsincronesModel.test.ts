import { describe, expect, it } from 'vitest';
import {
    esperantReintent,
    formatData,
    hiHaEnCurs,
    intentsRestants,
    solicitudReintentable,
    tempsFins,
    trams,
} from './consultesAsincronesModel';

describe('consultesAsincronesModel', () => {
    it('calcula els trams de la barra de progrés', () => {
        expect(trams({ total: 4, tramitades: 2, errors: 1, enviant: 1 })).toEqual({
            tramitades: 50,
            errors: 25,
            enviant: 25,
        });
        expect(trams({ total: 0, tramitades: 0, errors: 0, enviant: 0 })).toEqual({
            tramitades: 0,
            errors: 0,
            enviant: 0,
        });
    });

    it("només s'actualitza si hi ha consultes en curs", () => {
        expect(hiHaEnCurs(undefined)).toBe(false);
        expect(hiHaEnCurs([{ finalitzada: true }])).toBe(false);
        expect(hiHaEnCurs([{ finalitzada: true }, { finalitzada: false }])).toBe(true);
    });

    it('reintents de les sol·licituds', () => {
        const enCurs = { finalitzada: false };
        expect(solicitudReintentable(enCurs, { estat: 'ERROR' })).toBe(true);
        expect(solicitudReintentable(enCurs, { estat: 'PENDENT', dataProperIntent: 1000 })).toBe(true);
        expect(solicitudReintentable(enCurs, { estat: 'PENDENT' })).toBe(false);
        expect(solicitudReintentable(enCurs, { estat: 'TRAMITADA' })).toBe(false);
        expect(solicitudReintentable({ finalitzada: true }, { estat: 'ERROR' })).toBe(false);

        expect(esperantReintent({ estat: 'PENDENT', dataProperIntent: 1000, intents: 1 })).toBe(true);
        expect(esperantReintent({ estat: 'PENDENT', intents: 0 })).toBe(false);

        expect(intentsRestants({ estat: 'PENDENT', intents: 1, intentsMaxim: 3 })).toBe(2);
        expect(intentsRestants({ estat: 'ERROR', intents: 3, intentsMaxim: 3 })).toBeNull();
        expect(intentsRestants({ estat: 'PENDENT', intents: 1 })).toBeNull();
    });

    it('formata les dates i el temps que falta', () => {
        expect(formatData(new Date(2026, 10, 18, 14, 5, 9).getTime())).toBe('18/11/2026 14:05:09');
        expect(formatData(null)).toBe('');
        const ara = new Date(2026, 10, 18, 14, 0, 0).getTime();
        expect(tempsFins(ara + 30 * 1000, ara)).toEqual({ valor: 30, unitat: 's' });
        expect(tempsFins(ara + 150 * 1000, ara)).toEqual({ valor: 3, unitat: 'min' });
        expect(tempsFins(ara - 1000, ara)).toBeNull();
    });
});
