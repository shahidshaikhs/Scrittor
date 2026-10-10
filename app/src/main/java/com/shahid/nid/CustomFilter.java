package com.shahid.nid;

import android.widget.Filter;

import com.shahid.nid.Adapters.NoteRecyclerAdapter;

import java.util.ArrayList;

/**
 * Created by Shahid Shaikh on 17,March,2018.
 */

public class CustomFilter extends Filter{

    private NoteRecyclerAdapter adapter;
    private ArrayList<Note> filterList;

    public CustomFilter(ArrayList<Note> filterList, NoteRecyclerAdapter adapter)
    {
        this.adapter=adapter;
        this.filterList=filterList;

    }

    private String safeText(String text) {
        return text == null ? "" : text.toUpperCase(java.util.Locale.ROOT);
    }

    //FILTERING OCURS
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();

        //CHECK CONSTRAINT VALIDITY
        if(constraint != null && constraint.length() > 0)
        {
            //CHANGE TO UPPER
            constraint=constraint.toString().toUpperCase(java.util.Locale.ROOT);
            //STORE OUR FILTERED PLAYERS
            ArrayList<Note> filteredPlayers=new ArrayList<>();

            for (int i=0;i<filterList.size();i++)
            {
                //CHECK
                if(safeText(filterList.get(i).getNoteTitle()).contains(constraint) || safeText(filterList.get(i).getNoteContent()).contains(constraint))
                {
                    //ADD PLAYER TO FILTERED PLAYERS
                    filteredPlayers.add(filterList.get(i));
                }
            }

            results.count=filteredPlayers.size();
            results.values=filteredPlayers;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;

        }


        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.list= (ArrayList<Note>) results.values;

        //REFRESH
        adapter.notifyDataSetChanged();
    }
}