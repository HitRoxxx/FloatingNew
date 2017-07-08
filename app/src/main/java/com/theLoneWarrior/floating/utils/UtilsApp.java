package com.theLoneWarrior.floating.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.theLoneWarrior.floating.pojoClass.AppInfo;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;


public class UtilsApp {

    /**
     * Default folder where APKs will be saved
     * @return File with the path
     */
    public static File getDefaultAppFolder() {
        return new File(Environment.getExternalStorageDirectory() + "/FloSo");
    }

    /**
     * Custom folder where APKs will be saved
     * @return File with the path
     */
    public static File getAppFolder() {
     //   AppPreferences appPreferences = MLManagerApplication.getAppPreferences();
     //   return new File(appPreferences.getCustomPath());

            String filepath = Environment.getExternalStorageDirectory().getPath();
            File file = new File(filepath, "FloSo");

            if (!file.exists()) {
                file.mkdirs();
            }

            return file;

     //   return new File();
    }

    public static Boolean copyFile(AppInfo appInfo) {
        Boolean res = false;
        Log.e("App",""+appInfo.getSource());
        File initialFile = new File(appInfo.getSource());
        File finalFile = getOutputFilename(appInfo);

        try {
            FileUtils.copyFile(initialFile, finalFile);
            res = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Retrieve the name of the extracted APK
     * @param appInfo AppInfo
     * @return String with the output name
     */
    public static String getAPKFilename(AppInfo appInfo) {
    //    AppPreferences appPreferences = MLManagerApplication.getAppPreferences();
        String res;
        res = appInfo.getAppName();
      /*  switch ("2") {
            case "1":
                res = appInfo.getPacName() *//*+ "_" + appInfo.getVersion()*//*;
                break;
            case "2":
                res = appInfo.getAppName() *//*+ "_" + appInfo.getVersion()*//*;
                break;
            case "4":
                res = appInfo.getAppName() + "_" + appInfo.getPacName();
                break;
            default:
                res = appInfo.getAppName();
                break;
        }*/

        return res;
    }

    /**
     * Retrieve the name of the extracted APK with the path
     * @param appInfo AppInfo
     * @return File with the path and output name
     */
    public static File getOutputFilename(AppInfo appInfo) {
        return new File(getAppFolder().getPath() + "/" + getAPKFilename(appInfo) + ".apk");
    }



    public static Intent getShareIntent(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.setType("application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

}
