package com.example.resparza.galletasinventariosv2.views;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.example.resparza.galletasinventariosv2.MainActivity;
import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.adapters.ProductContentAdapter;
import com.example.resparza.galletasinventariosv2.dbadapters.OrderDBAdapter;
import com.example.resparza.galletasinventariosv2.dbadapters.ProductDBAdapter;
import com.example.resparza.galletasinventariosv2.models.Order;
import com.example.resparza.galletasinventariosv2.models.Product;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Main.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Main extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = "Main";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FloatingActionButton afab;
    private FloatingActionButton dfab;
    private FloatingActionButton efab;

    ProductContentAdapter adapter;


    public Main() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Main.
     */
    // TODO: Rename and change types and number of parameters
    public static Main newInstance(String param1, String param2) {
        Main fragment = new Main();
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

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        CalendarView calendarView = (CalendarView) rootView.findViewById(R.id.calendarView);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.main_recycler_view);
        ProductDBAdapter productDBAdapter = new ProductDBAdapter(recyclerView.getContext());
        List<Product> products = null;
        try {
            productDBAdapter.open();
            products = productDBAdapter.getAllItemsOrderByQuantity();
        }catch (SQLiteException e){
            Log.e(TAG, "Error:"+ e.getMessage());
            //Add ui if list is empty
        } catch (SQLException e) {
            Log.e(TAG, "Error:"+ e.getMessage());
        }finally {
            productDBAdapter.close();
        }
        adapter = new ProductContentAdapter(recyclerView.getContext(),products, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setFocusable(false);

        OrderDBAdapter orderDBAdapter = new OrderDBAdapter(recyclerView.getContext());
        List<Order> orders = null;
        try {
            orderDBAdapter.open();
            orders = orderDBAdapter.getAllItemsByDate(Calendar.getInstance().getTime());
            orderDBAdapter.close();
            for (Order order:orders) {

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initFloatingActionButtons();
        return rootView;
    }

    private void initFloatingActionButtons(){
        MainActivity.closeAddFAB();
        MainActivity.closeEditFAB();
        MainActivity.closeDeleteFAB();
    }

    @Override
    public void onResume() {
        super.onResume();
        initFloatingActionButtons();
    }
}
