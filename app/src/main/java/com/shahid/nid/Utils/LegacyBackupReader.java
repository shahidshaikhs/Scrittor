package com.shahid.nid.Utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.shahid.nid.Categories.Category;
import com.shahid.nid.Note;
import com.shahid.nid.NotesContract.MainNotes;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

/** Reads legacy FeedReader.db backups without replacing or opening the live database files. */
public final class LegacyBackupReader {
    private LegacyBackupReader() {}
    public static int restore(File file, DbHelper destination) {
        ArrayList<Note> notes = new ArrayList<>();
        LinkedHashMap<String, Category> categories = new LinkedHashMap<>();
        try (SQLiteDatabase source = SQLiteDatabase.openDatabase(file.getPath(), null, SQLiteDatabase.OPEN_READONLY);
             Cursor rows = source.query(MainNotes.TABLE_NAME, null, null, null, null, null, null)) {
            // Reject unrelated SQLite files before any local changes.
            rows.getColumnIndexOrThrow(MainNotes.COLUMN_NAME_TITLE);
            rows.getColumnIndexOrThrow(MainNotes.COLUMN_NAME_CONTENT);
            while (rows.moveToNext()) {
                Note note = new Note();
                note.setNoteTitle(text(rows, MainNotes.COLUMN_NAME_TITLE));
                note.setNoteContent(text(rows, MainNotes.COLUMN_NAME_CONTENT));
                note.setCreationDate(text(rows, MainNotes.COLUMN_DATE));
                note.setIsStarred(text(rows, MainNotes.COLUMN_STARRED_CHECK));
                String id = text(rows, MainNotes.COLUMN_UNIQUE_NOTE_ID);
                if (id == null || id.isEmpty()) id = stableId("note:" + text(rows, "_id") + ":" + note.getCreationDate() + ":" + note.getNoteTitle());
                note.setNoteUniqueId(id);
                String edited = text(rows, MainNotes.COLUMN_LAST_EDITED);
                note.setLastEdited(edited == null ? "0" : edited);
                String name = text(rows, MainNotes.COLUMN_CATEGORY);
                if (name == null || name.isEmpty()) name = "Not Specified";
                Category category = categories.get(name);
                if (category == null) {
                    category = new Category();
                    category.setCategoryName(name);
                    category.setCategoryUniqueId(stableId("category:" + name));
                    category.setDescription("");
                    String color = text(rows, MainNotes.COLUMN_CATEGORY_COLOR);
                    try { android.graphics.Color.parseColor(color); }
                    catch (Exception e) { color = "#6ECFFF"; }
                    category.setCategoryColor(color);
                    categories.put(name, category);
                }
                note.setCategoryName(name);
                note.setCategoryId(category.getCategoryUniqueId());
                note.setCategoryColor(category.getCategoryColor());
                notes.add(note);
            }
        }
        return BackupManager.restore(destination, notes, new ArrayList<>(categories.values()));
    }
    private static String text(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return index < 0 || cursor.isNull(index) ? null : cursor.getString(index);
    }
    private static String stableId(String value) { return UUID.nameUUIDFromBytes(value.getBytes(StandardCharsets.UTF_8)).toString(); }
}
