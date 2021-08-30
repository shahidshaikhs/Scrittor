package com.shahid.nid.Categories;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shahid.nid.Activties.AddNotesActivity;
import com.shahid.nid.Activties.BaseActivity;
import com.shahid.nid.Adapters.NoteRecyclerAdapter;
import com.shahid.nid.Note;
import com.shahid.nid.NotesContract;
import com.shahid.nid.R;
import com.shahid.nid.Utils.DbHelper;
import com.shahid.nid.interfaces.ItemClickListener;

import java.util.ArrayList;

public class SingleCategoryActivity extends BaseActivity {

    private String categoryValue;
    private TextView categoryDescriptionTop;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_category);

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

        category = DbHelper.getInstance(getApplication()).getCategoryByName(categoryValue);

        categoryDescriptionTop.setText(category.getCategoryName());

        if (category.getCategoryName().equals("Not Specified")) {
            optionsButton.setVisibility(View.GONE);
        }

        categoryEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (category.getCategoryName().equals("Not Specified")) {
                    Toast.makeText(SingleCategoryActivity.this, "This category cannot be edited", Toast.LENGTH_SHORT).show();
                    categoryDescriptionTop.setText("All notes that are not assigned to a specific category are displayed here");

                } else {
                    startActivity(new Intent(SingleCategoryActivity.this, ManageCategoriesActivity.class)
                            .putExtra("noteId", category.getCategoryUniqueId()));
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
                        DbHelper.getInstance(getApplication()).deleteCategory(category.getCategoryUniqueId());
                        Toast.makeText(SingleCategoryActivity.this, "Your category has been deleted", Toast.LENGTH_SHORT).show();
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
    }


    @Override
    protected void onResume() {
        super.onResume();
        setNotesInCategories();
    }

    public void setNotesInCategories(){
        String selection = NotesContract.MainNotes.COLUMN_CATEGORY + " = ?";
        String[] selectionArgs = {categoryValue};
        ArrayList<Note> notesList = new ArrayList<>(DbHelper.getInstance(getApplication()).fetchNotesBy(null, selection, selectionArgs));

        RecyclerView recyclerView = findViewById(R.id.categoriesList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SingleCategoryActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
        NoteRecyclerAdapter noteRecyclerAdapter = new NoteRecyclerAdapter(notesList, SingleCategoryActivity.this, new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Note note = ((NoteRecyclerAdapter) recyclerView.getAdapter()).getItem(position);
                startActivity(new Intent(SingleCategoryActivity.this, AddNotesActivity.class).putExtra("noteId", note.getNoteID()));
            }
        });
        recyclerView.setAdapter(noteRecyclerAdapter);
        noteRecyclerAdapter.notifyDataSetChanged();
    }
}
