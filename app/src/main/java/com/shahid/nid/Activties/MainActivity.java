package com.shahid.nid.Activties;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shahid.nid.AccentManager;
import com.shahid.nid.Adapters.SimpleFragmentPagerAdapter;
import com.shahid.nid.BottomSheets.BottomSheetDrawer;
import com.shahid.nid.Categories.CategoriesDbHelper;
import com.shahid.nid.Categories.CategoriesNotesContract;
import com.shahid.nid.R;
import com.shahid.nid.Utils.UtilityVariables;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BottomSheetDrawer.BottomSheetListener {

    private FloatingActionButton addNotesButton;
    private MaterialButton categoryButton;
    private ViewPager viewPager;
    private SearchView searchIcon;
    private TextView HEADING_TEXT;
    private BottomSheetDrawer bottomSheetDrawer;
    private SharedPreferences editorAccent;
    private int tabIconColor;
    private int tabIconColor1;
    private ImageView cloudIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        tabIconColor = ContextCompat.getColor(MainActivity.this, R.color.textColorSecondary);
        tabIconColor1 = ContextCompat.getColor(MainActivity.this, R.color.colorAccent);

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

        editorAccent = getSharedPreferences(getResources().getString(R.string.MY_PREFS_ACCENT), MODE_PRIVATE);
        String accent = editorAccent.getString("accent", "not_defined");

        setContentView(R.layout.activity_main);

        categoryButton = findViewById(R.id.new_category_button);
        ImageView HAMBURGER_ICON = findViewById(R.id.hamburger_icon);
        cloudIcon = findViewById(R.id.cloudStorageIcon);

        searchIcon = findViewById(R.id.mSearch);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.MY_PREFS_USER_NAME), MODE_PRIVATE);

        /*Accessing Helper class*/
        CategoriesDbHelper mDbHelper = new CategoriesDbHelper(MainActivity.this);

        /*This code needs to run only for the first time*/
        if (!prefs.getBoolean("firstTime", false)) {
            // <---- run your one time code here

            SQLiteDatabase dbWrite = mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(CategoriesNotesContract.categoriesContract.COLUMN_NAME_CATEGORY, "Not Specified");
            values.put(CategoriesNotesContract.categoriesContract.COLUMN_DESCRIPTION_CATEGORY, "All notes that are not assigned to a specific category are displayed here");
            values.put(CategoriesNotesContract.categoriesContract.COLUMN_COLOR, "#6ECFFF");
            values.put(CategoriesNotesContract.categoriesContract.COLUMN_CATEGORY_UNIQUE_ID, String.valueOf(Calendar.getInstance().getTimeInMillis()));
            dbWrite.insert(CategoriesNotesContract.categoriesContract.TABLE_NAME, null, values);

            getUserName("");

            SharedPreferences.Editor editorTheme = getSharedPreferences(getResources().getString(R.string.MY_PREFS_THEME), MODE_PRIVATE).edit();
            editorTheme.putString("theme", "dark");
            editorTheme.apply();

            // mark first time has runned.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.apply();

            dbWrite.close();
        }

        HEADING_TEXT = findViewById(R.id.title_text);

        final int[] ICONS = new int[]{
                R.drawable.twotone_category_24,
                R.drawable.twotone_home_24,
                R.drawable.twotone_star_black_24
        };

        viewPager = findViewById(R.id.viewpager);

        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1, false);

        final TabLayout tabLayout;
        tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setOffscreenPageLimit(3);
        addNotesButton = findViewById(R.id.add_note);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor1, PorterDuff.Mode.SRC_IN);
                    tabLayout.getTabAt(1).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    tabLayout.getTabAt(2).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    HEADING_TEXT.setText(R.string.tab_category);
                    categoryButton.setVisibility(View.VISIBLE);
                    HEADING_TEXT.setVisibility(View.VISIBLE);
                    searchIcon.setVisibility(View.GONE);
                    searchIcon.setIconified(true);
                    addNotesButton.hide();

                } else if (position == 1) {
                    tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    tabLayout.getTabAt(1).getIcon().setColorFilter(tabIconColor1, PorterDuff.Mode.SRC_IN);
                    tabLayout.getTabAt(2).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    categoryButton.setVisibility(View.GONE);
                    HEADING_TEXT.setText(R.string.tab_notes);
                    searchIcon.setVisibility(View.VISIBLE);
                    addNotesButton.show();
                } else if(position ==2){
                    tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    tabLayout.getTabAt(1).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    tabLayout.getTabAt(2).getIcon().setColorFilter(tabIconColor1, PorterDuff.Mode.SRC_IN);
                    categoryButton.setVisibility(View.GONE);
                    HEADING_TEXT.setVisibility(View.VISIBLE);
                    HEADING_TEXT.setText(R.string.tab_starred);
                    searchIcon.setVisibility(View.GONE);
                    searchIcon.setIconified(true);
                    addNotesButton.show();
                }

                else {
                    addNotesButton.hide();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        addNotesButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AddNotesActivity.class)));

        HAMBURGER_ICON.setOnClickListener(view -> {
            bottomSheetDrawer = new BottomSheetDrawer();
            bottomSheetDrawer.show(getSupportFragmentManager(), "bottomSheetDrawer");
        });


        searchIcon.setOnSearchClickListener(v -> {
            HEADING_TEXT.setVisibility(View.GONE);
        });

        searchIcon.setOnCloseListener(() -> {
            HEADING_TEXT.setVisibility(View.VISIBLE);
            return false;
        });

        if (accent.equals("color1")) {
            tabIconColor1 = ContextCompat.getColor(MainActivity.this, R.color.accent1);
            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this, R.color.accent1));
            addNotesButton.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.accent1));
            categoryButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.accent1), PorterDuff.Mode.SRC_OVER);

        } else if (accent.equals("color2")) {
            tabIconColor1 = ContextCompat.getColor(MainActivity.this, R.color.accent2);
            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this, R.color.accent2));
            addNotesButton.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.accent2));
            categoryButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.accent2), PorterDuff.Mode.SRC_OVER);
        } else if (accent.equals("color3")) {
            tabIconColor1 = ContextCompat.getColor(MainActivity.this, R.color.accent3);
            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this, R.color.accent3));
            addNotesButton.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.accent3));
            categoryButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.accent3), PorterDuff.Mode.SRC_OVER);
        } else if (accent.equals("color4")) {
            tabIconColor1 = ContextCompat.getColor(MainActivity.this, R.color.accent4);
            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this, R.color.accent4));
            addNotesButton.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.accent4));
            categoryButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.accent4), PorterDuff.Mode.SRC_OVER);
        } else if (accent.equals("color5")) {
            tabIconColor1 = ContextCompat.getColor(MainActivity.this, R.color.accent5);
            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this, R.color.accent5));
            addNotesButton.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.accent5));
            categoryButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.accent5), PorterDuff.Mode.SRC_OVER);
        } else if (accent.equals("color6")) {
            tabIconColor1 = ContextCompat.getColor(MainActivity.this, R.color.accent6);
            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this, R.color.accent6));
            addNotesButton.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.accent6));
            categoryButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.accent6), PorterDuff.Mode.SRC_OVER);
        } else if (accent.equals("color7")) {
            tabIconColor1 = ContextCompat.getColor(MainActivity.this, R.color.accent7);
            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this, R.color.accent7));
            addNotesButton.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.accent7));
            categoryButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.accent7), PorterDuff.Mode.SRC_OVER);
        } else if (accent.equals("color8")) {
            tabIconColor1 = ContextCompat.getColor(MainActivity.this, R.color.accent8);
            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this, R.color.accent8));
            addNotesButton.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.accent2));
            categoryButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.accent8), PorterDuff.Mode.SRC_OVER);
        } else if (accent.equals("color9")) {
            tabIconColor1 = ContextCompat.getColor(MainActivity.this, R.color.accent9);
            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this, R.color.accent9));
            addNotesButton.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.accent9));
            categoryButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.accent9), PorterDuff.Mode.SRC_OVER);
        } else if (accent.equals("color10")) {
            tabIconColor1 = ContextCompat.getColor(MainActivity.this, R.color.accent10);
            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this, R.color.accent10));
            addNotesButton.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.accent10));
            categoryButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.accent10), PorterDuff.Mode.SRC_OVER);
        } else {
            tabIconColor1 = ContextCompat.getColor(MainActivity.this, R.color.colorAccent);
            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
            addNotesButton.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorAccent));
            categoryButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_OVER);
        }

        tabLayout.getTabAt(0).setIcon(ICONS[0]);
        tabLayout.getTabAt(1).setIcon(ICONS[1]);
        tabLayout.getTabAt(2).setIcon(ICONS[2]);

        tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(tabIconColor1, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

        cloudIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, BackupSettingsActivity.class));
            }
        });

    }

    public void openLinks(String link) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        searchIcon.setIconified(true);
    }

    public void passwordCheckMethod() {
        SharedPreferences prefsPassword = getSharedPreferences(getResources().getString(R.string.MY_PREFS_PASSWORD), MODE_PRIVATE);
        String password_check = prefsPassword.getString("password_check", null);

        /*Fingerprint Preferences*/
        SharedPreferences prefsFingerPrint = getSharedPreferences(getResources().getString(R.string.MY_PREFS_FINGERPRINT), MODE_PRIVATE);
        String fingerprint_check = prefsFingerPrint.getString("fingerprint_check", null);

        if (password_check != null) {
            if (!password_check.equals("enable")) {

                Intent intent = getIntent();
                if (intent.hasExtra("check")) {

                } else {

                    if (fingerprint_check != null) {
                        if (fingerprint_check.equals("enable")) {
                            startActivity(new Intent(MainActivity.this, SignInActivity.class));
                            finish();
                        }
                    }
                }

            } else {
                Intent intent = getIntent();
                if (intent.hasExtra("check")) {
                    Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_LONG).show();
                } else {
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    finish();
                }
            }
        }
    }

    public void getUserName(String present) {
        final SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.MY_PREFS_USER_NAME), MODE_PRIVATE).edit();

        Dialog userNameDialog = new Dialog(MainActivity.this,  R.style.Theme_Dialog);
        // Include dialog.xml file
        userNameDialog.setContentView(R.layout.dialog_username);
        userNameDialog.setTitle(null);

        TextView agree, disagree;
        EditText userNameText;
        agree = userNameDialog.findViewById(R.id.agree);
        disagree = userNameDialog.findViewById(R.id.disagree);
        userNameText = userNameDialog.findViewById(R.id.userNameText);

        userNameText.setText(present);

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("user_name", String.valueOf(userNameText.getText().toString()).replaceAll("\\s+$", ""));
                editor.apply();
                userNameDialog.dismiss();
            }
        });


        disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userNameDialog.dismiss();
            }
        });

        userNameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        userNameDialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        UtilityVariables utilityVariables = new UtilityVariables();
        if (utilityVariables.getSomeVariable() == 1) {
            /*This method checks when to display the SignInActivity*/
            passwordCheckMethod();
        }
    }

    @Override
    protected void onDestroy() {
        viewPager.addOnPageChangeListener(null);
        super.onDestroy();
    }

    @Override
    public void onButtonClickDrawer(String buttonClick) {
        switch (buttonClick) {
            case "settings":
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case "rate":
                Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.com.shahid.nid.GoogleDrive.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                }
                break;
            case "about":
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case "donate":
                startActivity(new Intent(MainActivity.this, BillingActivity.class));
                break;
            case "translate":
                openLinks("https://scrittor.oneskyapp.com/collaboration/project?id=146657");
                break;
            case "accent":
                Dialog colorDialog = new Dialog(MainActivity.this,  R.style.Theme_Dialog);
                colorDialog.setContentView(R.layout.activity_custom_dialog_accent_color);
                colorDialog.setTitle(null);

                colorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                colorDialog.show();

                ImageView color1 = colorDialog.findViewById(R.id.color1);
                ImageView color2 = colorDialog.findViewById(R.id.color2);
                ImageView color3 = colorDialog.findViewById(R.id.color3);
                ImageView color4 = colorDialog.findViewById(R.id.color4);
                ImageView color5 = colorDialog.findViewById(R.id.color5);
                ImageView color6 = colorDialog.findViewById(R.id.color6);
                ImageView color7 = colorDialog.findViewById(R.id.color7);
                ImageView color8 = colorDialog.findViewById(R.id.color8);
                ImageView color9 = colorDialog.findViewById(R.id.color9);
                ImageView color10 = colorDialog.findViewById(R.id.color10);

                AccentManager accentManager = new AccentManager();

                color1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        accentManager.checkColor(MainActivity.this, "color1");
                        recreate();
                    }
                });

                color2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        accentManager.checkColor(MainActivity.this, "color2");
                        recreate();
                    }
                });

                color3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        accentManager.checkColor(MainActivity.this, "color3");
                        recreate();
                    }
                });

                color4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        accentManager.checkColor(MainActivity.this, "color4");
                        recreate();
                    }
                });

                color5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        accentManager.checkColor(MainActivity.this, "color5");
                        recreate();
                    }
                });

                color6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        accentManager.checkColor(MainActivity.this, "color6");
                        recreate();
                    }
                });

                color7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        accentManager.checkColor(MainActivity.this, "color7");
                        recreate();
                    }
                });

                color8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        accentManager.checkColor(MainActivity.this, "color8");
                        recreate();
                    }
                });

                color9.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        accentManager.checkColor(MainActivity.this, "color9");
                        recreate();
                    }
                });

                color10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        accentManager.checkColor(MainActivity.this, "color10");
                        recreate();
                    }
                });
                break;
        }
    }
}
