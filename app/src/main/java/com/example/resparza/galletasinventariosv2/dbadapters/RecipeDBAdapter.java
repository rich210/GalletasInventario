package com.example.resparza.galletasinventariosv2.dbadapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.resparza.galletasinventariosv2.models.MeasureType;
import com.example.resparza.galletasinventariosv2.models.Recipe;
import com.example.resparza.galletasinventariosv2.models.RecipeProduct;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by resparza on 29/01/2016.
 */
public class RecipeDBAdapter {

    public static final String TAG = "RecipeDBAdapter";
    public static final String RECIPE_ID = "recipe_id";
    public static final String RECIPE_NAME = "recipe_name";
    //public static final String MEASURE_TYPE_ID = "measure_type_id";
    public static final String QUANTITY = "quantity";
    //public static final String RECIPE_COST = "recipe_cost"; //TODO: Delete recipe cost column it will be generated
    public static final String CREATED_ON = "created";
    public static final String UPDATED_ON = "updated";
    public static final String IMAGE_PATH = "image_path";
    public static final String RECIPE_INSTRUCTIONS = "recipe_instructions";

    public static final String RECIPE_TABLE = "tbl_recipes";
    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public RecipeDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public RecipeDBAdapter open() throws SQLException {
        this.mDbHelper = new DatabaseHelper(this.mCtx);
        this.mDb = this.mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.mDbHelper.close();
    }

    public long insertItem(Recipe recipe) {
        ContentValues initialValues = itemToValues(recipe, false);
        return this.mDb.insert(RECIPE_TABLE, null, initialValues);
    }

    public long insertItem(Recipe recipe, List<RecipeProduct> recipeProductList) throws SQLException {
        long recipeId = 0;
        ContentValues initialValues = itemToValues(recipe, false);

        try {
            this.mDb.beginTransaction();
            recipeId = this.mDb.insert(RECIPE_TABLE, null, initialValues);
            RecipeProductDBAdapter recipeProductDBAdapter = new RecipeProductDBAdapter(mCtx);
            //recipeProductDBAdapter.open();
            for (RecipeProduct recipeProduct: recipeProductList) {
                recipeProduct.setRecipeId(recipeId);
                long recipeProductId = recipeProductDBAdapter.insertItem(recipeProduct, this.mDb);
                if (recipeProductId<1){
                    Log.e(TAG,"Error trying to insert recipe product");
                    break;
                }
            }
            //recipeProductDBAdapter.close();
            this.mDb.setTransactionSuccessful();
        }catch (Exception e){
            Log.e(TAG, "insertItem: Error:",e );
        }
        this.mDb.endTransaction();
        return recipeId;

    }

    public boolean deleteItemById(long recipeId) {

        return this.mDb.delete(RECIPE_TABLE, RECIPE_ID + "=" + recipeId, null) > 0; //$NON-NLS-1$
    }

    public boolean deleteItemsByIds(long recipeIds[]) {
        RecipeProductDBAdapter recipeProductDBAdapter = new RecipeProductDBAdapter(mCtx);
        boolean isDeleted = false;
        StringBuilder whereClause = new StringBuilder();
        whereClause.append(RECIPE_ID + " IN (");
        try {
            this.mDb.beginTransaction();
            recipeProductDBAdapter.open();
            for (int i = 0; i <= recipeIds.length- 1; i++) {
                if (i < recipeIds.length - 1)
                    whereClause.append(recipeIds[i] + ",");
                else
                    whereClause.append(recipeIds[i]);
            }
            whereClause.append(")");
            isDeleted = this.mDb.delete(RECIPE_TABLE, whereClause.toString(), null) > 0;
            if (!isDeleted){
                Log.e(TAG, "Error tryinn to delete in tbl_recipes");
                return isDeleted;
            }
            isDeleted = recipeProductDBAdapter.deleteRecipeProductByRecipeIds(recipeIds);
            if (!isDeleted)
                Log.e(TAG, "Error tryinn to delete in tbl_recipe_product");
            recipeProductDBAdapter.close();
             //$NON-NLS-1$
            this.mDb.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.mDb.endTransaction();
        return isDeleted;
    }

    /**
     * Return a Cursor over the list of all cars in the database
     *
     * @return Cursor over all cars
     */
    public List<Recipe> getAllItems() {
        List<Recipe> recipes = new ArrayList<Recipe>();
        String selectQuery = "SELECT  * FROM " + RECIPE_TABLE;

        //Log.e(LOG, selectQuery);

        Cursor c = this.mDb.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Recipe recipe = cursorToItem(c);
                recipes.add(recipe);
            } while (c.moveToNext());
        }
        return recipes;
    }

    public Recipe getItemById(long recipeId) throws SQLException {

        String selectQuery = "SELECT  * FROM " + RECIPE_TABLE + " WHERE "
                + RECIPE_ID + " = " + recipeId;

        Cursor c = this.mDb.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Recipe recipe = cursorToItem(c);
        return recipe;
    }

    public boolean updateItem(Recipe recipe, List<RecipeProduct> recipeProducts) throws SQLException {
        boolean isRecipeUpdated = false;
        ContentValues values = itemToValues(recipe, true);

        try{
            this.mDb.beginTransaction();
            isRecipeUpdated = this.mDb.update(RECIPE_TABLE, values, RECIPE_ID + "=" + recipe.getRecipeId(), null) > 0;
            if (isRecipeUpdated){
                RecipeProductDBAdapter recipeProductDBAdapter = new RecipeProductDBAdapter(mCtx);
                recipeProductDBAdapter.open();
                for (RecipeProduct recipeProduct: recipeProducts) {
                    isRecipeUpdated = recipeProductDBAdapter.updateItem(recipeProduct);
                    if (!isRecipeUpdated){
                        Log.e(TAG,"Error trying to insert recipe product");
                        break;
                    }
                }
                recipeProductDBAdapter.close();
            }else {
                Log.e(TAG, "updateItem: Error updating recipe");
            }
            this.mDb.setTransactionSuccessful();
        }catch (Exception e){
            Log.e(TAG, "updateItem: Error"+e.getMessage(),e.getCause());
        }
        this.mDb.endTransaction();
        return isRecipeUpdated;
    }

    private Recipe cursorToItem(Cursor cursor) {
        RecipeProductDBAdapter recipeProductDBAdapter = new RecipeProductDBAdapter(this.mCtx);
        Recipe recipe = new Recipe();
        recipe.setRecipeId(cursor.getInt(cursor.getColumnIndex(RECIPE_ID)));
        recipe.setRecipeName(cursor.getString(cursor.getColumnIndex(RECIPE_NAME)));
        recipe.setMeasureTypeQuantity(cursor.getInt(cursor.getColumnIndex(QUANTITY)));
        //recipe.setRecipeCost(cursor.getFloat(cursor.getColumnIndex(RECIPE_COST)));
        recipe.setRecipeImagePath(cursor.getString(cursor.getColumnIndex(IMAGE_PATH)));
        recipe.setRecipeInstructions(cursor.getString(cursor.getColumnIndex(RECIPE_INSTRUCTIONS)));
        try {
            recipeProductDBAdapter.open();
            List<RecipeProduct> recipeProducts = recipeProductDBAdapter.getAllItemsByRecipeId(recipe.getRecipeId());
            recipe.setRecipeProducts(recipeProducts);
            recipeProductDBAdapter.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "Error obtaining recipe products");
        }
        return recipe;

    }

    private ContentValues itemToValues(Recipe recipe, boolean isUpdate) {
        ContentValues values = new ContentValues();
        values.put(RECIPE_NAME, recipe.getRecipeName());
        //values.put(MEASURE_TYPE_ID, recipe.getMeasureType().getMeasureTypeId());
        values.put(QUANTITY, recipe.getMeasureTypeQuantity());
        //values.put(RECIPE_COST, recipe.getRecipeCost(mCtx));
        values.put(IMAGE_PATH,recipe.getRecipeImagePath());
        values.put(RECIPE_INSTRUCTIONS,recipe.getRecipeInstructions());

        values.put(UPDATED_ON, sdf.format(new Date(0)));
        if (isUpdate) {
            values.put(CREATED_ON, sdf.format(new Date(0)));
        }
        return values;
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
