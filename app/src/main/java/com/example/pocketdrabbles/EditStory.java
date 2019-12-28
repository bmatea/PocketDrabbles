package com.example.pocketdrabbles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class EditStory extends AppCompatActivity {

    String title;
    String description;
    String key;
    String imageUri;

    EditText titleEdit;
    EditText descriptionEdit;
    ImageView image;

    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    private Story story;
    String coverUrl;

    DatabaseReference dbr;
    StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_story);

        dbr = FirebaseDatabase.getInstance().getReference("PocketDrabbles/stories");

        titleEdit = findViewById(R.id.title);
        descriptionEdit = findViewById(R.id.description);
        image = findViewById(R.id.imageview);
        FloatingActionButton done = findViewById(R.id.done);
        FloatingActionButton delete = findViewById(R.id.delete);
        Button add = findViewById(R.id.addButton);

        key = getIntent().getStringExtra("key");
        DatabaseReference reference = dbr.child(key);
        mStorageReference = FirebaseStorage.getInstance().getReference("images");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Story.class) != null)
                {
                   Story story =  dataSnapshot.getValue(Story.class);
                    titleEdit.setText(story.getTitle());
                    title = story.getTitle();
                    descriptionEdit.setText(story.getDescription());
                    description = story.getDescription();
                    coverUrl = story.getCover();
                    Picasso.with(getApplicationContext()).load(coverUrl).into(image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(EditStory.this);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete this story?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DatabaseHelper("PocketDrabbles/stories").delete(key, new DatabaseHelper.DataStatus() {
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
                                Toast.makeText(EditStory.this, "Story deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), StoryGrid.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if(filePath != null) {
            final StorageReference ref = mStorageReference.child("images/" + System.currentTimeMillis() + "." + getFIleExtension(filePath));
            //ref.putFile(filePath);
            if (ref.getFile(filePath) == null)
            {
                UploadTask uploadTask = ref.putFile(filePath);

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful())
                            throw task.getException();
                        return ref.getDownloadUrl();
                }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            update(titleEdit.getText().toString(), descriptionEdit.getText().toString(), task.getResult().toString());
                        } else {
                            Toast.makeText(EditStory.this, "Error in getting uri for db!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else
            {
                update(titleEdit.getText().toString(), descriptionEdit.getText().toString(), coverUrl);
            }
        }else {
            update(titleEdit.getText().toString(), descriptionEdit.getText().toString(), coverUrl);
        }
    }


    public void update(String title, String description, String cover)
    {
        story = new Story();
        story.setTitle(title);
        story.setDescription(description);
        story.setCover(cover);
        new DatabaseHelper("PocketDrabbles/stories").updateStory(key, story, new DatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Story> stories, List<String> keys) {

            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {
                Toast.makeText(EditStory.this, "Story updated successfuly", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void DataIsDeleted() {

            }
        });
        startActivity(new Intent(getApplicationContext(), StoryGrid.class));
    }

    public String getFIleExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimetypemap = MimeTypeMap.getSingleton();
        return mimetypemap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
