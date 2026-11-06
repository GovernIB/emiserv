import { createBrowserRouter, Navigate } from 'react-router-dom';
import App from './App';
import ServeisLlistat from './pages/serveis/ServeisLlistat';
import NotFound from './pages/NotFound';
import AuditoriaLlistat from './pages/auditoria/AuditoriaLlistat';
import ConfiguracioPagina from './pages/configuracio/ConfiguracioPagina';
import Informes from './pages/informes/Informes';
import NomesAdmin from './components/NomesAdmin';

export const router = createBrowserRouter(
    [
        {
            path: '/',
            element: <App />,
            children: [
                {
                    index: true,
                    element: <Navigate to="/serveis" replace />,
                },
                {
                    // Amb un id (o «nou»), el llistat obre la finestra del servei a la pestanya
                    // ?pestanya=... Una sola ruta perquè el llistat no es torni a muntar en obrir-la.
                    path: 'serveis/:id?',
                    element: <ServeisLlistat />,
                },
                {
                    // Amb un id, el llistat obre la finestra de detall de la petició
                    path: 'auditoria/backoffice/:id?',
                    element: <AuditoriaLlistat key="backoffice" tipus="backoffice" />,
                },
                {
                    path: 'auditoria/enrutador/:id?',
                    element: <AuditoriaLlistat key="enrutador" tipus="enrutador" />,
                },
                {
                    path: 'informes',
                    element: (
                        <NomesAdmin>
                            <Informes />
                        </NomesAdmin>
                    ),
                },
                {
                    // Manteniments de configuració (`/configuracio/<clau>`) i propietats (`/configuracio/propietats`)
                    path: 'configuracio/:clau',
                    element: (
                        <NomesAdmin>
                            <ConfiguracioPagina />
                        </NomesAdmin>
                    ),
                },
                {
                    path: '*',
                    element: <NotFound />,
                },
            ],
        },
    ],
    {
        basename: import.meta.env.BASE_URL,
    }
);
