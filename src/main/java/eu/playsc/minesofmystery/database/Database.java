package eu.playsc.minesofmystery.database;

import eu.playsc.minesofmystery.MinesOfMystery;
import eu.playsc.minesofmystery.common.Common;
import jakarta.persistence.Entity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;

public class Database {
	private static final SessionFactory sessionFactory;
	private static StandardServiceRegistry registry;

	static {
		final Configuration config = new Configuration();
		config.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/" + MinesOfMystery.DATABASE_NAME);
		config.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
		config.setProperty("hibernate.connection.username", "root");
		config.setProperty("hibernate.connection.password", "root");
		config.setProperty("hibernate.connection.pool_size", "5");
		config.setProperty("hibernate.order_updates", "true");
		config.setProperty("hibernate.show_sql", "false");
		config.setProperty("hibernate.hbm2ddl.auto", "validate");

		addClasses(config);

		try {
			registry = new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
			sessionFactory = config.buildSessionFactory(registry);
		} catch (final Exception e) {
			Common.error("Failed to create session factory", e);
			if (registry != null) {
				StandardServiceRegistryBuilder.destroy(registry);
			}
			throw new RuntimeException(e);
		}
	}

	public static Session getSession() {
		return sessionFactory.openSession();
	}

	private static void addClasses(final Configuration config) {
		final Reflections reflections = new Reflections("eu.playsc");
		reflections.getTypesAnnotatedWith(Entity.class).forEach(config::addAnnotatedClass);
	}
}
