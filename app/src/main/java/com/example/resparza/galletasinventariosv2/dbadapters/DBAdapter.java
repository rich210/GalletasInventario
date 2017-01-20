package com.example.resparza.galletasinventariosv2.dbadapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.StringBuilderPrinter;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by resparza on 27/01/2016.
 */
public class DBAdapter {

    public static final String DATABASE_NAME = "cookieManager"; //$NON-NLS-1$
    public static final String TAG = "DBAdapter";

    public static final int DATABASE_VERSION = 3;


    private static final String CREATE_TABLE_TBL_PRODUCTS =
            "create table "+ ProductDBAdapter.PRODUCT_TABLE+" ("
                    + ProductDBAdapter.PRODUCT_ID + " integer primary key autoincrement, " //$NON-NLS-1$
                    + ProductDBAdapter.PRODUCT_NAME + " TEXT,"
                    + ProductDBAdapter.MEASURE_TYPE_ID + " INTEGER,"
                    + ProductDBAdapter.QUANTITY + " INTEGER,"
                    + ProductDBAdapter.COST_PER_UNIT + " INTEGER,"
                    + ProductDBAdapter.PRODUCT_MIN + " INTEGER,"
                    + ProductDBAdapter.CREATED_ON + " TEXT,"
                    + ProductDBAdapter.UPDATED_ON + " TEXT,"
                    + "FOREIGN KEY (" + ProductDBAdapter.MEASURE_TYPE_ID + ") REFERENCES "+ MeasureTypeDBAdapter.MEASURE_TABLE +"("+MeasureTypeDBAdapter.MEASURE_TYPE_ID +"));";

    private static final String CREATE_TABLE_TBL_RECIPE =
            "create table "+ RecipeDBAdapter.RECIPE_TABLE +" ("
                    + RecipeDBAdapter.RECIPE_ID + " integer primary key autoincrement, " //$NON-NLS-1$
                    + RecipeDBAdapter.RECIPE_NAME +" TEXT," //$NON-NLS-1$
                    //+ RecipeDBAdapter.MEASURE_TYPE_ID + " INTEGER," //$NON-NLS-1$
                    + RecipeDBAdapter.QUANTITY +" INTEGER," //$NON-NLS-1$
                    + RecipeDBAdapter.RECIPE_COST +" REAL," //$NON-NLS-1$
                    + RecipeDBAdapter.CREATED_ON +" TEXT," //$NON-NLS-1$
                    + RecipeDBAdapter.UPDATED_ON +" TEXT," //$NON-NLS-1$
                    + RecipeDBAdapter.IMAGE_PATH +" TEXT);";


    private static final String CREATE_TABLE_TBL_ORDER =
            "create table "+ OrderDBAdapter.ORDER_TABLE +" ("
                    + OrderDBAdapter.ORDER_ID +" integer primary key autoincrement, "
                    + OrderDBAdapter.ORDER_NAME+" TEXT," //$NON-NLS-1$
                    + OrderDBAdapter.CLIENT_NAME+" TEXT," //$NON-NLS-1$
                    + OrderDBAdapter.CLIENT_NUMBER+" TEXT," //$NON-NLS-1$
                    + OrderDBAdapter.DELIVERY_DATE+" TEXT," //$NON-NLS-1$
                    + OrderDBAdapter.SELL_PRICE+" REAL," //$NON-NLS-1$
                    + OrderDBAdapter.TOTAL+" REAL," //$NON-NLS-1$
                    + OrderDBAdapter.CREATED_ON+" TEXT," //$NON-NLS-1$
                    + OrderDBAdapter.UPDATED_ON+" TEXT);"; //$NON-NLS-1$

    private static final String CREATE_TABLE_TBL_MEASURE_TYPE =
            "create table "+ MeasureTypeDBAdapter.MEASURE_TABLE +" ("
                    + MeasureTypeDBAdapter.MEASURE_TYPE_ID +" integer primary key autoincrement, "
                    + MeasureTypeDBAdapter.MEASURE_NAME+" TEXT," //$NON-NLS-1$
                    + MeasureTypeDBAdapter.MEASURE_SYMBOL + " TEXT,"
                    + MeasureTypeDBAdapter.MEASURE_EQUIVALENCY_ID +" integer,"
                    + MeasureTypeDBAdapter.QUANTITY_EQUIVALENCY + " integer,"
                    + MeasureTypeDBAdapter.IS_MEASURE_BASE + " integer,"
                    + MeasureTypeDBAdapter.CREATED_ON+" TEXT," //$NON-NLS-1$
                    + MeasureTypeDBAdapter.UPDATED_ON+" TEXT);"; //$NON-NLS-1$

    private static final String CREATE_TABLE_TBL_ORDER_RECIPE =
            "create table "+ OrderRecipeDBAdapter.ORDER_RECIPE_TABLE +" ("
                    + OrderRecipeDBAdapter.ORDER_ID+" INTEGER," //$NON-NLS-1$
                    + OrderRecipeDBAdapter.RECIPE_ID+" INTEGER," //$NON-NLS-1$
                    + OrderRecipeDBAdapter.ORDER_QUANTITY+" INTEGER," //$NON-NLS-1$
                    + OrderRecipeDBAdapter.PRICE_PER_UNIT+" REAL," //$NON-NLS-1$
                    + OrderRecipeDBAdapter.TOTAL+" REAL," //$NON-NLS-1$
                    + OrderRecipeDBAdapter.CREATED_ON+" TEXT," //$NON-NLS-1$
                    + OrderRecipeDBAdapter.UPDATED_ON+" TEXT," //$NON-NLS-1$
                    + "FOREIGN KEY (" + OrderRecipeDBAdapter.ORDER_ID + ") REFERENCES "+OrderDBAdapter.ORDER_TABLE+"("+OrderDBAdapter.ORDER_ID+"),"
                    + "FOREIGN KEY (" + OrderRecipeDBAdapter.RECIPE_ID + ") REFERENCES "+RecipeDBAdapter.RECIPE_TABLE+"("+RecipeDBAdapter.RECIPE_ID+"));";

    private static final String CREATE_TABLE_TBL_RECIPE_PRODUCT =
            "create table "+ RecipeProductDBAdapter.RECIPE_PRODUCT_TABLE +" ("
                    + RecipeProductDBAdapter.PRODUCT_ID+" INTEGER," //$NON-NLS-1$
                    + RecipeProductDBAdapter.RECIPE_ID+" INTEGER," //$NON-NLS-1$
                    + RecipeProductDBAdapter.MEASUREMENT_TYPE_ID+" INTEGER," //$NON-NLS-1$
                    + RecipeProductDBAdapter.PRODUCT_QUANTITY+" INTEGER," //$NON-NLS-1$
                    + RecipeProductDBAdapter.CREATED_ON+" TEXT," //$NON-NLS-1$
                    + RecipeProductDBAdapter.UPDATED_ON+" TEXT," //$NON-NLS-1$
                    + "FOREIGN KEY (" + RecipeProductDBAdapter.PRODUCT_ID + ") REFERENCES "+ProductDBAdapter.PRODUCT_TABLE+"("+ProductDBAdapter.PRODUCT_ID+"),"
                    + "FOREIGN KEY (" + RecipeProductDBAdapter.MEASUREMENT_TYPE_ID + ") REFERENCES "+MeasureTypeDBAdapter.MEASURE_TABLE+"("+MeasureTypeDBAdapter.MEASURE_TYPE_ID+"),"
                    + "FOREIGN KEY (" + RecipeProductDBAdapter.RECIPE_ID + ") REFERENCES "+RecipeDBAdapter.RECIPE_TABLE+"("+RecipeDBAdapter.RECIPE_ID+"));";


    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    /**
     * Constructor
     * @param ctx
     */
    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        this.DBHelper = new DatabaseHelper(this.context);
    }

    /**
     * open the db
     *
     * @return this
     * @throws SQLException return type: DBAdapter
     */
    public DBAdapter open() throws SQLException {
        this.db = this.DBHelper.getWritableDatabase();
        return this;
    }

    public int getDataBaseVersion (){
        return db.getVersion();
    }

    /**
     * close the db
     * return type: void
     */
    public void close() {
        this.DBHelper.close();
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(0));
            db.execSQL(CREATE_TABLE_TBL_MEASURE_TYPE);
            db.execSQL(CREATE_TABLE_TBL_ORDER);
            db.execSQL(CREATE_TABLE_TBL_RECIPE);
            db.execSQL(CREATE_TABLE_TBL_PRODUCTS);
            db.execSQL(CREATE_TABLE_TBL_ORDER_RECIPE);
            db.execSQL(CREATE_TABLE_TBL_RECIPE_PRODUCT);

            //Inser default values for measure table
            db.execSQL("insert into "+MeasureTypeDBAdapter.MEASURE_TABLE + "("
                    + MeasureTypeDBAdapter.MEASURE_NAME+" ," //$NON-NLS-1$
                    + MeasureTypeDBAdapter.MEASURE_SYMBOL + ","
                    //+ MeasureTypeDBAdapter.MEASURE_EQUIVALENCY_ID +" ,"
                    //+ MeasureTypeDBAdapter.QUANTITY_EQUIVALENCY + " ,"
                    + MeasureTypeDBAdapter.IS_MEASURE_BASE + " ,"
                    + MeasureTypeDBAdapter.CREATED_ON+" ," //$NON-NLS-1$
                    + MeasureTypeDBAdapter.UPDATED_ON+") values "
                    + "('Gramos', 'gr', 1, '"+ date +"','"+date+"' ),"
                    + "('Mililitros', 'ml', 1, '"+ date +"','"+date+"' );");

            db.execSQL("insert into "+MeasureTypeDBAdapter.MEASURE_TABLE + "("
                    + MeasureTypeDBAdapter.MEASURE_NAME+" ," //$NON-NLS-1$
                    + MeasureTypeDBAdapter.MEASURE_SYMBOL + ","
                    + MeasureTypeDBAdapter.MEASURE_EQUIVALENCY_ID +" ,"
                    + MeasureTypeDBAdapter.QUANTITY_EQUIVALENCY + " ,"
                    + MeasureTypeDBAdapter.IS_MEASURE_BASE + " ,"
                    + MeasureTypeDBAdapter.CREATED_ON+" ," //$NON-NLS-1$
                    + MeasureTypeDBAdapter.UPDATED_ON+") values "
                    + "('Kilogramos', 'kg', 1, 1000, 0,'"+ date +"','"+date+"'),"
                    + "('Litros','l', 2, 1000, 0,'"+ date +"','"+date+"');");


        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            db.execSQL("drop table if exists "+ MeasureTypeDBAdapter.MEASURE_TABLE);
            db.execSQL("drop table if exists "+ OrderDBAdapter.ORDER_TABLE);
            db.execSQL("drop table if exists "+ RecipeDBAdapter.RECIPE_TABLE);
            db.execSQL("drop table if exists "+ ProductDBAdapter.PRODUCT_TABLE);
            db.execSQL("drop table if exists "+ OrderRecipeDBAdapter.ORDER_RECIPE_TABLE);
            db.execSQL("drop table if exists "+ RecipeProductDBAdapter.RECIPE_PRODUCT_TABLE);

            onCreate(db);
            // Adding any table mods to this guy here
        }
    }
}
