package com.example.resparza.galletasinventariosv2.models;

import java.util.Date;

/**
 * Created by resparza on 27/01/2016.
 */
public class Order {

    private long orderId;
    private String orderName;
    private String clientName;
    private String clientNumber;
    private Date deliveryDate;
    private OrderRecipe[] orderRecipes;
    private float total;
    private float sellPrice;
    private float gain;

    public Order() {
    }

    public Order(String clientName, String clientNumber, Date deliveryDate, OrderRecipe[] orderRecipes, float total, float sellPrice) {
        this.clientName = clientName;
        this.clientNumber = clientNumber;
        this.deliveryDate = deliveryDate;
        this.orderRecipes = orderRecipes;
        this.total = total;
        this.sellPrice = sellPrice;
    }

    public Order(int orderId, String clientName, String clientNumber, Date deliveryDate, OrderRecipe[] orderRecipes, float total, float sellPrice) {
        this.orderId = orderId;
        this.clientName = clientName;
        this.clientNumber = clientNumber;
        this.deliveryDate = deliveryDate;
        this.orderRecipes = orderRecipes;
        this.total = total;
        this.sellPrice = sellPrice;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(String clientNumber) {
        this.clientNumber = clientNumber;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public OrderRecipe[] getOrderRecipes() {
        return orderRecipes;
    }

    public void setOrderRecipes(OrderRecipe[] orderRecipes) {
        this.orderRecipes = orderRecipes;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(float sellPrice) {
        this.sellPrice = sellPrice;
    }

    public float getGain() {
        return gain;
    }

    public void setGain(float gain) {
        this.gain = gain;
    }
}
