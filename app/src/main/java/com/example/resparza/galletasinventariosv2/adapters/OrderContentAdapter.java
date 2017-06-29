package com.example.resparza.galletasinventariosv2.adapters;

import android.content.Context;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.resparza.galletasinventariosv2.MainActivity;
import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.models.Order;

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
        final Animation anim = AnimationUtils.loadAnimation(this.context, R.anim.slide_down);
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

                    final CardView cv = (CardView)v;
                    TextView tvRecipe = (TextView) cv.findViewById(R.id.txtRecipes);
                    final int initialHeight = v.getHeight();
                    final int distanceToExpand = initialHeight + tvRecipe.getHeight();
                    final int distanceToCollapse = initialHeight - tvRecipe.getHeight();
                    Animation slideDown = AnimationUtils.loadAnimation(cv.getContext(),R.anim.slide_down);
                    Animation slideUp = AnimationUtils.loadAnimation(cv.getContext(),R.anim.slide_up);
                    /*
                    Animation lvSlideDown = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            if (interpolatedTime == 1){
                                // Do this after expanded
                            }

                            cv.getLayoutParams().height = (int) (initialHeight + (distanceToExpand * interpolatedTime));
                            cv.requestLayout();
                        }

                        @Override
                        public boolean willChangeBounds() {
                            return true;
                        }
                    };

                    Animation lvSlideUp = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            if (interpolatedTime == 1){
                                // Do this after expanded
                            }

                            cv.getLayoutParams().height = (int) (initialHeight - (distanceToCollapse * interpolatedTime));
                            cv.requestLayout();
                        }

                        @Override
                        public boolean willChangeBounds() {
                            return true;
                        }
                    };
                    lvSlideDown.setDuration(500);
                    lvSlideUp.setDuration(500);
                    //Animation lvSlideDown = AnimationUtils.loadAnimation(cv.getContext(),R.anim.lv_slide_down);
                    //Animation lvSlideUp = AnimationUtils.loadAnimation(cv.getContext(),R.anim.lv_slide_up);
                    */
                    if (tvRecipe.getVisibility()== View.GONE)
                    {

                        TransitionManager.beginDelayedTransition(cv);
                        tvRecipe.setVisibility(View.VISIBLE);
                        tvRecipe.startAnimation(slideDown);

                        //v.startAnimation(lvSlideDown);

                    }else {
                        tvRecipe.startAnimation(slideUp);
                        tvRecipe.setVisibility(View.GONE);
                        TransitionManager.beginDelayedTransition(cv);
                        //v.startAnimation(lvSlideUp);
                    }
                        RecyclerView rv = (RecyclerView)v.getParent();
                        toggleSelection(position);
                        if(isToggleSelection(position)){
                            cv.setCardBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.colorSecundaryAccent));

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
