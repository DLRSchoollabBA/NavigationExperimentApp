package com.skronawi.dlr_school_lab.navigation_english.tasks;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skronawi.dlr_school_lab.navigation_english.R;


public class TaskTrilateration1Fragment extends AbstractTaskTrilateration {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View taskView = inflater.inflate(R.layout.fragment_station_trilateration1, container, false);

        taskView.findViewById(R.id.buttonNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskTrilateration2Fragment nextFragment = new TaskTrilateration2Fragment();
                nextFragment.setTitle(title);
                nextFragment.setTaskId(taskId);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, nextFragment).addToBackStack(null).commit();
            }
        });

        trilaterationCanvasView = (TrilaterationCanvasView) taskView.findViewById(R.id.canvas);
        trilaterationCanvasView.enableTouchCircles(false);
        trilaterationCanvasView.enableTouchTarget(false);
        trilaterationCanvasView.renderRadii(false);
        trilaterationCanvasView.renderCircles(false);
        trilaterationCanvasView.renderTarget(false);

        return taskView;
    }

    @Override
    protected void storeState() {
        //
    }

    @Override
    protected void restoreState(View view) {
        restorePoints();
    }

}
