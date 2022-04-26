package com.skronawi.dlr_school_lab.navigation_english;

import android.app.Application;
import android.content.Intent;

import com.skronawi.dlr_school_lab.navigation_english.services.CoordinatesService;
import com.skronawi.dlr_school_lab.navigation_english.services.OrientationService;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Intent coordinateServiceIntent = new Intent(this, CoordinatesService.class);
        startService(coordinateServiceIntent);
        Intent degreeServiceIntent = new Intent(this, OrientationService.class);
        startService(degreeServiceIntent);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Intent coordinateServiceIntent = new Intent(this, CoordinatesService.class);
        stopService(coordinateServiceIntent);
        Intent degreeServiceIntent = new Intent(this, OrientationService.class);
        stopService(degreeServiceIntent);
    }
}
