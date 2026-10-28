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
    serveis: {
        titol: 'Servicios',
        columna: {
            codi: 'Código',
            nom: 'Nombre',
            tipus: 'Tipo',
            actiu: 'Activo',
            permisos: 'Permisos',
            rutes: 'Rutas',
        },
        tipus: {
            BACKOFFICE: 'Backoffice',
            ENRUTADOR: 'Enrutador',
            ENRUTADOR_MULTIPLE: 'Enrutador múltiple',
        },
        estat: {
            actiu: 'Activo',
            inactiu: 'Inactivo',
        },
        permisos: {
            titol_one: '{{count}} permiso',
            titol_other: '{{count}} permisos',
        },
        rutes: {
            titol_one: '{{count}} ruta',
            titol_other: '{{count}} rutas',
        },
        diagnostic: {
            titol: 'El servicio no está bien configurado:',
            accio: 'Haga clic para ir a la ficha del servicio',
            motiu: {
                FALTA_CONFIG_SCSP: 'Falta la configuración SCSP',
                FALTA_EMISOR: 'Falta el emisor en la configuración SCSP',
                ENRUTADOR_SENSE_RUTES: 'Enrutador sin rutas',
                RUTA_BACKOFFICE_SENSE_CREDENCIALS: 'Ruta BACKOFFICE sin credenciales',
            },
        },
        filtre: {
            codi: 'Código',
            nom: 'Nombre',
            tipus: 'Tipo',
            actiu: 'Estado',
            mostra: 'Mostrar el filtro',
            amaga: 'Ocultar el filtro',
        },
        accio: {
            activar: 'Activar',
            desactivar: 'Desactivar',
            esborrar: 'Eliminar',
            activarOk: 'El servicio se ha activado correctamente',
            desactivarOk: 'El servicio se ha desactivado correctamente',
            esborrarOk: 'El servicio se ha eliminado correctamente',
            confirmacioEsborrarTitol: 'Eliminar el servicio',
            confirmacioEsborrar: '¿Está seguro de querer eliminar este servicio?',
            error: 'No se ha podido ejecutar la acción',
        },
        exportar: {
            boto: 'Exportar',
            csv: 'CSV',
            xlsx: 'Excel (XLSX)',
            error: 'No se ha podido exportar',
        },
        fitxa: {
            titol: 'Servicio {{codi}}',
            avis: 'La ficha completa del servicio (con sus pestañas) llega en la próxima fase de la interfaz nueva. Mientras tanto, puede gestionarla desde la interfaz clásica.',
            pestanya: 'Pestaña solicitada: {{pestanya}}',
            pestanyes: {
                configScsp: 'Configuración SCSP',
                rutes: 'Rutas',
            },
            tornar: 'Volver al listado',
            errorCarrega: 'No se ha podido cargar el servicio.',
            configuratScsp: 'Configurado SCSP',
            si: 'Sí',
            no: 'No',
        },
    },
    page: {
        notFound: {
            titol: 'Página no encontrada',
            missatge: 'La página que busca no existe.',
            tornar: 'Volver al inicio',
        },
    },
};

export default translationEs;
