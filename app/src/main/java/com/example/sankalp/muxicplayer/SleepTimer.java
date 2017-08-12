package com.example.sankalp.muxicplayer;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SleepTimer extends AppCompatActivity {
    private TimePicker timePicker;
    int hour1, min1;
    Calendar calendar;
    private String format;
    int hour, min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_timer);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(false);
        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MONTH);
    }

    private void showTime(int hour, int min) {
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12)
            format = "PM";
        else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else
            format = "AM";
        Toast.makeText(SleepTimer.this, "Sleep at " + hour + " : " + min + " " + format, Toast.LENGTH_SHORT).show();

        //code for sleep

    }

    public void setTime(View view) {
        hour = timePicker.getCurrentHour();
        min = timePicker.getCurrentMinute();
        showTime(hour, min);

    }




    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case (999):
                // set time picker as current time
                return new TimePickerDialog(this,
                        timePickerListener, hour, min, false);

        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    hour1 = selectedHour;
                    min1 = selectedMinute;
                   /* if(hour1==hour && min1==min)
                    {
                                 finish();
                    }*/
                }
            };
}
