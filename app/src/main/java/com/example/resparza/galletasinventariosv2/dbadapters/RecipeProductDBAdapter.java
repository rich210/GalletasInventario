package com.example.resparza.galletasinventariosv2.dbadapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.resparza.galletasinventariosv2.models.RecipeProduct;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by resparza on 05/02/2016.
 */
public class RecipeProductDBAdapter {
    public static final String RECIPE_ID = "recipe_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String PRODUCT_QUANTITY = "product_quantity";
    public static final String MEASUREMENT_TYPE_ID = "measurement_type_id";
    public static final String CREATED_ON = "created";
    public static final String UPDATED_ON = "updated";

    public static final String RECIPE_PRODUCT_TABLE = "tbl_recipe_product";
    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public RecipeProductDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public RecipeProductDBAdapter open() throws SQLException {
        this.mDbHelper = new DatabaseHelper(this.mCtx);
        this.mDb = this.mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.mDbHelper.close();
    }

    public long insertItem(RecipeProduct recipeProduct) {
        ContentValues initialValues = itemToValues(recipeProduct, false);
        return this.mDb.insert(RECIPE_PRODUCT_TABLE, null, initialValues);
    }

    public boolean insertItems(List<RecipeProduct> recipeProducts) {
        for (RecipeProduct recipeProduct : recipeProducts) {
            ContentValues initialValues = itemToValues(recipeProduct, false);
            if (!(this.mDb.insert(RECIPE_PRODUCT_TABLE, null, initialValues) > 0)) {
                return false;
            }
        }

        return true;
    }

    public boolean deleteItem(long productId, long recipeId) {

        return this.mDb.delete(RECIPE_PRODUCT_TABLE, PRODUCT_ID + "=" + productId + "and" + RECIPE_ID + "=" + recipeId, null) > 0; //$NON-NLS-1$
    }

    public boolean deleteRecipeProductByRecipeId(long recipeId) {

        return this.mDb.delete(RECIPE_PRODUCT_TABLE, RECIPE_ID + "=" + recipeId, null) > 0; //$NON-NLS-1$ //add both columns to where
    }

    public boolean deleteRecipeProductByProductId(long productId) {

        return this.mDb.delete(RECIPE_PRODUCT_TABLE, PRODUCT_ID + "=" + productId, null) > 0; //$NON-NLS-1$
    }

    /**
     * Return a Cursor over the list of all cars in the database
     *
     * @return Cursor over all cars
     */
    public List<RecipeProduct> getAllItems() {
        List<RecipeProduct> recipeProducts = new ArrayList<RecipeProduct>();
        String selectQuery = "SELECT  * FROM " + RECIPE_PRODUCT_TABLE;

        //Log.e(LOG, selectQuery);

        Cursor c = this.mDb.rawQuery(selectQuery, null);
        recipeProducts = obtainListOrderRecipe(c);

        return recipeProducts;
    }

    public List<RecipeProduct> getAllItemsByRecipeId(long recipeId) {
        List<RecipeProduct> recipeProducts = new ArrayList<RecipeProduct>();
        String selectQuery = "SELECT  * FROM " + RECIPE_PRODUCT_TABLE + " WHERE "
                + RECIPE_ID + " = " + recipeId;

        //Log.e(LOG, selectQuery);

        Cursor c = this.mDb.rawQuery(selectQuery, null);

        recipeProducts = obtainListOrderRecipe(c);
        return recipeProducts;
    }

    public List<RecipeProduct> getAllItemsByProductId(long productId) {
        List<RecipeProduct> orderRecipes = new ArrayList<RecipeProduct>();
        String selectQuery = "SELECT  * FROM " + RECIPE_PRODUCT_TABLE + " WHERE "
                + PRODUCT_ID + " = " + productId;

        //Log.e(LOG, selectQuery);

        Cursor c = this.mDb.rawQuery(selectQuery, null);

        orderRecipes = obtainListOrderRecipe(c);
        return orderRecipes;
    }

    public boolean updateItem(RecipeProduct recipeProduct) {

        ContentValues values = itemToValues(recipeProduct, true);
        return this.mDb.update(RECIPE_PRODUCT_TABLE, values, RECIPE_ID + "=" + recipeProduct.getRecipeId() +" and " + PRODUCT_ID + "=" + recipeProduct.getProductId(), null) >0;
    }

    public List<RecipeProduct> obtainListOrderRecipe(Cursor c){
        List<RecipeProduct> recipeProducts = new ArrayList<RecipeProduct>();
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                RecipeProduct recipeProduct = cursorToItem(c);
                recipeProducts.add(recipeProduct);
            } while (c.moveToNext());
        }
        return recipeProducts;


    }

    private RecipeProduct cursorToItem(Cursor c) {

        RecipeProduct recipeProduct = new RecipeProduct();
        recipeProduct.setRecipeId(c.getInt(c.getColumnIndex(RECIPE_ID)));
        recipeProduct.setProductId(c.getInt(c.getColumnIndex(PRODUCT_ID)));
        recipeProduct.setProductQuantity(c.getInt(c.getColumnIndex(PRODUCT_QUANTITY)));
        recipeProduct.setMeasureTypeId(c.getLong(c.getColumnIndex(MEASUREMENT_TYPE_ID)));
        return recipeProduct;

    }

    private ContentValues itemToValues(RecipeProduct recipeProduct, boolean isUpdate) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(PRODUCT_QUANTITY, recipeProduct.getProductQuantity());
        initialValues.put(UPDATED_ON, sdf.format(new Date(0)));
        initialValues.put(MEASUREMENT_TYPE_ID,recipeProduct.getMeasureTypeId());
        if (isUpdate) {
            initialValues.put(CREATED_ON, sdf.format(new Date(0)));
        } else {
            initialValues.put(RECIPE_ID, recipeProduct.getRecipeId());
            initialValues.put(PRODUCT_ID, recipeProduct.getProductId());
        }
        return initialValues;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DBAdapter.DATABASE_NAME, null, DBAdapter.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
