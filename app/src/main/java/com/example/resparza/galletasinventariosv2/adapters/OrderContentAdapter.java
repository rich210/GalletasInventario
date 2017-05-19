package com.example.resparza.galletasinventariosv2.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.resparza.galletasinventariosv2.MainActivity;
import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.models.Order;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by resparza on 16/02/2017.
 */

public class OrderContentAdapter extends RecyclerView.Adapter<OrderContentAdapter.ViewHolder> {
    private List<Order> lItems;
    private SparseBooleanArray mSelectedItemsIds;
    private Context context;
    //private Drawable[] mRecipePictures;
    public static final String TAG = "OrderContentAdapter";


    public OrderContentAdapter(Context context, List<Order> orders) {
        this.lItems = orders;
        this.mSelectedItemsIds = new SparseBooleanArray();
        this.context= context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Order order = lItems.get(position);

        //holder.imageRecipe.setImageDrawable(mRecipePictures[1]);
        if(order == null){
        Log.d(TAG, "Error the order is null");
        }

        holder.tvOrderNO.setText(String.valueOf(order.getOrderId()));
        if(order.getClientName() != null){
            holder.tvClientName.setText(order.getClientName());
        }else{
            holder.tvClientName.setText(order.getOrderName());
        }
        holder.tvDeliveryDate.setText(order.getFormatedDelivaryDate());
//        holder.tvOrderState.setText(order.getOrderStatus());
        holder.stateBackground.setBackgroundColor(order.getColorStatus());
//        GradientDrawable drawable = (GradientDrawable)holder.tvOrderState.getBackground();
        switch (order.getOrderStatus()){
            case "Abierto":
                holder.ivOrderState.setBackgroundResource(R.drawable.bg_round_corner_open);
                break;
            case "Trabajando":
                holder.ivOrderState.setBackgroundResource(R.drawable.bg_round_corner_work);
                break;
            case "Entregado":
                holder.ivOrderState.setBackgroundResource(R.drawable.bg_round_corner_done);
                break;
            case "Cancelado":
                holder.ivOrderState.setBackgroundResource(R.drawable.bg_round_corner_cancel);
                break;
            case "Espera confirmacion":
                holder.ivOrderState.setBackgroundResource(R.drawable.bg_round_corner_wait);
                break;
        }
        holder.tvRecipeName.setText(order.getRecipes());
        holder.orderCardView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                        //Toast.makeText(v.getContext(),"Clicked card view",Toast.LENGTH_SHORT).show();
                        CardView cv = (CardView)v;
                    TextView tvRecipe = (TextView) cv.findViewById(R.id.txtRecipes);
                    if (tvRecipe.getVisibility()== View.GONE)
                    {
                        tvRecipe.setVisibility(View.VISIBLE);
                    }else {
                        tvRecipe.setVisibility(View.GONE);
                    }
                        RecyclerView rv = (RecyclerView)v.getParent();
                        toggleSelection(position);
                        if(isToggleSelection(position)){
                            cv.setCardBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.itemListBackgroundSecondary));

                        }else{
//                            cv.setCardBackgroundColor(order.getColorStatus());
                            cv.setCardBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.itemListBackgroundPrimary));

                        }

                        displayFloatingActionButtons();
                    Log.d(TAG, "onClick: "+order.toString());
                    Log.d(TAG, "onClick: size "+ getSelectedIds().size());
                        }
        });
        }

        @Override
        public int getItemCount() {
            return (getItems() != null && !getItems().isEmpty()) ? getItems().size() : 0 ;
        }

        public Order getItem(int position) {
                return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position) : null ;
        }

        public List<Order> getItems() {
                return lItems;
        }

        @Override
        public long getItemId(int position) {
                return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position).getOrderId() : position;
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
        public CardView orderCardView;
        public LinearLayout stateBackground;
        public TextView tvOrderNO;
        public TextView tvClientName;
        public TextView tvDeliveryDate;
        public ImageView ivOrderState;
        public TextView tvRecipeName;
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_order, parent, false));
            orderCardView = (CardView)itemView.findViewById(R.id.OrderCardView);
            tvOrderNO = (TextView) itemView.findViewById(R.id.txtOrderNo);
            tvClientName = (TextView)itemView.findViewById(R.id.txtClientName);
            tvDeliveryDate = (TextView)itemView.findViewById(R.id.txtDeliveryDate);
            ivOrderState = (ImageView) itemView.findViewById(R.id.ivOrderState);
            tvRecipeName = (TextView)itemView.findViewById(R.id.txtRecipes);
            stateBackground = (LinearLayout)itemView.findViewById(R.id.lineBackgroundState);
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
}
