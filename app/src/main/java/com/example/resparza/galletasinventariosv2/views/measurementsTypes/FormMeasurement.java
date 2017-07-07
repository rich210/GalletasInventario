package com.example.resparza.galletasinventariosv2.views.measurementsTypes;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.resparza.galletasinventariosv2.MainActivity;
import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.adapters.SpinnerMeasureTypesAdapter;
import com.example.resparza.galletasinventariosv2.dbadapters.MeasureTypeDBAdapter;
import com.example.resparza.galletasinventariosv2.models.MeasureType;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

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
    //private MeasureType measureType;
    private MeasureTypeDBAdapter mAdapter;
    private boolean isUpdate;
    public static final String APPNAME = "com.example.resparza.galletasinventario2";
    public static SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_measurement);
        Intent intent = getIntent();
        initView();
        String tittle = getResources().getString(R.string.measurementFormAddTitle);
        prefs = getSharedPreferences(APPNAME, MODE_PRIVATE);

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
                tittle = getResources().getString(R.string.measurementFormUpdateTitle);
            }
        }
        setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefs.getBoolean("formMeasurementFirstRun", true)) {
            new ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setTarget(new ViewTarget(txtMeasureTypeName))
                    //.hideOnTouchOutside()
                    .setContentTitle("Nombre de la medida")
                    .setContentText("Nombre completo que llevara la unidad de la medida ej. Pieza, Litro, Cuchara pequeña etc..")
                    .build();
            //prefs.edit().putBoolean("mainfirstrun", false).commit();
        }
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
        MeasureType measureType;
        Boolean isError = false;
        Editable measureTypeId = txtMeasureTypeId.getText();
        Editable measureTypeName = txtMeasureTypeName.getText();
        Editable measureSymbol = txtMeasureSymbol.getText();
        Editable quantityEquivalency = txtQuantityEquivalent.getText();
        MeasureType selectedMeasure = (MeasureType)spinnerMeasureType.getSelectedItem();
        if(isValid(measureTypeId,measureTypeName,measureSymbol,quantityEquivalency,selectedMeasure)){
            measureType = new MeasureType();
            measureType.setMeasureTypeName(measureTypeName.toString());
            measureType.setMeasureSymbol(measureSymbol.toString());
            measureType.setMeasureBase(cbMeasureBase.isChecked());
            if (!cbMeasureBase.isChecked()) {
                measureType.setQuantityEquivalency(Integer.valueOf(quantityEquivalency.toString()));
                measureType.setMeasureEquivalencyId(selectedMeasure.getMeasureTypeId());
            }
            if (isUpdate){
                measureType.setMeasureTypeId(Long.getLong( measureTypeId.toString()));
                isError = !mAdapter.updateItem(measureType);
                if(isError){
                    Log.e(TAG,getResources().getString(R.string.updateErrorText)+ measureType.toString());
                    Toast.makeText(this, R.string.updateErrorText, Toast.LENGTH_LONG).show();
                }
            }else{
                measureType.setMeasureTypeId(mAdapter.insertItem(measureType));
                if (measureType.getMeasureTypeId()<1){
                    isError = true;
                    Log.e(TAG,getResources().getString(R.string.insertErrorText)+ measureType.toString());
                    Toast.makeText(this, R.string.insertErrorText, Toast.LENGTH_LONG).show();
                }
            }
            if(!isError){
                setResult(RESULT_OK);
                finish();
            }
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
        MeasureType measureType;
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

    private boolean isValid(Editable measureTypeId, Editable measureTypeName, Editable measureTypeSymbol, Editable quantityEquivalent, MeasureType selectedMeasure){
        Boolean isValid = true;
        if (measureTypeName.toString().isEmpty()){
            this.txtMeasureTypeName.setError(txtMeasureTypeName.getHint()+" "+ getResources().getString(R.string.requiredErrorText));
            isValid = false;
        }
        if (measureTypeSymbol.toString().isEmpty()){
            this.txtMeasureSymbol.setError(txtMeasureSymbol.getHint()+" "+ getResources().getString(R.string.requiredErrorText));
            isValid = false;
        }
        if(!this.cbMeasureBase.isChecked() && quantityEquivalent.toString().isEmpty()){
            if (quantityEquivalent.toString().isEmpty()){
                this.txtQuantityEquivalent.setError(txtQuantityEquivalent.getHint()+ getResources().getString(R.string.requiredErrorText));
                isValid = false;
            }
            if (selectedMeasure.getMeasureTypeId()== 0){
                Toast.makeText(this, getResources().getString(R.string.measurementFormSpinnerErrorText), Toast.LENGTH_LONG).show();
                isValid = false;
            }
        }
        if(isUpdate && measureTypeId.toString().isEmpty()){
            Log.e(TAG,getResources().getString(R.string.updateErrorText));
            //Toast.makeText(this, getResources().getString(R.string.updateErrorText), Toast.LENGTH_LONG).show();
        }
        return isValid;
    }
}
