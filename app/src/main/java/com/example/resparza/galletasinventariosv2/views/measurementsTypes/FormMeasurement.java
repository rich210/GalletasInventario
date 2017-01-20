package com.example.resparza.galletasinventariosv2.views.measurementsTypes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.adapters.SpinnerMeasureTypesAdapter;
import com.example.resparza.galletasinventariosv2.dbadapters.MeasureTypeDBAdapter;
import com.example.resparza.galletasinventariosv2.models.MeasureType;

import java.sql.SQLException;
import java.util.List;

public class FormMeasurement extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "FormMeasurement";

    private EditText txtMeasureTypeId;
    private EditText txtMeasureTypeName;
    private EditText txtMeasureSymbol;
    private CheckBox cbMeasureBase;
    private EditText txtQuantityEquivalent;
    private Spinner spinnerMeasureType;
    private Button btnAddMeasureType;
    private SpinnerMeasureTypesAdapter sAdapter;
    private MeasureType measureType;
    private MeasureTypeDBAdapter mAdapter;
    private boolean isUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_measurement);
        Intent intent = getIntent();
        initView();
        String tittle = "Agrega medida";

        //Adding Measurement types to the spinner
        mAdapter = new MeasureTypeDBAdapter(getApplicationContext());
        List<MeasureType> measureTypeList = mAdapter.getAllMeasuresBase();
        if (measureTypeList != null) {
            sAdapter = new SpinnerMeasureTypesAdapter(this, measureTypeList);
            spinnerMeasureType.setAdapter(sAdapter);
        }

        //Get if the form is for an update or to add a new measurement
        isUpdate = intent.getExtras().getBoolean(MainMeasurements.IS_UPDATE);
        if(isUpdate){
            long id = intent.getExtras().getLong(MainMeasurements.EXTRA_SELECTED_MEASURE_ID);
            if (id != 0) {
                loadMeasureType(id);
                tittle = "Actualizar medida";
            }
        }
        setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void initView(){
        this.txtMeasureTypeId = (EditText)findViewById(R.id.eTMeasureTypeId);
        this.txtMeasureTypeName = (EditText)findViewById(R.id.eTMeasureTypeName);
        this.txtMeasureSymbol = (EditText)findViewById(R.id.eTMeasureTypeSymbol);
        this.txtQuantityEquivalent = (EditText)findViewById(R.id.eTQuantityEquivalency);
        this.btnAddMeasureType = (Button) findViewById(R.id.addMeasureTypeButton);
        this.spinnerMeasureType = (Spinner) findViewById(R.id.spinnerMeasureType);
        this.btnAddMeasureType.setOnClickListener(this);
        this.cbMeasureBase = (CheckBox) findViewById(R.id.cbMeasureBase);
        this.cbMeasureBase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    spinnerMeasureType.setVisibility(View.GONE);
                    txtQuantityEquivalent.setVisibility(View.GONE);
                }else {
                    spinnerMeasureType.setVisibility(View.VISIBLE);
                    txtQuantityEquivalent.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        MeasureType selectedMeasure;
        //MeasureType measureType;
        Editable measureTypeName = txtMeasureTypeName.getText();
        Editable measureTypeId = txtMeasureTypeId.getText();
        Editable measureSymbol = txtMeasureSymbol.getText();
        Editable quantityEquivalency = txtQuantityEquivalent.getText();
        switch (v.getId()) {
            case R.id.addMeasureTypeButton:
                if (!isUpdate){
                    if (!TextUtils.isEmpty(measureTypeName)) {
                        // add the company to database
                        measureType = new MeasureType();
                        measureType.setMeasureTypeName(measureTypeName.toString());
                        measureType.setMeasureSymbol(measureSymbol.toString());
                        measureType.setMeasureBase(cbMeasureBase.isChecked());
                        if (!cbMeasureBase.isChecked()) {
                            measureType.setQuantityEquivalency(Integer.valueOf(quantityEquivalency.toString()));
                            selectedMeasure = (MeasureType) spinnerMeasureType.getSelectedItem();
                            if(selectedMeasure.getMeasureTypeId() == 0){
                                Toast.makeText(this, "Seleccione un tipo de medida", Toast.LENGTH_LONG).show();
                                break;
                            }
                            measureType.setMeasureEquivalencyId(selectedMeasure.getMeasureTypeId());
                        }
                        measureType.setMeasureTypeId(mAdapter.insertItem(measureType));
                        Log.d(TAG, measureType.getInfo());
                        if (measureType.getMeasureTypeId()>0){
                            Log.d(TAG, "added type of measure : " + measureType.getMeasureTypeName() + " " + measureType.getMeasureTypeId());
                            setResult(RESULT_OK);
                            finish();

                        }else {
                            Log.d(TAG, "Error inserting type of measure");
                        }
                    }
                    else {
                        Toast.makeText(this, R.string.emptyFields, Toast.LENGTH_LONG).show();
                    }
                }else {
                    if (!TextUtils.isEmpty(measureTypeName) && !TextUtils.isEmpty(measureTypeId)) {
                        // add the company to database
                        measureType.setMeasureTypeName(measureTypeName.toString());
                        measureType.setMeasureSymbol(measureSymbol.toString());
                        measureType.setMeasureBase(cbMeasureBase.isChecked());
                        if (!cbMeasureBase.isChecked()) {
                            measureType.setQuantityEquivalency(Integer.valueOf(quantityEquivalency.toString()));
                            selectedMeasure = (MeasureType) spinnerMeasureType.getSelectedItem();
                            measureType.setMeasureEquivalencyId(selectedMeasure.getMeasureTypeId());
                        }
                        Log.d(TAG, measureType.getInfo());
                        if (mAdapter.updateItem(measureType)) {
                            Log.d(TAG, "Updated type of measure : " + measureType.getMeasureTypeName() + " " + measureType.getMeasureTypeId());
                            setResult(RESULT_OK);
                            finish();

                        } else {
                            Log.d(TAG, "Error updating type of measure");
                        }
                    } else {
                        Toast.makeText(this, R.string.emptyFields, Toast.LENGTH_LONG).show();
                    }
                }

                break;
           default:
                break;
        }
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

    private void loadMeasureType(long id) {
        //MeasureType measureType;
        try {
            measureType = mAdapter.getItemById(id);
            Log.d(TAG, measureType.getInfo());

            this.txtMeasureTypeId.setText(String.valueOf(measureType.getMeasureTypeId()));
            this.txtMeasureTypeName.setText(measureType.getMeasureTypeName());
            this.txtMeasureSymbol.setText(measureType.getMeasureSymbol());
            if (!measureType.isMeasureBase()){
                this.cbMeasureBase.setChecked(measureType.isMeasureBase());
                this.txtQuantityEquivalent.setText(String.valueOf(measureType.getQuantityEquivalency()));
                this.spinnerMeasureType.setSelection(sAdapter.getPositionById(measureType.getMeasureEquivalencyId()));
            }else{
                this.cbMeasureBase.setChecked(measureType.isMeasureBase());
            }
        } catch (SQLException e) {
            Log.d(TAG, "Error trying to get the type of measure by id");
        }


    }
}
