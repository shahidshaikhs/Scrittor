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
import com.google.api.client.http.javanet.NetHttpTransport;
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
    private static final String SNAPSHOT_FILE_NAME = "scrittor_backup_v1.json";

    private static final String DRIVE_NOTES_BACKUP_FILE_NAME = "new_scritor_notes_notes_backup";

    private static final String DRIVE_CATEGORIES_BACKUP_FILE_NAME = "new_scrittor_categories_backup";

    private DriveServiceHelper mDriveServiceHelper;
    private boolean operationRunning;

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
                getOnBackPressedDispatcher().onBackPressed();
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
                    if (isFinishing() || isDestroyed()) return;

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    this, Collections.singleton(DriveScopes.DRIVE_APPDATA));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive googleDriveService =
                            new Drive.Builder(
                                    new NetHttpTransport(),
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
                    if (isFinishing() || isDestroyed()) return;
                    rootBinding.loadingCard.setVisibility(View.GONE);
                    Snackbar.make(findViewById(android.R.id.content), "Sign-in failed! Please try again later!", Snackbar.LENGTH_SHORT).show();
                });
    }

    private HashMap<String, File> findBackupFiles() throws IOException {
        HashMap<String, File> latest = new HashMap<>();
        String page = null;
        do {
            FileList result = mDriveServiceHelper.queryFiles("nextPageToken, files(id, name, modifiedTime)",
                    "trashed = false and (name = '" + DRIVE_CATEGORIES_BACKUP_FILE_NAME
                            + "' or name = '" + DRIVE_NOTES_BACKUP_FILE_NAME + "' or name = '" + SNAPSHOT_FILE_NAME + "')", page);
            for (File file : result.getFiles()) {
                File previous = latest.get(file.getName());
                if (previous == null || (file.getModifiedTime() != null &&
                        (previous.getModifiedTime() == null || file.getModifiedTime().getValue() > previous.getModifiedTime().getValue()))) {
                    latest.put(file.getName(), file);
                }
            }
            page = result.getNextPageToken();
        } while (page != null);
        return latest;
    }

    private void startBackup() {
        updateProgressUi(getString(R.string.checking_backup), false);
        AppExecutors.getInstance().networkIO().execute(() -> {
            try {
                HashMap<String, File> files = findBackupFiles();
                DbHelper db = DbHelper.getInstance(getApplication());
                String json = com.shahid.nid.Utils.BackupManager.exportJson(db);
                File snapshot = files.get(SNAPSHOT_FILE_NAME);
                if (snapshot == null) mDriveServiceHelper.createJsonFile("appDataFolder", SNAPSHOT_FILE_NAME, json);
                else mDriveServiceHelper.saveFile(snapshot.getId(), SNAPSHOT_FILE_NAME, json);
                showSnackBar(getString(R.string.backup_complete));
            } catch (Exception e) {
                showSnackBar("Backup failed. Please try again.");
            } finally {
                updateProgressUi("", true);
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
        AppExecutors.getInstance().networkIO().execute(() -> {
            boolean legacy = false;
            try {
                HashMap<String, File> files = findBackupFiles();
                if (files.isEmpty()) {
                    legacy = true;
                    getOldBackupFiles();
                    return;
                }
                if (files.containsKey(SNAPSHOT_FILE_NAME)) {
                    int restored = com.shahid.nid.Utils.BackupManager.importJson(DbHelper.getInstance(getApplication()),
                            mDriveServiceHelper.getFileContentsSynchronously(files.get(SNAPSHOT_FILE_NAME).getId()));
                    showSnackBar("Restored " + restored + " notes. Newer local edits were kept.");
                    return;
                }
                if (!files.containsKey(DRIVE_NOTES_BACKUP_FILE_NAME) || !files.containsKey(DRIVE_CATEGORIES_BACKUP_FILE_NAME)) {
                    throw new IOException("Incomplete backup");
                }
                GsonHelper gson = GsonHelper.getInstance();
                ArrayList<Note> notes = gson.getNoteListFromJson(mDriveServiceHelper.getFileContentsSynchronously(files.get(DRIVE_NOTES_BACKUP_FILE_NAME).getId()));
                ArrayList<Category> categories = gson.getCategoryListFromJson(mDriveServiceHelper.getFileContentsSynchronously(files.get(DRIVE_CATEGORIES_BACKUP_FILE_NAME).getId()));
                initiateRestoreOnLocalDb(notes, categories);
            } catch (Exception e) {
                showSnackBar("Restore failed. The backup may be incomplete or invalid.");
            } finally {
                if (!legacy) updateProgressUi("", true);
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
                        files = mDriveServiceHelper.queryFiles("nextPageToken, files(id, name, modifiedTime)", "trashed = false", nextPageToken);

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
                    showSnackBar("Restore failed. Please check your backup and try again.");
                } finally {
                    updateProgressUi("", true);
                }
            }
        });
    }

    @WorkerThread
    private void initiateRestoreOnLocalDb(ArrayList<Note> notes, ArrayList<Category> categories) {
        int notesUpdated = com.shahid.nid.Utils.BackupManager.restore(DbHelper.getInstance(getApplication()), notes, categories);
        showSnackBar(getString(R.string.db_update_complete, notesUpdated, categories.size()));
    }

    private void updateProgressUi(@NonNull String msg, boolean isTaskComplete) {
        AppExecutors.getInstance().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                if (isTaskComplete) operationRunning = false;
                if (isFinishing() || isDestroyed()) return;
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
        if (operationRunning) return;
        if (mDriveServiceHelper == null) {
            requestSignIn();
            return;
        }
        operationRunning = true;
        int id = v.getId();
        if (id == R.id.backupTitle || id == R.id.backupSubtitle) startBackup();
        else if (id == R.id.restoreTitle || id == R.id.restoreSubtitle) getBackupFiles();
        else operationRunning = false;
    }


}