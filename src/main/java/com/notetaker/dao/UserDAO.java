package com.notetaker.dao;

import com.notetaker.model.User;
import com.notetaker.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class UserDAO {

    public boolean saveUser(User user) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception closeEx) {
                    System.err.println("Error closing session: " + closeEx.getMessage());
                }
            }
        }
    }

    public User getUserByUsername(String username) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception closeEx) {
                    System.err.println("Error closing session: " + closeEx.getMessage());
                }
            }
        }
    }

    public User getUserByEmail(String email) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception closeEx) {
                    System.err.println("Error closing session: " + closeEx.getMessage());
                }
            }
        }
    }

    public User getUserById(Long id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.get(User.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception closeEx) {
                    System.err.println("Error closing session: " + closeEx.getMessage());
                }
            }
        }
    }
}