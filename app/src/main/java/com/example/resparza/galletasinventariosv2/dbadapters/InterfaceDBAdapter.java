package com.example.resparza.galletasinventariosv2.dbadapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by resparza on 14/03/2016.
 */
public interface InterfaceDBAdapter {


    void open();

    void close();
}
