package com.shahid.nid.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shahid.nid.Adapters.CategoriesRecyclerAdapter;
import com.shahid.nid.Categories.Category;
import com.shahid.nid.Categories.ManageCategoriesActivity;
import com.shahid.nid.Categories.SingleCategoryActivity;
import com.shahid.nid.R;
import com.shahid.nid.Utils.DbHelper;
import com.shahid.nid.interfaces.ItemClickListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment implements ItemClickListener {

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
                startActivity(new Intent(rootview.getContext(), ManageCategoriesActivity.class));
            }
        });
        return rootview;
    }


    public void setCategoryRecyclerViews() {
        final ArrayList<Category> categoryList = new ArrayList<>(DbHelper.getInstance(getActivity().getApplication()).fetchAllCategoriesFromDb(null));

        recyclerView = rootview.findViewById(R.id.categories_list);

        _sGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(_sGridLayoutManager);

        recyclerView.setLayoutManager(_sGridLayoutManager);
        CategoriesRecyclerAdapter noteRecyclerAdapter = new CategoriesRecyclerAdapter(categoryList, rootview.getContext(), this);
        recyclerView.setAdapter(noteRecyclerAdapter);

        noteRecyclerAdapter.notifyDataSetChanged();
    }



    @Override
    public void onResume() {
        super.onResume();
        setCategoryRecyclerViews();
    }

    @Override
    public void onItemClick(int position) {
        Category category = ((CategoriesRecyclerAdapter)recyclerView.getAdapter()).getItem(position);
        startActivity(new Intent(getContext(), SingleCategoryActivity.class).putExtra("categoryValue", category.getCategoryName())
                .putExtra("categoryColorValue", category.getCategoryColor()));
    }
}
