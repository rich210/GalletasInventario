package com.example.resparza.galletasinventariosv2.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by resparza on 27/01/2016.
 */
public class Order {

    private long orderId;
    private String orderName;
    private String clientName;
    private String clientNumber;
    private Date deliveryDate;
    private List<OrderRecipe> orderRecipes;
    private double total;
    private double sellPrice;
    private double gain;
    private String orderStatus;

    public Order() {
    }

    public Order(String clientName, String clientNumber, Date deliveryDate, List<OrderRecipe> orderRecipes, double total, double sellPrice) {
        this.clientName = clientName;
        this.clientNumber = clientNumber;
        this.deliveryDate = deliveryDate;
        this.orderRecipes = orderRecipes;
        this.total = total;
        this.sellPrice = sellPrice;
    }

    public Order(int orderId, String clientName, String clientNumber, Date deliveryDate, List<OrderRecipe> orderRecipes, double total, double sellPrice) {
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

    public List<OrderRecipe> getOrderRecipes() {
        return orderRecipes;
    }

    public void setOrderRecipes(List<OrderRecipe> orderRecipes) {
        this.orderRecipes = orderRecipes;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public double getGain() {
        return gain;
    }

    public void setGain(double gain) {
        this.gain = gain;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getFormatedDelivaryDate(){
        String date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        date = sdf.format(this.deliveryDate);
        return date;
    }

    public String getRecipes(){
        StringBuilder recipeNames = new StringBuilder();
        for (OrderRecipe orderRecipe:this.orderRecipes) {
            if(!recipeNames.toString().isEmpty()){
                recipeNames.append(" \n");
            }
            recipeNames.append(orderRecipe.getRecipeName());

        }
        return recipeNames.toString();
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderName='" + orderName + '\'' +
                ", clientName='" + clientName + '\'' +
                ", clientNumber='" + clientNumber + '\'' +
                ", deliveryDate=" + deliveryDate +
                ", orderRecipes=" + orderRecipes +
                ", total=" + total +
                ", sellPrice=" + sellPrice +
                ", gain=" + gain +
                ", orderStatus='" + orderStatus + '\'' +
                '}';
    }
}
