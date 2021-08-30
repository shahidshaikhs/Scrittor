package com.shahid.nid.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shahid.nid.Activties.AddNotesActivity;
import com.shahid.nid.Adapters.NoteRecyclerAdapter;
import com.shahid.nid.Note;
import com.shahid.nid.R;
import com.shahid.nid.Utils.DbHelper;
import com.shahid.nid.interfaces.ItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private View rootView;
    private FloatingActionButton fab;
    NoteRecyclerAdapter noteRecyclerAdapter;
    ArrayList<Note> notesList;
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

        EditText searchEditText = searchView.findViewById(R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.textColorSecondary));
        searchEditText.setHintTextColor(getResources().getColor(R.color.textColorSecondary));

        notesList = new ArrayList<>();
        setValues();
        return rootView;
    }

    @Override
    public void onResume() {
        setValues();
        super.onResume();
    }

    public void setValues() {
        notesList.clear();
        noteRecyclerAdapter = new NoteRecyclerAdapter(notesList, rootView.getContext(), new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Note note = noteRecyclerAdapter.getItem(position);
                startActivity(new Intent(getContext(), AddNotesActivity.class).putExtra("noteId", note.getNoteID()));
            }
        });

        notesList.addAll(DbHelper.getInstance(getActivity().getApplication()).fetchAllNotesFromDb(null));
        Collections.sort(notesList, new Comparator<Note>() {
            @Override
            public int compare(Note o1, Note o2) {
                try {
                    return Long.getLong(o1.getLastEdited()).compareTo(Long.getLong(o2.getLastEdited()));
                } catch (Exception e){
                    return 1;
                }
            }
        });
        Collections.reverse(notesList);

        RecyclerView recyclerView = rootView.findViewById(R.id.notes_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
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
