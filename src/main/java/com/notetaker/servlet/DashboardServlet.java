package com.notetaker.servlet;

import com.notetaker.dao.NoteDAO;
import com.notetaker.dao.UserDAO;
import com.notetaker.model.Note;
import com.notetaker.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dashboard Servlet - Handle note management and dashboard view
 * Created by: Sayanduary
 * Date: 2025-07-25 16:21:41 UTC
 * Purpose: Manage user dashboard and notes operations
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard", "/dashboard/*"})
public class DashboardServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DashboardServlet.class);

    private UserDAO userDAO;
    private NoteDAO noteDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        noteDAO = new NoteDAO();

        String currentTime = getCurrentUTCTime();
        logger.info("🎯 DashboardServlet initialized successfully");
        logger.info("📅 Initialization Time: {}", currentTime);
        logger.info("👨‍💻 Developer: Sayanduary");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String currentTime = getCurrentUTCTime();
        logger.info("📊 Dashboard GET request at: {}", currentTime);

        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            logger.warn("⚠️ Unauthorized dashboard access attempt");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            User user = (User) session.getAttribute("user");
            logger.info("✅ Dashboard access by user: {}", user.getUsername());

            // Get user's notes
            List<Note> notes = noteDAO.getNotesByUser(user);
            request.setAttribute("notes", notes);
            request.setAttribute("user", user);
            request.setAttribute("noteCount", notes.size());

            logger.info("📝 Loaded {} notes for user: {}", notes.size(), user.getUsername());

            // Forward to dashboard page
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("❌ Error loading dashboard", e);
            request.setAttribute("error", "Error loading dashboard. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String currentTime = getCurrentUTCTime();
        String action = request.getParameter("action");

        logger.info("📊 Dashboard POST request - Action: {} at: {}", action, currentTime);

        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            logger.warn("⚠️ Unauthorized dashboard POST attempt");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            switch (action != null ? action : "") {
                case "create":
                    handleCreateNote(request, response, user);
                    break;
                case "update":
                    handleUpdateNote(request, response, user);
                    break;
                case "delete":
                    handleDeleteNote(request, response, user);
                    break;
                default:
                    logger.warn("⚠️ Unknown action: {}", action);
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                    break;
            }
        } catch (Exception e) {
            logger.error("❌ Error processing dashboard action: {}", action, e);
            request.setAttribute("error", "Error processing request. Please try again.");
            doGet(request, response);
        }
    }

    private void handleCreateNote(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {

        String title = request.getParameter("title");
        String content = request.getParameter("content");

        logger.info("📝 Creating new note - Title: '{}' for user: {}", title, user.getUsername());

        if (title == null || title.trim().isEmpty()) {
            logger.warn("⚠️ Note creation failed - empty title");
            response.sendRedirect(request.getContextPath() + "/dashboard?error=Title cannot be empty");
            return;
        }

        if (content == null || content.trim().isEmpty()) {
            logger.warn("⚠️ Note creation failed - empty content");
            response.sendRedirect(request.getContextPath() + "/dashboard?error=Content cannot be empty");
            return;
        }

        try {
            Note note = new Note();
            note.setTitle(title.trim());
            note.setContent(content.trim());
            note.setUser(user);

            noteDAO.saveNote(note);

            logger.info("✅ Note created successfully - ID: {} for user: {}", note.getId(), user.getUsername());
            response.sendRedirect(request.getContextPath() + "/dashboard?success=Note created successfully");

        } catch (Exception e) {
            logger.error("❌ Error creating note for user: {}", user.getUsername(), e);
            response.sendRedirect(request.getContextPath() + "/dashboard?error=Failed to create note");
        }
    }

    private void handleUpdateNote(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {

        String noteIdStr = request.getParameter("noteId");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        logger.info("✏️ Updating note - ID: {} for user: {}", noteIdStr, user.getUsername());

        if (noteIdStr == null || title == null || content == null) {
            logger.warn("⚠️ Note update failed - missing parameters");
            response.sendRedirect(request.getContextPath() + "/dashboard?error=Missing required fields");
            return;
        }

        try {
            Long noteId = Long.parseLong(noteIdStr);
            Note note = noteDAO.getNoteById(noteId);

            if (note == null || !note.getUser().getId().equals(user.getId())) {
                logger.warn("⚠️ Note update failed - note not found or unauthorized");
                response.sendRedirect(request.getContextPath() + "/dashboard?error=Note not found");
                return;
            }

            note.setTitle(title.trim());
            note.setContent(content.trim());

            noteDAO.updateNote(note);

            logger.info("✅ Note updated successfully - ID: {} for user: {}", noteId, user.getUsername());
            response.sendRedirect(request.getContextPath() + "/dashboard?success=Note updated successfully");

        } catch (NumberFormatException e) {
            logger.warn("⚠️ Invalid note ID: {}", noteIdStr);
            response.sendRedirect(request.getContextPath() + "/dashboard?error=Invalid note ID");
        } catch (Exception e) {
            logger.error("❌ Error updating note for user: {}", user.getUsername(), e);
            response.sendRedirect(request.getContextPath() + "/dashboard?error=Failed to update note");
        }
    }

    private void handleDeleteNote(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {

        String noteIdStr = request.getParameter("noteId");

        logger.info("🗑️ Deleting note - ID: {} for user: {}", noteIdStr, user.getUsername());

        if (noteIdStr == null) {
            logger.warn("⚠️ Note deletion failed - missing note ID");
            response.sendRedirect(request.getContextPath() + "/dashboard?error=Note ID required");
            return;
        }

        try {
            Long noteId = Long.parseLong(noteIdStr);
            Note note = noteDAO.getNoteById(noteId);

            if (note == null || !note.getUser().getId().equals(user.getId())) {
                logger.warn("⚠️ Note deletion failed - note not found or unauthorized");
                response.sendRedirect(request.getContextPath() + "/dashboard?error=Note not found");
                return;
            }

            noteDAO.deleteNote(noteId);

            logger.info("✅ Note deleted successfully - ID: {} for user: {}", noteId, user.getUsername());
            response.sendRedirect(request.getContextPath() + "/dashboard?success=Note deleted successfully");

        } catch (NumberFormatException e) {
            logger.warn("⚠️ Invalid note ID: {}", noteIdStr);
            response.sendRedirect(request.getContextPath() + "/dashboard?error=Invalid note ID");
        } catch (Exception e) {
            logger.error("❌ Error deleting note for user: {}", user.getUsername(), e);
            response.sendRedirect(request.getContextPath() + "/dashboard?error=Failed to delete note");
        }
    }

    private String getCurrentUTCTime() {
        return LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public void destroy() {
        String currentTime = getCurrentUTCTime();
        logger.info("🛑 DashboardServlet destroyed");
        logger.info("📅 Destroy Time: {}", currentTime);
        logger.info("👨‍💻 Developer: Sayanduary");
        super.destroy();
    }
}