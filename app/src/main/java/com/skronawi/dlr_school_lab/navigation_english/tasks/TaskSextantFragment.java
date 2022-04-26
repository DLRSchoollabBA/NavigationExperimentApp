package com.skronawi.dlr_school_lab.navigation_english.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.skronawi.dlr_school_lab.navigation_english.MainActivity;
import com.skronawi.dlr_school_lab.navigation_english.R;


public class TaskSextantFragment extends AbstractTask {

    private static final String TMP_RESULT_KEY = "task.sextant.tmp-result";

    private RadioGroup radios;
    private Button okButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View taskView = inflater.inflate(R.layout.fragment_station_sextant, container, false);
        radios = (RadioGroup) taskView.findViewById(R.id.radioAngles);
        okButton = (Button) taskView.findViewById(R.id.buttonOk);

        okButton.setOnClickListener(new View.OnClickListener() {
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
                        okButton.setEnabled(true);
                    }
                });

        return taskView;
    }

    private String mapToAnswer(RadioButton radioButton) {
        if (radioButton.getId() == R.id.radio19degrees) {
            return getResources().getString(R.string.sextant_19degrees);
        } else if (radioButton.getId() == R.id.radio22degrees) {
            return getResources().getString(R.string.sextant_22degrees);
        } else if (radioButton.getId() == R.id.radio25degrees) {
            return getResources().getString(R.string.sextant_25degrees);
        } else {// if (radioButton.getId() == R.id.radio28degrees) {
            return getResources().getString(R.string.sextant_28degrees);
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
                getView().findViewById(R.id.radio19degrees).setEnabled(false);
                getView().findViewById(R.id.radio22degrees).setEnabled(false);
                getView().findViewById(R.id.radio25degrees).setEnabled(false);
                getView().findViewById(R.id.radio28degrees).setEnabled(false);
                okButton.setEnabled(false);
            } else {
                okButton.setEnabled(true);
            }
        }
    }
}
