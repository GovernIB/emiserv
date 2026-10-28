/**
 * Desa un fitxer descarregat de l'API (p. ex. el resultat d'un informe amb `fileType`). L'aplicació
 * no passa cap `saveAs` a la llibreria, per això es fa aquí amb un enllaç temporal.
 */
export const descarregarFitxer = (blob: Blob, nomFitxer?: string) => {
    const nom = (nomFitxer ?? 'fitxer').trim().replace(/^"(.*)"$/, '$1');
    const url = URL.createObjectURL(blob);
    const enllac = document.createElement('a');
    enllac.href = url;
    enllac.download = nom;
    document.body.appendChild(enllac);
    enllac.click();
    enllac.remove();
    // El navegador ja ha iniciat la descàrrega; s'allibera la URL una mica després.
    setTimeout(() => URL.revokeObjectURL(url), 10000);
};

export default descarregarFitxer;
