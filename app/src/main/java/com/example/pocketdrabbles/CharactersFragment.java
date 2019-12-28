package com.example.pocketdrabbles;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CharactersFragment extends Fragment {
    private Context context;
    RecyclerView mRecycler;
    private String key;

    public CharactersFragment(String key, Context context) {

        this.key = key;
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_characters, container, false);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingButton);

        mRecycler = (RecyclerView) view.findViewById(R.id.recyclerView);
        new DatabaseHelper("/PocketDrabbles/characters").getCharacters(key, new DatabaseHelper.CharacterDataStatus() {
            @Override
            public void DataIsLoaded(List<Character> characters, List<String> keys) {
                CharacterAdapter adapter = new CharacterAdapter(context, characters, keys);
                mRecycler.setLayoutManager(new GridLayoutManager(context, 2));
                ItemOffsetDecoration itemDeco = new ItemOffsetDecoration(context, R.dimen.item_offset);
                mRecycler.addItemDecoration(itemDeco);
                mRecycler.setAdapter(adapter);
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddCharacter.class);
                intent.putExtra("key", getActivity().getIntent().getExtras().getString("key"));
                startActivity(intent);
            }
        });
        return view;
    }

}
