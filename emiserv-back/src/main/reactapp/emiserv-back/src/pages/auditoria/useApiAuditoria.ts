import React from 'react';
import { useResourceApiContext } from 'reactlib';
import { RECURS_AUDITORIA, type TipusAuditoria } from './auditoriaModel';

/**
 * Crides als camins propis dels recursos d'auditoria (sol·licituds, missatges, respostes i
 * procediments), que no són HAL-FORMS: `fetch` amb la galeta de sessió, com la resta de l'API. Un 204
 * vol dir que no hi ha missatge (null). Els errors es rebutgen amb el missatge del servidor.
 */
export const useApiAuditoria = (tipus: TipusAuditoria) => {
    const { apiUrl } = useResourceApiContext();
    return React.useMemo(() => {
        const url = (cami: string) => `${apiUrl.replace(/\/+$/, '')}/${RECURS_AUDITORIA[tipus]}/${cami}`;
        const consultar = async (cami: string, accept: string) => {
            const resposta = await fetch(url(cami), { credentials: 'include', headers: { Accept: accept } });
            if (!resposta.ok) {
                let missatge: string | undefined;
                try {
                    const cos = await resposta.json();
                    missatge = cos?.message ?? cos?.description;
                } catch {
                    // Sense cos JSON
                }
                throw { message: missatge ?? `HTTP ${resposta.status}` };
            }
            return resposta;
        };
        return {
            text: async (cami: string): Promise<string | null> => {
                const resposta = await consultar(cami, 'text/plain, */*');
                return resposta.status === 204 ? null : resposta.text();
            },
            json: async <T>(cami: string): Promise<T> => {
                const resposta = await consultar(cami, 'application/json');
                return (resposta.status === 204 ? [] : await resposta.json()) as T;
            },
        };
    }, [apiUrl, tipus]);
};
