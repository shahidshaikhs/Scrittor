package com.shahid.nid.Categories;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shahid.nid.Activties.MainActivity;
import com.shahid.nid.Adapters.NoteRecyclerAdapter;
import com.shahid.nid.NoteDataStructure;
import com.shahid.nid.NotesContract;
import com.shahid.nid.NotesDbHelper;
import com.shahid.nid.R;
import com.shahid.nid.Utils.DatabaseMethods;

import java.util.ArrayList;



public class SingleCategoryActivity extends AppCompatActivity {

    private String categoryValue;
    private TextView categoryDescriptionTop;
    private SQLiteDatabase dbRead;

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

        setContentView(R.layout.activity_single_category);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        TextView categoryLableTop = findViewById(R.id.categoryText);
        categoryDescriptionTop = findViewById(R.id.categoryDescriptionText);

        categoryValue = getIntent().getStringExtra("categoryValue");
        String categoryColorValue = getIntent().getStringExtra("categoryColorValue");

        RelativeLayout categoryEdit = findViewById(R.id.edit_category_details);
        ImageView optionsButton = findViewById(R.id.options_button);
        ImageView backButton = findViewById(R.id.back_button);

        categoryLableTop.setText(categoryValue);
        categoryLableTop.setTextColor(Color.parseColor(categoryColorValue));

        setNotesInCategories();

        CategoriesDbHelper categoriesDbHelper = new CategoriesDbHelper(this);
        dbRead = categoriesDbHelper.getReadableDatabase();

        String selectionCat = CategoriesNotesContract.categoriesContract.COLUMN_NAME_CATEGORY + " = ?";
        String[] selectionArgsCat = {categoryValue};

        Cursor cursorCat = dbRead.query(
                CategoriesNotesContract.categoriesContract.TABLE_NAME,                     // The table to query
                null,                               // The columns to return.. null means get all columns
                selectionCat,                                // The columns for the WHERE clause
                selectionArgsCat,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        cursorCat.moveToNext();
        final String category = cursorCat.getString(cursorCat.getColumnIndexOrThrow(CategoriesNotesContract.categoriesContract.COLUMN_NAME_CATEGORY)); //yeh change kab hua XD lol
        final String categoryID = cursorCat.getString(cursorCat.getColumnIndexOrThrow(CategoriesNotesContract.categoriesContract.COLUMN_CATEGORY_UNIQUE_ID));

        categoryDescriptionTop.setText(category);
        cursorCat.close();

        if (category.equalsIgnoreCase("Not Specified")) {
            optionsButton.setVisibility(View.GONE);
        }

        categoryEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (category.equals("Not Specified")) {
                    Toast.makeText(SingleCategoryActivity.this, "This category cannot be edited", Toast.LENGTH_SHORT).show();
                    categoryDescriptionTop.setText("All notes that are not assigned to a specific category are displayed here");

                } else {
                    startActivity(new Intent(SingleCategoryActivity.this, ManageCategories.class)
                            .putExtra("noteId", categoryID));
                    finish();
                }
            }
        });

        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create custom dialog object
                Dialog deleteDialog = new Dialog(SingleCategoryActivity.this);
                // Include dialog.xml file
                deleteDialog.setContentView(R.layout.dialog_delete_confirmation);
                deleteDialog.setTitle(null);

                TextView agree, disagree, title, content;
                agree = deleteDialog.findViewById(R.id.agree);
                disagree = deleteDialog.findViewById(R.id.disagree);
                title = deleteDialog.findViewById(R.id.title);
                content = deleteDialog.findViewById(R.id.textContent);

                title.setText("Delete " + categoryValue);
                content.setText("Permanently delete " + "\"" + categoryValue + "\"" + " category and move the notes to \"Not Specified\" section. Are you sure you want to continue?");

                agree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseMethods databaseMethods = new DatabaseMethods();
                        databaseMethods.deleteCategory(SingleCategoryActivity.this, categoryID);
                        Toast.makeText(SingleCategoryActivity.this, "Your category has been deleted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SingleCategoryActivity.this, MainActivity.class));
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbRead.close();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setNotesInCategories();
    }

    public void setNotesInCategories(){
        /*Accessing Helper class*/
        NotesDbHelper mDbHelper = new NotesDbHelper(SingleCategoryActivity.this);

        /*This is from where we are reading all the values*/
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                NotesContract.mainNotes._ID,
                NotesContract.mainNotes.COLUMN_NAME_TITLE,
                NotesContract.mainNotes.COLUMN_NAME_CONTENT,
                NotesContract.mainNotes.COLUMN_CATEGORY,
                NotesContract.mainNotes.COLUMN_DATE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = NotesContract.mainNotes.COLUMN_CATEGORY + " = ?";
        String[] selectionArgs = {categoryValue};

        Cursor cursor = db.query(
                NotesContract.mainNotes.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        ArrayList<NoteDataStructure> notesList = new ArrayList<NoteDataStructure>();
        while (cursor.moveToNext()) {
            String title = cursor.getString(
                    cursor.getColumnIndexOrThrow(NotesContract.mainNotes.COLUMN_NAME_TITLE));
            String content = cursor.getString(
                    cursor.getColumnIndexOrThrow(NotesContract.mainNotes.COLUMN_NAME_CONTENT));
            String creationDate = cursor.getString(
                    cursor.getColumnIndexOrThrow(NotesContract.mainNotes.COLUMN_DATE));
            int noteID = cursor.getInt(
                    cursor.getColumnIndexOrThrow(NotesContract.mainNotes._ID));

            notesList.add(new NoteDataStructure(title, content, creationDate, noteID));

        }
        cursor.close();

        RecyclerView recyclerView = findViewById(R.id.categoriesList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SingleCategoryActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
        NoteRecyclerAdapter noteRecyclerAdapter = new NoteRecyclerAdapter(notesList, SingleCategoryActivity.this);
        recyclerView.setAdapter(noteRecyclerAdapter);
        noteRecyclerAdapter.notifyDataSetChanged();
    }
}
