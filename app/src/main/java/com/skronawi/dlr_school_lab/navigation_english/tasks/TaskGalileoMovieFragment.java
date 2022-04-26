package com.skronawi.dlr_school_lab.navigation_english.tasks;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.skronawi.dlr_school_lab.navigation_english.MainActivity;
import com.skronawi.dlr_school_lab.navigation_english.R;


public class TaskGalileoMovieFragment extends AbstractTask {

    private static final String TMP_RESULT_KEY = "task.galileo.tmp-result";

    private boolean played;

    private VideoView myVideoView;
    //    private ProgressDialog progressDialog;
    private MediaController mediaControls;
    private Button okButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View taskView = inflater.inflate(R.layout.fragment_station_galileo_movie, container, false);

        okButton = (Button) taskView.findViewById(R.id.buttonOk);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                taskManager.setAnswer(taskId, "OK");
                taskManager.nextTask();
                ((MainActivity) getActivity()).onCurrentTask();
            }
        });

        //set the media controller buttons
        if (mediaControls == null) {
            mediaControls = new MediaController(getActivity());
        }

        //initialize the VideoView
        myVideoView = (VideoView) taskView.findViewById(R.id.galileo_video);

//        // create a progress bar while the video file is loading
//        progressDialog = new ProgressDialog(getActivity());
//        // set a title for the progress bar
//        progressDialog.setTitle("Galileo");
//        // set a message for the progress bar
//        progressDialog.setMessage("Lade...");
//        //set the progress bar not cancelable on users' touch
//        progressDialog.setCancelable(false);
//        // show the progress bar
//        progressDialog.show();

        try {
            //set the media controller in the VideoView
            myVideoView.setMediaController(mediaControls);

            //set the uri of the video to be played
            myVideoView.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName()
                    + "/" + R.raw.galileo_video));

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        myVideoView.requestFocus();
        //we also set an setOnPreparedListener in order to know when the video file is ready for playback
        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {
                // close the progress bar and play the video
//                progressDialog.dismiss();
                myVideoView.pause();
            }
        });
        myVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                getView().findViewById(R.id.preview).setVisibility(View.INVISIBLE);
                return false;
            }
        });
        myVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (!taskManager.isTaskSolved(taskId)) {
                    okButton.setEnabled(true);
                    played = true;
                }
            }
        });

        return taskView;
    }


    @Override
    protected void storeState() {
        preferencesManager.setString(TMP_RESULT_KEY, String.valueOf(played));
    }

    @Override
    protected void restoreState(View view) {
        String playedString = preferencesManager.getString(TMP_RESULT_KEY);
        played = Boolean.parseBoolean(playedString);
        if (played && !taskManager.isTaskSolved(taskId)){
            okButton.setEnabled(true);
        }
    }
}
