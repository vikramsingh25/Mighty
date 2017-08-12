package com.example.sankalp.muxicplayer;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

public class Timer extends AppCompatActivity {
    NumberPicker noPicker1;
    NumberPicker noPicker2;
    int hour=0,min=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        noPicker1=(NumberPicker)findViewById(R.id.noPicker1);
        noPicker2=(NumberPicker)findViewById(R.id.noPicker2);
        noPicker1.setMinValue(0);
        noPicker1.setMaxValue(23);
        noPicker2.setMinValue(0);
        noPicker2.setMaxValue(59);
        noPicker1.setWrapSelectorWheel(true);
        noPicker2.setWrapSelectorWheel(true);
        noPicker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                hour=newVal;
            }
        });

        noPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                min=newVal;
            }
        });
    }

    public void onBtnSetTimer(View view) {
        long hour1,min1,total;
        Toast.makeText(Timer.this, "Sleep after " + hour + " : " + min, Toast.LENGTH_SHORT).show();
        hour1=hour*60*60;
        min1=min*60;
        total=hour1+min1;
        Intent intent1 = new Intent(this, MainActivity.class);
        startActivity(intent1);
        timeLeft(total);
    }

    public void timeLeft(long total){
        if(total!=0)
        {
            total--;
            Handler handler=new Handler();
            final long finalTotal = total;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    timeLeft(finalTotal);
                }
            },1000);

        }
        else
        {
            finish();
            moveTaskToBack(true);
        }
    }
}