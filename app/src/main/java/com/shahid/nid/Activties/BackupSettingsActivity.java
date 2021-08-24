package com.shahid.nid.Activties;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.gson.Gson;
import com.shahid.nid.Categories.CategoriesDataStructure;
import com.shahid.nid.Categories.CategoriesDbHelper;
import com.shahid.nid.Categories.CategoriesNotesContract;
import com.shahid.nid.NoteDataStructure;
import com.shahid.nid.NotesContract;
import com.shahid.nid.NotesDbHelper;
import com.shahid.nid.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By M Junaid on 22/10/2018
 */
public class BackupSettingsActivity extends BaseDriveActivity {

    private Map<String, ?> noteIdsDeleted;

    private Map<String, ?> noteIdsToBackup;

    private ArrayList<NoteDataStructure> noteList = new ArrayList<>(); // used for backing up

    private ArrayList<CategoriesDataStructure> categoryList = new ArrayList<>(); // used for backing up

    private ArrayList<NoteDataStructure> notesToRestoreList = new ArrayList<>(); // used for restore

    private ArrayList<CategoriesDataStructure> categoriesToRestoreList = new ArrayList<>(); // used for restore

    private Map<String, Metadata> notesAndCategoriesInAppFolder = new HashMap<>();

    private AsyncTask<Void, Void, Void> backupTask;

    private AsyncTask<Void, Void, Void> restoreTask;

    boolean isAppFolderContentFetched = false;

    short notesIterator = 0;
    short categoriesIterator = 0;

    private ProgressBar progressBar;
    private TextView currentOperationTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefsTheme = getSharedPreferences(getResources().getString(R.string.MY_PREFS_THEME), MODE_PRIVATE);
        String theme = prefsTheme.getString("theme", "not_defined");
        if (theme.equals("dark")) {
            getTheme().applyStyle(R.style.OverlayPrimaryColorDark, true);
        } else if (theme.equals("light")) {
            getTheme().applyStyle(R.style.OverlayPrimaryColorLight, true);
        } else if (theme.equals("amoled")) {
            getTheme().applyStyle(R.style.OverlayPrimaryColorAmoled, true);
        } else {
            getTheme().applyStyle(R.style.OverlayPrimaryColorDark, true);
        }

        setContentView(R.layout.activity_backup_settings);

        progressBar = findViewById(R.id.loading_bar);
        currentOperationTv = findViewById(R.id.loading_text);

        noteIdsToBackup = getSharedPreferences(getString(R.string.last_edited_base_shared_pref), MODE_PRIVATE).getAll(); // List of notes to be updated
        noteIdsDeleted = getSharedPreferences(getString(R.string.deleted_notes_base_pref), MODE_PRIVATE).getAll();

        ImageView backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onDriveClientReady() {
        initiateDrive();
    }

    /**
     * this method should be called before any other click event is called .. and it should be successful..
     */
    private void initiateDrive() {
        getDriveClient().requestSync().addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getFilesInAppFolder();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                currentOperationTv.setVisibility(View.GONE);
//                Log.e("RETYRT", e.getMessage());
                Snackbar.make(findViewById(android.R.id.content), R.string.init_failed, Snackbar.LENGTH_INDEFINITE).setAction(R.string.drive_try_again, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signIn();
                        progressBar.setVisibility(View.VISIBLE);
                        currentOperationTv.setVisibility(View.VISIBLE);
                    }
                }).show();

                // TODO show retry option and call initiateDrive() method again from here on clicking retry
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void backupSettingsClickHandler(View view) {
        switch (view.getId()) {
            case R.id.cloud_backup_box:
                if (!isAppFolderContentFetched) {
                    Toast.makeText(this, R.string.drive_sync, Toast.LENGTH_LONG).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                currentOperationTv.setVisibility(View.VISIBLE);
                currentOperationTv.setText(R.string.drive_backing_up);
                // TODO Show progress bar or something that backup is running and prevent clicks on views and prevent even backpress
                backupTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        getAllNotesList();
                        getAllCategories();
                        backupNotesAndCategories();
                        return null;
                    }
                };

                backupTask.execute();
                break;
            case R.id.restore_notes_box:
                if (!isAppFolderContentFetched) {
                    Toast.makeText(this, R.string.drive_sync, Toast.LENGTH_LONG).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                currentOperationTv.setVisibility(View.VISIBLE);
                currentOperationTv.setText(R.string.drive_restoring);
                // TODO Show progress bar or something that restore is running and prevent clicks on views and prevent even backPress
                restoreTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        getAllNotesList();
                        getAllCategories();
                        restoreNotesAndCategories();
                        return null;
                    }
                };
                restoreTask.execute();
                break;
        }
    }

    /**
     * Backup's all notes and categories sequentially one by one since the drive operations work asynchronously using while or for loop isn't feasible. It sends one file to backup and when it succeeds this method is called again.
     */
    private void backupNotesAndCategories() {
        Gson gson = new Gson();

        if (notesIterator < noteList.size()) { // Trigger this first till all notes are backed up and then move to else to start categories
            NoteDataStructure note = noteList.get(notesIterator);

            if (noteIdsDeleted.containsKey(note.getNoteUniqueId()) && notesAndCategoriesInAppFolder.containsKey(note.getNoteUniqueId())) {
                deleteFile(notesAndCategoriesInAppFolder.get(note.getNoteUniqueId()).getDriveId().asDriveFile());
            }

            if (noteIdsToBackup.containsKey(note.getNoteUniqueId())) {

                note.setLastEdited(noteIdsToBackup.get(note.getNoteUniqueId()).toString());
                String json = gson.toJson(note);
                if (notesAndCategoriesInAppFolder.containsKey(note.getNoteUniqueId())) {
                    // TODO rewrite contents
                    rewriteContents(notesAndCategoriesInAppFolder.get(note.getNoteUniqueId()).getDriveId().asDriveFile(), json);
                } else {
                    // TODO create file
                    createFileInAppFolder(json, note.getNoteUniqueId());
                }

            }

            notesIterator++;
            if (!noteIdsToBackup.containsKey(note.getNoteUniqueId())) {
                backupNotesAndCategories();
            }

        } else if (categoriesIterator < categoryList.size()) {
            // Todo START backing up categories
            CategoriesDataStructure category = categoryList.get(categoriesIterator);
            String json = gson.toJson(category);
            if (notesAndCategoriesInAppFolder.containsKey(category.getCategoryName())) {
                rewriteContents(notesAndCategoriesInAppFolder.get(category.getCategoryName()).getDriveId().asDriveFile(), json);
            } else {
                createFileInAppFolder(json, category.getCategoryName());
            }

            categoriesIterator++;
        } else {
            // TODO HIDE the progress or whatever you displayed while starting backup.
            getSharedPreferences(getString(R.string.deleted_notes_base_pref), MODE_PRIVATE).edit().clear().apply();
            progressBar.setVisibility(View.GONE);
            currentOperationTv.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.drive_backup_note_complete) + notesIterator + getString(R.string.drive_backup_categories_complete) + categoriesIterator, Toast.LENGTH_LONG).show();
        }
    }

    short restoreIterator = 0;

    /**
     * Update your local Db from drive backup
     */
    private void restoreNotesAndCategories() {
        ArrayList<String> keyList = new ArrayList<>(notesAndCategoriesInAppFolder.keySet());

        if (restoreIterator < notesAndCategoriesInAppFolder.size()) {
            retrieveContents(notesAndCategoriesInAppFolder.get(keyList.get(restoreIterator)).getDriveId().asDriveFile());
            restoreIterator++;
        } else {
            updateDatabaseFromRestoreData();
        }

    }

    /**
     * Update your local database
     */
    private void updateDatabaseFromRestoreData() {
        short totalNotesRestored = 0;
        short totalCatRestored = 0;

        CategoriesDbHelper catgDbHelper = new CategoriesDbHelper(this);
        SQLiteDatabase catgDb = catgDbHelper.getWritableDatabase();

        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < categoriesToRestoreList.size(); i++) {
            CategoriesDataStructure category = categoriesToRestoreList.get(i);
            map.put(category.getCategoryName(), category.getCategoryUniqueId());

            String whereNameClause = CategoriesNotesContract.categoriesContract.COLUMN_NAME_CATEGORY + " = ?";
            String[] whereNameValues = new String[]{category.getCategoryName()};

            String whereUniqueIdClause = CategoriesNotesContract.categoriesContract.COLUMN_CATEGORY_UNIQUE_ID + " = ?";
            String[] whereUniqueIdValues = new String[]{category.getCategoryUniqueId()};

            ContentValues values = new ContentValues();
            values.put(CategoriesNotesContract.categoriesContract.COLUMN_NAME_CATEGORY, category.getCategoryName());
            values.put(CategoriesNotesContract.categoriesContract.COLUMN_COLOR, category.getCategoryColor());
            values.put(CategoriesNotesContract.categoriesContract.COLUMN_DESCRIPTION_CATEGORY, category.getDescription());
            values.put(CategoriesNotesContract.categoriesContract.COLUMN_CATEGORY_UNIQUE_ID, category.getCategoryUniqueId());

            if (catgDb.update(CategoriesNotesContract.categoriesContract.TABLE_NAME, values, whereNameClause, whereNameValues) <= 0 || catgDb.update(CategoriesNotesContract.categoriesContract.TABLE_NAME, values, whereUniqueIdClause, whereUniqueIdValues) <= 0) { // find by name first, if not found then find by unique ID
                catgDb.insert(CategoriesNotesContract.categoriesContract.TABLE_NAME, null, values);
            }

            totalCatRestored++;
        }

        catgDb.close();
        catgDbHelper.close();

        NotesDbHelper mDbHelper = new NotesDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        for (int i = 0; i < notesToRestoreList.size(); i++) {
            NoteDataStructure note = notesToRestoreList.get(i);

            if (noteIdsDeleted.containsKey(note.getNoteUniqueId())) {
                continue;
            }

            String whereClause = NotesContract.mainNotes.COLUMN_UNIQUE_NOTE_ID + " = ?";
            String[] whereValues = new String[]{note.getNoteUniqueId()};

            ContentValues values = new ContentValues();
            values.put(NotesContract.mainNotes.COLUMN_NAME_TITLE, note.getNoteTitle());
            values.put(NotesContract.mainNotes.COLUMN_NAME_CONTENT, note.getNoteContent());
            values.put(NotesContract.mainNotes.COLUMN_DATE, note.getCreationDate());
            values.put(NotesContract.mainNotes.COLUMN_CATEGORY, note.getCategoryName());
            if (map.containsKey(note.getCategoryName())) {
                note.setCategoryId(map.get(note.getCategoryName())); // This will update category unique id saved in notes if category with same name is created on another device
            }
            values.put(NotesContract.mainNotes.COLUMN_CATEGORY_ID, note.getCategoryId());
            values.put(NotesContract.mainNotes.COLUMN_CATEGORY_COLOR, note.getCategoryColor());
            values.put(NotesContract.mainNotes.COLUMN_STARRED_CHECK, note.getIsStarred());
            values.put(NotesContract.mainNotes.COLUMN_UNIQUE_NOTE_ID, note.getNoteUniqueId()); // TODO added this.. it will help identify unique notes across devices
            values.put(NotesContract.mainNotes.COLUMN_LAST_EDITED, note.getLastEdited());

            if (db.update(NotesContract.mainNotes.TABLE_NAME, values, whereClause, whereValues) <= 0) {
                db.insert(NotesContract.mainNotes.TABLE_NAME, null, values);
            }
            totalNotesRestored++;
        }

        db.close();
        mDbHelper.close();
        String toastString = getString(R.string.drive_restore_notes) + totalNotesRestored + getString(R.string.drive_restore_categories) + totalCatRestored;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                currentOperationTv.setVisibility(View.GONE);
                Toast.makeText(BackupSettingsActivity.this, toastString, Toast.LENGTH_LONG).show();
            }
        });

        // TODO HIDE the progress or whatever you displayed while starting restore.
        finish();
    }

    /**
     * Fetch all categories from local DB
     */
    private void getAllCategories() {
        CategoriesDbHelper mDbHelper = new CategoriesDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                CategoriesNotesContract.categoriesContract.TABLE_NAME,                     // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(CategoriesNotesContract.categoriesContract.COLUMN_NAME_CATEGORY));
            String color = cursor.getString(cursor.getColumnIndexOrThrow(CategoriesNotesContract.categoriesContract.COLUMN_COLOR));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(CategoriesNotesContract.categoriesContract.COLUMN_DESCRIPTION_CATEGORY));
            String uniqueId = cursor.getString(cursor.getColumnIndexOrThrow(CategoriesNotesContract.categoriesContract.COLUMN_CATEGORY_UNIQUE_ID));

            CategoriesDataStructure category = new CategoriesDataStructure();

            category.setCategoryName(name);
            category.setCategoryColor(color);
            category.setDescription(description);
            category.setCategoryUniqueId(uniqueId);
            categoryList.add(category);
        }

        cursor.close();
        db.close();
        mDbHelper.close();
    }

    /**
     * Fetch all notes from local DB
     */
    private void getAllNotesList() {
        NotesDbHelper mDbHelper = new NotesDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                NotesContract.mainNotes.TABLE_NAME,                     // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

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

            String uniqueId = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.mainNotes.COLUMN_UNIQUE_NOTE_ID));

            String lastEdited = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.mainNotes.COLUMN_LAST_EDITED));

            int noteID = cursor.getInt(
                    cursor.getColumnIndexOrThrow(NotesContract.mainNotes._ID));

            NoteDataStructure note = new NoteDataStructure(title, content, creationDate, noteID);

            note.setCategoryId(categoryId);
            note.setCategoryColor(categoryColor);
            note.setCategoryName(categoryName);
            note.setIsStarred(isStarred);
            note.setNoteUniqueId(uniqueId);
            note.setLastEdited(lastEdited);
            noteList.add(note);

        }

        cursor.close();
        db.close();
        mDbHelper.close();
    }

    /**
     * Get Files present in app folder
     */
    private void getFilesInAppFolder() {
        getDriveResourceClient().getAppFolder().addOnSuccessListener(this, new OnSuccessListener<DriveFolder>() {
            @Override
            public void onSuccess(DriveFolder driveFolder) {
                Query query = new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                        .build();
                // [START drive_android_query_children]
                Task<MetadataBuffer> queryTask = getDriveResourceClient().queryChildren(driveFolder, query);
                // END drive_android_query_children]
                queryTask
                        .addOnSuccessListener(BackupSettingsActivity.this, new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {

                                for (Metadata metadata : metadataBuffer) {
                                    notesAndCategoriesInAppFolder.put(metadata.getTitle(), metadata);
                                }

                                isAppFolderContentFetched = true;

                                progressBar.setVisibility(View.GONE);
                                currentOperationTv.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(BackupSettingsActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // TODO SHOW message that something went wrong and show retry option and call getFilesInAppFolder() method on clicking retry
                                Toast.makeText(getApplicationContext(), R.string.drive_error_occured, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    /**
     * Retrieve contents of the given drive file
     *
     * @param file
     */
    private void retrieveContents(DriveFile file) {
        Task<DriveContents> openFileTask =
                getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY);
        openFileTask
                .continueWithTask(task -> {
                    DriveContents contents = task.getResult();
                    // Process contents...
                    // [START drive_android_read_as_string]
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(contents.getInputStream()))) {
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line).append("\n");
                        }

                        Gson gson = new Gson();
                        String json = builder.toString();
                        if (json.contains("noteTitle")) {
                            notesToRestoreList.add(gson.fromJson(json, NoteDataStructure.class));
                        } else {
                            categoriesToRestoreList.add(gson.fromJson(json, CategoriesDataStructure.class));
                        }
                    }

                    return getDriveResourceClient().discardContents(contents);
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        restoreNotesAndCategories();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(getApplicationContext(), R.string.drive_error_occured, Toast.LENGTH_SHORT).show();
                    // TODO SHOW message that something went wrong and enable clicks and all again here
                });
    }

    /**
     * Creates a new file in drives app folder and fill the with contents from given string object
     *
     * @param objectUniqueId file name
     * @param objectInJson   - Content for the file
     */
    private void createFileInAppFolder(String objectInJson, String objectUniqueId) {
        final Task<DriveFolder> appFolderTask = getDriveResourceClient().getAppFolder();
        final Task<DriveContents> createContentsTask = getDriveResourceClient().createContents();
        Tasks.whenAll(appFolderTask, createContentsTask)
                .continueWithTask(new Continuation<Void, Task<DriveFile>>() {
                    @Override
                    public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
                        DriveFolder parent = appFolderTask.getResult();
                        DriveContents contents = createContentsTask.getResult();
                        OutputStream outputStream = contents.getOutputStream();
                        try (Writer writer = new OutputStreamWriter(outputStream)) {
                            writer.write(objectInJson);
                        }

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle(objectUniqueId)
                                .setMimeType("text/plain")
                                .setStarred(false)
                                .build();

                        return getDriveResourceClient().createFile(parent, changeSet, contents);
                    }
                })
                .addOnSuccessListener(this,
                        new OnSuccessListener<DriveFile>() {
                            @Override
                            public void onSuccess(DriveFile driveFile) {
                                backupNotesAndCategories();
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BackupSettingsActivity.this, R.string.create_failed, Toast.LENGTH_LONG).show();
                        // TODO SHOW message that app wasn't able to create file on drive and enable clicks and all again here
                        Toast.makeText(getApplicationContext(), R.string.drive_error_occured, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * @param file         file to update content
     * @param objectInJson - updated content to write to the provided file
     */
    private void rewriteContents(DriveFile file, String objectInJson) {
        // [START drive_android_open_for_write]
        Task<DriveContents> openTask =
                getDriveResourceClient().openFile(file, DriveFile.MODE_WRITE_ONLY);
        // [END drive_android_open_for_write]
        // [START drive_android_rewrite_contents]
        openTask.continueWithTask(task -> {
            DriveContents driveContents = task.getResult();
            try (OutputStream out = driveContents.getOutputStream()) {
                out.write(objectInJson.getBytes());
            }
            // [START drive_android_commit_content]
            Task<Void> commitTask =
                    getDriveResourceClient().commitContents(driveContents, null);
            // [END drive_android_commit_content]
            return commitTask;
        })
                .addOnSuccessListener(this,
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                backupNotesAndCategories();
                            }
                        })
                .addOnFailureListener(this, e -> {
                    Toast.makeText(BackupSettingsActivity.this, R.string.rewrite_failed, Toast.LENGTH_LONG).show();
                    // TODO SHOW message that app wasn't able to update contents on drive and enable clicks and all again here
//                    showMessage(getString(R.string.content_update_failed));
                });
        // [END drive_android_rewrite_contents]
    }

    private void deleteFile(DriveFile file) {
        // [START drive_android_delete_file]
        getDriveResourceClient()
                .delete(file)
                .addOnSuccessListener(this,
                        aVoid -> {
                        })
                .addOnFailureListener(this, e -> {
                    finish();
                });
        // [END drive_android_delete_file]
    }
}
