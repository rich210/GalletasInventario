package com.example.resparza.galletasinventariosv2.views.miscellaneous;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.resparza.galletasinventariosv2.MainActivity;
import com.example.resparza.galletasinventariosv2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by resparza on 07/06/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    private MyCalendar calendars[];
    ListPreference listPreference;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        initFloatingActionButtons();
        getCalendars();
        addPreferencesFromResource(R.xml.preferences);
        listPreference = (ListPreference)findPreference("pref_calendar_account");
        setListPreference();
    }

    private void initFloatingActionButtons(){
        MainActivity.closeDeleteFAB();
        MainActivity.closeAddFAB();
        MainActivity.closeEditFAB();
    }

    private void getCalendars() {
        String[] l_projection = new String[]{"_id", "displayName"};
        Uri l_calendars = CalendarContract.Calendars.CONTENT_URI;;
        Cursor l_managedCursor = getActivity().getContentResolver().query(l_calendars, l_projection, null, null, null);	//all calendars
        //Cursor l_managedCursor = this.managedQuery(l_calendars, l_projection, "selected=1", null, null);   //active calendars
        if (l_managedCursor.moveToFirst()) {
            calendars = new MyCalendar[l_managedCursor.getCount()];
            String l_calName;
            String l_calId;
            int l_cnt = 0;
            int l_nameCol = l_managedCursor.getColumnIndex(l_projection[1]);
            int l_idCol = l_managedCursor.getColumnIndex(l_projection[0]);
            do {
                l_calName = l_managedCursor.getString(l_nameCol);
                l_calId = l_managedCursor.getString(l_idCol);
                calendars[l_cnt] = new MyCalendar(l_calName, l_calId);
                ++l_cnt;
            } while (l_managedCursor.moveToNext());
        }
    }

    public void setListPreference(){
        CharSequence[] entries = new CharSequence[calendars.length];
        CharSequence[] entryValues = new CharSequence[calendars.length];
        int i = 0;
        //List<CharSequence> entries = new ArrayList<CharSequence>();
        //List<CharSequence> entryValues = new ArrayList<CharSequence>();

        for (MyCalendar calendar: calendars) {
            entries[i] = calendar.getId();
            entryValues[i] = calendar.getName();
            i++;
        }
        listPreference.setEntries(entries);
        listPreference.setEntryValues(entryValues);
    }
}

