package com.example.resparza.galletasinventariosv2.views;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.resparza.galletasinventariosv2.views.orders.FormOrder;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.EventsContainer;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.w3c.dom.Text;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    public static final int REQUEST_CODE_ADD_ORDER = 10;
    public static final int REQUEST_CODE_MODIFY_ORDER = 20;
    public static final String EXTRA_ADDED_ORDER = "extra_key_added_order";
    public static final String EXTRA_SELECTED_ORDER_ID = "extra_key_selected_order_id";
    public static final String IS_UPDATE = "isUpdate";

    private FloatingActionButton afab;
    private FloatingActionButton dfab;
    private FloatingActionButton efab;
    private CompactCalendarView calendarView;
    private Button btnNextMonth;
    private Button btnPreviousMonth;
    private TextView tvMonth;

    ProductContentAdapter adapter;
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
    private String[] dayColumnNames = {"Lun","Mar","Mie","Jue","Vie","Sab","Dom"};




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
        calendarView = (CompactCalendarView) rootView.findViewById(R.id.compactcalendar_view);
        tvMonth = (TextView)rootView.findViewById(R.id.txtMonth);
        btnNextMonth = (Button)rootView.findViewById(R.id.btnNext);
        btnPreviousMonth = (Button)rootView.findViewById(R.id.btnPrevious);
        tvMonth.setText(dateFormatForMonth.format(calendarView.getFirstDayOfCurrentMonth()));
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


        initFloatingActionButtons();
        //calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.setEventIndicatorStyle(CompactCalendarView.FILL_LARGE_INDICATOR);
        calendarView.setDayColumnNames(dayColumnNames);
        calendarView.addEvents(getEvents(recyclerView.getContext(),Calendar.getInstance().getTime()));
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                //TODO: Redirect to order form when the event is clicked
                Intent intent = new Intent(getActivity(), FormOrder.class);
                List<Event> events = calendarView.getEvents(dateClicked);
                if(events.size()== 1){
                    Event event = events.get(0);
                    long id = ((Order)event.getData()).getOrderId();
                    intent.putExtra(IS_UPDATE,true);
                    intent.putExtra(EXTRA_SELECTED_ORDER_ID, id);
                    startActivityForResult(intent, REQUEST_CODE_MODIFY_ORDER);
                }else
                //TODO: Check if only one event per day if no display a pop up to choose one
                tvMonth.setText(dateFormatForMonth.format(dateClicked));
                Log.d(TAG, "inside onclick " + dateFormatForDisplaying.format(dateClicked) + calendarView.getEvents(dateClicked));
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                tvMonth.setText(dateFormatForMonth.format(firstDayOfNewMonth));
                calendarView.removeAllEvents();
                calendarView.addEvents(getEvents(getContext(),firstDayOfNewMonth));
            }
        });

        btnPreviousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.showPreviousMonth();
            }
        });

        btnNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.showNextMonth();
            }
        });
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

    public List<Event> getEvents(Context context, Date date){
        List<Event> events = new ArrayList<Event>();
        List<Order> orders = null;
        OrderDBAdapter orderDBAdapter = new OrderDBAdapter(context);
        int eventColor = ResourcesCompat.getColor(getResources(),R.color.colorAccent,null);
        try {
            orderDBAdapter.open();
            orders = orderDBAdapter.getAllItemsByDate(date);
            orderDBAdapter.close();
            for (Order order:orders) {
                Event event = new Event(order.getColorStatus(),order.getDeliveryDate().getTime(),order);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
}
