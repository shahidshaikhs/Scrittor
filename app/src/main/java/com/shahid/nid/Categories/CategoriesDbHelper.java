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
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        backupDb(sqLiteDatabase);
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
        restoreDb(sqLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    /**
     * Creates a backup table and Copies data from main Notes table to this backup table.
     * @param sqLiteDatabase
     */
    private void backupDb(SQLiteDatabase sqLiteDatabase){
        String query = "INSERT INTO " + CategoriesNotesContract.CategoriesContract.CATEGORIES_BACKUP_TABLE_NAME + " SELECT * FROM " + CategoriesNotesContract.CategoriesContract.TABLE_NAME;
        final String SQL_CREATE_BACKUP_TABLE = "CREATE TABLE " + CategoriesNotesContract.CategoriesContract.CATEGORIES_BACKUP_TABLE_NAME + "(" +
                CategoriesNotesContract.CategoriesContract._ID + " INTEGER PRIMARY KEY," +
                CategoriesNotesContract.CategoriesContract.COLUMN_DESCRIPTION_CATEGORY + " TEXT," +
                CategoriesNotesContract.CategoriesContract.COLUMN_COLOR + " TEXT," +
                CategoriesNotesContract.CategoriesContract.COLUMN_NAME_CATEGORY + " TEXT)";

        sqLiteDatabase.execSQL(SQL_CREATE_BACKUP_TABLE);
        sqLiteDatabase.execSQL(query);
    }


    /**
     * Restore data from backup Table to newly created Main Table with new schema .. (Change it accordingly in future table schema upgrades
     * @param sqLiteDatabase
     */
    private void restoreDb(SQLiteDatabase sqLiteDatabase){
        // TODO get data from backup using sqlitedb and use cursor to put data
        Cursor cursor = sqLiteDatabase.query(
                CategoriesNotesContract.CategoriesContract.CATEGORIES_BACKUP_TABLE_NAME,                     // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        long i = 1;
        while (cursor.moveToNext()) {
            String title = cursor.getString(
                    cursor.getColumnIndexOrThrow(CategoriesNotesContract.CategoriesContract.COLUMN_NAME_CATEGORY));

            String description = cursor.getString(cursor.getColumnIndexOrThrow(CategoriesNotesContract.CategoriesContract.COLUMN_DESCRIPTION_CATEGORY));

            String color = cursor.getString(cursor.getColumnIndexOrThrow(CategoriesNotesContract.CategoriesContract.COLUMN_COLOR));



            ContentValues values = new ContentValues();
            values.put(CategoriesNotesContract.CategoriesContract.COLUMN_NAME_CATEGORY, title);
            values.put(CategoriesNotesContract.CategoriesContract.COLUMN_DESCRIPTION_CATEGORY, description);
            values.put(CategoriesNotesContract.CategoriesContract.COLUMN_COLOR, color);
            values.put(CategoriesNotesContract.CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID, String.valueOf(Calendar.getInstance().getTimeInMillis() + i));

            sqLiteDatabase.insert(CategoriesNotesContract.CategoriesContract.TABLE_NAME, null, values);
            i++;
        }

        cursor.close();
    }
}
