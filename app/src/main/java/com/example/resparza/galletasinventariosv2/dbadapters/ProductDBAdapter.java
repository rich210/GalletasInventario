package com.example.resparza.galletasinventariosv2.dbadapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.resparza.galletasinventariosv2.models.MeasureType;
import com.example.resparza.galletasinventariosv2.models.Product;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by resparza on 27/01/2016.
 */
public class ProductDBAdapter {

    public static final String TAG = "ProductDBAdapter";
    public static final String PRODUCT_ID = "product_id";
    public static final String PRODUCT_NAME = "product_name";
    public static final String MEASURE_TYPE_ID = "measure_type_id";
    public static final String QUANTITY = "quantity";
    public static final String COST_PER_UNIT = "cost_per_unit";
    public static final String PRODUCT_MIN = "product_min";
    public static final String CREATED_ON = "created";
    public static final String UPDATED_ON = "updated";
    public static final String PRODUCT_NEEDED = "product_needed";

    public static final String PRODUCT_TABLE = "tbl_products";
    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public ProductDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public ProductDBAdapter open() throws SQLException {
        this.mDbHelper = new DatabaseHelper(this.mCtx);
        this.mDb = this.mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.mDbHelper.close();
    }

    public long insertItem(Product product) {
        ContentValues initialValues = itemToValues(product, false);
        Log.d(TAG, initialValues.toString());
        return this.mDb.insert(PRODUCT_TABLE, null, initialValues);
    }

    public boolean deleteItemById(long productId) {

        return this.mDb.delete(PRODUCT_TABLE, PRODUCT_ID + "=" + productId, null) > 0; //$NON-NLS-1$
    }

    public boolean deleteItemsByIds(long productIds[]) {
        try {
            open();
            StringBuilder whereClause = new StringBuilder();
            whereClause.append(PRODUCT_ID + " IN (");
            for (int i = 0; i <= productIds.length -1; i++) {
                if (i < productIds.length -1)
                    whereClause.append(productIds[i] + ",");
                else
                    whereClause.append(productIds[i]);
            }
            whereClause.append(")");
            return this.mDb.delete(PRODUCT_TABLE, whereClause.toString(), null) > 0; //$NON-NLS-1$
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close();
        }
       return false;
    }

    /**
     * Return a Cursor over the list of all cars in the database
     *
     * @return Cursor over all cars
     */
    public List<Product> getAllItems() {
        List<Product> products = new ArrayList<Product>();
        String selectQuery = "SELECT  * FROM " + PRODUCT_TABLE;

        //Log.e(LOG, selectQuery);

        Cursor c = this.mDb.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Product prod = cursorToItem(c);
                products.add(prod);
            } while (c.moveToNext());
        }
        return products;
    }

    public List<Product> getAllItemsOrderByQuantity() {
        List<Product> products = new ArrayList<Product>();
        String selectQuery = "SELECT  *, (quantity - product_min) as result FROM " + PRODUCT_TABLE + " ORDER BY result limit 20";

        //Log.e(LOG, selectQuery);

        Cursor c = this.mDb.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Product prod = cursorToItem(c);
                products.add(prod);
            } while (c.moveToNext());
        }
        return products;
    }

    public List<Product> getAllProductNeededForOrder() {
        List<Product> products = new ArrayList<Product>();
        String selectQuery = "SELECT  * FROM view_product_needed limit 20";

        //Log.e(LOG, selectQuery);

        Cursor c = this.mDb.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Product prod = cursorToItem(c);
                products.add(prod);
            } while (c.moveToNext());
        }
        return products;
    }

    public Product getItemById(long productId) throws SQLException {

        String selectQuery = "SELECT  * FROM " + PRODUCT_TABLE + " WHERE "
                + PRODUCT_ID + " = " + productId;

        Cursor c = this.mDb.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Product prod = cursorToItem(c);
        return prod;
    }

    public boolean updateItem(Product prod) {
        float newQuantity;
        float newTotal;
        float newCostPerUnit;
        Product oldProduct;
        ContentValues values;

        try {
            oldProduct = this.getItemById(prod.getProductId());
            newQuantity = prod.getQuantity() + oldProduct.getQuantity();
            newTotal = prod.getTotalCost() + oldProduct.getTotalCost();
            newCostPerUnit = newTotal / newQuantity;
            prod.setTotalCost(newQuantity, newCostPerUnit);
            prod.setQuantity(newQuantity);
            prod.setCostPerUnit(newCostPerUnit);
            values = itemToValues(prod, true);
            return this.mDb.update(PRODUCT_TABLE, values, PRODUCT_ID + "=" + prod.getProductId(), null) > 0;
        } catch (SQLException e) {
            Log.d(TAG, "Error trying to retrieve product for : " + prod.getProductName() + " " + prod.getProductId());
        }

        return false;
    }


    private Product cursorToItem(Cursor cursor) {

        Product prod = new Product();
        prod.setProductId(cursor.getInt(cursor.getColumnIndex(PRODUCT_ID)));
        prod.setProductName(cursor.getString(cursor.getColumnIndex(PRODUCT_NAME)));
        prod.setQuantity(cursor.getInt(cursor.getColumnIndex(QUANTITY)));
        prod.setCostPerUnit(cursor.getFloat(cursor.getColumnIndex(COST_PER_UNIT)));
        prod.setProductMin(cursor.getFloat(cursor.getColumnIndex(PRODUCT_MIN)));
        prod.setTotalCost(cursor.getInt(cursor.getColumnIndex(QUANTITY)), cursor.getFloat(cursor.getColumnIndex(COST_PER_UNIT)));
        long measureTypeId = cursor.getLong(cursor.getColumnIndex(MEASURE_TYPE_ID));
        MeasureTypeDBAdapter measureTypeDBAdapter = new MeasureTypeDBAdapter(mCtx);
        try {
            MeasureType measureType = measureTypeDBAdapter.getItemById(measureTypeId);
            if (measureType != null)
                prod.setMeasureType(measureType);
        } catch (SQLException e) {
            Log.d(TAG, "Error trying to retrieve measure type for : " + prod.getProductName() + " " + prod.getProductId());
        }
        if(cursor.getColumnIndex(PRODUCT_NEEDED) != -1){
            prod.setProductNeeded(cursor.getFloat(cursor.getColumnIndex(PRODUCT_NEEDED)));
        }else{
            prod.setProductNeeded(0);
        }
        prod.setLowerThanMin(cursor.getInt(cursor.getColumnIndex(QUANTITY)),cursor.getFloat(cursor.getColumnIndex(PRODUCT_MIN)));
        return prod;

    }

    private ContentValues itemToValues(Product product, boolean isUpdate) {
        Calendar c = Calendar.getInstance();
        ContentValues initialValues = new ContentValues();
        initialValues.put(PRODUCT_NAME, product.getProductName());
        initialValues.put(COST_PER_UNIT, product.getCostPerUnit());
        initialValues.put(MEASURE_TYPE_ID, product.getMeasureType().getMeasureTypeId());
        initialValues.put(QUANTITY, product.getQuantity());
        initialValues.put(PRODUCT_MIN,product.getProductMin());
        initialValues.put(UPDATED_ON, sdf.format(c.getTime()));
        if (!isUpdate) {
            initialValues.put(CREATED_ON, sdf.format(c.getTime()));
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

    public List<Product> getAllItemsForSpinner(List<Long> productIds){
        List<Product> products = new ArrayList<Product>();
        StringBuilder queryIds = new StringBuilder();
        String selectQuery = null;
        if(!productIds.isEmpty() && productIds != null){
            for (Long id: productIds) {
                if(queryIds.toString().isEmpty()){
                    queryIds.append(id.toString());
                }else{
                    queryIds.append(", "+ id.toString());
                }
            }
            selectQuery = "SELECT  * FROM " + PRODUCT_TABLE + " WHERE product_id not in ("+queryIds.toString() +")";
        }else {
            selectQuery = "SELECT  * FROM " + PRODUCT_TABLE;
        }
        Cursor c = this.mDb.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Product prod = cursorToItem(c);
                products.add(prod);
            } while (c.moveToNext());
        }
        return products;

    }
}
