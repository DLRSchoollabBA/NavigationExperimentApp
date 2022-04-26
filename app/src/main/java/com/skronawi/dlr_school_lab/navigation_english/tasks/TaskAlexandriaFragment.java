package com.skronawi.dlr_school_lab.navigation_english.tasks;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.skronawi.dlr_school_lab.navigation_english.MainActivity;
import com.skronawi.dlr_school_lab.navigation_english.R;


public class TaskAlexandriaFragment extends AbstractTask {

    private static final String TMP_RESULT_KEY = "task.alexandria.tmp-result";

    private RadioGroup radios;
    private Button buttonOk;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View taskView = inflater.inflate(R.layout.fragment_station_alexandria, container, false);

        radios = (RadioGroup) taskView.findViewById(R.id.radioAlexandria);
        buttonOk = (Button) taskView.findViewById(R.id.buttonOk);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int checkedRadioButtonId = radios.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) taskView.findViewById(checkedRadioButtonId);
                taskManager.setAnswer(taskId, mapToAnswer(radioButton));
                taskManager.nextTask();
                ((MainActivity) getActivity()).onCurrentTask();
            }
        });

        radios.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        buttonOk.setEnabled(true);
                    }
                });

        return taskView;
    }

    private String mapToAnswer(RadioButton radioButton) {
        if (radioButton.getId() == R.id.radio2years) {
            return getResources().getString(R.string.alexandria_radio2years);
        } else if (radioButton.getId() == R.id.radio5years) {
            return getResources().getString(R.string.alexandria_radio5years);
        } else if (radioButton.getId() == R.id.radio20years) {
            return getResources().getString(R.string.alexandria_radio20years);
        } else {// if (radioButton.getId() == R.id.radio50years) {
            return getResources().getString(R.string.alexandria_radio50years);
        }
    }

    @Override
    protected void storeState() {
        int checkedRadioButtonId = radios.getCheckedRadioButtonId();
        preferencesManager.setInt(TMP_RESULT_KEY, checkedRadioButtonId);
    }

    @Override
    protected void restoreState(View view) {

        int checkedRadioButtonId = preferencesManager.getInt(TMP_RESULT_KEY);

        if (checkedRadioButtonId > 0) { //-1 for no button checked, 0 for nothing stored
            ((RadioButton) view.findViewById(checkedRadioButtonId)).setChecked(true);
            if (taskManager.isTaskSolved(taskId)) {
                getView().findViewById(R.id.radio2years).setEnabled(false);
                getView().findViewById(R.id.radio5years).setEnabled(false);
                getView().findViewById(R.id.radio20years).setEnabled(false);
                getView().findViewById(R.id.radio50years).setEnabled(false);
                buttonOk.setEnabled(false);
            } else {
                buttonOk.setEnabled(true);
            }
        }
    }
}
