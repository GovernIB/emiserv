import { describe, it, expect, vi } from 'vitest';
import { esUrlApiControlada, instalarInterceptorSessio } from './interceptorSessio';

const PAGINA = 'http://localhost:8080/emiservback/reactapp/serveis';
const API = 'http://localhost:8080/emiservback/api';

const resposta = (status: number, type: ResponseType = 'basic') =>
    ({ status, type, ok: status >= 200 && status < 300 }) as Response;

const objectiuAmb = (r: Response) => ({ fetch: vi.fn(async () => r) as unknown as typeof fetch });

describe('interceptorSessio', () => {
    it("un 401 d'una crida a l'API avisa que no hi ha sessió i retorna la resposta", async () => {
        const r = resposta(401);
        const objectiu = objectiuAmb(r);
        const onSenseSessio = vi.fn();
        instalarInterceptorSessio(objectiu, '../api', onSenseSessio, PAGINA);

        const obtinguda = await objectiu.fetch(new Request(API + '/servei?page=0'));

        expect(obtinguda).toBe(r);
        expect(onSenseSessio).toHaveBeenCalledTimes(1);
    });

    it("una redirecció al login (opaqueredirect) d'una crida a l'API també avisa", async () => {
        const objectiu = objectiuAmb(resposta(0, 'opaqueredirect'));
        const onSenseSessio = vi.fn();
        instalarInterceptorSessio(objectiu, API, onSenseSessio, PAGINA);

        await objectiu.fetch(API + '/servei/1', { redirect: 'manual' });

        expect(onSenseSessio).toHaveBeenCalledTimes(1);
    });

    it('un 403 (sense permisos) no es tracta com a sessió caducada', async () => {
        const objectiu = objectiuAmb(resposta(403));
        const onSenseSessio = vi.fn();
        instalarInterceptorSessio(objectiu, API, onSenseSessio, PAGINA);

        await objectiu.fetch(API + '/servei/1');

        expect(onSenseSessio).not.toHaveBeenCalled();
    });

    it("no avisa per a URL de fora de l'API ni per a la consulta de la sessió", async () => {
        const objectiu = objectiuAmb(resposta(401));
        const onSenseSessio = vi.fn();
        instalarInterceptorSessio(objectiu, API, onSenseSessio, PAGINA);

        await objectiu.fetch('http://localhost:8080/emiservback/sysenv');
        await objectiu.fetch(API + '/sessioUsuari');
        await objectiu.fetch(new URL(API + '/sessioUsuari/interficie'));
        await objectiu.fetch('http://altre:8080/emiservback/api/servei');

        expect(onSenseSessio).not.toHaveBeenCalled();
    });

    it('restaura el fetch original', () => {
        const objectiu = objectiuAmb(resposta(200));
        const original = objectiu.fetch;
        const restaurar = instalarInterceptorSessio(objectiu, API, vi.fn(), PAGINA);
        expect(objectiu.fetch).not.toBe(original);

        restaurar();

        expect(objectiu.fetch).toBe(original);
    });

    it("distingeix l'API de camins que només hi comencen igual", () => {
        expect(esUrlApiControlada(API, API, PAGINA)).toBe(true);
        expect(esUrlApiControlada('/emiservback/api/servei', API, PAGINA)).toBe(true);
        expect(esUrlApiControlada('/emiservback/apiX/servei', API, PAGINA)).toBe(false);
        expect(esUrlApiControlada('/emiservback/api/sessioUsuariX', API, PAGINA)).toBe(true);
    });
});
