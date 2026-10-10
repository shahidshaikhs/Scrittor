package com.shahid.nid;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.shahid.nid.Categories.Category;
import com.shahid.nid.Categories.CategoriesDbHelper;
import com.shahid.nid.Categories.CategoriesNotesContract.CategoriesContract;
import com.shahid.nid.NotesContract.MainNotes;
import com.shahid.nid.Utils.BackupManager;
import com.shahid.nid.Utils.DbHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, application = Application.class)
public class DataRegressionTest {
    private Application app;
    private DbHelper db;
    @Before public void setUp() {
        app = RuntimeEnvironment.getApplication();
        app.deleteDatabase("FeedReader.db");
        app.deleteDatabase("CategoriesHelper.db");
        db = DbHelper.getInstance(app);
    }
    @After public void tearDown() { db.destroy(); }

    private Note note(String id, String text, String edited) {
        Note note = new Note();
        note.setNoteUniqueId(id);
        note.setNoteTitle(text);
        note.setNoteContent("Body\nwith unicode: 日本語");
        note.setLastEdited(edited);
        return note;
    }
    private ArrayList<Note> notes(Note... notes) { return new ArrayList<>(Arrays.asList(notes)); }
    private Note find(String id) { return db.fetchNote(MainNotes.COLUMN_UNIQUE_NOTE_ID + " = ?", new String[]{id}); }

    @Test public void updatingNoteReturnsItsActualId() {
        db.upsertNote(note("first", "First", "1"));
        Note second = note("second", "Second", "2");
        int id = db.upsertNote(second);
        second.setNoteTitle("Updated");
        assertEquals(id, db.upsertNote(second));
        assertEquals(2, db.fetchAllNotesFromDb(null).size());
    }
    @Test public void reopeningHelperAfterCloseWorks() {
        db.upsertNote(note("one", "Saved", "1"));
        db.destroy();
        db = DbHelper.getInstance(app);
        assertEquals("Saved", find("one").getNoteTitle());
    }
    @Test public void restorePreservesNewerLocalEditsAndAcceptsNewerRemoteEdits() {
        db.upsertNotesListByUniqueId(notes(note("local", "New local", "200"), note("remote", "Old local", "100")));
        int count = BackupManager.restore(db, notes(note("local", "Old remote", "100"), note("remote", "New remote", "200")), new ArrayList<>());
        assertEquals(1, count);
        assertEquals("New local", find("local").getNoteTitle());
        assertEquals("New remote", find("remote").getNoteTitle());
        assertNotNull(find("remote").getCategoryId());
    }
    @Test public void invalidBackupDoesNotModifyDatabase() {
        db.upsertNotesListByUniqueId(notes(note("local", "Keep", "200")));
        try {
            BackupManager.restore(db, notes(note("new", "New", "200"), note(null, "Broken", "200")), new ArrayList<>());
            fail("Expected invalid backup rejection");
        } catch (IllegalArgumentException expected) {}
        assertEquals(1, db.fetchAllNotesFromDb(null).size());
    }
    @Test public void localBackupRoundTripsWithoutDuplicateNotes() {
        db.upsertNotesListByUniqueId(notes(note("id", "Title", "100")));
        String backup = BackupManager.exportJson(db);
        db.deleteNote(find("id"));
        assertEquals(1, BackupManager.importJson(db, backup));
        assertEquals(0, BackupManager.importJson(db, backup));
        assertEquals("Body\nwith unicode: 日本語", find("id").getNoteContent());
    }
    @Test public void renameAndDeleteCategoryKeepNotesReachable() {
        Category category = new Category();
        category.setCategoryUniqueId("category");
        category.setCategoryName("Work");
        category.setCategoryColor("#112233");
        db.insertNewCategory(category);
        Note note = note("note", "Title", "100");
        note.setCategoryId("category"); note.setCategoryName("Work");
        db.upsertNote(note);
        category.setCategoryName("Projects"); category.setCategoryColor("#445566");
        db.updateCategory(category);
        assertEquals("Projects", find("note").getCategoryName());
        assertEquals("#445566", find("note").getCategoryColor());
        db.deleteCategory("category");
        assertEquals("Not Specified", find("note").getCategoryName());
        String defaultId = db.ensureDefaultCategory().getCategoryUniqueId();
        db.deleteCategory(defaultId);
        assertNotNull(db.getCategoryById(defaultId));
    }
    @Test public void categoryMapHonorsRequestedKeyAndImmutableProjection() {
        Category category = db.ensureDefaultCategory();
        assertTrue(db.fetchAllCategoriesFromDb(Collections.singletonList(CategoriesContract.COLUMN_NAME_CATEGORY),
                CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID).containsKey(category.getCategoryUniqueId()));
    }
    @Test public void oldSchemaMigrationPreservesRowIdsAndCategoryLinks() {
        db.destroy();
        app.deleteDatabase("FeedReader.db"); app.deleteDatabase("CategoriesHelper.db");
        try (SQLiteDatabase categories = app.openOrCreateDatabase("CategoriesHelper.db", 0, null)) {
            categories.execSQL("CREATE TABLE mainCategories (_id INTEGER PRIMARY KEY, categoryDescription TEXT, categoryColor TEXT, categoryName TEXT)");
            categories.execSQL("INSERT INTO mainCategories VALUES (42, 'Work notes', '#112233', 'Work')");
            categories.setVersion(1);
        }
        try (SQLiteDatabase notes = app.openOrCreateDatabase("FeedReader.db", 0, null)) {
            notes.execSQL("CREATE TABLE mainNotes (_id INTEGER PRIMARY KEY, noteTitle TEXT, creationDate TEXT, noteCategory TEXT, categoryColor TEXT, categoryID TEXT, starredCheck TEXT, noteContent TEXT)");
            notes.execSQL("INSERT INTO mainNotes VALUES (73, 'Do not lose', 'old date', 'Work', '#112233', '42', 'starred', 'Original content')");
            notes.execSQL("INSERT INTO mainNotes (_id, noteTitle, noteContent) VALUES (74, 'Uncategorized', 'Retain me too')");
            notes.setVersion(3);
        }
        db = DbHelper.getInstance(app);
        Note migrated = db.fetchNote("_id = ?", new String[]{"73"});
        assertEquals(73, migrated.getNoteID());
        assertEquals("Original content", migrated.getNoteContent());
        assertNotNull(migrated.getNoteUniqueId());
        assertEquals(db.getCategoryByName("Work").getCategoryUniqueId(), migrated.getCategoryId());
        assertEquals("starred", migrated.getIsStarred());
        assertEquals("Retain me too", db.fetchNote("_id = ?", new String[]{"74"}).getNoteContent());
    }
    @Test public void timestampsAreParsedAsNumbersAndMalformedValuesAreSafe() {
        assertEquals(100L, note("a", "A", "100").lastEditedMillis());
        assertEquals(0L, note("b", "B", null).lastEditedMillis());
        assertEquals(0L, note("c", "C", "broken").lastEditedMillis());
    }
    @Test public void legacyLocalBackupImportsWithoutReplacingExistingNotes() throws Exception {
        db.upsertNotesListByUniqueId(notes(note("keep", "Keep existing", "100")));
        java.io.File backup = java.io.File.createTempFile("legacy", ".db", app.getCacheDir());
        try {
            try (SQLiteDatabase source = SQLiteDatabase.openOrCreateDatabase(backup, null)) {
                source.execSQL("CREATE TABLE mainNotes (_id INTEGER PRIMARY KEY, noteTitle TEXT, noteContent TEXT, creationDate TEXT, noteCategory TEXT, categoryColor TEXT)");
                source.execSQL("INSERT INTO mainNotes VALUES (5, 'Legacy', 'Recovered', '2017', 'Archive', '#112233')");
            }
            assertEquals(1, com.shahid.nid.Utils.LegacyBackupReader.restore(backup, db));
            assertEquals(0, com.shahid.nid.Utils.LegacyBackupReader.restore(backup, db));
            assertEquals(2, db.fetchAllNotesFromDb(null).size());
            assertEquals("Keep existing", find("keep").getNoteTitle());
            assertNotNull(db.getCategoryByName("Archive"));
        } finally { backup.delete(); }
    }

    @Test public void driveClientCanBeCreatedWithTheResolvedDependencies() {
        com.google.api.services.drive.Drive drive = new com.google.api.services.drive.Drive.Builder(
                new com.google.api.client.http.javanet.NetHttpTransport(),
                new com.google.api.client.json.gson.GsonFactory(), request -> {})
                .setApplicationName("Scrittor tests").build();
        assertNotNull(new com.shahid.nid.Utils.DriveServiceHelper(drive));
    }

}
