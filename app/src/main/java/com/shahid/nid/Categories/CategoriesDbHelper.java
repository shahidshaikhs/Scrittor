package com.shahid.nid.Categories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

/**
 * Created by shahi on 10/9/2017.
 */

public class CategoriesDbHelper extends SQLiteOpenHelper {

    /**
     * If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 2; //was 1
    private static final String DATABASE_NAME = "CategoriesHelper.db";

    /**
     * Constructor
     */
    public CategoriesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * String to create a table
     */
    private static final String SQL_CREATE_MAIN_TABLE = "CREATE TABLE " + CategoriesNotesContract.CategoriesContract.TABLE_NAME + "(" +
            CategoriesNotesContract.CategoriesContract._ID + " INTEGER PRIMARY KEY," +
            CategoriesNotesContract.CategoriesContract.COLUMN_DESCRIPTION_CATEGORY + " TEXT," +
            CategoriesNotesContract.CategoriesContract.COLUMN_COLOR + " TEXT," +
            CategoriesNotesContract.CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID + " TEXT," +
            CategoriesNotesContract.CategoriesContract.COLUMN_NAME_CATEGORY + " TEXT)";

    /**
     * String to delete the table
     */
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CategoriesNotesContract.CategoriesContract.TABLE_NAME;


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_MAIN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + CategoriesNotesContract.CategoriesContract.TABLE_NAME
                    + " ADD COLUMN " + CategoriesNotesContract.CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID + " TEXT");
            try (Cursor rows = db.query(CategoriesNotesContract.CategoriesContract.TABLE_NAME,
                    new String[]{"_id"}, null, null, null, null, null)) {
                while (rows.moveToNext()) {
                    ContentValues values = new ContentValues();
                    values.put(CategoriesNotesContract.CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID,
                            java.util.UUID.randomUUID().toString());
                    db.update(CategoriesNotesContract.CategoriesContract.TABLE_NAME, values, "_id = ?",
                            new String[]{rows.getString(0)});
                }
            }
        }
    }
}
