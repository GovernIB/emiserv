/**
 * Detecció de la sessió caducada a les crides a l'API.
 *
 * Les crides a l'API les fa la `lib/` de base-react (ketting via `ResourceApiProvider`), que no es pot
 * modificar i que, amb un 401, només mostra «HTTP error 401». Per això s'embolcalla `fetch`: si una
 * resposta d'una URL de l'API és un 401 o una redirecció (`opaqueredirect`, el login), es crida
 * `onSenseSessio`. Un 403 (usuari sense permisos) no és una sessió caducada i no es toca.
 *
 * `api/sessioUsuari` queda fora: la consulta de la sessió ja gestiona el seu propi resultat.
 */

type AmbFetch = { fetch: typeof fetch };

const urlDeLaPeticio = (input: RequestInfo | URL): string => {
    if (typeof input === 'string') {
        return input;
    }
    if (input instanceof URL) {
        return input.href;
    }
    return input.url;
};

/**
 * Indica si una URL és de l'API (`<apiUrl>/...`) però no de la consulta de la sessió.
 *
 * @param url URL de la petició, absoluta o relativa a `baseUrl`.
 * @param apiUrl URL absoluta de l'API, sense barra final.
 * @param baseUrl URL de la pàgina, per a resoldre les URL relatives.
 */
export const esUrlApiControlada = (url: string, apiUrl: string, baseUrl: string): boolean => {
    let absoluta: string;
    try {
        absoluta = new URL(url, baseUrl).href;
    } catch {
        return false;
    }
    if (absoluta !== apiUrl && !absoluta.startsWith(apiUrl + '/') && !absoluta.startsWith(apiUrl + '?')) {
        return false;
    }
    const cami = absoluta.substring(apiUrl.length);
    return !/^\/sessioUsuari(?=[/?#]|$)/.test(cami);
};

/**
 * Embolcalla `objectiu.fetch` perquè les respostes de l'API sense sessió cridin `onSenseSessio`.
 * La resposta es retorna igualment a qui ha fet la crida.
 *
 * @param objectiu objecte que té el `fetch` (normalment `window`).
 * @param apiUrl URL de l'API (absoluta o relativa a la pàgina).
 * @param onSenseSessio acció quan la sessió ha caducat.
 * @param baseUrl URL de la pàgina, per a resoldre les URL relatives.
 * @returns funció que restaura el `fetch` original.
 */
export const instalarInterceptorSessio = (
    objectiu: AmbFetch,
    apiUrl: string,
    onSenseSessio: () => void,
    baseUrl: string = window.location.href
): (() => void) => {
    const fetchOriginal = objectiu.fetch;
    const apiUrlAbsoluta = new URL(apiUrl, baseUrl).href.replace(/\/+$/, '');
    const fetchInterceptat: typeof fetch = async (input, init) => {
        const response = await fetchOriginal.call(objectiu, input, init);
        if (
            (response.status === 401 || response.type === 'opaqueredirect') &&
            esUrlApiControlada(urlDeLaPeticio(input), apiUrlAbsoluta, baseUrl)
        ) {
            onSenseSessio();
        }
        return response;
    };
    objectiu.fetch = fetchInterceptat;
    return () => {
        // Només es restaura si ningú l'ha tornat a embolcallar després.
        if (objectiu.fetch === fetchInterceptat) {
            objectiu.fetch = fetchOriginal;
        }
    };
};
