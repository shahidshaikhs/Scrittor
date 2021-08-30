package com.shahid.nid.Activties;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.shahid.nid.NID;
import com.shahid.nid.R;
import com.shahid.nid.executors.AppExecutors;

/**
 * Created by Junaid Gandhi on 08/28/2021.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().build());
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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        ((NID)getApplication()).incrementActivities();
    }


    public void showSnackBar(String msg){
        AppExecutors.getInstance().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        ((NID)getApplication()).decrementActivities();
        super.onDestroy();
    }
}
