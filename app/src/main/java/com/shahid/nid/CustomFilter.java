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

    //FILTERING OCURS
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();

        //CHECK CONSTRAINT VALIDITY
        if(constraint != null && constraint.length() > 0)
        {
            //CHANGE TO UPPER
            constraint=constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS
            ArrayList<Note> filteredPlayers=new ArrayList<>();

            for (int i=0;i<filterList.size();i++)
            {
                //CHECK
                if(filterList.get(i).getNoteTitle().toUpperCase().contains(constraint) || filterList.get(i).getNoteContent().toUpperCase().contains(constraint))
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