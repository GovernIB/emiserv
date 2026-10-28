import React from 'react';
import Box from '@mui/material/Box';
import Link from '@mui/material/Link';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import { BaseAppContext, toolbarBackgroundStyle } from 'reactlib';
import drassanaLogo from './assets/drassana.png';
import { useLot2Translation } from './i18n';
import { FOOTER_HEIGHT } from './layout';
import { DRASSANA_FOOTER_BACKGROUND } from './menuColors';

export type DrassanaFooterLink = { label: string; to: string };

export type DrassanaFooterProps = {
    /** Nom de l'aplicació. S'hi afegeix la versió del manifest (`Implementation-Version`). */
    title?: string;
    /** Enllaços del peu. Per defecte, el mapa del lloc web i l'accessibilitat; `[]` no en mostra cap. */
    links?: DrassanaFooterLink[];
    /** Text davant la revisió del manifest. Per defecte, `footer.revisio` de l'espai de noms `lot2`. */
    revisionLabel?: string;
    /** Logos de la dreta. Per defecte, el de Drassana. */
    logos?: string[];
    /** Color de fons. Per defecte, el gris de Drassana (`#5F5D5D`). */
    backgroundColor?: string;
    /** Estil de la barra (p. ex. per fixar-la a baix). */
    style?: React.CSSProperties;
    /** Contingut addicional, entre els enllaços i els logos. */
    children?: React.ReactNode;
};

type Manifest = { version?: string; buildTimestamp?: string; scmRevision?: string };

const readManifest = (): Manifest | undefined => {
    const manifest = (window as any).__MANIFEST__;
    return manifest
        ? {
              version: manifest['Implementation-Version'],
              buildTimestamp: manifest['Build-Timestamp'],
              scmRevision: manifest['Implementation-SCM-Revision'],
          }
        : undefined;
};

/** El manifest (`window.__MANIFEST__`) el carrega un script a part: s'espera fins a 5 segons. */
const useManifest = (): Manifest => {
    const [manifest, setManifest] = React.useState<Manifest | undefined>(readManifest);
    React.useEffect(() => {
        if (manifest) return;
        const interval = setInterval(() => {
            const loaded = readManifest();
            if (loaded) {
                clearInterval(interval);
                setManifest(loaded);
            }
        }, 100);
        const timeout = setTimeout(() => {
            clearInterval(interval);
            if (!readManifest()) {
                console.warn("Manifest no disponible després del temps d'espera");
            }
        }, 5000);
        return () => {
            clearInterval(interval);
            clearTimeout(timeout);
        };
    }, []);
    return manifest ?? {};
};

const FOOTER_TEXT_COLOR = '#F6F6F6';

/**
 * Peu de pàgina de Drassana (DrassanaFooter de Notib): títol i versió de l'aplicació, data de
 * compilació i revisió, enllaços i logo de Drassana. Els enllaços fan servir el `linkComponent` de
 * base-react (el de l'encaminador de l'aplicació).
 */
export const DrassanaFooter: React.FC<DrassanaFooterProps> = (props) => {
    const {
        title,
        links,
        revisionLabel,
        logos = [drassanaLogo],
        backgroundColor = DRASSANA_FOOTER_BACKGROUND,
        style,
        children,
    } = props;
    const { t } = useLot2Translation();
    const linkComponent = React.useContext(BaseAppContext)?.getLinkComponent() ?? 'a';
    const { version, buildTimestamp, scmRevision } = useManifest();
    const footerLinks = links ?? [
        { label: t('footer.sitemap'), to: '/sitemap' },
        { label: t('footer.accessibilitat'), to: '/accessibilitat' },
    ];
    const titleWithVersion = (title ?? '') + (version ? ' v' + version : '');
    return (
        <Toolbar
            style={{ ...style, ...toolbarBackgroundStyle(backgroundColor) }}
            sx={{
                color: FOOTER_TEXT_COLOR,
                minHeight: FOOTER_HEIGHT + 'px !important',
                lineHeight: '0.5em',
                gap: 1.5,
                zIndex: (theme) => theme.zIndex.drawer + 100,
            }}>
            <Typography
                variant="caption"
                component="div"
                noWrap
                title={titleWithVersion}
                sx={{
                    flexGrow: 1,
                    minWidth: 0,
                    alignSelf: 'flex-start',
                    fontSize: '14px',
                    fontWeight: 'bold',
                    mt: 1,
                    color: FOOTER_TEXT_COLOR,
                }}>
                {titleWithVersion}
                <span aria-hidden="true" style={{ color: backgroundColor, marginLeft: '16px' }}>
                    ({buildTimestamp} | {revisionLabel ?? t('footer.revisio')} {scmRevision})
                </span>
            </Typography>
            {footerLinks.map((link) => (
                <Link
                    key={link.to}
                    component={linkComponent}
                    {...{ to: link.to, href: link.to }}
                    underline="hover"
                    sx={{ color: FOOTER_TEXT_COLOR }}>
                    {link.label}
                </Link>
            ))}
            {children}
            {logos.map((logo, index) => (
                <Box key={index} sx={{ height: FOOTER_HEIGHT + 'px' }}>
                    <img src={logo} alt="Drassana" style={{ maxHeight: FOOTER_HEIGHT + 'px' }} />
                </Box>
            ))}
        </Toolbar>
    );
};

export default DrassanaFooter;
