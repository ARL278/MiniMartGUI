package com.minimart.minimartgui.dao;

import com.minimart.minimartgui.connection.HibernateUtil;
import com.minimart.minimartgui.model.Product;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) untuk Produk berbasis Hibernate 6 ORM.
 */
public class ProductDAO {

    /**
     * Mengambil seluruh daftar produk menggunakan HQL (Hibernate Query Language).
     */
    public List<Product> getAllProducts() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Product ORDER BY id DESC", Product.class).list();
        } catch (Exception e) {
            System.err.println("[ProductDAO - Hibernate] Error getAllProducts: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Mengambil data produk berdasarkan ID menggunakan session.find().
     */
    public Product getProductById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.find(Product.class, id);
        } catch (Exception e) {
            System.err.println("[ProductDAO - Hibernate] Error getProductById: " + e.getMessage());
            return null;
        }
    }

    /**
     * Mencari produk berdasarkan kata kunci menggunakan HQL LIKE.
     */
    public List<Product> searchProducts(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Product WHERE LOWER(name) LIKE :kw OR LOWER(category) LIKE :kw OR LOWER(description) LIKE :kw ORDER BY name ASC";
            Query<Product> query = session.createQuery(hql, Product.class);
            query.setParameter("kw", "%" + keyword.toLowerCase().trim() + "%");
            return query.list();
        } catch (Exception e) {
            System.err.println("[ProductDAO - Hibernate] Error searchProducts: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Memfilter produk berdasarkan kategori menggunakan HQL.
     */
    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.equalsIgnoreCase("Semua Kategori") || category.trim().isEmpty()) {
            return getAllProducts();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Product WHERE category = :cat ORDER BY name ASC";
            Query<Product> query = session.createQuery(hql, Product.class);
            query.setParameter("cat", category);
            return query.list();
        } catch (Exception e) {
            System.err.println("[ProductDAO - Hibernate] Error getProductsByCategory: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Menambahkan produk baru (INSERT) menggunakan session.persist().
     */
    public boolean insertProduct(Product product) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(product);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("[ProductDAO - Hibernate] Error insertProduct: " + e.getMessage());
            return false;
        }
    }

    /**
     * Memperbarui data produk (UPDATE) menggunakan session.merge().
     */
    public boolean updateProduct(Product product) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(product);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("[ProductDAO - Hibernate] Error updateProduct: " + e.getMessage());
            return false;
        }
    }

    /**
     * Menghapus produk (DELETE) menggunakan session.remove().
     */
    public boolean deleteProduct(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Product p = session.find(Product.class, id);
            if (p != null) {
                session.remove(p);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("[ProductDAO - Hibernate] Error deleteProduct: " + e.getMessage());
            return false;
        }
    }

    /**
     * Mengambil daftar kategori unik.
     */
    public List<String> getAllCategories() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL AND p.category <> '' ORDER BY p.category ASC";
            return session.createQuery(hql, String.class).list();
        } catch (Exception e) {
            System.err.println("[ProductDAO - Hibernate] Error getAllCategories: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
