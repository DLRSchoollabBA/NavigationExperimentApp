package com.skronawi.dlr_school_lab.navigation_english.tasks;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.skronawi.dlr_school_lab.navigation_english.MainActivity;
import com.skronawi.dlr_school_lab.navigation_english.R;
import com.skronawi.dlr_school_lab.navigation_english.services.WithinDistanceService;
import com.skronawi.dlr_school_lab.navigation_english.tools.CompassFragment;

public class TaskNavByMapFragment extends AbstractTask {

    private static final String TMP_REACHED = "task.mapnav.tmp.reached";
    private boolean reached;

    private Button okButton;

    BroadcastReceiver distanceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reached = true;
            getView().findViewById(R.id.buttonOk).setEnabled(true);
            getActivity().stopService(new Intent(getActivity(), WithinDistanceService.class));
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View taskView = inflater.inflate(R.layout.fragment_station_map_nav, container, false);

        CompassFragment compassFragment = new CompassFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.compass_fragment_container, compassFragment).commit();

        okButton = (Button) taskView.findViewById(R.id.buttonOk);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                taskManager.setAnswer(taskId, "OK");
                taskManager.nextTask();
                ((MainActivity) getActivity()).onCurrentTask();
            }
        });

        return taskView;
    }

    @Override
    protected void storeState() {
        try {
            getActivity().unregisterReceiver(distanceReceiver);
        } catch (Exception e) {
            //do nothing
        }
        getActivity().stopService(new Intent(getActivity(), WithinDistanceService.class));
        preferencesManager.setString(TMP_REACHED, String.valueOf(reached));
    }

    @Override
    protected void restoreState(View view) {
        View button = getView().findViewById(R.id.buttonOk);
        if (taskManager.isTaskSolved(taskId)) {
            button.setEnabled(false);

        } else {
            getActivity().registerReceiver(distanceReceiver, new IntentFilter(WithinDistanceService.NEW_WITHIN_DISTANCE));
            String reachedString = preferencesManager.getString(TMP_REACHED);
            if (!TextUtils.isEmpty(reachedString)) {
                reached = Boolean.parseBoolean(reachedString);
                button.setEnabled(reached && !taskManager.isTaskSolved(taskId));
            }
            if (!preferencesManager.useRealCoords()) {
                button.setEnabled(true);
            } else {
                if (!reached) {
                    Intent intent = new Intent(getActivity(), WithinDistanceService.class);
                    intent.putExtra(WithinDistanceService.LAT, 52.4288667f);
                    intent.putExtra(WithinDistanceService.LON, 13.5306900f);
                    getActivity().startService(intent);
                }
            }
        }
    }
}
