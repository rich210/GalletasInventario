package com.example.resparza.galletasinventariosv2.views.recipes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.adapters.SpinnerMeasureTypesAdapter;
import com.example.resparza.galletasinventariosv2.adapters.SpinnerProductAdapter;
import com.example.resparza.galletasinventariosv2.dbadapters.MeasureTypeDBAdapter;
import com.example.resparza.galletasinventariosv2.dbadapters.ProductDBAdapter;
import com.example.resparza.galletasinventariosv2.dbadapters.RecipeDBAdapter;
import com.example.resparza.galletasinventariosv2.models.MeasureType;
import com.example.resparza.galletasinventariosv2.models.Product;
import com.example.resparza.galletasinventariosv2.models.Recipe;
import com.example.resparza.galletasinventariosv2.models.RecipeProduct;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class FormRecipe extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "FormRecipe";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE_REQUEST = 2;

    private TextView tvRecipeId;
    private EditText etRecipeName;
    private LinearLayout llMainProducts;
    private ImageButton ibAddProduct;
    private EditText etRecipePortions;
    private EditText etRecipeInstructions;
    private Button btnTakePicture;
    private ImageView ivRecipeImage;
    private Button bSaveRecipe;
    private ProgressBar progressBar;

    private ArrayList<Integer> linearLayoutIds;
    private ArrayList<Long> productsIds;
    private int numberOfLinearLayout = 1;
    private ArrayList<LinearLayout> linearLayoutArray;
    //private LinearLayout[] linearLayoutArray = new LinearLayout[20];
    private String mCurrentPhotoPath;
    private boolean isUpdate;
    private Bitmap recipeImage;
    private String recipeImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_recipe);
        linearLayoutArray = new ArrayList<LinearLayout>();
        linearLayoutIds = new ArrayList<Integer>();
        productsIds = new ArrayList<Long>();
        recipeImage = null;
        initView();
        Intent intent = getIntent();
        String tittle = getResources().getString(R.string.recipeFormAddTitle);

        isUpdate = intent.getExtras().getBoolean(MainRecipes.IS_UPDATE);
        if (isUpdate) {
            long id = intent.getExtras().getLong(MainRecipes.EXTRA_SELECTED_RECIPE_ID);
            if (id != 0) {
                loadData(id);
                tittle = getResources().getString(R.string.recipeFormUpdateTitle);
            }
        }else{
            addProductForm(null);
        }
        setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                recipeImage = (Bitmap) extras.get("data");
            }else{
                if (data != null) {
                    try {
                        recipeImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            recipeImage = ThumbnailUtils.extractThumbnail(recipeImage,400,400);
            ivRecipeImage.setImageBitmap(recipeImage);
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

    /**
     * Initialize all the layout components
     */
    private void initView() {
        this.tvRecipeId = (TextView) findViewById(R.id.txtRecipeId);
        this.etRecipeName = (EditText) findViewById(R.id.etRecipeName);
        this.llMainProducts = (LinearLayout) findViewById(R.id.llMainProducts);
        this.ibAddProduct = (ImageButton) findViewById(R.id.btnAddProduct);
        this.btnTakePicture = (Button) findViewById(R.id.btnTakePictureRecipe);
        this.etRecipePortions = (EditText) findViewById(R.id.etRecipePortions);
        this.etRecipeInstructions = (EditText) findViewById(R.id.etRecipeInstruccions);
        this.ivRecipeImage = (ImageView)findViewById(R.id.ivRecipeImageTumb);
        this.bSaveRecipe = (Button)findViewById(R.id.saveRecipeButton);
        this.progressBar = (ProgressBar)findViewById(R.id.loading);

        this.bSaveRecipe.setOnClickListener(this);
        this.btnTakePicture.setOnClickListener(this);
        this.ibAddProduct.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //case R.id.btnRemoveProduct:
                //break;
            case R.id.btnAddProduct:
                Log.d(TAG, "Add button clicked");
                addProductForm(null);
                break;
            case R.id.saveRecipeButton:
                saveRecipe();
                break;
            case R.id.btnTakePictureRecipe:
                Log.d(TAG, "Take picture clicked");
                selectImage();
                break;
            default:
                Log.d(TAG, "Button clicked");
                removeProductForm(v);
                break;
        }
    }

    /**
     * Add layouts for products
     */
    //TODO: add only layout form_recipe_products for better performance
    public void addProductForm(final RecipeProduct recipeProduct) {
        if (numberOfLinearLayout <= 19) {

            LinearLayout.LayoutParams llProductParamas = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            LinearLayout llProductContainer = new LinearLayout(this);
            LinearLayout llProductQuantity = new LinearLayout(this);
            Spinner spinnerProduct = new Spinner(this);
            SpinnerProductAdapter productAdapter = getProducts();
            Spinner spinnerMeasureSymbol = new Spinner(this);
            EditText etProductQuantity = new EditText(this);
            //TextView tvMeasureSymbol = new TextView(this);
            ImageButton ibRemoveProduct = new ImageButton(this);
            spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    long equivalencyId = 0;
                    Product product = (Product) parent.getItemAtPosition(position);
                    SpinnerMeasureTypesAdapter measureTypeAdapter;
                    ViewGroup container = (ViewGroup)parent.getParent();
                    ViewGroup linearLayoutContainer = (ViewGroup)container.getChildAt(1);
                    Spinner spinnerMeasureSymbol = (Spinner) linearLayoutContainer.getChildAt(1);
                    if(product.getProductId()>0){
                        productsIds.add(new Long(product.getProductId()));
                        equivalencyId = product.getMeasureType().getMeasureEquivalencyId();
                        if(equivalencyId ==0){
                            equivalencyId = product.getMeasureType().getMeasureTypeId();
                        }
                        measureTypeAdapter = getMeasureTpes(equivalencyId);
                        spinnerMeasureSymbol.setAdapter(measureTypeAdapter);
                        if(recipeProduct != null){
                            spinnerMeasureSymbol.setSelection(measureTypeAdapter.getPositionById(recipeProduct.getMeasureTypeId()));
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(50, 0, 50, 0);
            spinnerProduct.setLayoutParams(layoutParams);
            spinnerProduct.setAdapter(productAdapter);
            etProductQuantity.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.5f));
            etProductQuantity.setHint(R.string.recipeFormQuantity);
            etProductQuantity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            if(recipeProduct != null){
                spinnerProduct.setSelection(productAdapter.getPositionById(recipeProduct.getProductId()));
                etProductQuantity.setText(String.valueOf(recipeProduct.getProductQuantity()));
            }
            spinnerMeasureSymbol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.3f));
            ibRemoveProduct.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f));
            ibRemoveProduct.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_minus));
            ibRemoveProduct.setOnClickListener(this);

            if (linearLayoutArray.size()== 0){
                ibRemoveProduct.setVisibility(View.GONE);
            }

            llProductContainer.setLayoutParams(llProductParamas);
            llProductContainer.setOrientation(LinearLayout.VERTICAL);
            llProductContainer.setId(View.generateViewId());

            llProductParamas.setMargins(0, 10, 0, 0);
            llProductQuantity.setLayoutParams(llProductParamas);
            llProductQuantity.addView(etProductQuantity);
            llProductQuantity.addView(spinnerMeasureSymbol);
            llProductQuantity.addView(ibRemoveProduct);

            llProductContainer.addView(spinnerProduct);
            llProductContainer.addView(llProductQuantity);

            linearLayoutIds.add(new Integer(llProductContainer.getId()));
            linearLayoutArray.add(llProductContainer);

            llMainProducts.addView(llProductContainer);
            numberOfLinearLayout++;
        } else{
            Toast.makeText(FormRecipe.this, "No se puede añadir mas prodcutos", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Remove layout created to add the products for the recipe
     * @param v is the button to check which layout is been clicked
     */
    public void removeProductForm(View v){
        LinearLayout linearLayout = (LinearLayout)v.getParent().getParent();
        Spinner productSpinner = (Spinner) linearLayout.getChildAt(0);
        Product product = (Product) productSpinner.getSelectedItem();
        productsIds.remove(new Long(product.getProductId()));
        linearLayoutIds.remove(new Integer(linearLayout.getId()));
        linearLayoutArray.remove(linearLayout);
        Log.d(TAG, linearLayoutIds.get(linearLayoutIds.size()-1).toString());
        numberOfLinearLayout--;
        llMainProducts.removeView(linearLayout);
    }

    /**
     * Get the product for the spinner
     * @return an adapter for the spinner
     */
    public SpinnerProductAdapter getProducts(){
        ProductDBAdapter productDBAdapter = new ProductDBAdapter(this);
        SpinnerProductAdapter spinnerAdapter = null;
        try {
            productDBAdapter.open();
            List<Product> products = productDBAdapter.getAllItemsForSpinner(productsIds);
            if (products != null) {
                spinnerAdapter = new SpinnerProductAdapter(this, products);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG,"Error trying to obtain all products",e.getCause());
        }finally {
            productDBAdapter.close();
        }
        return spinnerAdapter;
    }

    /**
     * Get the measures for the spinner
     * @return an adapter for the spinner
     */
    public SpinnerMeasureTypesAdapter getMeasureTpes(Long id){
        MeasureTypeDBAdapter measureTypeDBAdapter = new MeasureTypeDBAdapter(this);
        SpinnerMeasureTypesAdapter spinnerAdapter = null;
        try {
            measureTypeDBAdapter.open();
            List<MeasureType> measureTypes = measureTypeDBAdapter.getAllMeasureTypesByMeasureBase(id);
            if (measureTypes != null) {
                spinnerAdapter = new SpinnerMeasureTypesAdapter(this, measureTypes);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG,"Error trying to obtain all measureTypes",e.getCause());
        }finally {
            measureTypeDBAdapter.close();
        }
        return spinnerAdapter;
    }

    /**
     * Function that display the dialog activity with the options to obtain the picture
     */
    private void selectImage() {
        final CharSequence[] items = getResources().getStringArray(R.array.pictureOptions);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.chooseOption));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //boolean result= Utility.checkPermission(this);

                if (items[item].equals(getResources().getString(R.string.takePicture))) {
                    dispatchTakePictureIntent();

                } else if (items[item].equals(getResources().getString(R.string.chooseFromGallery))) {
                        galleryIntent();

                } else if (items[item].equals(getResources().getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * Function to start Camera activity and take the picture for the recipe
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Start galerry activity to choose the picture for the recipe
     */
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Seleccione una Imagen"),PICK_IMAGE_REQUEST);
    }


    private String saveImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "cookie_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        FileOutputStream fileOutputStream = new FileOutputStream(image.getAbsolutePath());
        Bitmap bitmap = recipeImage;
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
        return image.getAbsolutePath();
    }

    private void saveRecipe(){
        RecipeDBAdapter recipeDBAdapter = new RecipeDBAdapter(this);
        Boolean isProductValid = true;
        Editable recipeName = etRecipeName.getText();
        Editable portions = etRecipePortions.getText();
        Editable instructions = etRecipeInstructions.getText();
        Recipe recipe = new Recipe();
        List<RecipeProduct> recipeProducts = new ArrayList<RecipeProduct>();

        this.bSaveRecipe.setVisibility(View.GONE);
        this.progressBar.setVisibility(View.VISIBLE);

        if (isValid(recipeName,portions)){
            for (LinearLayout ll:linearLayoutArray) {
                Spinner spinner = (Spinner)ll.getChildAt(0);
                ViewGroup linearLayoutContainer = (ViewGroup)ll.getChildAt(1);
                EditText etQuantity = (EditText) linearLayoutContainer.getChildAt(0);
                Editable quantity = etQuantity.getText();
                Product product = (Product) spinner.getSelectedItem();
                if (TextUtils.isEmpty(quantity) || product.getProductId()<1)
                {
                    isProductValid = false;
                }
                RecipeProduct recipeProduct = new RecipeProduct(product.getProductId(), product.getMeasureType().getMeasureTypeId(), Float.valueOf(quantity.toString()));
                recipeProduct.setProductName(product.getProductName());
                if(isUpdate){
                    recipeProduct.setRecipeId(Long.valueOf( this.tvRecipeId.getText().toString()));
                }
                recipeProducts.add(recipeProduct);
            }
            if (recipeImage != null){
                try {
                    recipe.setRecipeImagePath(saveImageFile());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,"Error trying to save image");
                }
            }else if(recipeImagePath != null){
                recipe.setRecipeImagePath(recipeImagePath);
            }
            recipe.setRecipeName(recipeName.toString());
            recipe.setQuantity(Integer.valueOf(portions.toString()));

            if (isProductValid){

                    try {
                        recipeDBAdapter.open();
                        if(!isUpdate){
                            Log.d(TAG, "saveRecipe: "+ recipeProducts.toString());
                            recipe.setRecipeId(recipeDBAdapter.insertItem(recipe,recipeProducts));
                            if (recipe.getRecipeId()>0){
                                Log.d(TAG, "added recipe : " + recipe.getRecipeId() + " " + recipe.getRecipeName());
                                setResult(RESULT_OK);
                                recipeDBAdapter.close();
                                finish();
                            }else {
                                recipeDBAdapter.close();
                                Log.e(TAG, "saveRecipe: Error saving recipe");
                            }
                        }else{
                            recipe.setRecipeId(Long.valueOf(this.tvRecipeId.getText().toString()));
                            if(recipeDBAdapter.updateItem(recipe,recipeProducts)){
                                Log.d(TAG, "modified recipe : " + recipe.getRecipeId() + " " + recipe.getRecipeName());
                                setResult(RESULT_OK);
                                recipeDBAdapter.close();
                                finish();
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Log.d(TAG,"Error trying to insert recipe"+ e.getMessage());
                    }finally {
                        recipeDBAdapter.close();
                    }
            }else{
                Toast.makeText(this,R.string.errorRecipeProductsForm,Toast.LENGTH_SHORT);
                this.bSaveRecipe.setVisibility(View.VISIBLE);
                this.progressBar.setVisibility(View.GONE);
            }
        }else{
            Toast.makeText(this,R.string.errorRecipeForm,Toast.LENGTH_SHORT);
            this.bSaveRecipe.setVisibility(View.VISIBLE);
            this.progressBar.setVisibility(View.GONE);
        }
    }

    private boolean isValid(Editable recipeName, Editable portions){
        boolean isValid = true;
        if (recipeName.toString().isEmpty()){
            this.etRecipeName.setError(etRecipeName.getHint()+" "+ getResources().getString(R.string.requiredErrorText));
            isValid = false;
        }
        if (portions.toString().isEmpty()){
            this.etRecipePortions.setError(etRecipePortions.getHint()+" "+ getResources().getString(R.string.requiredErrorText));
            isValid = false;
        }
        return isValid;
    }

    private void loadData(long id){
        RecipeDBAdapter recipeDBAdapter = new RecipeDBAdapter(this);
        Recipe recipe;
        try {
            recipeDBAdapter.open();
            recipe = recipeDBAdapter.getItemById(id);
            recipeDBAdapter.close();
            this.tvRecipeId.setText(String.valueOf(recipe.getRecipeId()));
            this.etRecipeName.setText(recipe.getRecipeName());
            this.etRecipePortions.setText(String.valueOf(recipe.getQuantity()));
            this.etRecipeInstructions.setText(recipe.getRecipeInstructions());
            if(recipe.getRecipeImagePath() != null){
                this.ivRecipeImage.setImageURI(Uri.fromFile(new File(recipe.getRecipeImagePath())));
                this.recipeImagePath = recipe.getRecipeImagePath();
            }
            for (RecipeProduct recipeProduct: recipe.getRecipeProducts()) {
                addProductForm(recipeProduct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "Error retrieving recipe");
        }

    }

}
