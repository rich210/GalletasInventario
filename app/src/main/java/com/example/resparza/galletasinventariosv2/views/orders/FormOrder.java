package com.example.resparza.galletasinventariosv2.views.orders;

import android.content.Context;
import android.content.Intent;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.adapters.SpinnerRecipeAdapter;
import com.example.resparza.galletasinventariosv2.dbadapters.RecipeDBAdapter;
import com.example.resparza.galletasinventariosv2.models.Recipe;

import java.sql.SQLException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FormOrder extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "FormOrder";
    private TextView tvOrderId;
    private EditText etClientName;
    private TextView tvDeliveryDate;
    private DatePicker dpDeliveryDate;
    private LinearLayout llRecipes;
    private ArrayList<LinearLayout> linearLayoutArray;
    /*This should be created by program
    private Spinner spinnerRecipes;
    private CalendarView calendar;
    private EditText etQuantity;
    private TextView tvRecipePortion;
    private TextView tvCostRecipe;
    private TextView tvTotalRecipes;
    private ImageButton ibRemoveRecipe;

    */
    private ImageButton ibAddRecipe;
    private TextView tvTotalCost;
    private EditText etPriceToSell;
    private TextView tvGain;
    private Button btnSave;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_order);
        Intent intent = getIntent();
        String tittle;
        initView();
        if(intent.getExtras().getBoolean(MainOrders.IS_UPDATE)){
            //long id = intent.getExtras().getLong(MeasureTypeMainActivity.EXTRA_SELECTED_MEASURE_ID);
            //if (id != 0) {
            //loadMeasureType(id);
            tittle = "Actualizar orden";

            //}
        }else {
            tittle = "Agrega orden";
            addRecipeForm(null);
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

    /**
     * Initialize all the layout components
     */
    private void initView() {
        this.linearLayoutArray = new ArrayList<LinearLayout>();
        this.tvOrderId = (TextView) findViewById(R.id.txtOrderId);
        this.etClientName = (EditText)findViewById(R.id.etClientName);
        this.tvDeliveryDate = (TextView)findViewById(R.id.txtDeliveryDate);
        this.dpDeliveryDate = (DatePicker)findViewById(R.id.dpDeliveryDate);
        this.llRecipes = (LinearLayout) findViewById(R.id.llRecipes);
        this.ibAddRecipe = (ImageButton)findViewById(R.id.btnAddRecipe);
        this.tvTotalCost = (TextView)findViewById(R.id.txtTotalCost);
        this.etPriceToSell = (EditText)findViewById(R.id.etSell);
        this.etPriceToSell.addTextChangedListener(new CustomTextWatcher(this.etPriceToSell,this));
        this.tvGain = (TextView)findViewById(R.id.txtGain);
        this.btnSave = (Button) findViewById(R.id.btnSaveOrder);
        this.ibAddRecipe.setOnClickListener(this);
        this.btnSave.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRemoveRecipe:
                removeRecipeForm(v);
            break;
            case R.id.btnAddRecipe:
                addRecipeForm(null);
                break;
            case R.id.btnSaveOrder:
                Log.d(TAG, "Save button clicked");
//                saveOrder();
                break;
            /*case R.id.btnTakePictureRecipe:
                Log.d(TAG, "Take picture clicked");
                selectImage();
                break;*/
            default:
                Log.d(TAG, "Button clicked");
//                removeProductForm(v);
                break;
        }
    }

    private void saveOrder(){
        int orderId = Integer.valueOf((this.tvOrderId.getText().toString().isEmpty())?"0": this.tvOrderId.getText().toString());
        String clientName = this.etClientName.getText().toString();
        int day = this.dpDeliveryDate.getDayOfMonth();
        int month = this.dpDeliveryDate.getMonth() + 1;
        int year = this.dpDeliveryDate.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        Date deliveryDate = calendar.getTime();

    }

    private void addRecipeForm(Recipe recipe){
        // inflate content layout and add it to the relative layout as second child
        // add as second child, therefore pass index 1 (0,1,...)
        LinearLayout linearLayout = (LinearLayout)View.inflate(this,R.layout.form_order_recipe,null);
        Spinner spinner = (Spinner)linearLayout.findViewById(R.id.sRecipes);
        final EditText etQuantity = (EditText)linearLayout.findViewById(R.id.txtQuantity);
        final TextView tvRecipePortion = (TextView)linearLayout.findViewById(R.id.txtRecipePortion);
        final TextView tvTotalPortion = (TextView)linearLayout.findViewById(R.id.txtTotalPortion);
        final TextView tvRecipeCost= (TextView)linearLayout.findViewById(R.id.txtCostPerRecipe);
        final TextView tvtotalCost= (TextView)linearLayout.findViewById(R.id.txtTotalRecipes);
        ImageButton ibRemoveRecipe = (ImageButton)linearLayout.findViewById(R.id.btnRemoveRecipe);
        ibRemoveRecipe.setOnClickListener(this);
        etQuantity.setInputType(InputType.TYPE_NULL);
        SpinnerRecipeAdapter spinnerRecipeAdapter = getRecipes();
        spinner.setAdapter(spinnerRecipeAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Recipe recipe = (Recipe) parent.getItemAtPosition(position);
                if(recipe.getRecipeId()>0){
                    tvRecipePortion.setText(String.valueOf(recipe.getQuantity()));
                    tvRecipeCost.setText(String.valueOf(recipe.getRecipeCost(FormOrder.this)));
                    etQuantity.setFocusable(true);
                    etQuantity.setFocusableInTouchMode(true);
                    etQuantity.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    Log.d(TAG, "onItemSelected: "+etQuantity.toString());
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etQuantity.addTextChangedListener(new CustomTextWatcher(etQuantity, getApplicationContext()));
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if(llRecipes.getChildCount()==0){
            ibRemoveRecipe.setVisibility(View.GONE);
        }
        llRecipes.addView(linearLayout);
        linearLayoutArray.add(linearLayout);

    }

    private void removeRecipeForm(View v){
        ViewGroup viewGroup = (ViewGroup)v.getParent();
        llRecipes.removeView(viewGroup);
    }

    private SpinnerRecipeAdapter getRecipes(){
        RecipeDBAdapter recipeDBAdapter = new RecipeDBAdapter(this);
        List<Recipe> recipes = null;
        SpinnerRecipeAdapter spinnerRecipeAdapter = null;
        try {
            recipeDBAdapter.open();
            recipes = recipeDBAdapter.getAllItems();
            if (recipes!= null){
                spinnerRecipeAdapter = new SpinnerRecipeAdapter(this,recipes);
            }
            recipeDBAdapter.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return spinnerRecipeAdapter;
    }

    public void getTotalCost(){
        double total = 0;
        for (int i = 0; i< this.llRecipes.getChildCount(); i++){
            ViewGroup viewGroup = (ViewGroup)this.llRecipes.getChildAt(i);
            TextView totalRecipeCost = (TextView)viewGroup.findViewById(R.id.txtTotalRecipes);
            total = total + Double.valueOf(totalRecipeCost.getText().toString());
        }
        this.tvTotalCost.setText(String.valueOf(total));
        this.etPriceToSell.setFocusable(true);
        this.etPriceToSell.setFocusableInTouchMode(true);
        this.etPriceToSell.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    public void getTotalGain(){
        double total =0;
        double totalCost = Double.valueOf((this.tvTotalCost.getText().toString().isEmpty())? "0": this.tvTotalCost.getText().toString());
        double sell = Double.valueOf(this.etPriceToSell.getText().toString());
        total = sell - totalCost;
        this.tvGain.setText(String.valueOf(total));
    }

    class CustomTextWatcher implements TextWatcher {

        private ViewGroup viewGroup;
        private EditText tvRecipeQuantity;
        private TextView tvRecipePortions;
        private TextView tvTotalPortions;
        private TextView tvCostPerPortions;
        private TextView tvTotalCost;
        private int textChanged;

        private Context c;

        public CustomTextWatcher(EditText e, Context context) {
            switch (e.getId()){
                case R.id.txtQuantity:
                    this.viewGroup = (ViewGroup)e.getParent().getParent();
                    Log.d(TAG, "CustomTextWatcher: Parent" + viewGroup.toString());
                    this.tvRecipeQuantity = e;
                    this.tvRecipePortions = (TextView)viewGroup.findViewById(R.id.txtRecipePortion);
                    this.tvTotalPortions= (TextView)viewGroup.findViewById(R.id.txtTotalPortion);
                    this.tvCostPerPortions = (TextView)viewGroup.findViewById(R.id.txtCostPerRecipe);
                    this.tvTotalCost = (TextView)viewGroup.findViewById(R.id.txtTotalRecipes);
                    this.c = context;
                    this.textChanged = 1;
                    break;
                case R.id.etSell:
                    this.textChanged = 2;
                    break;
                default:
                    Log.d(TAG, "CustomTextWatcher: Text changed");
                    break;
            }
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
            switch (this.textChanged){
                case 1:
                    double quantity = Double.valueOf(this.tvRecipeQuantity.getText().toString());
                    int recipePortion = Integer.valueOf(this.tvRecipePortions.getText().toString());
                    double costPerPortion = Double.valueOf(this.tvCostPerPortions.getText().toString());

                    double totalPortions = quantity*recipePortion;
                    double totalCost = quantity*costPerPortion;

                    this.tvTotalPortions.setText(String.valueOf(totalPortions));
                    this.tvTotalCost.setText(String.valueOf(totalCost));
                    FormOrder.this.getTotalCost();
                    break;
                case 2:
                    FormOrder.this.getTotalGain();
                    break;
                default:
                    Log.d(TAG, "CustomTextWatcher: Text changed");
                    break;
            }
        }
    }
}
