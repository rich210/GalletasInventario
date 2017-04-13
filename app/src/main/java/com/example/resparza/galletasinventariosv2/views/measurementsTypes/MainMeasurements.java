package com.example.resparza.galletasinventariosv2.views.measurementsTypes;

import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resparza.galletasinventariosv2.MainActivity;
import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.dbadapters.MeasureTypeDBAdapter;
import com.example.resparza.galletasinventariosv2.models.MeasureType;

import java.sql.SQLException;
import java.util.List;


/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainMeasurementsMeasurements.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainMeasurements#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainMeasurements extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Layout views
    private static FloatingActionButton afab;
    private static FloatingActionButton dfab;
    private static FloatingActionButton efab;
    private RecyclerView recyclerView;

    //Animations
    private static Animation fbShow,fbClose;

    public static final String TAG = "MainMeasurements";
    public static final int REQUEST_CODE_ADD_MEASURE_TYPE = 10;
    public static final int REQUEST_CODE_MODIFY_MEASURE_TYPE = 20;
    public static final String EXTRA_ADDED_MEASURE = "extra_key_added_measure";
    public static final String EXTRA_SELECTED_MEASURE_ID = "extra_key_selected_measure_id";
    public static final String IS_UPDATE = "isUpdate";
    public static boolean isDfabOpen = false;
    public static boolean isEfabOpen = false;

    private MeasureTypeDBAdapter measureTypeDBAdapter;
    private List<MeasureType> measureTypes;
    private MeasureTypeContentAdapter adapter;


    public MainMeasurements() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Measurements.
     */
    // TODO: Rename and change types and number of parameters
    public static MainMeasurements newInstance(String param1, String param2) {
        MainMeasurements fragment = new MainMeasurements();
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
        // Inflate the layout for this fragment
        recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        recyclerView.setAdapter(getAdapter(recyclerView.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        initFloatingActionButtons();
        recyclerView.setOnClickListener(this);

        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView measurementTypeId;
        public CheckedTextView measurementTypeName;
        public CardView measurementCard;


        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_measurement_type, parent, false));
            measurementTypeId = (TextView) itemView.findViewById(R.id.txtMeasurerTypeId);
            measurementTypeName = (CheckedTextView) itemView.findViewById(R.id.txtMeasureTypeName);
            measurementCard = (CardView) itemView.findViewById(R.id.MeasurementsCardView);
        }
    }

    public static class MeasureTypeContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of List in RecyclerView.
        private List<MeasureType> lItems;
        private SparseBooleanArray mSelectedItemsIds;

        public MeasureTypeContentAdapter(Context context, List<MeasureType> measureTypes) {
            this.lItems = measureTypes;
            this.mSelectedItemsIds = new SparseBooleanArray();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        private void displayFloatingActionButtons(){
            //TODO: Add a booleant to check if the button is already open and do not animate again
            int size = getSelectedIds().size();
            if (size == 1){
                MainActivity.openDeleteFAB();
                MainActivity.openEditFAB();
            }else if (size > 1){
                MainActivity.closeEditFAB();
            }else {
                MainActivity.closeEditFAB();
                MainActivity.closeDeleteFAB();
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            MeasureType measureType = lItems.get(position);
            holder.measurementTypeId.setText(String.valueOf(measureType.getMeasureTypeId()));
            holder.measurementTypeName.setText(measureType.getMeasureTypeName());
            holder.measurementCard.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    //Toast.makeText(v.getContext(),"Clicked card view",Toast.LENGTH_SHORT).show();
                    CardView cv = (CardView)v;
                    RecyclerView rv = (RecyclerView)v.getParent();
                    toggleSelection(position);
                    if(isToggleSelection(position)){
                        cv.setCardBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.itemListBackgroundSecondary));

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

        public MeasureType getItem(int position) {
            return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position) : null ;
        }

        public List<MeasureType> getItems() {
            return lItems;
        }

        @Override
        public long getItemId(int position) {
            return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position).getMeasureTypeId() : position;
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
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Long selectedId;
        short size;
        SparseBooleanArray selected;
        switch (v.getId()) {
            case R.id.Addfab:
                intent = new Intent(getActivity(), FormMeasurement.class);
                intent.putExtra(IS_UPDATE,false);
                startActivityForResult(intent,REQUEST_CODE_ADD_MEASURE_TYPE);
                break;
            case R.id.Editfab:
                selected = adapter.getSelectedIds();
                size = (short) selected.size();
                if (size>1){
                    Toast.makeText(v.getContext(), "More that one type of measure is selected", Toast.LENGTH_SHORT).show();
                }else if (size == 1){
                    intent = new Intent(getActivity(), FormMeasurement.class);
                    selectedId = adapter.getItemId(selected.keyAt(0));
                    intent.putExtra(EXTRA_SELECTED_MEASURE_ID, selectedId);
                    intent.putExtra(IS_UPDATE,true);
                    startActivityForResult(intent, REQUEST_CODE_MODIFY_MEASURE_TYPE);
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
        afab = MainActivity.getAddFAB();
        afab.setOnClickListener(this);
        MainActivity.openAddFAB();
        dfab = MainActivity.getDeleteFAB();
        dfab.setOnClickListener(this);
        MainActivity.closeEditFAB();
        efab = MainActivity.getEditFAB();
        MainActivity.closeDeleteFAB();
        efab.setOnClickListener(this);
    }



    @Override
    public void onResume() {
        super.onResume();
        initFloatingActionButtons();
        recyclerView.setAdapter(getAdapter(recyclerView.getContext()));
    }

    private MeasureTypeContentAdapter getAdapter (Context context){
        MeasureTypeDBAdapter measureTypeDBAdapter = new MeasureTypeDBAdapter(context);
        List<MeasureType> measureTypes;
        try {
            measureTypeDBAdapter.open();
            measureTypes = measureTypeDBAdapter.getAllItems();
            if (measureTypes != null && !measureTypes.isEmpty()){
                adapter = new MeasureTypeContentAdapter(context, measureTypes);
            }else{
                //Add ui if list is empty
            }
        }catch (SQLiteException e){
            Log.e(TAG, "Error:"+ e.getMessage());
            //Add ui if list is empty
        } catch (SQLException e) {
            Log.e(TAG, "Error:"+ e.getMessage());
        }finally {
            measureTypeDBAdapter.close();
        }

        return adapter;
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
                MeasureTypeDBAdapter measureTypeDBAdapter = new MeasureTypeDBAdapter(recyclerView.getContext());
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
                if (measureTypeDBAdapter.deleteItemsByIds(ids)) {
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
