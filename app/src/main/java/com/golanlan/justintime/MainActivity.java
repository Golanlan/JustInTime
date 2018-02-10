package com.golanlan.justintime;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.joda.time.LocalTime;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static MainActivity inst;
    AlarmManager alarmManager;
    Calendar calendar = null;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private TextView alarmTextView;

    public static MainActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmTimePicker = findViewById(R.id.alarmTimePicker);
        alarmTextView = findViewById(R.id.alarmText);

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onToggleClicked(View view) {
        if (((ToggleButton) view).isChecked()) {
            Log.d("MainActivity", "Alarm On");

            Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, myIntent, 0);

            calendar = Calendar.getInstance();

            int hour = alarmTimePicker.getHour();
            int minute = alarmTimePicker.getMinute();

            LocalTime currentTime = new LocalTime(hour, minute);
            LocalTime timeForAlert = currentTime.minusMinutes(5);

            calendar.set(Calendar.HOUR_OF_DAY, timeForAlert.getHourOfDay());
            calendar.set(Calendar.MINUTE, timeForAlert.getMinuteOfHour());

            Toast.makeText(inst, "Alarm set to " + timeForAlert.toString(), Toast.LENGTH_SHORT).show();

            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);

        } else {
            alarmManager.cancel(pendingIntent);
            setAlarmText("");
            Log.d("MainActivity", "Alarm Off");
        }
    }

    public void setAlarmText(String alarmText) {
        alarmTextView.setText(alarmText);
    }

}