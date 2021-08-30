package com.shahid.nid.Adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shahid.nid.Categories.Category;
import com.shahid.nid.interfaces.ItemClickListener;
import com.shahid.nid.R;

import java.util.ArrayList;

/**
 * Created by shahi on 10/9/2017.
 */

public class CategoriesRecyclerAdapter extends RecyclerView.Adapter<CategoriesRecyclerAdapter.NoteViewHolder> {

    private Context context;
    private ArrayList<Category> list;
    private ItemClickListener listener;

    public CategoriesRecyclerAdapter(ArrayList<Category> list, Context context, ItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public CategoriesRecyclerAdapter.NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_row, parent, false);
        CategoriesRecyclerAdapter.NoteViewHolder noteViewHolder = new CategoriesRecyclerAdapter.NoteViewHolder(view, listener);
        return noteViewHolder;
    }

    @Override
    public void onBindViewHolder(final CategoriesRecyclerAdapter.NoteViewHolder holder, final int position) {
        final Category category = list.get(position);

        holder.titleText.setText(category.getCategoryName());
        holder.categoryId.setText(String.valueOf(category.getCategoryUniqueId()));
        holder.container.setColorFilter(Color.parseColor(category.getCategoryColor()));

    }

    public Category getItem(int position){
        return list.get(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titleText, categoryId;
        ImageView container;
        CardView layout;
        ItemClickListener listener;

        NoteViewHolder(View itemView, ItemClickListener listener) {
            super(itemView);
            titleText = itemView.findViewById(R.id.category_name);
            container = itemView.findViewById(R.id.container);
            layout = itemView.findViewById(R.id.whole_layout);
            categoryId = itemView.findViewById(R.id.category_identifier);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null){
                listener.onItemClick(getAdapterPosition());
            }
        }
    }
}
