package com.skronawi.dlr_school_lab.navigation_english.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.skronawi.dlr_school_lab.navigation_english.Constants;

/*
see http://www.vogella.com/tutorials/AndroidServices/article.html
 */
public class CoordinatesService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String NEW_COORDS = Constants.PREFS_PREFIX + ".services.coords";
    public static final String COORDS_LAT = Constants.PREFS_PREFIX + ".services.coords.lat";
    public static final String COORDS_LON = Constants.PREFS_PREFIX + ".services.coords.lon";
    public static final String COORDS_ACC = Constants.PREFS_PREFIX + ".services.coords.acc";

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //no binder is needed here, as the service makes broadcasts
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationRequest = new LocationRequest()
                .setInterval(1000 * 3)
                .setFastestInterval(1000 * 1)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //TODO
    }

    @Override
    public void onLocationChanged(Location location) {

        float lat = (float) location.getLatitude();
        float lng = (float) location.getLongitude();
        float acc = location.getAccuracy();

        Intent intent = new Intent(NEW_COORDS);
        intent.putExtra(COORDS_LAT, lat);
        intent.putExtra(COORDS_LON, lng);
        intent.putExtra(COORDS_ACC, acc);
        sendBroadcast(intent);
    }
}
