import React from 'react';
import Box from '@mui/material/Box';
import { SxProps, Theme } from '@mui/material/styles';
import { Form, FormProps, useFormApiContext } from '../../form/Form';
import { useBaseAppContext } from '../../BaseAppContext';
import { useMuiBaseAppContext } from '../MuiBaseAppContext';
import { useFormContext } from '../../form/FormContext';
import { useHeaderHeight } from '../../../util/useHeaderHeight';
import { ReactElementWithPosition } from '../../../util/reactNodePosition';
import { toToolbarIcon } from '../ToolbarIcon';
import { Toolbar } from '../Toolbar';

/**
 * Propietats del component MuiForm (també conté les propietats del component Form).
 */
export type MuiFormProps = Omit<FormProps, 'title'> & {
    /** Títol de la barra d'eines */
    title?: string | React.ReactElement;
    /** Subtítol de la barra d'eines */
    toolbarSubtitle?: string;
    /** Elements addicionals (amb la seva posició) per a la barra d'eines */
    toolbarElementsWithPositions?: ReactElementWithPosition[];
    /** Indica si la barra d'eines està oculta */
    hiddenToolbar?: true;
    /** Indica si el botó de retrocedir ha d'estar ocult */
    hiddenBackButton?: true;
    /** Indica si el botó de restablir els valors ha d'estar ocult */
    hiddenRevertButton?: true;
    /** Indica si el botó de desar ha d'estar ocult */
    hiddenSaveButton?: true;
    /** Indica si el botó d'esborrar ha d'estar ocult */
    hiddenDeleteButton?: true;
    /** Estils addicionals per a la barra d'eines (es combinen darrere dels per defecte) */
    toolbarSx?: SxProps<Theme>;
    /** Element HTML del títol de la barra d'eines (per defecte 'h6') */
    toolbarTitleComponent?: React.ElementType;
    /** Estils dels marges del contingut (substitueixen els per defecte) */
    marginsSx?: SxProps<Theme>;
    /** Propietats del component del formulari */
    componentProps?: any;
};

const MuiFormContent: React.FC<React.PropsWithChildren | any> = (props) => {
    const {
        id,
        title,
        resourceName,
        toolbarSubtitle,
        toolbarElementsWithPositions,
        goBackLink,
        hiddenToolbar,
        hiddenBackButton,
        hiddenRevertButton,
        hiddenSaveButton,
        hiddenDeleteButton,
        toolbarSx,
        toolbarTitleComponent,
        marginsSx,
        componentProps,
        children,
    } = props;
    const headerHeight = useHeaderHeight();
    const formApiRef = useFormApiContext();
    const { t, goBack, anyHistoryEntryExist, contentExpandsToAvailableHeight } =
        useBaseAppContext();
    const { modified, isSaving, isSaveActionPresent, isDeleteActionPresent } = useFormContext();
    const backButtonDisabled = !anyHistoryEntryExist() && !goBackLink;
    const toolbarNodes: ReactElementWithPosition[] = [];
    !hiddenBackButton &&
        toolbarNodes.push({
            position: 0,
            element: toToolbarIcon('arrow_back', {
                title: t('form.goBack.title'),
                onClick: () => goBack(goBackLink),
                disabled: backButtonDisabled || isSaving,
                sx: { mr: 1 },
            }),
        });
    toolbarNodes.push(...(toolbarElementsWithPositions ?? []));
    !hiddenRevertButton &&
        isSaveActionPresent &&
        toolbarNodes.push({
            position: 2,
            element: toToolbarIcon('undo', {
                title: t('form.revert.title'),
                onClick: () => formApiRef.current?.revert(),
                disabled: !modified || isSaving,
            }),
        });
    !hiddenSaveButton &&
        isSaveActionPresent &&
        toolbarNodes.push({
            position: 2,
            element: toToolbarIcon('save', {
                title: t(id != null ? 'form.update.title' : 'form.create.title'),
                // Es rebutja sense motiu si ja s'està desant (o si el formulari ja ha mostrat els
                // errors); la resta de motius es deixen veure a la consola
                onClick: () =>
                    formApiRef.current?.save().catch((reason: any) => {
                        reason != null && console.error(reason);
                    }),
                disabled: isSaving,
            }),
        });
    !hiddenDeleteButton &&
        isDeleteActionPresent &&
        toolbarNodes.push({
            position: 2,
            element: toToolbarIcon('delete', {
                title: t('form.delete.title'),
                onClick: () => formApiRef.current?.delete(),
                disabled: isSaving,
            }),
        });
    const outerBoxStyles = contentExpandsToAvailableHeight
        ? { display: 'flex', flexDirection: 'column', height: '100%' }
        : undefined;
    const sx2 = contentExpandsToAvailableHeight
        ? { display: 'flex', flexDirection: 'column', height: '100%' }
        : undefined;
    const innerBoxStyles = hiddenToolbar
        ? { mt: 1, ...sx2, ...componentProps?.sx }
        : marginsSx != null
          ? [
              ...(Array.isArray(marginsSx) ? marginsSx : [marginsSx]),
              { ...sx2, ...componentProps?.sx },
          ]
          : { m: 2, mt: 3, ...sx2, ...componentProps?.sx };
    return (
        <Box sx={outerBoxStyles}>
            {!hiddenToolbar && (
                <Toolbar
                    title={title ?? resourceName}
                    titleComponent={toolbarTitleComponent}
                    subtitle={toolbarSubtitle}
                    elementsWithPositions={toolbarNodes}
                    upperToolbar
                    sx={[
                        {
                            position: 'sticky',
                            top: `${headerHeight}px`,
                            zIndex: 10,
                        },
                        ...(Array.isArray(toolbarSx) ? toolbarSx : [toolbarSx]),
                    ]}
                />
            )}
            <Box sx={innerBoxStyles}>{children}</Box>
        </Box>
    );
};

/**
 * Component de formulari per a la llibreria MUI.
 *
 * @param props - Propietats del component.
 * @returns Element JSX del formulari.
 */
export const MuiForm: React.FC<MuiFormProps> = (props) => {
    const { defaultMuiComponentProps } = useMuiBaseAppContext();
    const joinedProps = { ...defaultMuiComponentProps.form, ...props };
    const {
        id,
        title,
        resourceName,
        toolbarSubtitle,
        toolbarElementsWithPositions,
        goBackLink,
        hiddenToolbar,
        hiddenBackButton,
        hiddenRevertButton,
        hiddenSaveButton,
        hiddenDeleteButton,
        toolbarSx,
        toolbarTitleComponent,
        marginsSx,
        componentProps,
        children,
    } = joinedProps;
    return (
        <Form {...(joinedProps as FormProps)}>
            <MuiFormContent
                id={id}
                title={title}
                resourceName={resourceName}
                toolbarSubtitle={toolbarSubtitle}
                toolbarElementsWithPositions={toolbarElementsWithPositions}
                goBackLink={goBackLink}
                hiddenToolbar={hiddenToolbar}
                hiddenBackButton={hiddenBackButton}
                hiddenRevertButton={hiddenRevertButton}
                hiddenSaveButton={hiddenSaveButton}
                hiddenDeleteButton={hiddenDeleteButton}
                toolbarSx={toolbarSx}
                toolbarTitleComponent={toolbarTitleComponent}
                marginsSx={marginsSx}
                componentProps={componentProps}>
                {children}
            </MuiFormContent>
        </Form>
    );
};
export default MuiForm;
