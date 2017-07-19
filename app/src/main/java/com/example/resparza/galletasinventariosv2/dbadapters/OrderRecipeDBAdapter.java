package com.example.resparza.galletasinventariosv2.dbadapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.resparza.galletasinventariosv2.models.OrderRecipe;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by resparza on 05/02/2016.
 */
public class OrderRecipeDBAdapter {

    public static final String TAG = "OrderRecipeDBAdapter";
    public static final String RECIPE_ID = "recipe_id";
    public static final String ORDER_ID = "order_id";
    public static final String ORDER_QUANTITY = "order_quantity";
    public static final String PRICE_PER_UNIT = "price_per_unit";
    public static final String TOTAL = "total";
    public static final String CREATED_ON = "created";
    public static final String UPDATED_ON = "updated";

    public static final String ORDER_RECIPE_TABLE = "tbl_order_recipes";
    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public OrderRecipeDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public OrderRecipeDBAdapter open() throws SQLException {
        this.mDbHelper = new DatabaseHelper(this.mCtx);
        this.mDb = this.mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.mDbHelper.close();
    }

    public long insertItem(OrderRecipe ordRec, SQLiteDatabase mDb) {
        ContentValues initialValues = itemToValues(ordRec, false);
        return mDb.insert(ORDER_RECIPE_TABLE, null, initialValues);
    }

    public boolean deleteOrderRecipeByRecipeId(long recipeId) {

        return this.mDb.delete(ORDER_RECIPE_TABLE, RECIPE_ID + "=" + recipeId, null) > 0; //$NON-NLS-1$
    }

    public boolean deleteOrderRecipeByOrderId(long orderId) {

        return this.mDb.delete(ORDER_RECIPE_TABLE, ORDER_ID + "=" + orderId, null) > 0; //$NON-NLS-1$
    }

    /**
     * Return a Cursor over the list of all cars in the database
     *
     * @return Cursor over all cars
     */
    public List<OrderRecipe> getAllItems() {
        List<OrderRecipe> orderRecipes = new ArrayList<OrderRecipe>();
        String selectQuery = "SELECT  * FROM " + ORDER_RECIPE_TABLE;

        //Log.e(LOG, selectQuery);

        Cursor c = this.mDb.rawQuery(selectQuery, null);
        orderRecipes = obtainListOrderRecipe(c);

        return orderRecipes;
    }

    public List<OrderRecipe> getItemsByRecipeId(long recipeId) {
        List<OrderRecipe> orderRecipes = new ArrayList<OrderRecipe>();
        String selectQuery = "SELECT  * FROM " + ORDER_RECIPE_TABLE + " WHERE "
                + RECIPE_ID + " = " + recipeId;

        //Log.e(LOG, selectQuery);

        Cursor c = this.mDb.rawQuery(selectQuery, null);

        orderRecipes = obtainListOrderRecipe(c);
        return orderRecipes;
    }

    public List<OrderRecipe> getItemsByOrderId(long orderId) {
        List<OrderRecipe> orderRecipes = new ArrayList<OrderRecipe>();
        RecipeDBAdapter recipeDBAdapter = new RecipeDBAdapter(mCtx);
        String selectQuery = "SELECT  * FROM " + ORDER_RECIPE_TABLE + " t1" +
                " JOIN "+recipeDBAdapter.RECIPE_TABLE+" t2 on t1."+RECIPE_ID+" = t2."+recipeDBAdapter.RECIPE_ID+" WHERE t1."
                + ORDER_ID + " = " + orderId;
        Cursor c = this.mDb.rawQuery(selectQuery, null);
        orderRecipes = obtainListOrderRecipe(c);
        return orderRecipes;
    }

    public boolean updateItem(OrderRecipe orderRecipe, SQLiteDatabase mDb) {

        ContentValues values = itemToValues(orderRecipe, true);
        return mDb.update(ORDER_RECIPE_TABLE, values, RECIPE_ID + "=" + orderRecipe.getRecipeId() +" and " + ORDER_ID + "=" + orderRecipe.getOrderId(), null) >0;
    }

    public List<OrderRecipe> obtainListOrderRecipe(Cursor c){
        List<OrderRecipe> orderRecipes = new ArrayList<OrderRecipe>();
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                OrderRecipe orderRecipe = cursorToItem(c);
                orderRecipes.add(orderRecipe);
            } while (c.moveToNext());
        }
        return orderRecipes;
    }

    private ContentValues itemToValues(OrderRecipe ordRec, boolean isUpdate) {
        Calendar c = Calendar.getInstance();
        ContentValues initialValues = new ContentValues();
        initialValues.put(RECIPE_ID, ordRec.getRecipeId());
        initialValues.put(ORDER_ID, ordRec.getOrderId());
        initialValues.put(ORDER_QUANTITY, ordRec.getOrderQuantity());
        initialValues.put(PRICE_PER_UNIT, ordRec.getPricePerUnit());
        initialValues.put(TOTAL, ordRec.getTotal());
        initialValues.put(UPDATED_ON, sdf.format(c.getTime()));
        if (!isUpdate) {
            initialValues.put(CREATED_ON, sdf.format(c.getTime()));
        }
        return initialValues;
    }

    private OrderRecipe cursorToItem(Cursor c) {
        OrderRecipe orderRecipe = new OrderRecipe();
        orderRecipe.setRecipeId(c.getInt(c.getColumnIndex(RECIPE_ID)));
        orderRecipe.setOrderId(c.getInt(c.getColumnIndex(ORDER_ID)));
        orderRecipe.setTotal(c.getFloat(c.getColumnIndex(TOTAL)));
        orderRecipe.setPricePerUnit(c.getFloat(c.getColumnIndex(PRICE_PER_UNIT)));
        orderRecipe.setOrderQuantity(c.getInt(c.getColumnIndex(ORDER_QUANTITY)));
        orderRecipe.setRecipeName(c.getString(c.getColumnIndex("recipe_name")));
        return orderRecipe;
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
