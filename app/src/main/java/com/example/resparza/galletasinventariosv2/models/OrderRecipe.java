package com.example.resparza.galletasinventariosv2.models;

/**
 * Created by resparza on 27/01/2016.
 */
public class OrderRecipe {

    private long orderId;
    private long recipeId;
    //quantity per reciepe, 1 cake, 10 cookies, 50 bombones
    private int orderQuantity;
    private float pricePerUnit;
    private float total;

    public OrderRecipe() {
    }

    public OrderRecipe(long orderId, long recipeId, int orderQuantity, float pricePerUnit, float total) {
        this.orderId = orderId;
        this.recipeId = recipeId;
        this.orderQuantity = orderQuantity;
        this.pricePerUnit = pricePerUnit;
        this.total = total;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public float getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(float pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
