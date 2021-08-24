package com.shahid.nid.CategoriesDialog;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shahid.nid.Activties.AddNotesActivity;
import com.shahid.nid.R;

import java.util.ArrayList;

/**
 * Created by shahi on 10/9/2017.
 */

public class DialogCategoriesRecyclerAdapter extends RecyclerView.Adapter<DialogCategoriesRecyclerAdapter.NoteViewHolder> {

    private Context context;
    private ArrayList<DialogCategoriesDataStructure> list;

    public DialogCategoriesRecyclerAdapter(ArrayList<DialogCategoriesDataStructure> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public DialogCategoriesRecyclerAdapter.NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_row, parent, false);

        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DialogCategoriesRecyclerAdapter.NoteViewHolder holder, final int position) {
        final DialogCategoriesDataStructure dialogCategory = list.get(position);

        holder.titleText.setText(dialogCategory.getCategoryName());
        holder.categoryId.setText( String.valueOf(dialogCategory.getCategoryID()));

        holder.container.setColorFilter(Color.parseColor(dialogCategory.getCategoryColor()));

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView cateogy =  ((AddNotesActivity) context).categoryLabelText;
                TextView categoryId =   ((AddNotesActivity) context).categoryIdentifier;
                TextView cateogyColor =  ((AddNotesActivity) context).categoryLabelColor;

                cateogy.setText(dialogCategory.getCategoryName());
                categoryId.setText( String.valueOf(dialogCategory.getCategoryID()));
                cateogyColor.setText(dialogCategory.getCategoryColor());

                ((AddNotesActivity) context).dismissDialog();
                ((AddNotesActivity) context).changeLabelIconColor(dialogCategory.getCategoryColor());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView titleText, categoryId;
        ImageView container;
        CardView  layout;

        NoteViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.category_name);
            container = itemView.findViewById(R.id.container);
            layout = itemView.findViewById(R.id.whole_layout);
            categoryId = itemView.findViewById(R.id.category_identifier);
        }
    }
}
