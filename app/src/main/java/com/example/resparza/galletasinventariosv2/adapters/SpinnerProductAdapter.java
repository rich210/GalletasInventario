package com.example.resparza.galletasinventariosv2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.models.Product;

import java.util.List;

/**
 * Created by resparza on 16/03/2016.
 */
public class SpinnerProductAdapter extends BaseAdapter {

    public static final String TAG = "SpinnerProductAdapter";

    private List<Product> mItems;
    private LayoutInflater mInflater;

    public SpinnerProductAdapter(Context context, List<Product> listMeasureTypes) {
        this.setItems(listMeasureTypes);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().size() : 0;
    }

    @Override
    public Product getItem(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position).getProductId() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            v = mInflater.inflate(R.layout.spinner_item_product, parent, false);
            holder = new ViewHolder();
            holder.txtProductName = (TextView) v.findViewById(R.id.txtProductName);
            holder.txtProductId = (TextView) v.findViewById(R.id.txtProductId);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        // fill row data
        Product currentItem = getItem(position);
        if (currentItem != null) {
            holder.txtProductName.setText(String.valueOf(currentItem.getProductName()));
            holder.txtProductId.setText(String.valueOf(currentItem.getProductId()));
        }

        return v;
    }

    public List<Product> getItems() {
        return mItems;
    }

    public void setItems(List<Product> mItems) {
        this.mItems = mItems;
        this.mItems.add(0,new Product("Seleccione",null,0,0));
    }

    public Product getItemById(long id) {
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
        TextView txtProductName;
        TextView txtProductId;
    }
}
