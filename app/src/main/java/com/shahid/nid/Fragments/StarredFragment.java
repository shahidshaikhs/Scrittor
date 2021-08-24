package com.shahid.nid.Fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shahid.nid.Adapters.NoteRecyclerAdapter;
import com.shahid.nid.NoteDataStructure;
import com.shahid.nid.NotesContract;
import com.shahid.nid.NotesDbHelper;
import com.shahid.nid.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class StarredFragment extends Fragment {

    private RecyclerView recyclerView;
    private View rootView;
    private FloatingActionButton fab;

    public StarredFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_starred, container, false);
        fab = getActivity().findViewById(R.id.add_note);
        recyclerView = rootView.findViewById(R.id.starred_list);

        setStarredNotes();

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
        return rootView;
    }


    public void setStarredNotes() {

          /*Accessing Helper class*/
        NotesDbHelper mDbHelper = new NotesDbHelper(rootView.getContext());

        /*This is from where we are reading all the values*/
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.


        String[] projection = {
                NotesContract.mainNotes._ID,
                NotesContract.mainNotes.COLUMN_NAME_TITLE,
                NotesContract.mainNotes.COLUMN_NAME_CONTENT,
                NotesContract.mainNotes.COLUMN_STARRED_CHECK,
                NotesContract.mainNotes.COLUMN_DATE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = NotesContract.mainNotes.COLUMN_STARRED_CHECK + " = ?";
        String[] selectionArgs = {"starred"};

        Cursor cursor = db.query(
                NotesContract.mainNotes.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        ArrayList<NoteDataStructure> notesList = new ArrayList<>();
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


        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
        NoteRecyclerAdapter noteRecyclerAdapter = new NoteRecyclerAdapter(notesList, rootView.getContext());
        recyclerView.setAdapter(noteRecyclerAdapter);

        noteRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        setStarredNotes();
    }
}
