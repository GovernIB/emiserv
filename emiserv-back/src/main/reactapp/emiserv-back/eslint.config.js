import js from '@eslint/js';
import globals from 'globals';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
import tseslint from 'typescript-eslint';
import { defineConfig, globalIgnores } from 'eslint/config';

export default defineConfig([
    // lib/ i src/lot2/ són còpies de base-react que no s'editen aquí (veure src/lot2/README.md): es
    // revisen a base-react, no amb les regles d'aquesta aplicació.
    globalIgnores(['dist', 'lib', 'src/lot2']),
    {
        files: ['src/**/*.{ts,tsx}'],
        extends: [
            js.configs.recommended,
            tseslint.configs.recommended,
            reactHooks.configs['recommended-latest'],
            reactRefresh.configs.vite,
        ],
        languageOptions: {
            ecmaVersion: 2020,
            globals: globals.browser,
        },
    },
]);
