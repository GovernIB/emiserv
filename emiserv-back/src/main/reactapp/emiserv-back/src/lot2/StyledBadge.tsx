import { styled } from '@mui/material/styles';
import Badge from '@mui/material/Badge';
import { accentFillColor } from './theme';

type ThemeColorKey = 'primary' | 'secondary' | 'error' | 'warning' | 'info' | 'success';

interface StyledBadgeProps {
    badgecolor?: ThemeColorKey | string;
    textcolor?: ThemeColorKey | string;
}

export const StyledBadge = styled(Badge)<StyledBadgeProps>(({ theme, badgecolor = 'primary', textcolor }) => {
    const resolveColor = (value: string | undefined) =>
        value === 'primary' ? accentFillColor(theme) : theme.palette[value as ThemeColorKey]?.main || value;
    const background = resolveColor(badgecolor) as string;

    return {
        paddingLeft: theme.spacing(1),
        paddingRight: theme.spacing(1),
        '& .MuiBadge-badge': {
            backgroundColor: background,
            color: resolveColor(
                textcolor ?? (badgecolor === 'primary' ? theme.palette.getContrastText(background) : 'white')
            ),
        },
    };
});
