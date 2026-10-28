import React from 'react';
import {
    Link as RouterLink,
    type LinkProps as RouterLinkProps,
    useBlocker,
    useLocation,
    useNavigate,
} from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import 'dayjs/locale/ca';
import 'dayjs/locale/es';
import { type MenuEntry, useBaseAppContext } from 'reactlib';
import { DrassanaFooter, icons, Lot2BaseApp } from '../lot2';
import i18n from '../i18n';
import goibLogoLight from '../assets/goib_logo_light.svg';
import goibLogoDark from '../assets/goib_logo_dark.svg';
import { useInterficieClassica } from './InterficieClassica';
import { UserProfileDialog, UserProfileMenuItem } from './UserProfile';
import { useSessioUsuari } from './EmiservAuthProvider';

/** Icones de les pantalles d'EMISERV (les comunes surten del catàleg `icons` de `lot2`). */
// eslint-disable-next-line react-refresh/only-export-components
export const emiservIcons = {
    servei: 'miscellaneous_services',
    auditoria: 'policy',
    auditoriaBackoffice: 'dns',
    auditoriaEnrutador: 'alt_route',
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
 * Marc de l'aplicació: `Lot2BaseApp` amb els logos, el peu de Drassana i el menú d'EMISERV.
 *
 * El menú només enllaça pantalles de la interfície REACT. Les pantalles d'auditoria es migren a la
 * fase 3: fins llavors les seves entrades hi són, però no fan res.
 */
export const EmiservBaseApp: React.FC<React.PropsWithChildren> = ({ children }) => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const location = useLocation();
    const { usuari } = useSessioUsuari();
    const { anarAInterficieClassica } = useInterficieClassica();
    const [perfilObert, setPerfilObert] = React.useState(false);

    const menuEntries: MenuEntry[] = [
        {
            id: 'serveis',
            title: t('app.menu.serveis'),
            to: '/serveis',
            icon: emiservIcons.servei,
        },
        {
            id: 'auditoria',
            title: t('app.menu.auditoria'),
            icon: emiservIcons.auditoria,
            children: [
                {
                    id: 'auditoriaBackoffice',
                    title: t('app.menu.properament', { titol: t('app.menu.backoffice') }),
                    icon: emiservIcons.auditoriaBackoffice,
                    onClick: () => {},
                },
                {
                    id: 'auditoriaEnrutador',
                    title: t('app.menu.properament', { titol: t('app.menu.enrutador') }),
                    icon: emiservIcons.auditoriaEnrutador,
                    onClick: () => {},
                },
            ],
        },
        {
            id: 'interficieClassica',
            title: t('app.menu.interficieClassica'),
            icon: emiservIcons.interficieClassica,
            onClick: anarAInterficieClassica,
        },
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
            headerAuthBadgeIcon={usuari?.rols.includes('EMS_ADMIN') ? 'settings' : undefined}
            headerAdditionalAuthComponents={[
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
