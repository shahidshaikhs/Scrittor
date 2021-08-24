package com.shahid.nid.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shahid.nid.Categories.CategoriesDbHelper;
import com.shahid.nid.Categories.CategoriesNotesContract;
import com.shahid.nid.NotesContract;
import com.shahid.nid.NotesDbHelper;

/**
 * Created by shahi on 10/19/2017.
 */

public class DatabaseMethods {

    ContentValues data = new ContentValues();
    private SQLiteDatabase dbWrite;
    private NotesDbHelper mDbHelper;

    public void editCatInNoteDB(Context context, long categoryId, String newCategoryName, String categoryColor) {
        /**Accessing Helper class*/
        mDbHelper = new NotesDbHelper(context);

        dbWrite = mDbHelper.getWritableDatabase();

        data.put(NotesContract.mainNotes.COLUMN_CATEGORY, newCategoryName);
        data.put(NotesContract.mainNotes.COLUMN_CATEGORY_COLOR, categoryColor);

        String whereClause = NotesContract.mainNotes.COLUMN_CATEGORY_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(categoryId)};

        dbWrite.update(NotesContract.mainNotes.TABLE_NAME, data, whereClause, whereArgs);
    }


    public void deleteCategory(Context context, String categoryId) {
        CategoriesDbHelper mDbHelperCategory = new CategoriesDbHelper(context);
        SQLiteDatabase dbWriteCategory = mDbHelperCategory.getWritableDatabase();
        String whereClause = CategoriesNotesContract.categoriesContract.COLUMN_CATEGORY_UNIQUE_ID + "= ?";
        String[] whereArgs = new String[]{categoryId};

        dbWriteCategory.delete(CategoriesNotesContract.categoriesContract.TABLE_NAME, whereClause, whereArgs);

        //        dbWriteCategory.execSQL("DELETE FROM " + CategoriesNotesContract.categoriesContract.TABLE_NAME + " WHERE " + CategoriesNotesContract.categoriesContract._ID + " = " + categoryId);

        Cursor cursor = dbWriteCategory.query(CategoriesNotesContract.categoriesContract.TABLE_NAME,
                null,
                CategoriesNotesContract.categoriesContract.COLUMN_NAME_CATEGORY.concat(" = ?"),
                new String[]{"Not Specified"},
                null,
                null,
                null);

        if (cursor.moveToNext()) {
            String newId = cursor.getString(cursor.getColumnIndexOrThrow(CategoriesNotesContract.categoriesContract.COLUMN_CATEGORY_UNIQUE_ID));
            mDbHelper = new NotesDbHelper(context);
            dbWrite = mDbHelper.getWritableDatabase();
            data.put(NotesContract.mainNotes.COLUMN_CATEGORY, "Not Specified");
            data.put(NotesContract.mainNotes.COLUMN_CATEGORY_ID, newId);
            data.put(NotesContract.mainNotes.COLUMN_CATEGORY_COLOR, "#6ECFFF");

            dbWrite.update(NotesContract.mainNotes.TABLE_NAME, data, NotesContract.mainNotes.COLUMN_CATEGORY_ID.concat(" = ?"), new String[]{categoryId});

        }
    }


}
