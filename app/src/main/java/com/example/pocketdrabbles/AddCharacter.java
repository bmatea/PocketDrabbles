package com.example.pocketdrabbles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AddCharacter extends AppCompatActivity {

    private String key;
    DatabaseReference mDatabaseReference;

    EditText name;
    EditText surname;
    EditText age;
    EditText sex;
    EditText description;
    FloatingActionButton done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_character);

        key = getIntent().getExtras().getString("key");

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("PocketDrabbles/characters");
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        age = findViewById(R.id.age);
        sex = findViewById(R.id.sex);
        description = findViewById(R.id.description);
        done = findViewById(R.id.done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().equals("") || surname.getText().toString().equals("") || age.getText().toString().equals("") || sex.getText().toString().equals("") || description.getText().toString().equals(""))
                    Toast.makeText(AddCharacter.this, "Please fill out the required fields", Toast.LENGTH_SHORT).show();
                else {
                    Character character = new Character(
                            name.getText().toString(),
                            surname.getText().toString(),
                            (int)Integer.valueOf(age.getText().toString()),
                            sex.getText().toString(),
                            description.getText().toString(),
                            key );
                    new DatabaseHelper("PocketDrabbles/characters").addCharacter(character, new DatabaseHelper.DataStatus() {

                        @Override
                        public void DataIsLoaded(List<Story> stories, List<String> keys) {

                        }

                        @Override
                        public void DataIsInserted() {
                            Toast.makeText(AddCharacter.this, "Character added", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void DataIsUpdated() {

                        }

                        @Override
                        public void DataIsDeleted() {

                        }
                    });
                    Intent intent = new Intent(getApplicationContext(), StoryDashboard.class);
                    intent.putExtra("key", key);
                    startActivity(intent);
                }
            }
        });
    }
}
