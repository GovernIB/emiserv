import React from 'react';
import { useLocation } from 'react-router-dom';
import { useSessioUsuari } from './EmiservAuthProvider';

/**
 * Pàgina JSP equivalent a cada ruta de la interfície REACT (camí relatiu a l'arrel de l'aplicació).
 *
 * Totes van a una pàgina JSP concreta i no a l'arrel (`/emiservback/`): l'arrel la decideix
 * `IndexController`, que redirigeix a la REACT si aquesta és la interfície efectiva de l'usuari, i
 * l'enllaç faria un bucle.
 */
const RUTES_JSP: Record<string, string> = {
    '/serveis': 'servei',
};

/** Pàgina JSP per a les rutes sense equivalent (la mateixa on aterra la JSP en entrar). */
const RUTA_JSP_PER_DEFECTE = 'servei';

/**
 * Enllaç cap a la interfície JSP equivalent a la pantalla actual. La JSP és al mateix servidor que
 * l'API (veure `EmiservAuthProvider`); amb `npm run dev`, al backend, no a Vite.
 */
export const useInterficieClassica = () => {
    const { backendBaseUrl } = useSessioUsuari();
    const { pathname } = useLocation();
    const getUrl = React.useCallback(() => {
        const base = import.meta.env.VITE_DEV_BACKEND_URL
            ? import.meta.env.VITE_DEV_BACKEND_URL.replace(/\/+$/, '') + new URL(backendBaseUrl).pathname
            : backendBaseUrl;
        const ruta = pathname.replace(/\/+$/, '') || '/';
        const rutaJsp = Object.prototype.hasOwnProperty.call(RUTES_JSP, ruta) ? RUTES_JSP[ruta] : RUTA_JSP_PER_DEFECTE;
        return base.replace(/\/+$/, '') + '/' + rutaJsp;
    }, [backendBaseUrl, pathname]);
    const anarAInterficieClassica = React.useCallback(() => {
        // Sortida "dura" del SPA: la JSP és una altra aplicació, no una ruta de React Router.
        window.location.href = getUrl();
    }, [getUrl]);
    return { getUrl, anarAInterficieClassica };
};
