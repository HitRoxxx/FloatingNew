package com.theLoneWarrior.floating;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.lusfold.spinnerloading.SpinnerLoading;
import com.theLoneWarrior.floating.adapter.RecyclerViewAdapter;
import com.theLoneWarrior.floating.pojoClass.AppInfo;
import com.theLoneWarrior.floating.services.FloatingViewServiceClose;
import com.theLoneWarrior.floating.services.FloatingViewServiceOpen;
import com.theLoneWarrior.floating.services.FloatingViewServiceOpenIconOnly;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.theLoneWarrior.floating.splash.SplashScreen.mInterstitialAd;


public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ListItemCheckListener {

    private final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 69;
    private ArrayList<AppInfo> installedPackageDetails;
    private ArrayList<AppInfo> result = new ArrayList<>();
    private SharedPreferences first, selectedAppPreference;
    private Intent intent;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private TextView appCountView;
    private boolean refreshFlag = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//////////////////////////////ADS////////////////////


        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
//////////////////////////////////////////////////////////////
        //  setupWindowAnimations();
        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        appCountView = (TextView) findViewById(R.id.appSelected);
        checkPermission();
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        intent = new Intent(MainActivity.this, FloatingViewServiceClose.class);
        //  searchPreviousService();

        /// recycler view propagation/////////////////////////////////////////////////
        selectedAppPreference = MainActivity.this.getSharedPreferences("SelectedApp", Context.MODE_PRIVATE);
        //  prefs = getSharedPreferences("appData", Context.MODE_PRIVATE);
        first = getSharedPreferences("first", Context.MODE_PRIVATE);
        boolean flag = first.getBoolean("check", true);

        /////////////////////////////////////storing data from sharedpreferences///////////////////////////////
        if (!flag) {

                 /*   recycleViewPropagation();
                    setRecycleView();*/

            new RecycleViewSet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");


        } else {
            //  Toast.makeText(this, "run", Toast.LENGTH_SHORT).show();
            installedPackageDetails = getShortedInstalledApps();
            setRecycleView();
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //    saveData();
                // Refresh items

                if (!refreshFlag) {

                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    refreshItems();
                }
            }

        });

    }

    private class RecycleViewSet extends AsyncTask<String, String, String> {
        SpinnerLoading button;

        @Override
        protected String doInBackground(String... params) {
            button = (SpinnerLoading) findViewById(R.id.animation);
            button.post(new Runnable() {
                @Override
                public void run() {
                    button.setVisibility(View.VISIBLE);

                }
            });

            installedPackageDetails = getShortedInstalledApps();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    button.setVisibility(View.GONE);
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String saveResult = selectedAppPreference.getString("PacName", null);
            //    Toast.makeText(this, "" + saveResult, Toast.LENGTH_SHORT).show();
            result.clear();
            if (saveResult != null) {
                String[] split = saveResult.split("\\+");
                for (String aSplit : split) {
                    for (int j = 0; j < installedPackageDetails.size(); j++) {
                        AppInfo obj = installedPackageDetails.get(j);
                        if (obj.getPacName().equals(aSplit)) {
                            installedPackageDetails.get(j).checked = true;
                            result.add(obj);
                        }
                    }
                }
            }
            // appCount = result.size();
            appCountView.setText("" + result.size());
            setRecycleView();
        }


    }

    private void setupWindowAnimations() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Explode slide = new Explode();
            slide.setDuration(1000);
            getWindow().setExitTransition(slide);
        }

    }

    private void recycleViewPropagation() {
        installedPackageDetails = getShortedInstalledApps();
        //    Toast.makeText(this, "data is there", Toast.LENGTH_SHORT).show();
        String saveResult = selectedAppPreference.getString("PacName", null);
        //    Toast.makeText(this, "" + saveResult, Toast.LENGTH_SHORT).show();
        result.clear();
        if (saveResult != null) {
            String[] split = saveResult.split("\\+");
            for (String aSplit : split) {
                for (int j = 0; j < installedPackageDetails.size(); j++) {
                    AppInfo obj = installedPackageDetails.get(j);
                    if (obj.getPacName().equals(aSplit)) {
                        installedPackageDetails.get(j).checked = true;
                        result.add(obj);
                    }
                }
            }
        }
        // appCount = result.size();
        appCountView.setText("" + result.size());
    }

    void refreshItems() {
        // Load items
        //   selectedAppPreference = MainActivity.this.getSharedPreferences("SelectedApp", Context.MODE_PRIVATE);
        recycleViewPropagation();
        // Load complete
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...
        setRecycleView();
        //  mAdapter.notifyDataSetChanged();
        // Stop refresh animation
        swipeRefreshLayout.setRefreshing(false);

    }

    void setRecycleView() {
        mAdapter = new RecyclerViewAdapter(MainActivity.this, installedPackageDetails, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

    }

    private void searchPreviousService() {
        stopService(intent);
        try {

            stopService(new Intent(this, FloatingViewServiceOpen.class));
            stopService(new Intent(this, FloatingViewServiceOpenIconOnly.class));
            // stopService(new Intent(this, MyIntentService.class));

        } catch (Exception e) {
            Toast.makeText(this, "Some Error Happened In Closing Services", Toast.LENGTH_SHORT).show();
        }
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
                ////////////////////////
                saveData();
                Intent newIntent = new Intent(MainActivity.this, SelectedApplication.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //   newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(new Intent(newIntent));


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

    private ArrayList<AppInfo> getShortedInstalledApps() {

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


        ArrayList<AppInfo> res = new ArrayList<>();
        for (int i = 0; i < appInfo.size(); i++) {
            ApplicationInfo p = appInfo.get(i);
            AppInfo newInfo = new AppInfo();
            newInfo.setAppName(p.loadLabel(getPackageManager()).toString());
            newInfo.setPacName(p.packageName);
            newInfo.setData(p.dataDir);
            newInfo.setSource(p.sourceDir);

            Uri uri;
            if (p.icon != 0) {
                uri = Uri.parse("android.resource://" + p.packageName + "/" + p.icon);
            } else {
                uri = Uri.parse("android.resource://com.theLoneWarrior.floating/drawable/default_image");
            }

            newInfo.setBitmapString(uri);
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
    public void onListItemCheck(int checkedItemIndex, CheckBox checkBox, ArrayList<AppInfo> filteredData) {
        AppInfo obj = filteredData.get(checkedItemIndex);
        if (checkBox.isChecked()) {
            obj.checked = true;
            result.add(obj);

        } else {
            obj.checked = false;
            result.remove(obj);
        }
        saveData();
        appCountView.setText("" + result.size());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    private void saveData() {


        if (result != null) {
            StringBuilder PacName = new StringBuilder("");

            for (AppInfo str : result) {

                PacName.append(str.getPacName()).append("+");

            }
            StringBuilder AppName = new StringBuilder("");

            for (AppInfo str : result) {

                AppName.append(str.getAppName()).append("+");

            }

            StringBuilder AppImage = new StringBuilder("");

            for (AppInfo str : result) {

                AppImage.append(str.getBitmapString()).append("+");

            }

            StringBuilder AppSource = new StringBuilder("");

            for (AppInfo str : result) {

                AppSource.append(str.getSource()).append("+");

            }

            StringBuilder AppData = new StringBuilder("");

            for (AppInfo str : result) {

                AppData.append(str.getData()).append("+");

            }


            SharedPreferences selectedAppPreference = MainActivity.this.getSharedPreferences("SelectedApp", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = selectedAppPreference.edit();
            editor.clear();
            editor.putString("AppName", String.valueOf(AppName));
            editor.putString("PacName", String.valueOf(PacName));
            editor.putString("AppImage", String.valueOf(AppImage));
            editor.putString("AppSource", String.valueOf(AppSource));
            editor.putString("AppData", String.valueOf(AppData));
            editor.apply();

            SharedPreferences.Editor firstEditor = first.edit();
            firstEditor.putBoolean("check", false);
            firstEditor.apply();
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView, menu);
        searchView.setQueryHint("Application Name");
        // searchView.setIconifiedByDefault(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchView.setTransitionGroup(true);
        }
        //   searchView.setIconifiedByDefault(true);
        //   searchView.setIconified(true);
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {

            @Override
            public void onViewDetachedFromWindow(View arg0) {
                // search was detached/closed
                MainActivity.this.setItemsVisibility(menu, true);
                //   Toast.makeText(MainActivity.this, "close", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onViewAttachedToWindow(View arg0) {
                // search was opened
                MainActivity.this.setItemsVisibility(menu, false);
            }
        });

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
                    for (AppInfo obj : installedPackageDetails) {
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
                    for (AppInfo obj : result) {
                        obj.checked = false;
                    }
                    result.clear();
                    setRecycleView();
                }
                break;
            }

        }

        saveData();
        appCountView.setText("" + result.size());
        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView, final Menu menu) {
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

    private void setItemsVisibility(final Menu menu, boolean visible) {
        refreshFlag = visible;
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            item.setVisible(visible);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // saveData();

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
        NavUtils.navigateUpFromSameTask(this);
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

    @Override
    protected void onResume() {
        super.onResume();

    }
}
