package com.pawpals.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pawpals.R;
import com.pawpals.adapters.UserPetsAdapter;
import com.pawpals.models.Pet;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private TextView tvUsername;
    private ImageView ivProfileImage;
    private TextView tvBio;
    private RecyclerView rvUserPets;
    protected ParseUser user;
    protected UserPetsAdapter adapter;
    protected List<Pet> userPets;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvUsername = view.findViewById(R.id.tvUsername);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvBio = view.findViewById(R.id.tvBio);

        user = ParseUser.getCurrentUser();

        tvUsername.setText(user.getUsername());
        tvBio.setText(user.getString("bio"));

        // TODO: Fix issue getting profile image
        ParseFile image = user.getParseFile("profileImage");
        if (image != null) {
            Glide.with(getContext()).load(image.getUrl())
            .into(ivProfileImage);
        }
        else {
            Log.i(TAG, "Issue with getting profile image");
        }

        rvUserPets = view.findViewById(R.id.rvUserPets);

        userPets = new ArrayList<>();
        adapter = new UserPetsAdapter(getContext(), userPets);

        rvUserPets.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvUserPets.setLayoutManager(linearLayoutManager);

        queryPets();
    }

    protected void queryPets() {
        ParseQuery<Pet> query = ParseQuery.getQuery(Pet.class);
        query.include(Pet.KEY_OWNER);
        query.whereEqualTo(Pet.KEY_OWNER, user);
        query.addDescendingOrder(Pet.KEY_CREATED_AT);
        query.findInBackground((pets, e) -> {
            if (e != null) {
                Log.e(TAG, "Issue with getting posts", e);
                return;
            }
            for (Pet pet : pets) {
                Log.i(TAG, "Pet: " + pet.getName() + ", Owner: "
                        + pet.getOwner().getUsername());
            }
            userPets.addAll(pets);
            adapter.notifyDataSetChanged();
        });
    }
}