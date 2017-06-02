package com.example.resparza.galletasinventariosv2.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.resparza.galletasinventariosv2.MainActivity;
import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.models.Product;
import com.example.resparza.galletasinventariosv2.views.products.FormProduct;

import java.util.List;

/**
 * Created by resparza on 09/01/2017.
 */
public class ProductContentAdapter extends RecyclerView.Adapter<ProductContentAdapter.ViewHolder> {
    // Set numbers of List in RecyclerView.
    private List<Product> lItems;
    private SparseBooleanArray mSelectedItemsIds;
    private Context context;
    private boolean isMain;
    public static final String TAG = "ProductContentAdapter";
    public static final String IS_UPDATE = "isUpdate";
    public static final String EXTRA_SELECTED_PRODUCT_ID = "extra_key_selected_product_id";
    public static final int REQUEST_CODE_MODIFY_PRODUCT = 20;


    public ProductContentAdapter(Context context, List<Product> products, boolean isMain) {
        this.lItems = products;
        this.mSelectedItemsIds = new SparseBooleanArray();
        this.context= context;
        this.isMain = isMain;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Product product = lItems.get(position);
        holder.productId.setText(String.valueOf(product.getProductId()));
        holder.productName.setText(product.getProductName());
        holder.productQuantity.setText(product.getQuantityInfo());
        holder.productCostPerUnit.setText(product.getCostPerUnitInfo());
            if(!isMain){
                holder.productCardView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        //Toast.makeText(v.getContext(),"Clicked card view",Toast.LENGTH_SHORT).show();
                        CardView cv = (CardView)v;
                        RecyclerView rv = (RecyclerView)v.getParent();
                        toggleSelection(position);
                        if(isToggleSelection(position)){
                            cv.setCardBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.itemListBackgroundSecondary));

                        }else{
                            if (product.isLowerThanMin()){
                                cv.setCardBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.itemProductLowerThanMin));
                            }else {
                                cv.setCardBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.itemListBackgroundPrimary));
                            }
                        }
                        displayFloatingActionButtons();
                        Log.d(TAG,product.getInfo());
                    }
                });
            }else{
                holder.productCardView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        showProductDialog(product);
                    }
                });
            }
        if (product.needToBuy()){
            holder.productCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.itemProductLowerThanMin));
        }else {
            holder.productCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.itemListBackgroundPrimary));
        }
    }



    public void displayFloatingActionButtons(){
        int size = getSelectedIds().size();
        if (size == 1){
            MainActivity.openDeleteFAB();
            MainActivity.openEditFAB();
        }else if (size > 1){
            MainActivity.closeEditFAB();
        }else {
            MainActivity.closeDeleteFAB();
            MainActivity.closeEditFAB();
        }
    }

    @Override
    public int getItemCount() {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().size() : 0 ;
    }

    public Product getItem(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position) : null ;
    }

    public List<Product> getItems() {
        return lItems;
    }

    @Override
    public long getItemId(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position).getProductId() : position;
    }

    public void selectView(int position, boolean value){
        if (value)
            mSelectedItemsIds.put(position,value);
        else
            mSelectedItemsIds.delete(position);
    }

    public boolean toggleSelection(int position){
        selectView(position, !mSelectedItemsIds.get(position));
        return !mSelectedItemsIds.get(position);
    }

    public boolean isToggleSelection(int position){
        return mSelectedItemsIds.get(position);
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public void clearSelectedIds() {
        this.mSelectedItemsIds = new SparseBooleanArray();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView productCardView;
        public TextView productId;
        public TextView productName;
        public TextView productQuantity;
        public TextView productCostPerUnit;
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_product, parent, false));
            productCardView = (CardView) itemView.findViewById(R.id.ProductCardView);
            productId = (TextView) itemView.findViewById(R.id.txtProductId);
            productName = (TextView) itemView.findViewById(R.id.txtProductName);
            productQuantity = (TextView) itemView.findViewById(R.id.txtQuantity);
            productCostPerUnit = (TextView) itemView.findViewById(R.id.txtCostPerUnit);
        }
    }

    private void showProductDialog(final Product product) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);


        alertDialogBuilder.setTitle("Cantidad");
        alertDialogBuilder
                .setMessage("Cantidad faltante: " + product.getProductNeeded());



        // set positive button YES message
        alertDialogBuilder.setPositiveButton("Añadir producto", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // delete the employee and refresh the list
                Intent intent = new Intent(context, FormProduct.class);

                long id = product.getProductId();
                intent.putExtra(IS_UPDATE,true);
                intent.putExtra(EXTRA_SELECTED_PRODUCT_ID, id);
                context.startActivity(intent);
            }
        });

        // set neutral button OK
        alertDialogBuilder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }
}
