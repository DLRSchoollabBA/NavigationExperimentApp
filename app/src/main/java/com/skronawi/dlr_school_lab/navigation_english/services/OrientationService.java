package com.skronawi.dlr_school_lab.navigation_english.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.skronawi.dlr_school_lab.navigation_english.Constants;

public class OrientationService extends Service implements SensorEventListener {

    public static final String NEW_ORIENTATION = Constants.PREFS_PREFIX + ".services.orientation";
    public static final String AZIMUTH = Constants.PREFS_PREFIX + ".services.orientation.azimuth";
    public static final String PITCH = Constants.PREFS_PREFIX + ".services.orientation.pitch";
    public static final String ROLL = Constants.PREFS_PREFIX + ".services.orientation.roll";

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private float lat;
    private float lon;
    private boolean currentLocationAvailable;

    private BroadcastReceiver coordsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                lat = bundle.getFloat(CoordinatesService.COORDS_LAT);
                lon = bundle.getFloat(CoordinatesService.COORDS_LON);

                currentLocationAvailable = true;
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
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        registerReceiver(coordsReceiver, new IntentFilter(CoordinatesService.NEW_COORDS));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this, mSensor);
        unregisterReceiver(coordsReceiver);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float azimuth = sensorEvent.values[0];
        float pitch = sensorEvent.values[1];
        float roll = sensorEvent.values[2];

        //do not use, compass deviates very much, if you change location -> north is not north any more :-)
//        if (currentLocationAvailable){
//            GeomagneticField geoField = new GeomagneticField(lat, lon, 0, System.currentTimeMillis());
//            azimuth += geoField.getDeclination();
//        }

        Intent intent = new Intent(NEW_ORIENTATION);
        intent.putExtra(AZIMUTH, azimuth + 90); // +90 because of landscape
        intent.putExtra(PITCH, pitch);
        intent.putExtra(ROLL, roll);
        sendBroadcast(intent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //nothing to do here
    }
}
