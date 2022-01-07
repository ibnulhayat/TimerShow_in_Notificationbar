package com.getrent.timer.entity;

import android.provider.BaseColumns;

public class TimerEntity implements BaseColumns {

    public static String TABLE_NAME = "timerEntity";

    public static String START_TIME = "startTime";
    public static String STOP_TIME = "stopTime";

    public static String TABLE_UPGRADE="DROP IF EXISTS "+TABLE_NAME;
}