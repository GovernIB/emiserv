import { describe, it, expect } from 'vitest';
import { analitzarXml, formatarText, formatarXml } from './xml';

describe('xml', () => {
    it('analitza els elements, els atributs i el text i descarta els espais entre elements', () => {
        const arrel = analitzarXml(
            '<?xml version="1.0"?>\n<a xmlns:p="urn:x" id="1">\n  <p:b>hola</p:b>\n  <c/>\n</a>'
        );
        expect(arrel).toEqual({
            tipus: 'element',
            nom: 'a',
            atributs: [
                ['xmlns:p', 'urn:x'],
                ['id', '1'],
            ],
            fills: [
                { tipus: 'element', nom: 'p:b', atributs: [], fills: [{ tipus: 'text', text: 'hola' }] },
                { tipus: 'element', nom: 'c', atributs: [], fills: [] },
            ],
        });
    });

    it('no és XML: text pla o mal format', () => {
        expect(analitzarXml('java.net.ConnectException: Connection refused')).toBeNull();
        expect(analitzarXml('<a><b></a>')).toBeNull();
        expect(formatarText('Error <no xml')).toBe('Error <no xml');
    });

    it('formata amb sagnat i escapa el text i els atributs', () => {
        const arrel = analitzarXml('<a t="x&quot;y"><b>1 &lt; 2</b><c><![CDATA[<raw>]]></c><!--nota--></a>');
        expect(formatarXml(arrel!)).toBe(
            [
                '<a t="x&quot;y">',
                '  <b>1 &lt; 2</b>',
                '  <c>',
                '    <![CDATA[<raw>]]>',
                '  </c>',
                '  <!--nota-->',
                '</a>',
            ].join('\n')
        );
    });
});
