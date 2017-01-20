package com.example.resparza.galletasinventariosv2.dbadapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.resparza.galletasinventariosv2.models.Order;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by resparza on 05/02/2016.
 */
public class OrderDBAdapter {
    public static final String ORDER_ID = "order_id";
    public static final String ORDER_NAME = "order_name";
    public static final String CLIENT_NAME = "client_name";
    public static final String CLIENT_NUMBER = "client_number";
    public static final String DELIVERY_DATE = "delivery_date";
    public static final String TOTAL = "total";
    public static final String SELL_PRICE = "sell_price";
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
        ContentValues initialValues = itemToValues(order, false);
        return this.mDb.insert(ORDER_TABLE, null, initialValues);
    }

    public boolean deleteItemById(long orderId) {

        return this.mDb.delete(ORDER_TABLE, ORDER_ID + "=" + orderId, null) > 0; //$NON-NLS-1$
    }

    public boolean deleteItemsByIds(long orderIds[]) {
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

        ContentValues values = itemToValues(order, true);

        return this.mDb.update(ORDER_TABLE, values, ORDER_ID + "=" + order.getOrderId(), null) > 0;
    }

    private ContentValues itemToValues(Order order, boolean isUpdate) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(ORDER_NAME, order.getOrderName());
        initialValues.put(CLIENT_NAME, order.getClientName());
        initialValues.put(CLIENT_NUMBER, order.getClientNumber());
        initialValues.put(DELIVERY_DATE, sdf.format(order.getDeliveryDate()));
        //initialValues.put(GAIN, order.getGain());
        initialValues.put(TOTAL, order.getTotal());
        initialValues.put(SELL_PRICE, order.getSellPrice());
        initialValues.put(UPDATED_ON, sdf.format(new Date(0)));
        if (isUpdate) {
            initialValues.put(CREATED_ON, sdf.format(new Date(0)));
        }
        return initialValues;
    }

    private Order cursorToItem(Cursor c) {

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
