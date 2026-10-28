import React from 'react';
import { SxProps } from '@mui/material';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Button from '@mui/material/Button';
import Badge from '@mui/material/Badge';
import { useBaseAppContext } from '../BaseAppContext';

type ToolbarIconParams = {
    title?: string;
    linkTo?: string;
    linkState?: any;
    badge?: React.ReactNode;
    onClick?: React.MouseEventHandler<HTMLButtonElement>;
    disabled?: boolean;
    small?: boolean;
    color?: string;
    sx?: SxProps;
    /** Aparença: 'icon' (per defecte, botó d'icona amb el títol com a title) o 'button' (botó amb la icona al principi i el títol com a text) */
    variant?: 'icon' | 'button';
};

type ToolbarIconProps = {
    icon: string;
    title?: string;
    linkTo?: string;
    badge?: React.ReactNode;
    onClick?: React.MouseEventHandler<HTMLButtonElement>;
    disabled?: boolean;
    small?: boolean;
    color?: string;
    variant?: 'icon' | 'button';
    otherProps?: any;
};

export const toToolbarIcon = (icon: string, params?: ToolbarIconParams): React.ReactElement => (
    <ToolbarIcon
        icon={icon}
        title={params?.title}
        linkTo={params?.linkTo}
        badge={params?.badge}
        small={params?.small}
        onClick={params?.onClick ?? undefined}
        disabled={params?.disabled}
        color={params?.color}
        variant={params?.variant}
        otherProps={{ sx: params?.sx }}
    />
);

export const ToolbarIcon: React.FC<ToolbarIconProps> = (props) => {
    const { icon, title, linkTo, badge, onClick, disabled, small, color, variant, otherProps } = props;
    const { getLinkComponent } = useBaseAppContext();
    const ic = <Icon fontSize={small ? 'small' : undefined}>{icon}</Icon>;
    const linkProps: any = linkTo
        ? {
              component: getLinkComponent(),
              to: linkTo,
          }
        : null;
    if (variant === 'button') {
        return (
            <Button
                onClick={onClick}
                disabled={disabled}
                size={small ? 'small' : undefined}
                color={color as any}
                startIcon={
                    badge ? (
                        <Badge badgeContent={badge} color="primary">
                            {ic}
                        </Badge>
                    ) : (
                        ic
                    )
                }
                {...linkProps}
                {...otherProps}>
                {title}
            </Button>
        );
    }
    return (
        <IconButton
            title={title}
            onClick={onClick}
            disabled={disabled}
            size={small ? 'small' : undefined}
            color={color}
            {...linkProps}
            {...otherProps}>
            {badge ? (
                <Badge badgeContent={badge} color="primary">
                    {ic}
                </Badge>
            ) : (
                ic
            )}
        </IconButton>
    );
};

export default ToolbarIcon;
