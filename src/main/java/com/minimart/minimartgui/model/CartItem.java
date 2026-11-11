package com.minimart.minimartgui.model;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Model class untuk merepresentasikan item belanja di Keranjang (Cart).
 */
public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void incrementQuantity() {
        this.quantity++;
    }

    public void decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }

    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    public String getFormattedSubtotal() {
        NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        return rupiahFormat.format(getSubtotal()).replace(",00", "");
    }
}
