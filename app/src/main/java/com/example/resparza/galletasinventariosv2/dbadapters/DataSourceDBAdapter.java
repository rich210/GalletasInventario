package com.example.resparza.galletasinventariosv2.dbadapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by resparza on 14/03/2016.
 */
public interface DataSourceDBAdapter<T extends Object> {


    void open() throws SQLException;

    void close();

    long insertItem(T item);

    boolean deleteItemById(long id);

    boolean deleteItemsByIds (long ids []);

    List getAllItems();

    Object getItemById (long id) throws SQLException;

    boolean updateItem(T item);

}
