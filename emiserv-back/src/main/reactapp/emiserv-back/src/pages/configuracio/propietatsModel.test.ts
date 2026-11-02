import { describe, it, expect } from 'vitest';
import { comptarPropietats, filtrarGrups, type GrupPropietats, trossosRessaltats } from './propietatsModel';

const grups: GrupPropietats[] = [
    {
        key: 'GENERAL',
        descripcio: 'General',
        propietats: [
            { key: 'es.caib.emiserv.xsd.base.path', descripcio: 'Carpeta dels XSD', value: '/home/xsd' },
            { key: 'es.caib.emiserv.pinbal.password', descripcio: 'Contrasenya', value: null, contrasenya: true },
        ],
        grups: [
            {
                key: 'BACKOFFICE',
                descripcio: 'Backoffice',
                propietats: [{ key: 'es.caib.emiserv.backoffice.url', descripcio: 'URL', value: 'http://xsd.local' }],
                grups: [],
            },
        ],
    },
    {
        key: 'ASYNC',
        descripcio: 'Asíncrones',
        propietats: [{ key: 'es.caib.emiserv.async', descripcio: 'Interval' }],
        grups: [],
    },
];

describe('propietatsModel', () => {
    it('filtra per descripció, clau o valor i descarta els grups buits', () => {
        const filtrats = filtrarGrups(grups, 'XSD');
        expect(filtrats.map((g) => g.key)).toEqual(['GENERAL']);
        expect(filtrats[0].propietats.map((p) => p.key)).toEqual(['es.caib.emiserv.xsd.base.path']);
        // El subgrup coincideix pel valor
        expect(filtrats[0].grups[0].propietats).toHaveLength(1);
        expect(comptarPropietats(filtrats[0])).toBe(2);
        expect(filtrarGrups(grups, '  ')).toBe(grups);
        expect(filtrarGrups(grups, 'no-hi-és')).toEqual([]);
    });

    it('no cerca en el valor de les contrasenyes', () => {
        const ambValor = [
            { ...grups[0], propietats: [{ key: 'k', descripcio: 'd', value: 'secret', contrasenya: true }], grups: [] },
        ];
        expect(filtrarGrups(ambValor, 'secret')).toEqual([]);
    });

    it('trosseja el text per ressaltar les coincidències sense distingir majúscules', () => {
        expect(trossosRessaltats('Carpeta XSD i xsd', 'xsd')).toEqual([
            { text: 'Carpeta ', coincident: false },
            { text: 'XSD', coincident: true },
            { text: ' i ', coincident: false },
            { text: 'xsd', coincident: true },
        ]);
        expect(trossosRessaltats('Text', '')).toEqual([{ text: 'Text', coincident: false }]);
    });
});
