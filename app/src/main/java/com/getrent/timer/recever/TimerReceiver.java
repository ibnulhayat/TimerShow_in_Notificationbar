package com.getrent.timer.recever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.getrent.timer.NotificationUtil;

public class TimerReceiver extends BroadcastReceiver {
    NotificationUtil notificationUtil;
    @Override
    public void onReceive(Context context, Intent intent) {
        notificationUtil = new NotificationUtil();
        notificationUtil.showTimerExpired(context);
    }

}
