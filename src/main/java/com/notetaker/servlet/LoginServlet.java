package com.notetaker.servlet;

import com.notetaker.dao.UserDAO;
import com.notetaker.model.User;
import org.mindrot.jbcrypt.BCrypt;
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
 * Login Servlet - Handle user authentication
 * Created by: Sayanduary
 * Date: 2025-07-25 16:25:09 UTC
 * Purpose: Authenticate users and manage login sessions
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();

        String currentTime = getCurrentUTCTime();
        logger.info("🔐 LoginServlet initialized successfully");
        logger.info("📅 Initialization Time: {}", currentTime);
        logger.info("👨‍💻 Developer: Sayanduary");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String currentTime = getCurrentUTCTime();
        logger.info("🔐 Login GET request at: {}", currentTime);

        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            logger.info("👤 User {} already logged in, redirecting to dashboard", user.getUsername());
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        // Forward to login page
        logger.info("📄 Forwarding to login page");
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String currentTime = getCurrentUTCTime();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        logger.info("🔐 Login POST request at: {} for username: {}", currentTime, username);

        // Validate input parameters
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            logger.warn("⚠️ Login failed - missing username or password");
            request.setAttribute("error", "Username and password are required");
            request.setAttribute("username", username); // Preserve username
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        // Trim inputs
        username = username.trim();

        try {
            // Get user from database
            User user = userDAO.getUserByUsername(username);

            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                // Successful authentication
                logger.info("✅ Login successful for user: {}", username);

                // Create session
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("loginTime", currentTime);

                // Set session timeout (30 minutes)
                session.setMaxInactiveInterval(30 * 60);

                // Handle "Remember Me" functionality
                if ("on".equals(rememberMe)) {
                    logger.info("🔄 Remember me enabled for user: {}", username);
                    session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 days
                }

                // Check for redirect parameter
                String redirectUrl = request.getParameter("redirect");
                if (redirectUrl != null && !redirectUrl.isEmpty() && redirectUrl.startsWith("/")) {
                    logger.info("↩️ Redirecting to requested URL: {}", redirectUrl);
                    response.sendRedirect(request.getContextPath() + redirectUrl);
                } else {
                    logger.info("🎯 Redirecting to dashboard");
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                }

            } else {
                // Authentication failed
                logger.warn("❌ Login failed for username: {} - invalid credentials", username);
                request.setAttribute("error", "Invalid username or password");
                request.setAttribute("username", username); // Preserve username
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            logger.error("❌ Error during login process for username: {}", username, e);
            request.setAttribute("error", "An error occurred during login. Please try again.");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
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
        logger.info("🛑 LoginServlet destroyed");
        logger.info("📅 Destroy Time: {}", currentTime);
        logger.info("👨‍💻 Developer: Sayanduary");
        super.destroy();
    }
}