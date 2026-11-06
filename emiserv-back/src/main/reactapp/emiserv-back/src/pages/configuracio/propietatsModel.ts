/** Propietat de configuració tal com la torna /api/configuracio/propietats. */
export type Propietat = {
    key: string;
    descripcio?: string;
    value?: string | null;
    typeCode?: string;
    origen?: string;
    requereixReinici?: boolean;
    validValues?: string[] | null;
    contrasenya?: boolean;
    nomesLectura?: boolean;
};

export type GrupPropietats = { key: string; descripcio?: string; propietats: Propietat[]; grups: GrupPropietats[] };

const normalitzar = (text: string) => text.toLocaleLowerCase();

/** La propietat coincideix amb el cercador: descripció, clau o valor (mai el d'una contrasenya). */
export const coincideix = (p: Propietat, cerca: string): boolean => {
    const c = normalitzar(cerca.trim());
    if (!c) {
        return true;
    }
    return [p.descripcio, p.key, p.contrasenya ? null : p.value].some((t) => t != null && normalitzar(t).includes(c));
};

/**
 * Grups amb només les propietats que coincideixen amb el cercador; es descarten els grups (i
 * subgrups) que es queden buits. Sense cercador, tots.
 */
export const filtrarGrups = (grups: GrupPropietats[], cerca?: string): GrupPropietats[] => {
    if (!cerca?.trim()) {
        return grups;
    }
    return grups
        .map((g) => ({
            ...g,
            propietats: g.propietats.filter((p) => coincideix(p, cerca)),
            grups: filtrarGrups(g.grups, cerca),
        }))
        .filter((g) => g.propietats.length > 0 || g.grups.length > 0);
};

/** Nombre de propietats d'un grup, amb les dels subgrups. */
export const comptarPropietats = (grup: GrupPropietats): number =>
    grup.propietats.length + grup.grups.reduce((n, g) => n + comptarPropietats(g), 0);

/**
 * Trossos del text per ressaltar les coincidències amb el cercador (sense distingir majúscules):
 * `coincident` indica els trossos que s'han de marcar.
 */
export const trossosRessaltats = (text: string, cerca?: string): { text: string; coincident: boolean }[] => {
    const c = cerca?.trim();
    if (!c || !text) {
        return [{ text, coincident: false }];
    }
    const trossos: { text: string; coincident: boolean }[] = [];
    const baix = normalitzar(text);
    const cercaBaix = normalitzar(c);
    let inici = 0;
    let pos = baix.indexOf(cercaBaix);
    while (pos >= 0) {
        if (pos > inici) {
            trossos.push({ text: text.slice(inici, pos), coincident: false });
        }
        trossos.push({ text: text.slice(pos, pos + c.length), coincident: true });
        inici = pos + c.length;
        pos = baix.indexOf(cercaBaix, inici);
    }
    if (inici < text.length) {
        trossos.push({ text: text.slice(inici), coincident: false });
    }
    return trossos;
};
