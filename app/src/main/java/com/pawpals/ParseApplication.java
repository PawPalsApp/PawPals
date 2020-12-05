package com.pawpals;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.pawpals.models.Pet;

public class ParseApplication extends Application {

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        // Register parse models
        ParseObject.registerSubclass(Pet.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("boqY3MK4uHoiSNCUOFWkBuSMiywoLkeJ2cW5YCAB")
                .clientKey("xH1itYMRmW3wgOJEX0pK51JwdsWLFtzBWrSmss6a")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
