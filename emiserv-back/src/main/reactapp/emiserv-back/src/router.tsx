import { createBrowserRouter, Navigate } from 'react-router-dom';
import App from './App';
import ServeiList from './pages/servei/ServeiList';
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
                    element: <ServeiList />,
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
