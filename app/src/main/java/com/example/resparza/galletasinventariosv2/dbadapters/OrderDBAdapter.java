package com.example.resparza.galletasinventariosv2.dbadapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;


import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.models.Order;
import com.example.resparza.galletasinventariosv2.models.OrderRecipe;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by resparza on 05/02/2016.
 */
public class OrderDBAdapter {
    public static final String TAG = "OrderDBAdapter";

    public static final String ORDER_ID = "order_id";
    public static final String ORDER_NAME = "order_name";
    public static final String CLIENT_NAME = "client_name";
    public static final String CLIENT_NUMBER = "client_number";
    public static final String DELIVERY_DATE = "delivery_date";
    public static final String TOTAL = "total";
    public static final String SELL_PRICE = "sell_price";
    public static final String ORDER_STATUS = "order_status";
    //public static final String GAIN = "gain";
    public static final String CREATED_ON = "created";
    public static final String UPDATED_ON = "updated";

    public static final String ORDER_TABLE = "tbl_orders";
    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public OrderDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public OrderDBAdapter open() throws SQLException {
        this.mDbHelper = new DatabaseHelper(this.mCtx);
        this.mDb = this.mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.mDbHelper.close();
    }

    public long insertItem(Order order) {
        long orderId = 0;

        OrderRecipeDBAdapter orderRecipeDBAdapter = new OrderRecipeDBAdapter(mCtx);
        ContentValues initialValues = itemToValues(order, false);
        this.mDb.beginTransaction();
        try{
            orderId = this.mDb.insert(ORDER_TABLE, null, initialValues);
            if(orderId>0){
                for (OrderRecipe orderRecipe: order.getOrderRecipes()) {
                    orderRecipe.setOrderId(orderId);
                    if(orderRecipeDBAdapter.insertItem(orderRecipe, this.mDb)<1){
                        orderId=0;
                        Log.e(TAG,"Error trying to insert order recipe");
                        throw new RuntimeException("Order Recipe was not updated correctly");
                    }
                }
                this.mDb.setTransactionSuccessful();
            }
        }
        catch (Exception e){

        }
        finally {
            this.mDb.endTransaction();
            return orderId;
        }
    }

    public boolean deleteItemById(long orderId) {

        return this.mDb.delete(ORDER_TABLE, ORDER_ID + "=" + orderId, null) > 0; //$NON-NLS-1$
    }
    //TODO: Add function to set to cancel beside delete
    public boolean deleteItemsByIds(long orderIds[]) {
        //TODO: Add transaction and logic to delete orders recipes
        StringBuilder whereClause = new StringBuilder();
        whereClause.append(ORDER_ID + " IN (");
        for (int i = 0; i <= orderIds.length; i++) {
            if (i < orderIds.length)
                whereClause.append(orderIds[i] + ",");
            else
                whereClause.append(orderIds[i]);
        }
        whereClause.append(")");
        return this.mDb.delete(ORDER_TABLE, whereClause.toString(), null) > 0; //$NON-NLS-1$
    }

    /**
     * Return a Cursor over the list of all cars in the database
     *
     * @return Cursor over all cars
     */
    public List<Order> getAllItems() {
        List<Order> orders = new ArrayList<Order>();
        String selectQuery = "SELECT  * FROM " + ORDER_TABLE;

        //Log.e(LOG, selectQuery);

        Cursor c = this.mDb.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Order order = cursorToItem(c);
                orders.add(order);
            } while (c.moveToNext());
        }
        return orders;
    }

    public List<Order> getAllItemsByDate(Date date) {
        List<Order> orders = new ArrayList<Order>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        String firstDayOfMonth = sdf.format(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDayOfMonth = sdf.format(calendar.getTime());
        Log.d(TAG, "getAllItemsByDate: FistDay " + firstDayOfMonth + " Last daay "+ lastDayOfMonth);
        String selectQuery = "SELECT  * FROM " + ORDER_TABLE +" WHERE "+ DELIVERY_DATE +" BETWEEN '" + firstDayOfMonth +"' and '"+ lastDayOfMonth +"'";
        Log.d(TAG, "getAllItemsByDate: "+selectQuery);

        //Log.e(LOG, selectQuery);

        Cursor c = this.mDb.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Order order = cursorToItem(c);
                orders.add(order);
            } while (c.moveToNext());
        }
        return orders;
    }

    public Order getItemById(long orderId) throws SQLException {

        String selectQuery = "SELECT  * FROM " + ORDER_TABLE + " WHERE "
                + ORDER_ID + " = " + orderId;

        Cursor c = this.mDb.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Order order = cursorToItem(c);
        return order;
    }

    public boolean updateItem(Order order) {
        //TODO: Add transaction
        boolean isOrderUpdated =true;
        OrderRecipeDBAdapter orderRecipeDBAdapter = new OrderRecipeDBAdapter(mCtx);
        ContentValues values = itemToValues(order, true);
        Log.d(TAG, "updateItem: "+values.toString());
        Log.d(TAG, "updateItem: "+ order.toString());
        this.mDb.beginTransaction();
        try{
            isOrderUpdated = this.mDb.update(ORDER_TABLE, values, ORDER_ID + "=" + order.getOrderId(), null) > 0;
            if(isOrderUpdated){
                try {
                    for (OrderRecipe orderRecipe: order.getOrderRecipes()) {
                        if(!orderRecipeDBAdapter.updateItem(orderRecipe, this.mDb)){
                            isOrderUpdated = false;
                            throw new RuntimeException("Error updating order recipe");
                        }
                    }

//                    this.mDb.execSQL("update tbl_products set quantity = (select tbl_products.quantity - t2.product_used" +
//                            "                                                        from view_product_used t2" +
//                            "                                                        where tbl_products.product_id = t2.product_id" +
//                            "                                                        and t2.order_id = ?)" +
//                            "                    where product_id in (select product_id" +
//                            "                                                        from view_product_used t2" +
//                            "                                                        where tbl_products.product_id = t2.product_id" +
//                            "                                                        and t2.order_id = ?)", order.getOrderId());
                    //TODO: Add query to update the producte when the order is completed
                    /*
                    update tbl_products set quantity = (select tbl_products.quantity - t2.product_used
                                                        from view_product_used t2
                                                        where tbl_products.product_id = t2.product_id
                                                        and t2.order_id = 2)
                    where product_id in (select product_id
                                                        from view_product_used t2
                                                        where tbl_products.product_id = t2.product_id
                                                        and t2.order_id = 2)
                     */
                    this.mDb.setTransactionSuccessful();
                } catch (Exception e) {
                    isOrderUpdated = false;
                    Log.e(TAG, "updateItem: Eror",e );
                }
            }else{
                Log.d(TAG, "updateItem: The order was not updated correctly");
            }
        }
        catch (Exception e){
            isOrderUpdated = false;
            Log.e(TAG, "updateItem: Error",e );
        }
        finally {
            this.mDb.endTransaction();
            return isOrderUpdated;
        }
    }

    private ContentValues itemToValues(Order order, boolean isUpdate) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(ORDER_NAME, order.getOrderName());
        initialValues.put(CLIENT_NAME, order.getClientName());
        initialValues.put(CLIENT_NUMBER, order.getClientNumber());
        initialValues.put(DELIVERY_DATE, sdf.format(order.getDeliveryDate()));
        initialValues.put(ORDER_STATUS,order.getOrderStatus());
        initialValues.put(TOTAL, order.getTotal());
        initialValues.put(SELL_PRICE, order.getSellPrice());
        initialValues.put(UPDATED_ON, sdf.format(new Date(0)));
        if (isUpdate) {
            initialValues.put(CREATED_ON, sdf.format(new Date(0)));
        }
        return initialValues;
    }

    private Order cursorToItem(Cursor c) {
        String[] strings = mCtx.getResources().getStringArray(R.array.orderStates);
        OrderRecipeDBAdapter orderRecipeDBAdapter = new OrderRecipeDBAdapter(mCtx);
        Order order = new Order();
        order.setOrderId(c.getInt(c.getColumnIndex(ORDER_ID)));
        order.setOrderName(c.getString(c.getColumnIndex(ORDER_NAME)));
        order.setClientName(c.getString(c.getColumnIndex(CLIENT_NAME)));
        order.setClientNumber(c.getString(c.getColumnIndex(CLIENT_NUMBER)));
        try {
            order.setDeliveryDate(sdf.parse(c.getString(c.getColumnIndex(DELIVERY_DATE))));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        order.setTotal(c.getFloat(c.getColumnIndex(TOTAL)));
        order.setSellPrice(c.getFloat(c.getColumnIndex(SELL_PRICE)));
        order.setOrderStatus(c.getString(c.getColumnIndex(ORDER_STATUS)));
        //TODO: Add colors to the background
        if(order.getOrderStatus().equals(strings[1])){ //Open State
            order.setColorStatus(ResourcesCompat.getColor(mCtx.getResources(), R.color.stateOpen, null));
        }else if (order.getOrderStatus().equals(strings[2])){ //Working State
            order.setColorStatus(ResourcesCompat.getColor(mCtx.getResources(), R.color.stateWorking, null));
        }else if (order.getOrderStatus().equals(strings[3])){ //Done State
            order.setColorStatus(ResourcesCompat.getColor(mCtx.getResources(), R.color.stateDone, null));
        }else if (order.getOrderStatus().equals(strings[4])){ //Cancel State
            order.setColorStatus(ResourcesCompat.getColor(mCtx.getResources(), R.color.stateCancel, null));
        }else if (order.getOrderStatus().equals(strings[5])){ //Wait State
            order.setColorStatus(ResourcesCompat.getColor(mCtx.getResources(), R.color.stateWait, null));
        }else{ //Default
            order.setColorStatus(ResourcesCompat.getColor(mCtx.getResources(), R.color.itemListBackgroundPrimary, null));
        }
        try {
            orderRecipeDBAdapter.open();
            List<OrderRecipe> orderRecipes = orderRecipeDBAdapter.getItemsByOrderId(order.getOrderId());
            order.setOrderRecipes(orderRecipes);
            orderRecipeDBAdapter.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;

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
