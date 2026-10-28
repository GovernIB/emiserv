import React from 'react';
import Box from '@mui/material/Box';
import { SxProps, Theme } from '@mui/material/styles';
import Toolbar from '../Toolbar';
import { toToolbarIcon } from '../ToolbarIcon';
import {
    ReactElementWithPosition,
    joinReactElementsWithPositionWithReactElementsWithPositions,
} from '../../../util/reactNodePosition';
import { useBaseAppContext } from '../../BaseAppContext';

export type DataToolbarType = 'default' | 'upper' | 'hidden';

type DataToolbarProps = {
    title?: string | React.ReactNode;
    titleComponent?: React.ElementType;
    subtitle?: string;
    elementsWithPositions?: ReactElementWithPosition[];
    upperToolbar?: boolean;
    error?: any;
    sx?: SxProps<Theme>;
};

export const useDataToolbar = (
    title: string | React.ReactNode,
    titleDisabled: boolean,
    subtitle: string | undefined,
    toolbarType: DataToolbarType,
    apiCurrentError: any,
    quickFilterComponent: React.ReactElement,
    doRefresh: () => void,
    doExport: () => void,
    toolbarShowBack?: boolean,
    toolbarHideExport?: boolean,
    toolbarHideRefresh?: boolean,
    toolbarHideQuickFilter?: boolean,
    toolbarElementsWithPositions?: ReactElementWithPosition[],
    toolbarSx?: SxProps<Theme>,
    toolbarTitleComponent?: React.ElementType,
    toolbarButtonsVariant?: 'icon' | 'button'
) => {
    const { t, goBack } = useBaseAppContext();
    const isUpperToolbarType = toolbarType === 'upper';
    const isHiddenToolbarType = toolbarType === 'hidden';
    const elementsWithPosition: ReactElementWithPosition[] = [];
    toolbarShowBack &&
        elementsWithPosition.push({
            position: 0,
            element: toToolbarIcon('arrow_back', {
                title: t('datacommon.back.label'),
                onClick: () => goBack(),
                sx: { mr: 1 },
                variant: toolbarButtonsVariant,
            }),
        });
    const toolbarNodesPosition = 2;
    !toolbarHideExport &&
        elementsWithPosition.push({
            position: toolbarNodesPosition,
            element: toToolbarIcon('file_download', {
                title: t('datacommon.export.label'),
                onClick: () => doExport(),
                variant: toolbarButtonsVariant,
            }),
        });
    !toolbarHideRefresh &&
        elementsWithPosition.push({
            position: toolbarNodesPosition,
            element: toToolbarIcon('refresh', {
                title: t('datacommon.refresh.label'),
                onClick: () => doRefresh(),
                variant: toolbarButtonsVariant,
            }),
        });
    !toolbarHideQuickFilter &&
        elementsWithPosition.push({
            position: toolbarNodesPosition,
            element: quickFilterComponent,
        });
    const joinedElementsWithPositions = joinReactElementsWithPositionWithReactElementsWithPositions(
        2,
        elementsWithPosition,
        toolbarElementsWithPositions
    );
    const toolbarElementProps = {
        title: !titleDisabled ? title : undefined,
        titleComponent: toolbarTitleComponent,
        subtitle,
        upperToolbar: isUpperToolbarType,
        elementsWithPositions: joinedElementsWithPositions,
        error: apiCurrentError ?? undefined,
        sx: toolbarSx,
    };
    return !isHiddenToolbarType ? <DataToolbar {...toolbarElementProps} /> : null;
};

const DataToolbar: React.FC<DataToolbarProps> = (props) => {
    const {
        title,
        titleComponent,
        subtitle,
        elementsWithPositions: elementsWithPositionsProp,
        upperToolbar,
        error,
        sx,
    } = props;
    const { t } = useBaseAppContext();
    const [errorShown, setErrorShown] = React.useState<boolean>(false);
    React.useEffect(() => {
        error && setErrorShown(true);
    }, [error]);
    const elementsWithPositions: ReactElementWithPosition[] = [];
    if (error) {
        elementsWithPositions.push({
            position: 0,
            element: toToolbarIcon('warning', {
                title: error.message,
                color: 'error',
                onClick: () => setErrorShown(true),
                sx: { mx: 1 },
            }),
        });
        errorShown &&
            elementsWithPositions.push({
                position: 1,
                element: toToolbarIcon('close', {
                    title: 'Tancar',
                    onClick: () => setErrorShown(false),
                    sx: { mr: 1 },
                }),
            });
    }
    !errorShown && elementsWithPositions.push(...(elementsWithPositionsProp ?? []));
    return (
        <Box sx={{ p: 0 }}>
            <Toolbar
                title={
                    error && errorShown && !React.isValidElement(title)
                        ? t('datacommon.toolbar.error')
                        : title
                }
                titleComponent={titleComponent}
                subtitle={
                    error && errorShown
                        ? error?.message ||
                          (React.isValidElement(title) ? t('datacommon.toolbar.error') : undefined)
                        : subtitle
                }
                elementsWithPositions={elementsWithPositions}
                upperToolbar={upperToolbar}
                error={error}
                sx={sx}
            />
        </Box>
    );
};

export default DataToolbar;
