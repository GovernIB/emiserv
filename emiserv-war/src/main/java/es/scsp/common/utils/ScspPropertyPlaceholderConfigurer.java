package es.scsp.common.utils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import es.caib.emiserv.war.helper.PropertiesHelper;
import es.scsp.common.domain.core.ParametroConfiguracion;

public class ScspPropertyPlaceholderConfigurer {

	@Autowired
	@Qualifier("sessionFactory")
	public SessionFactory sessionFactoryManager;

	public String getProperty(String property) {
		String valor = PropertiesHelper.getProperties().getProperty(
				"es.caib.emiserv.scsp." + property);
		if (valor != null) {
			return valor;
		} else {
			Session session = this.sessionFactoryManager.openSession();
			Transaction t = session.beginTransaction();
			Criteria criteria = session.createCriteria(ParametroConfiguracion.class);
			criteria.add(Restrictions.like("nombre", property));
			ParametroConfiguracion result = (ParametroConfiguracion)criteria.uniqueResult();
			t.commit();
			session.close();
			if (result != null) {
				return result.getValor().trim();
			} else {
				return null;
			}
		}
	}

}
