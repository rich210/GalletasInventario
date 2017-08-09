package com.example.resparza.galletasinventariosv2.models;

/**
 * Created by resparza on 27/01/2016.
 */
public class RecipeProduct {

    private long recipeId;
    private long productId;
    private long measureTypeId;
    private float productQuantity;
    private String productName;


    public RecipeProduct() {

    }

    public RecipeProduct(long recipeId, long productId,long measureTypeId , float productQuantity) {
        this.recipeId = recipeId;
        this.productId = productId;
        this.measureTypeId = measureTypeId;
        this.productQuantity = productQuantity;
    }

    public RecipeProduct(long productId, long measureTypeId, float productQuantity) {
        this.productId = productId;
        this.measureTypeId = measureTypeId;
        this.productQuantity = productQuantity;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public float getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(float productQuantity) {
        this.productQuantity = productQuantity;
    }

    public long getMeasureTypeId() {
        return measureTypeId;
    }

    public void setMeasureTypeId(long measureTypeId) {
        this.measureTypeId = measureTypeId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "RecipeProduct{" +
                "recipeId=" + recipeId +
                ", productId=" + productId +
                ", measureTypeId=" + measureTypeId +
                ", productQuantity=" + productQuantity +
                ", productName='" + productName + '\'' +
                '}';
    }
}
