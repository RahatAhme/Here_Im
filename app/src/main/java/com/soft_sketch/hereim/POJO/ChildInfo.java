package com.soft_sketch.hereim.POJO;

public class ChildInfo {
    private String childID;
    private String childName;
    private String childNumber;
    private String soundURI;
    private boolean vibrationCode;
    private boolean recodingCode;
    private double currentLocLati;
    private double currentLocLong;


    public ChildInfo() {
    }

    public ChildInfo(String childID, String childName, String childNumber, String soundURI, boolean vibrationCode, boolean recodingCode, double currentLocLati, double currentLocLong) {
        this.childID = childID;
        this.childName = childName;
        this.childNumber = childNumber;
        this.soundURI = soundURI;
        this.vibrationCode = vibrationCode;
        this.recodingCode = recodingCode;
        this.currentLocLati = currentLocLati;
        this.currentLocLong = currentLocLong;

    }

    public String getSoundURI() {
        return soundURI;
    }

    public String getChildID() {
        return childID;
    }

    public String getChildName() {
        return childName;
    }

    public String getChildNumber() {
        return childNumber;
    }

    public boolean getVibrationCode() {
        return vibrationCode;
    }

    public boolean getRecodingCode() {
        return recodingCode;
    }

    public double getCurrentLocLati() {
        return currentLocLati;
    }

    public double getCurrentLocLong() {
        return currentLocLong;
    }

}
