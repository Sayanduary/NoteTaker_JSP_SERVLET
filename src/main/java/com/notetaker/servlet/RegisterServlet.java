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
import java.util.regex.Pattern;

/**
 * Register Servlet - Handle user registration
 * Created by: Sayanduary
 * Date: 2025-07-25 16:46:19 UTC
 * Purpose: Register new users and manage registration process
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
    private UserDAO userDAO;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );

    // Username validation pattern (alphanumeric and underscore, 3-20 chars)
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();

        String currentTime = getCurrentUTCTime();
        logger.info("👤 RegisterServlet initialized successfully");
        logger.info("📅 Initialization Time: {}", currentTime);
        logger.info("👨‍💻 Developer: Sayanduary");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String currentTime = getCurrentUTCTime();
        logger.info("📝 Register GET request at: {}", currentTime);

        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            logger.info("👤 User {} already logged in, redirecting to dashboard", user.getUsername());
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        // Forward to register page
        logger.info("📄 Forwarding to register page");
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String currentTime = getCurrentUTCTime();
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        logger.info("📝 Register POST request at: {} for username: {}", currentTime, username);

        try {
            // Validate input parameters
            ValidationResult validation = validateRegistrationInput(username, email, password, confirmPassword);
            if (!validation.isValid()) {
                logger.warn("⚠️ Registration validation failed: {}", validation.getErrorMessage());
                handleRegistrationError(request, response, validation.getErrorMessage(), username, email);
                return;
            }

            // Trim inputs
            username = username.trim();
            email = email.trim().toLowerCase();

            // Check if username already exists
            if (userDAO.getUserByUsername(username) != null) {
                logger.warn("⚠️ Registration failed - username '{}' already exists", username);
                handleRegistrationError(request, response, "Username already exists. Please choose a different username.", username, email);
                return;
            }

            // Check if email already exists
            if (userDAO.getUserByEmail(email) != null) {
                logger.warn("⚠️ Registration failed - email '{}' already exists", email);
                handleRegistrationError(request, response, "Email already registered. Please use a different email or try logging in.", username, email);
                return;
            }

            // Hash password and create user
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
            User user = new User(username, email, hashedPassword);

            // Save user
            if (userDAO.saveUser(user)) {
                logger.info("✅ User registered successfully - Username: {}, Email: {}", username, email);
                request.setAttribute("success", "Registration successful! Please login with your credentials.");
                request.setAttribute("registeredUsername", username);
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            } else {
                logger.error("❌ Failed to save user during registration - Username: {}", username);
                handleRegistrationError(request, response, "Registration failed due to a server error. Please try again.", username, email);
            }

        } catch (Exception e) {
            logger.error("❌ Error during registration process for username: {}", username, e);
            handleRegistrationError(request, response, "An unexpected error occurred during registration. Please try again.", username, email);
        }
    }

    /**
     * Validate registration input
     */
    private ValidationResult validateRegistrationInput(String username, String email, String password, String confirmPassword) {
        // Check for null or empty fields
        if (username == null || email == null || password == null || confirmPassword == null ||
                username.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            return new ValidationResult(false, "All fields (username, email, password) are required.");
        }

        // Validate username format
        if (!USERNAME_PATTERN.matcher(username.trim()).matches()) {
            return new ValidationResult(false, "Username must be 3-20 characters long and contain only letters, numbers, and underscores.");
        }

        // Validate email format
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return new ValidationResult(false, "Please enter a valid email address.");
        }

        // Validate password length
        if (password.length() < 6) {
            return new ValidationResult(false, "Password must be at least 6 characters long.");
        }

        // Check password confirmation
        if (!password.equals(confirmPassword)) {
            return new ValidationResult(false, "Passwords do not match. Please re-enter your password.");
        }

        return new ValidationResult(true, null);
    }

    /**
     * Handle registration errors
     */
    private void handleRegistrationError(HttpServletRequest request, HttpServletResponse response,
                                         String error, String username, String email)
            throws ServletException, IOException {

        logger.warn("⚠️ Registration error: {}", error);
        request.setAttribute("error", error);
        request.setAttribute("username", username);
        request.setAttribute("email", email);
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    /**
     * Get current UTC time formatted
     */
    private String getCurrentUTCTime() {
        return LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Validation result helper class
     */
    private static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
    }

    @Override
    public void destroy() {
        String currentTime = getCurrentUTCTime();
        logger.info("🛑 RegisterServlet destroyed");
        logger.info("📅 Destroy Time: {}", currentTime);
        logger.info("👨‍💻 Developer: Sayanduary");
        super.destroy();
    }
}