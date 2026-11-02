import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import MenuItem from '@mui/material/MenuItem';
import Paper from '@mui/material/Paper';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useBaseAppContext, useResourceApiContext } from 'reactlib';
import { icons, MAIN_GAP, ScreenFrame } from '../../lot2';
import { descarregarFitxer } from '../../util/descarregarFitxer';

type Informe = 'generalEstat' | 'emisorEnrutat';

/** Valors del tipus de petició de l'informe d'estat general (TOTS: sense filtre), com la JSP. */
const TIPUS_PETICIO = ['TOTS', 'BACKOFFICE', 'ENRUTADOR'] as const;

/** Data d'avui i del primer dia del mes (aaaa-mm-dd), per defecte del formulari. */
const dataLocal = (data: Date) =>
    `${data.getFullYear()}-${String(data.getMonth() + 1).padStart(2, '0')}-${String(data.getDate()).padStart(2, '0')}`;

/** Nom del fitxer de la capçalera Content-Disposition. */
const nomFitxer = (capcalera: string | null, perDefecte: string) =>
    capcalera?.match(/filename="?([^";]+)"?/i)?.[1] ?? perDefecte;

/**
 * Un informe: interval de dates (obligatori), el tipus de petició a l'estat general i el botó de
 * generar, que descarrega el full de càlcul (XLS) que genera el servidor (InformeApiController, les
 * mateixes vistes que la JSP).
 */
const TargetaInforme: React.FC<{ informe: Informe; ambTipus?: boolean }> = ({ informe, ambTipus }) => {
    const { t } = useTranslation();
    const { apiUrl } = useResourceApiContext();
    const { temporalMessageShow } = useBaseAppContext();
    const avui = new Date();
    const [dataInici, setDataInici] = React.useState(dataLocal(new Date(avui.getFullYear(), avui.getMonth(), 1)));
    const [dataFi, setDataFi] = React.useState(dataLocal(avui));
    const [tipus, setTipus] = React.useState<(typeof TIPUS_PETICIO)[number]>('TOTS');
    const [generant, setGenerant] = React.useState(false);
    const datesValides = !!dataInici && !!dataFi && dataInici <= dataFi;
    const generar = () => {
        setGenerant(true);
        fetch(`${apiUrl.replace(/\/+$/, '')}/informes/${informe}`, {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ dataInici, dataFi, tipusPeticio: ambTipus && tipus !== 'TOTS' ? tipus : null }),
        })
            .then(async (resposta) => {
                if (!resposta.ok) {
                    const cos = await resposta.json().catch(() => ({}));
                    throw new Error(cos?.message ?? `HTTP ${resposta.status}`);
                }
                descarregarFitxer(
                    await resposta.blob(),
                    nomFitxer(resposta.headers.get('Content-Disposition'), `${informe}.xls`)
                );
            })
            .catch((error: Error) => temporalMessageShow(t('informes.errorGenerar'), error.message, 'error'))
            .finally(() => setGenerant(false));
    };
    return (
        <Paper variant="outlined" sx={{ display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
            <Typography
                variant="h6"
                component="h2"
                sx={{ px: 2, py: 1, color: 'primary.contrastText', bgcolor: 'primary.main' }}
            >
                {t(`informes.${informe}.titol`)}
            </Typography>
            <Box sx={{ p: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Typography variant="body2" color="text.secondary">
                    {t(`informes.${informe}.descripcio`)}
                </Typography>
                <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
                    <TextField
                        type="date"
                        size="small"
                        required
                        label={t('informes.dataInici')}
                        value={dataInici}
                        onChange={(e) => setDataInici(e.target.value)}
                        slotProps={{ inputLabel: { shrink: true } }}
                    />
                    <TextField
                        type="date"
                        size="small"
                        required
                        label={t('informes.dataFi')}
                        value={dataFi}
                        onChange={(e) => setDataFi(e.target.value)}
                        error={!!dataInici && !!dataFi && dataInici > dataFi}
                        helperText={
                            !!dataInici && !!dataFi && dataInici > dataFi ? t('informes.ordreDates') : undefined
                        }
                        slotProps={{ inputLabel: { shrink: true } }}
                    />
                    {ambTipus && (
                        <TextField
                            select
                            size="small"
                            label={t('informes.tipusPeticio.label')}
                            value={tipus}
                            onChange={(e) => setTipus(e.target.value as (typeof TIPUS_PETICIO)[number])}
                            sx={{ minWidth: 200 }}
                        >
                            {TIPUS_PETICIO.map((tp) => (
                                <MenuItem key={tp} value={tp}>
                                    {t(`informes.tipusPeticio.${tp}`)}
                                </MenuItem>
                            ))}
                        </TextField>
                    )}
                </Box>
                <Box>
                    <Button
                        variant="contained"
                        startIcon={<Icon>{icons.descarrega}</Icon>}
                        disabled={!datesValides || generant}
                        onClick={generar}
                    >
                        {t('informes.generar')}
                    </Button>
                </Box>
            </Box>
        </Paper>
    );
};

/** Informes (informeList.jsp): només per a l'administrador, com a la JSP. */
export const Informes: React.FC = () => {
    const { t } = useTranslation();
    return (
        <ScreenFrame icon="summarize" title={t('informes.titol')} padded fitContent>
            <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', lg: '1fr 1fr' }, gap: MAIN_GAP + 'px' }}>
                <TargetaInforme informe="generalEstat" ambTipus />
                <TargetaInforme informe="emisorEnrutat" />
            </Box>
        </ScreenFrame>
    );
};

export default Informes;
