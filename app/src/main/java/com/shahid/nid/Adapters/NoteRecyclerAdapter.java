package com.shahid.nid.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shahid.nid.Activties.AddNotesActivity;
import com.shahid.nid.CustomFilter;
import com.shahid.nid.Note;
import com.shahid.nid.R;
import com.shahid.nid.interfaces.ItemClickListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by shahi on 9/3/2017.
 */

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.NoteViewHolder> implements Filterable {

    private Context context;
    public ArrayList<Note> list;
    private ArrayList<Note> filterList;
    private ItemClickListener listener;

    private CustomFilter filter;

    public NoteRecyclerAdapter(ArrayList<Note> list , Context context, ItemClickListener listener)  {
        this.context = context;
        this.list = list;
        this.filterList=list;
        this.listener = listener;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_notes_row, parent, false);
        return new NoteViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(final NoteViewHolder holder, final int position) {
        final Note note = list.get(position);
        holder.titleText.setText(note.getNoteTitle());
        holder.textContent.setText(note.getNoteContent());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Note getItem(int position){
        return list.get(position);
    }

    //RETURN FILTER OBJ
    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new CustomFilter(filterList,this);
        }

        return filter;
    }


    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titleText, textContent;
        LinearLayout container;
        ItemClickListener listener;

        NoteViewHolder(View itemView, ItemClickListener listener) {
            super(itemView);
            this.listener = listener;
            titleText = itemView.findViewById(R.id.title_text);
            textContent = itemView.findViewById(R.id.note_content);
            container = itemView.findViewById(R.id.note_container);
            itemView.setOnClickListener(this);

            String textLimiterValue = context.getSharedPreferences(context.getResources()
                    .getString(R.string.MY_PREFS_TEXT_LIMITER), MODE_PRIVATE)
                    .getString("integer", null);

            if(textLimiterValue !=null) {
                switch (textLimiterValue) {
                    case "one":
                        textContent.setMaxLines(1);
                        break;
                    case "two":
                        textContent.setMaxLines(2);
                        break;
                    case "three":
                        textContent.setMaxLines(3);
                        break;
                    case "four":
                        textContent.setMaxLines(4);
                        break;
                    case "five":
                        textContent.setMaxLines(5);
                        break;
                    default:
                        textContent.setMaxLines(3);
                        break;
                }
            }
        }

        @Override
        public void onClick(View v) {
            if (listener != null){
                listener.onItemClick(getAdapterPosition());
            }
        }
    }
}
