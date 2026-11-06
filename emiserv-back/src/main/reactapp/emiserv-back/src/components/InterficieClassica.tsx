import React from 'react';
import { useTranslation } from 'react-i18next';
import { useLocation } from 'react-router-dom';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import { icons } from '../lot2';
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
    '/auditoria/backoffice': 'auditoriaBackoffice',
    '/auditoria/enrutador': 'auditoriaEnrutador',
    '/informes': 'informe',
    '/configuracio/aplicacio': 'aplicacio',
    '/configuracio/emisor': 'emisor',
    '/configuracio/organisme': 'organisme',
    '/configuracio/organismeCessionari': 'organismeCessionari',
    '/configuracio/clauPublica': 'scsp/claupublica',
    '/configuracio/clauPrivada': 'scsp/clauprivada',
    '/configuracio/autoritatCertificacio': 'scsp/autoritatca',
    '/configuracio/scspModul': 'scsp/modul',
    '/configuracio/scspParametre': 'scsp/parametres',
    '/configuracio/entitat': 'entitat',
    '/configuracio/propietats': 'config',
};

/**
 * Pàgina JSP d'una ruta: la de la ruta o la del prefix més llarg (p. ex. `/serveis/14302` o
 * `/auditoria/backoffice/<id>` van al llistat corresponent).
 */
// eslint-disable-next-line react-refresh/only-export-components
export const rutaJsp = (pathname: string): string => {
    const ruta = pathname.replace(/\/+$/, '') || '/';
    const prefix = Object.keys(RUTES_JSP)
        .filter((r) => ruta === r || ruta.startsWith(r + '/'))
        .sort((a, b) => b.length - a.length)[0];
    return prefix != null ? RUTES_JSP[prefix] : RUTA_JSP_PER_DEFECTE;
};

/** Pàgina JSP per a les rutes sense equivalent (la mateixa on aterra la JSP en entrar). */
const RUTA_JSP_PER_DEFECTE = 'servei';

/**
 * Enllaç cap a la interfície JSP equivalent a la pantalla actual. La JSP és al mateix servidor que
 * l'API (veure `EmiservAuthProvider`); amb `npm run dev`, al backend, no a Vite.
 */
// eslint-disable-next-line react-refresh/only-export-components
export const useInterficieClassica = () => {
    const { backendBaseUrl } = useSessioUsuari();
    const { pathname } = useLocation();
    const getUrl = React.useCallback(() => {
        const base = import.meta.env.VITE_DEV_BACKEND_URL
            ? import.meta.env.VITE_DEV_BACKEND_URL.replace(/\/+$/, '') + new URL(backendBaseUrl).pathname
            : backendBaseUrl;
        return base.replace(/\/+$/, '') + '/' + rutaJsp(pathname);
    }, [backendBaseUrl, pathname]);
    const anarAInterficieClassica = React.useCallback(() => {
        // Sortida "dura" del SPA: la JSP és una altra aplicació, no una ruta de React Router.
        window.location.href = getUrl();
    }, [getUrl]);
    return { getUrl, anarAInterficieClassica };
};

/**
 * Botó de la capçalera per anar a la interfície clàssica (la pantalla JSP equivalent), com a la
 * resta d'aplicacions (Distribució, Notib, Ripea).
 */
export const InterficieClassicaButton: React.FC = () => {
    const { t } = useTranslation();
    const { anarAInterficieClassica } = useInterficieClassica();
    return (
        <Button
            color="inherit"
            startIcon={<Icon fontSize="small">{icons.interficieClassica}</Icon>}
            onClick={anarAInterficieClassica}
        >
            {t('app.menu.interficieClassica')}
        </Button>
    );
};
