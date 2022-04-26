package com.skronawi.dlr_school_lab.navigation_english.tasks;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.skronawi.dlr_school_lab.navigation_english.MainActivity;
import com.skronawi.dlr_school_lab.navigation_english.R;
import com.skronawi.dlr_school_lab.navigation_english.services.OrientationService;
import com.skronawi.dlr_school_lab.navigation_english.tools.CompassFragment;

public class TaskPortolanFragment extends AbstractTask {

    private static final String TMP_RESULT_KEY = "task.portolan.tmp-result";

    private float currentAzimuth;
    private float setAzimuth;
    private Button okButton;
    private boolean isAzimuthSet;
    private Button northedButton;

    private BroadcastReceiver degreesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                currentAzimuth = bundle.getFloat(OrientationService.AZIMUTH);
                Log.i(TaskPortolanFragment.class.getSimpleName(), "currentAzimuth: " + currentAzimuth);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View taskView = inflater.inflate(R.layout.fragment_station_portolan, container, false);

        CompassFragment compassFragment = new CompassFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.compass_fragment_container, compassFragment).commit();

        northedButton = (Button) taskView.findViewById(R.id.buttonNorthed);
        northedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAzimuth = currentAzimuth;
                isAzimuthSet = true;
                okButton.setEnabled(true);
            }
        });

        okButton = (Button) taskView.findViewById(R.id.buttonOk);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                taskManager.setAnswer(taskId, String.valueOf(setAzimuth));
                taskManager.nextTask();
                ((MainActivity) getActivity()).onCurrentTask();
            }
        });

        return taskView;
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

    @Override
    protected void storeState() {
        if (isAzimuthSet) {
            preferencesManager.setString(TMP_RESULT_KEY, String.valueOf(setAzimuth));
        }
    }

    @Override
    protected void restoreState(View view) {
        if (taskManager.isTaskSolved(taskId)) {
            okButton.setEnabled(false);
            northedButton.setEnabled(false);
        } else {
            String azimuthString = preferencesManager.getString(TMP_RESULT_KEY);
            if (!TextUtils.isEmpty(azimuthString)) {
                setAzimuth = Float.valueOf(azimuthString);
                isAzimuthSet = true;
                okButton.setEnabled(true);
            }
        }
    }
}
