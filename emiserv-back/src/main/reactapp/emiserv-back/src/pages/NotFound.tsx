import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import { icons, ScreenFrame } from '../lot2';

/** Ruta inexistent dins la interfície REACT. */
export const NotFound = () => {
    const { t } = useTranslation();
    return (
        <ScreenFrame icon={icons.inici} title={t('page.notFound.titol')} padded fitContent>
            <Typography>{t('page.notFound.missatge')}</Typography>
            <div>
                <Button variant="contained" component={Link} to="/">
                    {t('page.notFound.tornar')}
                </Button>
            </div>
        </ScreenFrame>
    );
};

export default NotFound;
