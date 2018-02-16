package com.example.resparza.galletasinventariosv2.views.orders;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.CalendarContract;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.adapters.SpinnerRecipeAdapter;
import com.example.resparza.galletasinventariosv2.dbadapters.OrderDBAdapter;
import com.example.resparza.galletasinventariosv2.dbadapters.RecipeDBAdapter;
import com.example.resparza.galletasinventariosv2.models.Order;
import com.example.resparza.galletasinventariosv2.models.OrderRecipe;
import com.example.resparza.galletasinventariosv2.models.Recipe;

import org.w3c.dom.Text;

import java.sql.Array;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FormOrder extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final String TAG = "FormOrder";
    private TextView tvOrderId;
    private EditText etClientName;
    //private TextView tvDeliveryDate;
    private DatePicker dpDeliveryDate;
    private LinearLayout llRecipes;
    //private ArrayList<LinearLayout> linearLayoutArray;
    private Spinner sOrderState;
    //private Spinner sSellAs;
    private TextView tvErrorSpinnerOrderState;
    //private ImageButton ibAddRecipe;
    private TextView tvTotalCost;
    private EditText etTotalPriceToSell;
    private TextView tvGain;
    //private Button btnSave;
    private boolean isUpdate;
    //private long dateSelected;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_order);
        Intent intent = getIntent();
        String tittle = "Agrega orden";
        initView();
        isUpdate = intent.getExtras().getBoolean(MainOrders.IS_UPDATE);

        //Set title and behavior depending if is a new order or update
        if(isUpdate){
            long id = intent.getExtras().getLong(MainOrders.EXTRA_SELECTED_ORDER_ID);
            if (id != 0) {
            loadData(id);
            tittle = "Actualizar orden";
            }
        }else {
            addRecipeForm(null);
        }
        //If date is already selected add it to the order form
        long dateSelected = intent.getExtras().getLong(MainOrders.EXTRA_SELECTED_DATE);
        if(dateSelected != 0){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateSelected);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            this.dpDeliveryDate.updateDate(year,month,day);
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
//        ArrayList<LinearLayout> linearLayoutArray = new ArrayList<LinearLayout>();
        this.tvOrderId = (TextView) findViewById(R.id.txtOrderId);
        this.etClientName = (EditText)findViewById(R.id.etClientName);
        //this.tvDeliveryDate = (TextView)findViewById(R.id.txtDeliveryDate);
        this.dpDeliveryDate = (DatePicker)findViewById(R.id.dpDeliveryDate);
        this.dpDeliveryDate.setMinDate(new Date().getTime());
        this.llRecipes = (LinearLayout) findViewById(R.id.llRecipes);
        //this.ibAddRecipe = (ImageButton)findViewById(R.id.btnAddRecipe);
        ImageButton ibAddRecipe = (ImageButton)findViewById(R.id.btnAddRecipe);
        this.tvTotalCost = (TextView)findViewById(R.id.txtTotalCost);
        this.tvGain = (TextView)findViewById(R.id.txtGain);
        //this.btnSave = (Button) findViewById(R.id.btnSaveOrder);
        Button btnSave = (Button) findViewById(R.id.btnSaveOrder);
        ibAddRecipe.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        this.sOrderState = (Spinner)findViewById(R.id.sOrderState);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.orderStates,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.sOrderState.setAdapter(adapter);
        this.tvErrorSpinnerOrderState = (TextView)findViewById(R.id.tvErrorSpinnerOrderState);
        this.etTotalPriceToSell = (EditText)findViewById(R.id.etTotalSell);
    }
    /**
    * Initialize buttons behaviour
    */
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
                saveOrder();
                break;
            default:
                Log.d(TAG, "Button clicked");
                break;
        }
    }

    private void saveOrder(){
        long orderId;
        int day;
        int month;
        int year;
        double totalCost;
        double priceToSell;
        double gain;
        String clientName;
        String orderStatus;
        Calendar calendar = Calendar.getInstance();
        Date deliveryDate;
        Order order = new Order();
        List<OrderRecipe> orderRecipes = new ArrayList<OrderRecipe>();
        OrderDBAdapter orderDBAdapter = new OrderDBAdapter(this);

        if(isValid()){

            //Get all values from the form
            Log.d(TAG, "saveOrder: The form is valid");
            orderId = Long.valueOf((this.tvOrderId.getText().toString().isEmpty())?"0": this.tvOrderId.getText().toString());
            clientName = this.etClientName.getText().toString();
            day = this.dpDeliveryDate.getDayOfMonth();
            month = this.dpDeliveryDate.getMonth();
            year = this.dpDeliveryDate.getYear();
            calendar.set(year,month,day);
            deliveryDate = calendar.getTime();
            totalCost = Double.valueOf(this.tvTotalCost.getText().toString());
            priceToSell = Double.valueOf(this.etTotalPriceToSell.getText().toString());
            gain = Double.valueOf(this.tvGain.getText().toString());
            orderStatus = this.sOrderState.getSelectedItem().toString();
            //For each recipe added to the order calculate the cost and the total sell
            for (int i = 0; i< this.llRecipes.getChildCount(); i++){
                ViewGroup viewGroup = (ViewGroup)this.llRecipes.getChildAt(i);
                Spinner recipeSpinner = (Spinner)viewGroup.findViewById(R.id.sRecipes);
                EditText etQuantity = (EditText)viewGroup.findViewById(R.id.txtQuantity);
                //TextView tvTotalPortions = (TextView)viewGroup.findViewById(R.id.txtTotalPortion); //
                TextView tvCostPerUnit = (TextView)viewGroup.findViewById(R.id.txtCostPerRecipe);
                TextView tvTotalRecipes = (TextView)viewGroup.findViewById(R.id.txtTotalRecipes);
                EditText etPriceSell = (EditText) viewGroup.findViewById(R.id.etSell);
                Spinner sSellAs = (Spinner)viewGroup.findViewById(R.id.sSellAs);
                double quantity = Double.valueOf(etQuantity.getText().toString());
                //double totalPortions = Double.valueOf(tvTotalPortions.getText().toString());
                double costPerUnit = Double.valueOf(tvCostPerUnit.getText().toString());
                double totalRecipes = Double.valueOf(tvTotalRecipes.getText().toString());
                double priceSell = Double.valueOf(etPriceSell.getText().toString());
                String sellAs = sSellAs.getSelectedItem().toString();
                Recipe recipe = (Recipe)recipeSpinner.getSelectedItem();
                OrderRecipe orderRecipe = new OrderRecipe(recipe.getRecipeId(),quantity, costPerUnit,totalRecipes);
                orderRecipe.setRecipeName(recipe.getRecipeName());
                orderRecipe.setSellAs(sellAs);
                orderRecipe.setSellPrices(priceSell);
                if(isUpdate){
                    orderRecipe.setOrderId(orderId);
                }
                orderRecipes.add(orderRecipe);
            }

            order.setClientName(clientName);
            order.setDeliveryDate(deliveryDate);
            order.setOrderRecipes(orderRecipes);
            order.setTotal(totalCost);
            order.setSellPrice(priceToSell);
            order.setGain(gain);
            order.setOrderStatus(orderStatus);

            //Start adding records to the database
            try {
                orderDBAdapter.open();
                if(isUpdate){
                    Log.d(TAG, "saveOrder: Is update");
                    order.setOrderId(Long.valueOf(this.tvOrderId.getText().toString()));
                    if(orderDBAdapter.updateItem(order)){
                        setResult(RESULT_OK);
                        Log.d(TAG, "saveOrder: The order was updated correctyl");
                        orderDBAdapter.close();
                        if(order.getOrderStatus().equals("Cancelado")){
                            deleteCalendarEvent(order.getEventId());
                        }
                        finish();
                    }else{
                        Log.d(TAG, "saveOrder: the order is not updated correctly");
                    }
                }else{
                    order.setEventId(insertCalendarEvent(order));
                    if(order.getEventId()>0){
                        order.setOrderId(orderDBAdapter.insertItem(order));
                        if(order.getOrderId()>0){
                            setResult(RESULT_OK);
                            Log.d(TAG, "saveOrder: The order was inserted correctly");
                            orderDBAdapter.close();
                            finish();
                        }else{
                            Log.e(TAG, "saveOrder: Error inserting order");
                        }
                    }else{
                        Log.e(TAG, "saveOrder: Error inserting event");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            Log.d(TAG, "saveOrder: Is not valid");
        }
    }

    private boolean isValid(){
        boolean isValid = true;
        if(this.etClientName.getText().toString().isEmpty()){
            this.etClientName.setError(this.etClientName.getHint() + getResources().getString(R.string.requiredErrorText));
            isValid = false;
        }
        for (int i = 0; i< this.llRecipes.getChildCount(); i++){
            ViewGroup viewGroup = (ViewGroup)this.llRecipes.getChildAt(i);
            Spinner spinner = (Spinner)viewGroup.findViewById(R.id.sRecipes);
            EditText etQuantity = (EditText)viewGroup.findViewById(R.id.txtQuantity);
            if( spinner.getSelectedItemId()<=0){
                Toast.makeText(this, "Seleccione una receta",Toast.LENGTH_LONG).show();
                TextView tvErrorRecipe = (TextView)viewGroup.findViewById(R.id.tvErrorRecipe);
                tvErrorRecipe.setVisibility(View.VISIBLE);
                isValid = false;
            }
            if (etQuantity.getText().toString().isEmpty()){
                etQuantity.setError(etQuantity.getHint() + getResources().getString(R.string.requiredErrorText));
                isValid = false;
            }

        }
        if (this.tvTotalCost.getText().toString().isEmpty()) {
            this.tvTotalCost.setError("No se a asignado el precio total");
            isValid = false;
        }
        if(etTotalPriceToSell.getText().toString().isEmpty()){
            etTotalPriceToSell.setError("Precio a vender es requerido");
            isValid = false;
        }
        if (tvGain.getText().toString().isEmpty()){
            tvGain.setError("Ganancia no asignada");
            isValid = false;
        }
        if(this.sOrderState.getSelectedItemId()<=0){
            tvErrorSpinnerOrderState.setVisibility(View.VISIBLE);
            isValid = false;
        }
        if(isUpdate){
            if(this.tvOrderId.getText().toString().isEmpty()){
                Toast.makeText(this,"Error updating",Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        }
        return isValid;
    }

    private void addRecipeForm(final OrderRecipe orderRecipe){
        //Get All the layouts and views for the orderRecipe
        //TODO: Clean object no need it
        //linearLayoutArray = new ArrayList<LinearLayout>();
        LinearLayout linearLayout = (LinearLayout)View.inflate(this,R.layout.form_order_recipe,null);
        final EditText etQuantity = (EditText)linearLayout.findViewById(R.id.txtQuantity);
        final TextView tvRecipePortion = (TextView)linearLayout.findViewById(R.id.txtRecipePortion);
        final TextView tvTotalPortion = (TextView)linearLayout.findViewById(R.id.txtTotalPortion);
        final TextView tvRecipeCost= (TextView)linearLayout.findViewById(R.id.txtCostPerRecipe);
        final TextView tvtotalCost= (TextView)linearLayout.findViewById(R.id.txtTotalRecipes);
        final Spinner sSellAs = (Spinner)linearLayout.findViewById(R.id.sSellAs);
        final EditText etPriceToSell = (EditText)linearLayout.findViewById(R.id.etSell);
        Spinner spinner = (Spinner)linearLayout.findViewById(R.id.sRecipes);
        ImageButton ibRemoveRecipe = (ImageButton)linearLayout.findViewById(R.id.btnRemoveRecipe);

        //Get the array adapter for the spinner to choose the type to add the price to sell the product
        ArrayAdapter sellAsOptionsAdapter = ArrayAdapter.createFromResource(this,R.array.orderSellOption,android.R.layout.simple_spinner_item);
        sellAsOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSellAs.setAdapter(sellAsOptionsAdapter);
        sSellAs.setSelection(1);
        sSellAs.setOnItemSelectedListener(this);
        //Set Custom text watcher
        etPriceToSell.addTextChangedListener(new CustomTextWatcher(etPriceToSell,this));
        etPriceToSell.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);

        ibRemoveRecipe.setOnClickListener(this);

        //Get all the recipes for the spinner
        SpinnerRecipeAdapter spinnerRecipeAdapter = getRecipes();
        spinner.setAdapter(spinnerRecipeAdapter);
        //Set spinner selected item behavior
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: item was selected");
                Recipe recipe = (Recipe) parent.getItemAtPosition(position);
                if(recipe.getRecipeId()>0){
                    tvRecipePortion.setText(String.valueOf(recipe.getQuantity()));
                    tvRecipeCost.setText(String.valueOf(recipe.getRecipeCost(FormOrder.this)));
                    etQuantity.setFocusable(true);
                    etQuantity.setFocusableInTouchMode(true);
                    etQuantity.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    etQuantity.requestFocus();
                }
                if (orderRecipe != null) {
                    etQuantity.setText(String.valueOf(orderRecipe.getOrderQuantity()));
                    etPriceToSell.setText(String.valueOf(orderRecipe.getSellPrices()));
                    if(orderRecipe.getSellAs().equals("pz")){
                        sSellAs.setSelection(0);
                    }else{
                        sSellAs.setSelection(1);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //
        if (orderRecipe != null) {
            spinner.setSelection(spinnerRecipeAdapter.getPositionById(orderRecipe.getRecipeId()));
        }

        //Add type and add textwatcher for posibles chances
        etQuantity.setInputType(InputType.TYPE_NULL);
        etQuantity.addTextChangedListener(new CustomTextWatcher(etQuantity, getApplicationContext()));

        if(llRecipes.getChildCount()==0){
            ibRemoveRecipe.setVisibility(View.GONE);
        }

        //Add layouts to the view
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        llRecipes.addView(linearLayout);
        //linearLayoutArray.add(linearLayout);


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
            String sTotalRecipeCost = totalRecipeCost.getText().toString();
            total = total + Double.valueOf((sTotalRecipeCost.isEmpty())?"0":sTotalRecipeCost);
        }
        this.tvTotalCost.setText(String.valueOf(total));
        //this.etTotalPriceToSell.setFocusable(true);
        //this.etTotalPriceToSell.setFocusableInTouchMode(true);

    }

    public void getTotalGain(){
        double total =0;
        double partialTotal= 0;
        double gain = 0;
        String sellAs = null;
        for (int i = 0; i< this.llRecipes.getChildCount(); i++){
            ViewGroup viewGroup = (ViewGroup)this.llRecipes.getChildAt(i);
            Spinner sSellAs =(Spinner)viewGroup.findViewById(R.id.sSellAs);
            EditText etPriceToSell = ((EditText)viewGroup.findViewById(R.id.etSell));
            if(sSellAs != null){
                sellAs = sSellAs.getSelectedItem().toString();
                Double priceToSell = Double.valueOf(etPriceToSell.getText().toString().isEmpty()?"0":etPriceToSell.getText().toString());
                if (sellAs.equals("pz")){
                    Double totalPortions = Double.valueOf(((TextView)viewGroup.findViewById(R.id.txtTotalPortion)).getText().toString().isEmpty()?"0":((TextView)viewGroup.findViewById(R.id.txtTotalPortion)).getText().toString());
                    partialTotal = priceToSell * totalPortions;
                }else if (sellAs.equals("total")) {
                    partialTotal = priceToSell;
                } else{
                    if(priceToSell>0){
                        Double totalRecipe = Double.valueOf(((TextView)viewGroup.findViewById(R.id.txtTotalRecipes)).getText().toString().isEmpty()?"0":((TextView)viewGroup.findViewById(R.id.txtTotalRecipes)).getText().toString());
                        partialTotal = totalRecipe * (1+(priceToSell/100));
                    }else {
                        Toast.makeText(this,"Valor en % tiene que ser mayor que 0",Toast.LENGTH_LONG);
                        etPriceToSell.setError("Valor menor a 0");
                    }
                }
            }



            total = total + partialTotal;
        }
        this.etTotalPriceToSell.setText(String.valueOf(total));
        gain = total - Double.valueOf( this.tvTotalCost.getText().toString().isEmpty()?"0": this.tvTotalCost.getText().toString());
        this.tvGain.setText(String.valueOf(gain));
    }

    public void loadData(long id){
        Order order;
        OrderDBAdapter orderDBAdapter = new OrderDBAdapter(this);
        try {
            orderDBAdapter.open();
            order = orderDBAdapter.getItemById(id);
            orderDBAdapter.close();
            this.tvOrderId.setText(String.valueOf(order.getOrderId()));
            this.etClientName.setText(String.valueOf(order.getClientName()));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(order.getDeliveryDate());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            this.dpDeliveryDate.updateDate(year,month,day);
            for (OrderRecipe orderRecipe: order.getOrderRecipes()) {
                addRecipeForm(orderRecipe);
            }
            this.tvTotalCost.setText(String.valueOf(order.getTotal()));
            this.etTotalPriceToSell.setText(String.valueOf(order.getSellPrice()));
            this.tvGain.setText(String.valueOf(order.getGain()));
            String[] strings = getResources().getStringArray(R.array.orderStates);
            int position = 0;
            for (int i = 0; i<strings.length; i++){
                if(order.getOrderStatus().equals(strings[i])){
                    position = i;
                    break;
                }
            }
            this.sOrderState.setSelection(position);
            this.getTotalGain();



        } catch (SQLException e) {
            e.printStackTrace();
        }

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
                    if(this.tvRecipeQuantity.getText().toString().trim().length()>0){
                        String sQuantity = this.tvRecipeQuantity.getText().toString();
                        String sRecipePortion = this.tvRecipePortions.getText().toString();
                        String sCostPerPortion = this.tvCostPerPortions.getText().toString();
                        Log.d(TAG, "afterTextChanged: "+sRecipePortion + " " + sCostPerPortion);
                        double quantity = Double.valueOf((sQuantity.isEmpty())?"0": sQuantity);
                        int recipePortion = Integer.valueOf((sRecipePortion.isEmpty())?"0": sRecipePortion);
                        double costPerPortion = Double.valueOf((sCostPerPortion.isEmpty())?"0":sCostPerPortion);

                        double totalPortions = quantity*recipePortion;
                        double totalCost = quantity*costPerPortion;

                        this.tvTotalPortions.setText(String.valueOf(totalPortions));
                        this.tvTotalCost.setText(String.valueOf(totalCost));
                        FormOrder.this.getTotalCost();
                    }
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

    public long insertCalendarEvent(Order order){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        long calendarId = Long.valueOf(sharedPref.getString("pref_calendar_account","1"));
        int daysBefore = Integer.valueOf(sharedPref.getString("pref_days_reminder", "1"));
        int notificationHourTime = Integer.valueOf( sharedPref.getString("pref_time_hours_reminder", "8"));
        int notificationMinTime = Integer.valueOf( sharedPref.getString("pref_time_minutes_reminder", "0"));
        long startMillis = 0;
        long endMillis = 0;
        Calendar time = Calendar.getInstance();
        time.setTime(order.getDeliveryDate());
        time.set(time.get(Calendar.YEAR), time.get(Calendar.MONTH),time.get(Calendar.DATE), 0 , 0, 0);
        startMillis = time.getTimeInMillis();

        time.set(time.get(Calendar.YEAR), time.get(Calendar.MONTH),time.get(Calendar.DATE), 23 , 59, 59);
        endMillis = time.getTimeInMillis();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "Pedido para: "+ order.getClientName());
        values.put(CalendarContract.Events.DESCRIPTION, order.getOrderDescription());
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Mexico_City");
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        Log.d(TAG, "insertCalendarEvent: OrderClientName:"+ order.getClientName() + " orderDescruiption: "+order.getOrderDescription());

        long eventID = Long.parseLong(uri.getLastPathSegment());
        if(eventID>0){
            Log.d(TAG, "insertCalendarEvent: event "+eventID);
        }
        //Reminder for user configuration
        ContentValues reminderValues = new ContentValues();
        reminderValues.put(CalendarContract.Reminders.MINUTES, (((1440)*daysBefore)-(60*notificationHourTime)-notificationMinTime));
        reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventID);
        reminderValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues);

        //Reminder one day early
        reminderValues.remove(CalendarContract.Reminders.MINUTES);
        reminderValues.put(CalendarContract.Reminders.MINUTES, 1440-(60*notificationHourTime)-notificationMinTime);
        cr.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues);
        //Reminder the day of delivery
        /*
        reminderValues.remove(CalendarContract.Reminders.MINUTES);
        reminderValues.put(CalendarContract.Reminders.MINUTES, -(60*notificationHourTime)-notificationMinTime);
        cr.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues);
        */
        return eventID;
    }

    private void deleteCalendarEvent(long eventId){
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        int rows = getContentResolver().delete(deleteUri, null, null);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String option = (String)parent.getItemAtPosition(position);
        this.getTotalGain();
        Log.d(TAG, "onItemSelected: Option:" + option.toString());

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
