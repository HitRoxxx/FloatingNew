package com.theLoneWarrior.floating;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.theLoneWarrior.floating.pojoClass.AppInfo;
import com.theLoneWarrior.floating.utils.UtilsApp;


public class ExtractFileInBackground extends AsyncTask<Void, String, Boolean> {
    private Context context;
    private Activity activity;
  //  private MaterialDialog dialog;
    private AppInfo appInfo;

    public ExtractFileInBackground(Context context, AppInfo appInfo) {
        this.activity = (Activity) context;
        this.context = context;
      //  this.dialog = dialog;
        this.appInfo = appInfo;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Boolean status = false;

        if (UtilsApp.checkPermissions(activity)) {
        //    if (!appInfo.getAPK().equals(MLManagerApplication.getProPackage())) {
                status = UtilsApp.copyFile(appInfo);
        //    } else {
            //    status = UtilsApp.extractMLManagerPro(context, appInfo);
         //   }
        }

        return status;
    }

    @Override
    protected void onPostExecute(Boolean status) {
        super.onPostExecute(status);
      /*  dialog.dismiss();*/
        if (status) {
       //     UtilsDialog.showSnackbar(activity, String.format(context.getResources().getString(R.string.dialog_saved_description), appInfo.getName(), UtilsApp.getAPKFilename(appInfo)), context.getResources().getString(R.string.button_undo), UtilsApp.getOutputFilename(appInfo), 1).show();

        //    Snackbar.make(this,""+String.format(context.getResources().getString(R.string.dialog_saved_description), appInfo.getName(), UtilsApp.getAPKFilename(appInfo)),1000);
            View v= LayoutInflater.from(context).inflate(R.layout.activity_selected_application,null);
           CoordinatorLayout codinatorLayout = (CoordinatorLayout)v.findViewById(R.id.co);
            Snackbar.make(codinatorLayout, String.format(context.getResources().getString(R.string.dialog_saved_description), appInfo.getAppName(), UtilsApp.getAPKFilename(appInfo)), Snackbar.LENGTH_LONG)
                    .setAction("Undo", clickListener ).show();
            Toast.makeText(context, " String.format(context.getResources().getString(R.string.dialog_saved_description), appInfo.getName(), UtilsApp.getAPKFilename(appInfo))", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.dialog_extract_fail)+context.getResources().getString(R.string.dialog_extract_fail_description), Toast.LENGTH_LONG).show();
        }
    }

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            UtilsApp.getOutputFilename(appInfo).delete();
        }
    };
}