package com.skronawi.dlr_school_lab.navigation_english.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skronawi.dlr_school_lab.navigation_english.MainActivity;
import com.skronawi.dlr_school_lab.navigation_english.R;


public class TaskDegreeLonLatFragment extends AbstractTask {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View taskView = inflater.inflate(R.layout.fragment_station_degree_lon_lat, container, false);

        taskView.findViewById(R.id.buttonOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                taskManager.setAnswer(taskId, "OK");
                taskManager.nextTask();
                ((MainActivity) getActivity()).onCurrentTask();
            }
        });

        disableOkButtonIfAnswered(taskView, R.id.buttonOk);

        return taskView;
    }

    @Override
    protected void storeState() {
        //
    }

    @Override
    protected void restoreState(View view) {
        //
    }
}
