package com.skronawi.dlr_school_lab.navigation_english.tasks;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.skronawi.dlr_school_lab.navigation_english.R;
import com.skronawi.dlr_school_lab.navigation_english.util.PreferencesManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    private static final String ID_KEY = "task.id";
    private static final String ANSWER_KEY = "task.answer";

    private final Context context;
    private final PreferencesManager preferencesManager;

    public TaskManager(Context context) {
        this.context = context;
        preferencesManager = new PreferencesManager(context);
    }

    public int getCurrentTaskId() {
        return preferencesManager.getInt(ID_KEY);
    }

    private void setCurrentTaskId(int id) {
        preferencesManager.setInt(ID_KEY, id);
    }

    public int nextTask() {
        int currentTaskId = getCurrentTaskId();
        int numberOfTasks = getTaskTitles().size();
        if (currentTaskId < numberOfTasks - 1) {
            currentTaskId++;
            setCurrentTaskId(currentTaskId);
        }
        return currentTaskId;
    }

    public Fragment getTaskForId(int id) {

        String title = getTaskTitles().get(id);
        AbstractTask task = null;
        switch (id) {
            case 0:
                task = new TaskMapNavsToYearFragment();
                break;
            case 1:
                task = new TaskTerrestrialNavFragment();
                break;
            case 2:
                task = new TaskAlexandriaFragment();
                break;
            case 3:
                task = new TaskCoupledNavFragment();
                break;
            case 4:
                task = new TaskPortolanFragment();
                break;
            case 5:
                task = new TaskNavByMapFragment();
                break;
            case 6:
                task = new TaskSextantFragment();
                break;
            case 7:
                task = new TaskDegreeLonLatFragment();
                break;
            case 8:
                task = new TaskWaypointNavFragment();
                break;
            case 9:
                task = new TaskGalileoMovieFragment();
                break;
            case 10:
                task = new TaskEstimateHeightFragment();
                break;
            case 11:
                task = new TaskTrilateration1Fragment();
                break;
            case 12:
                task = new TaskMapNavsToYearRecapFragment();
                break;
            case 13:
                task = new TaskEndFragment();
                break;
        }
        if (task == null) {
            throw new IllegalArgumentException("no task for id: " + id);
        }
        task.setTaskId(id);
        task.setTitle(title);
        return task;
    }

    public void setAnswer(int id, String answer) {
        preferencesManager.setString(ANSWER_KEY + "." + id, answer);
    }

    public Map<Integer, String> getAnswers() {
        Map<Integer, String> idsToAnswers = new HashMap<>();
        int numberOfTasks = getTaskTitles().size();
        for (int i = 0; i < numberOfTasks; i++) {
            idsToAnswers.put(i, preferencesManager.getString(ANSWER_KEY + "." + i));
        }
        return idsToAnswers;
    }

    public List<String> getTaskTitles() {
        Resources resources = context.getResources();
        return Arrays.asList(
                resources.getString(R.string.navs_to_year_title),
                resources.getString(R.string.terrestrial_nav_title),
                resources.getString(R.string.alexandria_title),
                resources.getString(R.string.coupled_nav_title),
                resources.getString(R.string.portolan_title),
                resources.getString(R.string.nav_by_map_title),
                resources.getString(R.string.sextant_title),
                resources.getString(R.string.degree_lon_lat_title),
                resources.getString(R.string.waypoint_nav_title),
                resources.getString(R.string.galileo_movie_title),
                resources.getString(R.string.height_estimation_title),
                resources.getString(R.string.trilateration_title),
                resources.getString(R.string.navs_to_year_recap_title),
                resources.getString(R.string.end_title)
        );
    }

    public boolean isTaskSolved(int id) {
        int currentStationId = getCurrentTaskId();
        return id < currentStationId;
    }

    public Answer getMetersForStation(int stationNumber) {
        Answer answerObject = null;
        String answer;
        Resources resources = context.getResources();
        switch (stationNumber) {
            case 1:
                answer = getAnswers().get(2);
                if (answer.equals(resources.getString(R.string.alexandria_radio2years))) {
                    answerObject = new Answer(Accuracy.BAD, 105);
                } else if (answer.equals(resources.getString(R.string.alexandria_radio5years)) || answer.equals(resources.getString(R.string.alexandria_radio50years))) {
                    answerObject = new Answer(Accuracy.MEDIUM, 104);
                } else {
                    answerObject = new Answer(Accuracy.GOOD, 100);
                }
                break;

            case 2:
                float correct = 178;
                answer = getAnswers().get(4);
                float degree = Float.valueOf(answer);
                if (Math.abs(correct - degree) <= 5) {
                    answerObject = new Answer(Accuracy.GOOD, 55);
                } else if (Math.abs(correct - degree) <= 10) {
                    answerObject = new Answer(Accuracy.MEDIUM, 58);
                } else {
                    answerObject = new Answer(Accuracy.BAD, 63);
                }
                break;

            case 3:
                answer = getAnswers().get(6);
                if (answer.equals(resources.getString(R.string.sextant_28degrees))) {
                    answerObject = new Answer(Accuracy.BAD, 124);
                } else if (answer.equals(resources.getString(R.string.sextant_19degrees)) || answer.equals(resources.getString(R.string.sextant_25degrees))) {
                    answerObject = new Answer(Accuracy.MEDIUM, 126);
                } else {
                    answerObject = new Answer(Accuracy.GOOD, 120);
                }
                break;

            case 4:
                int numberOfCorrectAnswers = Integer.parseInt(getAnswers().get(10));
                if (numberOfCorrectAnswers == 6) {
                    answerObject = new Answer(Accuracy.GOOD, 169);
                } else if (numberOfCorrectAnswers >= 4) {
                    answerObject = new Answer(Accuracy.MEDIUM, 174);
                } else {
                    answerObject = new Answer(Accuracy.BAD, 176);
                }
                break;
        }
        return answerObject;
    }

    public enum Accuracy {
        GOOD(Color.GREEN),
        MEDIUM(Color.rgb(255, 165, 0)), //orange
        BAD(Color.RED);

        public final int color;

        Accuracy(int color) {
            this.color = color;
        }
    }

    public class Answer {
        public final int meters;
        public final Accuracy accuracy;

        public Answer(Accuracy accuracy, int meters) {
            this.accuracy = accuracy;
            this.meters = meters;
        }
    }
}
