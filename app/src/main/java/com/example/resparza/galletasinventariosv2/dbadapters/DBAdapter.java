package com.example.resparza.galletasinventariosv2.dbadapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.StringBuilderPrinter;

import com.example.resparza.galletasinventariosv2.models.OrderRecipe;
import com.example.resparza.galletasinventariosv2.models.Recipe;

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

    public static final int DATABASE_VERSION = 9;


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
                    + RecipeDBAdapter.RECIPE_INSTRUCTIONS + " TEXT,"
                    + RecipeDBAdapter.QUANTITY +" INTEGER," //$NON-NLS-1$
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
                    + OrderDBAdapter.ORDER_STATUS+" TEXT," //$NON-NLS-1$
                    + OrderDBAdapter.SELL_PRICE+" REAL," //$NON-NLS-1$
                    + OrderDBAdapter.TOTAL+" REAL," //$NON-NLS-1$
                    + OrderDBAdapter.EVENT_ID+" REAL,"
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
                    + OrderRecipeDBAdapter.PRICE_SELL+" REAL," //$NON-NLS-1$
                    + OrderRecipeDBAdapter.SELL_AS+" TEXT," //$NON-NLS-1$
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

    private static final String CREATE_VIEW_PRODUCT_NEEDED =
            "CREATE VIEW view_product_needed AS " +
                    "SELECT t1.*," +
                    "SUM( (t4."+OrderRecipeDBAdapter.ORDER_QUANTITY+" * t2."+ RecipeProductDBAdapter.PRODUCT_QUANTITY+") - t1."+ProductDBAdapter.QUANTITY+") as product_needed " +
                    "FROM "+ ProductDBAdapter.PRODUCT_TABLE +" t1 " +
                    "JOIN "+ RecipeProductDBAdapter.RECIPE_PRODUCT_TABLE +" t2 on t1.product_id = t2.product_id " +
                    "JOIN "+ RecipeDBAdapter.RECIPE_TABLE +" t3 on t2.recipe_id = t3.recipe_id " +
                    "JOIN "+ OrderRecipeDBAdapter.ORDER_RECIPE_TABLE +" t4 on t2.recipe_id = t4.recipe_id " +
                    "JOIN "+ OrderDBAdapter.ORDER_TABLE +" t5 on t4.order_id = t5.order_id " +
                    "WHERE t5."+ OrderDBAdapter.DELIVERY_DATE+" > DATE('now') " +
                    "AND t5."+OrderDBAdapter.ORDER_STATUS+" NOT IN (\"Trabajando\",\"Entregado\") " +
                    "AND (t4."+OrderRecipeDBAdapter.ORDER_QUANTITY+" * t2."+ RecipeProductDBAdapter.PRODUCT_QUANTITY+") > t1."+ ProductDBAdapter.QUANTITY + " "+
                    "GROUP BY 1,2,3,4,5,7,8 " +
                    "UNION " +
                    "SELECT t1.*, " +
                    "0 AS product_needed " +
                    "FROM "+ ProductDBAdapter.PRODUCT_TABLE +" t1 " +
                    "WHERE t1."+ ProductDBAdapter.QUANTITY +" < t1."+ ProductDBAdapter.PRODUCT_MIN;


    private static final String CREATE_VIEW_PRODUCT_USED =
            "CREATE VIEW view_product_used AS " +
            "SELECT t1.*," +
            "(t4."+OrderRecipeDBAdapter.ORDER_QUANTITY+" * t2."+ RecipeProductDBAdapter.PRODUCT_QUANTITY+") AS product_used, " +
            "t4."+ OrderRecipeDBAdapter.ORDER_ID+" " +
            "FROM "+ ProductDBAdapter.PRODUCT_TABLE +" t1 " +
            "JOIN "+ RecipeProductDBAdapter.RECIPE_PRODUCT_TABLE +" t2 ON t1.product_id = t2.product_id " +
            "JOIN "+ RecipeDBAdapter.RECIPE_TABLE +" t3 ON t2.recipe_id = t3.recipe_id " +
            "JOIN "+ OrderRecipeDBAdapter.ORDER_RECIPE_TABLE +" t4 ON t2.recipe_id = t4.recipe_id " +
            "JOIN "+ OrderDBAdapter.ORDER_TABLE +" t5 ON t4.order_id = t5.order_id " +
            "WHERE t5."+ OrderDBAdapter.DELIVERY_DATE+" > date('now') " +
            "GROUP BY t1.product_id,t1.product_name,t1.measure_type_id,t1.quantity,t1.cost_per_unit," +
                    "t1.product_min,t1.created,t1.updated,t4.order_id";



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
            db.execSQL(CREATE_VIEW_PRODUCT_NEEDED);
            db.execSQL(CREATE_VIEW_PRODUCT_USED);

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
                    + "('Mililitros', 'ml', 1, '"+ date +"','"+date+"' ),"
                    + "('Piezas', 'pz', 1, '"+ date +"','"+date+"' )");

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
            //TODO: Delete this block on production
            db.execSQL("insert into "+ProductDBAdapter.PRODUCT_TABLE +"("
                    + ProductDBAdapter.PRODUCT_NAME + ", "
                    + ProductDBAdapter.MEASURE_TYPE_ID + ", "
                    + ProductDBAdapter.QUANTITY + ", "
                    + ProductDBAdapter.COST_PER_UNIT + ", "
                    + ProductDBAdapter.PRODUCT_MIN + ", "
                    + ProductDBAdapter.CREATED_ON + ", "
                    + ProductDBAdapter.UPDATED_ON + ") values "
                    + "('Harina',1,500,0.1,200,'"+date+"','"+date+"'),"
                    + "('Leche',2,500,0.1,200,'"+date+"','"+date+"'),"
                    + "('Vainilla',2,500,0.1,200,'"+date+"','"+date+"'),"
                    + "('Chocolate',1,500,0.1,200,'"+date+"','"+date+"');");
            db.execSQL("insert into "+ RecipeDBAdapter.RECIPE_TABLE +"("
                    + RecipeDBAdapter.RECIPE_NAME + ", "
                    + RecipeDBAdapter.QUANTITY + ", "
                    + RecipeDBAdapter.IMAGE_PATH + ", "
                    + RecipeDBAdapter.RECIPE_INSTRUCTIONS + ", "
                    + RecipeDBAdapter.CREATED_ON + ", "
                    + RecipeDBAdapter.UPDATED_ON + ") values "
                    + "('Pastel',20,null,'na na','"+date+"','"+date+"'),"
                    + "('Galletas',20,null,null,'"+date+"','"+date+"'),"
                    + "('Muffins',5,null,'bake','"+date+"','"+date+"');");
            db.execSQL("insert into "+ RecipeProductDBAdapter.RECIPE_PRODUCT_TABLE +"("
                    + RecipeProductDBAdapter.RECIPE_ID + ", "
                    + RecipeProductDBAdapter.PRODUCT_ID + ", "
                    + RecipeProductDBAdapter.PRODUCT_QUANTITY + ", "
                    + RecipeProductDBAdapter.MEASUREMENT_TYPE_ID + ", "
                    + RecipeProductDBAdapter.CREATED_ON + ", "
                    + RecipeProductDBAdapter.UPDATED_ON + ") values "
                    + "(1,1,100,1,'"+date+"','"+date+"'),"
                    + "(1,2,100,2,'"+date+"','"+date+"'),"
                    + "(1,3,50,2,'"+date+"','"+date+"'),"
                    + "(2,1,90,1,'"+date+"','"+date+"'),"
                    + "(2,2,50,2,'"+date+"','"+date+"'),"
                    + "(2,3,30,2,'"+date+"','"+date+"'),"
                    + "(3,1,10,1,'"+date+"','"+date+"'),"
                    + "(4,2,40,2,'"+date+"','"+date+"'),"
                    + "(5,4,30,2,'"+date+"','"+date+"');");


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
            db.execSQL("drop view if exists view_product_needed");
            db.execSQL("drop view if exists view_product_used");

            onCreate(db);
            // Adding any table mods to this guy here
        }
    }
}
