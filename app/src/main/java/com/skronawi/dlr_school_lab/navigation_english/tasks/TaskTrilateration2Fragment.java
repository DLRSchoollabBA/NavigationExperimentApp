package com.skronawi.dlr_school_lab.navigation_english.tasks;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skronawi.dlr_school_lab.navigation_english.R;


public class TaskTrilateration2Fragment extends AbstractTaskTrilateration {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View taskView = inflater.inflate(R.layout.fragment_station_trilateration2, container, false);

        taskView.findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskTrilateration1Fragment nextFragment = new TaskTrilateration1Fragment();
                nextFragment.setTitle(title);
                nextFragment.setTaskId(taskId);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, nextFragment).addToBackStack(null).commit();
            }
        });

        taskView.findViewById(R.id.buttonNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskTrilateration3Fragment nextFragment = new TaskTrilateration3Fragment();
                nextFragment.setTitle(title);
                nextFragment.setTaskId(taskId);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, nextFragment).addToBackStack(null).commit();
            }
        });

        trilaterationCanvasView = (TrilaterationCanvasView) taskView.findViewById(R.id.canvas);

        trilaterationCanvasView.enableTouchCircles(!taskManager.isTaskSolved(taskId));
        trilaterationCanvasView.enableTouchTarget(false);
        trilaterationCanvasView.renderRadii(true);
        trilaterationCanvasView.renderCircles(true);
        trilaterationCanvasView.renderTarget(false);

        setAnswerMeters(taskView);

        return taskView;
    }

    @Override
    protected void storeState() {
        storePoints();
    }

    @Override
    protected void restoreState(View view) {
        restorePoints();
    }
}
