package com.notetaker.servlet;

import com.notetaker.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Home Servlet - Handle root path requests
 * Created by: Sayanduary
 * Date: 2025-07-25 16:57:47 UTC
 * Purpose: Route users to appropriate pages based on authentication status
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"", "/"})
public class HomeServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(HomeServlet.class);

    @Override
    public void init() throws ServletException {
        super.init();
        String currentTime = getCurrentUTCTime();
        logger.info("🏠 HomeServlet initialized successfully");
        logger.info("📅 Initialization Time: {}", currentTime);
        logger.info("👨‍💻 Developer: Sayanduary");
        logger.info("🎯 Routing: Authenticated users → Dashboard, Unauthenticated → Home page");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String currentTime = getCurrentUTCTime();
        String userAgent = request.getHeader("User-Agent");
        String clientIP = getClientIP(request);

        logger.info("🏠 Home GET request at: {}", currentTime);
        logger.info("👤 User: Sayanduary accessing home page");
        logger.info("🌐 Client IP: {}", clientIP);
        logger.info("🔍 Request URI: {}", request.getRequestURI());

        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            logger.info("✅ User {} already logged in, redirecting to dashboard", user.getUsername());
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            logger.info("🏡 No active session, showing home page");
            // Forward to the home.jsp page
            request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String currentTime = getCurrentUTCTime();
        logger.info("🏠 Home POST request at: {} - redirecting to GET", currentTime);

        // Redirect POST requests to GET
        doGet(request, response);
    }

    /**
     * Get client IP address
     */
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }

    /**
     * Get current UTC time formatted
     */
    private String getCurrentUTCTime() {
        return LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public void destroy() {
        String currentTime = getCurrentUTCTime();
        logger.info("🛑 HomeServlet destroyed at: {}", currentTime);
        logger.info("👨‍💻 Developer: Sayanduary");
        super.destroy();
    }
}