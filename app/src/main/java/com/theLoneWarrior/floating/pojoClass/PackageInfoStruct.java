package com.theLoneWarrior.floating.pojoClass;

public class PackageInfoStruct {
    private String appName = "";
    private String pName = "";
    public boolean checked;
    private String bitmapString = "";

    // default constuctor
    public PackageInfoStruct() {

    }


    public void setBitmapString(String bitmapString) {
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

    public String getBitmapString() {
        return bitmapString;
    }


    public String getPacName() {
        return pName;
    }


}
