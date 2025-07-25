package com.notetaker.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Hibernate Utility Class with proper cleanup
 * Created by: Sayanduary
 * Date: 2025-07-25 15:57:19 UTC
 */
public class HibernateUtil {
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;
    private static volatile boolean isShutdown = false;

    static {
        try {
            String currentTime = getCurrentUTCTime();

            logger.info("🚀 Initializing Hibernate SessionFactory...");
            logger.info("📅 Initialization Time: {}", currentTime);
            logger.info("👨‍💻 Developer: Sayanduary");
            logger.info("🌍 Timezone: UTC");

            // Create a builder for the StandardServiceRegistry
            StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

            // Load settings from hibernate.cfg.xml
            registryBuilder.configure("hibernate.cfg.xml");

            // Build the registry
            StandardServiceRegistry registry = registryBuilder.build();

            // Create a MetadataSources object
            MetadataSources sources = new MetadataSources(registry);

            // Add annotated classes
            sources.addAnnotatedClass(com.notetaker.model.User.class);
            sources.addAnnotatedClass(com.notetaker.model.Note.class);

            // Build the Metadata object
            org.hibernate.boot.Metadata metadata = sources.getMetadataBuilder().build();

            // Build the SessionFactory
            sessionFactory = metadata.getSessionFactoryBuilder().build();

            logger.info("✅ Hibernate SessionFactory initialized successfully");
            logger.info("📊 SessionFactory Status: ACTIVE");

        } catch (Throwable ex) {
            logger.error("❌ Initial SessionFactory creation failed", ex);
            logger.error("🕒 Failure Time: {}", getCurrentUTCTime());
            logger.error("👨‍💻 Developer: Sayanduary");
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (isShutdown) {
            throw new IllegalStateException("❌ SessionFactory has been shutdown");
        }
        if (sessionFactory == null) {
            throw new IllegalStateException("❌ SessionFactory is not initialized");
        }
        return sessionFactory;
    }

    public static synchronized void shutdown() {
        if (isShutdown) {
            logger.info("ℹ️ Hibernate already shutdown, skipping...");
            return;
        }

        String currentTime = getCurrentUTCTime();

        logger.info("🛑 Shutting down Hibernate...");
        logger.info("📅 Shutdown Time: {}", currentTime);
        logger.info("👨‍💻 Developer: Sayanduary");

        try {
            if (sessionFactory != null && !sessionFactory.isClosed()) {
                sessionFactory.close();
                logger.info("✅ SessionFactory closed successfully");
            }

            if (serviceRegistry != null) {
                StandardServiceRegistryBuilder.destroy((StandardServiceRegistry) serviceRegistry);
                logger.info("✅ ServiceRegistry destroyed successfully");
            }

            isShutdown = true;
            logger.info("✅ Hibernate shutdown completed");

        } catch (Exception e) {
            logger.error("❌ Error during Hibernate shutdown", e);
            logger.error("🕒 Error Time: {}", currentTime);
            logger.error("👨‍💻 Developer: Sayanduary");
        }
    }

    public static boolean isSessionFactoryActive() {
        return !isShutdown && sessionFactory != null && !sessionFactory.isClosed();
    }

    public static boolean isShutdown() {
        return isShutdown;
    }

    private static String getCurrentUTCTime() {
        return LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}