package com.example.resparza.galletasinventariosv2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.models.Recipe;

import java.util.List;

/**
 * Created by resparza on 16/02/2017.
 */

public class SpinnerRecipeAdapter extends BaseAdapter {

    public static final String TAG = "SpinnerProductAdapter";

    private List<Recipe> mItems;
    private LayoutInflater mInflater;

    public SpinnerRecipeAdapter(Context context, List<Recipe> items) {
        this.setItems(items);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().size() : 0;
    }

    @Override
    public Recipe getItem(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position).getRecipeId() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            v = mInflater.inflate(R.layout.spinner_item_recipe, parent, false);
            holder = new ViewHolder();
            holder.tvRecipeName = (TextView) v.findViewById(R.id.tvRecipeName);
            holder.tvRecipeId = (TextView) v.findViewById(R.id.tvRecipeId);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        // fill row data
        Recipe currentItem = getItem(position);
        if (currentItem != null) {
            holder.tvRecipeName.setText(String.valueOf(currentItem.getRecipeName()));
            holder.tvRecipeId.setText(String.valueOf(currentItem.getRecipeId()));
        }

        return v;
    }

    public List<Recipe> getItems() {
        return mItems;
    }

    public void setItems(List<Recipe> mItems) {
        this.mItems = mItems;
        this.mItems.add(0,new Recipe(0,"Seleccione",0,null,null,0f,"",""));
    }

    public Recipe getItemById(long id) {
        for (int i = 0; i <= mItems.size(); i++) {
            long itemId = getItemId(i);
            if (itemId == id) {
                return getItem(i);
            }
        }
        return null;
    }

    public int getPositionById(long id) {
        for (int i = 0; i <= mItems.size(); i++) {
            long itemId = getItemId(i);
            if (itemId == id) {
                return i;
            }
        }
        return 0;
    }

    class ViewHolder {
        TextView tvRecipeId;
        TextView tvRecipeName;
    }
}
