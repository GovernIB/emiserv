import { useTranslation } from 'react-i18next';
import { ScreenFrame } from '../../lot2';
import { emiservIcons } from '../../components/EmiservBaseApp';

/** Llista de serveis. De moment només el marc de la pantalla. */
export const ServeiList = () => {
    const { t } = useTranslation();
    return (
        <ScreenFrame icon={emiservIcons.servei} title={t('page.serveis.titol')} padded>
            {null}
        </ScreenFrame>
    );
};

export default ServeiList;
