package com.theLoneWarrior.floating;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.theLoneWarrior.floating.adapter.RecyclerViewAdapter;
import com.theLoneWarrior.floating.database.AppDataStorage;
import com.theLoneWarrior.floating.pojoClass.PackageInfoStruct;
import com.theLoneWarrior.floating.services.FloatingViewServiceClose;

import java.util.ArrayList;

public class SelectedApplication extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,RecyclerViewAdapter.ListItemCheckListener {

    private ArrayList<PackageInfoStruct> result = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_application);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setActionBarTitle();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SelectedApplication.this,MainActivity.class));
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                finish();*/
            }
        });

        FloatingActionButton fabStartFloating =(FloatingActionButton) findViewById(R.id.fabStart);
        fabStartFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(result.size() > 0)
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startService( new Intent(SelectedApplication.this, FloatingViewServiceClose.class));
                        }
                    }).start();
                    finish();
                }
                else
                {
                    CoordinatorLayout coordinator = (CoordinatorLayout) findViewById(R.id.co);

//                  /*  Snackbar.make(coordinator, textResId, Snackbar.LENGTH_LONG)
//                            .setAction(R.string.accept_ok, new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                }
//                            })
//                            .setDuration(Snackbar.LENGTH_INDEFINITE)
//                            .show();*/
                    Snackbar.make(coordinator, "Please Add Some Application To Display", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setRecycleView();
    }

    private void setActionBarTitle() {
        getSupportActionBar().setTitle(R.string.app_name);
    }

    private void setRecycleView() {
        SQLiteDatabase db = new AppDataStorage(this).getReadableDatabase();
        Cursor cursor = db.query(
                "APP_DATA",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                PackageInfoStruct newInfo = new PackageInfoStruct();
                newInfo.setAppName(cursor.getString(1));
                newInfo.setPacName(cursor.getString(2));
                newInfo.setBitmapString(Uri.parse(cursor.getString(3)));
                result.add(newInfo);
            } while (cursor.moveToNext());
            cursor.close();

        }
        db.close();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(SelectedApplication.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new RecyclerViewAdapter(SelectedApplication.this, result));
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
        getMenuInflater().inflate(R.menu.selected_application, menu);
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
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListItemCheck(int checkedItemIndex, CheckBox cb, ArrayList<PackageInfoStruct> pb) {
       /* PackageInfoStruct obj = result.get(checkedItemIndex);
        if (cb.isChecked()) {
            obj.checked = true;
            result.add(obj);

        } else {
            obj.checked = false;
            result.remove(obj);
        }
        saveData();*/
    }

   /* private void saveData() {
        StringBuilder saveResult = new StringBuilder("");
        if (result != null) {
            for (PackageInfoStruct str : result) {

                saveResult.append(str.getPacName()).append("+");

            }
        }


        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            SharedPreferences.Editor firstEditor = first.edit();
            editor.clear();
            editor.putString("data", String.valueOf(saveResult));
           *//* firstEditor.putBoolean("check", false);
            firstEditor.apply();*//*
            editor.apply();
        } else {
            //prefs = getSharedPreferences("appData", Context.MODE_PRIVATE);
            //  first = getSharedPreferences("first", Context.MODE_PRIVATE);

            Toast.makeText(this, "Enable the else Part", Toast.LENGTH_LONG).show();
          *//*  SharedPreferences.Editor editor = prefs.edit();
            SharedPreferences.Editor firstEditor = first.edit();
            Toast.makeText(this, "no data serialising", Toast.LENGTH_SHORT).show();
            firstEditor.putBoolean("check", false);
            firstEditor.apply();
            editor.apply();*//*
        }
        System.gc();
    }*/
}
