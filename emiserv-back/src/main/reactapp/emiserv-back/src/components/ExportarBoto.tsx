import React from 'react';
import { useTranslation } from 'react-i18next';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import Tooltip from '@mui/material/Tooltip';
import { useBaseAppContext, useMuiDataGridContext, useResourceApiService } from 'reactlib';
import { icons } from '../lot2';
import { descarregarFitxer } from '../util/descarregarFitxer';
import { missatgeError } from '../util/missatgeError';

/** Codi de l'informe d'exportació dels recursos (ServeiResource, AuditoriaBackofficeResource...). */
const INFORME_EXPORTAR = 'EXPORTAR';

type FormatExportacio = 'CSV' | 'XLSX';

/**
 * Botó d'exportar de la barra de títol d'una graella: CSV o Excel amb el filtre ràpid, el filtre
 * plegable i l'ordenació actuals (els `findArgs` del context de la graella). Fa servir l'informe
 * `EXPORTAR` del recurs amb el paràmetre `fileType`; si se supera el màxim de files, el servidor torna
 * un 422 amb el missatge traduït, que es mostra tal qual.
 */
export const ExportarBoto: React.FC<{ resourceName: string }> = ({ resourceName }) => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const { findArgs } = useMuiDataGridContext();
    const { isReady, artifactReport } = useResourceApiService(resourceName);
    const [anchor, setAnchor] = React.useState<HTMLElement | null>(null);
    const [exportant, setExportant] = React.useState(false);
    const exportar = (fileType: FormatExportacio) => {
        setAnchor(null);
        setExportant(true);
        artifactReport(null, {
            code: INFORME_EXPORTAR,
            fileType,
            data: {
                quickFilter: findArgs?.quickFilter,
                filter: findArgs?.filter,
                sort: findArgs?.sorts,
            },
        })
            .then((resposta) => {
                const { blob, fileName } = resposta as { blob: Blob; fileName?: string };
                descarregarFitxer(blob, fileName);
            })
            .catch((error: unknown) => temporalMessageShow(t('comu.exportar.error'), missatgeError(error), 'error'))
            .finally(() => setExportant(false));
    };
    return (
        <>
            <Tooltip title={t('comu.exportar.boto')}>
                <span>
                    <IconButton
                        aria-label={t('comu.exportar.boto')}
                        aria-haspopup="menu"
                        disabled={!isReady || exportant}
                        onClick={(event) => setAnchor(event.currentTarget)}
                    >
                        <Icon>{icons.descarrega}</Icon>
                    </IconButton>
                </span>
            </Tooltip>
            <Menu anchorEl={anchor} open={anchor != null} onClose={() => setAnchor(null)}>
                <MenuItem onClick={() => exportar('CSV')}>
                    <ListItemIcon>
                        <Icon fontSize="small">description</Icon>
                    </ListItemIcon>
                    <ListItemText>{t('comu.exportar.csv')}</ListItemText>
                </MenuItem>
                <MenuItem onClick={() => exportar('XLSX')}>
                    <ListItemIcon>
                        <Icon fontSize="small">table_chart</Icon>
                    </ListItemIcon>
                    <ListItemText>{t('comu.exportar.xlsx')}</ListItemText>
                </MenuItem>
            </Menu>
        </>
    );
};

export default ExportarBoto;
