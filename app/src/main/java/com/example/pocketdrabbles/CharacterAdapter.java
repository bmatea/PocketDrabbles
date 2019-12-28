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

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder> {

    private Context mContext;
    private List<Character> characters;
    private List<String> keys;

    public CharacterAdapter(Context mContext, List<Character> characters, List<String> keys) {
        this.mContext = mContext;
        this.characters = characters;
        this.keys = keys;
    }

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CharacterViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        holder.bind(characters.get(position), keys.get(position));
    }

    @Override
    public int getItemCount() {
        return characters.size();
    }


    protected class CharacterViewHolder extends RecyclerView.ViewHolder
    {
        TextView fullName;
        ImageView personIcon;
        private String key;

        public CharacterViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(mContext).inflate(R.layout.charactercardview_item, parent, false));

            fullName = (TextView) itemView.findViewById(R.id.fullName);
            personIcon = (ImageView) itemView.findViewById(R.id.cover);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, EditCharacter.class);
                    intent.putExtra("key", key);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });

        }

        public void bind(Character character, String key) {
            fullName.setText(character.getName() + " " + character.getSurname());
            if(character.getSex().equals("m"))
                Picasso.with(mContext).load(R.drawable.male).into(personIcon);
            else if(character.getSex().equals("f"))
                Picasso.with(mContext).load(R.drawable.female).into(personIcon);
            this.key = key;
        }
    }
}
