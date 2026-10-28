package es.caib.emiserv.logic.base.helper;

/**
 * Tipus de combinació lògica a aplicar quan es comprova una llista de permisos amb
 * {@link BasePermissionHelper}.
 *
 * @author Límit Tecnologies
 */
public enum PermissionMatchType {

	/** N'hi ha prou que l'usuari tingui algun dels permisos indicats. */
	ANY,

	/** L'usuari ha de tenir tots els permisos indicats. */
	ALL;

}
