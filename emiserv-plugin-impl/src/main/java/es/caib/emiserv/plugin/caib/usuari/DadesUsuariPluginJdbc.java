/**
 * 
 */
package es.caib.emiserv.plugin.caib.usuari;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.emiserv.plugin.SistemaExternException;
import es.caib.emiserv.plugin.usuari.DadesUsuari;
import es.caib.emiserv.plugin.usuari.DadesUsuariPlugin;
import es.caib.emiserv.plugin.utils.PropertiesHelper;

/**
 * Implementació del plugin de consulta de dades d'usuaris emprant JDBC.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesUsuariPluginJdbc implements DadesUsuariPlugin {

	@Override
	public DadesUsuari consultarAmbUsuariCodi(
			String usuariCodi) throws SistemaExternException {
		LOGGER.debug("Consulta de les dades de l'usuari (codi=" + usuariCodi + ")");
		return consultaDadesUsuariUnic(
				getJdbcQueryUsuariCodi(),
				"codi",
				usuariCodi,
				true);
	}

	@Override
	public DadesUsuari consultarAmbUsuariNif(
			String usuariNif) throws SistemaExternException {
		LOGGER.debug("Consulta de les dades de l'usuari (nif=" + usuariNif + ")");
		return consultaDadesUsuariUnic(
				getJdbcQueryUsuariNif(),
				"nif",
				usuariNif,
				true);
	}

	@Override
	public List<DadesUsuari> findUsuarisPerGrup(
			String grupCodi) throws SistemaExternException {
		LOGGER.debug("Consulta dels usuaris del grup (grupCodi=" + grupCodi + ")");
		return consultaDadesUsuari(
				getJdbcQueryUsuariGrup(),
				"grup",
				grupCodi,
				false);
	}



	private DadesUsuari consultaDadesUsuariUnic(
			String sqlQuery,
			String paramName,
			String paramValue,
			boolean ambRols) throws SistemaExternException {
		List<DadesUsuari> llista = consultaDadesUsuari(
				sqlQuery,
				paramName,
				paramValue,
				ambRols);
		if (llista.size() > 0)
			return llista.get(0);
		else
			return null;
	}

	private List<DadesUsuari> consultaDadesUsuari(
			String sqlQuery,
			String paramName,
			String paramValue,
			boolean ambRols) throws SistemaExternException {
		List<DadesUsuari> llistaUsuaris = new ArrayList<DadesUsuari>();
		Connection con = null;
		PreparedStatement ps = null;
		try {
			Context initContext = new InitialContext();
			DataSource ds = (DataSource)initContext.lookup(getDatasourceJndiName());
			con = ds.getConnection();
			if (sqlQuery.contains("?")) {
				ps = con.prepareStatement(sqlQuery);
				ps.setString(1, paramValue);
			} else if (sqlQuery.contains(":" + paramName)) {
				ps = con.prepareStatement(
						sqlQuery.replace(":" + paramName, "'" + paramValue + "'"));
			} else {
				ps = con.prepareStatement(sqlQuery);
			}
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				DadesUsuari dadesUsuari = new DadesUsuari();
				dadesUsuari.setCodi(rs.getString(1));
				dadesUsuari.setNom(rs.getString(2));
				dadesUsuari.setNif(rs.getString(3));
				dadesUsuari.setEmail(rs.getString(4));
				llistaUsuaris.add(dadesUsuari);
			}
		} catch (Exception ex) {
			throw new SistemaExternException(ex);
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (Exception ex) {
				LOGGER.error("Error al tancar el PreparedStatement", ex);
			}
			try {
				if (con != null) con.close();
			} catch (Exception ex) {
				LOGGER.error("Error al tancar la connexió", ex);
			}
		}
		if (ambRols) {
			for (DadesUsuari dadesUsuari: llistaUsuaris) {
				dadesUsuari.setRols(
						consultaRolsUsuari(
								"codi",
								dadesUsuari.getCodi()));
			}
		}
		return llistaUsuaris;
	}

	private String[] consultaRolsUsuari(
			String paramName,
			String paramValue) throws SistemaExternException {
		String sqlQuery = getJdbcQueryUsuariRols();
		if (sqlQuery == null)
			return null;
		Connection con = null;
		PreparedStatement ps = null;
		List<String> rols = new ArrayList<String>();
		try {
			Context initContext = new InitialContext();
			DataSource ds = (DataSource)initContext.lookup(getDatasourceJndiName());
			con = ds.getConnection();
			if (sqlQuery.contains("?")) {
				ps = con.prepareStatement(sqlQuery);
				ps.setString(1, paramValue);
			} else if (sqlQuery.contains(":" + paramName)) {
				ps = con.prepareStatement(
						sqlQuery.replace(":" + paramName, "'" + paramValue + "'"));
			} else {
				ps = con.prepareStatement(sqlQuery);
			}
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				rols.add(rs.getString(1));
			}
		} catch (Exception ex) {
			throw new SistemaExternException(ex);
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (Exception ex) {
				LOGGER.error("Error al tancar el PreparedStatement", ex);
			}
			try {
				if (con != null) con.close();
			} catch (Exception ex) {
				LOGGER.error("Error al tancar la connexió", ex);
			}
		}
		return rols.toArray(new String[rols.size()]);
	}

	private String getDatasourceJndiName() {
		return PropertiesHelper.getProperties().getProperty("es.caib.emiserv.plugin.dades.usuari.jdbc.datasource.jndi.name");
	}
	private String getJdbcQueryUsuariCodi() {
		return PropertiesHelper.getProperties().getProperty("es.caib.emiserv.plugin.dades.usuari.jdbc.query");
	}
	private String getJdbcQueryUsuariNif() {
		return PropertiesHelper.getProperties().getProperty("es.caib.emiserv.plugin.dades.usuari.jdbc.query.nif");
	}
	private String getJdbcQueryUsuariRols() {
		return PropertiesHelper.getProperties().getProperty("es.caib.emiserv.plugin.dades.usuari.jdbc.query.rols");
	}
	private String getJdbcQueryUsuariGrup() {
		return PropertiesHelper.getProperties().getProperty("es.caib.emiserv.plugin.dades.usuari.jdbc.query.grup");
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DadesUsuariPluginJdbc.class);

}
