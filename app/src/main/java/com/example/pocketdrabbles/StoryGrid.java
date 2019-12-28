package com.example.pocketdrabbles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class StoryGrid extends AppCompatActivity {

    private RecyclerView mRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_grid);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getDrawable(R.drawable.ic_menu_black_24dp));
        setSupportActionBar(toolbar);

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingButton);

        mRecycler = (RecyclerView) findViewById(R.id.recyclerVIew);
        new DatabaseHelper("/PocketDrabbles/stories").getStories(new DatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Story> stories, List<String> keys) {
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(getApplicationContext(), stories, keys);
                mRecycler.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                ItemOffsetDecoration itemDeco = new ItemOffsetDecoration(getApplicationContext(), R.dimen.item_offset);
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
                startActivity(new Intent(StoryGrid.this, AddStory.class));
            }
        });
    }
}
