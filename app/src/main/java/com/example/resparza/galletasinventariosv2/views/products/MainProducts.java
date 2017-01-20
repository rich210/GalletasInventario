package com.example.resparza.galletasinventariosv2.views.products;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resparza.galletasinventariosv2.MainActivity;
import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.adapters.ProductContentAdapter;
import com.example.resparza.galletasinventariosv2.dbadapters.ProductDBAdapter;
import com.example.resparza.galletasinventariosv2.models.Product;

import java.sql.SQLException;
import java.util.List;

/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainProducts.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainProducts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainProducts extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //
    public static final String TAG = "ProductMainActivity";
    //Constants for intents
    public static final int REQUEST_CODE_ADD_PRODUCT = 10;
    public static final int REQUEST_CODE_MODIFY_PRODUCT = 20;
    public static final String EXTRA_ADDED_PRODUCT = "extra_key_added_product";
    public static final String EXTRA_SELECTED_PRODUCT_ID = "extra_key_selected_product_id";
    public static final String IS_UPDATE = "isUpdate";

    //Objects
    ProductDBAdapter productDBAdapter;
    List<Product> products;
    ProductContentAdapter adapter;

    //Layout Components
    private TextView txtProducts;
    private static FloatingActionButton afab;
    private static FloatingActionButton dfab;
    private static FloatingActionButton efab;
    private RecyclerView recyclerView;


    public MainProducts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainProducts.
     */
    // TODO: Rename and change types and number of parameters
    public static MainProducts newInstance(String param1, String param2) {
        MainProducts fragment = new MainProducts();
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
        recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        List<Product> products = getProducts(recyclerView.getContext());
        if (products == null || products.isEmpty()){
            Intent intent = new Intent(getActivity(), FormProduct.class);
            intent.putExtra(IS_UPDATE,false);
            startActivityForResult(intent, REQUEST_CODE_ADD_PRODUCT);
        }

        adapter = new ProductContentAdapter(recyclerView.getContext(),products);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //recyclerView.setBackgroundResource(R.color.listPrimary);

        initFloatingActionButtons();

        return recyclerView;

    }





    @Override
    public void onClick(View v) {
        SparseBooleanArray selected;
        Intent intent;
        Long selectedId;
        short size;
        switch (v.getId()) {
            case R.id.Addfab:
                intent = new Intent(getActivity(), FormProduct.class);
                intent.putExtra(IS_UPDATE,false);
                startActivityForResult(intent, REQUEST_CODE_ADD_PRODUCT);
                break;
            case R.id.Editfab:
                selected = adapter.getSelectedIds();
                size = (short) selected.size();
                if (size > 1) {
                    Toast.makeText(v.getContext(), "More that one product is selected", Toast.LENGTH_SHORT).show();
                } else if (size == 1) {
                    intent = new Intent(getActivity(), FormProduct.class);
                    selectedId = adapter.getItemId(selected.keyAt(0));
                    intent.putExtra(IS_UPDATE,true);
                    intent.putExtra(EXTRA_SELECTED_PRODUCT_ID, selectedId);
                    startActivityForResult(intent, REQUEST_CODE_MODIFY_PRODUCT);
                }
                break;
            case R.id.Deletefab:
                showDeleteDialogConfirmation();
                break;

            default:
                break;
        }
    }

    private void initFloatingActionButtons(){
        afab = ((MainActivity)getActivity()).getAddFAB();
        afab.setVisibility(View.VISIBLE);
        afab.setOnClickListener(this);
        dfab = ((MainActivity)getActivity()).getDeleteFAB();
        dfab.setVisibility(View.GONE);
        dfab.setOnClickListener(this);
        efab = ((MainActivity)getActivity()).getEditFAB();
        efab.setVisibility(View.GONE);
        efab.setOnClickListener(this);
    }


    private List<Product> getProducts(Context context){
        ProductDBAdapter productDBAdapter = new ProductDBAdapter(context);
        List<Product> products = null;
        try {
            productDBAdapter.open();
            products = productDBAdapter.getAllItems();
            if (products != null && !products.isEmpty()){
                return products;
            }else{
            }
        }catch (SQLiteException e){
            Log.e(TAG, "Error:"+ e.getMessage());
            //Add ui if list is empty
        } catch (SQLException e) {
            Log.e(TAG, "Error:"+ e.getMessage());
        }finally {
            productDBAdapter.close();
        }
        return products;

    }

    @Override
    public void onResume() {
        super.onResume();
        List<Product> products = getProducts(recyclerView.getContext());
        adapter = new ProductContentAdapter(recyclerView.getContext(),products);
        adapter.clearSelectedIds();
        adapter.displayFloatingActionButtons();
        recyclerView.setAdapter(adapter);
    }

    private void showDeleteDialogConfirmation() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(recyclerView.getContext());

        alertDialogBuilder.setTitle("Delete");
        alertDialogBuilder
                .setMessage("Are you sure you want to delete the measure type(s)");

        // set positive button YES message
        alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // delete the employee and refresh the list
                Long selectedId;
                short size;
                SparseBooleanArray selected;
                ProductDBAdapter productDBAdapter = new ProductDBAdapter(recyclerView.getContext());
                selected = adapter.getSelectedIds();
                size = (short) selected.size();
                long ids[] = new long[size];
                for (int i = 0 ; i<(size); i++){
                    if (selected.valueAt(i)){
                        selectedId = adapter.getItemId(selected.keyAt(i));
                        adapter.toggleSelection(selected.keyAt(i));
                        ids[i] = selectedId;
                    }
                }
                if (productDBAdapter.deleteItemsByIds(ids)) {
                    Toast.makeText(recyclerView.getContext(), "Deleted " +size +" types of measures", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    onResume();
                }else{
                    Log.d(TAG, "Error trying to delete types of measures");
                    Toast.makeText(recyclerView.getContext(), "Error", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // set neutral button OK
        alertDialogBuilder.setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {

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
