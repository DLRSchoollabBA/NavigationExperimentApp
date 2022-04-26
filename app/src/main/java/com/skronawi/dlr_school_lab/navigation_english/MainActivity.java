package com.skronawi.dlr_school_lab.navigation_english;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.skronawi.dlr_school_lab.navigation_english.pages.Answers;
import com.skronawi.dlr_school_lab.navigation_english.pages.Impressum;
import com.skronawi.dlr_school_lab.navigation_english.pages.Preferences;
import com.skronawi.dlr_school_lab.navigation_english.pages.WelcomeFragment;
import com.skronawi.dlr_school_lab.navigation_english.tasks.TaskManager;

import java.util.List;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    private TaskManager taskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        taskManager = new TaskManager(this);

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.option_impressum) {
            startActivity(new Intent(this, Impressum.class));
        } else if (id == R.id.option_answers) {
            startActivity(new Intent(this, Answers.class));
        } else if (id == R.id.option_settings) {
            startActivity(new Intent(this, Preferences.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, boolean isFirstView) {
        Fragment fragment = getFragment(position);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction().replace(R.id.container, fragment);
        if (!isFirstView) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public String[] itemTitles() {
        TaskManager taskManager = new TaskManager(this);
        List<String> strings = taskManager.getTaskTitles();
        String[] titles = new String[strings.size() + 1];
        titles[0] = getString(R.string.title_overview);
        for (int i = 0; i < strings.size(); i++) {
            titles[i + 1] = strings.get(i);
        }
        return titles;
    }

    @Override
    public boolean isItemActive(int position) {
        if (position == 0) {
            return true;
        } else {
            int taskId = position - 1;
            return taskManager.isTaskSolved(taskId) || taskId == taskManager.getCurrentTaskId();
        }
    }

    @Override
    public void onCurrentTask() {

        //the tasks start at item-index 1 (after the overview)
        int position = taskManager.getCurrentTaskId() + 1;

        Fragment fragment = getFragment(position);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();

        mNavigationDrawerFragment.setItemChecked(position);
    }

    private Fragment getFragment(int position) {
        if (position == 0) {
            return new WelcomeFragment();
        } else {
            return taskManager.getTaskForId(position - 1);
        }
    }

    public void onSectionAttached(String title, int itemId) {
        mTitle = title;
        restoreActionBar();
        if (itemId >= -1) {
            mNavigationDrawerFragment.setItemChecked(itemId + 1);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public void onTask(int taskId) {
        int position = taskId + 1;
        Fragment fragment = getFragment(position);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
        mNavigationDrawerFragment.setItemChecked(position);
    }
}
