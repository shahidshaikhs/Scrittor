package com.shahid.nid.Fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.shahid.nid.Adapters.NoteRecyclerAdapter;
import com.shahid.nid.NoteDataStructure;
import com.shahid.nid.NotesContract;
import com.shahid.nid.NotesDbHelper;
import com.shahid.nid.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private View rootView;
    private FloatingActionButton fab;
    NoteRecyclerAdapter noteRecyclerAdapter;
    ArrayList<NoteDataStructure> notesList;
    private SearchView searchView;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        fab = getActivity().findViewById(R.id.add_note);
        searchView = getActivity().findViewById(R.id.mSearch);

        EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.textColorSecondary));
        searchEditText.setHintTextColor(getResources().getColor(R.color.textColorSecondary));

        setValues();

        return rootView;
    }

    @Override
    public void onResume() {
        setValues();
        super.onResume();
    }


    public void setValues() {
        NotesDbHelper mDbHelper = new NotesDbHelper(rootView.getContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.

        notesList = new ArrayList<>();
        noteRecyclerAdapter = new NoteRecyclerAdapter(notesList, rootView.getContext());

        String[] projection = {
                NotesContract.mainNotes._ID,
                NotesContract.mainNotes.COLUMN_NAME_TITLE,
                NotesContract.mainNotes.COLUMN_NAME_CONTENT,
                NotesContract.mainNotes.COLUMN_DATE
        };

        Cursor cursor = db.query(
                NotesContract.mainNotes.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        while (cursor.moveToNext()) {
            String title = cursor.getString(
                    cursor.getColumnIndexOrThrow(NotesContract.mainNotes.COLUMN_NAME_TITLE));
            String content = cursor.getString(
                    cursor.getColumnIndexOrThrow(NotesContract.mainNotes.COLUMN_NAME_CONTENT));
            String creationDate = cursor.getString(
                    cursor.getColumnIndexOrThrow(NotesContract.mainNotes.COLUMN_DATE));
            int noteID = cursor.getInt(
                    cursor.getColumnIndexOrThrow(NotesContract.mainNotes._ID));

            notesList.add(new NoteDataStructure(title, content, creationDate, noteID));

        }
        cursor.close();

        RecyclerView recyclerView = rootView.findViewById(R.id.notes_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(noteRecyclerAdapter);

        noteRecyclerAdapter.notifyDataSetChanged();

        recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int dx, int dy) {
                if (dy > 0){
                    fab.hide();
                } else {
                    fab.show();
                }
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                noteRecyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
