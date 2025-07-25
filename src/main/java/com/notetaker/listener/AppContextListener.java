package com.notetaker.listener;

import com.notetaker.util.HibernateUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Application Context Listener - Hibernate Enabled
 * Created by: Sayanduary
 * Date: 2025-07-25 15:59:22 UTC
 * Status: Production Mode (Hibernate Enabled)
 */
@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String currentTime = getCurrentUTCTime();

        logger.info("🚀 Note Taker Application Starting...");
        logger.info("📅 Startup Time: {}", currentTime);
        logger.info("👨‍💻 Developer: Sayanduary");
        logger.info("🌍 Timezone: UTC");
        logger.info("🎯 Context Path: {}", sce.getServletContext().getContextPath());
        logger.info("✅ Running in PRODUCTION MODE (Hibernate enabled)");

        try {
            // Initialize Hibernate
            HibernateUtil.getSessionFactory();
            logger.info("✅ Hibernate initialized successfully");
            logger.info("📊 Application Status: READY (With Database)");
            logger.info("🎯 Status: Full application ready for use!");
        } catch (Exception e) {
            logger.error("❌ Failed to initialize Hibernate", e);
            logger.error("💡 Check entity classes and database connection");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        String currentTime = getCurrentUTCTime();

        logger.info("🛑 Note Taker Application Stopping...");
        logger.info("📅 Shutdown Time: {}", currentTime);
        logger.info("👨‍💻 Developer: Sayanduary");

        try {
            HibernateUtil.shutdown();
            logger.info("✅ Hibernate shutdown completed");
        } catch (Exception e) {
            logger.error("❌ Error during Hibernate shutdown", e);
        }

        logger.info("✅ Application cleanup completed successfully");
    }

    private String getCurrentUTCTime() {
        return LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}