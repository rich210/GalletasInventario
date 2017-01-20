package com.example.resparza.galletasinventariosv2.views.recipes;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.resparza.galletasinventariosv2.MainActivity;
import com.example.resparza.galletasinventariosv2.R;
import com.example.resparza.galletasinventariosv2.adapters.RecipeContentAdapter;
import com.example.resparza.galletasinventariosv2.dbadapters.RecipeDBAdapter;
import com.example.resparza.galletasinventariosv2.models.Recipe;

import java.sql.SQLException;
import java.util.List;

/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainRecipes.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainRecipes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainRecipes extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String TAG = "MainRecipes";
    //Constants for intents
    public static final int REQUEST_CODE_ADD_RECIPE = 10;
    public static final int REQUEST_CODE_MODIFY_RECIPE = 20;
    public static final String EXTRA_ADDED_RECIPE = "extra_key_added_recipe";
    public static final String EXTRA_SELECTED_RECIPE_ID = "extra_key_selected_recipe_id";
    public static final String IS_UPDATE = "isUpdate";

    private FloatingActionButton afab;
    private FloatingActionButton dfab;
    private FloatingActionButton efab;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public MainRecipes() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainRecipes.
     */
    // TODO: Rename and change types and number of parameters
    public static MainRecipes newInstance(String param1, String param2) {
        MainRecipes fragment = new MainRecipes();
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
        RecipeDBAdapter recipeDBAdapter = new RecipeDBAdapter(recyclerView.getContext());
        List<Recipe> recipes = null;
        try {
            recipeDBAdapter.open();
            recipes = recipeDBAdapter.getAllItems();
        }catch (SQLiteException e){
            Log.e(TAG, "Error:"+ e.getMessage());
            //Add ui if list is empty
        } catch (SQLException e) {
            Log.e(TAG, "Error:"+ e.getMessage());
        }finally {
            recipeDBAdapter.close();
        }
        if (recipes == null || recipes.isEmpty()){
            Intent intent = new Intent(getActivity(), FormRecipe.class);
            intent.putExtra(IS_UPDATE,false);
            startActivityForResult(intent, REQUEST_CODE_ADD_RECIPE);
        }
        RecipeContentAdapter adapter = new RecipeContentAdapter(recyclerView.getContext(),recipes);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //recyclerView.setBackgroundResource(R.color.colorSecondary);

        initFloatingActionButtons();

        return recyclerView;
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
                intent = new Intent(getActivity(), FormRecipe.class);
                intent.putExtra(IS_UPDATE,false);
                startActivityForResult(intent, REQUEST_CODE_ADD_RECIPE);
                break;
            case R.id.Editfab:
                //selected = mAdapter.getSelectedIds();
                //size = (short) selected.size();
                //if (size > 1) {
//                    Toast.makeText(ProductMainActivity.this, "More that one product is selected", Toast.LENGTH_SHORT).show();
                //} else if (size == 1) {
                intent = new Intent(getActivity(), FormRecipe.class);
                //selectedId = mAdapter.getItemId(selected.keyAt(0));
                intent.putExtra(IS_UPDATE,true);
                //intent.putExtra(EXTRA_SELECTED_PRODUCT_ID, selectedId);
                startActivityForResult(intent, REQUEST_CODE_MODIFY_RECIPE);
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
