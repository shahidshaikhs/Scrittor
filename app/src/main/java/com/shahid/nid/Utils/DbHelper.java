package com.shahid.nid.Utils;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.shahid.nid.Categories.CategoriesDbHelper;
import com.shahid.nid.Categories.CategoriesNotesContract;
import com.shahid.nid.Categories.CategoriesNotesContract.CategoriesContract;
import com.shahid.nid.Categories.Category;
import com.shahid.nid.Note;
import com.shahid.nid.NotesContract;
import com.shahid.nid.NotesContract.MainNotes;
import com.shahid.nid.NotesDbHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Junaid Gandhi on 08/27/2021.
 * <p>
 * A database helper class with singleton approach for easy access to database CRUD operations and
 * managing (closing) connections.
 */
public class DbHelper {
    private static final Object LOCK = new Object();
    private static DbHelper sInstance;
    private NotesDbHelper mNotesDbHelper;
    private CategoriesDbHelper mCategoryDbHelper;
    private SQLiteDatabase notesDb, categoriesDb;

    private DbHelper(Context context) {
        mNotesDbHelper = new NotesDbHelper(context);
        mCategoryDbHelper = new CategoriesDbHelper(context);
        notesDb = mNotesDbHelper.getWritableDatabase();
        categoriesDb = mCategoryDbHelper.getWritableDatabase();
    }

    public static DbHelper getInstance(Application applicationContext) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new DbHelper(applicationContext);
            }
        }
        return sInstance;
    }

    public void destroy() {
        notesDb.close();
        categoriesDb.close();
        mNotesDbHelper.close();
        mCategoryDbHelper.close();
    }

    // -------- NOTES SECTION ----------

    /**
     * Fetches all notes from db and also parses them to Note object and adds it to arraylist
     * returning an arraylist of Note objects.
     *
     * @param projection
     * @return
     */
    public ArrayList<Note> fetchAllNotesFromDb(@Nullable String[] projection) {
        ArrayList<Note> notes = new ArrayList<>();
        Cursor cursor = notesDb.query(
                MainNotes.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                MainNotes.COLUMN_LAST_EDITED                                 // The sort order
        );

        List<String> selectedColumns = projection != null ? Arrays.asList(projection) : new ArrayList<>();
        while (cursor.moveToNext()) {
            Note note = new Note();
            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes._ID)) {
                note.setNoteID(cursor.getInt(
                        cursor.getColumnIndexOrThrow(MainNotes._ID)));
            }
            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes.COLUMN_NAME_TITLE)) {
                note.setNoteTitle(cursor.getString(
                        cursor.getColumnIndex(MainNotes.COLUMN_NAME_TITLE)));
            }
            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes.COLUMN_NAME_CONTENT)) {
                note.setNoteContent(cursor.getString(
                        cursor.getColumnIndex(MainNotes.COLUMN_NAME_CONTENT)));
            }

            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes.COLUMN_DATE)) {
                note.setCreationDate(cursor.getString(
                        cursor.getColumnIndexOrThrow(MainNotes.COLUMN_DATE)));
            }

            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes.COLUMN_CATEGORY_ID)) {
                note.setCategoryId(cursor.getString(
                        cursor.getColumnIndexOrThrow(MainNotes.COLUMN_CATEGORY_ID)));
            }

            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes.COLUMN_CATEGORY_COLOR)) {
                note.setCategoryColor(cursor.getString(
                        cursor.getColumnIndexOrThrow(MainNotes.COLUMN_CATEGORY_COLOR)));
            }

            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes.COLUMN_CATEGORY)) {
                note.setCategoryName(cursor.getString(
                        cursor.getColumnIndexOrThrow(MainNotes.COLUMN_CATEGORY)));
            }

            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes.COLUMN_LAST_EDITED)) {
                note.setLastEdited(cursor.getString(
                        cursor.getColumnIndexOrThrow(MainNotes.COLUMN_LAST_EDITED)));
            }

            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes.COLUMN_STARRED_CHECK)) {
                note.setIsStarred(cursor.getString(
                        cursor.getColumnIndexOrThrow(MainNotes.COLUMN_STARRED_CHECK)));
            }

            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes.COLUMN_UNIQUE_NOTE_ID)) {
                note.setNoteUniqueId(cursor.getString(
                        cursor.getColumnIndexOrThrow(MainNotes.COLUMN_UNIQUE_NOTE_ID)));
            }

            notes.add(note);
        }

        cursor.close();

        return notes;
    }

    /**
     * Fetches all notes from db and also parses them to Note object and adds it to arraylist
     * returning an arraylist of Note objects.
     *
     * @param projection
     * @return
     */
    public ArrayList<Note> fetchNotesBy(@Nullable String[] projection, String selection, String[] selectionArgs) {
        ArrayList<Note> notes = new ArrayList<>();
        Cursor cursor = notesDb.query(
                MainNotes.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                MainNotes.COLUMN_LAST_EDITED                                 // The sort order
        );

        List<String> selectedColumns = projection != null ? Arrays.asList(projection) : new ArrayList<>();
        while (cursor.moveToNext()) {
            Note note = new Note();
            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes._ID)) {
                note.setNoteID(cursor.getInt(
                        cursor.getColumnIndexOrThrow(MainNotes._ID)));
            }
            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes.COLUMN_NAME_TITLE)) {
                note.setNoteTitle(cursor.getString(
                        cursor.getColumnIndex(MainNotes.COLUMN_NAME_TITLE)));
            }
            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes.COLUMN_NAME_CONTENT)) {
                note.setNoteContent(cursor.getString(
                        cursor.getColumnIndex(MainNotes.COLUMN_NAME_CONTENT)));
            }

            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes.COLUMN_DATE)) {
                note.setCreationDate(cursor.getString(
                        cursor.getColumnIndexOrThrow(MainNotes.COLUMN_DATE)));
            }

            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes.COLUMN_CATEGORY_ID)) {
                note.setCategoryId(cursor.getString(
                        cursor.getColumnIndexOrThrow(MainNotes.COLUMN_CATEGORY_ID)));
            }

            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes.COLUMN_CATEGORY_COLOR)) {
                note.setCategoryColor(cursor.getString(
                        cursor.getColumnIndexOrThrow(MainNotes.COLUMN_CATEGORY_COLOR)));
            }

            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes.COLUMN_CATEGORY)) {
                note.setCategoryName(cursor.getString(
                        cursor.getColumnIndexOrThrow(MainNotes.COLUMN_CATEGORY)));
            }

            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes.COLUMN_LAST_EDITED)) {
                note.setLastEdited(cursor.getString(
                        cursor.getColumnIndexOrThrow(MainNotes.COLUMN_LAST_EDITED)));
            }

            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes.COLUMN_STARRED_CHECK)) {
                note.setIsStarred(cursor.getString(
                        cursor.getColumnIndexOrThrow(MainNotes.COLUMN_STARRED_CHECK)));
            }

            if (selectedColumns.isEmpty() || selectedColumns.contains(MainNotes.COLUMN_UNIQUE_NOTE_ID)) {
                note.setNoteUniqueId(cursor.getString(
                        cursor.getColumnIndexOrThrow(MainNotes.COLUMN_UNIQUE_NOTE_ID)));
            }

            notes.add(note);
        }

        cursor.close();

        return notes;
    }

    /**
     * Use to fetch a single Note object from db which matches the fetchBy query provided with all
     * columns data included.
     *
     * @param fetchBy   - A filter declaring which rows to return. Passing null will return all rows for the given table.
     * @param whereArgs - the ? in fetch by will be replaced by these args
     * @return - First note object matching the query
     */
    public Note fetchNote(String fetchBy, String[] whereArgs) {
        Cursor cursor = notesDb.query(MainNotes.TABLE_NAME, null, fetchBy, whereArgs, null, null, null);
        if (cursor.moveToNext()) {
            Note note = new Note();

            note.setNoteID(cursor.getInt(
                    cursor.getColumnIndexOrThrow(MainNotes._ID)));
            note.setNoteTitle(cursor.getString(
                    cursor.getColumnIndex(MainNotes.COLUMN_NAME_TITLE)));
            note.setNoteContent(cursor.getString(
                    cursor.getColumnIndex(MainNotes.COLUMN_NAME_CONTENT)));
            note.setCreationDate(cursor.getString(
                    cursor.getColumnIndexOrThrow(MainNotes.COLUMN_DATE)));
            note.setCategoryId(cursor.getString(
                    cursor.getColumnIndexOrThrow(MainNotes.COLUMN_CATEGORY_ID)));
            note.setCategoryColor(cursor.getString(
                    cursor.getColumnIndexOrThrow(MainNotes.COLUMN_CATEGORY_COLOR)));
            note.setCategoryName(cursor.getString(
                    cursor.getColumnIndexOrThrow(MainNotes.COLUMN_CATEGORY)));
            note.setLastEdited(cursor.getString(
                    cursor.getColumnIndexOrThrow(MainNotes.COLUMN_LAST_EDITED)));
            note.setIsStarred(cursor.getString(
                    cursor.getColumnIndexOrThrow(MainNotes.COLUMN_STARRED_CHECK)));
            note.setNoteUniqueId(cursor.getString(
                    cursor.getColumnIndexOrThrow(MainNotes.COLUMN_UNIQUE_NOTE_ID)));
            cursor.close();
            return note;
        }
        return new Note();
    }

    /**
     * @param list
     * @return
     */
    public int upsertNotesListByUniqueId(ArrayList<Note> list) {
        int count = 0;
        for (Note note : list) {
            String whereClause = MainNotes.COLUMN_UNIQUE_NOTE_ID + " = ?";
            String[] whereValues = new String[]{note.getNoteUniqueId()};

            ContentValues values = new ContentValues();
            values.put(MainNotes.COLUMN_NAME_TITLE, note.getNoteTitle());
            values.put(MainNotes.COLUMN_NAME_CONTENT, note.getNoteContent());
            values.put(MainNotes.COLUMN_DATE, note.getCreationDate());
            values.put(MainNotes.COLUMN_CATEGORY, note.getCategoryName());
            values.put(MainNotes.COLUMN_CATEGORY_ID, note.getCategoryId());
            values.put(MainNotes.COLUMN_CATEGORY_COLOR, note.getCategoryColor());
            values.put(MainNotes.COLUMN_STARRED_CHECK, note.getIsStarred());
            values.put(MainNotes.COLUMN_UNIQUE_NOTE_ID, note.getNoteUniqueId()); // TODO added this.. it will help identify unique notes across devices
            values.put(MainNotes.COLUMN_LAST_EDITED, note.getLastEdited());

            if (notesDb.update(MainNotes.TABLE_NAME, values, whereClause, whereValues) <= 0) {
                if (notesDb.insert(MainNotes.TABLE_NAME, null, values) != -1) {
                    count++;
                }
            } else {
                count++;
            }
        }

        return list.size();
    }

    /**
     * @param query
     * @param selectionArgs
     * @return
     */
    public Cursor runNotesRawQuery(String query, String[] selectionArgs) {
        return notesDb.rawQuery(query, selectionArgs);
    }

    public int upsertNote(Note note) {
        String whereClause = MainNotes.COLUMN_UNIQUE_NOTE_ID + " = ?";
        String[] whereValues = new String[]{note.getNoteUniqueId()};

        ContentValues values = new ContentValues();
        if (note.getNoteTitle() != null) {
            values.put(NotesContract.MainNotes.COLUMN_NAME_TITLE, note.getNoteTitle());
        }
        if (note.getNoteContent() != null) {
            values.put(NotesContract.MainNotes.COLUMN_NAME_CONTENT, note.getNoteContent());
        }
        if (note.getCreationDate() != null) {
            values.put(NotesContract.MainNotes.COLUMN_DATE, note.getCreationDate());
        }
        if (note.getCategoryName() != null) {
            values.put(NotesContract.MainNotes.COLUMN_CATEGORY, note.getCategoryName());
        }

        if (note.getCategoryId() != null) {
            values.put(NotesContract.MainNotes.COLUMN_CATEGORY_ID, note.getCategoryId());
        }
        if (note.getCategoryColor() != null) {
            values.put(NotesContract.MainNotes.COLUMN_CATEGORY_COLOR, note.getCategoryColor());
        }
        if (note.getNoteUniqueId() != null) {
            values.put(NotesContract.MainNotes.COLUMN_UNIQUE_NOTE_ID, note.getNoteUniqueId());
        }

        values.put(NotesContract.MainNotes.COLUMN_LAST_EDITED, String.valueOf(Calendar.getInstance().getTimeInMillis()));


        if (note.getIsStarred() != null) {
            values.put(NotesContract.MainNotes.COLUMN_STARRED_CHECK, note.getIsStarred());
        }

        if (notesDb.update(MainNotes.TABLE_NAME, values, whereClause, whereValues) <= 0) {
            return (int) notesDb.insert(MainNotes.TABLE_NAME, null, values);
        }
        return 1;
    }

    public void deleteNote(Note note) {
        String selection = NotesContract.MainNotes._ID + " = ?"; // LIKE??? should'nt it be = ?
        String[] selectionArgs = {String.valueOf(note.getNoteID())};
        notesDb.delete(NotesContract.MainNotes.TABLE_NAME, selection, selectionArgs);
    }
    // ------------ CATEGORY SECTION ---------

    /**
     * Use this method to fetch all categories from db. It also parses them to Category object and adds
     * it to arraylist returning an arraylist of Category objects.
     *
     * @param projection - Columns to be selected. null selects all columns.
     * @return - ArrayList of categories.
     */
    public ArrayList<Category> fetchAllCategoriesFromDb(@Nullable String[] projection) {
        ArrayList<Category> categories = new ArrayList<>();
        Cursor cursor = categoriesDb.query(
                CategoriesContract.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID                                 // The sort order
        );

        List<String> selectedColumns = projection != null ? Arrays.asList(projection) : new ArrayList<>();
        while (cursor.moveToNext()) {
            Category category = new Category();
            if (selectedColumns.isEmpty() || selectedColumns.contains(CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID)) {
                category.setCategoryUniqueId(cursor.getString(
                        cursor.getColumnIndexOrThrow(CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID)));
            }

            if (selectedColumns.isEmpty() || selectedColumns.contains(CategoriesContract.COLUMN_NAME_CATEGORY)) {
                category.setCategoryName(cursor.getString(
                        cursor.getColumnIndex(CategoriesContract.COLUMN_NAME_CATEGORY)));
            }

            if (selectedColumns.isEmpty() || selectedColumns.contains(CategoriesContract.COLUMN_DESCRIPTION_CATEGORY)) {
                category.setDescription(cursor.getString(
                        cursor.getColumnIndex(CategoriesContract.COLUMN_DESCRIPTION_CATEGORY)));
            }

            if (selectedColumns.isEmpty() || selectedColumns.contains(CategoriesContract.COLUMN_COLOR)) {
                category.setCategoryColor(cursor.getString(
                        cursor.getColumnIndexOrThrow(CategoriesContract.COLUMN_COLOR)));
            }
            categories.add(category);
        }

        cursor.close();

        return categories;
    }

    /**
     * Use this method to fetch all categories from db in Hashmap. It also parses them to Category object and adds
     * it to arraylist returning an arraylist of Category objects.
     *
     * @param projection  - Columns to be selected. null selects all columns.
     * @param columnAsKey - Which column should be used as key
     * @return - ArrayList of categories.
     */
    public HashMap<String, Category> fetchAllCategoriesFromDb(@Nullable List<String> projection, String columnAsKey) {
        HashMap<String, Category> categories = new HashMap<>();
        if (projection != null && !projection.contains(columnAsKey)) {
            projection.add(columnAsKey);
        }
        Cursor cursor = categoriesDb.query(
                CategoriesContract.TABLE_NAME,                     // The table to query
                projection == null ? null : projection.toArray(new String[]{}),                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID                                 // The sort order
        );


        while (cursor.moveToNext()) {
            Category category = new Category();
            if (projection == null || projection.contains(CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID)) {
                category.setCategoryUniqueId(cursor.getString(
                        cursor.getColumnIndexOrThrow(CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID)));
            }

            if (projection == null || projection.contains(CategoriesContract.COLUMN_NAME_CATEGORY)) {
                category.setCategoryName(cursor.getString(
                        cursor.getColumnIndex(CategoriesContract.COLUMN_NAME_CATEGORY)));
            }

            if (projection == null || projection.contains(CategoriesContract.COLUMN_DESCRIPTION_CATEGORY)) {
                category.setDescription(cursor.getString(
                        cursor.getColumnIndex(CategoriesContract.COLUMN_DESCRIPTION_CATEGORY)));
            }

            if (projection == null || projection.contains(CategoriesContract.COLUMN_COLOR)) {
                category.setCategoryColor(cursor.getString(
                        cursor.getColumnIndexOrThrow(CategoriesContract.COLUMN_COLOR)));
            }

            categories.put(category.getCategoryName(), category);
        }

        cursor.close();

        return categories;
    }

    /**
     * @param list
     * @param whereClause
     * @param whereArgs - Usually null. only supply this when you want to update all the objects
     *                  based on single arg instead of value of individual object.
     * @return
     */
    public int upsertCategoryListBy(ArrayList<Category> list, String whereClause, @Nullable ArrayList<String> whereArgs) {
        int count = 0;
        for (Category category : list) {
            String[] whereValues = whereArgs != null ? whereArgs.toArray(new String[]{}) : new String[]{getCategoryWhereArg(whereClause, category)};
            ContentValues values = parseCategoryToContentValues(category);
            if (categoriesDb.update(CategoriesContract.TABLE_NAME, values, whereClause, whereValues) <= 0) { // find by name first, if not found then find by unique ID
                if (categoriesDb.insert(CategoriesContract.TABLE_NAME, null, values) != -1) {
                    count++;
                }
            } else {
                count++;
            }
        }
        return count;
    }

    public Category getCategoryByName(String name) {
        Cursor cursor = findCategories(null, CategoriesNotesContract.CategoriesContract.COLUMN_NAME_CATEGORY + " = ?", new String[]{name});
        if (cursor.moveToNext()) {
            return parseCursorToCategory(cursor);
        }
        return null;
    }

    public Category getCategoryById(String categoryId) {
        Cursor cursor = findCategories(null, CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID + " = ?", new String[]{categoryId});
        if (cursor.moveToNext()) {
            return parseCursorToCategory(cursor);
        }
        return null;

    }

    public Cursor findCategories(String[] projection, String selection, String[] selectionArgs) {
        return categoriesDb.query(CategoriesContract.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);
    }

    public void deleteCategory(String categoryId) {
        String whereClause = CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID + "= ?";
        String[] whereArgs = new String[]{categoryId};

        categoriesDb.delete(CategoriesContract.TABLE_NAME, whereClause, whereArgs);

        Category defaultCategory = getCategoryByName("Not Specified");

        ContentValues values = new ContentValues();
        values.put(NotesContract.MainNotes.COLUMN_CATEGORY, defaultCategory.getCategoryName());
        values.put(NotesContract.MainNotes.COLUMN_CATEGORY_ID, defaultCategory.getCategoryUniqueId());
        values.put(NotesContract.MainNotes.COLUMN_CATEGORY_COLOR, defaultCategory.getCategoryColor());

        notesDb.update(NotesContract.MainNotes.TABLE_NAME, values, NotesContract.MainNotes.COLUMN_CATEGORY_ID.concat(" = ?"), new String[]{categoryId});
    }

    public int insertNewCategory(Category category) {
        return (int) categoriesDb.insert(CategoriesContract.TABLE_NAME, null, parseCategoryToContentValues(category));
    }

    public int updateCategory(Category category) {
        return upsertCategoryListBy(new ArrayList<>(Collections.singletonList(category)), CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID + " = ?", null);
    }

    private ContentValues parseCategoryToContentValues(Category category) {
        ContentValues values = new ContentValues();
        values.put(CategoriesNotesContract.CategoriesContract.COLUMN_NAME_CATEGORY, category.getCategoryName());
        values.put(CategoriesNotesContract.CategoriesContract.COLUMN_DESCRIPTION_CATEGORY, category.getDescription());
        values.put(CategoriesNotesContract.CategoriesContract.COLUMN_COLOR, category.getCategoryColor());
        values.put(CategoriesNotesContract.CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID, category.getCategoryUniqueId());
        return values;
    }

    /**
     * Provide a cursor returned by ONLY Category DB QUERY
     *
     * @param cursor
     * @return
     */
    private Category parseCursorToCategory(Cursor cursor) {
        Category category = new Category();

        category.setCategoryUniqueId(cursor.getString(
                cursor.getColumnIndexOrThrow(CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID)));


        category.setCategoryName(cursor.getString(
                cursor.getColumnIndex(CategoriesContract.COLUMN_NAME_CATEGORY)));
        category.setDescription(cursor.getString(
                cursor.getColumnIndex(CategoriesContract.COLUMN_DESCRIPTION_CATEGORY)));
        category.setCategoryColor(cursor.getString(
                cursor.getColumnIndexOrThrow(CategoriesContract.COLUMN_COLOR)));


        return category;
    }

    private String getCategoryWhereArg(String selection, Category category) {
        selection = selection.replace(" = ?", "");
        switch (selection) {
            case CategoriesContract.COLUMN_NAME_CATEGORY:
                return category.getCategoryName();

            case CategoriesContract.COLUMN_COLOR:
                return category.getCategoryColor();

            case CategoriesContract.COLUMN_DESCRIPTION_CATEGORY:
                return category.getDescription();

            case CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID:
            default:
                return category.getCategoryUniqueId();
        }
    }
}
