package com.skronawi.dlr_school_lab.navigation_english;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

//http://stackoverflow.com/questions/27197582/navigation-drawer-selector-disable-not-working
public class EnabledDisabledArrayAdapter extends ArrayAdapter<String> {

    private final NavigationDrawerFragment.NavigationDrawerCallbacks mCallbacks;

    public EnabledDisabledArrayAdapter(Context context, int resource, int textViewResourceId, String[] items,
                                       NavigationDrawerFragment.NavigationDrawerCallbacks mCallbacks) {
        super(context, resource, textViewResourceId, items);
        this.mCallbacks = mCallbacks;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return mCallbacks.isItemActive(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.setEnabled(isEnabled(position));
        return view;
    }
}
