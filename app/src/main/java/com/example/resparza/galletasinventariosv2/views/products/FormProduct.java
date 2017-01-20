package com.example.resparza.galletasinventariosv2.views.products;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.adapters.SpinnerMeasureTypesAdapter;
import com.example.resparza.galletasinventariosv2.dbadapters.MeasureTypeDBAdapter;
import com.example.resparza.galletasinventariosv2.dbadapters.ProductDBAdapter;
import com.example.resparza.galletasinventariosv2.models.MeasureType;
import com.example.resparza.galletasinventariosv2.models.Product;

import java.sql.SQLException;
import java.util.List;

public class FormProduct extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "FormProduct";

    private EditText txtProductName;
    private TextView txtCostPerUnit;
    private EditText txtTotal;
    private EditText txtQuantity;
    private Spinner spinnerMeasureType;
    private EditText txtProductMin;
    private TextView txtProductMinSymbol;
    private SpinnerMeasureTypesAdapter mAdapter;
    private Button btnSaveProduct;


    private MeasureType selectedMeasure;
    private Product product;
    private ProductDBAdapter productDBAdapter;
    private MeasureTypeDBAdapter measureTypeDBAdapter;
    private boolean isUpdate;

    private float total = 0;
    private float quantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_product);
        Intent intent = getIntent();
        initView();
        String tittle = "Agrega producto";
        this.productDBAdapter = new ProductDBAdapter(this);
        this.measureTypeDBAdapter = new MeasureTypeDBAdapter(this);
        List<MeasureType> measureTypeList = measureTypeDBAdapter.getAllItems();
        if (measureTypeList != null) {
            mAdapter = new SpinnerMeasureTypesAdapter(this, measureTypeList);
            spinnerMeasureType.setAdapter(mAdapter);
        }
        isUpdate = intent.getExtras().getBoolean(MainProducts.IS_UPDATE);
        if (isUpdate) {
            long id = intent.getExtras().getLong(MainProducts.EXTRA_SELECTED_PRODUCT_ID);
            if (id != 0) {
                loadData(id);
                tittle = "Actualizar producto";
            }
            txtProductName.setInputType(InputType.TYPE_NULL);
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

    private void initView() {
        this.txtProductName = (EditText) findViewById(R.id.eTProductName);
        this.txtCostPerUnit = (TextView) findViewById(R.id.eTCostPerUnit);
        this.txtTotal = (EditText) findViewById(R.id.eTTotal);
        this.txtQuantity = (EditText) findViewById(R.id.eTQuantity);
        this.txtProductMin = (EditText)findViewById(R.id.eTProductMin);
        this.txtProductMinSymbol = (TextView) findViewById(R.id.tvProductMinSymbol);
        this.spinnerMeasureType = (Spinner) findViewById(R.id.spinnerMeasureType);
        this.spinnerMeasureType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMeasure = (MeasureType) parent.getItemAtPosition(position);
                if(selectedMeasure.getMeasureTypeId()>0){
                    if(selectedMeasure.isMeasureBase()){
                        txtProductMinSymbol.setText(selectedMeasure.getMeasureSymbol());
                    }else{

                        try {
                            MeasureType newMeasureType = measureTypeDBAdapter.getItemById(selectedMeasure.getMeasureEquivalencyId());
                            txtProductMinSymbol.setText(newMeasureType.getMeasureSymbol());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        this.btnSaveProduct = (Button) findViewById(R.id.saveProductButton);
        this.btnSaveProduct.setOnClickListener(this);
        this.txtTotal.addTextChangedListener(new CustomTextWatcher(txtTotal, getApplicationContext()));
        this.txtQuantity.addTextChangedListener(new CustomTextWatcher(txtQuantity, getApplicationContext()));

    }

    private void loadData(long id) {
        try {
            productDBAdapter.open();
            this.product = productDBAdapter.getItemById(id);
            Log.d(TAG,product.getInfo());
            this.txtProductName.setText(product.getProductName());
            this.txtProductMin.setText(String.valueOf(product.getProductMin()));
            MeasureType newMeasureType = measureTypeDBAdapter.getItemById(product.getMeasureType().getMeasureEquivalencyId());
            this.txtProductMinSymbol.setText(newMeasureType.getMeasureSymbol());
            this.txtProductMin.setInputType(InputType.TYPE_NULL);
            productDBAdapter.close();
        } catch (SQLException e) {
            Log.d(TAG, "Error trying to get product by id");
        }
    }

    @Override
    public void onClick(View v) {
        float newQuantity;
        float oldQuantity;
        float newCostPerUnit;
        float oldCostPerUnit;
        float newTotalCost;
        int equivalency;
        MeasureType newMeasureType;

        Editable productName = txtProductName.getText();
        String costPerUnit = txtCostPerUnit.getText().toString();
        Editable total = txtTotal.getText();
        Editable quantity = txtQuantity.getText();
        Editable productMin = txtProductMin.getText();

        selectedMeasure = (MeasureType) spinnerMeasureType.getSelectedItem();
        Product newProduct = new Product();
        switch (v.getId()) {
            case R.id.saveProductButton:
                if (!isUpdate) {
                    if (selectedMeasure.getMeasureTypeId() == 0 || selectedMeasure == null) {
                        Toast.makeText(this, "Seleccione un tipo de medida", Toast.LENGTH_LONG).show();
                        break;
                    }
                    if (!TextUtils.isEmpty(productName) && !costPerUnit.isEmpty() && !TextUtils.isEmpty(total)
                            && !TextUtils.isEmpty(quantity) && selectedMeasure != null) {
                        // add the company to database
                        newProduct.setProductName(productName.toString());
                        try {
                            if (!selectedMeasure.isMeasureBase()) {
                                newMeasureType = measureTypeDBAdapter.getItemById(selectedMeasure.getMeasureEquivalencyId());
                                equivalency = selectedMeasure.getQuantityEquivalency();
                            } else {
                                newMeasureType = selectedMeasure;
                                equivalency = 1;
                            }
                            oldQuantity = Float.valueOf(quantity.toString());
                            newQuantity = oldQuantity * equivalency;
                            oldCostPerUnit = Float.valueOf(total.toString());
                            newCostPerUnit = oldCostPerUnit / newQuantity;
                            newProduct.setMeasureType(newMeasureType);
                            newProduct.setQuantity(newQuantity);
                            newProduct.setProductMin(Float.valueOf(productMin.toString()));
                            newProduct.setCostPerUnit(newCostPerUnit);
                            newProduct.setTotalCost(Float.valueOf(total.toString()));
                        } catch (SQLException e) {
                            Log.e(TAG, "SQLException trying to get item " + e.getMessage());
                        }

                        try {
                            productDBAdapter.open();
                            newProduct.setProductId(productDBAdapter.insertItem(newProduct));
                            productDBAdapter.close();
                        } catch (SQLException e) {
                            Log.e(TAG, "SQLException on oppening database " + e.getMessage());
                        }
                        if (newProduct.getProductId() > 0) {
                            Log.d(TAG, "added type of measure : " + newProduct.getProductId() + " " + newProduct.getProductName());
                            setResult(RESULT_OK);
                            finish();

                        } else {
                            Log.d(TAG, "Error inserting type of measure");
                        }
                    } else {
                        Toast.makeText(this, R.string.emptyFields, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "ProductName :" + productName.toString() + "Quantity: " + quantity.toString() + "Total: " + total.toString() +
                                " Cost Per unit" + costPerUnit);
                    }
                } else {
                    if (selectedMeasure.getMeasureTypeId() == 0 || selectedMeasure == null) {
                        Toast.makeText(this, "Seleccione un tipo de medida", Toast.LENGTH_LONG).show();
                        break;
                    }
                    if (!TextUtils.isEmpty(productName) && !costPerUnit.isEmpty() && !TextUtils.isEmpty(total)
                            && !TextUtils.isEmpty(quantity) && product.getProductId() != 0 && selectedMeasure != null) {
                        // add the company to database
                        newProduct.setProductName(productName.toString());
                        try {
                            if (!selectedMeasure.isMeasureBase()) {
                                newMeasureType = measureTypeDBAdapter.getItemById(selectedMeasure.getMeasureEquivalencyId());
                                equivalency = selectedMeasure.getQuantityEquivalency();
                            } else {
                                newMeasureType = selectedMeasure;
                                equivalency = 1;
                            }
                            Log.d(TAG, newMeasureType.getInfo() + " \n" + selectedMeasure.getInfo() + "\n" + product.getMeasureType().getInfo());
                            if (newMeasureType.getMeasureTypeId() != product.getMeasureType().getMeasureTypeId()) {
                                Toast.makeText(this, "El tipo de medida no es equivalente a la medida base", Toast.LENGTH_LONG).show();
                                break;
                            }
                            oldQuantity = Float.valueOf(quantity.toString());
                            newQuantity = (oldQuantity * equivalency);// Continued working, on how it should be added the total and the unit cost
                            oldCostPerUnit = Float.valueOf(total.toString());
                            newCostPerUnit = oldCostPerUnit / newQuantity;
                            newProduct.setMeasureType(newMeasureType);
                            newProduct.setQuantity(newQuantity);
                            newProduct.setCostPerUnit(newCostPerUnit);
                            newProduct.setProductMin(Float.valueOf(productMin.toString()));
                            newProduct.setTotalCost(Float.valueOf(total.toString()));
                            newProduct.setProductId(product.getProductId());
                        } catch (SQLException e) {
                            Log.e(TAG, "SQLException trying to get item " + e.getMessage());
                        }
                        try {
                            productDBAdapter.open();
                            if (productDBAdapter.updateItem(newProduct)) {
                                Log.d(TAG, "Updated product : " + newProduct.getProductId() + " " + newProduct.getProductName());
                                setResult(RESULT_OK);
                                finish();

                            } else {
                                Log.d(TAG, "Error updating product");
                            }
                            productDBAdapter.close();
                        } catch (SQLException e) {
                            Log.e(TAG, "SQLException on oppening database " + e.getMessage());
                        }
                    } else {
                        Toast.makeText(this, R.string.emptyFields, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "ProductName :" + productName.toString() + "Quantity: " + quantity.toString() + "Total: " + total.toString() +
                                " Cost Per unit" + costPerUnit + " ProductId: " + product.getProductId());
                    }
                }
                break;
            default:
                break;
        }
    }

    class CustomTextWatcher implements TextWatcher {

        EditText m;
        Context c;

        float costPerUnit = 0;

        private TextView twCostPerUnit;

        public CustomTextWatcher(EditText e, Context context) {
            m = e;
            c = context;
            twCostPerUnit = (TextView) findViewById(R.id.eTCostPerUnit);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d(TAG, "onTextChanged");
        }

        @Override
        public void afterTextChanged(Editable s) {
            String field = m.getText().toString();
            Log.d(TAG, "AfterTextChanged");
            Log.d(TAG, "OBject:" + m.getId());

            switch (m.getId()) {
                case R.id.eTTotal:
                    total = (field != null && !field.isEmpty()) ? Float.valueOf(field) : 0;
                    Log.d(TAG, "Enter case ettotal");

                    break;
                case R.id.eTQuantity:
                    quantity = (field != null && !field.isEmpty()) ? Float.valueOf(field) : 0;
                    Log.d(TAG, "Enter case eTQuantity");

                    break;
            }
            Log.d(TAG, "Quantity: " + quantity + " & Total: " + total);
            if (total != 0 && quantity != 0) {
                costPerUnit = total / quantity;
                twCostPerUnit.setText(String.valueOf(costPerUnit));
                Log.d(TAG, "Cost por unidad: " + costPerUnit);
            }

        }
    }

    private void setQuantityAndMeasureType(MeasureType measureType, float quantity) {
        try {
            measureTypeDBAdapter = new MeasureTypeDBAdapter(this);
            MeasureType newMeasureType = measureTypeDBAdapter.getItemById(measureType.getMeasureEquivalencyId());
            int equivalency = measureType.getQuantityEquivalency();
            float newQuantity = quantity * equivalency;
            this.product.setMeasureType(newMeasureType);
            this.product.setQuantity(newQuantity);
        } catch (SQLException e) {
            Log.e(TAG, "SQLException trying to get item " + e.getMessage());
        }
    }
}
