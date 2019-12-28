package com.example.pocketdrabbles;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper  {
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private List<Story> stories = new ArrayList<Story>();
    private List<Character> characters = new ArrayList<Character>();

    public interface DataStatus {
        void DataIsLoaded(List<Story> stories, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public interface CharacterDataStatus {
        void DataIsLoaded(List<Character> stories, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public DatabaseHelper(String table) {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference(table);
    }

    public void getStories(final DataStatus dataStatus) {
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stories.clear();
                List<String> keys = new ArrayList<String>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren())
                {
                    keys.add(keyNode.getKey());
                    Story story = keyNode.getValue(Story.class);
                    stories.add(story);
                }
                dataStatus.DataIsLoaded(stories, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getCharacters(final CharacterDataStatus dataStatus) {
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                characters.clear();
                List<String> keys = new ArrayList<String>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren())
                {
                    keys.add(keyNode.getKey());
                    Character character = keyNode.getValue(Character.class);
                    characters.add(character);
                }
                dataStatus.DataIsLoaded(characters, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getCharacters(String key, final CharacterDataStatus dataStatus) {
        Query query = mDatabaseReference.orderByChild("story").equalTo(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                characters.clear();
                List<String> keys = new ArrayList<String>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren())
                {
                    keys.add(keyNode.getKey());
                    characters.add(keyNode.getValue(Character.class));
                }
                dataStatus.DataIsLoaded(characters, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addStory(Story story, final DataStatus dataStatus) {
        String key = mDatabaseReference.push().getKey();
        mDatabaseReference.child(key).setValue(story).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dataStatus.DataIsInserted();
            }
        });
    }

    public void addCharacter(Character character, final DataStatus dataStatus) {
        String key = mDatabaseReference.push().getKey();
        mDatabaseReference.child(key).setValue(character).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dataStatus.DataIsInserted();
            }
        });
    }

    public void updateStory(String key, Story story, final DataStatus status)
    {
        mDatabaseReference.child(key).setValue(story).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                status.DataIsUpdated();
            }
        });
    }

    public void updateCharacter(String key, Character character, final DataStatus status)
    {
        mDatabaseReference.child(key).setValue(character).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                status.DataIsUpdated();
            }
        });
    }

    public void delete(String key, final DataStatus status)
    {
        mDatabaseReference.child(key).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                status.DataIsDeleted();
            }
        });
    }
}
