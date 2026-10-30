import { createBrowserRouter, Navigate } from 'react-router-dom';
import App from './App';
import ServeisLlistat from './pages/serveis/ServeisLlistat';
import NotFound from './pages/NotFound';

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
