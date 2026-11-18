package es.caib.emiserv.boot.config;

import liquibase.change.Change;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.change.core.CreateTableChange;
import liquibase.database.Database;
import liquibase.database.core.HsqlDatabase;
import liquibase.database.core.OracleDatabase;
import liquibase.database.core.PostgresDatabase;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LiquibaseChangelogTest {

	@Test
	void oracleAclTypeIsResolved() throws Exception {
		assertAclTypes(new OracleDatabase(), "BIGINT", null);
	}

	@Test
	void postgresqlAclTypeIsResolved() throws Exception {
		assertAclTypes(new PostgresDatabase(), "BIGSERIAL", null);
	}

	@Test
	void hsqlAclTypeIsResolved() throws Exception {
		assertAclTypes(new HsqlDatabase(), "BIGINT", null);
	}

	@Test
	void configuredAclTypeOverridesDatabaseDefault() throws Exception {
		assertAclTypes(new OracleDatabase(), "NUMBER(19,0)", "NUMBER(19,0)");
	}

	private void assertAclTypes(Database database, String expectedType, String configuredType) throws Exception {
		ClassLoaderResourceAccessor resources = new ClassLoaderResourceAccessor();
		String path = "db/changelog/db.changelog-master.yaml";
		ChangeLogParameters parameters = new ChangeLogParameters(database);
		if (configuredType != null) {
			parameters.set("acl_pk_type", configuredType);
		}
		DatabaseChangeLog changelog = ChangeLogParserFactory.getInstance().getParser(path, resources)
				.parse(path, parameters, resources);
		for (String id : List.of("init-10", "init-11", "init-12", "init-13")) {
			ChangeSet acl = changelog.getChangeSets().stream().filter(change -> id.equals(change.getId()))
					.findFirst().orElseThrow();
			for (Change change : acl.getChanges()) {
				CreateTableChange createTable = (CreateTableChange) change;
				assertEquals(expectedType, createTable.getColumns().get(0).getType());
			assertTrue(change.generateStatements(database).length > 0);
			}
		}
	}
}
