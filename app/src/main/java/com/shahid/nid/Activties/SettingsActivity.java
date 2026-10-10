package com.shahid.nid.Activties;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shahid.nid.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Objects;



public class SettingsActivity extends BaseActivity {


    private CheckBox password_Check, fingerprint_Check;
    private SharedPreferences.Editor editorPassword;
    private SharedPreferences.Editor editorFingerPrint;
    private SharedPreferences.Editor textLimiterPrefsEditor;
    private int check = 0;
    private  SharedPreferences prefsPassword;
    private boolean updatingAuthenticationControls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        ImageView backButton = findViewById(R.id.back_button);
        LinearLayout fingerPrintBox = findViewById(R.id.fingerprint_box);
        LinearLayout textLimiter = findViewById(R.id.text_limiter);
        LinearLayout exportNotes = findViewById(R.id.export_box);
        LinearLayout importNotes = findViewById(R.id.import_box);
        LinearLayout driveBackup = findViewById(R.id.google_drive_backup);

        password_Check = findViewById(R.id.password_check);
        fingerprint_Check = findViewById(R.id.fingerprint_check);

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

                if (updatingAuthenticationControls) return;

                if (fingerprint_Check.isChecked()) {
                    Toast.makeText(SettingsActivity.this, "Fingerprint Authentication Enabled", Toast.LENGTH_SHORT).show();
                    ((com.shahid.nid.NID) getApplication()).unlock();
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

            String textLimit = textPref.getString("integer", "four");
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

        int biometricStatus = androidx.biometric.BiometricManager.from(this).canAuthenticate(
                androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK);
        fingerPrintBox.setVisibility(biometricStatus == androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
                ? View.VISIBLE : View.GONE);

        exportNotes.setOnClickListener(v -> startActivityForResult(new Intent(Intent.ACTION_CREATE_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE).setType("application/json")
                .putExtra(Intent.EXTRA_TITLE, "Scrittor-backup.json"), 101));
        importNotes.setOnClickListener(v -> startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE).setType("*/*"), 102));

        driveBackup.setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, DriveBackupActivity.class)));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode != 101 && requestCode != 102) || resultCode != RESULT_OK || data == null || data.getData() == null) return;
        android.net.Uri uri = data.getData();
        com.shahid.nid.executors.AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                com.shahid.nid.Utils.DbHelper db = com.shahid.nid.Utils.DbHelper.getInstance(getApplication());
                if (requestCode == 101) {
                    String json = com.shahid.nid.Utils.BackupManager.exportJson(db);
                    try (java.io.OutputStream output = getContentResolver().openOutputStream(uri, "wt")) {
                        if (output == null) throw new java.io.IOException("Cannot open document");
                        output.write(json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    }
                    showSnackBar("Backup exported.");
                } else {
                    try (java.io.InputStream input = getContentResolver().openInputStream(uri)) {
                        if (input == null) throw new java.io.IOException("Cannot open document");
                        java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
                        byte[] buffer = new byte[8192];
                        int count;
                        while ((count = input.read(buffer)) != -1) {
                            if (bytes.size() + count > 64 * 1024 * 1024) throw new java.io.IOException("Backup too large");
                            bytes.write(buffer, 0, count);
                        }
                        byte[] payload = bytes.toByteArray();
                        int restored;
                        if (payload.length >= 16 && new String(payload, 0, 15, java.nio.charset.StandardCharsets.US_ASCII).equals("SQLite format 3")) {
                            java.io.File temporary = java.io.File.createTempFile("legacy-backup-", ".db", getCacheDir());
                            try {
                                try (java.io.OutputStream output = new java.io.FileOutputStream(temporary)) { output.write(payload); }
                                restored = com.shahid.nid.Utils.LegacyBackupReader.restore(temporary, db);
                            } finally { temporary.delete(); }
                        } else {
                            restored = com.shahid.nid.Utils.BackupManager.importJson(db, bytes.toString("UTF-8"));
                        }
                        showSnackBar("Restored " + restored + " notes. Newer local edits were kept.");
                    }
                }
            } catch (Exception e) {
                showSnackBar("Backup operation failed. Check the selected file and try again.");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void handleBackNavigation() {
        super.handleBackNavigation();
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
        updatingAuthenticationControls = true;
        fingerprint_Check.setChecked("enable".equals(getSharedPreferences(getString(R.string.MY_PREFS_FINGERPRINT), MODE_PRIVATE).getString("fingerprint_check", null)));
        if (Objects.equals(prefsPassword.getString("password", null), "")) {
            password_Check.setChecked(false);

        } else {
            if (Objects.equals(prefsPassword.getString("password_check", null), "enable")) {
                password_Check.setChecked(true);

            } else {
                password_Check.setChecked(false);

            }
        }
        updatingAuthenticationControls = false;
    }
}
