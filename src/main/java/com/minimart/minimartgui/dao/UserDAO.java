package com.minimart.minimartgui.dao;

import com.minimart.minimartgui.connection.HibernateUtil;
import com.minimart.minimartgui.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 * Data Access Object (DAO) untuk Pengguna berbasis Hibernate 6 ORM.
 */
public class UserDAO {

    /**
     * Otentikasi login pengguna menggunakan HQL.
     */
    public User login(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM User WHERE username = :u AND password = :p";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("u", username);
            query.setParameter("p", password);
            return query.uniqueResult();
        } catch (Exception e) {
            System.err.println("[UserDAO - Hibernate] Error login: " + e.getMessage());
            return null;
        }
    }

    /**
     * Mendaftarkan pengguna baru menggunakan session.persist().
     */
    public boolean register(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("[UserDAO - Hibernate] Error register: " + e.getMessage());
            return false;
        }
    }

    /**
     * Memeriksa apakah username sudah terdaftar via HQL COUNT.
     */
    public boolean isUsernameTaken(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(u) FROM User u WHERE u.username = :u";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("u", username);
            Long count = query.uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            System.err.println("[UserDAO - Hibernate] Error isUsernameTaken: " + e.getMessage());
            return false;
        }
    }
}
