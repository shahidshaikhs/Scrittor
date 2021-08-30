package com.shahid.nid.Categories;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.shahid.nid.Activties.BaseActivity;
import com.shahid.nid.Activties.MainActivity;
import com.shahid.nid.R;
import com.shahid.nid.Utils.DbHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class ManageCategoriesActivity extends BaseActivity {

    private View dividerView;
    private EditText categoryTitle, categoryDescription;
    private String colorValue = "#8D6E63";
    private Category category;

    ArrayList<String> categoryNamesPresentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_categories);

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
                dividerView.setBackgroundColor(ContextCompat.getColor(ManageCategoriesActivity.this, R.color.color1));
                colorValue = "#EF5350";
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dividerView.setBackgroundColor(ContextCompat.getColor(ManageCategoriesActivity.this, R.color.color2));
                colorValue = "#6672B0";
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dividerView.setBackgroundColor(ContextCompat.getColor(ManageCategoriesActivity.this, R.color.color3));
                colorValue = "#6ECFFF";
            }
        });
        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dividerView.setBackgroundColor(ContextCompat.getColor(ManageCategoriesActivity.this, R.color.color4));
                colorValue = "#FFCA28";
            }
        });
        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dividerView.setBackgroundColor(ContextCompat.getColor(ManageCategoriesActivity.this, R.color.color5));
                colorValue = "#9CCC65";
            }
        });
        image6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dividerView.setBackgroundColor(ContextCompat.getColor(ManageCategoriesActivity.this, R.color.color6));
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

        HashMap<String, Category> categoryHashMap = DbHelper.getInstance(getApplication()).fetchAllCategoriesFromDb(null, CategoriesNotesContract.CategoriesContract.COLUMN_NAME_CATEGORY);

        categoryNamesPresentList.addAll(categoryHashMap.keySet());


        /*This code will only run when category needs to be edited*/
        if (getIntent().hasExtra("noteId")) {
            category = DbHelper.getInstance(getApplication()).getCategoryById(getIntent().getStringExtra("noteId"));

            categoryTitle.setText(category.getCategoryName());
            categoryDescription.setText(category.getDescription());
            ChangeDividerColor(category.getCategoryColor());
            colorValue = category.getCategoryColor();

            addCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (categoryTitle.getText().toString().trim().length() == 0) {
                        Toast.makeText(getApplicationContext(), "There is nothing to save. Kindly, write something.", Toast.LENGTH_SHORT).show();
                    } else {
                        updateCategoryMethod(Long.parseLong(category.getCategoryUniqueId()));
                    }

                }
            });
        } else {
            category = new Category();
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void addCategoryMethod() {
        if (categoryNamesPresentList.contains(categoryTitle.getText().toString().trim().toLowerCase())) {
            Snackbar.make(findViewById(android.R.id.content), R.string.category_exists, Snackbar.LENGTH_INDEFINITE).show();
            return;
        }

        category.setCategoryName(categoryTitle.getText().toString());
        category.setDescription(categoryDescription.getText().toString().trim());
        category.setCategoryColor(colorValue);
        category.setCategoryUniqueId(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        DbHelper.getInstance(getApplication()).insertNewCategory(category);
        Toast.makeText(getApplicationContext(), categoryTitle.getText().toString() + " category has been added", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void updateCategoryMethod(long categoryId) {
        if (categoryNamesPresentList.contains(categoryTitle.getText().toString().trim().toLowerCase())) {
            Snackbar.make(findViewById(android.R.id.content), R.string.category_exists, Snackbar.LENGTH_INDEFINITE).show();
            return;
        }
        category.setCategoryName(categoryTitle.getText().toString());
        category.setDescription(categoryDescription.getText().toString().trim());
        category.setCategoryColor(colorValue);
        DbHelper.getInstance(getApplication()).updateCategory(category);

        Toast.makeText(ManageCategoriesActivity.this, "Your category has been updated", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ManageCategoriesActivity.this, MainActivity.class));
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
    }
}
