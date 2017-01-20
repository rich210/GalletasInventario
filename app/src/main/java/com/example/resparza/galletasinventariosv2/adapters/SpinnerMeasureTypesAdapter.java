package com.example.resparza.galletasinventariosv2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.models.MeasureType;

import java.util.List;

/**
 * Created by resparza on 04/03/2016.
 */
public class SpinnerMeasureTypesAdapter extends BaseAdapter {

    public static final String TAG = "SpinnerMeasureTypesAdapter";

    private List<MeasureType> mItems;
    private LayoutInflater mInflater;

    public SpinnerMeasureTypesAdapter(Context context, List<MeasureType> listMeasureTypes) {
        this.setItems(listMeasureTypes);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().size() : 0;
    }

    @Override
    public MeasureType getItem(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position).getMeasureTypeId() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            v = mInflater.inflate(R.layout.spinner_item_measure_type, parent, false);
            holder = new ViewHolder();
            holder.txtMeasureTypeId = (TextView) v.findViewById(R.id.txtMeasurerTypeId);
            holder.txtMeasureName = (TextView) v.findViewById(R.id.txtMeasureTypeName);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        // fill row data
        MeasureType currentItem = getItem(position);
        if (currentItem != null) {
            holder.txtMeasureTypeId.setText(String.valueOf(currentItem.getMeasureTypeId()));
            holder.txtMeasureName.setText(currentItem.getMeasureSymbol());
        }

        return v;
    }

    public List<MeasureType> getItems() {
        return mItems;
    }

    public void setItems(List<MeasureType> mItems) {
        this.mItems = mItems;
        this.mItems.add(0,new MeasureType(0, "Seleccione", "Seleccione",0,0,false));
    }

    public MeasureType getItemById(long id) {
        for (int i = 0; i <= mItems.size(); i++) {
            long measureTypeId = getItemId(i);
            if (measureTypeId == id) {
                return getItem(i);
            }
        }
        return null;
    }

    public int getPositionById(long id) {
        for (int i = 0; i <= mItems.size(); i++) {
            long measureTypeId = getItemId(i);
            if (measureTypeId == id) {
                return i;
            }
        }
        return 0;
    }

    class ViewHolder {
        TextView txtMeasureTypeId;
        TextView txtMeasureName;
    }
}
