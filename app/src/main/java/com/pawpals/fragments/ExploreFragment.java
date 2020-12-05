package com.pawpals.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pawpals.EndlessRecyclerViewScrollListener;
import com.pawpals.R;
import com.pawpals.adapters.NearbyAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExploreFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "ExploreFragment";

    protected RecyclerView rvNearby;
    protected NearbyAdapter adapter;
    protected List<ParseUser> nearbyUsers;
    protected SwipeRefreshLayout swipeContainer;

    private GoogleMap map;
    private CameraPosition cameraPosition;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    // A default location (Sydney, Australia) and default zoom to use when location permission is not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 14;
    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;
    private boolean locationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        // save current location
        saveCurrentUserLocation();
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        rvNearby = view.findViewById(R.id.rvNearby);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light));
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching data!");
                queryNearbyUsers(false);
            }
        });

        nearbyUsers = new ArrayList<>();
        // 0. create layout for one row in the list
        // 1. create the adapter
        adapter = new NearbyAdapter(getContext(), nearbyUsers);
        // 2. create the data source
        // 3. set the adapter on the recycler view
        rvNearby.setAdapter(adapter);
        // 4. set the layout manager on the recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvNearby.setLayoutManager(layoutManager);
        // add divider between
        //DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvNearby.getContext(), layoutManager.getOrientation());
        //mDividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(rvNearby.getContext(), R.drawable.divider)));
        //rvPosts.addItemDecoration(mDividerItemDecoration);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + page);
                // TODO: turn to true to add on instead of replace
                queryNearbyUsers(false);
            }
        };
        // Adds a scroll listener to RecyclerView
        rvNearby.addOnScrollListener(scrollListener);
    }

    private void queryNearbyUsers(boolean more) {
        // query 20 nearest users' locations
        if (lastKnownLocation != null) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereNear(KEY_LOCATION, new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
            query.setLimit(20);
            query.findInBackground((nearUsers, e) -> {
                if (e == null) {
                    // add list of results to adapter
                    if (!more) {
                        adapter.clear();
                    }
                    adapter.addAll(nearUsers);
                    for (int i = 0; i < nearUsers.size(); i++) {
                        if (!nearUsers.get(i).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                            // add pin for other users
                            LatLng loc = new LatLng(nearUsers.get(i).getParseGeoPoint(KEY_LOCATION).getLatitude(), nearUsers.get(i).getParseGeoPoint(KEY_LOCATION).getLongitude());
                            map.addMarker(new MarkerOptions().position(loc).title(nearUsers.get(i).getUsername()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        }
                    }
                } else {
                    Log.e(TAG, "Issue with getting nearby users", e);
                }
            });
            ParseQuery.clearAllCachedResults();
            // signal refresh has finished
            swipeContainer.setRefreshing(false);
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                    saveCurrentUserLocation();
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void centerMapOnUserLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            LatLng currentLoc = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                            // creating a marker in the map showing the current user location
                            map.addMarker(new MarkerOptions().position(currentLoc).title(ParseUser.getCurrentUser().getUsername()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            // zoom the map to the currentUserLocation
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, DEFAULT_ZOOM));
                            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, DEFAULT_ZOOM));
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void saveCurrentUserLocation() {
        // requesting permission to get user's location
        if(ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_LOCATION);
        }
        else {
            // checking if the location is null
            if (lastKnownLocation != null) {
                // if it isn't, save it to Back4App Dashboard
                ParseGeoPoint currentUserLocation = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    currentUser.put(KEY_LOCATION, currentUserLocation);
                    currentUser.saveInBackground();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        this.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        centerMapOnUserLocation();
        queryNearbyUsers(false);
    }
}