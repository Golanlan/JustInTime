package com.golanlan.justintime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shawnlin.numberpicker.NumberPicker;
import com.suke.widget.SwitchButton;

import net.steamcrafted.lineartimepicker.dialog.LinearTimePickerDialog;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Calendar;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    private EditText editTime;
    private ConstraintLayout constraintLayout3;
    private LocalTime selectedTime;
    Calendar calendar = null;
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private NumberPicker numPickMins;
    private TextView textAlarmTime;
    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        SwitchButton switchButton = findViewById(R.id.switchButton);
        constraintLayout3 = findViewById(R.id.constraintLayout3);
        textAlarmTime = findViewById(R.id.textAlarmTime);

        // https://github.com/ShawnLin013/NumberPicker
        // Values for minutes picker
        numPickMins = findViewById(R.id.numPickMins);
        numPickMins.setValue(5);
        numPickMins.setMaxValue(60);

        calendar = Calendar.getInstance();

        editTime = findViewById(R.id.editTime);
        editTime.setOnClickListener(this);
        final TextView textTime = findViewById(R.id.textTime);
        time = "hh:mm:ss";

        // A clock with seconds on the main page.
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                java.util.Date noteTS = Calendar.getInstance().getTime();
                                textTime.setText(DateFormat.format(time, noteTS));
                            }
                        });
                    }
                } catch (InterruptedException ignored) {
                }
            }
        };
        t.start();

        // https://github.com/zcweng/SwitchButton
        // When switch changes, start/stop alarm
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                String mEditTime = editTime.getText().toString();
                if (isChecked) {
                    if (mEditTime.matches("")) {
                        Toast.makeText(MainActivity2.this, "You must enter a deadline", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("MainActivity", "Alarm On");

                        Intent myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
                        pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, myIntent, 0);

                        LocalTime timeForAlert = selectedTime.minusMinutes(numPickMins.getValue());

                        calendar.set(Calendar.HOUR_OF_DAY, timeForAlert.getHourOfDay());
                        calendar.set(Calendar.MINUTE, timeForAlert.getMinuteOfHour());
                        calendar.set(Calendar.SECOND, 0);

                        textAlarmTime.setText(DateFormat.format(time, calendar.getTimeInMillis()));

                        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        }
                    }
                } else {
                    if (mEditTime.matches("")) {

                    } else {
                        alarmManager.cancel(pendingIntent);
                        Log.d("MainActivity", "Alarm Off");
                    }
                }
            }
        });
        // TODO: 4/2/2018 change alarm time when numPickMins changes
    }

    //
    @Override
    public void onClick(View view) {
        LinearTimePickerDialog dialog = LinearTimePickerDialog.Builder.with(this)
                .setShowTutorial(false)
                // TODO: 4/2/2018 tutorial=true, Use sharedPreferences to put flag that dialog opened, and change tutorial to false.

                .setButtonCallback(new LinearTimePickerDialog.ButtonCallback() {
                    @Override
                    public void onPositive(DialogInterface dialog, int hour, int minutes) {
                        selectedTime = new LocalTime(hour, minutes);
                        editTime.setText(selectedTime.toString(DateTimeFormat.shortTime()) + "");
                        constraintLayout3.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNegative(DialogInterface dialog) {
                        Toast.makeText(MainActivity2.this, "You need to tell us the deadline so we could proceed", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        dialog.show();
    }
}
