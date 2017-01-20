package com.example.resparza.galletasinventariosv2.views.orders;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.example.resparza.galletasinventariosv2.MainActivity;
import com.example.resparza.galletasinventariosv2.R;

/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainOrders.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainOrders#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainOrders extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final String TAG = "MainOrders";
    //Constants for intents
    public static final int REQUEST_CODE_ADD_ORDER = 10;
    public static final int REQUEST_CODE_MODIFY_ORDER = 20;
    public static final String EXTRA_ADDED_ORDER = "extra_key_added_order";
    public static final String EXTRA_SELECTED_ORDER_ID = "extra_key_selected_order_id";
    public static final String IS_UPDATE = "isUpdate";

    private FloatingActionButton afab;
    private FloatingActionButton dfab;
    private FloatingActionButton efab;


    public MainOrders() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainOrders.
     */
    // TODO: Rename and change types and number of parameters
    public static MainOrders newInstance(String param1, String param2) {
        MainOrders fragment = new MainOrders();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //recyclerView.setBackgroundResource(R.color.colorSecondary);

        initFloatingActionButtons();
        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckedTextView orderClientName;
        public TextView orderNumber;
        public TextView orderDeliveryDate;
        public TextView orderState;
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_order, parent, false));
            orderClientName = (CheckedTextView) itemView.findViewById(R.id.txtClientName);
            orderNumber = (TextView)itemView.findViewById(R.id.txtOrderNo);
            orderDeliveryDate = (TextView)itemView.findViewById(R.id.txtDeliveryDate);
            orderState = (TextView)itemView.findViewById(R.id.txtOrderState);
        }
    }

    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of List in RecyclerView.
        private static final int LENGTH = 18;
        private final String[] orderNumbers;
        private final String[] orderClientNames;
        private final String[] orderDeliveryDates;
        private final String[] orderStates;


        public ContentAdapter(Context context) {
            Resources resources = context.getResources();
            orderNumbers = resources.getStringArray(R.array.orderNumber);
            orderClientNames = resources.getStringArray(R.array.ordersClientNames);
            orderDeliveryDates = resources.getStringArray(R.array.orderDeliveryDate);
            orderStates = resources.getStringArray(R.array.orderStates);

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.orderNumber.setText(orderNumbers[position % orderNumbers.length]);
            holder.orderClientName.setText(orderClientNames[position % orderClientNames.length]);
            holder.orderDeliveryDate.setText(orderDeliveryDates[position % orderDeliveryDates.length]);
            holder.orderState.setText(orderStates[position % orderStates.length]);
        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }

    private void initFloatingActionButtons(){
        afab = ((MainActivity)getActivity()).getAddFAB();
        afab.setVisibility(View.VISIBLE);
        afab.setOnClickListener(this);
        dfab = ((MainActivity)getActivity()).getDeleteFAB();
        dfab.setVisibility(View.GONE);
        efab = ((MainActivity)getActivity()).getEditFAB();
        efab.setVisibility(View.VISIBLE);
        efab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //SparseBooleanArray selected;
        Intent intent;
        Long selectedId;
        short size;
        switch (v.getId()) {
            case R.id.Addfab:
                intent = new Intent(getActivity(), FormOrder.class);
                intent.putExtra(IS_UPDATE,false);
                startActivityForResult(intent, REQUEST_CODE_ADD_ORDER);
                break;
            case R.id.Editfab:
                //selected = mAdapter.getSelectedIds();
                //size = (short) selected.size();
                //if (size > 1) {
//                    Toast.makeText(ProductMainActivity.this, "More that one product is selected", Toast.LENGTH_SHORT).show();
                //} else if (size == 1) {
                intent = new Intent(getActivity(), FormOrder.class);
                //selectedId = mAdapter.getItemId(selected.keyAt(0));
                intent.putExtra(IS_UPDATE,true);
                //intent.putExtra(EXTRA_SELECTED_PRODUCT_ID, selectedId);
                startActivityForResult(intent, REQUEST_CODE_MODIFY_ORDER);
                //}
                break;
            case R.id.Deletefab:
                //showDeleteDialogConfirmation();
                /*
                selected = mAdapter.getSelectedIds();
                size = (short) selected.size();
                long ids[] = new long[size];
                for (int i = 0; i < size; i++) {
                    if (selected.valueAt(i)) {
                        selectedId = mAdapter.getItemId(selected.keyAt(i));
                        ids[i] = selectedId;
                    }
                }
                if (productDBAdapter.deleteItemsByIds(ids)) {
                    Log.d(TAG, "Deleted " + size + " products");
                    Toast.makeText(ProductMainActivity.this, "Deleted " + size + " products", Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error trying to delete products");
                    Toast.makeText(ProductMainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }*/
                break;

            default:
                break;
        }
    }

}
