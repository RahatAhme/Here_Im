package com.soft_sketch.hereim;

public class PoliceStation {
    private String id;
    private String name;
    private String number;
    private double policeStationLat;
    private double policeStationLong;

    public PoliceStation(String id, String name, String number, double policeStationLat, double policeStationLong) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.policeStationLat = policeStationLat;
        this.policeStationLong = policeStationLong;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public double getPoliceStationLat() {
        return policeStationLat;
    }

    public double getPoliceStationLong() {
        return policeStationLong;
    }
}
