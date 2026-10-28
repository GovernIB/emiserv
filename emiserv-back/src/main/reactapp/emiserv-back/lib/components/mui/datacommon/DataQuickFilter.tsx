import React from 'react';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { useBaseAppContext } from '../../BaseAppContext';

type DataQuickFilterProps = {
    value: string;
    setFocus?: true;
    /** Plegable: buit es mostra com un botó d'icona de cerca que obre el camp (per defecte false) */
    collapsible?: boolean;
    onChange: (value: string) => void;
} & any;

export const DataQuickFilter: React.FC<DataQuickFilterProps> = (props) => {
    const { value, setFocus, collapsible, onChange, ...otherProps } = props;
    const { t } = useBaseAppContext();
    const [opened, setOpened] = React.useState<boolean>(!!setFocus);
    const focusOnOpenRef = React.useRef<boolean>(false);
    const focusSearchButtonRef = React.useRef<boolean>(false);
    const inputElementRef = React.useRef<HTMLInputElement | null>(null);
    const mouseDownRef = React.useRef<boolean>(false);
    const valueRef = React.useRef<string>(value);
    valueRef.current = value;
    const inputId = React.useId();
    const expanded = !collapsible || opened || !!value;
    // Es plega si continua buit i sense el focus
    const collapseIfEmpty = () => {
        !valueRef.current &&
            document.activeElement !== inputElementRef.current &&
            setOpened(false);
    };
    // Plegable: es detecta el clic del ratolí (el blur arriba al mousedown) per no plegar el camp
    // fins després del clic; si no, la barra es mouria i el clic es podria perdre.
    React.useEffect(() => {
        if (!collapsible || !expanded) return;
        const onMouseDown = () => (mouseDownRef.current = true);
        const onMouseUp = () => (mouseDownRef.current = false);
        document.addEventListener('mousedown', onMouseDown, true);
        document.addEventListener('mouseup', onMouseUp, true);
        return () => {
            document.removeEventListener('mousedown', onMouseDown, true);
            document.removeEventListener('mouseup', onMouseUp, true);
        };
    }, [collapsible, expanded]);
    // Plegable: un valor buidat des de fora amb el camp sense el focus el plega
    React.useEffect(() => {
        collapsible && opened && !value && collapseIfEmpty();
    }, [value]);
    // Plegable: es manté obert mentre té valor (també si el valor és persistit)
    if (!expanded) {
        return (
            <IconButton
                title={t('datacommon.quickfilter.label')}
                aria-label={t('datacommon.quickfilter.label')}
                aria-expanded={false}
                aria-controls={inputId}
                ref={(button: HTMLButtonElement | null) => {
                    if (button && focusSearchButtonRef.current) {
                        focusSearchButtonRef.current = false;
                        button.focus();
                    }
                }}
                onClick={() => {
                    focusOnOpenRef.current = true;
                    setOpened(true);
                }}
                sx={otherProps.sx}>
                <Icon>search</Icon>
            </IconButton>
        );
    }
    const handleBlur = () => {
        if (valueRef.current) return;
        if (mouseDownRef.current) {
            // Es plega després del clic (el mouseup i el click van seguits)
            document.addEventListener(
                'mouseup',
                () => setTimeout(collapseIfEmpty, 0),
                { once: true, capture: true }
            );
        } else {
            setOpened(false);
        }
    };
    return (
        <TextField
            title={t('datacommon.quickfilter.label')}
            size="small"
            value={value}
            id={collapsible ? inputId : undefined}
            inputRef={
                collapsible
                    ? (input: HTMLInputElement | null) => {
                          inputElementRef.current = input;
                          if (input && (setFocus || focusOnOpenRef.current)) {
                              focusOnOpenRef.current = false;
                              input.focus();
                          }
                      }
                    : setFocus
                      ? (input) => input && input.focus()
                      : undefined
            }
            onChange={(event) => onChange(event.target.value)}
            onFocus={collapsible ? () => setOpened(true) : undefined}
            onBlur={collapsible ? handleBlur : undefined}
            onKeyDown={
                collapsible
                    ? (event: React.KeyboardEvent) => {
                          if (event.key === 'Escape' && !value) {
                              focusSearchButtonRef.current = true;
                              setOpened(false);
                          }
                      }
                    : undefined
            }
            slotProps={{
                htmlInput: {
                    'aria-label': t('datacommon.quickfilter.label'),
                },
                input: {
                    endAdornment: (
                        <>
                            {value && (
                                <InputAdornment position="end">
                                    <IconButton
                                        size="small"
                                        onClick={() => {
                                            onChange('');
                                            collapsible && setOpened(true);
                                            collapsible && inputElementRef.current?.focus();
                                        }}>
                                        <Icon fontSize="inherit">clear</Icon>
                                    </IconButton>
                                </InputAdornment>
                            )}
                            <InputAdornment position="end">
                                <Icon>search</Icon>
                            </InputAdornment>
                        </>
                    ),
                },
            }}
            {...otherProps}
        />
    );
};

export default DataQuickFilter;
