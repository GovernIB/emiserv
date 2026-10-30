import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import {
    MuiDialog,
    MuiForm,
    type DialogButton,
    useBaseAppContext,
    useConfirmDialogButtons,
    useFormApiRef,
} from 'reactlib';
import { useEsAdmin } from '../../components/EmiservAuthProvider';
import ServeiAutoritzacions from './ServeiAutoritzacions';
import ServeiDadesForm from './ServeiDadesForm';
import ServeiPermisos from './ServeiPermisos';
import ServeiRutes from './ServeiRutes';
import { esEnrutador, type MotiuDiagnostic, type PestanyaServei, type ServeiTipus } from './serveiModel';

/** Camps del formulari de dades (els que es comparen per saber si hi ha canvis sense desar). */
const CAMPS_EDITABLES = [
    'codi',
    'nom',
    'tipus',
    'descripcio',
    'backofficeClass',
    'backofficeCaibUrl',
    'backofficeCaibAutenticacio',
    'backofficeCaibAsyncTipus',
    'backofficeCaibAsyncTer',
    'resolverClass',
    'urlPerDefecte',
    'responseResolverClass',
    'asyncSincron',
    'asyncInterval',
    'asyncConcurrencia',
] as const;

/**
 * Valors editables normalitzats: buit, null i undefined són el mateix, i els números i els booleans es
 * comparen com a text (un camp numèric pot tornar el valor com a text).
 */
const valorsEditables = (dades: Record<string, unknown> | undefined): string => {
    const valors = CAMPS_EDITABLES.map((camp) => {
        const valor = dades?.[camp];
        return valor == null || valor === '' ? null : String(valor);
    });
    return JSON.stringify(valors);
};

/** Dades del servei que decideixen les pestanyes i el títol. */
type ServeiCarregat = {
    id: number;
    codi: string;
    tipus: ServeiTipus;
    configuratScsp?: boolean;
    diagnostic?: MotiuDiagnostic[];
};

type ServeiDialogProps = {
    /** Id del servei; sense id, alta d'un servei nou. */
    serveiId?: number;
    pestanya: PestanyaServei;
    onPestanyaChange: (pestanya: PestanyaServei) => void;
    /** El servei nou s'ha creat: la finestra continua oberta amb el servei creat. */
    onCreat: (id: number) => void;
    /** Alguna dada del servei ha canviat (s'ha de refrescar el llistat en tancar). */
    onCanvi: () => void;
    onClose: () => void;
};

/**
 * Finestra d'un servei, amb una pestanya per a cada part de la seva configuració:
 * <ul>
 *   <li>Dades: formulari del servei (`serveiForm.jsp`), amb els botons de desar i tancar.</li>
 *   <li>Rutes: només els enrutadors (`serveiRutes.jsp`).</li>
 *   <li>Permisos i autoritzacions: només els administradors; les autoritzacions, només si el servei
 *   està configurat a l'emissor SCSP (com els botons de la JSP).</li>
 * </ul>
 * Les pestanyes de rutes, permisos i autoritzacions desen cada canvi en el moment (cadascuna té la seva
 * finestra d'alta i modificació), per això només apareixen quan el servei ja existeix. El formulari de
 * dades continua muntat en canviar de pestanya, i no es perden els canvis que no s'hagin desat.
 */
export const ServeiDialog: React.FC<ServeiDialogProps> = ({
    serveiId,
    pestanya,
    onPestanyaChange,
    onCreat,
    onCanvi,
    onClose,
}) => {
    const { t } = useTranslation();
    const esAdmin = useEsAdmin();
    const formApiRef = useFormApiRef();
    const [servei, setServei] = React.useState<ServeiCarregat>();
    const [desant, setDesant] = React.useState(false);
    // Valors del formulari de dades tal com s'han carregat o desat. En tancar es comparen amb els
    // actuals: els esdeveniments de canvi del formulari també es produeixen sense que l'usuari hagi
    // modificat res (p. ex. quan els desplegables carreguen les opcions).
    const valorsDesatsRef = React.useRef<string>(undefined);
    const { messageDialogShow } = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const existent = serveiId != null;

    React.useEffect(() => {
        // Un altre servei: les pestanyes es decideixen quan es carregui
        setServei(undefined);
        valorsDesatsRef.current = undefined;
    }, [serveiId]);

    const disponibles: PestanyaServei[] = ['dades'];
    if (existent && servei != null) {
        if (esEnrutador(servei.tipus)) {
            disponibles.push('rutes');
        }
        if (esAdmin) {
            disponibles.push('permisos');
            if (servei.configuratScsp) {
                disponibles.push('autoritzacions');
            }
        }
    }
    // Una pestanya que el servei no té (o que encara no s'ha carregat) mostra les dades
    const activa = disponibles.includes(pestanya) ? pestanya : 'dades';

    const actualitzarServei = (dades: ServeiCarregat | undefined) => {
        if (dades?.id != null) {
            setServei({
                id: dades.id,
                codi: dades.codi,
                tipus: dades.tipus,
                configuratScsp: dades.configuratScsp,
                diagnostic: dades.diagnostic,
            });
        }
    };

    const botons: DialogButton[] =
        activa === 'dades'
            ? [
                  { value: false, text: t('serveis.dialog.tancar'), componentProps: { variant: 'outlined' } },
                  {
                      value: true,
                      text: t('serveis.dialog.desar'),
                      icon: 'save',
                      componentProps: { variant: 'contained', disabled: desant },
                  },
              ]
            : [{ value: false, text: t('serveis.dialog.tancar'), componentProps: { variant: 'contained' } }];

    /** Tanca la finestra; si hi ha canvis sense desar, abans demana confirmació. */
    const tancar = () => {
        const modificat =
            valorsDesatsRef.current != null &&
            valorsEditables(formApiRef.current?.getData()) !== valorsDesatsRef.current;
        if (!modificat) {
            onClose();
            return;
        }
        messageDialogShow(t('serveis.dialog.canvisTitol'), t('serveis.dialog.canvis'), confirmDialogButtons, {
            maxWidth: 'sm',
            fullWidth: true,
        })
            .then((confirmat: unknown) => {
                if (confirmat === true) {
                    onClose();
                }
            })
            // Cancel·lar o tancar el diàleg rebutja la promesa: la finestra continua oberta
            .catch(() => undefined);
    };

    const buttonCallback = (value: unknown) => {
        if (!value) {
            tancar();
            return;
        }
        setDesant(true);
        formApiRef.current
            ?.save()
            .then((dades: ServeiCarregat) => {
                valorsDesatsRef.current = valorsEditables(dades as unknown as Record<string, unknown>);
                onCanvi();
                actualitzarServei(dades);
                if (!existent && dades?.id != null) {
                    onCreat(dades.id);
                }
            })
            // Els errors (validació, codi repetit...) els mostra el formulari
            .catch(() => undefined)
            .finally(() => setDesant(false));
    };

    const titol = !existent ? t('serveis.dialog.titolNou') : t('serveis.dialog.titol', { codi: servei?.codi ?? '' });

    return (
        <MuiDialog
            open
            title={titol}
            buttons={botons}
            buttonCallback={buttonCallback}
            closeCallback={() => tancar()}
            // Alçada fixa: les graelles de les pestanyes ocupen tot l'espai que queda sota les pestanyes
            componentProps={{ maxWidth: 'lg', fullWidth: true, slotProps: { paper: { sx: { height: '85vh' } } } }}
        >
            <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%', minHeight: 0 }}>
                <Tabs
                    value={activa}
                    onChange={(_event, valor: PestanyaServei) => onPestanyaChange(valor)}
                    sx={{ mb: 2, borderBottom: 1, borderColor: 'divider' }}
                >
                    {disponibles.map((p) => (
                        <Tab key={p} value={p} label={t(`serveis.dialog.pestanya.${p}`)} />
                    ))}
                </Tabs>
                <Box sx={{ display: activa === 'dades' ? 'block' : 'none', flex: 1, minHeight: 0, overflowY: 'auto' }}>
                    <MuiForm
                        key={serveiId ?? 'nou'}
                        resourceName="servei"
                        id={serveiId}
                        apiRef={formApiRef}
                        initialData={existent ? undefined : { tipus: 'ENRUTADOR', asyncSincron: false }}
                        onReady={(dades: ServeiCarregat) => {
                            valorsDesatsRef.current = valorsEditables(dades as unknown as Record<string, unknown>);
                            actualitzarServei(dades);
                        }}
                        // La finestra s'obre i es tanca amb la URL: el bloquejador de base-react (confirm del
                        // navegador) aturaria la navegació a /serveis/<id> després de crear el servei. L'avís
                        // de canvis sense desar el fa tancar(), comparant els valors.
                        formBlockerDisabled
                        hiddenToolbar
                    >
                        <ServeiDadesForm existent={existent} motius={servei?.diagnostic} />
                    </MuiForm>
                </Box>
                {servei != null && activa !== 'dades' && (
                    <Box sx={{ flex: 1, minHeight: 0, display: 'flex', flexDirection: 'column' }}>
                        {activa === 'rutes' && <ServeiRutes serveiId={servei.id} />}
                        {activa === 'permisos' && <ServeiPermisos serveiId={servei.id} />}
                        {activa === 'autoritzacions' && <ServeiAutoritzacions serveiId={servei.id} />}
                    </Box>
                )}
            </Box>
        </MuiDialog>
    );
};

export default ServeiDialog;
