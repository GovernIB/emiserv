import React from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import { MAIN_PADDING } from './layout';
import type { ScreenIconName } from './icons';
import { ScreenFrameContext } from './ScreenFrameContext';
import { titleBarSx } from './titleBarControlsSx';
import ScreenTitle from './ScreenTitle';

export type ScreenFrameProps = {
    /**
     * Icona i títol de la barra de títol pròpia del marc, per a les pantalles on la llibreria no en
     * pinta cap. A les graelles i formularis de la llibreria el títol va a la seva barra d'eines
     * (`title={<ScreenTitle icon={...} title={...} />}`) i aquestes propietats no s'hi posen.
     */
    icon?: ScreenIconName;
    title?: string;
    /** Accions pròpies de la pantalla, a la dreta de la barra de títol pròpia del marc. */
    actions?: React.ReactNode;
    /** Padding del contingut (per al contingut que no és de la llibreria). */
    padded?: boolean;
    /**
     * El marc només ocupa l'alçada del seu contingut (i no tota la disponible), de manera que
     * la resta de la pantalla deixa veure el fons. Per a pantalles curtes sense graella.
     */
    fitContent?: boolean;
    children: React.ReactNode;
};

/**
 * Marc d'una pantalla del backoffice: targeta amb la barra de títol (icona + títol + informació +
 * accions) a dalt i el contingut a sota.
 *
 * Ocupa tota l'alçada disponible dins `<main>`, de manera que el títol hi queda sempre visible i és el
 * contingut qui fa scroll si no hi cap (les graelles fan scroll de les files per dins).
 *
 * La barra de títol pot ser:
 *  - la barra d'eines de la llibreria (graelles i formularis), si el seu `title` és un ScreenTitle.
 *    L'aspecte de barra de títol, l'enganxament i els marges del contingut els donen les propietats
 *    per defecte dels components (`lot2DefaultMuiComponentProps`), no aquest marc;
 *  - la barra pròpia del marc, si es passen `icon` i `title` (pantalles sense barra de la llibreria).
 */
export const ScreenFrame: React.FC<ScreenFrameProps> = (props) => {
    const { icon, title, actions, padded, fitContent, children } = props;
    const [infoSlot, setInfoSlot] = React.useState<HTMLElement | null>(null);
    const context = React.useMemo(() => ({ infoSlot, setInfoSlot }), [infoSlot]);
    return (
        <ScreenFrameContext.Provider value={context}>
            <Card
                className="screen-frame"
                sx={{
                    height: fitContent ? 'auto' : '100%',
                    flex: fitContent ? '0 1 auto' : undefined,
                    minHeight: 0,
                    display: 'flex',
                    flexDirection: 'column',
                    overflow: 'hidden',
                }}
            >
                {icon != null && title != null && (
                    <Box sx={(theme) => ({ ...titleBarSx(theme), display: 'flex', alignItems: 'center', flex: '0 0 auto' })}>
                        <ScreenTitle icon={icon} title={title} />
                        {actions != null && (
                            <Box sx={{ ml: 'auto', display: 'flex', alignItems: 'center', gap: 1, flex: '0 0 auto' }}>
                                {actions}
                            </Box>
                        )}
                    </Box>
                )}
                <Box
                    sx={{
                        flex: '1 1 auto',
                        minHeight: 0,
                        display: 'flex',
                        flexDirection: 'column',
                        overflowY: 'auto',
                        ...(padded ? { p: MAIN_PADDING + 'px', gap: MAIN_PADDING + 'px' } : null),
                    }}
                >
                    {children}
                </Box>
            </Card>
        </ScreenFrameContext.Provider>
    );
};

export default ScreenFrame;
