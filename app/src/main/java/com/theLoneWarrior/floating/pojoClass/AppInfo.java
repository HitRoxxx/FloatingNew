package com.theLoneWarrior.floating.pojoClass;

import android.net.Uri;

public class AppInfo {
    private String appName = "";
    private String pName = "";
    public boolean checked;
    private Uri bitmapString ;
    private String source;
    private String data;
    private Boolean system;

    // default constuctor
    public AppInfo() {

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

    public String getSource() {
        return source;
    }

    public String getData() {
        return data;
    }
    public Boolean isSystem() {
        return system;
    }

    public String toString() {
        return getAppName() + "##" + getPacName() + "##" + getSource() + "##" + getData() + "##" + isSystem();
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public void setSystem(Boolean system) {
        this.system = system;
    }


}
