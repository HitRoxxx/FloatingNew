package com.theLoneWarrior.floating.pojoClass;

import android.net.Uri;

public class PackageInfoStruct {
    private String appName = "";
    private String pName = "";
    public boolean checked;
    private Uri bitmapString ;

    // default constuctor
    public PackageInfoStruct() {

    }

    public void setBitmapString(Uri bitmapString) {
        this.bitmapString = bitmapString;
    }


    public void setPacName(String pName) {

        this.pName = pName;

    }

    public void setAppName(String appName) {

        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public Uri getBitmapString() {
        return bitmapString;
    }


    public String getPacName() {
        return pName;
    }


}
