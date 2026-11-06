import React from 'react';
import { useParams } from 'react-router-dom';
import NotFound from '../NotFound';
import ConfiguracioLlistat from './ConfiguracioLlistat';
import { mantenimentPerClau } from './manteniments';
import PropietatsConfiguracio from './PropietatsConfiguracio';

/** Ruta `/configuracio/:clau`: un manteniment de configuració o les propietats (`propietats`). */
export const ConfiguracioPagina: React.FC = () => {
    const { clau } = useParams();
    if (clau === 'propietats') {
        return <PropietatsConfiguracio />;
    }
    const manteniment = mantenimentPerClau(clau);
    return manteniment ? <ConfiguracioLlistat key={manteniment.clau} manteniment={manteniment} /> : <NotFound />;
};

export default ConfiguracioPagina;
