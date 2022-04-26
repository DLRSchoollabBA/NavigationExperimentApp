package com.skronawi.dlr_school_lab.navigation_english.tools;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.skronawi.dlr_school_lab.navigation_english.R;
import com.skronawi.dlr_school_lab.navigation_english.services.CoordinatesService;
import com.skronawi.dlr_school_lab.navigation_english.services.OrientationService;
import com.skronawi.dlr_school_lab.navigation_english.services.WithinDistanceService;
import com.skronawi.dlr_school_lab.navigation_english.util.DegreeFormatter;
import com.skronawi.dlr_school_lab.navigation_english.util.PreferencesManager;

public class PointToCoordsFragment extends Fragment {

    private ImageView mPointer;
    private float mCurrentDegree = 0f;

    private float lat;
    private float lon;

    private float latTarget;
    private float lonTarget;

    private boolean targetLocationAvailable;
    private boolean currentLocationAvailable;

    private CoordsReachedReceiver coordsReachedReceiver;

    private BroadcastReceiver degreesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (!targetLocationAvailable || !currentLocationAvailable) {
                return;
            }

            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                //http://stackoverflow.com/questions/5479753/using-orientation-sensor-to-point-towards-a-specific-location
                float azimuth = bundle.getFloat(OrientationService.AZIMUTH);
                long now = System.currentTimeMillis();

                Location currentLocation = new Location("");
                currentLocation.setLatitude(lat);
                currentLocation.setLongitude(lon);
                currentLocation.setTime(now);
                currentLocation.setAltitude(0);

                Location targetLocation = new Location("");
                targetLocation.setLatitude(latTarget);
                targetLocation.setLongitude(lonTarget);
                targetLocation.setTime(now);
                targetLocation.setAltitude(0);

                float bearing = currentLocation.bearingTo(targetLocation); // it's already in degrees
                float direction = azimuth - bearing; // We pass a negative argument because canvas rotations are anti-clockwise.
                rotate(-direction);

                float distanceBetween = currentLocation.distanceTo(targetLocation);
                ((TextView) getView().findViewById(R.id.dist_target_input)).setText(
                        String.valueOf((int) distanceBetween) + " m");
            }
        }
    };

    private BroadcastReceiver coordsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                lat = bundle.getFloat(CoordinatesService.COORDS_LAT);
                lon = bundle.getFloat(CoordinatesService.COORDS_LON);

                ((TextView) getView().findViewById(R.id.lat_input)).setText(DegreeFormatter.toLat(lat));
                ((TextView) getView().findViewById(R.id.lon_input)).setText(DegreeFormatter.toLon(lon));

                currentLocationAvailable = true;
            }
        }
    };

    BroadcastReceiver distanceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            coordsReachedReceiver.coordsReached();
            getActivity().stopService(new Intent(getActivity(), WithinDistanceService.class));
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_point_to_coords, container, false);

        mPointer = (ImageView) rootView.findViewById(R.id.pointer);

        return rootView;
    }

    public void setCoords(float lat, float lon) {
        latTarget = lat;
        lonTarget = lon;
        ((TextView) getView().findViewById(R.id.lat_target_input)).setText(DegreeFormatter.toLat(lat));
        ((TextView) getView().findViewById(R.id.lon_target_input)).setText(DegreeFormatter.toLon(lon));
        targetLocationAvailable = true;

        if (!new PreferencesManager(getActivity()).useRealCoords()) {
            coordsReachedReceiver.coordsReached();
        } else {
            Intent intent = new Intent(getActivity(), WithinDistanceService.class);
            intent.putExtra(WithinDistanceService.LAT, latTarget);
            intent.putExtra(WithinDistanceService.LON, lonTarget);
            getActivity().startService(intent);
        }
    }

    private void rotate(float degree) {

        RotateAnimation ra = new RotateAnimation(
                mCurrentDegree,
                degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        ra.setDuration(200);
        ra.setFillAfter(true);
        mPointer.startAnimation(ra);
        mCurrentDegree = degree;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(degreesReceiver, new IntentFilter(OrientationService.NEW_ORIENTATION));
        getActivity().registerReceiver(coordsReceiver, new IntentFilter(CoordinatesService.NEW_COORDS));
        getActivity().registerReceiver(distanceReceiver, new IntentFilter(WithinDistanceService.NEW_WITHIN_DISTANCE));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(degreesReceiver);
        getActivity().unregisterReceiver(coordsReceiver);
        getActivity().unregisterReceiver(distanceReceiver);
        getActivity().stopService(new Intent(getActivity(), WithinDistanceService.class));
    }

    public void setCoordsReachedReceiver(CoordsReachedReceiver coordsReachedReceiver) {
        this.coordsReachedReceiver = coordsReachedReceiver;
    }

    public interface CoordsReachedReceiver {
        void coordsReached();
    }
}