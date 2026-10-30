type ErrorApi = { description?: string; message?: string };

/** Missatge d'un error de l'API: el del servidor (409, 422...) si n'hi ha, si no el genèric. */
export const missatgeError = (error: unknown): string => {
    const e = error as ErrorApi | undefined;
    return e?.description ?? e?.message ?? String(error);
};
