package com.skronawi.dlr_school_lab.navigation_english.tasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.skronawi.dlr_school_lab.navigation_english.MainActivity;
import com.skronawi.dlr_school_lab.navigation_english.R;
import com.skronawi.dlr_school_lab.navigation_english.services.WithinDistanceService;


public class TaskTerrestrialNavFragment extends AbstractTask {

    private static final String TMP_REACHED = "task.terrnav.tmp.reached";

    private boolean reached;

    BroadcastReceiver distanceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reached = true;
            getView().findViewById(R.id.buttonOk).setEnabled(true);
            getActivity().stopService(new Intent(getActivity(), WithinDistanceService.class));
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View taskView = inflater.inflate(R.layout.fragment_station_terrestrial_nav, container, false);

        taskView.findViewById(R.id.buttonOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String answer = "OK";
                taskManager.setAnswer(taskId, answer);
                taskManager.nextTask();
                ((MainActivity) getActivity()).onCurrentTask();
            }
        });

        TableLayout logbookTable = (TableLayout) taskView.findViewById(R.id.logbook_nav_table);
        populate(logbookTable);

        return taskView;
    }

    private void populate(TableLayout logbookTable) {

        String[] logbookTimes = getResources().getStringArray(R.array.logbook_times);
        String[] logbookStations = getResources().getStringArray(R.array.logbook_stations);

        for (int i = 0; i < logbookTimes.length; i++) {

            TextView time = new TextView(getActivity());
            time.setText(logbookTimes[i]);
            time.setTextAppearance(getActivity(), R.style.TextAppearance_AppCompat_Large);
            time.setPadding(22, 10, 10, 10);
            time.setWidth(150);
            time.setHeight(80);

            TextView station = new TextView(getActivity());
            station.setText(logbookStations[i]);
            station.setTextAppearance(getActivity(), R.style.TextAppearance_AppCompat_Large);
            station.setPadding(16, 10, 10, 10);
            station.setWidth(1000);
            station.setHeight(80);

            TableRow tableRow = new TableRow(getActivity());
            tableRow.addView(time);
            tableRow.addView(station);

            logbookTable.addView(tableRow);
        }
    }

    @Override
    protected void storeState() {
        try {
            getActivity().unregisterReceiver(distanceReceiver);
        } catch (Exception e) {
            //do nothing
        }
        getActivity().stopService(new Intent(getActivity(), WithinDistanceService.class));
        preferencesManager.setString(TMP_REACHED, String.valueOf(reached));
    }

    @Override
    protected void restoreState(View view) {

        View button = getView().findViewById(R.id.buttonOk);
        if (taskManager.isTaskSolved(taskId)) {
            button.setEnabled(false);

        } else {
            getActivity().registerReceiver(distanceReceiver, new IntentFilter(
                    WithinDistanceService.NEW_WITHIN_DISTANCE));
            String reachedString = preferencesManager.getString(TMP_REACHED);
            if (!TextUtils.isEmpty(reachedString)) {
                reached = Boolean.parseBoolean(reachedString);
                button.setEnabled(reached && !taskManager.isTaskSolved(taskId));
            }
            if (!preferencesManager.useRealCoords()) {
                button.setEnabled(true);
            } else {
                if (!reached) {
                    Intent intent = new Intent(getActivity(), WithinDistanceService.class);
                    intent.putExtra(WithinDistanceService.LAT, 52.427351f);
                    intent.putExtra(WithinDistanceService.LON, 13.528944f);
                    getActivity().startService(intent);
                }
            }
        }
    }
}
