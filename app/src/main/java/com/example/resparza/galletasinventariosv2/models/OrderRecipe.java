package com.example.resparza.galletasinventariosv2.models;

/**
 * Created by resparza on 27/01/2016.
 */
public class OrderRecipe {

    private long orderId;
    private long recipeId;
    //quantity per reciepe, 1 cake, 10 cookies, 50 bombones
    private double orderQuantity;
    private double pricePerUnit;
    private double total;
    private String recipeName;

    public OrderRecipe() {
    }

    public OrderRecipe(long orderId, long recipeId, double orderQuantity, double pricePerUnit, double total) {
        this.orderId = orderId;
        this.recipeId = recipeId;
        this.orderQuantity = orderQuantity;
        this.pricePerUnit = pricePerUnit;
        this.total = total;
    }

    public OrderRecipe(long recipeId, double orderQuantity, double pricePerUnit, double total) {
        this.recipeId = recipeId;
        this.orderQuantity = orderQuantity;
        this.pricePerUnit = pricePerUnit;
        this.total = total;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public double getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(double orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }
}
