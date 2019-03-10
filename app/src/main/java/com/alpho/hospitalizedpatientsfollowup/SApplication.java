package com.alpho.hospitalizedpatientsfollowup;

import android.app.Application;
import android.content.Intent;
import android.location.Location;

public class SApplication<p> extends Application {
    
    private final String TAG = SApplication.class.getSimpleName();
    public static Location LOCATION = null;

    public void onCreate() {
        super.onCreate();
        Intent location = new Intent(getApplicationContext(), LocationService.class);
        startService(location);
    }

}