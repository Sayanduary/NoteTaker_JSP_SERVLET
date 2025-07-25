package com.notetaker.servlet;

import com.notetaker.dao.NoteDAO;
import com.notetaker.model.Note;
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
 * Note Servlet - Handle individual note operations
 * Created by: Sayanduary
 * Date: 2025-07-25 16:30:10 UTC
 * Purpose: Manage CRUD operations for individual notes
 */
@WebServlet(name = "NoteServlet", urlPatterns = {"/note", "/note/*"})
public class NoteServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(NoteServlet.class);
    private NoteDAO noteDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        noteDAO = new NoteDAO();

        String currentTime = getCurrentUTCTime();
        logger.info("📝 NoteServlet initialized successfully");
        logger.info("📅 Initialization Time: {}", currentTime);
        logger.info("👨‍💻 Developer: Sayanduary");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String currentTime = getCurrentUTCTime();
        logger.info("📝 Note GET request at: {}", currentTime);

        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            logger.warn("⚠️ Unauthorized note access attempt");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");
        String noteId = request.getParameter("id");

        logger.info("📝 Note GET - Action: {}, Note ID: {}, User: {}", action, noteId, user.getUsername());

        try {
            if ("edit".equals(action) && noteId != null) {
                handleEditNoteView(request, response, user, noteId);
            } else if ("view".equals(action) && noteId != null) {
                handleViewNote(request, response, user, noteId);
            } else if ("delete".equals(action) && noteId != null) {
                handleDeleteNote(request, response, user, noteId);
            } else {
                handleAddNoteView(request, response, user);
            }
        } catch (Exception e) {
            logger.error("❌ Error in Note GET request", e);
            request.setAttribute("error", "An error occurred. Please try again.");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String currentTime = getCurrentUTCTime();
        logger.info("📝 Note POST request at: {}", currentTime);

        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            logger.warn("⚠️ Unauthorized note POST attempt");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String noteId = request.getParameter("noteId");

        logger.info("📝 Note POST - Note ID: {}, User: {}, Title: '{}'", noteId, user.getUsername(), title);

        try {
            // Validate input
            if (title == null || title.trim().isEmpty()) {
                logger.warn("⚠️ Note save failed - empty title for user: {}", user.getUsername());
                handleSaveError(request, response, "Title is required", noteId, title, content);
                return;
            }

            if (content == null || content.trim().isEmpty()) {
                logger.warn("⚠️ Note save failed - empty content for user: {}", user.getUsername());
                handleSaveError(request, response, "Content is required", noteId, title, content);
                return;
            }

            // Trim inputs
            title = title.trim();
            content = content.trim();

            Note note;
            boolean isUpdate = noteId != null && !noteId.isEmpty();

            if (isUpdate) {
                // Update existing note
                note = handleUpdateNote(user, noteId, title, content);
                if (note == null) {
                    logger.warn("⚠️ Note update failed - note not found or unauthorized");
                    response.sendRedirect(request.getContextPath() + "/dashboard?error=Note not found");
                    return;
                }
            } else {
                // Create new note
                note = new Note(title, content, user);
                logger.info("📝 Creating new note for user: {}", user.getUsername());
            }

            // Save note
            boolean success;
            if (isUpdate) {
                success = noteDAO.updateNote(note);
            } else {
                success = noteDAO.saveNote(note);
            }

            if (success) {
                String action = isUpdate ? "updated" : "created";
                logger.info("✅ Note {} successfully - ID: {} for user: {}", action, note.getId(), user.getUsername());
                response.sendRedirect(request.getContextPath() + "/dashboard?success=Note " + action + " successfully");
            } else {
                logger.error("❌ Failed to save note for user: {}", user.getUsername());
                handleSaveError(request, response, "Failed to save note", noteId, title, content);
            }

        } catch (Exception e) {
            logger.error("❌ Error in Note POST request for user: {}", user.getUsername(), e);
            handleSaveError(request, response, "An error occurred while saving the note", noteId, title, content);
        }
    }

    private void handleEditNoteView(HttpServletRequest request, HttpServletResponse response, User user, String noteId)
            throws ServletException, IOException {

        try {
            Long id = Long.parseLong(noteId);
            Note note = noteDAO.getNoteById(id);

            if (note != null && note.getUser().getId().equals(user.getId())) {
                logger.info("📝 Loading note for edit - ID: {} for user: {}", id, user.getUsername());
                request.setAttribute("note", note);
                request.setAttribute("action", "edit");
                request.getRequestDispatcher("/WEB-INF/views/edit-note.jsp").forward(request, response);
            } else {
                logger.warn("⚠️ Unauthorized edit attempt - Note ID: {} by user: {}", id, user.getUsername());
                response.sendRedirect(request.getContextPath() + "/dashboard?error=Note not found");
            }
        } catch (NumberFormatException e) {
            logger.warn("⚠️ Invalid note ID format: {}", noteId);
            response.sendRedirect(request.getContextPath() + "/dashboard?error=Invalid note ID");
        }
    }

    private void handleViewNote(HttpServletRequest request, HttpServletResponse response, User user, String noteId)
            throws ServletException, IOException {

        try {
            Long id = Long.parseLong(noteId);
            Note note = noteDAO.getNoteById(id);

            if (note != null && note.getUser().getId().equals(user.getId())) {
                logger.info("👁️ Viewing note - ID: {} for user: {}", id, user.getUsername());
                request.setAttribute("note", note);
                request.setAttribute("action", "view");
                request.getRequestDispatcher("/WEB-INF/views/view-note.jsp").forward(request, response);
            } else {
                logger.warn("⚠️ Unauthorized view attempt - Note ID: {} by user: {}", id, user.getUsername());
                response.sendRedirect(request.getContextPath() + "/dashboard?error=Note not found");
            }
        } catch (NumberFormatException e) {
            logger.warn("⚠️ Invalid note ID format: {}", noteId);
            response.sendRedirect(request.getContextPath() + "/dashboard?error=Invalid note ID");
        }
    }

    private void handleDeleteNote(HttpServletRequest request, HttpServletResponse response, User user, String noteId)
            throws IOException {

        try {
            Long id = Long.parseLong(noteId);
            Note note = noteDAO.getNoteById(id);

            if (note != null && note.getUser().getId().equals(user.getId())) {
                boolean deleted = noteDAO.deleteNote(id);
                if (deleted) {
                    logger.info("🗑️ Note deleted successfully - ID: {} for user: {}", id, user.getUsername());
                    response.sendRedirect(request.getContextPath() + "/dashboard?success=Note deleted successfully");
                } else {
                    logger.error("❌ Failed to delete note - ID: {} for user: {}", id, user.getUsername());
                    response.sendRedirect(request.getContextPath() + "/dashboard?error=Failed to delete note");
                }
            } else {
                logger.warn("⚠️ Unauthorized delete attempt - Note ID: {} by user: {}", id, user.getUsername());
                response.sendRedirect(request.getContextPath() + "/dashboard?error=Note not found");
            }
        } catch (NumberFormatException e) {
            logger.warn("⚠️ Invalid note ID format: {}", noteId);
            response.sendRedirect(request.getContextPath() + "/dashboard?error=Invalid note ID");
        }
    }

    private void handleAddNoteView(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        logger.info("➕ Loading add note page for user: {}", user.getUsername());
        request.setAttribute("action", "create");
        request.getRequestDispatcher("/WEB-INF/views/add-note.jsp").forward(request, response);
    }

    private Note handleUpdateNote(User user, String noteId, String title, String content) {
        try {
            Long id = Long.parseLong(noteId);
            Note note = noteDAO.getNoteById(id);

            if (note != null && note.getUser().getId().equals(user.getId())) {
                logger.info("✏️ Updating note - ID: {} for user: {}", id, user.getUsername());
                note.setTitle(title);
                note.setContent(content);
                // Note: Don't call preUpdate() manually - it's handled by JPA annotations
                return note;
            } else {
                logger.warn("⚠️ Note not found or unauthorized - ID: {} for user: {}", id, user.getUsername());
                return null;
            }
        } catch (NumberFormatException e) {
            logger.warn("⚠️ Invalid note ID format: {}", noteId);
            return null;
        }
    }

    private void handleSaveError(HttpServletRequest request, HttpServletResponse response,
                                 String error, String noteId, String title, String content)
            throws ServletException, IOException {

        logger.warn("⚠️ Note save error: {}", error);
        request.setAttribute("error", error);
        request.setAttribute("title", title);
        request.setAttribute("content", content);

        if (noteId != null && !noteId.isEmpty()) {
            try {
                Long id = Long.parseLong(noteId);
                Note note = noteDAO.getNoteById(id);
                request.setAttribute("note", note);
                request.getRequestDispatcher("/WEB-INF/views/edit-note.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                request.getRequestDispatcher("/WEB-INF/views/add-note.jsp").forward(request, response);
            }
        } else {
            request.getRequestDispatcher("/WEB-INF/views/add-note.jsp").forward(request, response);
        }
    }

    private String getCurrentUTCTime() {
        return LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public void destroy() {
        String currentTime = getCurrentUTCTime();
        logger.info("🛑 NoteServlet destroyed");
        logger.info("📅 Destroy Time: {}", currentTime);
        logger.info("👨‍💻 Developer: Sayanduary");
        super.destroy();
    }
}