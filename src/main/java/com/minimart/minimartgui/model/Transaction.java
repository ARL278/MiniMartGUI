package com.minimart.minimartgui.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Model class untuk entitas Transaksi Utama dengan anotasi Hibernate ORM / JPA.
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name = "payment_amount", nullable = false)
    private double paymentAmount;

    @Column(name = "change_amount", nullable = false)
    private double changeAmount;

    @Column(name = "transaction_date", insertable = false, updatable = false)
    private Timestamp transactionDate;

    @Transient
    private List<TransactionDetail> details;

    public Transaction() {
        this.details = new ArrayList<>();
    }

    public Transaction(String invoiceNumber, int userId, String customerName, double totalAmount, double paymentAmount, double changeAmount) {
        this.invoiceNumber = invoiceNumber;
        this.userId = userId;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.paymentAmount = paymentAmount;
        this.changeAmount = changeAmount;
        this.details = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public double getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(double changeAmount) {
        this.changeAmount = changeAmount;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    public List<TransactionDetail> getDetails() {
        return details;
    }

    public void setDetails(List<TransactionDetail> details) {
        this.details = details;
    }

    public void addDetail(TransactionDetail detail) {
        this.details.add(detail);
    }

    public String getFormattedTotal() {
        NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        return rupiahFormat.format(totalAmount).replace(",00", "");
    }
}
