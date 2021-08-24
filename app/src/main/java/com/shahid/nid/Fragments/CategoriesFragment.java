package com.shahid.nid.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.shahid.nid.Categories.CategoriesDataStructure;
import com.shahid.nid.Categories.CategoriesDbHelper;
import com.shahid.nid.Categories.CategoriesNotesContract;
import com.shahid.nid.Categories.CategoriesRecyclerAdapter;
import com.shahid.nid.Categories.ManageCategories;
import com.shahid.nid.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment {

    private View rootview;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager _sGridLayoutManager;

    public CategoriesFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_categories, container, false);

        fab = getActivity().findViewById(R.id.add_note);
        final Button addCategory = getActivity().findViewById(R.id.new_category_button);
        setCategoryRecyclerViews();

        recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int dx, int dy) {
                if (dy > 0){
                    addCategory.animate().scaleX(0).scaleY(0).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            addCategory.setVisibility(View.GONE);
                        }
                    }).start();
                } else {
                    addCategory.animate().scaleX(1).scaleY(1).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            addCategory.setVisibility(View.VISIBLE);
                        }
                    }).start();
                }
                return false;
            }
        });

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(rootview.getContext(), ManageCategories.class));
            }
        });
        return rootview;
    }


    public void setCategoryRecyclerViews() {

        /*Accessing Helper class*/
        CategoriesDbHelper mDbHelper = new CategoriesDbHelper(rootview.getContext());

        /*This is from where we are reading all the values*/
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                CategoriesNotesContract.categoriesContract.TABLE_NAME,                     // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        final ArrayList<CategoriesDataStructure> categoryList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String title = cursor.getString(
                    cursor.getColumnIndexOrThrow(CategoriesNotesContract.categoriesContract.COLUMN_NAME_CATEGORY));
            String color = cursor.getString(
                    cursor.getColumnIndexOrThrow(CategoriesNotesContract.categoriesContract.COLUMN_COLOR));

            categoryList.add(new CategoriesDataStructure(title, color));

        }
        cursor.close();

        recyclerView = rootview.findViewById(R.id.categories_list);

        _sGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(_sGridLayoutManager);

        recyclerView.setLayoutManager(_sGridLayoutManager);
        CategoriesRecyclerAdapter noteRecyclerAdapter = new CategoriesRecyclerAdapter(categoryList, rootview.getContext());
        recyclerView.setAdapter(noteRecyclerAdapter);

        noteRecyclerAdapter.notifyDataSetChanged();
    }



    @Override
    public void onResume() {
        super.onResume();
        setCategoryRecyclerViews();
    }
}
