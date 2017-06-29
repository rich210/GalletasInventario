package com.example.resparza.galletasinventariosv2.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.resparza.galletasinventariosv2.MainActivity;
import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.models.Recipe;

import java.io.File;
import java.util.List;

/**
 * Created by resparza on 10/01/2017.
 */
public class RecipeContentAdapter extends RecyclerView.Adapter<RecipeContentAdapter.ViewHolder> {
    // Set numbers of List in RecyclerView.
    private static final int LENGTH = 18;

    private List<Recipe> lItems;
    private SparseBooleanArray mSelectedItemsIds;
    private Context context;
    private Drawable[] mRecipePictures;
    public static final String TAG = "ProductContentAdapter";


    public RecipeContentAdapter(Context context, List<Recipe> recipes) {
        this.lItems = recipes;
        this.mSelectedItemsIds = new SparseBooleanArray();
        this.context= context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Recipe recipe = lItems.get(position);

        //holder.imageRecipe.setImageDrawable(mRecipePictures[1]);
        if(recipe == null){
            Log.d(TAG, recipe.toString());
        }
        if(recipe.getRecipeImagePath() != null && !recipe.getRecipeImagePath().isEmpty() ){
            holder.imageRecipe.setImageURI(Uri.fromFile(new File(recipe.getRecipeImagePath())));
        }else{
            holder.imageRecipe.setVisibility(View.GONE);
        }

        holder.recipeName.setText(recipe.getRecipeName());
        holder.recipeCost.setText(String.valueOf(recipe.getRecipeCost(context)));
        //holder.recipeName.setText(recipeNames[position % recipeNames.length]);
        holder.recipeCardView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                //Toast.makeText(v.getContext(),"Clicked card view",Toast.LENGTH_SHORT).show();
                CardView cv = (CardView)v;
                RecyclerView rv = (RecyclerView)v.getParent();
                toggleSelection(position);
                if(isToggleSelection(position)){
                    cv.setCardBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.colorSecundaryAccent));

                }else{
                    cv.setCardBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.itemListBackgroundPrimary));

                }
                displayFloatingActionButtons();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().size() : 0 ;
    }

    public Recipe getItem(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position) : null ;
    }

    public List<Recipe> getItems() {
        return lItems;
    }

    @Override
    public long getItemId(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position).getRecipeId() : position;
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
       public CardView recipeCardView;
       public ImageView imageRecipe;
        public TextView recipeName;
        public TextView products;
        public TextView recipeCost;
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_recipe, parent, false));
            recipeCardView = (CardView)itemView.findViewById(R.id.RecipeCardView);
            imageRecipe = (ImageView) itemView.findViewById(R.id.ivRecipeImage);
            recipeName = (TextView) itemView.findViewById(R.id.txtRecipeName);
            products = (TextView)itemView.findViewById(R.id.txtProducts);
            recipeCost = (TextView)itemView.findViewById(R.id.txtRecipeCost);
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
