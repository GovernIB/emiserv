import type translationCa from './translationCa';

const translationEs: typeof translationCa = {
    app: {
        titol: 'EMISERV',
        menu: {
            serveis: 'Servicios',
            auditoria: 'Auditoría',
            backoffice: 'Backoffice',
            enrutador: 'Enrutador',
            properament: '{{titol}} (próximamente)',
            interficieClassica: 'Interfaz clásica',
        },
        sessio: {
            caducada: 'La sesión ha caducado o no se ha podido iniciar.',
            iniciar: 'Iniciar sesión',
            error: 'No se ha podido consultar la sesión del usuario.',
            tornarAProvar: 'Volver a intentarlo',
            sensePermisos: 'No tiene permisos para acceder a EMISERV. Pida al administrador que le asigne un rol de la aplicación.',
            sortir: 'Salir',
        },
    },
    perfil: {
        menu: 'Perfil',
        titol: 'Perfil del usuario',
        codi: 'Código',
        nom: 'Nombre',
        email: 'Correo electrónico',
        rols: 'Roles',
        interficie: {
            label: 'Interfaz preferida',
            defecte: 'Por defecto',
            jsp: 'Clásica',
            react: 'Nueva',
            ajuda: 'Interfaz que se abre al entrar en la aplicación. «Por defecto» sigue la configuración de la aplicación.',
        },
        desar: 'Guardar',
        cancelar: 'Cancelar',
        errorDesar: 'No se ha podido guardar la interfaz preferida.',
    },
    page: {
        serveis: {
            titol: 'Servicios',
        },
        notFound: {
            titol: 'Página no encontrada',
            missatge: 'La página que busca no existe.',
            tornar: 'Volver al inicio',
        },
    },
};

export default translationEs;
