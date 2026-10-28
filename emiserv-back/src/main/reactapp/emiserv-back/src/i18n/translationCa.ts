const translationCa = {
    app: {
        titol: 'EMISERV',
        menu: {
            serveis: 'Serveis',
            auditoria: 'Auditoria',
            backoffice: 'Backoffice',
            enrutador: 'Enrutador',
            properament: '{{titol}} (properament)',
            interficieClassica: 'Interfície clàssica',
        },
        sessio: {
            caducada: "La sessió ha caducat o no s'ha pogut iniciar.",
            iniciar: 'Inicia la sessió',
            error: "No s'ha pogut consultar la sessió de l'usuari.",
            tornarAProvar: 'Torna-ho a provar',
            sensePermisos: "No teniu permisos per accedir a EMISERV. Demanau a l'administrador que us assigni un rol de l'aplicació.",
            sortir: 'Surt',
        },
    },
    perfil: {
        menu: 'Perfil',
        titol: "Perfil de l'usuari",
        codi: 'Codi',
        nom: 'Nom',
        email: 'Correu electrònic',
        rols: 'Rols',
        interficie: {
            label: 'Interfície preferida',
            defecte: 'Per defecte',
            jsp: 'Clàssica',
            react: 'Nova',
            ajuda: "Interfície que s'obre en entrar a l'aplicació. «Per defecte» segueix la configuració de l'aplicació.",
        },
        desar: 'Desa',
        cancelar: 'Cancel·la',
        errorDesar: "No s'ha pogut desar la interfície preferida.",
    },
    page: {
        serveis: {
            titol: 'Serveis',
        },
        notFound: {
            titol: 'Pàgina no trobada',
            missatge: 'La pàgina que cercau no existeix.',
            tornar: "Torna a l'inici",
        },
    },
};

export default translationCa;
