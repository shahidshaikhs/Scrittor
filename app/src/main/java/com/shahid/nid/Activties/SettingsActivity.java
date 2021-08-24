package com.shahid.nid.Activties;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.shahid.nid.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Objects;



public class SettingsActivity extends AppCompatActivity {


    private CheckBox password_Check, fingerprint_Check;
    private SharedPreferences.Editor editorPassword;
    private SharedPreferences.Editor editorFingerPrint;
    private SharedPreferences.Editor textLimiterPrefsEditor;
    private int check = 0;
    private  SharedPreferences prefsPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefsTheme = getSharedPreferences(getResources().getString(R.string.MY_PREFS_THEME), MODE_PRIVATE);
        String theme = prefsTheme.getString("theme", "not_defined");
        if(theme.equals("dark")){
            getTheme().applyStyle(R.style.OverlayPrimaryColorDark, true);
        }else if(theme.equals("light")){
            getTheme().applyStyle(R.style.OverlayPrimaryColorLight, true);
        }else if(theme.equals("amoled")){
            getTheme().applyStyle(R.style.OverlayPrimaryColorAmoled, true);
        }else{
            getTheme().applyStyle(R.style.OverlayPrimaryColorDark, true);
        }

        setContentView(R.layout.activity_settings);

        ImageView backButton = findViewById(R.id.back_button);
        LinearLayout fingerPrintBox = findViewById(R.id.fingerprint_box);
        LinearLayout textLimiter = findViewById(R.id.text_limiter);
        LinearLayout exportNotes = findViewById(R.id.export_box);
        LinearLayout importNotes = findViewById(R.id.import_box);
        LinearLayout driveBackup = findViewById(R.id.google_drive_backup);

        password_Check = findViewById(R.id.password_check);
        fingerprint_Check = findViewById(R.id.fingerprint_check);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        backButton.setOnClickListener(view -> {
            finish();
            if (check == 1) {
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        editorPassword = getSharedPreferences(getResources().getString(R.string.MY_PREFS_PASSWORD), MODE_PRIVATE).edit();
        prefsPassword = getSharedPreferences(getResources().getString(R.string.MY_PREFS_PASSWORD), MODE_PRIVATE);


        /*Fingerprint Preferences*/
        editorFingerPrint = getSharedPreferences(getResources().getString(R.string.MY_PREFS_FINGERPRINT), MODE_PRIVATE).edit();
        SharedPreferences prefsFingerPrint = getSharedPreferences(getResources().getString(R.string.MY_PREFS_FINGERPRINT), MODE_PRIVATE);


        if (Objects.equals(prefsPassword.getString("password", null), "")) {
            password_Check.setChecked(false);

        } else {
            if (Objects.equals(prefsPassword.getString("password_check", null), "enable")) {
                password_Check.setChecked(true);

            } else {
                password_Check.setChecked(false);

            }
        }

        password_Check.setOnClickListener(view -> {
            if (password_Check.isChecked()) {
                editorPassword.putString("password_check", "enable");
                editorPassword.apply();

                fingerprint_Check.setChecked(false);
                editorFingerPrint.putString("fingerprint_check", "disable");
                editorFingerPrint.apply();

                startActivity(new Intent(SettingsActivity.this, PasswordReset.class));

            } else {
                editorPassword.putString("password_check", "disable");
                editorPassword.apply();
            }
        });


        if (Objects.equals(prefsFingerPrint.getString("fingerprint_check", null), "enable")) {
            fingerprint_Check.setChecked(true);
        } else {
            fingerprint_Check.setChecked(false);
        }

        fingerprint_Check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (fingerprint_Check.isChecked()) {
                    Toast.makeText(SettingsActivity.this, "Fingerprint Authentication Enabled", Toast.LENGTH_SHORT).show();
                    editorFingerPrint.putString("fingerprint_check", "enable");
                    editorFingerPrint.apply();

                    password_Check.setChecked(false);
                    editorPassword.putString("password_check", "disable");
                    editorPassword.apply();

                } else {
                    Toast.makeText(SettingsActivity.this, "Fingerprint Authentication Disabled", Toast.LENGTH_SHORT).show();
                    editorFingerPrint.putString("fingerprint_check", "disable");
                    editorFingerPrint.apply();
                }
            }
        });

        /*Fingerprint Preferences*/
        textLimiterPrefsEditor = getSharedPreferences(getResources().getString(R.string.MY_PREFS_TEXT_LIMITER), MODE_PRIVATE).edit();
        SharedPreferences textPref;
        textPref = getSharedPreferences(getResources().getString(R.string.MY_PREFS_TEXT_LIMITER), MODE_PRIVATE);

        String textLimit =  textPref.getString("integer", null);

        textLimiter.setOnClickListener(v -> {

            Dialog textLimitDialog = new Dialog(SettingsActivity.this,  R.style.Theme_Dialog);
            // Include dialog.xml file
            textLimitDialog.setContentView(R.layout.dialog_text_limiter);
            textLimitDialog.setTitle(null);

            textLimitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


            TextView one, two, three, four, five;
            one = textLimitDialog.findViewById(R.id.one);
            two = textLimitDialog.findViewById(R.id.two);
            three = textLimitDialog.findViewById(R.id.three);
            four = textLimitDialog.findViewById(R.id.four);
            five = textLimitDialog.findViewById(R.id.five);

            if(textLimit !=null) {
                switch (textLimit) {
                    case "one":
                        one.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.colorAccent));
                        break;
                    case "two":
                        two.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.colorAccent));
                        break;
                    case "three":
                        three.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.colorAccent));
                        break;
                    case "four":
                        four.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.colorAccent));
                        break;
                    case "five":
                        five.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.colorAccent));
                        break;
                    default:
                        four.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.colorAccent));
                        break;
                }
            }

            one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textLimiterPrefsEditor.putString("integer", "one");
                    textLimiterPrefsEditor.apply();
                    one.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.colorAccent));
                    textLimitDialog.dismiss();
                }
            });

            two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textLimiterPrefsEditor.putString("integer", "two");
                    textLimiterPrefsEditor.apply();
                    two.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.colorAccent));
                    textLimitDialog.dismiss();
                }
            });

            three.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textLimiterPrefsEditor.putString("integer", "three");
                    textLimiterPrefsEditor.apply();
                    three.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.colorAccent));
                    textLimitDialog.dismiss();
                }
            });

            four.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textLimiterPrefsEditor.putString("integer", "four");
                    textLimiterPrefsEditor.apply();
                    four.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.colorAccent));
                    textLimitDialog.dismiss();
                }
            });

            five.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textLimiterPrefsEditor.putString("integer", "five");
                    textLimiterPrefsEditor.apply();
                    five.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.colorAccent));
                    textLimitDialog.dismiss();
                }
            });

            textLimitDialog.show();

        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
            if (fingerprintManager != null) {
                if (!fingerprintManager.isHardwareDetected()) {
                    fingerPrintBox.setVisibility(View.GONE);
                } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                    fingerPrintBox.setVisibility(View.GONE);
                    // User hasn't enrolled any fingerprints to authenticate with
                }  // Everything is ready for fingerprint authentication
            }
        } else {
            fingerPrintBox.setVisibility(View.GONE);
        }

        exportNotes.setOnClickListener(v -> Dexter.withActivity(SettingsActivity.this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        // Create custom dialog object
                        Dialog deleteDialog = new Dialog(SettingsActivity.this,  R.style.Theme_Dialog);
                        // Include dialog.xml file
                        deleteDialog.setContentView(R.layout.dialog_delete_confirmation);
                        deleteDialog.setTitle(null);

                        TextView agree, disagree, title, content;
                        agree = deleteDialog.findViewById(R.id.agree);
                        disagree = deleteDialog.findViewById(R.id.disagree);
                        title = deleteDialog.findViewById(R.id.title);
                        content = deleteDialog.findViewById(R.id.textContent);

                        title.setText("Export Notes?");
                        content.setText("Existing backup notes will be replaced by latest exported notes.");

                        agree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //creating a new folder for the database to be backuped to
                                File direct = new File(Environment.getExternalStorageDirectory() + "/Scrittor_Backup");

                                if (!direct.exists()) {
                                    if (direct.mkdir()) {
                                        exportDB();
                                    } else {
                                        direct.mkdir();
                                        exportDB();
                                    }
                                }
                                exportDB();
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
                .check());

        importNotes.setOnClickListener(v -> Dexter.withActivity(SettingsActivity.this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        // Create custom dialog object
                        Dialog deleteDialog = new Dialog(SettingsActivity.this,  R.style.Theme_Dialog);
                        // Include dialog.xml file
                        deleteDialog.setContentView(R.layout.dialog_delete_confirmation);
                        deleteDialog.setTitle(null);

                        TextView agree, disagree, title, content;
                        agree = deleteDialog.findViewById(R.id.agree);
                        disagree = deleteDialog.findViewById(R.id.disagree);
                        title = deleteDialog.findViewById(R.id.title);
                        content = deleteDialog.findViewById(R.id.textContent);

                        title.setText("Import Notes?");
                        content.setText("Existing backup notes will be replaced by latest imported notes.");

                        agree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                importDB();
                                check = 1;
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

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getBaseContext(), "This permission is required so that the app can import database",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check());

        driveBackup.setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, BackupSettingsActivity.class)));

    }

    //exporting database
    private void exportDB() {
        // TODO Auto-generated method stub

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/user/0/com.shahid.nid/databases/FeedReader.db";
                String currentCategoryDBPath = "/user/0/com.shahid.nid/databases/CategoriesHelper.db";

                String backupDBPath = "/Scrittor_Backup/FeedReader.db";
                String backupCategoryDBPath = "/Scrittor_Backup/CategoriesHelper.db";

                File currentDB = new File(data, currentDBPath);
                File currentCategoryDB = new File(data, currentCategoryDBPath);


                File backupDB = new File(sd, backupDBPath);
                File backupCategoryDB = new File(sd, backupCategoryDBPath);


                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel srcCat = new FileInputStream(currentCategoryDB).getChannel();


                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                FileChannel dstCat = new FileOutputStream(backupCategoryDB).getChannel();


                dst.transferFrom(src, 0, src.size());
                dstCat.transferFrom(srcCat, 0, srcCat.size());


                src.close();
                srcCat.close();

                dst.close();
                dstCat.close();

                Toast.makeText(getBaseContext(), "Notes database has been exported to : Storage/Scrittor_Backup",
                        Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {

            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                    .show();

        }
    }


    //importing database
    private void importDB() {
        // TODO Auto-generated method stub

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/user/0/com.shahid.nid/databases/FeedReader.db";
                String currentCategoryDBPath = "/user/0/com.shahid.nid/databases/CategoriesHelper.db";


                String backupDBPath = "/Scrittor_Backup/FeedReader.db";
                String backupCategoryDBPath = "/Scrittor_Backup/CategoriesHelper.db";

                File backupDB = new File(data, currentDBPath);
                File backupCategoryDB = new File(data, currentCategoryDBPath);


                File currentDB = new File(sd, backupDBPath);
                File currentCategoryDB = new File(sd, backupCategoryDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel srcCat = new FileInputStream(currentCategoryDB).getChannel();

                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                FileChannel dstCat = new FileOutputStream(backupCategoryDB).getChannel();


                dst.transferFrom(src, 0, src.size());
                dstCat.transferFrom(srcCat, 0, srcCat.size());

                src.close();
                dst.close();
                srcCat.close();
                dstCat.close();

                Toast.makeText(getBaseContext(), "Notes have been imported from the previously backed up database.",
                        Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Could not locate the required database to import. Please export notes first.", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

        if (check == 1) {
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Objects.equals(prefsPassword.getString("password", null), "")) {
            password_Check.setChecked(false);

        } else {
            if (Objects.equals(prefsPassword.getString("password_check", null), "enable")) {
                password_Check.setChecked(true);

            } else {
                password_Check.setChecked(false);

            }
        }
    }
}
