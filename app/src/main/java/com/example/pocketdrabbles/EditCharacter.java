package com.example.pocketdrabbles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class EditCharacter extends AppCompatActivity {

    EditText name;
    EditText surname;
    EditText age;
    EditText sex;
    EditText description;
    FloatingActionButton done;
    FloatingActionButton delete;

    private String key;
    private String story;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_character);

        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        age = findViewById(R.id.age);
        sex = findViewById(R.id.sex);
        description = findViewById(R.id.description);
        done = findViewById(R.id.done);
        delete = findViewById(R.id.delete);

        key = getIntent().getExtras().getString("key");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("PocketDrabbles/characters");

        mDatabaseReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Character character = dataSnapshot.getValue(Character.class);
                name.setText(character.getName());
                surname.setText(character.getSurname());
                age.setText(String.valueOf(character.getAge()));
                sex.setText(character.getSex());
                description.setText(character.getDescription());
                story = character.getStory();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().equals("") || surname.getText().toString().equals("") || age.getText().toString().equals("") || sex.getText().toString().equals("") || description.getText().toString().equals(""))
                    Toast.makeText(EditCharacter.this, "Please fill in the required fields", Toast.LENGTH_SHORT).show();
                else {
                    update(name.getText().toString(), surname.getText().toString(), (int)Integer.valueOf(age.getText().toString()), sex.getText().toString(), description.getText().toString(), story);
                    Intent intent = new Intent(getApplicationContext(), StoryDashboard.class);
                    intent.putExtra("key", story);
                    startActivity(intent);
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(EditCharacter.this);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete this character?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DatabaseHelper("PocketDrabbles/characters").delete(key, new DatabaseHelper.DataStatus() {
                            @Override
                            public void DataIsLoaded(List<Story> stories, List<String> keys) {

                            }

                            @Override
                            public void DataIsInserted() {

                            }

                            @Override
                            public void DataIsUpdated() {

                            }

                            @Override
                            public void DataIsDeleted() {
                                Toast.makeText(EditCharacter.this, "Character deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), StoryDashboard.class);
                        intent.putExtra("key", story);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialogue = builder.create();
                dialogue.show();
            }
        });
    }

    private void update(String name, String surname, int age, String sex, String description, String story)
    {
        Character character = new Character(name, surname, age, sex, description, story);
        new DatabaseHelper("PocketDrabbles/characters").updateCharacter(key, character, new DatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Story> stories, List<String> keys) {

            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {
                Toast.makeText(EditCharacter.this, "Character info updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }
}
