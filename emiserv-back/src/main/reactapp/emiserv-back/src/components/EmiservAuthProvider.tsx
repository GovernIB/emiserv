import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import { AuthContext } from 'reactlib';

/**
 * Autenticació de la interfície REACT amb la sessió de servidor (adaptat de
 * `DistribucioAuthProvider` de Distribució).
 *
 * El navegador no gestiona cap token: les crides a l'API viatgen amb la galeta de la mateixa
 * sessió HTTP que la interfície JSP (adaptador Keycloak de JBoss en mode EAR,
 * `KeycloakWebSecurityConfigurerAdapter` en mode Spring Boot).
 *
 * El codi, el nom, el correu, els rols i la interfície de l'usuari surten de `api/sessioUsuari`
 * (`SessioUsuariController`), que serveix també per a comprovar si la sessió continua viva. No es
 * consulta periòdicament, perquè cada consulta allargaria la sessió i un usuari inactiu no
 * arribaria mai a caducar (a la JSP sí que caduca). Es consulta en tornar a la pestanya i amb
 * l'activitat de l'usuari (com a molt un cop per minut).
 *
 * Si no hi ha sessió (401) es va a `sessioUsuari/login?redirect=<URL actual>`: el servidor passa
 * pel login i torna a la mateixa URL. Si l'usuari està autenticat però no té cap rol d'EMISERV
 * (403), es mostra un missatge de "sense permisos" en lloc de tornar al login (que no hi canviaria
 * res i faria un bucle).
 */

export type Interficie = 'JSP' | 'REACT';

export type SessioUsuari = {
    codi: string;
    nom?: string;
    email?: string;
    /** Rols de l'aplicació de l'usuari (EMS_ADMIN, EMS_RESP). */
    rols: string[];
    /** Interfície efectiva: la preferida de l'usuari o, si no en té, la per defecte de l'aplicació. */
    interficie: Interficie;
    /** Interfície preferida de l'usuari (null: la per defecte de l'aplicació). */
    interficiePreferida: Interficie | null;
};

type EstatSessio = 'carregant' | 'autenticat' | 'caducada' | 'sensePermisos' | 'error';

type SessioUsuariContextType = {
    usuari?: SessioUsuari;
    /** Arrel de l'aplicació al servidor (context path), p. ex. `https://host/emiservback`. */
    backendBaseUrl: string;
    /** Comprova si la sessió continua viva i, si ha caducat, torna a iniciar-la. */
    comprovarSessio: () => void;
    /** Desa la interfície preferida de l'usuari (`PUT api/sessioUsuari/interficie`). */
    desarInterficiePreferida: (interficie: Interficie | null) => Promise<void>;
};

const SessioUsuariContext = React.createContext<SessioUsuariContextType | undefined>(undefined);

// eslint-disable-next-line react-refresh/only-export-components
export const useSessioUsuari = (): SessioUsuariContextType => {
    const context = React.useContext(SessioUsuariContext);
    if (context === undefined) {
        throw new Error('useSessioUsuari must be used within a EmiservAuthProvider');
    }
    return context;
};

/** Temps mínim entre dues comprovacions provocades per l'activitat de l'usuari. */
const INTERVAL_ACTIVITAT_MS = 60000;
/** Temps mínim entre dues comprovacions provocades per la pestanya. */
const INTERVAL_MINIM_MS = 10000;
/**
 * Marca (sessionStorage) del darrer intent d'iniciar la sessió. Si en tornar la sessió encara no
 * hi és, no es torna a intentar (evita un bucle de redireccions, p. ex. si la galeta de sessió no
 * arriba al backend) i es mostra la pantalla de sessió caducada.
 */
const INICI_SESSIO_KEY = 'emiserv.sessioCaducada.intent';
const INICI_SESSIO_MARGE_MS = 60000;

const ESDEVENIMENTS_ACTIVITAT = ['pointerdown', 'keydown'] as const;

/** Arrel de l'aplicació al servidor (context path), a partir de la URL de l'API. */
const getBackendBaseUrl = (apiUrl: string) =>
    new URL(apiUrl, window.location.href).href.replace(/\/+$/, '').replace(/\/api$/, '');

/**
 * URL de `sessioUsuari/login`. Si la pàgina la serveix el backend, és al mateix origen i el retorn
 * és un camí relatiu (el backend no accepta URL absolutes d'un altre origen). Amb `npm run dev` la
 * pàgina és a Vite (:5173), el login es fa directament al backend (`VITE_DEV_BACKEND_URL`) i el
 * retorn és la URL de Vite (origen permès a `SessioUsuariController`).
 */
const getLoginUrl = (backendBaseUrl: string) => {
    const devBackendUrl = import.meta.env.VITE_DEV_BACKEND_URL;
    if (devBackendUrl) {
        const contextPath = new URL(backendBaseUrl).pathname.replace(/\/+$/, '');
        return (
            devBackendUrl.replace(/\/+$/, '') +
            contextPath +
            '/sessioUsuari/login?redirect=' +
            encodeURIComponent(window.location.href)
        );
    }
    const { pathname, search, hash } = window.location;
    return backendBaseUrl + '/sessioUsuari/login?redirect=' + encodeURIComponent(pathname + search + hash);
};

/** URL de sortida, la mateixa que la interfície JSP (decorators/default.jsp). */
const getLogoutUrl = (backendBaseUrl: string) => {
    const devBackendUrl = import.meta.env.VITE_DEV_BACKEND_URL;
    if (devBackendUrl) {
        return devBackendUrl.replace(/\/+$/, '') + new URL(backendBaseUrl).pathname.replace(/\/+$/, '') + '/logout';
    }
    return backendBaseUrl + '/logout';
};

type ResultatSessio = { estat: 'autenticat'; usuari: SessioUsuari } | { estat: 'senseSessio' | 'sensePermisos' };

/**
 * Consulta l'usuari de la sessió. Sense sessió el servidor respon amb un 401 o amb una redirecció
 * cap al login (que amb `redirect: 'manual'` arriba com a `opaqueredirect`); amb un 403 l'usuari
 * està autenticat però no té cap rol d'EMISERV. Qualsevol altre error (servidor aturat, xarxa) es
 * propaga: no vol dir que la sessió hagi caducat.
 */
const consultarSessio = async (backendBaseUrl: string): Promise<ResultatSessio> => {
    const response = await fetch(backendBaseUrl + '/api/sessioUsuari', {
        credentials: 'include',
        redirect: 'manual',
        cache: 'no-store',
        headers: { Accept: 'application/json' },
    });
    if (response.type === 'opaqueredirect' || response.status === 401) {
        return { estat: 'senseSessio' };
    }
    if (response.status === 403) {
        return { estat: 'sensePermisos' };
    }
    if (!response.ok) {
        throw new Error("Error consultant la sessió de l'usuari (HTTP " + response.status + ')');
    }
    return { estat: 'autenticat', usuari: await response.json() };
};

const llegirMarcaIniciSessio = (): number => {
    try {
        return Number(sessionStorage.getItem(INICI_SESSIO_KEY)) || 0;
    } catch {
        return 0;
    }
};

const escriureMarcaIniciSessio = (valor?: number) => {
    try {
        if (valor == null) {
            sessionStorage.removeItem(INICI_SESSIO_KEY);
        } else {
            sessionStorage.setItem(INICI_SESSIO_KEY, String(valor));
        }
    } catch {
        // Sense sessionStorage no hi ha protecció contra el bucle, però l'inici de sessió funciona.
    }
};

const PantallaSessio: React.FC<{ missatge: string; accio: string; onAccio: () => void }> = ({
    missatge,
    accio,
    onAccio,
}) => (
    <Box
        sx={{
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            height: '100vh',
            gap: 2,
            px: 2,
            textAlign: 'center',
        }}
    >
        <Typography>{missatge}</Typography>
        <Button variant="contained" onClick={onAccio}>
            {accio}
        </Button>
    </Box>
);

type EmiservAuthProviderProps = React.PropsWithChildren & {
    /** URL de l'API REST, d'on es dedueix l'arrel de l'aplicació al servidor. */
    apiUrl: string;
};

export const EmiservAuthProvider: React.FC<EmiservAuthProviderProps> = ({ apiUrl, children }) => {
    const { t } = useTranslation();
    const backendBaseUrl = React.useMemo(() => getBackendBaseUrl(apiUrl), [apiUrl]);
    const [estat, setEstat] = React.useState<EstatSessio>('carregant');
    const [usuari, setUsuari] = React.useState<SessioUsuari>();
    const usuariRef = React.useRef<SessioUsuari>(undefined);
    const comprovantRef = React.useRef(false);
    const darreraComprovacioRef = React.useRef(0);

    const iniciarSessio = React.useCallback(() => {
        escriureMarcaIniciSessio(Date.now());
        window.location.href = getLoginUrl(backendBaseUrl);
    }, [backendBaseUrl]);

    const sortir = React.useCallback(() => {
        // NO facis un fetch del logout: ha d'acabar redirigint a Keycloak, i cal una navegació
        // real perquè el navegador hi enviï les seves galetes i es tanqui la sessió SSO.
        window.location.href = getLogoutUrl(backendBaseUrl);
    }, [backendBaseUrl]);

    const gestionarSenseSessio = React.useCallback(() => {
        const intentRecent = Date.now() - llegirMarcaIniciSessio() < INICI_SESSIO_MARGE_MS;
        if (intentRecent) {
            setEstat('caducada');
        } else {
            iniciarSessio();
        }
    }, [iniciarSessio]);

    const comprovar = React.useCallback(
        (intervalMinim: number) => {
            if (comprovantRef.current || Date.now() - darreraComprovacioRef.current < intervalMinim) {
                return;
            }
            comprovantRef.current = true;
            darreraComprovacioRef.current = Date.now();
            consultarSessio(backendBaseUrl)
                .then((resultat) => {
                    if (resultat.estat !== 'autenticat') {
                        if (resultat.estat === 'senseSessio') {
                            gestionarSenseSessio();
                        } else {
                            escriureMarcaIniciSessio(undefined);
                            usuariRef.current = undefined;
                            setUsuari(undefined);
                            setEstat('sensePermisos');
                        }
                        return;
                    }
                    escriureMarcaIniciSessio(undefined);
                    const sessio = resultat.usuari;
                    const usuariActual = usuariRef.current;
                    if (usuariActual == null) {
                        usuariRef.current = sessio;
                        setUsuari(sessio);
                        setEstat('autenticat');
                    } else if (usuariActual.codi !== sessio.codi) {
                        // S'ha iniciat sessió amb un altre usuari (en una altra pestanya): tot el
                        // que hi ha carregat és de l'usuari anterior.
                        window.location.reload();
                    }
                })
                .catch((error) => {
                    console.error(error);
                    // Si ja s'havia carregat l'usuari, un error puntual no atura l'aplicació: les
                    // crides a l'API ja mostraran el seu propi error.
                    if (usuariRef.current == null) {
                        setEstat('error');
                    }
                })
                .finally(() => {
                    comprovantRef.current = false;
                });
        },
        [backendBaseUrl, gestionarSenseSessio]
    );

    const comprovarSessio = React.useCallback(() => comprovar(INTERVAL_MINIM_MS), [comprovar]);

    const tornarAProvar = React.useCallback(() => {
        setEstat('carregant');
        comprovar(0);
    }, [comprovar]);

    const desarInterficiePreferida = React.useCallback(
        async (interficie: Interficie | null) => {
            const response = await fetch(backendBaseUrl + '/api/sessioUsuari/interficie', {
                method: 'PUT',
                credentials: 'include',
                redirect: 'manual',
                cache: 'no-store',
                headers: { Accept: 'application/json', 'Content-Type': 'application/json' },
                body: JSON.stringify({ interficie }),
            });
            if (response.type === 'opaqueredirect' || response.status === 401) {
                gestionarSenseSessio();
                throw new Error('Sessió caducada');
            }
            if (!response.ok) {
                throw new Error('Error desant la interfície preferida (HTTP ' + response.status + ')');
            }
            // La interfície efectiva depèn de la configuració de l'aplicació quan la preferida és
            // nul·la: es torna a llegir del servidor.
            const resultat = await consultarSessio(backendBaseUrl);
            if (resultat.estat === 'autenticat') {
                usuariRef.current = resultat.usuari;
                setUsuari(resultat.usuari);
            }
        },
        [backendBaseUrl, gestionarSenseSessio]
    );

    React.useEffect(() => {
        comprovar(0);
    }, [comprovar]);

    React.useEffect(() => {
        if (estat !== 'autenticat') {
            return;
        }
        const onVisibilitat = () => {
            if (document.visibilityState === 'visible') {
                comprovar(INTERVAL_MINIM_MS);
            }
        };
        const onActivitat = () => comprovar(INTERVAL_ACTIVITAT_MS);
        document.addEventListener('visibilitychange', onVisibilitat);
        ESDEVENIMENTS_ACTIVITAT.forEach((e) => window.addEventListener(e, onActivitat, { passive: true }));
        return () => {
            document.removeEventListener('visibilitychange', onVisibilitat);
            ESDEVENIMENTS_ACTIVITAT.forEach((e) => window.removeEventListener(e, onActivitat));
        };
    }, [estat, comprovar]);

    const authContext = React.useMemo(() => {
        const autenticat = estat === 'autenticat' && usuari != null;
        return {
            isLoading: !autenticat,
            isReady: autenticat,
            isAuthenticated: autenticat,
            // Sense token: les crides a l'API s'autentiquen amb la galeta de sessió.
            bearerTokenActive: false,
            getToken: () => undefined,
            // Imita els camps del token que llegeix base-react (p. ex. el botó d'usuari).
            getTokenParsed: () =>
                usuari != null
                    ? { preferred_username: usuari.codi, name: usuari.nom ?? usuari.codi, email: usuari.email }
                    : undefined,
            getUserId: () => usuari?.codi,
            getUserName: () => usuari?.nom ?? usuari?.codi,
            getUserEmail: () => usuari?.email,
            signIn: undefined,
            signOut: sortir,
        };
    }, [estat, usuari, sortir]);

    const sessioContext = React.useMemo(
        () => ({ usuari, backendBaseUrl, comprovarSessio, desarInterficiePreferida }),
        [usuari, backendBaseUrl, comprovarSessio, desarInterficiePreferida]
    );

    let contingut: React.ReactNode;
    if (estat === 'autenticat') {
        contingut = children;
    } else if (estat === 'caducada') {
        contingut = (
            <PantallaSessio missatge={t('app.sessio.caducada')} accio={t('app.sessio.iniciar')} onAccio={iniciarSessio} />
        );
    } else if (estat === 'sensePermisos') {
        contingut = (
            <PantallaSessio missatge={t('app.sessio.sensePermisos')} accio={t('app.sessio.sortir')} onAccio={sortir} />
        );
    } else if (estat === 'error') {
        contingut = (
            <PantallaSessio
                missatge={t('app.sessio.error')}
                accio={t('app.sessio.tornarAProvar')}
                onAccio={tornarAProvar}
            />
        );
    } else {
        // Mentre es consulta la sessió no es pinta res (com feien els proveïdors de base-react).
        contingut = null;
    }
    return (
        <AuthContext.Provider value={authContext}>
            <SessioUsuariContext.Provider value={sessioContext}>{contingut}</SessioUsuariContext.Provider>
        </AuthContext.Provider>
    );
};

export default EmiservAuthProvider;
