package com.skronawi.dlr_school_lab.navigation_english.tasks;

import android.view.View;
import android.widget.TextView;

import com.skronawi.dlr_school_lab.navigation_english.R;


public abstract class AbstractTaskTrilateration extends AbstractTask {

    protected TrilaterationCanvasView trilaterationCanvasView;

    protected void storePoints() {
        TrilaterationCanvasView.Point[] points = trilaterationCanvasView.getPoints();
        preferencesManager.setString(TaskTrilateration3Fragment.TMP_RESULT_KEY + ".point1",
                points[0].toString());
        preferencesManager.setString(TaskTrilateration3Fragment.TMP_RESULT_KEY + ".point2",
                points[1].toString());
        preferencesManager.setString(TaskTrilateration3Fragment.TMP_RESULT_KEY + ".point3",
                points[2].toString());
        preferencesManager.setString(TaskTrilateration3Fragment.TMP_RESULT_KEY + ".point4",
                points[3].toString());
        preferencesManager.setString(TaskTrilateration3Fragment.TMP_RESULT_KEY + ".target",
                points[4] == null ? null : points[4].toString());
    }

    protected void restorePoints() {
        String point1String = preferencesManager.getString(TaskTrilateration3Fragment.TMP_RESULT_KEY + ".point1");
        String point2String = preferencesManager.getString(TaskTrilateration3Fragment.TMP_RESULT_KEY + ".point2");
        String point3String = preferencesManager.getString(TaskTrilateration3Fragment.TMP_RESULT_KEY + ".point3");
        String point4String = preferencesManager.getString(TaskTrilateration3Fragment.TMP_RESULT_KEY + ".point4");
        String targetString = preferencesManager.getString(TaskTrilateration3Fragment.TMP_RESULT_KEY + ".target");
        if (point1String == null) {
            return;
        }
        trilaterationCanvasView.setPoints(new TrilaterationCanvasView.Point[]{
                TrilaterationCanvasView.Point.fromString(point1String),
                TrilaterationCanvasView.Point.fromString(point2String),
                TrilaterationCanvasView.Point.fromString(point3String),
                TrilaterationCanvasView.Point.fromString(point4String),
                targetString == null ? null : TrilaterationCanvasView.Point.fromString(targetString)
        });
    }

    protected void setAnswerMeters(View taskView) {
        ((TextView) taskView.findViewById(R.id.alexandria_meters)).setText(
                String.valueOf(taskManager.getMetersForStation(1).meters) + " m");
        ((TextView) taskView.findViewById(R.id.portolan_meters)).setText(
                String.valueOf(taskManager.getMetersForStation(2).meters) + " m");
        ((TextView) taskView.findViewById(R.id.sextant_meters)).setText(
                String.valueOf(taskManager.getMetersForStation(3).meters) + " m");
        ((TextView) taskView.findViewById(R.id.height_estimate_meters)).setText(
                String.valueOf(taskManager.getMetersForStation(4).meters) + " m");
    }

    protected void visualizeAccuracyOnMeters(View view) {
        ((TextView) view.findViewById(R.id.alexandria_meters)).setTextColor(
                taskManager.getMetersForStation(1).accuracy.color);
        ((TextView) view.findViewById(R.id.portolan_meters)).setTextColor(
                taskManager.getMetersForStation(2).accuracy.color);
        ((TextView) view.findViewById(R.id.sextant_meters)).setTextColor(
                taskManager.getMetersForStation(3).accuracy.color);
        ((TextView) view.findViewById(R.id.height_estimate_meters)).setTextColor(
                taskManager.getMetersForStation(4).accuracy.color);
    }


}
