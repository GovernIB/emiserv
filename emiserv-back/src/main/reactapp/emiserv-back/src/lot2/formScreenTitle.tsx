import type React from 'react';
import ScreenTitle from './ScreenTitle';
import type { ScreenIconName } from './icons';

/**
 * Títol de pantalla (ScreenTitle) per al `title` d'un MuiForm de la llibreria, que l'accepta com a
 * `string | ReactElement`. A Notib es forçava el tipus a `string`; aquí ja no cal.
 */
export const formScreenTitle = (icon: ScreenIconName, title: string): React.ReactElement => (
    <ScreenTitle icon={icon} title={title} />
);
