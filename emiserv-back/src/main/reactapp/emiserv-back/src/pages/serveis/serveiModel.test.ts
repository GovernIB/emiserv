import { describe, expect, it } from 'vitest';
import { asyncProcessatSincron } from './serveiModel';

describe('serveiModel', () => {
    it('les asíncrones es processen com a síncrones als enrutadors asyncSincron i als backoffices PROCESSAR_SINCRONES', () => {
        expect(asyncProcessatSincron({ tipus: 'ENRUTADOR', asyncSincron: true })).toBe(true);
        expect(asyncProcessatSincron({ tipus: 'ENRUTADOR_MULTIPLE', asyncSincron: true })).toBe(true);
        expect(asyncProcessatSincron({ tipus: 'ENRUTADOR', asyncSincron: false })).toBe(false);
        expect(asyncProcessatSincron({ tipus: 'BACKOFFICE', backofficeCaibAsyncTipus: 'PROCESSAR_SINCRONES' })).toBe(
            true
        );
        expect(asyncProcessatSincron({ tipus: 'BACKOFFICE', backofficeCaibAsyncTipus: 'REENVIAR_BACKOFFICE' })).toBe(
            false
        );
        // asyncSincron només s'aplica als enrutadors
        expect(asyncProcessatSincron({ tipus: 'BACKOFFICE', asyncSincron: true })).toBe(false);
    });
});
