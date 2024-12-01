package com.example.playinkodi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;
    private static final String DATABASE_NAME = "MyAppDatabase.db";
    private static final int DATABASE_VERSION = 1;

    // SQL command to create a table
    private static final String TABLES_CREATE =
        "CREATE TABLE Bookmarks (id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT);";

    private static final String TABLES_DROP =
        "DROP TABLE IF EXISTS Bookmarks;";

    public MyDatabaseHelper(Context mContext) {
        super(mContext, DATABASE_NAME, null, DATABASE_VERSION);
//        Log.d("kodilog", String.valueOf(mContext));
        this.mContext = mContext;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLES_CREATE); // Create the Users table
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TABLES_DROP); // If upgrading, drop the old table
        onCreate(db); // Create a new one
    }

    public void addBookmark(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("url", url);
        db.insert("Bookmarks", null, values);
        db.close();
    }
}
