/**
 * 
 */
package es.caib.emiserv.logic.base;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.EhCacheBasedAclCache;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Configuració de les ACLs de Spring Security.
 * 
 * @author Limit Tecnologies
 */
@Configuration
@EnableAutoConfiguration
public class AclConfig {

	public static final String TABLE_PREFIX = "ems_";
	public static final String TABLE_OBJECT_IDENTITY = TABLE_PREFIX + "acl_object_identity";
	public static final String TABLE_SID = TABLE_PREFIX + "acl_sid";
	public static final String TABLE_CLASS = TABLE_PREFIX + "acl_class";
	public static final String TABLE_ENTRY = TABLE_PREFIX + "acl_entry";
	public static final String SEQUENCE_OBJECT_IDENTITY = TABLE_PREFIX + "acl_object_identity_seq";
	public static final String SEQUENCE_SID = TABLE_PREFIX + "acl_sid_seq";
	public static final String SEQUENCE_CLASS = TABLE_PREFIX + "acl_class_seq";
	public static final String SEQUENCE_ENTRY = TABLE_PREFIX + "acl_entry_seq";
	private static final String SELECT_CLAUSE = "select " + TABLE_OBJECT_IDENTITY +".object_id_identity, "
			+ TABLE_ENTRY + ".ace_order,  "
			+ TABLE_OBJECT_IDENTITY + ".id as acl_id, "
			+ TABLE_OBJECT_IDENTITY + ".parent_object, "
			+ TABLE_OBJECT_IDENTITY + ".entries_inheriting, "
			+ TABLE_ENTRY + ".id as ace_id, "
			+ TABLE_ENTRY + ".mask,  "
			+ TABLE_ENTRY + ".granting,  "
			+ TABLE_ENTRY + ".audit_success, "
			+ TABLE_ENTRY + ".audit_failure,  "
			+ TABLE_SID + ".principal as ace_principal, "
			+ TABLE_SID + ".sid as ace_sid,  "
			+ "acli_sid.principal as acl_principal, "
			+ "acli_sid.sid as acl_sid, "
			+ TABLE_CLASS + ".class "
			+ "from " + TABLE_OBJECT_IDENTITY + " "
			+ "left join " + TABLE_SID + " acli_sid on acli_sid.id = " + TABLE_OBJECT_IDENTITY + ".owner_sid "
			+ "left join " + TABLE_CLASS + " on " + TABLE_CLASS + ".id = " + TABLE_OBJECT_IDENTITY + ".object_id_class   "
			+ "left join " + TABLE_ENTRY + " on " + TABLE_OBJECT_IDENTITY + ".id = " + TABLE_ENTRY + ".acl_object_identity "
			+ "left join " + TABLE_SID + " on " + TABLE_ENTRY + ".sid = " + TABLE_SID + ".id  "
			+ "where ( ";
	private static final String LOOKUP_KEYS_WHERE_CLAUSE = "(" + TABLE_OBJECT_IDENTITY + ".id = ?)";
	private static final String LOOKUP_IDENTITIES_WHERE_CLAUSE = "(" + TABLE_OBJECT_IDENTITY + ".object_id_identity = ? and " + TABLE_CLASS + ".class = ?)";
	private static final String ORDER_BY_CLAUSE = ") order by " + TABLE_OBJECT_IDENTITY + ".object_id_identity"
			+ " asc, " + TABLE_ENTRY + ".ace_order asc";

	private static final String SELECT_ACL_WITH_PARENT_SQL = "select obj.object_id_identity as obj_id, class.class as class "
			+ "from " + TABLE_OBJECT_IDENTITY + " obj, " + TABLE_OBJECT_IDENTITY + " parent, " + TABLE_CLASS + " class "
			+ "where obj.parent_object = parent.id and obj.object_id_class = class.id "
			+ "and parent.object_id_identity = ? and parent.object_id_class = ("
			+ "select id from " + TABLE_CLASS + " where " + TABLE_CLASS + ".class = ?)";

	private static final String CLASS_IDENTITY_QUERY_ORACLE = "SELECT ACL_CLASS_SQ.CURRVAL FROM DUAL";
	private static final String SID_IDENTITY_QUERY_ORACLE = "SELECT ACL_SID_SQ.CURRVAL FROM DUAL";
	private static final String CLASS_IDENTITY_QUERY_POSTGRES = "select currval(pg_get_serial_sequence('acl_class', 'id'))";
	private static final String SID_IDENTITY_QUERY_POSTGRES = "select currval(pg_get_serial_sequence('acl_sid', 'id'))";
	private static final String CLASS_IDENTITY_QUERY_HYPERSONIC = "call identity()";
	private static final String SID_IDENTITY_QUERY_HYPERSONIC = "call identity()";

	private static final String DELETE_ENTRY_BY_OBJECTIDENTITYFK = "delete from " + TABLE_ENTRY + " where acl_object_identity=?";
	private static final String DELETE_ENTRY_BY_PK = "delete from " + TABLE_OBJECT_IDENTITY + " where id=?";
	private static final String INSERT_CLASS = "insert into " + TABLE_CLASS + " (class) values (?)";
	private static final String INSERT_ENTRY = "insert into " + TABLE_ENTRY + " " +
			"(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) " +
			"values (?, ?, ?, ?, ?, ?, ?)";
	private static final String INSERT_OBJECT_IDENTITY = "insert into " + TABLE_OBJECT_IDENTITY + " " +
			"(object_id_class, object_id_identity, owner_sid, entries_inheriting) " +
			"values (?, ?, ?, ?)";
	private static final String INSERT_SID = "insert into " + TABLE_SID + " (principal, sid) values (?, ?)";
	private static final String SELECT_CLASS_PK = "select id from " + TABLE_CLASS + " where class=?";
	private static final String SELECT_OBJECT_IDENTITY_PK = "select " + TABLE_OBJECT_IDENTITY + ".id " +
			"from " + TABLE_OBJECT_IDENTITY + ", " + TABLE_CLASS + " " +
			"where " + TABLE_OBJECT_IDENTITY + ".object_id_class = " + TABLE_CLASS + ".id " +
			"and " + TABLE_CLASS + ".class=? " +
			"and " + TABLE_OBJECT_IDENTITY + ".object_id_identity = ?";
	private static final String SELECT_SID_PK = "select id from " + TABLE_SID + " where principal=? and sid=?";
	private static final String UPDATE_OBJECT_IDENTITY = "update " + TABLE_OBJECT_IDENTITY + " set " +
			"parent_object = ?, owner_sid = ?, entries_inheriting = ?" + " where id = ?";

	@Value("${spring.jpa.properties.hibernate.dialect:#{null}}")
	private String hibernateDialect;

	@Autowired
	private DataSource dataSource;

	@Bean
	public EhCacheBasedAclCache aclCache() {
		return new EhCacheBasedAclCache(
				aclEhCacheFactoryBean().getObject(),
				permissionGrantingStrategy(),
				aclAuthorizationStrategy());
	}

	@Bean
	public EhCacheFactoryBean aclEhCacheFactoryBean() {
		EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
		ehCacheFactoryBean.setCacheManager(aclCacheManager().getObject());
		ehCacheFactoryBean.setCacheName("aclCache");
		return ehCacheFactoryBean;
	}

	@Bean
	public EhCacheManagerFactoryBean aclCacheManager() {
		return new EhCacheManagerFactoryBean();
	}

	@Bean
	public PermissionGrantingStrategy permissionGrantingStrategy() {
		return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
	}

	@Bean
	public AclAuthorizationStrategy aclAuthorizationStrategy() {
		return new AclAuthorizationStrategy() {
			@Override
			public void securityCheck(Acl acl, int changeType) {
				if ((SecurityContextHolder.getContext() == null)
						|| (SecurityContextHolder.getContext().getAuthentication() == null)
						|| !SecurityContextHolder.getContext().getAuthentication()
								.isAuthenticated()) {
					throw new AccessDeniedException(
							"Authenticated principal required to operate with ACLs");
				}
			}
		};
	}

	@Bean
	public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setPermissionEvaluator(permissionEvaluator());
		expressionHandler.setPermissionCacheOptimizer(new AclPermissionCacheOptimizer(aclService()));
		return expressionHandler;
	}

	@Bean
	public LookupStrategy lookupStrategy() {
		BasicLookupStrategy lookupStrategy = new BasicLookupStrategy(
				dataSource,
				aclCache(),
				aclAuthorizationStrategy(),
				new ConsoleAuditLogger());
		lookupStrategy.setPermissionFactory(permissionFactory());
		lookupStrategy.setSelectClause(SELECT_CLAUSE);
		lookupStrategy.setLookupPrimaryKeysWhereClause(LOOKUP_KEYS_WHERE_CLAUSE);
		lookupStrategy.setLookupObjectIdentitiesWhereClause(LOOKUP_IDENTITIES_WHERE_CLAUSE);
		lookupStrategy.setOrderByClause(ORDER_BY_CLAUSE);
		return lookupStrategy;
	}

	@Bean
	public PermissionFactory permissionFactory() {
		return new ExtendedPermissionFactory();
	}

	@Bean
	public PermissionEvaluator permissionEvaluator() {
		return new AclPermissionEvaluator(aclService());
	}

	@Bean
	public JdbcMutableAclService aclService() {
		// S'han hagut de modificar els mètodes retrieveObjectIdentityPrimaryKey i findChildren per a
		// solucionar errors en les consultes quan el tipus de base de dades és PostgreSQL. Si forçam
		// que l'identificador del ObjectIdentity sigui un String dona error al executar la consulta
		// dient que no es pot convertir un bigint al tipus varchar.
		JdbcMutableAclService jdbcMutableAclService = new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache()) {
			protected Long retrieveObjectIdentityPrimaryKey(ObjectIdentity oid) {
				return super.retrieveObjectIdentityPrimaryKey(
						new ObjectIdentityImpl(
								oid.getType(),
								oid.getIdentifier().toString()));
			}
			public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity) {
				return super.findChildren(
						new ObjectIdentityImpl(
								parentIdentity.getType(),
								parentIdentity.getIdentifier().toString()));
			}
		};
		jdbcMutableAclService.setFindChildrenQuery(SELECT_ACL_WITH_PARENT_SQL);
		// Les consultes per a obtenir els ids son diferents per a cada base de dades.
		if (hibernateDialect == null || hibernateDialect.toLowerCase().contains("hsql")) {
			jdbcMutableAclService.setClassIdentityQuery(CLASS_IDENTITY_QUERY_HYPERSONIC);
			jdbcMutableAclService.setSidIdentityQuery(SID_IDENTITY_QUERY_HYPERSONIC);
		} else if (hibernateDialect.toLowerCase().contains("oracle")) {
			jdbcMutableAclService.setClassIdentityQuery(CLASS_IDENTITY_QUERY_ORACLE);
			jdbcMutableAclService.setSidIdentityQuery(SID_IDENTITY_QUERY_ORACLE);
		} else if (hibernateDialect.toLowerCase().contains("postgres")) {
			jdbcMutableAclService.setClassIdentityQuery(CLASS_IDENTITY_QUERY_POSTGRES);
			jdbcMutableAclService.setSidIdentityQuery(SID_IDENTITY_QUERY_POSTGRES);
		}
		jdbcMutableAclService.setDeleteEntryByObjectIdentityForeignKeySql(DELETE_ENTRY_BY_OBJECTIDENTITYFK);
		jdbcMutableAclService.setDeleteObjectIdentityByPrimaryKeySql(DELETE_ENTRY_BY_PK);
		jdbcMutableAclService.setInsertClassSql(INSERT_CLASS);
		jdbcMutableAclService.setInsertEntrySql(INSERT_ENTRY);
		jdbcMutableAclService.setInsertObjectIdentitySql(INSERT_OBJECT_IDENTITY);
		jdbcMutableAclService.setInsertSidSql(INSERT_SID);
		jdbcMutableAclService.setClassPrimaryKeyQuery(SELECT_CLASS_PK);
		jdbcMutableAclService.setObjectIdentityPrimaryKeyQuery(SELECT_OBJECT_IDENTITY_PK);
		jdbcMutableAclService.setSidPrimaryKeyQuery(SELECT_SID_PK);
		jdbcMutableAclService.setUpdateObjectIdentity(UPDATE_OBJECT_IDENTITY);
		return jdbcMutableAclService;
	}

	private class ExtendedPermissionFactory extends DefaultPermissionFactory {
		private ExtendedPermissionFactory() {
			super();
			registerPublicPermissions(BasePermission.class);
		}
	}

}
