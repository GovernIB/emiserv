import { describe, it, expect } from 'vitest';
import { darkTheme, draculaTheme, lightTheme } from '../theme';
import { temaMui } from './TemaProvider';

describe('TemaProvider', () => {
    it('tria el tema MUI del perfil; sense tema o SISTEMA, el del sistema operatiu', () => {
        expect(temaMui('CLAR', true)).toBe(lightTheme);
        expect(temaMui('OBSCUR', false)).toBe(darkTheme);
        expect(temaMui('DRACULA', false)).toBe(draculaTheme);
        expect(temaMui('SISTEMA', true)).toBe(darkTheme);
        expect(temaMui(null, false)).toBe(lightTheme);
    });
});
