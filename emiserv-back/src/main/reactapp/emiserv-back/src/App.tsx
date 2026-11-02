import { Outlet } from 'react-router-dom';
import { envVar, ResourceApiProvider } from 'reactlib';
import { EmiservAuthProvider } from './components/EmiservAuthProvider';
import { EmiservBaseApp } from './components/EmiservBaseApp';
import { TemaProvider } from './components/TemaProvider';

const envVars = {
    VITE_API_URL: import.meta.env.VITE_API_URL,
    VITE_API_PUBLIC_URL: import.meta.env.VITE_API_PUBLIC_URL,
    VITE_API_BASE_URL: import.meta.env.VITE_API_BASE_URL,
    VITE_API_SUFFIX: import.meta.env.VITE_API_SUFFIX,
};

const getEnvApiUrl = (): string => {
    const envApiPublicUrl = envVar('VITE_API_PUBLIC_URL', envVars);
    const envApiUrl = envVar('VITE_API_URL', envVars);
    if (envApiPublicUrl || envApiUrl) {
        return envApiPublicUrl ?? envApiUrl;
    }
    const envApiBaseUrl = envVar('VITE_API_BASE_URL', envVars);
    const envApiSuffix = envVar('VITE_API_SUFFIX', envVars) ?? '/api';
    if (envApiBaseUrl) {
        return envApiBaseUrl + envApiSuffix;
    }
    // Sense cap URL configurada, l'API és al mateix servidor que serveix la pàgina i sota el mateix
    // context path: el pare del base path del SPA ("/emiservback/reactapp" -> "/emiservback").
    const contextPath = import.meta.env.BASE_URL.replace(/\/+$/, '').replace(/\/[^/]*$/, '');
    return window.location.origin + contextPath + envApiSuffix;
};

export const App = () => {
    const apiUrl = getEnvApiUrl();
    return (
        <TemaProvider>
            {/* Autenticació amb la sessió de servidor, sense token al navegador. */}
            <EmiservAuthProvider apiUrl={apiUrl}>
                <ResourceApiProvider apiUrl={apiUrl}>
                    <EmiservBaseApp>
                        <Outlet />
                    </EmiservBaseApp>
                </ResourceApiProvider>
            </EmiservAuthProvider>
        </TemaProvider>
    );
};

export default App;
