package com.shahid.nid.Utils;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.core.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Junaid Gandhi on 08/25/2021.
 *
 * A utility for performing read/write operations on Drive files via the REST API and opening a
 * file picker UI via Storage Access Framework.
 */
public class DriveServiceHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;

    public DriveServiceHelper(Drive driveService) {
        mDriveService = driveService;
    }

    /**
     * Creates a text file in the user's My Drive folder and returns its file ID.
     */
    @WorkerThread
    public String createFile(String parent, String mimeTye, String fileName) throws IOException {

        File metadata = new File()
                .setParents(Collections.singletonList(parent))
                .setMimeType(mimeTye)
                .setName(fileName);

        File googleFile = mDriveService.files().create(metadata).execute();
        if (googleFile == null) {
            throw new IOException("Null result when requesting file creation.");
        }

        return googleFile.getId();
    }


    public void deleteFile(String fileId) throws IOException {
        mDriveService.files().delete(fileId).execute();
    }


    /**
     * Opens the file identified by {@code fileId} and returns a {@link Pair} of its name and
     * contents.
     */
    public Pair<String, String> readFile(String fileId) throws IOException {
        // Retrieve the metadata as a File object.
        File metadata = mDriveService.files().get(fileId).execute();
        String name = metadata.getName();

        // Stream the file contents to a String.
        InputStream is = mDriveService.files().get(fileId).executeMediaAsInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        String contents = stringBuilder.toString();

        return Pair.create(name, contents);

    }

    /**
     * Updates the file identified by {@code fileId} with the given {@code name} and {@code
     * content}.
     */
    public File saveFile(String fileId, String name, String content) throws IOException {
        // Create a File containing any metadata changes.
        File metadata = new File().setName(name);

        // Convert content to an AbstractInputStreamContent instance.
        ByteArrayContent contentStream = ByteArrayContent.fromString("application/json", content);

        // Update the metadata and contents.
        return mDriveService.files().update(fileId, metadata, contentStream).execute();

    }

    /**
     * Returns a {@link FileList} containing all the visible files in the user's Scrittor app data
     * on Google Drive.
     *
     * @param fields - Fields to be included in result.
     * @param query  - query stirng based on which the files will be filtered and fetched
     */
    @WorkerThread
    public FileList queryFiles(@Nullable String fields, @Nullable String query, @Nullable String nextPageToken) throws IOException {
        Drive.Files.List list = mDriveService.files().list().setPageToken(nextPageToken)
                .setSpaces("appDataFolder")
                .setPageSize(100);
        if (fields != null && !fields.isEmpty()) {
            list.setFields(fields);
        }
        if (query != null && !query.isEmpty()) {
            list.setQ(query);
        }
        return list.execute();
    }

    /**
     * Returns an {@link Intent} for opening the Storage Access Framework file picker.
     */
    public Intent createFilePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        return intent;
    }

    /**
     * Opens the file at the {@code uri} returned by a Storage Access Framework {@link Intent}
     * created by {@link #createFilePickerIntent()} using the given {@code contentResolver}.
     */
    public Task<Pair<String, String>> openFileUsingStorageAccessFramework(
            ContentResolver contentResolver, Uri uri) {
        return Tasks.call(mExecutor, () -> {
            // Retrieve the document's display name from its metadata.
            String name;
            try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    name = cursor.getString(nameIndex);
                } else {
                    throw new IOException("Empty cursor returned for file.");
                }
            }

            // Read the document's contents as a String.
            String content;
            try (InputStream is = contentResolver.openInputStream(uri);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                content = stringBuilder.toString();
            }

            return Pair.create(name, content);
        });
    }

    @WorkerThread
    public String getFileContentsSynchronously(String fileId) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mDriveService.files().get(fileId)
                .executeMediaAndDownloadTo(outputStream);
        return outputStream.toString();

    }

    public Drive getmDriveService() {
        return mDriveService;
    }
}
