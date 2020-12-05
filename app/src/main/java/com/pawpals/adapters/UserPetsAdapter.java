package com.pawpals.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.ParseFile;
import com.pawpals.R;
import com.pawpals.models.Pet;

import java.util.List;

public class UserPetsAdapter extends RecyclerView.Adapter<UserPetsAdapter.ViewHolder> {

    private Context context;
    private List<Pet> pets;

    public UserPetsAdapter(Context context, List<Pet> pets) {
        this.context = context;
        this.pets = pets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pet pet = pets.get(position);
        holder.bind(pet);
    }

    // Clean all elements of the recycler
    public void clear() {
        pets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Pet> list) {
        pets.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivPetImage;
        private TextView tvPetName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPetImage = itemView.findViewById(R.id.ivPetImage);
            tvPetName = itemView.findViewById(R.id.tvPetName);
        }

        public void bind(Pet pet) {
            // Bind the post data to the view elements
            tvPetName.setText(pet.getName());
            ParseFile image = pet.getPetImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl())
                .circleCrop()
                .into(ivPetImage);
            }
        }
    }
}
