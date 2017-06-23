package com.example.resparza.galletasinventariosv2.views.miscellaneous;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.example.resparza.galletasinventariosv2.MainActivity;
import com.example.resparza.galletasinventariosv2.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by resparza on 07/06/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    private final String TAG = "SettingsFragment";
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
        String[] l_projection = new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.Calendars.OWNER_ACCOUNT};
        Uri l_calendars = CalendarContract.Calendars.CONTENT_URI;
        String selection = "(("+ CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = "+ CalendarContract.Calendars.ACCOUNT_NAME+"))";
        String[] selectionArgs = new String[] {"com.google"};
        Cursor c = getActivity().getContentResolver().query(l_calendars, l_projection, selection, selectionArgs, null);	//all calendars
        //Cursor l_managedCursor = this.managedQuery(l_calendars, l_projection, "selected=1", null, null);   //active calendars
        if (c.moveToFirst()) {
            calendars = new MyCalendar[c.getCount()];
            int l_cnt = 0;
            do {
                calendars[l_cnt] = cursorToCalendar(c);
                ++l_cnt;
            } while (c.moveToNext());
        }
    }

    public void setListPreference(){
        CharSequence[] entries = new CharSequence[calendars.length];
        CharSequence[] entryValues = new CharSequence[calendars.length];
        int i = 0;
        //List<CharSequence> entries = new ArrayList<CharSequence>();
        //List<CharSequence> entryValues = new ArrayList<CharSequence>();

        for (MyCalendar calendar: calendars) {
            Log.d(TAG, "setListPreference: "+calendar.toString());
            entries[i] = calendar.getName();
            entryValues[i] = calendar.getId();
            i++;
        }
        listPreference.setEntries(entries);
        listPreference.setEntryValues(entryValues);
        listPreference.setDefaultValue(entryValues[0]);
    }

    private MyCalendar cursorToCalendar (Cursor c){
        MyCalendar calendar = new MyCalendar();
        calendar.setId(c.getString(c.getColumnIndex(CalendarContract.Calendars._ID)));
        calendar.setName(c.getString(c.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)));
        calendar.setAccountType(c.getString(c.getColumnIndex(CalendarContract.Calendars.ACCOUNT_TYPE)));
        calendar.setAccountOwner(c.getString(c.getColumnIndex(CalendarContract.Calendars.OWNER_ACCOUNT)));

        return calendar;
    }
}

