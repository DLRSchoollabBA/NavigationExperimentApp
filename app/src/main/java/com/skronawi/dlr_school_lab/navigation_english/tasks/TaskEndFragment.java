package com.skronawi.dlr_school_lab.navigation_english.tasks;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skronawi.dlr_school_lab.navigation_english.R;


public class TaskEndFragment extends AbstractTask {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_goal, container, false);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
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
