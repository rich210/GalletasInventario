package com.example.resparza.galletasinventariosv2;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import com.example.resparza.galletasinventariosv2.dbadapters.DBAdapter;
import com.example.resparza.galletasinventariosv2.views.Main;
import com.example.resparza.galletasinventariosv2.views.measurementsTypes.MainMeasurements;
import com.example.resparza.galletasinventariosv2.views.miscellaneous.SettingsFragment;
import com.example.resparza.galletasinventariosv2.views.orders.MainOrders;
import com.example.resparza.galletasinventariosv2.views.products.MainProducts;
import com.example.resparza.galletasinventariosv2.views.recipes.MainRecipes;

import java.sql.SQLException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "MainActivity";

    private static FloatingActionButton afab;
    private static FloatingActionButton dfab;
    private static FloatingActionButton efab;
    private AdView mAdView;
    //Animations
    private static Animation dfbOpen,efbOpen, afbOpen,fbClose;
    private static boolean isDfabOpen = false;
    private static boolean isEfabOpen = false;
    private static boolean isAfabOpen = false;
    private DBAdapter db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //TODO: Uncomment ad app id when it released
        String adAppId = "ca-app-pub-9102302636499642~7518001410";
        //String adAppId = "ca-app-pub-3940256099942544/6300978111";
        MobileAds.initialize(this, adAppId);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("95775FA647BCF103AB1894D2E28F56A6") //Delete this after test
                .build();
        mAdView.loadAd(adRequest);
        db = new DBAdapter(getApplicationContext());

        try {
            db.open();
            db.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Fragment fragment = null;
        Class fragmentClass = Main.class;;

        afab = (FloatingActionButton) findViewById(R.id.Addfab);
        dfab = (FloatingActionButton) findViewById(R.id.Deletefab);
        efab = (FloatingActionButton) findViewById(R.id.Editfab);

        dfbOpen = AnimationUtils.loadAnimation(this,R.anim.fbshow);
        efbOpen = AnimationUtils.loadAnimation(this,R.anim.fbshow);
        afbOpen = AnimationUtils.loadAnimation(this,R.anim.fbshow);
        fbClose = AnimationUtils.loadAnimation(this,R.anim.fbclose);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (fragment != null){
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        Class fragmentClass;
        int id = item.getItemId();
        switch(id){
            case R.id.nav_orders:
                fragmentClass = MainOrders.class;
                break;
            case R.id.nav_recipes:
                fragmentClass = MainRecipes.class;
                break;
            case R.id.nav_products:
                fragmentClass = MainProducts.class;
                break;
            case R.id.nav_measures:
                fragmentClass = MainMeasurements.class;
                break;
            case R.id.nav_settings:
                fragmentClass = SettingsFragment.class;
                break;
            default:
                fragmentClass = Main.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (fragment != null){
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            // Highlight the selected item has been done by NavigationView
            item.setChecked(true);
            // Set action bar title
            setTitle(item.getTitle());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static FloatingActionButton getAddFAB (){
        return afab;
    }

    public static FloatingActionButton getDeleteFAB (){
        return dfab;
    }

    public static FloatingActionButton getEditFAB (){
        return efab;
    }
    public static void openAddFAB(){
        if (!isAfabOpen) {
            afab.startAnimation(afbOpen);
            isAfabOpen = !isAfabOpen;
        }
    }
    public static void closeAddFAB(){
        if (isAfabOpen) {
            afab.startAnimation(fbClose);
            isAfabOpen= !isAfabOpen;
        }
    }
    public static void openDeleteFAB(){
        if (!isDfabOpen) {
            dfab.startAnimation(dfbOpen);
            isDfabOpen = !isDfabOpen;
        }
    }
    public static void closeDeleteFAB(){
        if (isDfabOpen) {
            dfab.startAnimation(fbClose);
            isDfabOpen = !isDfabOpen;
        }
    }
    public static void openEditFAB(){
        if (!isEfabOpen) {
            efab.startAnimation(efbOpen);
            isEfabOpen = !isEfabOpen;
        }
    }
    public static void closeEditFAB(){
        if (isEfabOpen) {
            efab.startAnimation(fbClose);
            isEfabOpen = !isEfabOpen;
        }
    }
}

