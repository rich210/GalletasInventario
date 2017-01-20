package com.example.resparza.galletasinventariosv2.views.orders;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.resparza.galletasinventariosv2.R;

import java.util.Calendar;

public class FormOrder extends AppCompatActivity {

    public static final String TAG = "FormOrder";
    private CalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_order);
        Intent intent = getIntent();
        String tittle;
        if(intent.getExtras().getBoolean(MainOrders.IS_UPDATE)){
            //long id = intent.getExtras().getLong(MeasureTypeMainActivity.EXTRA_SELECTED_MEASURE_ID);
            //if (id != 0) {
            //loadMeasureType(id);
            tittle = "Actualizar orden";
            //}
        }else {
            tittle = "Agrega orden";
        }
        setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
