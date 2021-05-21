/**
 * 
 */
package es.caib.emiserv.logic.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.emiserv.logic.intf.dto.ObjecteAmbPermisosDto;
import es.caib.emiserv.logic.intf.dto.PermisDto;
import es.caib.emiserv.logic.intf.dto.PrincipalTipusEnumDto;

/**
 * Helper per a la gestió de permisos dins les ACLs.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PermisosHelper {
	
	@Autowired
	private MutableAclService aclService;
	@Autowired
	private LookupStrategy lookupStrategy;
	@Autowired
	private Environment env;

	public <T extends ObjecteAmbPermisosDto> void omplirPermisos(
			List<T> entitats,
			ObjectIdentifierExtractor<T> oie,
			Class<?> clazz,
			boolean ambLlistaPermisos) {
		// Filtra les entitats per saber els permisos per a l'usuari actual
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<T> entitatsRead = new ArrayList<T>();
		entitatsRead.addAll(entitats);
		filterGrantedAll(
				entitatsRead,
				oie,
				clazz,
				new Permission[] {BasePermission.READ},
				auth);
		List<T> entitatsWrite = new ArrayList<T>();
		entitatsWrite.addAll(entitats);
		filterGrantedAll(
				entitatsWrite,
				oie,
				clazz,
				new Permission[] {BasePermission.WRITE},
				auth);
		List<T> entitatsCreate = new ArrayList<T>();
		entitatsCreate.addAll(entitats);
		filterGrantedAll(
				entitatsCreate,
				oie,
				clazz,
				new Permission[] {BasePermission.CREATE},
				auth);
		List<T> entitatsDelete = new ArrayList<T>();
		entitatsDelete.addAll(entitats);
		filterGrantedAll(
				entitatsDelete,
				oie,
				clazz,
				new Permission[] {BasePermission.DELETE},
				auth);
		List<T> entitatsAdministration = new ArrayList<T>();
		entitatsAdministration.addAll(entitats);
		filterGrantedAll(
				entitatsAdministration,
				oie,
				clazz,
				new Permission[] {BasePermission.ADMINISTRATION},
				auth);
		for (T entitat: entitats) {
			entitat.setUsuariActualRead(
					entitatsRead.contains(entitat));
			entitat.setUsuariActualWrite(
					entitatsWrite.contains(entitat));
			entitat.setUsuariActualCreate(
					entitatsCreate.contains(entitat));
			entitat.setUsuariActualDelete(
					entitatsDelete.contains(entitat));
			entitat.setUsuariActualAdministration(
					entitatsAdministration.contains(entitat));
		}
		// Obté els permisos per a totes les entitats només amb una consulta
		if (ambLlistaPermisos) {
			List<Serializable> ids = new ArrayList<Serializable>();
			for (T entitat: entitats) {
				ids.add(oie.getObjectIdentifier(entitat));
			}
			Map<Serializable, List<PermisDto>> permisos = findPermisos(
					ids,
					clazz);
			for (T entitat: entitats)
				entitat.setPermisos(
						permisos.get(
								oie.getObjectIdentifier(entitat)));
		}
	}
	public <T extends ObjecteAmbPermisosDto> void omplirPermisos(
			T entitat,
			ObjectIdentifierExtractor<T> oie,
			Class<?> clazz,
			boolean ambLlistaPermisos) {
		if (entitat != null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			entitat.setUsuariActualRead(
					isGrantedAll(
							oie.getObjectIdentifier(entitat),
							clazz,
							new Permission[] {BasePermission.READ},
							auth));
			entitat.setUsuariActualWrite(
					isGrantedAll(
							oie.getObjectIdentifier(entitat),
							clazz,
							new Permission[] {BasePermission.WRITE},
							auth));
			entitat.setUsuariActualCreate(
					isGrantedAll(
							oie.getObjectIdentifier(entitat),
							clazz,
							new Permission[] {BasePermission.CREATE},
							auth));
			entitat.setUsuariActualDelete(
					isGrantedAll(
							oie.getObjectIdentifier(entitat),
							clazz,
							new Permission[] {BasePermission.DELETE},
							auth));
			entitat.setUsuariActualAdministration(
					isGrantedAll(
							oie.getObjectIdentifier(entitat),
							clazz,
							new Permission[] {BasePermission.ADMINISTRATION},
							auth));
			if (ambLlistaPermisos) {
				entitat.setPermisos(
						findPermisos(oie.getObjectIdentifier(entitat), clazz));
			}
		}
	}

	public <T> void filterGrantedAll(
			Collection<T> objects,
			ObjectIdentifierExtractor<T> objectIdentifierExtractor,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		Iterator<T> it = objects.iterator();
		while (it.hasNext()) {
			Serializable objectIdentifier = objectIdentifierExtractor.getObjectIdentifier(
					it.next());
			if (!isGrantedAll(
					objectIdentifier,
					clazz,
					permissions,
					auth))
				it.remove();
		}
	}
	public boolean isGrantedAll(
			Serializable objectIdentifier,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		boolean[] granted = verificarPermisos(
				objectIdentifier,
				clazz,
				permissions,
				auth);
		boolean result = true;
		for (int i = 0; i < granted.length; i++) {
			if (!granted[i]) {
				result = false;
				break;
			}
		}
		return result;
	}

	public List<PermisDto> findPermisos(
			Serializable objectIdentifier,
			Class<?> objectClass) {
		Acl acl = null;
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			acl = aclService.readAclById(oid);
		} catch (NotFoundException nfex) {
			return new ArrayList<PermisDto>();
		}
		return findPermisosPerAcl(acl);
	}
	public Map<Serializable, List<PermisDto>> findPermisos(
			List<Serializable> objectIdentifiers,
			Class<?> objectClass) {
		try {
			Map<Serializable, List<PermisDto>> resposta = new HashMap<Serializable, List<PermisDto>>();
			List<ObjectIdentity> oids = new ArrayList<ObjectIdentity>();
			for (Serializable objectIdentifier: objectIdentifiers) {
				ObjectIdentity oid = new ObjectIdentityImpl(
						objectClass,
						objectIdentifier);
				oids.add(oid);
			}
			if (!oids.isEmpty()) {
				Map<ObjectIdentity, Acl> acls = lookupStrategy.readAclsById(oids, null);
				for (ObjectIdentity oid: acls.keySet()) {
					resposta.put(
							(Serializable)oid.getIdentifier(),
							findPermisosPerAcl(acls.get(oid)));
				}
			}
			return resposta;
		} catch (NotFoundException nfex) {
			return new HashMap<Serializable, List<PermisDto>>();
		}
	}
	public void updatePermis(
			Serializable objectIdentifier,
			Class<?> objectClass,
			PermisDto permis) {
		if (PrincipalTipusEnumDto.USUARI.equals(permis.getPrincipalTipus())) {
			assignarPermisos(
					new PrincipalSid(permis.getPrincipalNom()),
					objectClass,
					objectIdentifier,
					getPermissionsFromPermis(permis),
					true);
		} else if (PrincipalTipusEnumDto.ROL.equals(permis.getPrincipalTipus())) {
			assignarPermisos(
					new GrantedAuthoritySid(getMapeigRol(permis.getPrincipalNom())),
					objectClass,
					objectIdentifier,
					getPermissionsFromPermis(permis),
					true);
		}
	}
	public void deletePermis(
			Serializable objectIdentifier,
			Class<?> objectClass,
			Serializable permisId) {
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			Acl acl = aclService.readAclById(oid);
			for (AccessControlEntry ace: acl.getEntries()) {
				if (permisId.equals(ace.getId())) {
					assignarPermisos(
							ace.getSid(),
							objectClass,
							objectIdentifier,
							new Permission[] {},
							true);
				}
			}
		} catch (NotFoundException nfex) {
		}
	}
	public void deleteAcl(
			Serializable objectIdentifier,
			Class<?> objectClass) {
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			aclService.deleteAcl(oid, true);
		} catch (NotFoundException nfex) {
		}
	}

	private void assignarPermisos(
			Sid sid,
			Class<?> objectClass,
			Serializable objectIdentifier,
			Permission[] permissions,
			boolean netejarAbans) {
		ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
		MutableAcl acl = null;
		try {
			acl = (MutableAcl)aclService.readAclById(oid);
		} catch (NotFoundException nfex) {
			acl = aclService.createAcl(oid);
		}
		if (netejarAbans) {
			// Es recorren girats perque cada vegada que s'esborra un ace
			// es reorganitzen els índexos
			for (int i = acl.getEntries().size() - 1; i >= 0; i--) {
				AccessControlEntry ace = acl.getEntries().get(i);
				if (ace.getSid().equals(sid))
					acl.deleteAce(i);
			}
		}
		aclService.updateAcl(acl);
		for (Permission permission: permissions) {
			acl.insertAce(
					acl.getEntries().size(),
					permission,
					sid,
					true);
		}
		aclService.updateAcl(acl);
	}

	private boolean[] verificarPermisos(
			Serializable objectIdentifier,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		List<Sid> sids = new ArrayList<Sid>();
		sids.add(new PrincipalSid(auth.getName()));
		for (GrantedAuthority ga: auth.getAuthorities())
			sids.add(new GrantedAuthoritySid(ga.getAuthority()));
		boolean[] granted = new boolean[permissions.length];
		for (int i = 0; i < permissions.length; i++)
			granted[i] = false;
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(
					clazz,
					objectIdentifier);
			Acl acl = aclService.readAclById(oid);
			List<Permission> ps = new ArrayList<Permission>();
			for (int i = 0; i < permissions.length; i++) {
				try {
					ps.add(permissions[i]);
					granted[i] = acl.isGranted(
							ps,
							sids,
							false);
					ps.clear();
				} catch (NotFoundException ex) {}
			}
		} catch (NotFoundException ex) {}
		return granted;
	}

	private List<PermisDto> findPermisosPerAcl(Acl acl) {
		List<PermisDto> resposta = new ArrayList<PermisDto>();
		if (acl != null) {
			Map<String, PermisDto> permisosUsuari = new HashMap<String, PermisDto>();
			Map<String, PermisDto> permisosRol = new HashMap<String, PermisDto>();
			for (AccessControlEntry ace: acl.getEntries()) {
				PermisDto permis = null;
				if (ace.getSid() instanceof PrincipalSid) {
					String principal = ((PrincipalSid)ace.getSid()).getPrincipal();
					permis = permisosUsuari.get(principal);
					if (permis == null) {
						permis = new PermisDto();
						permis.setId((Serializable)ace.getId());
						permis.setPrincipalNom(principal);
						permis.setPrincipalTipus(PrincipalTipusEnumDto.USUARI);
						permisosUsuari.put(principal, permis);
					}
				} else if (ace.getSid() instanceof GrantedAuthoritySid) {
					String grantedAuthority = ((GrantedAuthoritySid)ace.getSid()).getGrantedAuthority();
					permis = permisosRol.get(grantedAuthority);
					if (permis == null) {
						permis = new PermisDto();
						permis.setId((Serializable)ace.getId());
						permis.setPrincipalNom(grantedAuthority);
						permis.setPrincipalTipus(PrincipalTipusEnumDto.ROL);
						permisosRol.put(grantedAuthority, permis);
					}
				}
				if (permis != null) {
					if (BasePermission.READ.equals(ace.getPermission()))
						permis.setRead(true);
					if (BasePermission.WRITE.equals(ace.getPermission()))
						permis.setWrite(true);
					if (BasePermission.CREATE.equals(ace.getPermission()))
						permis.setCreate(true);
					if (BasePermission.DELETE.equals(ace.getPermission()))
						permis.setDelete(true);
					if (BasePermission.ADMINISTRATION.equals(ace.getPermission()))
						permis.setAdministration(true);
				}
			}
			resposta.addAll(permisosUsuari.values());
			resposta.addAll(permisosRol.values());
		}
		return resposta;
	}

	private Permission[] getPermissionsFromPermis(PermisDto permis) {
		List<Permission> permissions = new ArrayList<Permission>();
		if (permis.isRead())
			permissions.add(BasePermission.READ);
		if (permis.isWrite())
			permissions.add(BasePermission.WRITE);
		if (permis.isCreate())
			permissions.add(BasePermission.CREATE);
		if (permis.isDelete())
			permissions.add(BasePermission.DELETE);
		if (permis.isAdministration())
			permissions.add(BasePermission.ADMINISTRATION);
		return permissions.toArray(new Permission[permissions.size()]);
	}

	private String getMapeigRol(String rol) {
		String propertyMapeig = env.getProperty("es.caib.emiserv.mapeig.rol." + rol);
		if (propertyMapeig != null) {
			return propertyMapeig;
		} else {
			return rol;
		}
	}

	public interface ObjectIdentifierExtractor<T> {
		public Serializable getObjectIdentifier(T object);
	}

}
