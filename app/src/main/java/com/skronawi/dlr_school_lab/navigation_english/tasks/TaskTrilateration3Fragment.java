package com.skronawi.dlr_school_lab.navigation_english.tasks;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skronawi.dlr_school_lab.navigation_english.MainActivity;
import com.skronawi.dlr_school_lab.navigation_english.R;
import com.skronawi.dlr_school_lab.navigation_english.util.DegreeFormatter;

public class TaskTrilateration3Fragment extends AbstractTaskTrilateration implements TrilaterationCoordReceiver {

    public static final String TMP_RESULT_KEY = "task.trilateration.tmp-result";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View taskView = inflater.inflate(R.layout.fragment_station_trilateration3, container, false);

        taskView.findViewById(R.id.buttonOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView latInputView = (TextView) taskView.findViewById(R.id.lat_input);
                TextView lonInputView = (TextView) taskView.findViewById(R.id.lon_input);

                float lat = DegreeFormatter.fromLat(latInputView.getText().toString());
                float lon = DegreeFormatter.fromLon(lonInputView.getText().toString());

                //start the navigator app
                openGeoIntent(lat, lon);

                taskManager.setAnswer(taskId, "OK");
                taskManager.nextTask();
                ((MainActivity) getActivity()).onCurrentTask();
            }
        });

        taskView.findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
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
        trilaterationCanvasView.setTrilaterationCoordReceiver(this);

        trilaterationCanvasView.enableTouchCircles(false);
        trilaterationCanvasView.enableTouchTarget(!taskManager.isTaskSolved(taskId));
        trilaterationCanvasView.renderRadii(true);
        trilaterationCanvasView.renderCircles(true);
        trilaterationCanvasView.renderTarget(true);

        setAnswerMeters(taskView);
        visualizeAccuracyOnMeters(taskView);

        trilaterationCanvasView.visualizeAccuracy(1, taskManager.getMetersForStation(1).accuracy);
        trilaterationCanvasView.visualizeAccuracy(2, taskManager.getMetersForStation(2).accuracy);
        trilaterationCanvasView.visualizeAccuracy(3, taskManager.getMetersForStation(3).accuracy);
        trilaterationCanvasView.visualizeAccuracy(4, taskManager.getMetersForStation(4).accuracy);

        return taskView;
    }

    /*
    http://kb.mapfactor.com/kb/Navigator/Intents
    http://stackoverflow.com/questions/6829187/android-explicit-intent-with-target-component
     */
    private void openGeoIntent(float lat, float lon) {
        Uri uri = Uri.parse("geo:" + Float.toString(lat) + "," + Float.toString(lon) + "?q=navigate=yes");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

        //offline google maps mit fussgängernavigation und coordinaten-weg, damit auch richtig navigiert wird
        //https://developers.google.com/maps/documentation/android-api/intents
//        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon + " & mode=w");
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        mapIntent.setPackage("com.google.android.apps.maps");
//        startActivity(mapIntent);
    }

    @Override
    protected void storeState() {

        TextView latInputView = (TextView) getView().findViewById(R.id.lat_input);
        TextView lonInputView = (TextView) getView().findViewById(R.id.lon_input);

        preferencesManager.setString(TMP_RESULT_KEY + ".lat", latInputView.getText().toString());
        preferencesManager.setString(TMP_RESULT_KEY + ".lon", lonInputView.getText().toString());

        storePoints();
    }

    @Override
    protected void restoreState(View view) {

        TextView latInputView = (TextView) getView().findViewById(R.id.lat_input);
        TextView lonInputView = (TextView) getView().findViewById(R.id.lon_input);
        String latString = preferencesManager.getString(TMP_RESULT_KEY + ".lat");
        String lonString = preferencesManager.getString(TMP_RESULT_KEY + ".lon");
        latInputView.setText(latString);
        lonInputView.setText(lonString);

        restorePoints();

        if (!taskManager.isTaskSolved(taskId)) {
            String targetString = preferencesManager.getString(TaskTrilateration3Fragment.TMP_RESULT_KEY + ".target");
            if (!TextUtils.isEmpty(targetString)) {
                getView().findViewById(R.id.buttonOk).setEnabled(true);
            }
        }
    }

    @Override
    public void setLatLon(float lat, float lon) {
        TextView latInputView = (TextView) getView().findViewById(R.id.lat_input);
        TextView lonInputView = (TextView) getView().findViewById(R.id.lon_input);

        latInputView.setText(DegreeFormatter.toLat(lat));
        lonInputView.setText(DegreeFormatter.toLon(lon));

        getView().findViewById(R.id.buttonOk).setEnabled(true);
    }
}
