package com.theLoneWarrior.floating;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.theLoneWarrior.floating.adapter.RecyclerViewAdapter;
import com.theLoneWarrior.floating.database.AppDataStorage;
import com.theLoneWarrior.floating.pojoClass.PackageInfoStruct;
import com.theLoneWarrior.floating.services.FloatingViewService;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ListItemCheckListener {

    private final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 69;
    private ArrayList<PackageInfoStruct> installedPackageDetails;
    private ArrayList<PackageInfoStruct> result = new ArrayList<>();
    private SharedPreferences prefs, first;
    private Intent intent;
    private SQLiteDatabase database;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        checkPermission();
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        intent = new Intent(MainActivity.this, FloatingViewService.class);
        searchPreviousService();

        /// recycler view propagation/////////////////////////////////////////////////

        prefs = getSharedPreferences("appData", Context.MODE_PRIVATE);
        first = getSharedPreferences("first", Context.MODE_PRIVATE);
        boolean flag = first.getBoolean("check", true);

        /////////////////////////////////////storing data from sharedpreferences///////////////////////////////
        if (!flag) {
            // if (false) {
            installedPackageDetails = getShortedInstalledApps();
            //    Toast.makeText(this, "data is there", Toast.LENGTH_SHORT).show();
            String saveResult = prefs.getString("data", null);
            //    Toast.makeText(this, "" + saveResult, Toast.LENGTH_SHORT).show();

            if (saveResult != null) {
                String[] split = saveResult.split("\\+");
                for (String aSplit : split) {
                    for (int j = 0; j < installedPackageDetails.size(); j++) {
                        PackageInfoStruct obj = installedPackageDetails.get(j);
                        if (obj.getPacName().equals(aSplit)) {
                            installedPackageDetails.get(j).checked = true;
                            result.add(obj);
                        }
                    }
                }
            }

            setRecycleView();

        } else {
            Toast.makeText(this, "run", Toast.LENGTH_SHORT).show();
            installedPackageDetails = getShortedInstalledApps();
            setRecycleView();
        }


    }

    void setRecycleView() {
        mAdapter = new RecyclerViewAdapter(MainActivity.this, installedPackageDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

    }

    private void searchPreviousService() {

        stopService(intent);
        // stopService(new Intent(this, MyIntentService.class));
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(382);
    }

    //////////////onCreate Ends/////////////////////////////////////////////////////


    /**
     * Set and initialize the view elements.
     */
    private void initializeView() {

        //////////////////////////Starting service ////////////////////////////////////

        findViewById(R.id.notify_me).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                database = new AppDataStorage(MainActivity.this).getWritableDatabase();

                ///////////////////////use async task or thread//////////////
                database.delete("APP_DATA", null, null);
                if (result != null) {
                    for (final PackageInfoStruct obj : result) {

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("NAME", obj.getAppName());
                        contentValues.put("PACKAGE", obj.getPacName());
                        contentValues.put("IMAGE_RESOURCE", obj.getBitmapString());


                        long i = database.insert("APP_DATA", null, contentValues);
                        if (i == -1) {
                            Toast.makeText(MainActivity.this, "Sorry for Inconvenience, Database Error", Toast.LENGTH_SHORT).show();
                        }

                    }
                    database.close();
                }

                startService(intent);
                saveData();
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    // You don't have permission
                    checkPermission();

                } else {
                    initializeView();
                }
            } else initializeView();
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            } else {
                initializeView();
            }
        } else {
            initializeView();
        }
    }

    //////////////////////////new application finder ////////////////////////////

    private ArrayList<PackageInfoStruct> getShortedInstalledApps() {

        final PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resInfo = packageManager.queryIntentActivities(intent, 0);

        //using hashset so that there will be no duplicate packages,
        //if no duplicate packages then there will be no duplicate apps
        HashSet<String> packageNames = new HashSet<>(0);
        List<ApplicationInfo> appInfo = new ArrayList<>(0);

        //getting package names and adding them to the hashset
        for (ResolveInfo resolveInfo : resInfo) {
            packageNames.add(resolveInfo.activityInfo.packageName);
        }

        //now we have unique packages in the hashset, so get their application info
        //and add them to the arraylist

        for (String packageName : packageNames) {
            try {
                appInfo.add(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
            } catch (PackageManager.NameNotFoundException e) {
                //Do Nothing
                Toast.makeText(this, "Some Internal Error", Toast.LENGTH_SHORT).show();
            }
        }

        //to sort the list of apps by their names
        Collections.sort(appInfo, new ApplicationInfo.DisplayNameComparator(packageManager));


        ArrayList<PackageInfoStruct> res = new ArrayList<>();
        for (int i = 0; i < appInfo.size(); i++) {
            ApplicationInfo p = appInfo.get(i);
            PackageInfoStruct newInfo = new PackageInfoStruct();
            newInfo.setAppName(p.loadLabel(getPackageManager()).toString());
            newInfo.setPacName(p.packageName);
            Drawable d = p.loadIcon(getPackageManager());
            Bitmap bitmap;
            //   bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
            bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            d.draw(canvas);
            //    newInfo.bitmap = codec(bitmap, 50);
            //  newInfo.bitmap = bitmap;
            bitmap = scaleDownBitmap(bitmap, 50, this);
            newInfo.setBitmapString(BitmapToString(bitmap));
            //Log.d("Trial", str);
            res.add(newInfo);
        }
        return res;
    }

    private String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bAOS);
        byte[] b = bAOS.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    ////////////////////////////scale downBitmap////////////////////////////////////////////////////

    private Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

       // final float densityMultiplier = ;

        int h = (int) (newHeight * context.getResources().getDisplayMetrics().density);
        int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));

        photo = Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }


    @Override
    public void onListItemCheck(int checkedItemIndex, CheckBox checkBox, ArrayList<PackageInfoStruct> filteredData) {
        PackageInfoStruct obj = filteredData.get(checkedItemIndex);
        if (checkBox.isChecked()) {
            obj.checked = true;
            result.add(obj);

        } else {
            obj.checked = false;
            result.remove(obj);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveData();
    }

    private void saveData() {
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
            firstEditor.putBoolean("check", false);
            firstEditor.apply();
            editor.apply();
        } else {
            //prefs = getSharedPreferences("appData", Context.MODE_PRIVATE);
            //  first = getSharedPreferences("first", Context.MODE_PRIVATE);

            Toast.makeText(this, "Enable the else Part", Toast.LENGTH_LONG).show();
          /*  SharedPreferences.Editor editor = prefs.edit();
            SharedPreferences.Editor firstEditor = first.edit();
            Toast.makeText(this, "no data serialising", Toast.LENGTH_SHORT).show();
            firstEditor.putBoolean("check", false);
            firstEditor.apply();
            editor.apply();*/
        }
        System.gc();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.selectAll: {

                if (!(result.size() == installedPackageDetails.size())) {
                 //   Toast.makeText(this, "Select All", Toast.LENGTH_SHORT).show();
                    result.clear();
                    for (PackageInfoStruct obj : installedPackageDetails) {
                        obj.checked = true;
                        result.add(obj);
                    }
                /*mAdapter.getFilter().filter("");*/
                    setRecycleView();
                }

                break;
            }

            case R.id.deSelectAll: {

                if ((result.size() > 0)) {
                   // Toast.makeText(this, "Deselect All" + result.size(), Toast.LENGTH_SHORT).show();
                    for (PackageInfoStruct obj : result) {
                        obj.checked = false;
                    }
                    result.clear();
                    setRecycleView();
                }
                break;
            }

        }

        saveData();
        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();

    }

    @Override
    public void onBackPressed() {
        //Toast.makeText(this, "Pressed Back ", Toast.LENGTH_SHORT).show();
        for (int i = 0; i < 5; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Runtime.getRuntime().gc();
            } else {
                System.gc();
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        for (int i = 0; i < 5; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Runtime.getRuntime().gc();
            } else {
                System.gc();
            }
        }
        super.onDestroy();
    }


}
