package com.golanlan.justintime;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.steamcrafted.lineartimepicker.dialog.LinearTimePickerDialog;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Calendar;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    private EditText editTime;
    private ConstraintLayout constraintLayout3;
    private LocalTime selectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        editTime = findViewById(R.id.editTime);
        constraintLayout3 = findViewById(R.id.constraintLayout3);
        editTime.setOnClickListener(this);
        final TextView textTime = findViewById(R.id.textTime);

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

                                String time = "hh:mm:ss"; // 12:00
                                textTime.setText(DateFormat.format(time, noteTS));
                            }
                        });
                    }
                } catch (InterruptedException ignored) {
                }
            }
        };

        t.start();


    }


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
