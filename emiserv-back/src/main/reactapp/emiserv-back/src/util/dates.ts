import dayjs from 'dayjs';

/** Data i hora (dd/mm/aaaa hh:mm:ss) d'un valor de l'API (ISO o mil·lisegons); buit si no n'hi ha. */
export const formatDataHora = (valor?: string | number | null): string =>
    valor == null || valor === '' ? '' : dayjs(valor).format('DD/MM/YYYY HH:mm:ss');
