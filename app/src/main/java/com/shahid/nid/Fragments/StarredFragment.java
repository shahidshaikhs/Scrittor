package com.shahid.nid.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shahid.nid.Activties.AddNotesActivity;
import com.shahid.nid.Adapters.NoteRecyclerAdapter;
import com.shahid.nid.Note;
import com.shahid.nid.NotesContract;
import com.shahid.nid.R;
import com.shahid.nid.Utils.DbHelper;
import com.shahid.nid.interfaces.ItemClickListener;

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
        String selection = NotesContract.MainNotes.COLUMN_STARRED_CHECK + " = ?";
        String[] selectionArgs = {"starred"};
        ArrayList<Note> notesList = new ArrayList<>(DbHelper.getInstance(getActivity().getApplication()).fetchNotesBy(null, selection, selectionArgs));

        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
        NoteRecyclerAdapter noteRecyclerAdapter = new NoteRecyclerAdapter(notesList, rootView.getContext(), new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Note note = ((NoteRecyclerAdapter)recyclerView.getAdapter()).getItem(position);
                startActivity(new Intent(getContext(), AddNotesActivity.class).putExtra("noteId", note.getNoteID()));
            }
        });

        recyclerView.setAdapter(noteRecyclerAdapter);

        noteRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        setStarredNotes();
    }
}
