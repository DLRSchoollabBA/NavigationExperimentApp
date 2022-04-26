package com.skronawi.dlr_school_lab.navigation_english.tools;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.skronawi.dlr_school_lab.navigation_english.R;

public class StopWatchFragment extends Fragment {

    private TextView textTimer;
    private Button playPauseButton;
    private Button resetButton;

    private long startTime = 0L;
    private Handler myHandler = new Handler();
    long stopwatchTimeMillis = 0L;
    long pausedMillisTime = 0L;

    enum StopWatchState {
        STOPPED,
        PAUSED,
        RUNNING;
    }

    private StopWatchState stopWatchState;


    private Runnable updateTimerMethod = new Runnable() {

        public void run() {
            stopwatchTimeMillis = SystemClock.uptimeMillis() - startTime;
            int seconds = (int) (stopwatchTimeMillis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (stopwatchTimeMillis % 1000);
            setTextTimer(seconds, minutes, milliseconds);
            myHandler.postDelayed(this, 0);
        }
    };

    private void setTextTimer(int seconds, int minutes, int milliseconds) {
        textTimer.setText(
                String.format("%02d", minutes) + ":"
                        + String.format("%02d", seconds) + ":"
                        + String.format("%03d", milliseconds));
    }

    private void resetTime() {
        stopwatchTimeMillis = 0L;
        pausedMillisTime = 0L;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        stopWatchState = StopWatchState.STOPPED;

        View rootView = inflater.inflate(R.layout.fragment_stopwatch, container, false);

        textTimer = (TextView) rootView.findViewById(R.id.textTimer);

        playPauseButton = (Button) rootView.findViewById(R.id.play_pause_button);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if (stopWatchState == StopWatchState.STOPPED) {
                    //start
                    startTime = SystemClock.uptimeMillis();
                    pausedMillisTime = 0L;
                    stopWatchState = StopWatchState.RUNNING;
                    playPauseButton.setText(R.string.stopwatch_button_pause);
                    myHandler.postDelayed(updateTimerMethod, 0);

                } else if (stopWatchState == StopWatchState.PAUSED) {
                    //go on
                    myHandler.removeCallbacks(updateTimerMethod);
                    startTime = SystemClock.uptimeMillis() - (pausedMillisTime - startTime);
                    resetTime();
                    stopWatchState = StopWatchState.RUNNING;
                    playPauseButton.setText(R.string.stopwatch_button_pause);
                    myHandler.postDelayed(updateTimerMethod, 0);

                } else if (stopWatchState == StopWatchState.RUNNING) {
                    //pause
                    myHandler.removeCallbacks(updateTimerMethod);
                    pausedMillisTime = SystemClock.uptimeMillis();
                    stopWatchState = StopWatchState.PAUSED;
                    playPauseButton.setText(R.string.stopwatch_button_goon);
                }
            }
        });

        resetButton = (Button) rootView.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                resetTime();
                setTextTimer(0, 0, 0);
                myHandler.removeCallbacks(updateTimerMethod);
                playPauseButton.setText(R.string.stopwatch_button_start);
                stopWatchState = StopWatchState.STOPPED;
            }
        });
        return rootView;
    }
}