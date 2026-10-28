import React from 'react';
import Drawer from '@mui/material/Drawer';
import List from '@mui/material/List';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import ListItemButton from '@mui/material/ListItemButton';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Divider from '@mui/material/Divider';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { styled, useTheme, Theme, CSSObject } from '@mui/material/styles';
import { useBaseAppContext } from '../BaseAppContext';
import { MenuColors, useMuiBaseAppAppearance } from './MuiBaseAppContext';
import { useSmallScreen } from '../../util/useSmallScreen';
import { useHeaderHeight } from '../../util/useHeaderHeight';
import { Badge, BadgeProps } from '@mui/material';

export type MenuEntry = {
    id: string;
    title?: string;
    badgeProps?: BadgeProps;
    description?: string;
    to?: string;
    icon: string;
    onClick?: () => void;
    children?: MenuEntry[];
    divider?: boolean;
};

export type MenuProps = {
    title?: string;
    entries?: MenuEntry[];
    level?: number;
    onTitleClose?: () => void;
    shrink?: boolean;
    iconClicked?: boolean;
    drawerWidth?: number;
    footerHeight?: number;
    compactPanelWidth?: number;
    submenuTitleHeight?: number;
};

type ListMenuContentProps = MenuProps & {
    onMenuItemClick?: (entry: MenuEntry) => void;
    onMenuItemMouseEnter?: (entry: MenuEntry) => void;
    onMenuItemMouseLeave?: (entry: MenuEntry) => void;
    boldPrimary?: boolean;
    hideChildren?: boolean;
};

type MenuItemProps = React.PropsWithChildren & {
    entry: MenuEntry;
    level?: number;
    selected?: boolean;
    shrink?: boolean;
    boldPrimary?: boolean;
    onMenuItemClick?: (entry: MenuEntry) => void;
    onMenuItemMouseEnter?: (entry: MenuEntry) => void;
    onMenuItemMouseLeave?: (entry: MenuEntry) => void;
};

type MenuTitleProps = {
    title: string;
    onClose?: () => void;
};

const openedMixin = (theme: Theme, width: number | string): CSSObject => ({
    width,
    transition: theme.transitions.create('width', {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.enteringScreen,
    }),
});

const drawerClosedWidthSmall = (theme: Theme) => `calc(${theme.spacing(6)} + 1px)`;
const drawerClosedWidthStandard = (theme: Theme) => `calc(${theme.spacing(7)} + 1px)`;

const closedMixin = (theme: Theme, collapsedWidth?: number): CSSObject => ({
    transition: theme.transitions.create('width', {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
    }),
    ...(collapsedWidth != null
        ? { width: `${collapsedWidth}px` }
        : {
              width: drawerClosedWidthSmall(theme),
              [theme.breakpoints.up('sm')]: {
                  width: drawerClosedWidthStandard(theme),
              },
          }),
});

const DEFAULT_ICON_SIZE = 20;
const DEFAULT_SELECTED_COLOR = 'hsl(210, 100%, 60%)';

const paperColorsSx = (colors?: MenuColors): CSSObject => ({
    ...(colors?.background != null && { backgroundColor: colors.background }),
    ...(colors?.textPrimary != null && { color: colors.textPrimary }),
});

const ShrinkableDrawer = styled(Drawer, {
    shouldForwardProp: (prop) =>
        prop !== 'open' && prop !== 'width' && prop !== 'collapsedWidth' && prop !== 'menuColors',
})(({ theme, open, ...otherProps }) => {
    const width = (otherProps as any)?.['width'];
    const collapsedWidth = (otherProps as any)?.['collapsedWidth'] as number | undefined;
    const menuColors = (otherProps as any)?.['menuColors'] as MenuColors | undefined;
    const shrinkableStyles: CSSObject = {
        ...(open && {
            ...openedMixin(theme, width),
            '& .MuiDrawer-paper': openedMixin(theme, width),
        }),
        ...(!open && {
            ...closedMixin(theme, collapsedWidth),
            '& .MuiDrawer-paper': closedMixin(theme, collapsedWidth),
        }),
    };
    const paperSx = paperColorsSx(menuColors);
    if (shrinkableStyles['& .MuiDrawer-paper']) {
        shrinkableStyles['& .MuiDrawer-paper'] = {
            ...(shrinkableStyles['& .MuiDrawer-paper'] as CSSObject),
            ...paperSx,
        };
    }
    return {
        flexShrink: 0,
        boxSizing: 'border-box',
        ...shrinkableStyles,
    };
});

const StyledList = styled(List, {
    shouldForwardProp: (prop) => prop !== 'iconSize' && prop !== 'menuColors',
})<{ component?: React.ElementType; iconSize?: number; menuColors?: MenuColors }>(
    ({ iconSize, menuColors }) => ({
        '& .MuiListItemIcon-root': {
            minWidth: 0,
            marginRight: 16,
        },
        '& .MuiListItemText-root': {
            overflowWrap: 'anywhere',
        },
        '& .MuiSvgIcon-root': {
            fontSize: iconSize ?? DEFAULT_ICON_SIZE,
        },
        paddingTop: 0,
        paddingBottom: '8px',
        overflow: 'auto',
        overflowX: 'hidden',
        ...paperColorsSx(menuColors),
        ...(menuColors?.divider != null && {
            '& .MuiDivider-root': { borderColor: menuColors.divider },
        }),
    })
);

const COMPACT_PANEL_WIDTH = 250;
const SUBMENU_TITLE_HEIGHT = 48;

const isCurrentMenuEntryOrAnyChildrenSelected = (
    menuEntry: MenuEntry,
    locationPath: string
): boolean => {
    const anyChildSelected =
        menuEntry.children?.find((e) => isCurrentMenuEntryOrAnyChildrenSelected(e, locationPath)) !=
        null;
    if (menuEntry.to != null) {
        const menuEntryTo = locationPath.startsWith('/')
            ? menuEntry.to.startsWith('/')
                ? menuEntry.to
                : '/' + menuEntry.to
            : menuEntry.to;
        const selected = menuEntryTo === locationPath || locationPath.startsWith(menuEntryTo + '/');
        return selected || anyChildSelected;
    } else {
        return anyChildSelected;
    }
};

const menuItemIconClassName = 'menu-item-icon';

const MenuItem: React.FC<MenuItemProps> = (props) => {
    const {
        entry,
        level = 0,
        selected,
        shrink,
        boldPrimary = level === 0,
        onMenuItemClick,
        onMenuItemMouseEnter,
        onMenuItemMouseLeave,
        children,
    } = props;
    const { getLinkComponent } = useBaseAppContext();
    const {
        menuCollapsedWidth,
        menuIconSize,
        menuSelectedColor = DEFAULT_SELECTED_COLOR,
        menuSelectedIndicator = 'line',
        menuColors,
    } = useMuiBaseAppAppearance();
    const [expanded, setExpanded] = React.useState<boolean>(selected ?? false);
    const itemButtonSx = {
        minHeight: 48,
        justifyContent: !shrink ? 'initial' : 'center',
        ...(menuColors?.hoverBackground != null && {
            '&:hover': { backgroundColor: menuColors.hoverBackground },
        }),
        ...(menuSelectedIndicator === 'background' &&
            menuColors?.selectedBackground != null && {
                '&.Mui-selected, &.Mui-selected:hover': {
                    backgroundColor: menuColors.selectedBackground,
                },
            }),
        ...(menuColors?.accent != null && {
            [`&.Mui-selected .${menuItemIconClassName}`]: { color: menuColors.accent },
        }),
        '&>:before':
            level > 0 && !shrink && menuSelectedIndicator === 'line'
                ? {
                      content: '""',
                      display: 'block',
                      position: 'absolute',
                      zIndex: '100',
                      top: '0',
                      left: '40px',
                      height: '100%',
                      width: '2px',
                      opacity: '1',
                      background: selected ? menuSelectedColor : 'hsl(215, 15%, 92%)',
                  }
                : undefined,
    };
    const itemIconSx = {
        minWidth: 0,
        ml: !shrink ? 1 : menuCollapsedWidth != null ? 0 : -1,
        mr: !shrink ? 1 : menuCollapsedWidth != null ? 0 : 'auto',
        justifyContent: 'center',
        ...(menuColors?.textSecondary != null && { color: menuColors.textSecondary }),
    };
    const itemTextSx = {
        opacity: !shrink ? 1 : 0,
        '& span': {
            fontSize: '14px',
            fontWeight: boldPrimary ? 'bold' : undefined,
        },
    };
    const primary = entry.title ?? '';
    const to = entry.to;
    const icon = entry.icon;
    const handleMenuItemClick = () => {
        if (entry.onClick) {
            entry.onClick();
        } else {
            if (children != null) {
                setExpanded((expanded) => !expanded);
            } else {
                onMenuItemClick?.(entry);
            }
        }
    };
    const expandedIconComponent =
        children != null ? (
            <Icon fontSize={'small'}>{expanded ? 'expand_less' : 'expand_more'}</Icon>
        ) : null;
    const iconComponent = (
        <Icon fontSize={'small'} sx={menuIconSize != null ? { fontSize: menuIconSize } : undefined}>
            {icon}
        </Icon>
    );
    const listItemIconComponent = icon ? (
        <ListItemIcon className={menuItemIconClassName} sx={itemIconSx}>
            {entry.badgeProps ? (
                <Badge {...entry.badgeProps}>{iconComponent}</Badge>
            ) : (
                iconComponent
            )}
        </ListItemIcon>
    ) : null;
    return (
        <>
            {(!shrink || !children) && (
                <ListItemButton
                    title={shrink ? primary : undefined}
                    selected={selected}
                    to={children == null ? to : undefined}
                    component={
                        children == null ? (to != null ? getLinkComponent() : undefined) : undefined
                    }
                    onClick={handleMenuItemClick}
                    onMouseEnter={() => onMenuItemMouseEnter?.(entry)}
                    onMouseLeave={() => onMenuItemMouseLeave?.(entry)}
                    sx={itemButtonSx}
                    style={{
                        ...(shrink && menuCollapsedWidth != null && { paddingRight: 0 }),
                        paddingLeft: shrink
                            ? menuCollapsedWidth != null
                                ? 0
                                : '40px'
                            : 24 + 16 * level + (level > 0 ? 8 : 0) + 'px',
                    }}>
                    {listItemIconComponent}
                    {!shrink && <ListItemText primary={primary} sx={itemTextSx} />}
                    {expandedIconComponent}
                </ListItemButton>
            )}
            {(shrink || expanded) && children}
        </>
    );
};

const ListMenuContent: React.FC<ListMenuContentProps> = (props) => {
    const {
        entries,
        level,
        shrink,
        onMenuItemClick,
        onMenuItemMouseEnter,
        onMenuItemMouseLeave,
        boldPrimary,
        hideChildren,
    } = props;
    const { useLocationPath } = useBaseAppContext();
    const locationPath = useLocationPath();
    const { menuIconSize, menuColors, menuListItemSemantics } = useMuiBaseAppAppearance();
    return (
        <StyledList iconSize={menuIconSize} menuColors={menuColors}>
            {entries?.map((item, index) => {
                const selected = isCurrentMenuEntryOrAnyChildrenSelected(item, locationPath);
                const entryComponent = item.divider ? (
                    <Divider key={index} />
                ) : (
                    <MenuItem
                        key={index}
                        entry={item}
                        level={level}
                        selected={selected}
                        shrink={shrink}
                        boldPrimary={boldPrimary}
                        onMenuItemClick={onMenuItemClick}
                        onMenuItemMouseEnter={onMenuItemMouseEnter}
                        onMenuItemMouseLeave={onMenuItemMouseLeave}>
                        {item.children?.length && !hideChildren ? (
                            <Box>
                                <ListMenuContent
                                    entries={item.children}
                                    level={(level ?? 0) + 1}
                                    shrink={shrink}
                                    boldPrimary={boldPrimary}
                                    onMenuItemClick={onMenuItemClick}
                                />
                            </Box>
                        ) : null}
                    </MenuItem>
                );
                // El botó i la Box dels fills queden germans dins el mateix <li>
                return menuListItemSemantics ? (
                    <li key={index} style={{ listStyle: 'none' }}>
                        {entryComponent}
                    </li>
                ) : (
                    entryComponent
                );
            })}
        </StyledList>
    );
};

const MenuTitle: React.FC<MenuTitleProps> = (props) => {
    const { title, onClose } = props;
    const theme = useTheme();
    const { menuColors } = useMuiBaseAppAppearance();
    const handleButtonClick = () => onClose?.();
    return (
        <Box>
            <ListItemButton
                sx={{ backgroundColor: menuColors?.background ?? theme.palette.grey[200] }}>
                <ListItemIcon sx={{ minWidth: '40px' }}>
                    <IconButton size="small" onClick={handleButtonClick}>
                        <Icon fontSize={'small'}>clear</Icon>
                    </IconButton>
                </ListItemIcon>
                <ListItemText primary={title} sx={{ '& span': { fontWeight: 'bold' } }} />
            </ListItemButton>
            <Divider
                sx={menuColors?.divider != null ? { borderColor: menuColors.divider } : undefined}
            />
        </Box>
    );
};

export const Menu: React.FC<MenuProps> = (props) => {
    const {
        title,
        entries,
        onTitleClose,
        shrink,
        iconClicked,
        drawerWidth = 240,
        footerHeight,
        compactPanelWidth = COMPACT_PANEL_WIDTH,
        submenuTitleHeight = SUBMENU_TITLE_HEIGHT,
    } = props;
    const smallScreen = useSmallScreen();
    const headerHeight = useHeaderHeight();
    const theme = useTheme();
    const { menuCollapsedWidth, menuColors } = useMuiBaseAppAppearance();
    const [open, setOpen] = React.useState<boolean>(false);
    const [compactPanelEntryId, setCompactPanelEntryId] = React.useState<string | null>(null);
    const closeTimeoutRef = React.useRef<ReturnType<typeof setTimeout> | null>(null);
    const compactMenuContainerRef = React.useRef<HTMLDivElement | null>(null);
    const compactMode = !smallScreen && !!shrink;
    // El menú compacte no es mostra quan la mida de pantalla és petita
    const compactMenuWidth =
        menuCollapsedWidth != null ? `${menuCollapsedWidth}px` : drawerClosedWidthStandard(theme);
    const topMargin = `${headerHeight}px`;
    const compactPanelEntry = compactMode
        ? entries?.find((entry) => entry.id === compactPanelEntryId && entry.children?.length)
        : undefined;

    React.useEffect(() => {
        setOpen((o) => !o);
    }, [iconClicked]);
    React.useEffect(() => {
        setOpen(false);
    }, [smallScreen]);
    React.useEffect(() => {
        if (!compactMode || !compactPanelEntry) {
            return;
        }
        // Si hi ha un panell flotant obert, qualsevol clic fora del panell i fora
        // de la columna d'icones el tanca.
        const handlePointerDownOutside = (event: MouseEvent) => {
            const target = event.target as Node | null;
            if (target == null || !compactMenuContainerRef.current?.contains(target)) {
                setCompactPanelEntryId(null);
            }
        };
        document.addEventListener('mousedown', handlePointerDownOutside);
        return () => {
            document.removeEventListener('mousedown', handlePointerDownOutside);
        };
    }, [compactMode, compactPanelEntry]);
    const handleMenuItemClick = () => {
        setOpen(false);
        setCompactPanelEntryId(null);
    };
    const handleCompactEntryClick = (entry: MenuEntry) => {
        if (entry.children?.length) {
            setCompactPanelEntryId((current) => (current === entry.id ? null : entry.id));
        } else {
            setCompactPanelEntryId(null);
        }
    };
    const resetCloseTimeout = () => {
        if (closeTimeoutRef.current) {
            clearTimeout(closeTimeoutRef.current);
            closeTimeoutRef.current = null;
        }
    };
    const handleCompactEntryMouseEnter = (entry: MenuEntry) => {
        if (!compactMode || !window.matchMedia('(hover: hover)').matches) {
            return;
        }
        resetCloseTimeout();
        if (entry.children?.length) {
            setCompactPanelEntryId(entry.id);
        } else {
            setCompactPanelEntryId(null);
        }
    };
    const handleMouseLeave = () => {
        if (!compactMode || !window.matchMedia('(hover: hover)').matches) {
            return;
        }
        closeTimeoutRef.current = setTimeout(() => {
            setCompactPanelEntryId(null);
        }, 150);
    };
    const drawerContent = (
        <>
            <Box sx={{ mt: topMargin }} />
            {title && <MenuTitle title={title} onClose={onTitleClose} />}
            {compactMode ? (
                <div ref={compactMenuContainerRef}>
                    <ListMenuContent
                        entries={entries}
                        hideChildren={true}
                        shrink={true}
                        onMenuItemClick={handleCompactEntryClick}
                        onMenuItemMouseEnter={(entry) => handleCompactEntryMouseEnter(entry)}
                        onMenuItemMouseLeave={handleMouseLeave}
                    />
                    {compactPanelEntry ? (
                        <Box
                            onMouseEnter={resetCloseTimeout}
                            onMouseLeave={handleMouseLeave}
                            sx={(theme) => ({
                                position: 'fixed',
                                top: topMargin,
                                bottom: footerHeight ? `${footerHeight}px` : null,
                                left: compactMenuWidth,
                                width: compactPanelWidth,
                                borderRight: `1px solid ${menuColors?.divider ?? theme.palette.divider}`,
                                backgroundColor:
                                    menuColors?.background ?? theme.palette.background.paper,
                                color: menuColors?.textPrimary ?? theme.palette.text.primary,
                                overflowY: 'auto',
                                zIndex: theme.zIndex.drawer + 1,
                            })}>
                            <Box
                                sx={(theme) => ({
                                    px: 2,
                                    py: 1.7,
                                    backgroundColor:
                                        menuColors?.background ?? theme.palette.background.paper,
                                    borderBottom: `1px solid ${menuColors?.divider ?? theme.palette.divider}`,
                                    minHeight: submenuTitleHeight,
                                    overflowWrap: 'anywhere',
                                })}>
                                <Typography
                                    variant="subtitle1"
                                    sx={() => ({
                                        fontWeight: 700,
                                        color: menuColors?.accent ?? theme.palette.primary.light,
                                    })}>
                                    {compactPanelEntry.title}
                                </Typography>
                                {compactPanelEntry.description ? (
                                    <Typography
                                        variant="body2"
                                        sx={{
                                            mt: 0.25,
                                            color: menuColors?.textSecondary ?? 'text.secondary',
                                            fontSize: '0.6rem',
                                        }}>
                                        {compactPanelEntry.description}
                                    </Typography>
                                ) : null}
                            </Box>
                            <Box
                                sx={{
                                    [`& .${menuItemIconClassName}`]: { ml: -1 },
                                    overflowWrap: 'anywhere',
                                }}>
                                <ListMenuContent
                                    entries={compactPanelEntry.children}
                                    level={0}
                                    shrink={false}
                                    boldPrimary={false}
                                    onMenuItemClick={handleMenuItemClick}
                                />
                            </Box>
                        </Box>
                    ) : null}
                </div>
            ) : (
                <ListMenuContent
                    entries={entries}
                    shrink={false}
                    onMenuItemClick={handleMenuItemClick}
                />
            )}
            {footerHeight ? <Box sx={{ mb: footerHeight + 'px' }} /> : undefined}
        </>
    );
    return !smallScreen ? (
        <ShrinkableDrawer
            variant={'permanent'}
            open={!shrink}
            {...{ width: drawerWidth, collapsedWidth: menuCollapsedWidth, menuColors }}>
            {drawerContent}
        </ShrinkableDrawer>
    ) : (
        <Drawer
            open={open}
            onClose={() => setOpen(false)}
            sx={{
                display: { sm: 'block', md: 'none' },
                flexShrink: 0,
                '& .MuiDrawer-paper': {
                    width: drawerWidth,
                    boxSizing: 'border-box',
                    ...paperColorsSx(menuColors),
                },
            }}>
            {drawerContent}
        </Drawer>
    );
};

export default Menu;
