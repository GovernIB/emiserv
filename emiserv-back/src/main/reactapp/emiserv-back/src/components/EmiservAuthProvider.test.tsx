import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { EmiservAuthProvider } from './EmiservAuthProvider';
import { TemaProvider } from './TemaProvider';

const BACKEND = 'http://localhost/emiservback';

const USUARI = {
    codi: 'ems_admin',
    nom: 'Admin',
    rols: ['EMS_ADMIN'],
    interficie: 'REACT',
    interficiePreferida: null,
};

const json = (status: number, body?: unknown) =>
    new Response(body != null ? JSON.stringify(body) : null, {
        status,
        headers: { 'Content-Type': 'application/json' },
    });

describe('EmiservAuthProvider', () => {
    const fetchOriginal = window.fetch;
    const locationOriginal = window.location;
    let href: string;

    beforeEach(() => {
        sessionStorage.clear();
        href = 'http://localhost/emiservback/reactapp/serveis';
        // jsdom no navega: es captura l'assignació a location.href.
        Object.defineProperty(window, 'location', {
            configurable: true,
            value: {
                ...locationOriginal,
                pathname: '/emiservback/reactapp/serveis',
                search: '',
                hash: '',
                get href() {
                    return href;
                },
                set href(valor: string) {
                    href = valor;
                },
            },
        });
    });

    afterEach(() => {
        window.fetch = fetchOriginal;
        Object.defineProperty(window, 'location', { configurable: true, value: locationOriginal });
    });

    const renderitzar = (respostaApi: Response) => {
        window.fetch = vi.fn(async (input: RequestInfo | URL) => {
            const url = typeof input === 'string' ? input : input instanceof URL ? input.href : input.url;
            return url.endsWith('/api/sessioUsuari') ? json(200, USUARI) : respostaApi;
        }) as typeof fetch;
        render(
            <TemaProvider>
                <EmiservAuthProvider apiUrl={BACKEND + '/api'}>
                    <span>contingut</span>
                </EmiservAuthProvider>
            </TemaProvider>
        );
        return screen.findByText('contingut');
    };

    it("un 401 d'una crida a l'API, poc després de comprovar la sessió, porta al login", async () => {
        await renderitzar(json(401));

        // Crida feta per la lib/ de base-react (ketting), amb un Request.
        const response = await window.fetch(new Request(BACKEND + '/api/servei?page=0'));

        expect(response.status).toBe(401);
        await waitFor(() =>
            expect(href).toBe(
                BACKEND + '/sessioUsuari/login?redirect=' + encodeURIComponent('/emiservback/reactapp/serveis')
            )
        );
    });

    it("un 403 d'una crida a l'API no porta al login", async () => {
        await renderitzar(json(403));

        await window.fetch(BACKEND + '/api/servei/1');

        expect(href).toBe('http://localhost/emiservback/reactapp/serveis');
        expect(screen.getByText('contingut')).toBeInTheDocument();
    });

    it('en desmuntar-se es restaura el fetch', async () => {
        const fetchMock = vi.fn(async () => json(200, USUARI)) as unknown as typeof fetch;
        window.fetch = fetchMock;
        const { unmount } = render(
            <TemaProvider>
                <EmiservAuthProvider apiUrl={BACKEND + '/api'}>
                    <span>contingut</span>
                </EmiservAuthProvider>
            </TemaProvider>
        );
        await screen.findByText('contingut');
        expect(window.fetch).not.toBe(fetchMock);

        unmount();

        expect(window.fetch).toBe(fetchMock);
    });
});
