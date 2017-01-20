package com.example.resparza.galletasinventariosv2.models;

/**
 * Created by resparza on 27/01/2016.
 */
public class Recipe {

    private long recipeId;
    private String recipeName;
    private int quantity;
    private RecipeProduct[] recipeProducts;
    private MeasureType measureType;
    private float recipeCost;
    private String recipeImagePath;

    public Recipe() {
    }

    public Recipe(String recipeName, MeasureType measureType, int quantity, RecipeProduct[] recipeProducts, String recipeImagePath) {
        this.recipeName = recipeName;
        this.measureType = measureType;
        this.quantity = quantity;
        this.recipeProducts = recipeProducts;
        this.recipeImagePath = recipeImagePath;
    }

    public Recipe(int recipeId, String recipeName, MeasureType measureType, int quantity, RecipeProduct[] recipeProducts) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.measureType = measureType;
        this.quantity = quantity;
        this.recipeProducts = recipeProducts;
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

    public RecipeProduct[] getRecipeProducts() {
        return recipeProducts;
    }

    public void setRecipeProducts(RecipeProduct[] recipeProducts) {
        this.recipeProducts = recipeProducts;
    }

    public float getRecipeCost() {
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
}
