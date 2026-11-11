package com.minimart.minimartgui.dao;

import com.minimart.minimartgui.connection.HibernateUtil;
import com.minimart.minimartgui.model.CartItem;
import com.minimart.minimartgui.model.Product;
import com.minimart.minimartgui.model.Transaction;
import com.minimart.minimartgui.model.TransactionDetail;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) untuk Transaksi Checkout berbasis Hibernate 6 ORM.
 * Menerapkan Database Transaction (ACID) menggunakan Hibernate Transaction.
 */
public class TransactionDAO {

    /**
     * Memproses checkout secara transaksional:
     * 1. Validasi kecukupan stok dan kurangi stok produk via ORM.
     * 2. Simpan header transaksi via session.persist().
     * 3. Simpan seluruh detail barang transaksi via session.persist().
     * 4. Commit transaksi, atau Rollback jika terjadi kendala.
     */
    public boolean processCheckout(Transaction transaction, List<CartItem> cartItems) throws Exception {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Keranjang belanja masih kosong!");
        }

        org.hibernate.Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // 1. Validasi stok & kurangi stok produk
            for (CartItem item : cartItems) {
                Product p = session.find(Product.class, item.getProduct().getId());
                if (p == null) {
                    throw new Exception("Produk '" + item.getProduct().getName() + "' tidak ditemukan di database!");
                }
                if (p.getStock() < item.getQuantity()) {
                    throw new Exception("Stok untuk '" + p.getName() + "' tidak mencukupi! Tersedia: " 
                            + p.getStock() + ", Diminta: " + item.getQuantity());
                }
                // Kurangi stok di entity
                p.setStock(p.getStock() - item.getQuantity());
                session.merge(p);
            }

            // 2. Simpan Header Transaksi
            session.persist(transaction);
            session.flush(); // Flush untuk menghasilkan auto-generated ID

            // 3. Simpan Rincian Transaksi
            for (CartItem item : cartItems) {
                TransactionDetail detail = new TransactionDetail(
                        transaction.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity(),
                        item.getSubtotal()
                );
                session.persist(detail);
            }

            // Commit seluruh operasi
            tx.commit();
            return true;

        } catch (Exception ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        }
    }

    /**
     * Mengambil riwayat seluruh transaksi menggunakan HQL.
     */
    public List<Transaction> getAllTransactions() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Transaction ORDER BY id DESC", Transaction.class).list();
        } catch (Exception e) {
            System.err.println("[TransactionDAO - Hibernate] Error getAllTransactions: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Mengambil detail barang dari sebuah transaksi berdasarkan transaction_id.
     */
    public List<TransactionDetail> getDetailsByTransactionId(int transactionId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM TransactionDetail WHERE transactionId = :tid";
            Query<TransactionDetail> query = session.createQuery(hql, TransactionDetail.class);
            query.setParameter("tid", transactionId);
            return query.list();
        } catch (Exception e) {
            System.err.println("[TransactionDAO - Hibernate] Error getDetailsByTransactionId: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
