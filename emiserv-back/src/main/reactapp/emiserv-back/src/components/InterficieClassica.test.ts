import { describe, it, expect } from 'vitest';
import { rutaJsp } from './InterficieClassica';

describe('InterficieClassica', () => {
    it('porta a la pantalla JSP equivalent (també des del detall)', () => {
        expect(rutaJsp('/serveis')).toBe('servei');
        expect(rutaJsp('/serveis/14302')).toBe('servei');
        expect(rutaJsp('/auditoria/enrutador/400988')).toBe('auditoriaEnrutador');
        expect(rutaJsp('/configuracio/clauPrivada')).toBe('scsp/clauprivada');
        expect(rutaJsp('/configuracio/propietats/')).toBe('config');
        expect(rutaJsp('/informes')).toBe('informe');
        expect(rutaJsp('/serveisX')).toBe('servei');
        expect(rutaJsp('/desconeguda')).toBe('servei');
    });
});
