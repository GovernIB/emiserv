import { useMuiBaseAppAppearance } from '../components/mui/MuiBaseAppContext';
import { useSmallHeader } from './useSmallScreen';

export const DEFAULT_HEADER_HEIGHT = 64;
export const DEFAULT_SMALL_HEADER_HEIGHT = 56;

/**
 * Alçada de la capçalera: el valor de appearance.headerHeight si s'ha indicat,
 * o 56 (pantalla petita) / 64 en cas contrari.
 */
export const useHeaderHeight = (): number => {
    const { headerHeight } = useMuiBaseAppAppearance();
    const smallHeader = useSmallHeader();
    return headerHeight ?? (smallHeader ? DEFAULT_SMALL_HEADER_HEIGHT : DEFAULT_HEADER_HEIGHT);
};

export default useHeaderHeight;
