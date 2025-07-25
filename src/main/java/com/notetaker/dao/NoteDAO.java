package com.notetaker.dao;

import com.notetaker.model.Note;
import com.notetaker.model.User;
import com.notetaker.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Note Data Access Object
 * Created by: Sayanduary
 * Date: 2025-07-25 16:28:36 UTC
 * Purpose: Handle all database operations for Note entities
 */
public class NoteDAO {
    private static final Logger logger = LoggerFactory.getLogger(NoteDAO.class);

    /**
     * Save a new note to database
     */
    public boolean saveNote(Note note) {
        Session session = null;
        Transaction transaction = null;

        String currentTime = getCurrentUTCTime();
        logger.info("💾 Saving note - Title: '{}' at: {}", note.getTitle(), currentTime);

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            // Don't manually set timestamps - let @PrePersist handle it
            // Or if your Note entity doesn't have @PrePersist, set them manually:
            if (note.getCreatedAt() == null) {
                Date now = new Date();
                note.setCreatedAt(now);
                note.setUpdatedAt(now);
            }

            session.persist(note);
            transaction.commit();

            logger.info("✅ Note saved successfully - ID: {} for user: {}",
                    note.getId(), note.getUser().getUsername());
            return true;

        } catch (Exception e) {
            logger.error("❌ Error saving note", e);
            if (transaction != null) {
                try {
                    transaction.rollback();
                    logger.info("🔄 Transaction rolled back");
                } catch (Exception rollbackEx) {
                    logger.error("❌ Error during rollback", rollbackEx);
                }
            }
            return false;
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception closeEx) {
                    logger.error("❌ Error closing session", closeEx);
                }
            }
        }
    }

    /**
     * Update an existing note
     */
    public boolean updateNote(Note note) {
        Session session = null;
        Transaction transaction = null;

        String currentTime = getCurrentUTCTime();
        logger.info("✏️ Updating note - ID: {} at: {}", note.getId(), currentTime);

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            // Set updated timestamp manually (or let @PreUpdate handle it)
            note.setUpdatedAt(new Date());

            session.merge(note);
            transaction.commit();

            logger.info("✅ Note updated successfully - ID: {} for user: {}",
                    note.getId(), note.getUser().getUsername());
            return true;

        } catch (Exception e) {
            logger.error("❌ Error updating note - ID: {}", note.getId(), e);
            if (transaction != null) {
                try {
                    transaction.rollback();
                    logger.info("🔄 Transaction rolled back");
                } catch (Exception rollbackEx) {
                    logger.error("❌ Error during rollback", rollbackEx);
                }
            }
            return false;
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception closeEx) {
                    logger.error("❌ Error closing session", closeEx);
                }
            }
        }
    }

    /**
     * Get all notes for a specific user
     */
    public List<Note> getNotesByUser(User user) {
        Session session = null;

        String currentTime = getCurrentUTCTime();
        logger.info("📋 Fetching notes for user: {} at: {}", user.getUsername(), currentTime);

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Note> query = session.createQuery(
                    "FROM Note WHERE user = :user ORDER BY updatedAt DESC",
                    Note.class
            );
            query.setParameter("user", user);

            List<Note> notes = query.list();
            logger.info("✅ Retrieved {} notes for user: {}", notes.size(), user.getUsername());
            return notes;

        } catch (Exception e) {
            logger.error("❌ Error fetching notes for user: {}", user.getUsername(), e);
            return new ArrayList<>();
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception closeEx) {
                    logger.error("❌ Error closing session", closeEx);
                }
            }
        }
    }

    /**
     * Get a note by its ID
     */
    public Note getNoteById(Long id) {
        Session session = null;

        String currentTime = getCurrentUTCTime();
        logger.info("🔍 Fetching note by ID: {} at: {}", id, currentTime);

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Note note = session.get(Note.class, id);

            if (note != null) {
                logger.info("✅ Note found - ID: {}, Title: '{}'", id, note.getTitle());
            } else {
                logger.warn("⚠️ Note not found - ID: {}", id);
            }

            return note;

        } catch (Exception e) {
            logger.error("❌ Error fetching note by ID: {}", id, e);
            return null;
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception closeEx) {
                    logger.error("❌ Error closing session", closeEx);
                }
            }
        }
    }

    /**
     * Delete a note by its ID
     */
    public boolean deleteNote(Long id) {
        Session session = null;
        Transaction transaction = null;

        String currentTime = getCurrentUTCTime();
        logger.info("🗑️ Deleting note - ID: {} at: {}", id, currentTime);

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Note note = session.get(Note.class, id);
            if (note != null) {
                session.remove(note);
                transaction.commit();

                logger.info("✅ Note deleted successfully - ID: {}, Title: '{}'",
                        id, note.getTitle());
                return true;
            } else {
                logger.warn("⚠️ Note not found for deletion - ID: {}", id);
                return false;
            }

        } catch (Exception e) {
            logger.error("❌ Error deleting note - ID: {}", id, e);
            if (transaction != null) {
                try {
                    transaction.rollback();
                    logger.info("🔄 Transaction rolled back");
                } catch (Exception rollbackEx) {
                    logger.error("❌ Error during rollback", rollbackEx);
                }
            }
            return false;
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception closeEx) {
                    logger.error("❌ Error closing session", closeEx);
                }
            }
        }
    }

    /**
     * Search notes by title or content
     */
    public List<Note> searchNotes(User user, String searchTerm) {
        Session session = null;

        String currentTime = getCurrentUTCTime();
        logger.info("🔍 Searching notes for user: {} with term: '{}' at: {}",
                user.getUsername(), searchTerm, currentTime);

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Note> query = session.createQuery(
                    "FROM Note WHERE user = :user AND (LOWER(title) LIKE LOWER(:search) OR LOWER(content) LIKE LOWER(:search)) ORDER BY updatedAt DESC",
                    Note.class
            );
            query.setParameter("user", user);
            query.setParameter("search", "%" + searchTerm + "%");

            List<Note> notes = query.list();
            logger.info("✅ Found {} notes matching search term '{}' for user: {}",
                    notes.size(), searchTerm, user.getUsername());
            return notes;

        } catch (Exception e) {
            logger.error("❌ Error searching notes for user: {} with term: '{}'",
                    user.getUsername(), searchTerm, e);
            return new ArrayList<>();
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception closeEx) {
                    logger.error("❌ Error closing session", closeEx);
                }
            }
        }
    }

    /**
     * Get notes count for a user
     */
    public long getNoteCountByUser(User user) {
        Session session = null;

        String currentTime = getCurrentUTCTime();
        logger.info("📊 Getting note count for user: {} at: {}", user.getUsername(), currentTime);

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM Note WHERE user = :user",
                    Long.class
            );
            query.setParameter("user", user);

            Long count = query.uniqueResult();
            logger.info("✅ User {} has {} notes", user.getUsername(), count);
            return count != null ? count : 0L;

        } catch (Exception e) {
            logger.error("❌ Error getting note count for user: {}", user.getUsername(), e);
            return 0L;
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception closeEx) {
                    logger.error("❌ Error closing session", closeEx);
                }
            }
        }
    }

    /**
     * Get recent notes for a user (limit specified)
     */
    public List<Note> getRecentNotes(User user, int limit) {
        Session session = null;

        String currentTime = getCurrentUTCTime();
        logger.info("📋 Fetching {} recent notes for user: {} at: {}",
                limit, user.getUsername(), currentTime);

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Note> query = session.createQuery(
                    "FROM Note WHERE user = :user ORDER BY updatedAt DESC",
                    Note.class
            );
            query.setParameter("user", user);
            query.setMaxResults(limit);

            List<Note> notes = query.list();
            logger.info("✅ Retrieved {} recent notes for user: {}", notes.size(), user.getUsername());
            return notes;

        } catch (Exception e) {
            logger.error("❌ Error fetching recent notes for user: {}", user.getUsername(), e);
            return new ArrayList<>();
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception closeEx) {
                    logger.error("❌ Error closing session", closeEx);
                }
            }
        }
    }

    /**
     * Check if a note belongs to a specific user
     */
    public boolean isNoteOwnedByUser(Long noteId, User user) {
        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM Note WHERE id = :noteId AND user = :user",
                    Long.class
            );
            query.setParameter("noteId", noteId);
            query.setParameter("user", user);

            Long count = query.uniqueResult();
            boolean isOwned = count != null && count > 0;

            logger.info("🔒 Note ownership check - Note ID: {}, User: {}, Owned: {}",
                    noteId, user.getUsername(), isOwned);

            return isOwned;

        } catch (Exception e) {
            logger.error("❌ Error checking note ownership - Note ID: {}, User: {}",
                    noteId, user.getUsername(), e);
            return false;
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception closeEx) {
                    logger.error("❌ Error closing session", closeEx);
                }
            }
        }
    }

    /**
     * Get current UTC time formatted
     */
    private String getCurrentUTCTime() {
        return LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}