package com.example.pocketdrabbles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class AddStory extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 1;

    private String coverUrl;
    private Story story;

    EditText title;
    EditText description;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        image = findViewById(R.id.imageview);
        FloatingActionButton done = findViewById(R.id.done);
        Button add = findViewById(R.id.addButton);

        mStorageReference = FirebaseStorage.getInstance().getReference("images");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("PocketDrabbles/stories");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
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
            if (!ref.getFile(filePath).isSuccessful())
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
                            save(title.getText().toString(), description.getText().toString(), task.getResult().toString());
                        } else {
                            Toast.makeText(AddStory.this, "Error in getting uri for db!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else
            {
                save(title.getText().toString(), description.getText().toString(), coverUrl);
            }
        }
    }

    public String getFIleExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimetypemap = MimeTypeMap.getSingleton();
        return mimetypemap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void save(String title, String description, String cover)
    {
        story = new Story();
        story.setTitle(title);
        story.setDescription(description);
        story.setCover(cover);
        new DatabaseHelper("PocketDrabbles/stories").addStory(story, new DatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Story> stories, List<String> keys) {

            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {
                Toast.makeText(AddStory.this, "Story updated successfuly", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void DataIsDeleted() {

            }
        });
        startActivity(new Intent(getApplicationContext(), StoryGrid.class));
    }
}
