import React from 'react';
import Box from '@mui/material/Box';
import Icon from '@mui/material/Icon';
import Typography from '@mui/material/Typography';
import { accentColor } from './theme';
import { ICON_SIZE, SCREEN_TITLE_GAP } from './layout';
import type { ScreenIconName } from './icons';
import { SCREEN_TITLE_CLASS, useScreenFrame } from './ScreenFrameContext';

type ScreenTitleProps = {
    /** Icona del concepte (catàleg `icons`, o el de l'aplicació): la mateixa que l'entrada del menú. */
    icon: ScreenIconName;
    /** Títol de la pantalla. */
    title: React.ReactNode;
};

/**
 * Títol d'una pantalla: icona del concepte, títol i, al costat, l'espai on els components de la
 * pantalla hi poden pintar informació breu (el recompte de filtres, veure FilterCount).
 *
 * Es passa com a `title` de les graelles i formularis de la llibreria (MuiDataGrid, MuiForm), que amb
 * `lot2DefaultMuiComponentProps` pinten la seva barra d'eines com a barra de títol; i el fa servir el
 * mateix ScreenFrame a la seva barra pròpia.
 */
export const ScreenTitle: React.FC<ScreenTitleProps> = (props) => {
    const { icon, title } = props;
    const screenFrame = useScreenFrame();
    return (
        <Box
            className={SCREEN_TITLE_CLASS}
            sx={{ display: 'flex', alignItems: 'center', gap: SCREEN_TITLE_GAP + 'px', minWidth: 0 }}
        >
            <Icon
                aria-hidden
                sx={(theme) => ({ fontSize: ICON_SIZE + 'px', color: accentColor(theme), flex: '0 0 auto' })}
            >
                {icon}
            </Icon>
            <Typography variant="h6" component="h1" noWrap sx={{ fontWeight: 600, minWidth: 0 }}>
                {title}
            </Typography>
            {screenFrame != null && (
                <Box ref={screenFrame.setInfoSlot} sx={{ display: 'flex', alignItems: 'center', flex: '0 0 auto' }} />
            )}
        </Box>
    );
};

export default ScreenTitle;
