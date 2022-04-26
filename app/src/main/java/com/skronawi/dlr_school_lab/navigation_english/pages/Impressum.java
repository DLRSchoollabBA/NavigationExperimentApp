package com.skronawi.dlr_school_lab.navigation_english.pages;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.skronawi.dlr_school_lab.navigation_english.R;


public class Impressum extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_impressum);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActionBar().setTitle(getResources().getString(R.string.title_imprint));
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
    }
}
