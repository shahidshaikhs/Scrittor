package com.shahid.nid.Categories;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.shahid.nid.Activties.MainActivity;
import com.shahid.nid.R;
import com.shahid.nid.Utils.DatabaseMethods;

import java.util.ArrayList;
import java.util.Calendar;



public class ManageCategories extends AppCompatActivity {

    private View dividerView;
    private SQLiteDatabase dbWrite, dbWriteExist;
    private CategoriesDbHelper mDbHelper;
    private EditText categoryTitle, categoryDescription;
    private String colorValue = "#8D6E63";
    private String currentCategoryId;

    ArrayList<String> categoryNamesPresentList = new ArrayList<>();

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

        setContentView(R.layout.activity_manage_categories);

//        LinearLayout rootView = findViewById(R.id.rootLayout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        /*Accessing Helper class*/
        mDbHelper = new CategoriesDbHelper(ManageCategories.this);
        dbWrite = mDbHelper.getWritableDatabase();

        ImageView image1 = findViewById(R.id.color1);
        ImageView image2 = findViewById(R.id.color2);
        ImageView image3 = findViewById(R.id.color3);
        ImageView image4 = findViewById(R.id.color4);
        ImageView image5 = findViewById(R.id.color5);
        ImageView image6 = findViewById(R.id.color6);
        dividerView = findViewById(R.id.categoryColorDivider);
        ImageView addCategory = findViewById(R.id.new_category_button);
        categoryTitle = findViewById(R.id.category_title);
        categoryDescription = findViewById(R.id.cateogry_description);
        ImageView backButton = findViewById(R.id.back_button);

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dividerView.setBackgroundColor(ContextCompat.getColor(ManageCategories.this, R.color.color1));
                colorValue = "#EF5350";
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dividerView.setBackgroundColor(ContextCompat.getColor(ManageCategories.this, R.color.color2));
                colorValue = "#6672B0";
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dividerView.setBackgroundColor(ContextCompat.getColor(ManageCategories.this, R.color.color3));
                colorValue = "#6ECFFF";
            }
        });
        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dividerView.setBackgroundColor(ContextCompat.getColor(ManageCategories.this, R.color.color4));
                colorValue = "#FFCA28";
            }
        });
        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dividerView.setBackgroundColor(ContextCompat.getColor(ManageCategories.this, R.color.color5));
                colorValue = "#9CCC65";
            }
        });
        image6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dividerView.setBackgroundColor(ContextCompat.getColor(ManageCategories.this, R.color.color6));
                colorValue = "#8D6E63";
            }
        });

        categoryDescription.setText("You can add a description here");

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryTitle.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Category title is a mandatory field", Toast.LENGTH_SHORT).show();
                } else {
                    addCategoryMethod();
                }
            }
        });


        /*This code will only run when category needs to be edited*/
        if (getIntent().hasExtra("noteId")) {
            currentCategoryId = getIntent().getStringExtra("noteId");
            dbWriteExist = mDbHelper.getWritableDatabase();
//            String Query = "Select * from " + CategoriesNotesContract.categoriesContract.TABLE_NAME + " where " + CategoriesNotesContract.categoriesContract.COLUMN_CATEGORY_UNIQUE_ID + " = " + currentCategoryId;
//            Cursor cursor = dbWriteExist.rawQuery(Query, null);
            Cursor cursor = dbWriteExist.query(CategoriesNotesContract.categoriesContract.TABLE_NAME,
                    null,
                    CategoriesNotesContract.categoriesContract.COLUMN_CATEGORY_UNIQUE_ID.concat(" = ?"),
                    new String[]{currentCategoryId},
                    null,
                    null,
                    null);


            cursor.moveToNext();
            final String categoryName = cursor.getString(
                    cursor.getColumnIndexOrThrow(CategoriesNotesContract.categoriesContract.COLUMN_NAME_CATEGORY));
            String categoryDescriptionValue = cursor.getString(
                    cursor.getColumnIndexOrThrow(CategoriesNotesContract.categoriesContract.COLUMN_DESCRIPTION_CATEGORY));
            final String categoryColor = cursor.getString(
                    cursor.getColumnIndexOrThrow(CategoriesNotesContract.categoriesContract.COLUMN_COLOR));

            final long categoryId = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(CategoriesNotesContract.categoriesContract.COLUMN_CATEGORY_UNIQUE_ID)));
            cursor.close();

            categoryTitle.setText(categoryName);
            categoryDescription.setText(categoryDescriptionValue);
            ChangeDividerColor(categoryColor);
            colorValue = categoryColor;

            addCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (categoryTitle.getText().toString().trim().length() == 0) {
                        Toast.makeText(getApplicationContext(), "There is nothing to save. Kindly, write something.", Toast.LENGTH_SHORT).show();
                    } else {
                        UpdateCategoryMethod(categoryId);
                    }

                }
            });
        }

        Cursor cursor = dbWrite.query(CategoriesNotesContract.categoriesContract.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        while (cursor.moveToNext()){
            categoryNamesPresentList.add(cursor.getString(cursor.getColumnIndexOrThrow(CategoriesNotesContract.categoriesContract.COLUMN_NAME_CATEGORY)).trim().toLowerCase());
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void addCategoryMethod() {
        if (categoryNamesPresentList.contains(categoryTitle.getText().toString().trim().toLowerCase())){
            Snackbar.make(findViewById(android.R.id.content), R.string.category_exists, Snackbar.LENGTH_INDEFINITE).show();
            return;
        }

        dbWrite = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CategoriesNotesContract.categoriesContract.COLUMN_NAME_CATEGORY, String.valueOf(categoryTitle.getText().toString().trim()));
        values.put(CategoriesNotesContract.categoriesContract.COLUMN_DESCRIPTION_CATEGORY, String.valueOf(categoryDescription.getText().toString().trim()));
        values.put(CategoriesNotesContract.categoriesContract.COLUMN_COLOR, colorValue);
        values.put(CategoriesNotesContract.categoriesContract.COLUMN_CATEGORY_UNIQUE_ID, String.valueOf(Calendar.getInstance().getTimeInMillis()));
        dbWrite.insert(CategoriesNotesContract.categoriesContract.TABLE_NAME, null, values);
        Toast.makeText(getApplicationContext(), categoryTitle.getText().toString() + " category has been added", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void UpdateCategoryMethod(long categoryId) {
        /*This Code will update if changes made*/
        ContentValues valuesUpdate = new ContentValues();
        valuesUpdate.put(CategoriesNotesContract.categoriesContract.COLUMN_NAME_CATEGORY, String.valueOf(categoryTitle.getText().toString().trim()));
        valuesUpdate.put(CategoriesNotesContract.categoriesContract.COLUMN_DESCRIPTION_CATEGORY, String.valueOf(categoryDescription.getText().toString().trim()));
        valuesUpdate.put(CategoriesNotesContract.categoriesContract.COLUMN_COLOR, colorValue);
        dbWriteExist.update(CategoriesNotesContract.categoriesContract.TABLE_NAME, valuesUpdate, CategoriesNotesContract.categoriesContract.COLUMN_CATEGORY_UNIQUE_ID.concat(" = ?"), new String[]{currentCategoryId});

        /* Call method to edit Category in Notes Table*/
        DatabaseMethods databaseMethods = new DatabaseMethods();
        databaseMethods.editCatInNoteDB(ManageCategories.this, categoryId, String.valueOf(categoryTitle.getText().toString()), colorValue);
        Toast.makeText(ManageCategories.this, "Your category has been updated", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ManageCategories.this, MainActivity.class));
        dbWriteExist.close();
    }

    public void ChangeDividerColor(String colorValue) {
        dividerView.setBackgroundColor(Color.parseColor(colorValue));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbWrite.close();
    }
}
