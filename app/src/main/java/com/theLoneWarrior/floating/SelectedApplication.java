package com.theLoneWarrior.floating;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Explode;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.igalata.bubblepicker.BubblePickerListener;
import com.igalata.bubblepicker.adapter.BubblePickerAdapter;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.BubblePicker;
import com.theLoneWarrior.floating.adapter.RecyclerViewAdapterSelectedApp;
import com.theLoneWarrior.floating.helper.OnStartDragListener;
import com.theLoneWarrior.floating.helper.SimpleItemTouchHelperCallback;
import com.theLoneWarrior.floating.pojoClass.PackageInfoStruct;
import com.theLoneWarrior.floating.preference.PreferenceDialog;
import com.theLoneWarrior.floating.services.FloatingViewServiceClose;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class SelectedApplication extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnStartDragListener {

    BubblePicker picker;
    private ArrayList<PackageInfoStruct> result = new ArrayList<>();
    private ItemTouchHelper mItemTouchHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   setupWindowAnimations();
      setContentView(R.layout.activity_selected_application);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setActionBarTitle();
        picker = (BubblePicker) findViewById(R.id.picker);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SelectedApplication.this, MainActivity.class));
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                finish();*/
            }
        });

        FloatingActionButton fabStartFloating = (FloatingActionButton) findViewById(R.id.fabStart);
        fabStartFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (result.size() > 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startService(new Intent(SelectedApplication.this, FloatingViewServiceClose.class));
                        }
                    }).start();
                    finish();
                } else {
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

    private void setupWindowAnimations() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.setDuration(1000);
            getWindow().setEnterTransition(fade);
            Toast.makeText(this, "Animation", Toast.LENGTH_SHORT).show();
            Explode slide = new Explode();
            slide.setDuration(1000);
            getWindow().setReenterTransition(slide);
        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setRecycleView();
    }

    private void setActionBarTitle() {
        getSupportActionBar().setTitle(R.string.app_name);
    }

    private void setRecycleView() {
      /*  SQLiteDatabase db = new AppDataStorage(this).getReadableDatabase();
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
        db.close();*/

        SharedPreferences selectedAppPreference = SelectedApplication.this.getSharedPreferences("SelectedApp", Context.MODE_PRIVATE);

        String AppName = selectedAppPreference.getString("AppName", null);
        String PacName = selectedAppPreference.getString("PacName", null);
        String AppImage = selectedAppPreference.getString("AppImage", null);
        if (PacName != null && !PacName.equals("")) {
            String[] split1 = AppName.split("\\+");
            String[] split2 = PacName.split("\\+");
            String[] split3 = AppImage.split("\\+");
            result.clear();
            for (int i = 0; i < split2.length; i++) {
                PackageInfoStruct newInfo = new PackageInfoStruct();
                newInfo.setAppName(split1[i]);
                newInfo.setPacName(split2[i]);
                newInfo.setBitmapString(Uri.parse(split3[i]));
                result.add(newInfo);
            }
        }


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SelectedApplication.this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        // String syncConnPref = sharedPref.getString("OutputVie", "");
        if (sharedPref.getBoolean("SelectedApp", true)) {
            RecyclerViewAdapterSelectedApp adapter=new RecyclerViewAdapterSelectedApp(SelectedApplication.this, result);
            picker.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(SelectedApplication.this));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);

            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(recyclerView);

        } else {
            picker.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            setBubble();
        }


    }

    private void setBubble() {
        picker.setAdapter(new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {
                return result.size();
            }

            @NotNull
            @Override
            public PickerItem getItem(int position) {
                PackageInfoStruct packageInfoStruct = result.get(position);
                PickerItem item = new PickerItem();
                item.setTitle(packageInfoStruct.getAppName());
                /*item.setGradient(new BubbleGradient(colors.getColor((position * 2) % 8, 0),
                        colors.getColor((position * 2) % 8 + 1, 0), BubbleGradient.VERTICAL));*/
                Typeface mediumTypeface = Typeface.create("", Typeface.ITALIC);
                item.setTypeface(mediumTypeface);
                item.setTextColor(ContextCompat.getColor(SelectedApplication.this, android.R.color.white));
                /*item.setTextSize(40);*/
                try {
                    InputStream is = getContentResolver().openInputStream(packageInfoStruct.getBitmapString());
                    item.setBackgroundImage(Drawable.createFromStream(is, packageInfoStruct.getBitmapString().toString()));
                } catch (FileNotFoundException e) {
                    item.setBackgroundImage(ContextCompat.getDrawable(SelectedApplication.this, R.drawable.default_image));
                }
                item.setSelected(true);
                return item;
            }
        });
/// complete later
        picker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(@NotNull PickerItem item) {

            }

            @Override
            public void onBubbleDeselected(@NotNull PickerItem item) {
                item.setSelected(false);
            }
        });
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

           Intent intent = new Intent(SelectedApplication.this, PreferenceDialog.class);
            startActivity(intent);
          /*  finish();*/
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

    @Override
    protected void onResume() {
        super.onResume();
        picker.onResume();
        //  setRecycleView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        picker.onPause();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
