package com.skronawi.dlr_school_lab.navigation_english.pages;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.skronawi.dlr_school_lab.navigation_english.R;
import com.skronawi.dlr_school_lab.navigation_english.tasks.TaskManager;


public class Answers extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_answers);
        Resources res = getResources();

        //given answers, always
        TaskManager taskManager = new TaskManager(this);
        String alexandriaAnswer = taskManager.getAnswers().get(2);
        String portolanAnswer = taskManager.getAnswers().get(4);
        String sextantAnswer = taskManager.getAnswers().get(6);
        String heightEstimateAnswer = taskManager.getAnswers().get(10);
        if (!TextUtils.isEmpty(alexandriaAnswer)) {
            ((TextView) findViewById(R.id.answer_alexandria_given)).setText(
                    alexandriaAnswer);
        }
        if (!TextUtils.isEmpty(portolanAnswer)) {
            Float portolanFloat = Float.valueOf(portolanAnswer);
            int diversion = (int) Math.abs(178 - portolanFloat);
            ((TextView) findViewById(R.id.answer_portolan_given)).setText(
                    diversion + res.getString(R.string.portolan_div));
        }
        if (!TextUtils.isEmpty(sextantAnswer)) {
            ((TextView) findViewById(R.id.answer_sextant_given)).setText(
                    sextantAnswer);
        }
        if (!TextUtils.isEmpty(heightEstimateAnswer)) {
            ((TextView) findViewById(R.id.answer_heightestimate_given)).setText(
                    heightEstimateAnswer + res.getString(R.string.heightestimate_correct));
        }

        //after height estimate has been solved, show meters
        if (taskManager.isTaskSolved(10)) {
            ((TextView) findViewById(R.id.answer_alexandria_given_distance)).setText(
                    taskManager.getMetersForStation(1).meters + " m");
            ((TextView) findViewById(R.id.answer_portolan_given_distance)).setText(
                    taskManager.getMetersForStation(2).meters + " m");
            ((TextView) findViewById(R.id.answer_sextant_given_distance)).setText(
                    taskManager.getMetersForStation(3).meters + " m");
            ((TextView) findViewById(R.id.answer_heightestimate_given_distance)).setText(
                    taskManager.getMetersForStation(4).meters + " m");
        }

        //after trilateration has been solved, show correct meters and answers
        if (taskManager.isTaskSolved(11)) {

            ((TextView) findViewById(R.id.answer_alexandria_correct)).setText(
                    getResources().getString(R.string.alexandria_radio20years));
            ((TextView) findViewById(R.id.answer_portolan_correct)).setText(
                    "178°");
            ((TextView) findViewById(R.id.answer_sextant_correct)).setText(
                    getResources().getString(R.string.sextant_22degrees));
            ((TextView) findViewById(R.id.answer_heightestimate_correct)).setText(
                    "6" + res.getString(R.string.heightestimate_correct));

            ((TextView) findViewById(R.id.answer_alexandria_correct_distance)).setText(
                    res.getString(R.string.distance_alexandria));
            ((TextView) findViewById(R.id.answer_portolan_correct_distance)).setText(
                    res.getString(R.string.distance_portolan));
            ((TextView) findViewById(R.id.answer_sextant_correct_distance)).setText(
                    res.getString(R.string.distance_sextant));
            ((TextView) findViewById(R.id.answer_heightestimate_correct_distance)).setText(
                    res.getString(R.string.distance_height));
        }

        //after trilateration has been solved, show correct meters and answers
        if (taskManager.isTaskSolved(11)) {
            ((TextView) findViewById(R.id.answer_alexandria_given)).setTextColor(
                    taskManager.getMetersForStation(1).accuracy.color);
            ((TextView) findViewById(R.id.answer_portolan_given)).setTextColor(
                    taskManager.getMetersForStation(2).accuracy.color);
            ((TextView) findViewById(R.id.answer_sextant_given)).setTextColor(
                    taskManager.getMetersForStation(3).accuracy.color);
            ((TextView) findViewById(R.id.answer_heightestimate_given)).setTextColor(
                    taskManager.getMetersForStation(4).accuracy.color);

            ((TextView) findViewById(R.id.answer_alexandria_given_distance)).setTextColor(
                    taskManager.getMetersForStation(1).accuracy.color);
            ((TextView) findViewById(R.id.answer_portolan_given_distance)).setTextColor(
                    taskManager.getMetersForStation(2).accuracy.color);
            ((TextView) findViewById(R.id.answer_sextant_given_distance)).setTextColor(
                    taskManager.getMetersForStation(3).accuracy.color);
            ((TextView) findViewById(R.id.answer_heightestimate_given_distance)).setTextColor(
                    taskManager.getMetersForStation(4).accuracy.color);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActionBar().setTitle(getResources().getString(R.string.title_answers));
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
    }
}
