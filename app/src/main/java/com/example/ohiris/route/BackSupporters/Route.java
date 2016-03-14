package com.example.ohiris.route.BackSupporters;

public class Route {

    private long userId;
    private int routeId;
    private long time;
    private double distance;
    private double speed;

    private String name;
    private Boolean share;

    private String date;

    public Route(){

    }

    public Route(long userId, int routeId, long time, double distance, double speed, String name, Boolean share, String date){
        this.userId = userId;
        this.routeId = routeId;
        this.time = time;
        this.distance = distance;
        this.speed = speed;
        this.name = name;
        this.share = share;
        this.date = date;
    }

    public long getUserId() {
        return userId;
    }

    public int getRouteId() {
        return routeId;
    }

    public long getTime() {
        return time;
    }

    public double getDistance() {
        return distance;
    }

    public double getSpeed() {
        return speed;
    }

    public String getName() {
        return name;
    }

    public Boolean getShare() {
        return share;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShare(Boolean share) {
        this.share = share;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }
}
