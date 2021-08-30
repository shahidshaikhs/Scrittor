package com.shahid.nid.Activties;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.shahid.nid.Adapters.CategoriesRecyclerAdapter;
import com.shahid.nid.Categories.Category;
import com.shahid.nid.Note;
import com.shahid.nid.NotesContract;
import com.shahid.nid.R;
import com.shahid.nid.Utils.DbHelper;
import com.shahid.nid.interfaces.ItemClickListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class AddNotesActivity extends BaseActivity {

    private ImageView cancelButton, doneButton, categoryButton, listButton;
    private EditText titleEditText, descriptionEditText;
    private ImageView deleteButton, starButton, shareButton, addNotification, notesInfo;
    private TextView creationDateTextView;
    private RelativeLayout rootView;
    private TextView wordCountTextView, lastEditedTextView;
    private TextView charCountTextView;
    private Dialog categoryDialog;
    public TextView categoryLabelText, categoryIdentifier, categoryLabelColor;

    /*Integer Variables*/
    private int currentId;
    private int starredCheck = 0;
    private int isDelete=0;
    private int listCheckEnabled = 0;
    private int Unique_Integer_Number;
    private String currentNoteUniqueId = "";

    /*String variables*/
    private String formattedDate;
    private String formattedTime;

    private Note note;
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Dialog notesInfoDialog = new Dialog(AddNotesActivity.this, R.style.Theme_Dialog);
        // Include dialog.xml file
        notesInfoDialog.setContentView(R.layout.dialog_notes_info);
        notesInfoDialog.setTitle(null);

        creationDateTextView = notesInfoDialog.findViewById(R.id.creation_date_text);
        categoryLabelText = notesInfoDialog.findViewById(R.id.category_label);
        wordCountTextView = notesInfoDialog.findViewById(R.id.word_count_text_value);
        charCountTextView = notesInfoDialog.findViewById(R.id.char_count_text_value);
        categoryIdentifier = notesInfoDialog.findViewById(R.id.category_id);
        categoryLabelColor = notesInfoDialog.findViewById(R.id.category_color);

        setContentView(R.layout.activity_add_notes);

        /*Initializing Views Method*/
        initializeView();

        /*Getting Current Date*/
        final Calendar c = Calendar.getInstance();

        /*This is getting the date, when the note is created*/
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        formattedDate = df.format(c.getTime());

        /*This is the time when the note is created*/
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm a", Locale.getDefault());

        formattedTime = date.format(currentLocalTime);

        if (getIntent().hasExtra("noteId")) {
            currentId = Integer.parseInt(getIntent().getExtras().get("noteId").toString());
               /*This method takes a cursor, and later uses the values from it*/
            note = DbHelper.getInstance(getApplication()).fetchNote(NotesContract.MainNotes._ID + " = ?", new String[]{String.valueOf(currentId)});

            getValuesFromDB();
            rootView.setFocusableInTouchMode(true);

        } else {
            note = new Note();
            deleteButton.setVisibility(View.GONE);
            shareButton.setVisibility(View.GONE);
            lastEditedTextView.setVisibility(View.GONE);
        }

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (titleEditText.getText().toString().trim().length() == 0 && descriptionEditText.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "There is nothing to save. Kindly, write something.", Toast.LENGTH_SHORT).show();
                } else {
                    updateNotesData(starredCheck);
                    isDelete =1;
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create custom dialog object
                Dialog deleteDialog = new Dialog(AddNotesActivity.this);
                // Include dialog.xml file
                deleteDialog.setContentView(R.layout.dialog_delete_confirmation);
                deleteDialog.setTitle(null);

                TextView agree, disagree;
                agree = deleteDialog.findViewById(R.id.agree);
                disagree = deleteDialog.findViewById(R.id.disagree);

                agree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Define 'where' part of query.
                        DbHelper.getInstance(getApplication()).deleteNote(note);
                        Toast.makeText(getApplicationContext(), "Your note has been deleted", Toast.LENGTH_LONG).show();
                        getSharedPreferences(getString(R.string.deleted_notes_base_pref), MODE_PRIVATE).edit().putBoolean(currentNoteUniqueId, true).apply();
                        isDelete = 1;
                        finish();
                    }
                });

                disagree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteDialog.dismiss();
                    }
                });

                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                deleteDialog.show();
            }
        });


        categoryButton.setOnClickListener(view -> {
            /*Call the the method which will invoke the dialog to select the category*/
            categoryButtonInvoker();
        });

        shareButton.setOnClickListener(view -> {

            Dialog dialogShare = new Dialog(AddNotesActivity.this);
            // Include dialog.xml file
            dialogShare.setContentView(R.layout.share_dialog_box);
            dialogShare.setTitle(null);

            dialogShare.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogShare.show();

            LinearLayout textExportNote = dialogShare.findViewById(R.id.text_export_txt);
            LinearLayout shareNote = dialogShare.findViewById(R.id.share_notes);

            textExportNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Dexter.withActivity(AddNotesActivity.this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse response) {
                                    saveTextAsFile(titleEditText.getText().toString().trim(), titleEditText.getText().toString().trim() + " - " + descriptionEditText.getText().toString().trim());
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse response) {
                                    Toast.makeText(getBaseContext(), "This permission is required so that the app can export database.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            })
                            .check();

                }
            });

            shareNote.setOnClickListener(v -> {
                dialogShare.dismiss();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, titleEditText.getText().toString() + " - " + descriptionEditText.getText().toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            });

        });

        starButton.setOnClickListener(view -> {
            if (starredCheck == 0) {

                starButton.setImageResource(R.drawable.twotone_star_black_24);
                starButton.setColorFilter(ContextCompat.getColor(AddNotesActivity.this, R.color.color4));
                starredCheck = 1;
                Toast.makeText(getApplicationContext(), "The note has been saved and added to starred list", Toast.LENGTH_LONG).show();
            } else if (starredCheck == 1) {

                starButton.setImageResource(R.drawable.twotone_star_black_24);
                starButton.setColorFilter(ContextCompat.getColor(AddNotesActivity.this, R.color.dividerColor));
                starredCheck = 0;
                Toast.makeText(getApplicationContext(), "The note has been removed from the starred list.", Toast.LENGTH_SHORT).show();

            }
        });


        /*Notification Intent*/
        final Intent intent = new Intent(this, AddNotesActivity.class);
        intent.putExtra("noteId", currentId);

        /*Notification Code*/
        addNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (titleEditText.getText().toString().trim().length() == 0 || descriptionEditText.getText().toString().trim().length() == 0) {

                    Toast.makeText(getApplicationContext(), "Please write something to add note to the notification area.", Toast.LENGTH_SHORT).show();
                } else {

                    RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
                    contentView.setTextViewText(R.id.title, titleEditText.getText().toString());
                    contentView.setTextViewText(R.id.text, descriptionEditText.getText().toString());

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    String NOTIFICATION_CHANNEL_ID = random();


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

                        // Configure the notification channel.
                        notificationChannel.setDescription("Channel description");
                        notificationChannel.setLightColor(Color.RED);
                        notificationChannel.setShowBadge(false);
                        notificationChannel.setSound(null, null);
                        notificationChannel.enableVibration(false);

                        if (notificationManager != null) {
                            notificationManager.createNotificationChannel(notificationChannel);
                        }
                    }


                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(AddNotesActivity.this, NOTIFICATION_CHANNEL_ID);

                    notificationBuilder.setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.twotone_notifications_active_24)
                            .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                            .setContent(contentView);

                    Unique_Integer_Number = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
                    if (notificationManager != null) {
                        notificationManager.notify(/*notification id*/Unique_Integer_Number, notificationBuilder.build());
                    }
                    Toast.makeText(getApplicationContext(), "The note has been added to the notification area", Toast.LENGTH_SHORT).show();

                }
            }
        });

        /*This will go back to the main activity and auto save the changes made*/
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (titleEditText.getText().toString().trim().length() == 0 && descriptionEditText.getText().toString().trim().length() == 0) {
                    finish();
                } else {
                    updateNotesData(starredCheck);
                    isDelete =1;
                }
            }
        });

        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String currentText = s.toString();
                int currentLength = currentText.length();
                charCountTextView.setText(String.valueOf(currentLength));

                String[] wc = s.toString().split("\\s+");
                wordCountTextView.setText(String.valueOf(wc.length));
            }
        });

        notesInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesInfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                notesInfoDialog.show();
            }
        });

        descriptionEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(listCheckEnabled==1) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on key press
                        descriptionEditText.append("\n" + "● ");
                        return true;
                    }
                }
                return false;
            }
        });

        listButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if(listCheckEnabled ==0){
                    listButton.setColorFilter(ContextCompat.getColor(AddNotesActivity.this, R.color.color5));
                    listCheckEnabled=1;
                    descriptionEditText.append("\n" + "● ");
                }
                else{
                    listButton.setColorFilter(ContextCompat.getColor(AddNotesActivity.this, R.color.dividerColor));
                    listCheckEnabled=0;
                }
            }
        });

        ShareCompat.IntentReader intentReader =
                ShareCompat.IntentReader.from(this);
        if (intentReader.isShareIntent()) {
            String text = intentReader.getHtmlText();
            descriptionEditText.setText(text);
        }

        SharedPreferences prefsAccent = getSharedPreferences(getResources().getString(R.string.MY_PREFS_ACCENT), MODE_PRIVATE);
        String accent = prefsAccent.getString("accent", "default");

        switch (accent) {
            case "color1":
                doneButton.setColorFilter(ContextCompat.getColor(AddNotesActivity.this, R.color.accent1));
                break;

            case "color3":
                doneButton.setColorFilter(ContextCompat.getColor(AddNotesActivity.this, R.color.accent3));
                break;

            case "color4":
                doneButton.setColorFilter(ContextCompat.getColor(AddNotesActivity.this, R.color.accent4));
                break;
            case "color5":
                doneButton.setColorFilter(ContextCompat.getColor(AddNotesActivity.this, R.color.accent5));
                break;
            case "color6":
                doneButton.setColorFilter(ContextCompat.getColor(AddNotesActivity.this, R.color.accent6));
                break;
            case "color7":
                doneButton.setColorFilter(ContextCompat.getColor(AddNotesActivity.this, R.color.accent7));
                break;
            case "color8":
                doneButton.setColorFilter(ContextCompat.getColor(AddNotesActivity.this, R.color.accent8));
                break;
            case "color9":
                doneButton.setColorFilter(ContextCompat.getColor(AddNotesActivity.this, R.color.accent9));
                break;
            case "color10":
                doneButton.setColorFilter(ContextCompat.getColor(AddNotesActivity.this, R.color.accent10));
                break;

            default:
                doneButton.setColorFilter(ContextCompat.getColor(AddNotesActivity.this, R.color.accent2));
                break;
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (TextUtils.isEmpty(titleEditText.getText().toString().trim()) && TextUtils.isEmpty(descriptionEditText.getText().toString().trim())) {
            finish();
        } else {
            updateNotesData(starredCheck);
            isDelete=1;
        }
    }

    public void updateNotesData(int starredVal) {
        if (note.getNoteID() <= 0) {
            currentNoteUniqueId = String.valueOf(Calendar.getInstance().getTimeInMillis());
            note.setNoteUniqueId(currentNoteUniqueId);
            note.setNoteTitle(titleEditText.getText().toString());
            note.setNoteContent(descriptionEditText.getText().toString());
            note.setCreationDate(formattedDate + " " + formattedTime);
            note.setLastEdited(String.valueOf(Calendar.getInstance().getTimeInMillis()));

            if (starredVal == 1) {
                note.setIsStarred("starred");
            } else {
                note.setIsStarred("notstarred");
            }

            // Insert the new row, returning the
            // primary key value of the new row
            currentId = DbHelper.getInstance(getApplication()).upsertNote(note);
            note.setNoteID(currentId);

            Toast.makeText(getApplicationContext(), "The note has been added", Toast.LENGTH_SHORT).show();
            doneButton.isHapticFeedbackEnabled();
            finish();
        } else {
            note.setNoteTitle(titleEditText.getText().toString());
            note.setNoteContent(descriptionEditText.getText().toString());
            note.setLastEdited(String.valueOf(Calendar.getInstance().getTimeInMillis()));

            if (starredVal == 1) {
                note.setIsStarred("starred");
            } else {
                note.setIsStarred("notstarred");
            }

            Toast.makeText(getApplicationContext(), "The note has been udated", Toast.LENGTH_SHORT).show();
            DbHelper.getInstance(getApplication()).upsertNote(note);
            finish();
        }

        getSharedPreferences(getString(R.string.last_edited_base_shared_pref), MODE_PRIVATE).edit().putLong(currentNoteUniqueId, Calendar.getInstance().getTimeInMillis()).apply();
    }


    public void getValuesFromDB() {
        titleEditText.setText(note.getNoteTitle(), TextView.BufferType.EDITABLE);
        descriptionEditText.setText(note.getNoteContent(), TextView.BufferType.EDITABLE);
        creationDateTextView.setText(note.getCreationDate());
        categoryLabelText.setText(note.getCategoryName());
        categoryIdentifier.setText(note.getCategoryId());
        categoryLabelColor.setText(note.getCategoryColor());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(note.getLastEdited()));

        String mMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);


        lastEditedTextView.setText("Edited " + mDay + " " + mMonth + "\n" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));

        /*This is to get the character count from a string*/
        charCountTextView.setText(String.valueOf(descriptionEditText.length()));

        String[] wc = descriptionEditText.getText().toString().split("\\s+");
        wordCountTextView.setText(String.valueOf(wc.length));

        try {
            categoryLabelText.setTextColor(Color.parseColor(note.getCategoryColor().trim()));
            categoryButton.setColorFilter(Color.parseColor(note.getCategoryColor().trim()));
        } catch (Exception e){
            categoryLabelText.setTextColor(ContextCompat.getColor(this, R.color.color3));
            categoryButton.setColorFilter(ContextCompat.getColor(this, R.color.color3));
        }

        if (note.getIsStarred().equals("starred")) {
            starButton.setImageResource(R.drawable.twotone_star_black_24);
            starButton.setColorFilter(ContextCompat.getColor(AddNotesActivity.this, R.color.color4));
            starredCheck = 1;
        } else {
            starButton.setImageResource(R.drawable.twotone_star_black_24);
            starButton.setColorFilter(ContextCompat.getColor(AddNotesActivity.this, R.color.dividerColor));
            starredCheck = 0;
        }
    }

    public void dismissDialog() {
        categoryDialog.dismiss();
    }

    public void changeLabelIconColor(String value) {
        categoryLabelText.setTextColor(Color.parseColor(value));
        categoryButton.setColorFilter(Color.parseColor(value));
    }

    public void initializeView() {
        cancelButton = findViewById(R.id.cancelNoteCreation);
        doneButton = findViewById(R.id.addNoteDone);
        titleEditText = findViewById(R.id.note_title);
        descriptionEditText = findViewById(R.id.note_description);
        deleteButton = findViewById(R.id.delete_note_button);
        starButton = findViewById(R.id.star_button);
        categoryButton = findViewById(R.id.category_button);
        rootView = findViewById(R.id.rootView);
        shareButton = findViewById(R.id.share_note_button);
        addNotification = findViewById(R.id.add_to_notification);
        listButton = findViewById(R.id.list_action);
        notesInfo = findViewById(R.id.notesInfo);
        lastEditedTextView = findViewById(R.id.lastEditedView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (titleEditText.getText().toString().trim().length() == 0 && descriptionEditText.getText().toString().trim().length() == 0 ) {
            finish();
        }
        else if (isDelete == 1){
            finish();
        }
        else {
            updateNotesData(starredCheck);
        }
    }


    public void categoryButtonInvoker(){
        categoryDialog = new Dialog(AddNotesActivity.this);
        // Include dialog.xml file
        categoryDialog.setContentView(R.layout.custom_dialog_categories);
        categoryDialog.setTitle(null);

        categoryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        categoryDialog.show();

        RecyclerView recyclerView = categoryDialog.findViewById(R.id.categories_dialog_list);

        CategoriesRecyclerAdapter noteRecyclerAdapter;

        ArrayList<Category> categories = DbHelper.getInstance(getApplication()).fetchAllCategoriesFromDb(null);

        LinearLayoutManager layoutManager = new LinearLayoutManager(AddNotesActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(layoutManager);
        }

        noteRecyclerAdapter = new CategoriesRecyclerAdapter(categories, AddNotesActivity.this, new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Category category = ((CategoriesRecyclerAdapter)recyclerView.getAdapter()).getItem(position);
                categoryLabelText.setText(category.getCategoryName());
                categoryIdentifier.setText(String.valueOf(category.getCategoryUniqueId()));
                categoryLabelColor.setText(category.getCategoryColor());
                note.setCategoryName(category.getCategoryName());
                note.setCategoryId(category.getCategoryUniqueId());
                note.setCategoryColor(category.getCategoryColor());

                dismissDialog();
                changeLabelIconColor(category.getCategoryColor());
            }
        });

        if (recyclerView != null) {
            recyclerView.setAdapter(noteRecyclerAdapter);
        }
    }

    private void saveTextAsFile(String FileName, String Content) {
        String fileName = FileName + ".txt";
        File file = new File(Environment.getExternalStorageDirectory() + "/Scrittor_Export/");

        if (!file.exists()) {
            file.mkdir();
        }

        try {
            FileOutputStream fos = new FileOutputStream(file +File.separator+ fileName);
            fos.write(Content.getBytes());
            fos.close();
            Toast.makeText(AddNotesActivity.this, "Text file has been exported to Scrittor_Export folder", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(AddNotesActivity.this, "File not Found", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e){
            e.printStackTrace();
            Toast.makeText(AddNotesActivity.this, "Error Saving!", Toast.LENGTH_SHORT).show();
        }

    }
}

