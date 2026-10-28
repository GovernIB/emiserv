import React from 'react';
import MuiToolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import { red } from '@mui/material/colors';
import { useTheme } from '@mui/material/styles';
import {
    ReactElementWithPosition,
    joinReactElementsWithReactElementsWithPositions,
} from '../../util/reactNodePosition';

export type ToolbarProps = React.PropsWithChildren & {
    title?: React.ReactNode;
    /** Element HTML del títol de text (per defecte 'h6') */
    titleComponent?: React.ElementType;
    subtitle?: string;
    lovMode?: boolean;
    upperToolbar?: boolean;
    error?: boolean;
    noFlexGrow?: true;
    sx?: any;
    elementsWithPositions?: ReactElementWithPosition[];
};

export const Toolbar: React.FC<ToolbarProps> = (props) => {
    const {
        title,
        titleComponent = 'h6',
        subtitle,
        elementsWithPositions,
        upperToolbar,
        error,
        noFlexGrow,
        sx: sxProp,
        children,
    } = props;
    const theme = useTheme();
    // Un títol buit no pinta cap encapçalament buit
    const textTitle =
        typeof title !== 'string' || title.trim() !== '' ? (
            <Typography variant="h6" component={titleComponent}>
                {title}
            </Typography>
        ) : null;
    const titleElement = subtitle ? (
        <div style={{ minWidth: 0, flex: '1 1 auto' }}>
            {typeof title === 'string' ? textTitle : title}
            <Typography
                variant="body2"
                sx={{
                    color: error ? theme.palette.text.primary : theme.palette.text.secondary,
                    width: '100%',
                    overflow: 'hidden',
                    whiteSpace: 'nowrap',
                    textOverflow: 'ellipsis',
                }}>
                {subtitle}
            </Typography>
        </div>
    ) : typeof title === 'string' ? (
        // Es manté la posició 0 perquè els elementsWithPositions no es desplacin
        (textTitle ?? <></>)
    ) : (
        (title as React.ReactElement)
    );
    const flexGrow = !noFlexGrow ? <div style={{ flexGrow: 1 }} /> : <></>;
    const upperToolbarBgColor =
        theme.palette.mode === 'light' ? theme.palette.grey[200] : theme.palette.grey[900];
    const toolbarElements: React.ReactElement[] =
        title != null ? [titleElement, flexGrow] : [flexGrow];
    return (
        <>
            <MuiToolbar
                disableGutters
                sx={[
                    {
                        width: '100%',
                        display: 'flex',
                        px: upperToolbar ? 2 : 0,
                        ml: 0,
                        mr: 0,
                        mt: 0,
                        backgroundColor: upperToolbar ? upperToolbarBgColor : undefined,
                    },
                    ...(Array.isArray(sxProp) ? sxProp : [sxProp]),
                    // Després del sx de l'app perquè un degradat no tapi l'estat d'error.
                    error && {
                        background: theme.palette.mode === 'light' ? red[100] : red[900],
                        boxSizing: 'border-box',
                        borderLeft: `4px solid ${theme.palette.error.main}`,
                    },
                ]}>
                {joinReactElementsWithReactElementsWithPositions(
                    toolbarElements,
                    elementsWithPositions,
                    true
                )}
            </MuiToolbar>
            {children}
        </>
    );
};

export default Toolbar;
