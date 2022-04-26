package com.skronawi.dlr_school_lab.navigation_english.tasks;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;

import com.skronawi.dlr_school_lab.navigation_english.MainActivity;
import com.skronawi.dlr_school_lab.navigation_english.util.PreferencesManager;

public abstract class AbstractTask extends Fragment {

    protected TaskManager taskManager;
    protected int taskId;
    protected String title;
    protected PreferencesManager preferencesManager;

    public AbstractTask setTitle(String title) {
        this.title = title;
        return this;
    }

    public AbstractTask setTaskId(int taskId) {
        this.taskId = taskId;
        return this;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        preferencesManager = new PreferencesManager(activity);
        taskManager = new TaskManager(activity);
    }

    protected void disableOkButtonIfAnswered(View rootView, int buttonOk) {
        Button okButton = (Button) rootView.findViewById(buttonOk);
        if (taskManager.isTaskSolved(taskId)) {
            okButton.setEnabled(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        storeState();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        activity.onSectionAttached(title, taskId);
        activity.getActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        restoreState(getView());
    }

    protected abstract void storeState();

    protected abstract void restoreState(View view);
}