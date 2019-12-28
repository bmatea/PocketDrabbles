package com.example.pocketdrabbles;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends  RecyclerView.Adapter<RecyclerViewAdapter.StoryViewHolder>{

    private Context mContext;
    private List<Story> stories;
    private List<String> keys;

    public RecyclerViewAdapter(Context mContext, List<Story> stories, List<String> keys) {
        this.mContext = mContext;
        this.stories = stories;
        this.keys = keys;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StoryViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        holder.bind(stories.get(position), keys.get(position));
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    protected class StoryViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView cover;

        private String key;

        public StoryViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(mContext).inflate(R.layout.cardview_item, parent, false));

            title = (TextView)itemView.findViewById(R.id.title);
            cover = (ImageView)itemView.findViewById(R.id.imageview);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(mContext, EditStory.class);
                    intent.putExtra("key", key);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, StoryDashboard.class);
                    intent.putExtra("key", key);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });
        }

        public void bind(Story story, String key) {
            title.setText(story.getTitle());
            Picasso.with(mContext).load(story.getCover()).into(cover);
            this.key = key;
        }

    }

}
