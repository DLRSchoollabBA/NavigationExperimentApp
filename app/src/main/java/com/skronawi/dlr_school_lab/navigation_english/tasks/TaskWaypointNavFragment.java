package com.skronawi.dlr_school_lab.navigation_english.tasks;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skronawi.dlr_school_lab.navigation_english.MainActivity;
import com.skronawi.dlr_school_lab.navigation_english.R;
import com.skronawi.dlr_school_lab.navigation_english.tools.PointToCoordsFragment;

import java.util.ArrayList;
import java.util.List;

public class TaskWaypointNavFragment extends AbstractTask implements PointToCoordsFragment.CoordsReachedReceiver {

    private static final String TMP_CURRENT_COORD = "task.waypointnav.tmp.current-coord";
    private static final String TMP_CURRENT_COORD_REACHED = "task.waypointnav.tmp.current-coord-reached";

    private PointToCoordsFragment pointToCoordsFragment;
    private List<Pair<Float, Float>> coordinates = new ArrayList<>(); //lat,lon

    private int currentCoord = -1;
    private boolean currentCoordReached = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View taskView = inflater.inflate(R.layout.fragment_station_waypoint_nav, container, false);

        taskView.findViewById(R.id.buttonOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskManager.setAnswer(taskId, "OK");
                taskManager.nextTask();
                ((MainActivity) getActivity()).onCurrentTask();
            }
        });

        pointToCoordsFragment = new PointToCoordsFragment();
        pointToCoordsFragment.setCoordsReachedReceiver(this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.point_to_coords_fragment_container, pointToCoordsFragment).commit();

        coordinates.add(new Pair<>(52.429120f, 13.529350f));
        coordinates.add(new Pair<>(52.428738f, 13.527238f));

        taskView.findViewById(R.id.waypoint_coord_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCoordReached = false;
                currentCoord = 0;
                pointToCoordsFragment.setCoords(coordinates.get(0).first, coordinates.get(0).second);
            }
        });
        taskView.findViewById(R.id.waypoint_coord_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCoordReached = false;
                currentCoord = 1;
                pointToCoordsFragment.setCoords(coordinates.get(1).first, coordinates.get(1).second);
            }
        });

        taskView.findViewById(R.id.waypoint_coord_2).setEnabled(false);

        return taskView;
    }

    @Override
    protected void storeState() {
        preferencesManager.setString(TMP_CURRENT_COORD, String.valueOf(currentCoord));
        preferencesManager.setString(TMP_CURRENT_COORD_REACHED, String.valueOf(currentCoordReached));
    }

    @Override
    protected void restoreState(View view) {

        String coordinateIndexString = preferencesManager.getString(TMP_CURRENT_COORD);
        if (TextUtils.isEmpty(coordinateIndexString)) {
            getView().findViewById(R.id.waypoint_coord_1).setEnabled(true);
            getView().findViewById(R.id.waypoint_coord_2).setEnabled(false);
            getView().findViewById(R.id.buttonOk).setEnabled(false);
            return;
        }
        int coordinateIndex = Integer.parseInt(coordinateIndexString);

        currentCoord = coordinateIndex;
        currentCoordReached = Boolean.parseBoolean(preferencesManager.getString(TMP_CURRENT_COORD_REACHED));

        if (taskManager.isTaskSolved(taskId)) {

            getView().findViewById(R.id.waypoint_coord_1).setEnabled(false);
            getView().findViewById(R.id.waypoint_coord_2).setEnabled(false);
            getView().findViewById(R.id.buttonOk).setEnabled(false);

        } else {
            if (currentCoord == -1) {
                getView().findViewById(R.id.waypoint_coord_1).setEnabled(true);
                getView().findViewById(R.id.waypoint_coord_2).setEnabled(false);
                getView().findViewById(R.id.buttonOk).setEnabled(false);

            } else if (currentCoord == 0) {

                if (currentCoordReached) {
                    getView().findViewById(R.id.waypoint_coord_1).setEnabled(false);
                    getView().findViewById(R.id.waypoint_coord_2).setEnabled(true);
                } else {
                    getView().findViewById(R.id.waypoint_coord_1).setEnabled(true);
                    getView().findViewById(R.id.waypoint_coord_2).setEnabled(false);
                }
                getView().findViewById(R.id.buttonOk).setEnabled(false);

            } else {// if (coordinateIndex == 1) {

                if (currentCoordReached) {
                    getView().findViewById(R.id.waypoint_coord_1).setEnabled(false);
                    getView().findViewById(R.id.waypoint_coord_2).setEnabled(false);
                    getView().findViewById(R.id.buttonOk).setEnabled(true);
                } else {
                    getView().findViewById(R.id.waypoint_coord_1).setEnabled(false);
                    getView().findViewById(R.id.waypoint_coord_2).setEnabled(true);
                    getView().findViewById(R.id.buttonOk).setEnabled(false);
                }
            }
        }
    }

    @Override
    public void coordsReached() {

        currentCoordReached = true;

        if (currentCoord == 0) {
            getView().findViewById(R.id.waypoint_coord_1).setEnabled(false);
            getView().findViewById(R.id.waypoint_coord_2).setEnabled(true);
            getView().findViewById(R.id.buttonOk).setEnabled(false);
        } else if (currentCoord == 1) {
            getView().findViewById(R.id.waypoint_coord_1).setEnabled(false);
            getView().findViewById(R.id.waypoint_coord_2).setEnabled(false);
            getView().findViewById(R.id.buttonOk).setEnabled(true);
        }
    }
}
