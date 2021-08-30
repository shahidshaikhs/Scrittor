package com.shahid.nid.Activties;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.util.Pair;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.gson.JsonObject;
import com.shahid.nid.Categories.CategoriesNotesContract;
import com.shahid.nid.Categories.Category;
import com.shahid.nid.Note;
import com.shahid.nid.NotesContract;
import com.shahid.nid.R;
import com.shahid.nid.Utils.DbHelper;
import com.shahid.nid.Utils.DriveServiceHelper;
import com.shahid.nid.Utils.GsonHelper;
import com.shahid.nid.databinding.ActivityDriveBackupBinding;
import com.shahid.nid.executors.AppExecutors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Junaid Gandhi on 08/25/2021.
 *
 * Activity to backup and restore notes and categories to drive
 */
public class DriveBackupActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_SIGN_IN = 1;

    private static final String DRIVE_NOTES_BACKUP_FILE_NAME = "new_scritor_notes_notes_backup";

    private static final String DRIVE_CATEGORIES_BACKUP_FILE_NAME = "new_scrittor_categories_backup";

    private DriveServiceHelper mDriveServiceHelper;

    private ActivityDriveBackupBinding rootBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootBinding = ActivityDriveBackupBinding.inflate(getLayoutInflater());
        setContentView(rootBinding.getRoot());

        // Authenticate the user. For most apps, this should be done when the user performs an
        // action that requires Drive access rather than in onCreate.
        requestSignIn();

        initViews();
    }

    private void initViews() {
        rootBinding.backupTitle.setOnClickListener(this);
        rootBinding.backupSubtitle.setOnClickListener(this);
        rootBinding.restoreTitle.setOnClickListener(this);
        rootBinding.restoreSubtitle.setOnClickListener(this);

        rootBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }

    /**
     * Starts a sign-in activity using {@link #REQUEST_CODE_SIGN_IN}.
     */
    private void requestSignIn() {
        Log.d(getClass().getName(), "Requesting sign-in");

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    /**
     * Handles the {@code result} of a completed sign-in activity initiated from {@link
     * #requestSignIn()}.
     */
    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    this, Collections.singleton(DriveScopes.DRIVE_APPDATA));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive googleDriveService =
                            new Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    new GsonFactory(),
                                    credential)
                                    .setApplicationName("Scrittor - A simple note app")
                                    .build();

                    // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                    // Its instantiation is required before handling any onClick actions.
                    mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
                    rootBinding.loadingCard.setVisibility(View.GONE);
                    Snackbar.make(findViewById(android.R.id.content), "Sign-in successful!", Snackbar.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    rootBinding.loadingCard.setVisibility(View.GONE);
                    Snackbar.make(findViewById(android.R.id.content), "Sign-in failed! Please try again later!", Snackbar.LENGTH_SHORT).show();
                });
    }

    private void startBackup() {
        updateProgressUi(getString(R.string.checking_backup), false);
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                FileList files = null;
                try {
                    files = mDriveServiceHelper.queryFiles("nextPageToken, files(id, name, modifiedTime)", "name contains '" + DRIVE_CATEGORIES_BACKUP_FILE_NAME + "' or name contains '" + DRIVE_NOTES_BACKUP_FILE_NAME + "'", null);
                    // If new backup format is not present then check in old backup format
                    if (files.getFiles().isEmpty()) { // No backup present, create new
                        updateProgressUi(getString(R.string.no_prev_backup_found), false);
                        String categoriesId = createFile(DRIVE_CATEGORIES_BACKUP_FILE_NAME);
                        String notesId = createFile(DRIVE_NOTES_BACKUP_FILE_NAME);
                        if (categoriesId != null && notesId != null){
                            String[] noteProjection = {
                                    NotesContract.MainNotes.COLUMN_CATEGORY,
                                    NotesContract.MainNotes.COLUMN_CATEGORY_COLOR,
                                    NotesContract.MainNotes.COLUMN_CATEGORY_ID,
                                    NotesContract.MainNotes.COLUMN_NAME_CONTENT,
                                    NotesContract.MainNotes.COLUMN_DATE,
                                    NotesContract.MainNotes.COLUMN_LAST_EDITED,
                                    NotesContract.MainNotes.COLUMN_NAME_TITLE,
                                    NotesContract.MainNotes.COLUMN_STARRED_CHECK,
                                    NotesContract.MainNotes.COLUMN_UNIQUE_NOTE_ID
                            };
                            updateProgressUi(getString(R.string.backing_up_notes), false);
                            ArrayList<Note> noteList = DbHelper.getInstance(getApplication()).fetchAllNotesFromDb(noteProjection);
                            File notesBackupFile = mDriveServiceHelper.saveFile(notesId, DRIVE_NOTES_BACKUP_FILE_NAME, GsonHelper.getInstance().toJson(noteList));

                            updateProgressUi(getString(R.string.backing_up_categories), false);
                            String[] catgProjection = {
                                    CategoriesNotesContract.CategoriesContract.COLUMN_NAME_CATEGORY,
                                    CategoriesNotesContract.CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID,
                                    CategoriesNotesContract.CategoriesContract.COLUMN_COLOR,
                                    CategoriesNotesContract.CategoriesContract.COLUMN_DESCRIPTION_CATEGORY
                            };
                            ArrayList<Category> categoryList = DbHelper.getInstance(getApplication()).fetchAllCategoriesFromDb(catgProjection);
                            File categoriesBackupFile = mDriveServiceHelper.saveFile(categoriesId, DRIVE_CATEGORIES_BACKUP_FILE_NAME, GsonHelper.getInstance().toJson(categoryList));

                            updateProgressUi("", true);
                            showSnackBar(getString(R.string.backup_complete));
                        }
                    } else {
                        updateProgressUi(getString(R.string.prev_backup_found), false);
                        for (File file : files.getFiles()){
//                            mDriveServiceHelper.deleteFile(file.getId());
                            if (file.getName().equals(DRIVE_NOTES_BACKUP_FILE_NAME)){
                                String[] noteProjection = {
                                        NotesContract.MainNotes.COLUMN_CATEGORY,
                                        NotesContract.MainNotes.COLUMN_CATEGORY_COLOR,
                                        NotesContract.MainNotes.COLUMN_CATEGORY_ID,
                                        NotesContract.MainNotes.COLUMN_NAME_CONTENT,
                                        NotesContract.MainNotes.COLUMN_DATE,
                                        NotesContract.MainNotes.COLUMN_LAST_EDITED,
                                        NotesContract.MainNotes.COLUMN_NAME_TITLE,
                                        NotesContract.MainNotes.COLUMN_STARRED_CHECK,
                                        NotesContract.MainNotes.COLUMN_UNIQUE_NOTE_ID
                                };
                                updateProgressUi(getString(R.string.backing_up_notes), false);
                                ArrayList<Note> noteList = DbHelper.getInstance(getApplication()).fetchAllNotesFromDb(noteProjection);
                                File notesBackupFile = mDriveServiceHelper.saveFile(file.getId(), DRIVE_NOTES_BACKUP_FILE_NAME, GsonHelper.getInstance().toJson(noteList));
                            } else if (file.getName().equals(DRIVE_CATEGORIES_BACKUP_FILE_NAME)){
                                updateProgressUi(getString(R.string.backing_up_categories), false);
                                String[] catgProjection = {
                                        CategoriesNotesContract.CategoriesContract.COLUMN_NAME_CATEGORY,
                                        CategoriesNotesContract.CategoriesContract.COLUMN_CATEGORY_UNIQUE_ID,
                                        CategoriesNotesContract.CategoriesContract.COLUMN_COLOR,
                                        CategoriesNotesContract.CategoriesContract.COLUMN_DESCRIPTION_CATEGORY
                                };
                                ArrayList<Category> categoryList = DbHelper.getInstance(getApplication()).fetchAllCategoriesFromDb(catgProjection);
                                File categoriesBackupFile = mDriveServiceHelper.saveFile(file.getId(), DRIVE_CATEGORIES_BACKUP_FILE_NAME, GsonHelper.getInstance().toJson(categoryList));
                                updateProgressUi("", true);
                                showSnackBar(getString(R.string.backup_complete));
                            }
                        }

                        updateProgressUi("", true);
                        showSnackBar(getString(R.string.backup_complete));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Creates a new file via the Drive REST API.
     */
    private String createFile(String fileName) throws IOException{
        if (mDriveServiceHelper != null) {
            Log.d(getClass().getName(), "Creating a file.");
            String id = mDriveServiceHelper.createFile("appDataFolder", "application/json", fileName);
            if (id != null && !id.isEmpty()){
                return id;
            } else {
                return null;
            }
        }
        return null;
    }

    private void getBackupFiles() {
        updateProgressUi(getString(R.string.finding_backup_on_drive), false);
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                FileList files = null;
                try {
                    files = mDriveServiceHelper.queryFiles("nextPageToken, files(id, name, modifiedTime)", "name contains '" + DRIVE_CATEGORIES_BACKUP_FILE_NAME + "' or name contains '" + DRIVE_NOTES_BACKUP_FILE_NAME + "'", null);

                    // If new backup format is not present then check in old backup format
                    if (files.getFiles().isEmpty()) {
                        getOldBackupFiles();
                        return;
                    }
                    updateProgressUi(getString(R.string.backup_found), false);

                    ArrayList<Category> categories = new ArrayList<>();
                    ArrayList<Note> notes = new ArrayList<>();
                    for (File file : files.getFiles()) {
                        if (file.getName().contains(DRIVE_CATEGORIES_BACKUP_FILE_NAME)) {
                            updateProgressUi(getString(R.string.downloading_categories), false);
                            categories.addAll(GsonHelper.getInstance().getCategoryListFromJson(mDriveServiceHelper.getFileContentsSynchronously(file.getId())));
                        } else if (file.getName().contains(DRIVE_NOTES_BACKUP_FILE_NAME)) {
                            updateProgressUi(getString(R.string.downloading_notes), false);
                            notes.addAll(GsonHelper.getInstance().getNoteListFromJson(mDriveServiceHelper.getFileContentsSynchronously(file.getId())));
                        }
                    }

                    updateProgressUi(getString(R.string.num_of_notes_category_downloaded, notes.size(), categories.size(), (notes.size() + categories.size()), notes.size() + categories.size()), false);
                    initiateRestoreOnLocalDb(notes, categories);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Fetches backup made by legacy drive android api...
     */
    private void getOldBackupFiles() {
        updateProgressUi(getString(R.string.finding_backup_on_drive), false);
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                FileList files = null;
                try {
                    String nextPageToken = null;
                    HashMap<String, String> fileNameIdMap = new HashMap<>();
                    HashMap<String, Long> lastModifiedMap = new HashMap<>();
                    do {
                        files = mDriveServiceHelper.queryFiles("nextPageToken, files(id, name, modifiedTime)", null, nextPageToken);

                        if (files.getFiles().isEmpty()) {
                            showSnackBar(getString(R.string.no_backup_found));
                            return;
                        }

                        for (File file : files.getFiles()) {
                            if (lastModifiedMap.containsKey(file.getName()) && lastModifiedMap.get(file.getName()).longValue() < file.getModifiedTime().getValue()) {
                                lastModifiedMap.put(file.getName(), file.getModifiedTime().getValue());
                                fileNameIdMap.put(file.getName(), file.getId());
                            } else if (!lastModifiedMap.containsKey(file.getName())) {
                                lastModifiedMap.put(file.getName(), file.getModifiedTime().getValue());
                                fileNameIdMap.put(file.getName(), file.getId());
                            }
                        }
                        nextPageToken = files.getNextPageToken();
                    } while (nextPageToken != null);

                    // Update ui based on number of backup files found
                    updateProgressUi(getString(R.string.num_of_backup_files_found, fileNameIdMap.size()), false);

                    // fetch files and segregate them according to their models
                    ArrayList<Category> categories = new ArrayList<>();
                    ArrayList<Note> notes = new ArrayList<>();
                    for (String name : fileNameIdMap.keySet()) {
                        JsonObject jsonObject = GsonHelper.getInstance().getJsonObject(mDriveServiceHelper.getFileContentsSynchronously(fileNameIdMap.get(name)));
                        if (jsonObject.has("categoryUniqueId")) {
                            categories.add(GsonHelper.getInstance().getCategoryObject(jsonObject.toString()));
                        } else {
                            notes.add(GsonHelper.getInstance().getNoteObject(jsonObject.toString()));
                        }
                        updateProgressUi(getString(R.string.num_of_notes_category_downloaded, notes.size(), categories.size(), (notes.size() + categories.size()), fileNameIdMap.size()), false);
                    }

                    updateProgressUi(getString(R.string.restoring_fetched_data_to_db), false);
                    initiateRestoreOnLocalDb(notes, categories);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @WorkerThread
    private void initiateRestoreOnLocalDb(ArrayList<Note> notes, ArrayList<Category> categories) {
        DbHelper dbHelper = DbHelper.getInstance(getApplication());
        updateProgressUi(getString(R.string.updating_categories_in_local_db), false);
        int categoriesUpdated = dbHelper.upsertCategoryListBy(categories, CategoriesNotesContract.CategoriesContract.COLUMN_NAME_CATEGORY + " = ?", null);
        HashMap<String, Category> updatedCategories = dbHelper.fetchAllCategoriesFromDb(null, CategoriesNotesContract.CategoriesContract.COLUMN_NAME_CATEGORY);
        notes.addAll(dbHelper.fetchAllNotesFromDb(null));
        updateProgressUi(getString(R.string.updating_notes_in_local_db), false);
        for (Note note : notes) {
            Log.e("notes", GsonHelper.getInstance().toJson(note));
            if (note.getCategoryName() == null){
                note.setCategoryName("Not Specified");
            }
            note.setCategoryId(updatedCategories.get(note.getCategoryName()).getCategoryUniqueId());
            note.setCategoryColor(updatedCategories.get(note.getCategoryName()).getCategoryColor());
        }
        int notesUpdated = dbHelper.upsertNotesListByUniqueId(notes);
        updateProgressUi("", true);
        showSnackBar(getString(R.string.db_update_complete, notesUpdated, categoriesUpdated));
    }

    private void updateProgressUi(@NonNull String msg, boolean isTaskComplete) {
        AppExecutors.getInstance().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                if (isTaskComplete) {
                    rootBinding.loadingCard.setVisibility(View.GONE);
                    return;
                }
                rootBinding.loadingCard.setVisibility(View.VISIBLE);
                rootBinding.loadingText.setText(msg);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (mDriveServiceHelper == null) {
            requestSignIn();
            return;
        }
        switch (v.getId()) {
            case R.id.backupTitle:
            case R.id.backupSubtitle:
                startBackup();
                // TODO backup files
                break;

            case R.id.restoreTitle:
            case R.id.restoreSubtitle:
                getBackupFiles();
                break;
        }
    }


}