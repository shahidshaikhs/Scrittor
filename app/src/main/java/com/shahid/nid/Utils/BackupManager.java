package com.shahid.nid.Utils;

import com.google.gson.JsonObject;
import com.shahid.nid.Categories.Category;
import com.shahid.nid.Categories.CategoriesNotesContract.CategoriesContract;
import com.shahid.nid.Note;
import com.shahid.nid.NotesContract.MainNotes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

/** Shared validation and merge rules for local and Drive backups. */
public final class BackupManager {
    private BackupManager() {}

    public static String exportJson(DbHelper db) {
        synchronized (db) {
        JsonObject backup = new JsonObject();
        GsonHelper gson = GsonHelper.getInstance();
        backup.addProperty("formatVersion", 1);
        backup.add("notes", new com.google.gson.JsonParser().parse(gson.toJson(db.fetchAllNotesFromDb(null))));
        backup.add("categories", new com.google.gson.JsonParser().parse(gson.toJson(db.fetchAllCategoriesFromDb(null))));
        return backup.toString();
        }
    }

    public static int importJson(DbHelper db, String json) {
        GsonHelper gson = GsonHelper.getInstance();
        JsonObject backup = gson.getJsonObject(json);
        if (backup == null || !backup.has("formatVersion") || backup.get("formatVersion").getAsInt() != 1
                || !backup.has("notes") || !backup.has("categories")) {
            throw new IllegalArgumentException("Unsupported backup format");
        }
        return restore(db, gson.getNoteListFromJson(backup.get("notes").toString()),
                gson.getCategoryListFromJson(backup.get("categories").toString()));
    }

    public static void validate(ArrayList<Note> notes, ArrayList<Category> categories) {
        if (notes == null || categories == null) throw new IllegalArgumentException("Empty backup data");
        for (Note note : notes) {
            if (note == null || blank(note.getNoteUniqueId()) || (note.getNoteTitle() == null && note.getNoteContent() == null))
                throw new IllegalArgumentException("Invalid note in backup");
        }
        for (Category category : categories) {
            if (category == null || blank(category.getCategoryUniqueId()) || blank(category.getCategoryName()))
                throw new IllegalArgumentException("Invalid category in backup");
            try { android.graphics.Color.parseColor(category.getCategoryColor()); }
            catch (Exception e) { throw new IllegalArgumentException("Invalid category color", e); }
        }
    }

    public static synchronized int restore(DbHelper db, ArrayList<Note> notes, ArrayList<Category> categories) {
        synchronized (db) {
        // Validate the complete payload before modifying either database.
        validate(notes, categories);
        HashMap<String, Category> mapped = new HashMap<>();
        for (Category category : categories) {
            Category local = db.getCategoryById(category.getCategoryUniqueId());
            if (local == null) local = db.getCategoryByName(category.getCategoryName());
            if (local == null) {
                db.upsertCategoryListBy(new ArrayList<>(Collections.singletonList(category)),
                        CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID + " = ?", null);
                local = category;
            }
            // Existing category identities must remain stable for local notes.
            mapped.put(category.getCategoryUniqueId(), local);
        }
        Category fallback = db.ensureDefaultCategory();
        HashMap<String, Note> localNotes = new HashMap<>();
        for (Note note : db.fetchAllNotesFromDb(null)) localNotes.put(note.getNoteUniqueId(), note);
        ArrayList<Note> incoming = new ArrayList<>();
        for (Note note : notes) {
            Note local = localNotes.get(note.getNoteUniqueId());
            if (local != null && local.lastEditedMillis() >= note.lastEditedMillis()) continue;
            Category category = mapped.get(note.getCategoryId());
            if (category == null) category = db.getCategoryById(note.getCategoryId());
            if (category == null) category = db.getCategoryByName(note.getCategoryName());
            if (category == null) category = fallback;
            note.setCategoryId(category.getCategoryUniqueId());
            note.setCategoryName(category.getCategoryName());
            note.setCategoryColor(category.getCategoryColor());
            incoming.add(note);
            localNotes.put(note.getNoteUniqueId(), note);
        }
        return db.upsertNotesListByUniqueId(incoming);
        }
    }

    private static boolean blank(String value) { return value == null || value.trim().isEmpty(); }
}
