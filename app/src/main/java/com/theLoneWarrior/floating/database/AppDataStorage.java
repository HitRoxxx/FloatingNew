package com.theLoneWarrior.floating.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by HitRo on 12-05-2017.
 */

public class AppDataStorage extends SQLiteOpenHelper {
    private static final String DB_NAME = "appData";
    private static final int DB_VERSION = 1;

    public AppDataStorage(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE APP_DATA (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "NAME TEXT, "
                + "PACKAGE TEXT, "
                + "IMAGE_RESOURCE TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
