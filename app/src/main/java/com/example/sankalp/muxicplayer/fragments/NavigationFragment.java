package com.example.sankalp.muxicplayer.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sankalp.muxicplayer.AboutActivity;
import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.SettingsActivity;
import com.example.sankalp.muxicplayer.adapters.NavListAdapter;
import com.example.sankalp.muxicplayer.data.NavigationInfo;
import com.example.sankalp.muxicplayer.dialogs.RateUsDialog;
import com.example.sankalp.muxicplayer.services.MightyPlayerService;

import java.util.ArrayList;
import java.util.List;


public class NavigationFragment extends Fragment implements NavListAdapter.ClickListener{

    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    LayoutInflater inflater1;
    private RecyclerView mNavRecyclerView;
    private NavListAdapter adapter;
    int hour=0,min=0,second=60;
    public static boolean isTimerSet=false;
    public static boolean isTimerCanceled=false;
    AlertDialog.Builder dialogBuilder;
//    static int sec1=60;

    TextView runningTimeHour, runningTimeSecond, runningTimeMinute;

    public NavigationFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_navigation, container, false);
        mNavRecyclerView= (RecyclerView) view.findViewById(R.id.nav_recycler_view);
        adapter=new NavListAdapter(getContext(),getData());
        adapter.setClickListener(this);
        mNavRecyclerView.setAdapter(adapter);
        mNavRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    public static List<NavigationInfo> getData(){
        List<NavigationInfo> list=new ArrayList<>();
        int[] icons={R.drawable.ic_menu_play_clip,
                R.drawable.ic_menu_play_clip,
                R.drawable.ic_menu_play_clip,
                R.drawable.ic_menu_play_clip,
                R.drawable.ic_menu_play_clip};
        String titles[]={"Sleep timer","Settings","Share App","About","Rate Us"};
        for (int i=0;i<icons.length && i<titles.length;i++) {
            NavigationInfo currentInfo=new NavigationInfo();
            currentInfo.navIconId=icons[i];
            currentInfo.navTilte=titles[i];
            list.add(currentInfo);
        }
        return list;
    }

    public void setUp(DrawerLayout drawerLayout, Toolbar toolbar) {
        mDrawerLayout=drawerLayout;
        mDrawerToggle=new ActionBarDrawerToggle(getActivity(),drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }

    @Override
    public void itemClicked(View view, int position) {

        switch ((position)){
            case 0:  //sleep timer
                showTimerDialog(view);
                break;
            case 1:   //settings
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;
            case 2:   //share app
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,"Hey,download this app..!");
                startActivity(Intent.createChooser(sharingIntent,"Share using"));
                break;
            case 3:   //about
                Intent intent1 = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent1);
                break;
            case 4:  //rating
                showRateUsDialog();
                break;
        }
        mDrawerLayout.closeDrawers();
    }

    void showRateUsDialog(){
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        RateUsDialog dialog=new RateUsDialog();
        dialog.show(ft,"RateUsDialog");
    }


    void showTimerDialog(View view){

        if (!isTimerSet) {

            LayoutInflater timerInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = timerInflater.inflate(R.layout.timer_dialog, null, false);
            NumberPicker noPicker1 = (NumberPicker) view.findViewById(R.id.noPicker1);
            NumberPicker noPicker2 = (NumberPicker) view.findViewById(R.id.noPicker2);
            noPicker1.setMinValue(0);
            noPicker1.setMaxValue(23);
            noPicker2.setMinValue(0);
            noPicker2.setMaxValue(59);
            noPicker1.setWrapSelectorWheel(true);
            noPicker2.setWrapSelectorWheel(true);
            noPicker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    hour = newVal;
                }
            });

            noPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    min = newVal;
                }
            });
            //timer dialog
            dialogBuilder = new AlertDialog.Builder(getContext());
            dialogBuilder.setView(view)
                    .setCancelable(true)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int total;
                            Toast.makeText(getContext(), "Sleep after " + hour + " : " + min, Toast.LENGTH_SHORT).show();
                            total = hour * 60 * 60 + min * 60;
                            second=60;
                            isTimerSet=true;
                            isTimerCanceled=false;
                            timeLeft(total);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create()
                    .show();
        } else {
            LayoutInflater inflater= (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.timer_running_dialog,null,false);
            runningTimeHour= (TextView) view.findViewById(R.id.running_timer_hour);
            runningTimeMinute = (TextView) view.findViewById(R.id.running_timer_minute);
            runningTimeSecond = (TextView) view.findViewById(R.id.running_timer_second);
            //running timer dialog
            dialogBuilder=new AlertDialog.Builder(getContext());
            dialogBuilder.setView(view)
                    .setCancelable(true)
                    .setTitle("Time Remaining")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("Cancel Timer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isTimerCanceled=true;
                            hour=0;
                            min=0;
                            second=0;
                            isTimerSet=false;
                            Toast.makeText(getContext(),"Sleep Timer Canceled", Toast.LENGTH_SHORT).show();
                        }
                    }).create()
                    .show();
        }
    }

    public void timeLeft(int total){

        if (!isTimerCanceled) {

            if (total--!=0) {
                    second--;
                Handler handler = new Handler();
                final int finalTotal = total;
                final String hourStr = this.hour<=9 ? "0"+String.valueOf(this.hour) : String.valueOf(this.hour);
                final String minStr = min<=9 ? "0"+String.valueOf(min-1) : String.valueOf(min-1);
                final String secStr = second<=9 ? "0"+String.valueOf(second) : String.valueOf(second);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                            if (runningTimeHour != null && runningTimeMinute != null && runningTimeSecond != null) {
                                runningTimeHour.setText(hourStr);
                                runningTimeMinute.setText(":" + minStr + ":");
                                runningTimeSecond.setText(secStr);
                                if (second==0 && min != 0) {
                                    min--;
                                    second=60;
                                    if (min == 0 && hour != 0) {
                                        hour--;
                                        min=60;
                                    }
                                }
                            }
                            timeLeft(finalTotal);
                        }
                    }, 1000);
            } else {
                MightyPlayerService service = new MightyPlayerService();
                service.stopSelf();
                isTimerSet=false;
                isTimerCanceled=true;
                getActivity().finish();
                getActivity().moveTaskToBack(true);
            }
        }
    }



}
