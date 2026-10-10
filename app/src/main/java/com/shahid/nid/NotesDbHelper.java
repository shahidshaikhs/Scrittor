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
    private static final String SQL_CREATE_MAIN_TABLE = "CREATE TABLE " + NotesContract.MainNotes.TABLE_NAME + "(" +
            NotesContract.MainNotes._ID + " INTEGER PRIMARY KEY," +
            NotesContract.MainNotes.COLUMN_NAME_TITLE + " TEXT," +
            NotesContract.MainNotes.COLUMN_DATE + " TEXT," +
            NotesContract.MainNotes.COLUMN_CATEGORY + " TEXT," +
            NotesContract.MainNotes.COLUMN_CATEGORY_COLOR + " TEXT," +
            NotesContract.MainNotes.COLUMN_CATEGORY_ID + " TEXT," +
            NotesContract.MainNotes.COLUMN_STARRED_CHECK + " TEXT," +
            NotesContract.MainNotes.COLUMN_UNIQUE_NOTE_ID + " TEXT," +
            NotesContract.MainNotes.COLUMN_LAST_EDITED + " TEXT," +
            NotesContract.MainNotes.COLUMN_NAME_CONTENT + " TEXT)";
    /**
     * String to delete the table
     */
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + NotesContract.MainNotes.TABLE_NAME;

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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            addTextColumn(db, NotesContract.MainNotes.COLUMN_UNIQUE_NOTE_ID);
            addTextColumn(db, NotesContract.MainNotes.COLUMN_LAST_EDITED);
            // Old notes referenced category row IDs. Preserve those IDs during the
            // category migration, then translate the references to stable IDs.
            try (CategoriesDbHelper helper = new CategoriesDbHelper(context)) {
                SQLiteDatabase categories = helper.getReadableDatabase();
                try (Cursor rows = db.query(NotesContract.MainNotes.TABLE_NAME, null,
                        null, null, null, null, null)) {
                    while (rows.moveToNext()) {
                        ContentValues values = new ContentValues();
                        String id = rows.getString(rows.getColumnIndexOrThrow(NotesContract.MainNotes.COLUMN_UNIQUE_NOTE_ID));
                        if (id == null || id.isEmpty()) {
                            values.put(NotesContract.MainNotes.COLUMN_UNIQUE_NOTE_ID, java.util.UUID.randomUUID().toString());
                        }
                        String edited = rows.getString(rows.getColumnIndexOrThrow(NotesContract.MainNotes.COLUMN_LAST_EDITED));
                        if (edited == null || edited.isEmpty()) {
                            values.put(NotesContract.MainNotes.COLUMN_LAST_EDITED, String.valueOf(System.currentTimeMillis()));
                        }
                        String categoryId = rows.getString(rows.getColumnIndexOrThrow(NotesContract.MainNotes.COLUMN_CATEGORY_ID));
                        String name = rows.getString(rows.getColumnIndexOrThrow(NotesContract.MainNotes.COLUMN_CATEGORY));
                        if (categoryId != null && name != null) {
                        try (Cursor category = categories.query(CategoriesNotesContract.CategoriesContract.TABLE_NAME, null,
                                "_id = ? AND " + CategoriesNotesContract.CategoriesContract.COLUMN_NAME_CATEGORY + " = ?",
                                new String[]{categoryId, name}, null, null, null)) {
                            if (category.moveToFirst()) {
                                values.put(NotesContract.MainNotes.COLUMN_CATEGORY_ID, category.getString(category.getColumnIndexOrThrow(
                                        CategoriesNotesContract.CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID)));
                            }
                        }
                        }
                        if (values.size() > 0) {
                            db.update(NotesContract.MainNotes.TABLE_NAME, values, "_id = ?",
                                    new String[]{rows.getString(rows.getColumnIndexOrThrow("_id"))});
                        }
                    }
                }
            }
        }
    }

    private void addTextColumn(SQLiteDatabase db, String name) {
        try (Cursor columns = db.rawQuery("PRAGMA table_info(" + NotesContract.MainNotes.TABLE_NAME + ")", null)) {
            while (columns.moveToNext()) {
                if (name.equals(columns.getString(columns.getColumnIndexOrThrow("name")))) return;
            }
        }
        db.execSQL("ALTER TABLE " + NotesContract.MainNotes.TABLE_NAME + " ADD COLUMN " + name + " TEXT");
    }
}
