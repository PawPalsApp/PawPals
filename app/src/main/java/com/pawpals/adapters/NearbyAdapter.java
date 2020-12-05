package com.pawpals.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseUser;
import com.pawpals.R;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.ViewHolder> {

    private final Context context;
    private List<ParseUser> nearbyList;

    public NearbyAdapter(Context context, List<ParseUser> nearbyList) {
        this.context = context;
        this.nearbyList = nearbyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser nearby = nearbyList.get(position);
        holder.bind(nearby);
    }

    @Override
    public int getItemCount() {
        return nearbyList.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        nearbyList.clear();
        notifyDataSetChanged();
    }
    // Add a new list of tweets
    public void addAll(List<ParseUser> newNearby) {
        nearbyList.addAll(newNearby);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvDisplayName;
        private final TextView tvDist;
        private final ConstraintLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDisplayName = itemView.findViewById(R.id.tvDisplayName);
            tvDist = itemView.findViewById(R.id.tvDist);
            container = itemView.findViewById(R.id.container);
        }

        // binds the post data to the view element
        public void bind(final ParseUser nearby) {
            tvDisplayName.setText(nearby.getUsername());
            // finding and displaying the distance between the current user and the closest user to him
            double distance = ParseUser.getCurrentUser().getParseGeoPoint("location").distanceInKilometersTo(nearby.getParseGeoPoint("location"));
            tvDist.setText(String.format(Locale.ENGLISH, "%1$,.2f km", distance));
            // register click listener on the whole row (container)
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // navigate to a new activity on tap
                    //Intent i = new Intent(context, DetailActivity.class);
                    // Pass data object in the bundle and populate details activity.
                    //i.putExtra("post", Parcels.wrap(post));
                    //Pair<View, String> profilePicture = Pair.create((View)ivProfilePicture, "profilePicture");
                    //ActivityOptionsCompat options;
                    //if (image != null) {
                    //    Pair<View, String> postImage = Pair.create((View)ivPost, "postImage");
                    //    options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, profilePicture, postImage);
                    //} else {
                    //    options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, profilePicture);
                    //}
                    //context.startActivity(i, options.toBundle());
                }
            });

        }
    }
}
