/**
 * Arbre d'un document XML per al visor (`XmlVisor`): s'analitza amb el DOMParser del navegador i es
 * redueix a nodes simples (elements, text, comentaris i CDATA). Els espais en blanc entre elements es
 * descarten; el text de cada element es conserva tal qual.
 */
export type NodeXml =
    | { tipus: 'element'; nom: string; atributs: [string, string][]; fills: NodeXml[] }
    | { tipus: 'text'; text: string }
    | { tipus: 'cdata'; text: string }
    | { tipus: 'comentari'; text: string };

/** Resultat de l'anàlisi: l'arrel, o null si el text no és XML ben format (p. ex. un error en text). */
export const analitzarXml = (text: string): NodeXml | null => {
    const textNet = text.replace(/^\uFEFF/, '').trim();
    if (!textNet.startsWith('<')) {
        return null;
    }
    const document = new DOMParser().parseFromString(textNet, 'application/xml');
    if (document.getElementsByTagName('parsererror').length > 0 || document.documentElement == null) {
        return null;
    }
    return toNode(document.documentElement);
};

const toNode = (node: Node): NodeXml | null => {
    switch (node.nodeType) {
        case Node.ELEMENT_NODE: {
            const element = node as Element;
            return {
                tipus: 'element',
                nom: element.tagName,
                atributs: Array.from(element.attributes).map((a) => [a.name, a.value]),
                fills: Array.from(element.childNodes)
                    .map(toNode)
                    .filter((n): n is NodeXml => n != null),
            };
        }
        case Node.TEXT_NODE:
            return node.nodeValue?.trim() ? { tipus: 'text', text: node.nodeValue.trim() } : null;
        case Node.CDATA_SECTION_NODE:
            return { tipus: 'cdata', text: node.nodeValue ?? '' };
        case Node.COMMENT_NODE:
            return { tipus: 'comentari', text: node.nodeValue ?? '' };
        default:
            return null;
    }
};

const escapar = (text: string, atribut = false) => {
    const t = text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return atribut ? t.replace(/"/g, '&quot;') : t;
};

/** Text indentat (dos espais) de l'arbre, per copiar o descarregar el missatge formatat. */
export const formatarXml = (node: NodeXml, nivell = 0): string => {
    const sagnat = '  '.repeat(nivell);
    if (node.tipus === 'text') {
        return sagnat + escapar(node.text);
    }
    if (node.tipus === 'cdata') {
        return sagnat + '<![CDATA[' + node.text + ']]>';
    }
    if (node.tipus === 'comentari') {
        return sagnat + '<!--' + node.text + '-->';
    }
    const atributs = node.atributs.map(([n, v]) => ` ${n}="${escapar(v, true)}"`).join('');
    if (node.fills.length === 0) {
        return `${sagnat}<${node.nom}${atributs}/>`;
    }
    if (node.fills.length === 1 && node.fills[0].tipus === 'text') {
        return `${sagnat}<${node.nom}${atributs}>${escapar(node.fills[0].text)}</${node.nom}>`;
    }
    return [
        `${sagnat}<${node.nom}${atributs}>`,
        ...node.fills.map((f) => formatarXml(f, nivell + 1)),
        `${sagnat}</${node.nom}>`,
    ].join('\n');
};

/** El text formatat si és XML; si no, el text original. */
export const formatarText = (text: string): string => {
    const arrel = analitzarXml(text);
    return arrel != null ? formatarXml(arrel) : text;
};
