package com.shahid.nid.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
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
import com.shahid.nid.NoteDataStructure;
import com.shahid.nid.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by shahi on 9/3/2017.
 */

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.NoteViewHolder> implements Filterable {

    private Context context;
    public ArrayList<NoteDataStructure> list;
    private ArrayList<NoteDataStructure> filterList;

    private CustomFilter filter;

    Gson gson;
    public NoteRecyclerAdapter(ArrayList<NoteDataStructure> list , Context context)  {
        this.context = context;
        this.list = list;
        this.filterList=list;
        gson = new Gson();
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_notes_row, parent, false);

        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NoteViewHolder holder, final int position) {
        final NoteDataStructure noteDataStructure = list.get(position);
        holder.titleText.setText(noteDataStructure.getNoteTitle());
        holder.textContent.setText(noteDataStructure.getNoteContent());


        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, AddNotesActivity.class).putExtra("noteId", noteDataStructure.getNoteID()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView titleText, textContent;
        LinearLayout container;

        NoteViewHolder(View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.title_text);
            textContent = itemView.findViewById(R.id.note_content);
            container = itemView.findViewById(R.id.note_container);

            SharedPreferences textLimiterPrefs = context.getSharedPreferences(context.getResources().getString(R.string.MY_PREFS_TEXT_LIMITER), MODE_PRIVATE);
            String textLimiterValue = textLimiterPrefs.getString("integer", null);

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
}
