package com.skronawi.dlr_school_lab.navigation_english.tools;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.skronawi.dlr_school_lab.navigation_english.R;
import com.skronawi.dlr_school_lab.navigation_english.services.OrientationService;

public class CompassFragment extends Fragment {

    private ImageView mPointer;
    private float mCurrentDegree = 0f;

    private BroadcastReceiver degreesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                float azimuth = bundle.getFloat(OrientationService.AZIMUTH);
                rotate(-azimuth);
            }
        }
    };

    private void rotate(float azimuth) {

        RotateAnimation ra = new RotateAnimation(
                mCurrentDegree,
                azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        ra.setDuration(200);
        ra.setFillAfter(true);
        mPointer.startAnimation(ra);
        mCurrentDegree = azimuth;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_compass, container, false);
        mPointer = (ImageView) rootView.findViewById(R.id.pointer);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(degreesReceiver, new IntentFilter(OrientationService.NEW_ORIENTATION));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(degreesReceiver);
    }
}