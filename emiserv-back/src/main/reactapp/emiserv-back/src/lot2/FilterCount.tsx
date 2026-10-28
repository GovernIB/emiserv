import { createPortal } from 'react-dom';
import Icon from '@mui/material/Icon';
import Typography from '@mui/material/Typography';
import { icons } from './icons';
import { useLot2Translation } from './i18n';
import { countSpringFilterTerms } from './springFilterCount';
import { useScreenFrame } from './ScreenFrameContext';

type FilterCountProps = {
    /** Spring Filter aplicat pel filtre de la pantalla. */
    springFilter?: string;
    /**
     * Recompte a mostrar en lloc del calculat: un nombre, o una funció que rep el nombre de termes
     * calculat del `springFilter` i retorna el que s'ha de mostrar (p. ex. per afegir filtres que no
     * van al springFilter).
     */
    count?: number | ((terms: number) => number);
};

/**
 * Recompte de filtres aplicats d'un llistat, com a text breu: «( [icona de filtre] 2 filtres
 * aplicats )». Es pinta a la barra de títol de la pantalla, al costat del títol (veure ScreenTitle);
 * fora d'un ScreenFrame, o sense cap filtre aplicat, no pinta res.
 */
export const FilterCount: React.FC<FilterCountProps> = (props) => {
    const { springFilter, count: countProp } = props;
    const { t } = useLot2Translation();
    const screenFrame = useScreenFrame();
    const terms = countSpringFilterTerms(springFilter);
    const count = typeof countProp === 'function' ? countProp(terms) : (countProp ?? terms);
    if (!count || screenFrame?.infoSlot == null) {
        return null;
    }
    return createPortal(
        <Typography
            variant="body2"
            color="text.secondary"
            noWrap
            sx={{ display: 'inline-flex', alignItems: 'center', gap: 0.5 }}
        >
            {'('}
            <Icon aria-hidden sx={{ fontSize: '1.1rem' }}>
                {icons.filtre}
            </Icon>
            {`${t('filtresAplicats', { count })} )`}
        </Typography>,
        screenFrame.infoSlot
    );
};

export default FilterCount;
