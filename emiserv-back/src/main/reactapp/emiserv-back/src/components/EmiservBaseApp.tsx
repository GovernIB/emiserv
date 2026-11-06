import React from 'react';
import {
    Link as RouterLink,
    type LinkProps as RouterLinkProps,
    useBlocker,
    useLocation,
    useNavigate,
} from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import { useTheme } from '@mui/material/styles';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import 'dayjs/locale/ca';
import 'dayjs/locale/es';
import { type MenuEntry, useBaseAppContext, useResourceApiContext } from 'reactlib';
import { DrassanaFooter, icons, Lot2BaseApp } from '../lot2';
import i18n, { idiomaNavegador } from '../i18n';
import goibLogoLight from '../assets/goib_logo_light.svg';
import goibLogoDark from '../assets/goib_logo_dark.svg';
import { InterficieClassicaButton } from './InterficieClassica';
import { UserProfileDialog, UserProfileMenuItem } from './UserProfile';
import { ICONES_ROL, SelectorRol } from './SelectorRol';
import { useEsAdmin, useSessioUsuari } from './EmiservAuthProvider';
import { MANTENIMENTS } from '../pages/configuracio/manteniments';

/** Icones de les pantalles d'EMISERV (les comunes surten del catàleg `icons` de `lot2`). */
// eslint-disable-next-line react-refresh/only-export-components
export const emiservIcons = {
    servei: 'miscellaneous_services',
    auditoria: 'policy',
    auditoriaBackoffice: 'dns',
    auditoriaEnrutador: 'alt_route',
    configuracio: 'settings',
    interficieClassica: icons.interficieClassica,
};

const Link = React.forwardRef<HTMLAnchorElement, RouterLinkProps>((itemProps, ref) => (
    <RouterLink ref={ref} {...itemProps} role={undefined} />
));

const useLocationPath = () => useLocation().pathname;

const CustomLocalizationProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
    const { currentLanguage } = useBaseAppContext();
    const adapterLocale = currentLanguage?.substring(0, 2).toLowerCase() === 'es' ? 'es' : 'ca';
    return (
        <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale={adapterLocale}>
            {children}
        </LocalizationProvider>
    );
};

const version = import.meta.env.VITE_APP_VERSION ?? '0.0.0';

/**
 * Aplica l'idioma del perfil (o, si no en té, el del navegador) en carregar l'usuari i en desar el
 * perfil. Es canvia el `currentLanguage` de base-react, que el propaga a i18n: canviar i18n
 * directament mentre arrenca fa que les dues fonts es corregeixin l'una a l'altra sense fi.
 */
const useIdiomaPerfil = () => {
    const { usuari } = useSessioUsuari();
    const { setCurrentLanguage } = useResourceApiContext();
    const idioma = usuari != null ? (usuari.idioma ?? idiomaNavegador()) : undefined;
    React.useEffect(() => {
        if (idioma != null) {
            setCurrentLanguage(idioma);
        }
    }, [idioma, setCurrentLanguage]);
};

/**
 * Marc de l'aplicació: `Lot2BaseApp` amb els logos, el peu de Drassana i el menú d'EMISERV.
 *
 * El menú només enllaça pantalles de la interfície REACT: serveis, les dues auditories i, per als
 * administradors, la configuració.
 */
export const EmiservBaseApp: React.FC<React.PropsWithChildren> = ({ children }) => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const location = useLocation();
    const { usuari } = useSessioUsuari();
    const [perfilObert, setPerfilObert] = React.useState(false);

    const esAdmin = useEsAdmin();
    const theme = useTheme();
    const fonsCapcalera = theme.palette.mode === 'light' ? theme.palette.background.paper : undefined;
    const textCapcalera = fonsCapcalera ? theme.palette.getContrastText(fonsCapcalera) : undefined;
    useIdiomaPerfil();
    const menuEntries: MenuEntry[] = [
        {
            id: 'serveis',
            title: t('app.menu.serveis'),
            to: '/serveis',
            icon: emiservIcons.servei,
        },
        // Informes: només amb el rol actual d'administrador, com el menú de la JSP
        ...(esAdmin ? [{ id: 'informes', title: t('app.menu.informes'), icon: 'summarize', to: '/informes' }] : []),
        {
            id: 'auditoria',
            title: t('app.menu.auditoria'),
            icon: emiservIcons.auditoria,
            children: [
                {
                    id: 'auditoriaBackoffice',
                    title: t('app.menu.backoffice'),
                    icon: emiservIcons.auditoriaBackoffice,
                    to: '/auditoria/backoffice',
                },
                {
                    id: 'auditoriaEnrutador',
                    title: t('app.menu.enrutador'),
                    icon: emiservIcons.auditoriaEnrutador,
                    to: '/auditoria/enrutador',
                },
            ],
        },
        // Configuració: només per als administradors, com el menú de la JSP
        ...(esAdmin
            ? [
                  {
                      id: 'configuracio',
                      title: t('app.menu.configuracio'),
                      icon: emiservIcons.configuracio,
                      children: [
                          ...MANTENIMENTS.map((m) => ({
                              id: `configuracio_${m.clau}`,
                              title: t(`configuracio.${m.clau}.menu`),
                              icon: m.icona,
                              to: `/configuracio/${m.clau}`,
                          })),
                          {
                              id: 'configuracio_propietats',
                              title: t('configuracio.propietats.menu'),
                              icon: 'settings_applications',
                              to: '/configuracio/propietats',
                          },
                      ],
                  },
              ]
            : []),
    ];

    const anyHistoryEntryExist = () => location.key !== 'default';
    const goBack = (fallback?: string) => {
        if (fallback != null) {
            navigate(fallback);
        } else if (anyHistoryEntryExist()) {
            navigate(-1);
        }
    };

    return (
        <Lot2BaseApp
            code="EMISERV"
            logo={goibLogoLight}
            logoDark={goibLogoDark}
            headerTitle={t('app.titol')}
            headerVersion={version}
            headerAuthBadgeIcon={usuari?.rolActual ? ICONES_ROL[usuari.rolActual] : undefined}
            // Capçalera segons el tema, com a Distribució i Notib: al tema clar, el fons del paper (no el
            // color de l'aplicació); als foscos, la barra del tema
            headerAppbarBackgroundColor={fonsCapcalera}
            headerAppbarStyle={{ color: textCapcalera }}
            headerAdditionalComponents={[
                <Box key="interficieClassica" sx={{ display: 'flex', alignItems: 'center', mr: 1 }}>
                    <InterficieClassicaButton />
                </Box>,
            ]}
            headerAdditionalAuthComponents={[
                <SelectorRol key="rol" />,
                <UserProfileMenuItem key="perfil" onClick={() => setPerfilObert(true)} />,
            ]}
            menuEntries={menuEntries}
            footer={<DrassanaFooter title="EMISERV" links={[]} />}
            i18nUseTranslation={useTranslation}
            i18nCurrentLanguage={i18n.language}
            i18nHandleLanguageChange={(language?: string) => i18n.changeLanguage(language)}
            i18nAddResourceBundleCallback={(language: string, namespace: string, bundle: unknown) =>
                i18n.addResourceBundle(language, namespace, bundle)
            }
            routerGoBack={goBack}
            routerNavigate={navigate}
            routerUseBlocker={useBlocker}
            routerUseLocationPath={useLocationPath}
            routerAnyHistoryEntryExist={anyHistoryEntryExist}
            linkComponent={Link}
        >
            <UserProfileDialog open={perfilObert} onClose={() => setPerfilObert(false)} />
            <CustomLocalizationProvider>{children}</CustomLocalizationProvider>
        </Lot2BaseApp>
    );
};

export default EmiservBaseApp;
