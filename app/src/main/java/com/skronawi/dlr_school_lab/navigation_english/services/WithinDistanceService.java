package com.skronawi.dlr_school_lab.navigation_english.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.skronawi.dlr_school_lab.navigation_english.Constants;

public class WithinDistanceService extends Service {

    public static final String NEW_WITHIN_DISTANCE = Constants.PREFS_PREFIX + ".services.reached";
    public static final String LAT = Constants.PREFS_PREFIX + ".services.distance.lat";
    public static final String LON = Constants.PREFS_PREFIX + ".services.distance.lon";

    private float targetLat;
    private float targetLon;
    private boolean notified;

    private BroadcastReceiver coordsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (notified) {
                return;
            }

            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                float currentLat = bundle.getFloat(CoordinatesService.COORDS_LAT);
                float currentLon = bundle.getFloat(CoordinatesService.COORDS_LON);

                long now = System.currentTimeMillis();

                Location currentLocation = new Location("");
                currentLocation.setLatitude(currentLat);
                currentLocation.setLongitude(currentLon);
                currentLocation.setTime(now);
                currentLocation.setAltitude(0);

                Location targetLocation = new Location("");
                targetLocation.setLatitude(targetLat);
                targetLocation.setLongitude(targetLon);
                targetLocation.setTime(now);
                targetLocation.setAltitude(0);

                float distanceBetween = currentLocation.distanceTo(targetLocation);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WithinDistanceService.this);
                int coordAccuracyMeters = Integer.parseInt(preferences.getString("setting_coord_accuracy", "15"));

                if (distanceBetween < coordAccuracyMeters) {
                    notified = true;
                    Intent distanceIntent = new Intent(NEW_WITHIN_DISTANCE);
                    sendBroadcast(distanceIntent);
                }
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle extras = intent.getExtras();
        targetLat = extras.getFloat(LAT);
        targetLon = extras.getFloat(LON);

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(coordsReceiver, new IntentFilter(CoordinatesService.NEW_COORDS));
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(coordsReceiver);
        super.onDestroy();
    }
}
