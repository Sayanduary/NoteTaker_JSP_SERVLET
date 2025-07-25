package com.notetaker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * JVM Shutdown Hook for emergency cleanup
 * Created by: Sayanduary
 * Date: 2025-07-25 15:06:34 UTC
 */
public class ShutdownHook extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    static {
        // Register shutdown hook
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }

    @Override
    public void run() {
        String currentTime = getCurrentUTCTime();

        logger.info("🚨 JVM Shutdown Hook Triggered");
        logger.info("📅 Hook Time: {}", currentTime);
        logger.info("👨‍💻 Developer: Sayanduary");

        try {
            if (HibernateUtil.isSessionFactoryActive()) {
                logger.info("🛑 Emergency Hibernate shutdown...");
                HibernateUtil.shutdown();
            }
        } catch (Exception e) {
            logger.error("❌ Error in shutdown hook", e);
        }
    }

    public static void initialize() {
        // This method is called to ensure the static block runs
        logger.info("🔧 Shutdown hook initialized");
        logger.info("👨‍💻 Developer: Sayanduary");
        logger.info("📅 Hook Registration Time: {}", getCurrentUTCTime());
    }

    private static String getCurrentUTCTime() {
        return LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}