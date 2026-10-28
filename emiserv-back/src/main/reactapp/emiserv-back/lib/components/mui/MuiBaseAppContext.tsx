import React from 'react';
import { MuiDataGridProps } from './datagrid/MuiDataGrid';
import { MuiDataListProps } from './datalist/MuiDataList';
import { MuiFormProps } from './form/MuiForm';
import { MuiFilterProps } from './form/MuiFilter';
import { MuiDetailProps } from './detail/MuiDetail';

export type DefaultMuiComponentProps = {
    dataGrid?: Partial<MuiDataGridProps>;
    dataList?: Partial<MuiDataListProps>;
    form?: Partial<MuiFormProps>;
    filter?: Partial<MuiFilterProps>;
    detail?: Partial<MuiDetailProps>;
};

export type MenuColors = {
    background?: string;
    textPrimary?: string;
    textSecondary?: string;
    divider?: string;
    accent?: string;
    selectedBackground?: string;
    hoverBackground?: string;
};

export type MuiBaseAppAppearance = {
    headerHeight?: number;
    menuCollapsedWidth?: number;
    menuIconSize?: number;
    menuSelectedColor?: string;
    menuSelectedIndicator?: 'line' | 'background' | 'none';
    menuColors?: MenuColors;
    /** Embolcalla cada entrada del menú amb un <li> i pinta els divisors dins un <li> (<li><hr/></li>) (per defecte false) */
    menuListItemSemantics?: boolean;
};

export type MuiBaseAppContextType = {
    defaultMuiComponentProps: DefaultMuiComponentProps;
    appearance: MuiBaseAppAppearance;
};

export const MuiBaseAppContext = React.createContext<MuiBaseAppContextType | undefined>(undefined);
export const useMuiBaseAppContext = () => {
    const context = React.useContext(MuiBaseAppContext);
    if (context === undefined) {
        throw new Error('useMuiBaseAppContext must be used within a MuiBaseAppProvider');
    }
    return context;
};

export const useOptionalMuiBaseAppContext = (): MuiBaseAppContextType | undefined => {
    return React.useContext(MuiBaseAppContext);
};

const EMPTY_APPEARANCE: MuiBaseAppAppearance = {};

export const useMuiBaseAppAppearance = (): MuiBaseAppAppearance => {
    return React.useContext(MuiBaseAppContext)?.appearance ?? EMPTY_APPEARANCE;
};

export default MuiBaseAppContext;
