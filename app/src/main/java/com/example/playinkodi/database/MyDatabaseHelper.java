package com.example.playinkodi.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

    public void updateBookmark(int id, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("url", url);
        String whereClause = "id = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        db.update("Bookmarks", values, whereClause, whereArgs);
        db.close();
    }

    public boolean isBookmarkExist(String url) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "url = ?";
        String[] selectionArgs = { url };

        Cursor cursor = db.query(
            "Bookmarks",      // Table name
            new String[]{"url"},   // Columns to return
            selection,             // WHERE clause
            selectionArgs,         // WHERE clause arguments
            null,                  // GROUP BY
            null,                  // HAVING
            null                   // ORDER BY
        );

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    @SuppressLint("Range")
    public int getUpdatableBookmark(String url) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "url LIKE ?";
        String[] selectionArgs = { "%" + url + "%" };

        Cursor cursor = db.query(
            "Bookmarks",      // Table name
            new String[]{"id", "url"},   // Columns to return
            selection,             // WHERE clause
            selectionArgs,         // WHERE clause arguments
            null,                  // GROUP BY
            null,                  // HAVING
            null                   // ORDER BY
        );

        if (cursor.getCount() == 1) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex("id")); // Replace "name" with your column name
            }
        }

        return 0;
    }

    public void deleteBookmark(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Bookmarks WHERE id = " + id);
    }
}
