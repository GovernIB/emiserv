import React from 'react';
import { useTranslation } from 'react-i18next';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import CircularProgress from '@mui/material/CircularProgress';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Paper from '@mui/material/Paper';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import { useTheme } from '@mui/material/styles';
import { useBaseAppContext } from 'reactlib';
import { icons } from '../../lot2';
import { descarregarFitxer } from '../../util/descarregarFitxer';
import { missatgeError } from '../../util/missatgeError';
import { analitzarXml, formatarText, type NodeXml } from '../../util/xml';

/** Colors del ressaltat: els de la paleta del tema, perquè es llegeixin en clar i en fosc. */
const useColors = () => {
    const theme = useTheme();
    return {
        etiqueta: theme.palette.primary.main,
        atribut: theme.palette.secondary.main,
        valor: theme.palette.success.main,
        comentari: theme.palette.text.disabled,
    };
};

type Colors = ReturnType<typeof useColors>;

/** Un element de l'arbre: amb fills, es pot plegar clicant el triangle. */
const NodeVisor: React.FC<{ node: NodeXml; colors: Colors; plegatInicial: boolean }> = ({
    node,
    colors,
    plegatInicial,
}) => {
    const [plegat, setPlegat] = React.useState(plegatInicial);
    if (node.tipus === 'text') {
        return <Box component="span">{node.text}</Box>;
    }
    if (node.tipus === 'cdata') {
        return <Box component="span" sx={{ color: colors.comentari }}>{`<![CDATA[${node.text}]]>`}</Box>;
    }
    if (node.tipus === 'comentari') {
        return <Box component="span" sx={{ color: colors.comentari }}>{`<!--${node.text}-->`}</Box>;
    }
    const obertura = (tancat: boolean) => (
        <>
            <Box component="span" sx={{ color: colors.etiqueta }}>
                {'<' + node.nom}
            </Box>
            {node.atributs.map(([nom, valor]) => (
                <React.Fragment key={nom}>
                    {' '}
                    <Box component="span" sx={{ color: colors.atribut }}>
                        {nom}
                    </Box>
                    =<Box component="span" sx={{ color: colors.valor }}>{`"${valor}"`}</Box>
                </React.Fragment>
            ))}
            <Box component="span" sx={{ color: colors.etiqueta }}>
                {tancat ? '/>' : '>'}
            </Box>
        </>
    );
    const tancament = <Box component="span" sx={{ color: colors.etiqueta }}>{`</${node.nom}>`}</Box>;
    if (node.fills.length === 0) {
        return <Box sx={{ pl: 2 }}>{obertura(true)}</Box>;
    }
    if (node.fills.length === 1 && node.fills[0].tipus === 'text') {
        return (
            <Box sx={{ pl: 2 }}>
                {obertura(false)}
                <Box component="span" sx={{ wordBreak: 'break-all' }}>
                    {node.fills[0].text}
                </Box>
                {tancament}
            </Box>
        );
    }
    return (
        <Box sx={{ pl: 2, position: 'relative' }}>
            <Box
                component="button"
                type="button"
                aria-expanded={!plegat}
                aria-label={node.nom}
                onClick={() => setPlegat((p) => !p)}
                sx={{
                    position: 'absolute',
                    left: 0,
                    top: 0,
                    width: 16,
                    p: 0,
                    border: 0,
                    background: 'none',
                    color: 'text.secondary',
                    cursor: 'pointer',
                    font: 'inherit',
                }}
            >
                {plegat ? '▸' : '▾'}
            </Box>
            {obertura(false)}
            {plegat ? (
                <Box
                    component="span"
                    sx={{ color: colors.comentari, cursor: 'pointer' }}
                    onClick={() => setPlegat(false)}
                >
                    {' … '}
                </Box>
            ) : (
                node.fills.map((fill, index) => (
                    <Box key={index} sx={fill.tipus === 'element' ? undefined : { pl: 2 }}>
                        <NodeVisor node={fill} colors={colors} plegatInicial={false} />
                    </Box>
                ))
            )}
            {tancament}
        </Box>
    );
};

type XmlVisorProps = {
    titol: string;
    /** Carrega el missatge en obrir el visor (null o buit: no n'hi ha). */
    carregar: () => Promise<string | null>;
    /** Nom del fitxer descarregat. */
    nomFitxer: string;
};

/**
 * Visor d'un missatge (spec §5.4): es carrega en obrir-lo i, si és XML, es mostra formatat, ressaltat i
 * amb els nodes plegables; si no ho és (p. ex. un error de comunicació), com a text. Es pot copiar i
 * descarregar (formatat).
 */
export const XmlVisor: React.FC<XmlVisorProps> = ({ titol, carregar, nomFitxer }) => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const colors = useColors();
    const [estat, setEstat] = React.useState<{ carregant: boolean; text?: string | null; error?: string }>({
        carregant: true,
    });
    const [versio, setVersio] = React.useState(0);
    React.useEffect(() => {
        let actiu = true;
        setEstat({ carregant: true });
        carregar()
            .then((text) => actiu && setEstat({ carregant: false, text }))
            .catch((error: unknown) => actiu && setEstat({ carregant: false, error: missatgeError(error) }));
        return () => {
            actiu = false;
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [versio]);
    const arrel = React.useMemo(() => (estat.text ? analitzarXml(estat.text) : null), [estat.text]);
    const formatat = React.useMemo(() => (estat.text ? formatarText(estat.text) : ''), [estat.text]);
    const copiar = () => {
        navigator.clipboard
            .writeText(formatat)
            .then(() => temporalMessageShow(null, t('auditoria.xml.copiat'), 'success'))
            .catch(() => temporalMessageShow(null, t('auditoria.xml.errorCopiar'), 'error'));
    };
    const descarregar = () =>
        descarregarFitxer(
            new Blob([formatat], { type: arrel != null ? 'application/xml' : 'text/plain' }),
            nomFitxer + (arrel != null ? '.xml' : '.txt')
        );
    const teText = !estat.carregant && !estat.error && !!estat.text;
    return (
        <Paper variant="outlined" sx={{ display: 'flex', flexDirection: 'column', minHeight: 0, flex: 1, minWidth: 0 }}>
            <Box
                sx={{
                    display: 'flex',
                    alignItems: 'center',
                    px: 1.5,
                    py: 0.5,
                    borderBottom: 1,
                    borderColor: 'divider',
                }}
            >
                <Typography variant="subtitle2" sx={{ flex: 1 }}>
                    {titol}
                </Typography>
                <Tooltip title={t('auditoria.xml.copiar')}>
                    <span>
                        <IconButton
                            size="small"
                            aria-label={t('auditoria.xml.copiar')}
                            disabled={!teText}
                            onClick={copiar}
                        >
                            <Icon fontSize="small">{icons.copia}</Icon>
                        </IconButton>
                    </span>
                </Tooltip>
                <Tooltip title={t('auditoria.xml.descarregar')}>
                    <span>
                        <IconButton
                            size="small"
                            aria-label={t('auditoria.xml.descarregar')}
                            disabled={!teText}
                            onClick={descarregar}
                        >
                            <Icon fontSize="small">{icons.descarrega}</Icon>
                        </IconButton>
                    </span>
                </Tooltip>
            </Box>
            <Box
                sx={{
                    flex: 1,
                    minHeight: 120,
                    overflow: 'auto',
                    p: 1.5,
                    fontFamily: 'monospace',
                    fontSize: 12.5,
                    lineHeight: 1.5,
                    whiteSpace: arrel != null ? 'normal' : 'pre-wrap',
                    wordBreak: 'break-word',
                }}
            >
                {estat.carregant && <CircularProgress size={24} />}
                {estat.error && (
                    <Alert
                        severity="error"
                        action={
                            <IconButton
                                size="small"
                                aria-label={t('auditoria.xml.reintentar')}
                                onClick={() => setVersio((v) => v + 1)}
                            >
                                <Icon fontSize="small">{icons.refresca}</Icon>
                            </IconButton>
                        }
                    >
                        {estat.error}
                    </Alert>
                )}
                {!estat.carregant && !estat.error && !estat.text && (
                    <Typography variant="body2" color="text.secondary" sx={{ fontFamily: 'inherit' }}>
                        {t('auditoria.xml.buit')}
                    </Typography>
                )}
                {teText &&
                    (arrel != null ? (
                        <Box sx={{ ml: -2 }}>
                            <NodeVisor node={arrel} colors={colors} plegatInicial={false} />
                        </Box>
                    ) : (
                        estat.text
                    ))}
            </Box>
        </Paper>
    );
};

export default XmlVisor;
