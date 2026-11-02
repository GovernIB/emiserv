import React from 'react';
import CssBaseline from '@mui/material/CssBaseline';
import useMediaQuery from '@mui/material/useMediaQuery';
import { ThemeProvider } from '@mui/material/styles';
import { darkTheme, draculaTheme, lightTheme } from '../theme';

/** Tema de la interfície triat per l'usuari (TemaAplicacioEnumDto), com a les altres aplicacions. */
export const TEMES = ['CLAR', 'OBSCUR', 'DRACULA', 'SISTEMA'] as const;
export type Tema = (typeof TEMES)[number];

/**
 * Memòria cau del darrer tema desat al perfil. La preferència arriba amb la sessió de l'usuari,
 * però la pantalla de càrrega i l'inici de sessió es pinten abans: sense la memòria cau, un usuari
 * amb el tema obscur veuria un instant el clar a cada recàrrega (com a Distribució).
 */
const CLAU_TEMA_CACHE = 'EMISERV_TEMA';

const esTema = (valor: unknown): valor is Tema => (TEMES as readonly unknown[]).includes(valor);

const llegirTemaCache = (): Tema | undefined => {
    try {
        const desat = localStorage.getItem(CLAU_TEMA_CACHE);
        return esTema(desat) ? desat : undefined;
    } catch {
        return undefined;
    }
};

/** Recorda el tema desat al perfil per a la propera arrencada (mai una previsualització). */
// eslint-disable-next-line react-refresh/only-export-components
export const desarTemaCache = (tema?: Tema | null) => {
    try {
        if (tema != null) {
            localStorage.setItem(CLAU_TEMA_CACHE, tema);
        } else {
            localStorage.removeItem(CLAU_TEMA_CACHE);
        }
    } catch {
        // Navegació privada o emmagatzematge ple: només es perd l'estalvi del parpelleig
    }
};

/** Tema MUI d'un tema de l'usuari; sense tema o SISTEMA, el del sistema operatiu. */
// eslint-disable-next-line react-refresh/only-export-components
export const temaMui = (tema: Tema | null | undefined, prefersDarkMode: boolean) => {
    switch (tema) {
        case 'CLAR':
            return lightTheme;
        case 'OBSCUR':
            return darkTheme;
        case 'DRACULA':
            return draculaTheme;
        default:
            return prefersDarkMode ? darkTheme : lightTheme;
    }
};

const TemaContext = React.createContext<((tema?: Tema | null) => void) | undefined>(undefined);

/**
 * Tema visual de tota l'aplicació. Arrenca amb el tema de la memòria cau; el proveïdor de la sessió
 * hi aplica el del perfil en carregar l'usuari, i el diàleg de perfil el previsualitza mentre s'edita.
 */
export const TemaProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
    const [tema, setTema] = React.useState<Tema | null | undefined>(llegirTemaCache);
    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');
    return (
        <TemaContext.Provider value={setTema}>
            <ThemeProvider theme={temaMui(tema, prefersDarkMode)}>
                {/* enableColorScheme: els controls natius (barres de desplaçament...) segueixen el tema */}
                <CssBaseline enableColorScheme />
                {children}
            </ThemeProvider>
        </TemaContext.Provider>
    );
};

/** Fixa el tema actiu (sense desar-lo). */
// eslint-disable-next-line react-refresh/only-export-components
export const useSetTema = () => {
    const context = React.useContext(TemaContext);
    if (context === undefined) {
        throw new Error('useSetTema must be used within a TemaProvider');
    }
    return context;
};

export default TemaProvider;
