package com.getrent.timer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.getrent.timer.database.DBConnection;
import com.getrent.timer.recever.NotificationActionReceiver;
import com.getrent.timer.recever.TimerReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.getrent.timer.AppConstants.RUNNING;
import static com.getrent.timer.AppConstants.SHARE;
import static com.getrent.timer.AppConstants.TOTAL;
import static com.getrent.timer.AppConstants.WALCK;
import static com.getrent.timer.AppConstants.runingTime;
import static com.getrent.timer.AppConstants.secondsRemaining;

public class MainActivity extends AppCompatActivity {
    private static final long START_TIME_IN_MILLIS = 0;
    private TextView mTextViewCountDown;
    private FloatingActionButton startPause;
    private FloatingActionButton stop;
    private FloatingActionButton fab_List;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private MaterialProgressBar progress_countdown;
    private int totalProgress,runingProgress;
    private String state="";
    private String TAG = "ACIVITYLIFECYCLE";
    private long nowSeconds;
    private NotificationUtil notificationUtil;
    private DBConnection sqliteDB;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy/hh:mm a", Locale.getDefault());
    private String submit;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificationUtil = new NotificationUtil();
        sqliteDB = new DBConnection(this);
        Log.e(TAG,"onCreate");
    }

    public long setAlarm(Context context){

        long wakeUpTime = (nowSeconds + runingProgress) * 1000;
        AlarmManager alarmManager =(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent =new Intent(context, NotificationActionReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent);

        return wakeUpTime;
    }

    public void removeAlarm(Context context){
        Intent intent =new Intent(context, TimerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pauseTimer();

    }


    @SuppressLint("RestrictedApi")
    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG,"onStart");

        preferences = getSharedPreferences(SHARE,MODE_PRIVATE);
        editor = preferences.edit();


        nowSeconds = Calendar.getInstance().getTimeInMillis() / 1000;
        mTextViewCountDown = findViewById(R.id.textViewCountdown);
        progress_countdown = findViewById(R.id.progress_countdown);

        startPause = findViewById(R.id.fab_start);
        stop = findViewById(R.id.fab_stop);
        fab_List = findViewById(R.id.fab_List);

        mTextViewCountDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mTimerRunning) {
                    showDailog();
                }
            }
        });

        startPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimeLeftInMillis == 0){
                    Toast.makeText(MainActivity.this, "Please seat the time.", Toast.LENGTH_LONG).show();
                }else {
                    if (mTimerRunning) {
                        pauseTimer();
                    } else {
                        startTimer();
                    }
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
        fab_List.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimerRunning) {

                    long recordtime = (totalProgress -runingProgress);
                    String message = sqliteDB.addData(submit,String.valueOf(recordtime));
                    Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                }else {

                    Intent intent = new Intent(MainActivity.this, TimerListActivity.class);
                    startActivity(intent);
                }
            }
        });
        fab_List.setVisibility(View.VISIBLE);
        fab_List.setImageResource(R.drawable.ic_list_numb);
        updateCountDownText();


    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (state.equals("onPause")){
//            startTimer();
//            state="onResume";
//        }

        if (state.contains("Stop")){
            removeAlarm(this);
            notificationUtil.hideTimerNotification(this);
        }

        Log.e(TAG,"onResume");
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onPause() {
        super.onPause();
//        pauseTimer();
//        state="onPause";
        if (mTimerRunning){
            secondsRemaining = runingProgress;
            AppConstants.nowSeconds = nowSeconds;

            long wakeUpTime = setAlarm(this);
            notificationUtil.showTimerRunning(MainActivity.this);
            state = "onPause";
            Log.i(TAG,"onPause");
        }
        fab_List.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
    }

    @SuppressLint("RestrictedApi")
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
                editor.putString(RUNNING, String.valueOf(runingProgress));
                editor.commit();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                startPause.setImageResource(R.drawable.ic_play);
                sqliteDB.addData(submit,String.valueOf(totalProgress));
                notificationUtil.showTimerExpired(MainActivity.this);
                fab_List.setImageResource(R.drawable.ic_list_numb);
            }
        }.start();
        mTimerRunning = true;
        submit = dateFormat.format(Calendar.getInstance().getTime());
        startPause.setImageResource(R.drawable.ic_pause);
        fab_List.setVisibility(View.VISIBLE);
        fab_List.setImageResource(R.drawable.ic_playlist_add);
    }


    @SuppressLint("RestrictedApi")
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        startPause.setImageResource(R.drawable.ic_play);
        fab_List.setVisibility(View.GONE);
        editor.putString(RUNNING, String.valueOf(runingProgress));
        editor.commit();

    }

    @SuppressLint("RestrictedApi")
    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateCountDownText();
        startPause.setImageResource(R.drawable.ic_play);
        fab_List.setVisibility(View.VISIBLE);
        fab_List.setImageResource(R.drawable.ic_list_numb);
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        runingProgress =(int) (mTimeLeftInMillis/1000);
        progress_countdown.setProgress(runingProgress);

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        runingTime= timeLeftFormatted;
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void showDailog(){
        View view = View.inflate(MainActivity.this, R.layout.time_dialog, null);
        Button cancel = view.findViewById(R.id.cancel);
        Button ok = view.findViewById(R.id.ok);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);

        final NumberPicker numberPickerMinutes = view.findViewById(R.id.numpicker_minutes);
        numberPickerMinutes.setMaxValue(59);

        final NumberPicker numberPickerSeconds = view.findViewById(R.id.numpicker_seconds);
        numberPickerSeconds.setMaxValue(59);

        final AlertDialog alertDialog = builder.create();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimeLeftInMillis = (numberPickerMinutes.getValue()*60000)+(numberPickerSeconds.getValue()*1000);
                updateCountDownText();
                totalProgress = (int) (mTimeLeftInMillis/1000);
                setProgressBarValues();
                editor.putString(TOTAL, String.valueOf(totalProgress));
                editor.commit();
                editor.apply();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void setProgressBarValues() {
        progress_countdown.setMax(totalProgress);
        progress_countdown.setProgress(runingProgress);
    }

    public void callShare(){
        totalProgress = Integer.parseInt(preferences.getString(TOTAL, ""));
        runingProgress = Integer.parseInt(preferences.getString(RUNNING, ""));
        nowSeconds = Long.parseLong(preferences.getString(WALCK, ""));
        Toast.makeText(this, runingProgress+" "+totalProgress, Toast.LENGTH_SHORT).show();
    }


}
