package com.getrent.timer.model;

public class TimerList {

    private String id;
    private String startTime;
    private String stopTime;

    public TimerList(String id, String startTime, String stopTime) {
        this.id = id;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public String getId() {
        return id;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getStopTime() {
        return stopTime;
    }
}
