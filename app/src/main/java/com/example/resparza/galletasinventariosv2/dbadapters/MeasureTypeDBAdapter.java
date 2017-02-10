package com.example.resparza.galletasinventariosv2.dbadapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.resparza.galletasinventariosv2.models.MeasureType;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by resparza on 08/02/2016.
 */
public class MeasureTypeDBAdapter implements DataSourceDBAdapter {

    public static final String TAG = "MeasureTypeDBAdapter";

    public static final String MEASURE_TYPE_ID = "measure_type_id";
    public static final String MEASURE_NAME = "measure_name";
    public static final String MEASURE_SYMBOL = "measure_symbol";
    public static final String MEASURE_EQUIVALENCY_ID = "measure_equivalency_id";
    public static final String QUANTITY_EQUIVALENCY = "quantity_equivalency";
    public static final String IS_MEASURE_BASE = "is_measure_base";
    public static final String CREATED_ON = "created";
    public static final String UPDATED_ON = "updated";

    public static final String MEASURE_TABLE = "tbl_measure_types";
    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public MeasureTypeDBAdapter(Context ctx) {
        this.mCtx = ctx;
        this.mDbHelper = new DatabaseHelper(this.mCtx);
        try{
            open();
        }catch (SQLException e){
            Log.e(TAG, "SQLException on oppening database " + e.getMessage());
        }
    }

    public void open() throws SQLException {
        this.mDb = this.mDbHelper.getWritableDatabase();
    }

    public void close() {
        this.mDbHelper.close();
    }

    public long insertItem(Object item) {
        ContentValues initialValues = itemToValues(item, false);
        return this.mDb.insert(MEASURE_TABLE, null, initialValues);
    }
    /*
    public long insertItem(MeasureType item) {
        ContentValues initialValues = itemToValues(item, false);
        return this.mDb.insert(MEASURE_TABLE, null, initialValues);
    }
    */

    public boolean deleteItemById(long measureTypeId) {

        return this.mDb.delete(MEASURE_TABLE, MEASURE_TYPE_ID + "=" + measureTypeId, null) > 0; //$NON-NLS-1$
    }

    public boolean deleteItemsByIds(long measureTypeId[]) {
        StringBuilder whereClause = new StringBuilder();
        whereClause.append(MEASURE_TYPE_ID+ " IN (");
        Log.d(TAG, "Size: "+measureTypeId.length);
        for (int i = 0; i<= measureTypeId.length - 1; i++){
            if(i<measureTypeId.length - 1)
                whereClause.append(measureTypeId[i]+",");
            else
                whereClause.append(measureTypeId[i]);

            Log.d(TAG, "Measure Id: "+measureTypeId[i]);
        }
        whereClause.append(")");
        Log.d(TAG, "Query: "+whereClause.toString());
        return this.mDb.delete(MEASURE_TABLE, whereClause.toString() , null) > 0; //$NON-NLS-1$
    }

    /**
     * Return a Cursor over the list of all cars in the database
     *
     * @return Cursor over all cars
     */
    public List getAllItems() {
        List<MeasureType> measureTypes = new ArrayList<MeasureType>();
        String selectQuery = "SELECT  * FROM " + MEASURE_TABLE;

        //Log.e(LOG, selectQuery);

        Cursor c = this.mDb.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                MeasureType measureType = cursorToItem(c);
                measureTypes.add(measureType);
            } while (c.moveToNext());
        }
        return measureTypes;
    }

    public MeasureType getItemById(long measureTypeId) throws SQLException {

        String selectQuery = "SELECT  * FROM " + MEASURE_TABLE + " WHERE "
                + MEASURE_TYPE_ID + " = " + measureTypeId;

        Cursor c = this.mDb.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        MeasureType measureType = cursorToItem(c);

        return measureType;
    }

    public boolean updateItem(Object item) {
        MeasureType measureType = (MeasureType)item;
        ContentValues values = itemToValues(item, true);
        return this.mDb.update(MEASURE_TABLE, values, MEASURE_TYPE_ID + "=" + measureType.getMeasureTypeId(), null) >0;
    }

    private MeasureType cursorToItem(Cursor cursor) {

        MeasureType measureType = new MeasureType();
        measureType.setMeasureTypeId(cursor.getInt(cursor.getColumnIndex(MEASURE_TYPE_ID)));
        measureType.setMeasureTypeName(cursor.getString(cursor.getColumnIndex(MEASURE_NAME)));
        measureType.setMeasureSymbol(cursor.getString(cursor.getColumnIndex(MEASURE_SYMBOL)));
        measureType.setMeasureEquivalencyId(cursor.getLong(cursor.getColumnIndex(MEASURE_EQUIVALENCY_ID)));
        measureType.setQuantityEquivalency(cursor.getInt(cursor.getColumnIndex(QUANTITY_EQUIVALENCY)));
        measureType.setMeasureBase(cursor.getInt(cursor.getColumnIndex(IS_MEASURE_BASE))!=0);

        return measureType;

    }

    private ContentValues itemToValues(Object item, boolean isUpdate) {
        MeasureType measureType = (MeasureType)item;
        ContentValues values = new ContentValues();
        values.put(MEASURE_NAME, measureType.getMeasureTypeName());
        values.put(MEASURE_SYMBOL, measureType.getMeasureSymbol());
        values.put(MEASURE_EQUIVALENCY_ID,measureType.getMeasureEquivalencyId());
        values.put(QUANTITY_EQUIVALENCY,measureType.getQuantityEquivalency());
        values.put(IS_MEASURE_BASE,(measureType.isMeasureBase())?1:0);
        values.put(UPDATED_ON, sdf.format(new Date(0)));
        if (!isUpdate) {
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

    public List<MeasureType> getAllMeasuresBase() {
        List<MeasureType> measureTypes = new ArrayList<MeasureType>();
        String selectQuery = "SELECT  * FROM " + MEASURE_TABLE + " WHERE " + IS_MEASURE_BASE + " = 1";

        //Log.e(LOG, selectQuery);

        Cursor c = this.mDb.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                MeasureType measureType = cursorToItem(c);
                measureTypes.add(measureType);
            } while (c.moveToNext());
        }
        return measureTypes;
    }

    public List<MeasureType> getAllMeasureTypesByMeasureBase(Long id){
        List<MeasureType> measureTypes = new ArrayList<MeasureType>();
        String selectQuery = "SELECT  * FROM " + MEASURE_TABLE + " WHERE " + MEASURE_EQUIVALENCY_ID + " = " + id + " OR " + MEASURE_TYPE_ID + " = " + id;
        Cursor c = this.mDb.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                MeasureType measureType = cursorToItem(c);
                measureTypes.add(measureType);
            } while (c.moveToNext());
        }
        return measureTypes;
    }
}
