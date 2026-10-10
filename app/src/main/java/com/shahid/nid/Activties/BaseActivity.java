package com.shahid.nid.Activties;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.shahid.nid.AccentManager;
import com.shahid.nid.NID;
import com.shahid.nid.R;
import com.shahid.nid.executors.AppExecutors;

/**
 * Created by Junaid Gandhi on 08/28/2021.
 */
public class BaseActivity extends AppCompatActivity {
    private boolean useDarkSystemBarIcons;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferences prefsTheme = getSharedPreferences(getResources().getString(R.string.MY_PREFS_THEME), MODE_PRIVATE);
        String theme = prefsTheme.getString("theme", "not_defined");
        if (theme.equals("dark")) {
            getTheme().applyStyle(R.style.OverlayPrimaryColorDark, true);
            useDarkSystemBarIcons = false;
        } else if (theme.equals("light")) {
            getTheme().applyStyle(R.style.OverlayPrimaryColorLight, true);
            useDarkSystemBarIcons = true;
        } else if (theme.equals("amoled")) {
            getTheme().applyStyle(R.style.OverlayPrimaryColorAmoled, true);
            useDarkSystemBarIcons = false;
        } else {
            getTheme().applyStyle(R.style.OverlayPrimaryColorDark, true);
            useDarkSystemBarIcons = false;
        }
        AccentManager.applySavedAccent(this);
        super.onCreate(savedInstanceState);
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { handleBackNavigation(); }
        });
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().build());
        applySystemBarAppearance();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

    }


    protected void handleBackNavigation() { finish(); }

    @Override public void setContentView(int layout) {
        super.setContentView(layout);
        applySystemInsets();
    }
    @Override public void setContentView(android.view.View view) {
        super.setContentView(view);
        applySystemInsets();
    }
    private void applySystemInsets() {
        android.view.View content = findViewById(android.R.id.content);
        android.util.TypedValue primaryColor = new android.util.TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.colorPrimary, primaryColor, true)) {
            content.setBackgroundColor(primaryColor.data);
        }
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(content, (view, insets) -> {
            androidx.core.graphics.Insets bars = insets.getInsets(
                    androidx.core.view.WindowInsetsCompat.Type.systemBars() | androidx.core.view.WindowInsetsCompat.Type.ime());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return androidx.core.view.WindowInsetsCompat.CONSUMED;
        });
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        applySystemBarAppearance();
        androidx.core.view.ViewCompat.requestApplyInsets(content);
    }

    private void applySystemBarAppearance() {
        androidx.core.view.WindowInsetsControllerCompat controller =
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(useDarkSystemBarIcons);
        controller.setAppearanceLightNavigationBars(useDarkSystemBarIcons);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
    }

    private boolean unlocking;
    public boolean isContentAccessible() {
        NID app = (NID) getApplication();
        return !app.isLockEnabled() || app.isAuthenticated();
    }

    @Override protected void onStart() {
        super.onStart();
        if (isFinishing()) return;
        if (((NID) getApplication()).isLockEnabled()) {
            getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE);
        }
        if (!(this instanceof SignInActivity) && !isContentAccessible()) {
            getWindow().getDecorView().setVisibility(android.view.View.INVISIBLE);
            if (!unlocking) {
                unlocking = true;
                startActivityForResult(new android.content.Intent(this, SignInActivity.class), 900);
            }
        } else {
            getWindow().getDecorView().setVisibility(android.view.View.VISIBLE);
        }
    }

    @Override protected void onActivityResult(int request, int result, android.content.Intent data) {
        super.onActivityResult(request, result, data);
        if (request == 900) {
            unlocking = false;
            if (result != RESULT_OK || !isContentAccessible()) finish();
            else getWindow().getDecorView().setVisibility(android.view.View.VISIBLE);
        }
    }

    public void showSnackBar(String msg){
        AppExecutors.getInstance().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                if (isFinishing() || isDestroyed()) return;
                Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
