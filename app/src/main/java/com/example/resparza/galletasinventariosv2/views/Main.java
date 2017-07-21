package com.example.resparza.galletasinventariosv2.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
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
import android.widget.Toast;

import com.example.resparza.galletasinventariosv2.MainActivity;
import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.adapters.ProductContentAdapter;
import com.example.resparza.galletasinventariosv2.dbadapters.OrderDBAdapter;
import com.example.resparza.galletasinventariosv2.dbadapters.ProductDBAdapter;
import com.example.resparza.galletasinventariosv2.models.Order;
import com.example.resparza.galletasinventariosv2.models.Product;
import com.example.resparza.galletasinventariosv2.views.orders.FormOrder;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
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

import static android.content.Context.MODE_PRIVATE;
import static com.example.resparza.galletasinventariosv2.MainActivity.APPNAME;

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
    public static final String EXTRA_SELECTED_DATE = "extra_key_selected_date";

    private FloatingActionButton afab;
    private FloatingActionButton dfab;
    private FloatingActionButton efab;
    RecyclerView recyclerView;
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
        recyclerView = (RecyclerView) rootView.findViewById(R.id.main_recycler_view);

        adapter = new ProductContentAdapter(recyclerView.getContext(),getProducts(), true);
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
        calendarView.setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.listBackground,null));
        calendarView.shouldSelectFirstDayOfMonthOnScroll(false);
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                //TODO: Add date information to the form
                                Intent intent = new Intent(getActivity(), FormOrder.class);
                List<Event> events = calendarView.getEvents(dateClicked);
                if(events.size()== 1){
                    Event event = events.get(0);
                    long id = ((Order)event.getData()).getOrderId();
                    intent.putExtra(IS_UPDATE,true);
                    intent.putExtra(EXTRA_SELECTED_ORDER_ID, id);
                    startActivityForResult(intent, REQUEST_CODE_MODIFY_ORDER);
                }else if (events.size()>1){
                    showOrderListDialog(events);
                }else{
                    if(!(new Date().after(dateClicked))){
                        intent.putExtra(IS_UPDATE,false);
                        intent.putExtra(EXTRA_SELECTED_DATE, dateClicked);
                        startActivityForResult(intent, REQUEST_CODE_ADD_ORDER);
                    }
                    else{
                        Toast.makeText(recyclerView.getContext(),R.string.mainDateBefore,Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onDayClick: Can´t add new order in this date" + dateClicked.toString());
                    }

                }

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*
        if (MainActivity.prefs.getBoolean("mainfirsttrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs

            //Calendar´s buttons instruction
            new ShowcaseView.Builder(getActivity())
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setTarget(new ViewTarget(btnNextMonth))
//                    .setShowcaseEventListener()
                    //.hideOnTouchOutside()
                    .setContentTitle("Botones del calendario")
                    .setContentText("Estos botones sirven para cambiar de mes del calendario")
                    .build();

            //Instructions for calendar
            new ShowcaseView.Builder(getActivity())
                    .withMaterialShowcase()
                    //.setStyle()
                    .setTarget(new ViewTarget(calendarView))
                    //.hideOnTouchOutside()
                    .setContentTitle("Calendario")
                    .setContentText("Aqui se pueden ver todo los pedidos por mes, pasando el dedo de un costado a otro se puede cambiar el mes")
                    .build();

            //List instructions
            new ShowcaseView.Builder(getActivity())
                    .withMaterialShowcase()
                    //.setStyle()
                    .setTarget(new ViewTarget(calendarView))
                    //.hideOnTouchOutside()
                    .setContentTitle("Lista")
                    .setContentText("En esta parte de la pantalla se mostrara una lista con los ingredientes faltantes para una orden, o los ingredientes que se estan acabando")
                    .build();

            //MainActivity.prefs.edit().putBoolean("mainfirsttrun", false).commit();

        }
*/
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
        adapter = new ProductContentAdapter(recyclerView.getContext(),getProducts(), true);
        adapter.clearSelectedIds();
        adapter.displayFloatingActionButtons();
        recyclerView.setAdapter(adapter);
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
    private void showOrderListDialog(final List<Event> events) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(recyclerView.getContext());
        List<String> strings = new ArrayList<String>();
        for (Event e:events) {
            strings.add(((Order)e.getData()).getNameAndDate());
        }

        alertDialogBuilder.setTitle(R.string.mainSelectOrder);
//        alertDialogBuilder
//                .setMessage(getString(R.string.orderDiaglogConfirmationText));
        alertDialogBuilder.setItems(strings.toArray(new String[strings.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), FormOrder.class);
                Event event = events.get(which);
                long id = ((Order)event.getData()).getOrderId();
                intent.putExtra(IS_UPDATE,true);
                intent.putExtra(EXTRA_SELECTED_ORDER_ID, id);
                startActivityForResult(intent, REQUEST_CODE_MODIFY_ORDER);
            }
        });


        // set positive button YES message
//        alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // delete the employee and refresh the list
////                Long selectedId;
////                short size;
////                SparseBooleanArray selected;
////                OrderDBAdapter orderDBAdapter = new OrderDBAdapter(recyclerView.getContext());
////                selected = adapter.getSelectedIds();
////                size = (short) selected.size();
////                long ids[] = new long[size];
////                for (int i = 0 ; i<(size); i++){
////                    if (selected.valueAt(i)){
////                        selectedId = adapter.getItemId(selected.keyAt(i));
////                        adapter.toggleSelection(selected.keyAt(i));
////                        ids[i] = selectedId;
////                    }
////                }
////                if (orderDBAdapter.deleteItemsByIds(ids)) {
////                    Toast.makeText(recyclerView.getContext(), "Deleted " +size +" orders", Toast.LENGTH_SHORT).show();
////                    adapter.notifyDataSetChanged();
////                    onResume();
////                }else{
////                    Log.d(TAG, "Error trying to delete orders");
////                    Toast.makeText(recyclerView.getContext(), getText(R.string.deleteErrorText), Toast.LENGTH_SHORT).show();
////                }
//
//            }
//        });

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
    private void showProductDialog(final List<Event> events) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(recyclerView.getContext());
        List<String> strings = new ArrayList<String>();
        for (Event e:events) {
            strings.add(((Order)e.getData()).getNameAndDate());
        }

        alertDialogBuilder.setTitle(R.string.mainSelectOrder);
//        alertDialogBuilder
//                .setMessage(getString(R.string.orderDiaglogConfirmationText));
        alertDialogBuilder.setItems(strings.toArray(new String[strings.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), FormOrder.class);
                Event event = events.get(which);
                long id = ((Order)event.getData()).getOrderId();
                intent.putExtra(IS_UPDATE,true);
                intent.putExtra(EXTRA_SELECTED_ORDER_ID, id);
                startActivityForResult(intent, REQUEST_CODE_MODIFY_ORDER);
            }
        });


        // set positive button YES message
//        alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // delete the employee and refresh the list
////                Long selectedId;
////                short size;
////                SparseBooleanArray selected;
////                OrderDBAdapter orderDBAdapter = new OrderDBAdapter(recyclerView.getContext());
////                selected = adapter.getSelectedIds();
////                size = (short) selected.size();
////                long ids[] = new long[size];
////                for (int i = 0 ; i<(size); i++){
////                    if (selected.valueAt(i)){
////                        selectedId = adapter.getItemId(selected.keyAt(i));
////                        adapter.toggleSelection(selected.keyAt(i));
////                        ids[i] = selectedId;
////                    }
////                }
////                if (orderDBAdapter.deleteItemsByIds(ids)) {
////                    Toast.makeText(recyclerView.getContext(), "Deleted " +size +" orders", Toast.LENGTH_SHORT).show();
////                    adapter.notifyDataSetChanged();
////                    onResume();
////                }else{
////                    Log.d(TAG, "Error trying to delete orders");
////                    Toast.makeText(recyclerView.getContext(), getText(R.string.deleteErrorText), Toast.LENGTH_SHORT).show();
////                }
//
//            }
//        });

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

    public List<Product> getProducts(){
        ProductDBAdapter productDBAdapter = new ProductDBAdapter(recyclerView.getContext());
        List<Product> products = null;
        try {
            productDBAdapter.open();
            products = productDBAdapter.getAllProductNeededForOrder();
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

}
