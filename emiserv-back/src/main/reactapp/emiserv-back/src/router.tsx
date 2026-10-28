import { createBrowserRouter, Navigate } from 'react-router-dom';
import App from './App';
import ServeisLlistat from './pages/serveis/ServeisLlistat';
import ServeiFitxa from './pages/serveis/ServeiFitxa';
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
                    path: 'serveis',
                    element: <ServeisLlistat />,
                },
                {
                    // Fitxa provisional de només lectura fins a la fase 2.
                    path: 'serveis/:id',
                    element: <ServeiFitxa />,
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
