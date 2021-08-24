package com.shahid.nid.Categories;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shahid.nid.R;

import java.util.ArrayList;

/**
 * Created by shahi on 10/9/2017.
 */

public class CategoriesRecyclerAdapter extends RecyclerView.Adapter<CategoriesRecyclerAdapter.NoteViewHolder> {

    private Context context;
    private ArrayList<CategoriesDataStructure> list;

    public CategoriesRecyclerAdapter(ArrayList<CategoriesDataStructure> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public CategoriesRecyclerAdapter.NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_row, parent, false);
        CategoriesRecyclerAdapter.NoteViewHolder noteViewHolder = new CategoriesRecyclerAdapter.NoteViewHolder(view);

        return noteViewHolder;
    }

    @Override
    public void onBindViewHolder(final CategoriesRecyclerAdapter.NoteViewHolder holder, final int position) {
        final CategoriesDataStructure category = list.get(position);

        holder.titleText.setText(category.getCategoryName());
        holder.container.setColorFilter(Color.parseColor(category.getCategoryColor()));

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, SingleCategoryActivity.class).putExtra("categoryValue", category.getCategoryName())
                                                                                        .putExtra("categoryColorValue", category.getCategoryColor()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView titleText;
        ImageView container;
        CardView layout;

        NoteViewHolder(View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.category_name);
            container = itemView.findViewById(R.id.container);
            layout = itemView.findViewById(R.id.whole_layout);
        }
    }
}
