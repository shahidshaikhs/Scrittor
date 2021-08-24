package com.shahid.nid.Activties;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shahid.nid.R;



public class PasswordReset extends AppCompatActivity {

    private EditText password_edit_text;
    private SharedPreferences.Editor editorPassword;

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

        setContentView(R.layout.activity_password_reset);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        password_edit_text = findViewById(R.id.app_password_edit_text);
        ImageView save_password_button = findViewById(R.id.save_password_button);
        ImageView backButton = findViewById(R.id.back_button);

        editorPassword = getSharedPreferences(getResources().getString(R.string.MY_PREFS_PASSWORD), MODE_PRIVATE).edit();



        backButton.setOnClickListener(view -> {
            editorPassword.putString("password_check", "disable");
            editorPassword.putString("password_check", null);
            editorPassword.apply();
            finish();
        });

        save_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password_edit_text.getText().toString().equals("") || password_edit_text.length() < 4) {

                    // Create custom dialog object
                    Dialog deleteDialog = new Dialog(PasswordReset.this);
                    // Include dialog.xml file
                    deleteDialog.setContentView(R.layout.dialog_delete_confirmation);
                    deleteDialog.setTitle(null);

                    TextView agree, disagree, title, content;
                    agree = deleteDialog.findViewById(R.id.agree);
                    disagree = deleteDialog.findViewById(R.id.disagree);
                    title = deleteDialog.findViewById(R.id.title);
                    content = deleteDialog.findViewById(R.id.textContent);

                    title.setText("Oh no! Wait");
                    content.setText("You have not entered the 4 digit PIN yet. Do you still wish to go back?");

                    agree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(PasswordReset.this, SettingsActivity.class));
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


                } else {
                    editorPassword.putString("password", password_edit_text.getText().toString());
                    editorPassword.apply();
                    Toast.makeText(getApplicationContext(), "The changes have been saved", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PasswordReset.this, SettingsActivity.class));
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        editorPassword.putString("password_check", "disable");
        editorPassword.putString("password_check", null);
        editorPassword.apply();
        super.onBackPressed();
    }
}
