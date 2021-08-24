package com.shahid.nid;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.shahid.nid.Categories.CategoriesDbHelper;
import com.shahid.nid.Categories.CategoriesNotesContract;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by shahi on 9/2/2017.
 */

public class NotesDbHelper extends SQLiteOpenHelper {
     private Context context;
    /**
     * If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 4; //was 3
    private static final String DATABASE_NAME = "FeedReader.db";

    /**
     * String to create a table
     */
    private static final String SQL_CREATE_MAIN_TABLE = "CREATE TABLE " + NotesContract.mainNotes.TABLE_NAME + "(" +
            NotesContract.mainNotes._ID + " INTEGER PRIMARY KEY," +
            NotesContract.mainNotes.COLUMN_NAME_TITLE + " TEXT," +
            NotesContract.mainNotes.COLUMN_DATE + " TEXT," +
            NotesContract.mainNotes.COLUMN_CATEGORY + " TEXT," +
            NotesContract.mainNotes.COLUMN_CATEGORY_COLOR + " TEXT," +
            NotesContract.mainNotes.COLUMN_CATEGORY_ID + " TEXT," +
            NotesContract.mainNotes.COLUMN_STARRED_CHECK + " TEXT," +
            NotesContract.mainNotes.COLUMN_UNIQUE_NOTE_ID + " TEXT," +
            NotesContract.mainNotes.COLUMN_LAST_EDITED + " TEXT," +
            NotesContract.mainNotes.COLUMN_NAME_CONTENT + " TEXT)";
    /**
     * String to delete the table
     */
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + NotesContract.mainNotes.TABLE_NAME;

    /**
     * Constructor
     */
    public NotesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_MAIN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) { // so I don't have to worry about export and import? nope.. though i just saw one issue
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        backupDb(sqLiteDatabase);  // TODO FIRST BACKUP OLD TABLE TO A BACKUP TABLE
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES); // TODO DELETE MAIN TABLE
        onCreate(sqLiteDatabase); // TODO CREATE MAIN TABLE WITH NEW SCHEMA
        restoreDb(sqLiteDatabase); // TODO RESTORE DATA TO NEW MAIN TABLE FROM BACKUP TABLE
    }

    /**
     * Creates a backup table and Copies data from main Notes table to this backup table.
     * @param sqLiteDatabase
     */
    private void backupDb(SQLiteDatabase sqLiteDatabase){
        String query = "INSERT INTO " + NotesContract.mainNotes.BACKUP_TABLE_NAME_FOR_UPGRADE_DB + " SELECT * FROM " + NotesContract.mainNotes.TABLE_NAME;
        final String SQL_CREATE_BACKUP_TABLE = "CREATE TABLE " + NotesContract.mainNotes.BACKUP_TABLE_NAME_FOR_UPGRADE_DB + "(" +
                NotesContract.mainNotes._ID + " INTEGER PRIMARY KEY," +
                NotesContract.mainNotes.COLUMN_NAME_TITLE + " TEXT," +
                NotesContract.mainNotes.COLUMN_DATE + " TEXT," +
                NotesContract.mainNotes.COLUMN_CATEGORY + " TEXT," +
                NotesContract.mainNotes.COLUMN_CATEGORY_COLOR + " TEXT," +
                NotesContract.mainNotes.COLUMN_CATEGORY_ID + " TEXT," +
                NotesContract.mainNotes.COLUMN_STARRED_CHECK + " TEXT," +
                NotesContract.mainNotes.COLUMN_NAME_CONTENT + " TEXT)";

        sqLiteDatabase.execSQL(SQL_CREATE_BACKUP_TABLE);
        sqLiteDatabase.execSQL(query);
    }

    /**
     * Restore data from backup Table to newly created Main Table with new schema .. (Change it accordingly in future table schema upgrades
     * @param sqLiteDatabase
     */
    @SuppressLint("ApplySharedPref")
    private void restoreDb(SQLiteDatabase sqLiteDatabase){
        // TODO get data from backup using sqlitedb and use cursor to put data
        CategoriesDbHelper mDbHelper = new CategoriesDbHelper(context);
        SQLiteDatabase categoryDb = mDbHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(
                NotesContract.mainNotes.BACKUP_TABLE_NAME_FOR_UPGRADE_DB,                     // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        long i = 0;
        while (cursor.moveToNext()) {
            String title = cursor.getString(
                    cursor.getColumnIndexOrThrow(NotesContract.mainNotes.COLUMN_NAME_TITLE));
            String content = cursor.getString(
                    cursor.getColumnIndexOrThrow(NotesContract.mainNotes.COLUMN_NAME_CONTENT));
            String creationDate = cursor.getString(
                    cursor.getColumnIndexOrThrow(NotesContract.mainNotes.COLUMN_DATE));

            String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.mainNotes.COLUMN_CATEGORY));

            String categoryColor = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.mainNotes.COLUMN_CATEGORY_COLOR));

            String categoryId = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.mainNotes.COLUMN_CATEGORY_ID));

            String isStarred = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.mainNotes.COLUMN_STARRED_CHECK));

            String whereClause = CategoriesNotesContract.categoriesContract._ID + " = ? AND " + CategoriesNotesContract.categoriesContract.COLUMN_NAME_CATEGORY + " = ?";
            String[] whereArguments = new String[] { categoryId, categoryName};

            Cursor categoryCursor = categoryDb.query(CategoriesNotesContract.categoriesContract.TABLE_NAME,
                    null,
                    whereClause,
                    whereArguments,
                    null,
                    null,
                    CategoriesNotesContract.categoriesContract._ID);

            ContentValues values = new ContentValues();
            values.put(NotesContract.mainNotes.COLUMN_NAME_TITLE, title);
            values.put(NotesContract.mainNotes.COLUMN_NAME_CONTENT, content);
            values.put(NotesContract.mainNotes.COLUMN_DATE, creationDate);
            values.put(NotesContract.mainNotes.COLUMN_CATEGORY, categoryName);

            if (categoryCursor.moveToNext()){
                categoryId = categoryCursor.getString(categoryCursor.getColumnIndex(CategoriesNotesContract.categoriesContract.COLUMN_CATEGORY_UNIQUE_ID));
            }

            values.put(NotesContract.mainNotes.COLUMN_CATEGORY_ID, categoryId);
            values.put(NotesContract.mainNotes.COLUMN_CATEGORY_COLOR, categoryColor);
            values.put(NotesContract.mainNotes.COLUMN_STARRED_CHECK, isStarred);
            String uniqueId = String.valueOf(Calendar.getInstance().getTimeInMillis() + i);
            values.put(NotesContract.mainNotes.COLUMN_UNIQUE_NOTE_ID, uniqueId);
            values.put(NotesContract.mainNotes.COLUMN_LAST_EDITED, uniqueId);

            Log.e("Notes Parsed", values.toString());
            sqLiteDatabase.insert(NotesContract.mainNotes.TABLE_NAME, null, values);
            context.getSharedPreferences("last_edited_base_shared_pref", MODE_PRIVATE).edit().putLong(uniqueId, Calendar.getInstance().getTimeInMillis()).commit();
            i++;
        }

        cursor.close();
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
