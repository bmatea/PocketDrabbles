package com.example.pocketdrabbles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class StoryDashboard extends AppCompatActivity {

    private TabLayout tab;
    private AppBarLayout appBarLayout;
    private ViewPager vpager;

    private String key;

    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_dashboard);

        tab = findViewById(R.id.tablayout);
        appBarLayout = findViewById(R.id.dashboard);
        vpager = findViewById(R.id.viewpager);

        key = getIntent().getExtras().getString("key");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("PocketDrabbles/stories").child(key);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Story.class) != null)
                {
                    Picasso.with(getApplicationContext()).load(dataSnapshot.getValue(Story.class).getCover()).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            appBarLayout.setBackground(new BitmapDrawable(getApplicationContext().getResources(), bitmap));
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CharactersFragment(key, getApplicationContext()), "Characters");
        adapter.addFragment(new TheasaurusFragment(getApplicationContext()), "Theasurus");
        adapter.addFragment(new DialogFragment(getApplicationContext()), "Dialogs");

        vpager.setAdapter(adapter);
        tab.setupWithViewPager(vpager);
    }
}
