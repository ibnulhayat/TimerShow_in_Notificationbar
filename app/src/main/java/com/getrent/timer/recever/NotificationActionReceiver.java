package com.getrent.timer.recever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.getrent.timer.AppConstants;
import com.getrent.timer.MainActivity;
import com.getrent.timer.NotificationUtil;

public class NotificationActionReceiver extends BroadcastReceiver {
    NotificationUtil notificationUtil;
    MainActivity mainActivity;
    @Override
    public void onReceive(Context context, Intent intent) {

        notificationUtil = new NotificationUtil();
        mainActivity = new MainActivity();

        StringBuilder sb = new StringBuilder();
        sb.append(intent.getAction());
        String action = sb.toString();


        AppConstants.state = action;
        if (action.contains(AppConstants.ACTION_STOP)){
            mainActivity.removeAlarm(context);
            notificationUtil.hideTimerNotification(context);

        }else if (action.contains(AppConstants.ACTION_PAUSE)){
            long secondsRemaining = AppConstants.secondsRemaining;
            long nowSeconds = AppConstants.nowSeconds;
            notificationUtil.showTimerPaused(context);
            long wakeUpTime = mainActivity.setAlarm(context);
            Log.e("HHHHHHHHHHHH", String.valueOf(wakeUpTime));
        }else if (action.contains(AppConstants.ACTION_RESUME)){
            long secondsRemaining = AppConstants.secondsRemaining;
            long nowSeconds = AppConstants.nowSeconds;
            long wakeUpTime = mainActivity.setAlarm(context);
            notificationUtil.showTimerRunning(context);

        }else if (action.contains(AppConstants.ACTION_START)){

            long wakeUpTime = mainActivity.setAlarm(context);
            notificationUtil.showTimerRunning(context);
        }

    }
}
