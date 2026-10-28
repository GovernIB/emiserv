/// <reference types="vite/client" />

interface ImportMetaEnv {
    readonly VITE_API_URL: string;
    readonly VITE_API_PUBLIC_URL: string;
    readonly VITE_API_BASE_URL: string;
    readonly VITE_API_SUFFIX: string;
    readonly VITE_APP_VERSION: string;
    /** Només amb `npm run dev`: URL del backend (veure vite.config.ts). */
    readonly VITE_DEV_BACKEND_URL?: string;
}

interface ImportMeta {
    readonly env: ImportMetaEnv;
}
