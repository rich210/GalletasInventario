package com.example.resparza.galletasinventariosv2.models;

import android.content.Context;

import com.example.resparza.galletasinventariosv2.dbadapters.MeasureTypeDBAdapter;
import com.example.resparza.galletasinventariosv2.dbadapters.ProductDBAdapter;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by resparza on 27/01/2016.
 */
public class Recipe {

    private long recipeId;
    private String recipeName;
    private int quantity;
    private List<RecipeProduct> recipeProducts;
    private MeasureType measureType;
    private float recipeCost;
    private String recipeImagePath;
    private String recipeInstructions;

    public Recipe() {
    }

    public Recipe(String recipeName, MeasureType measureType, int quantity, List<RecipeProduct> recipeProducts, String recipeImagePath) {
        this.recipeName = recipeName;
        this.measureType = measureType;
        this.quantity = quantity;
        this.recipeProducts = recipeProducts;
        this.recipeImagePath = recipeImagePath;
    }

    public Recipe(int recipeId, String recipeName, MeasureType measureType, int quantity, List<RecipeProduct> recipeProducts) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.measureType = measureType;
        this.quantity = quantity;
        this.recipeProducts = recipeProducts;
    }

    public Recipe(long recipeId, String recipeName, int quantity, List<RecipeProduct> recipeProducts, MeasureType measureType, float recipeCost, String recipeImagePath, String recipeInstructions) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.quantity = quantity;
        this.recipeProducts = recipeProducts;
        this.measureType = measureType;
        this.recipeCost = recipeCost;
        this.recipeImagePath = recipeImagePath;
        this.recipeInstructions = recipeInstructions;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public MeasureType getMeasureType() {
        return measureType;
    }

    public void setMeasureType(MeasureType measureType) {
        this.measureType = measureType;
    }

    public int getMeasureTypeQuantity() {
        return quantity;
    }

    public void setMeasureTypeQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<RecipeProduct> getRecipeProducts() {
        return recipeProducts;
    }

    public void setRecipeProducts(List<RecipeProduct> recipeProducts) {
        this.recipeProducts = recipeProducts;
    }

    public float getRecipeCost(Context ctx) {
        ProductDBAdapter productDBAdapter = new ProductDBAdapter(ctx);
        MeasureTypeDBAdapter measureTypeDBAdapter = new MeasureTypeDBAdapter(ctx);
        Product product;
        MeasureType measureType;
        float productCost = 0;
        float recipeCost = 0;
        for (RecipeProduct recipeProduct: this.recipeProducts) {
            try {
                productDBAdapter.open();
                product = productDBAdapter.getItemById(recipeProduct.getProductId());
                productDBAdapter.close();
                measureTypeDBAdapter.open();
                measureType = measureTypeDBAdapter.getItemById(recipeProduct.getMeasureTypeId());
                productCost = product.getCostPerUnit();
                if(!measureType.isMeasureBase()){
                    productCost = productCost*measureType.getMeasureEquivalencyId();
                }
                productCost = productCost*recipeProduct.getProductQuantity();
                recipeCost = recipeCost + productCost;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return recipeCost;
    }

    public void setRecipeCost(float recipeCost) {
        this.recipeCost = recipeCost;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getRecipeImagePath() {
        return recipeImagePath;
    }

    public void setRecipeImagePath(String recipeImagePath) {
        this.recipeImagePath = recipeImagePath;
    }

    public String getRecipeInstructions() {
        return recipeInstructions;
    }

    public void setRecipeInstructions(String recipeInstructions) {
        this.recipeInstructions = recipeInstructions;
    }

    public String getProducts(){
        StringBuffer products = new StringBuffer();
        for (RecipeProduct recipeProduct: this.recipeProducts) {
            if(!products.toString().isEmpty()){
                products.append(" \n");
            }
            products.append(recipeProduct.getProductName());
        }
        return products.toString();
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "recipeId=" + recipeId +
                ", recipeName='" + recipeName + '\'' +
                ", quantity=" + quantity +
                ", recipeProducts=" + recipeProducts.toString() +
                ", measureType=" + measureType +
                ", recipeCost=" + recipeCost +
                ", recipeImagePath='" + recipeImagePath + '\'' +
                ", recipeInstructions='" + recipeInstructions + '\'' +
                '}';
    }
}
