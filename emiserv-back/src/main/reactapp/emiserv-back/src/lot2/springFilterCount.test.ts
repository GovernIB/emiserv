import { describe, it, expect } from 'vitest';
import { countSpringFilterTerms } from './springFilterCount';

describe('countSpringFilterTerms', () => {
    it('sense filtre no compta res', () => {
        expect(countSpringFilterTerms(undefined)).toBe(0);
        expect(countSpringFilterTerms('')).toBe(0);
        expect(countSpringFilterTerms('   ')).toBe(0);
    });

    it('un sol terme', () => {
        expect(countSpringFilterTerms('a:1')).toBe(1);
    });

    it('compta els AND/OR de primer nivell', () => {
        expect(countSpringFilterTerms("a:1 and b~'x'")).toBe(2);
        expect(countSpringFilterTerms("a:1 AND b~'x' OR c:2")).toBe(3);
    });

    it('treu els parèntesis que envolten tota l\'expressió', () => {
        expect(countSpringFilterTerms('(a:1 and b:2)')).toBe(2);
    });

    it('ignora els parèntesis interiors', () => {
        expect(countSpringFilterTerms('(a:1 or a:2) and b:3')).toBe(2);
    });

    it('ignora el text dels literals entre cometes', () => {
        expect(countSpringFilterTerms("a~'x and y'")).toBe(1);
    });

    it('ignora les cometes escapades dins dels literals', () => {
        expect(countSpringFilterTerms("a~'o\\'b' and c:1")).toBe(2);
        expect(countSpringFilterTerms("a~'x\\' and y'")).toBe(1);
    });

    it('literal que acaba amb una barra invertida escapada', () => {
        expect(countSpringFilterTerms("a~'x\\\\' and b:1")).toBe(2);
    });

    it('cometa sense tancar i barra final solitària', () => {
        expect(countSpringFilterTerms("a~'x and b:1")).toBe(1);
        expect(countSpringFilterTerms("a:1 and b~'x\\")).toBe(2);
    });

    it('un parèntesi dins d\'un literal no trenca els parèntesis exteriors', () => {
        expect(countSpringFilterTerms("(a~')' and b:1)")).toBe(2);
    });
});
