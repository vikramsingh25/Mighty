package com.example.sankalp.muxicplayer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.sankalp.muxicplayer.MainActivity;
import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.mightyDB.MightyContract;
import com.example.sankalp.muxicplayer.mightyDB.MightyDbHelper;

/**
 * Created by sankalp on 9/2/2016.
 */
public class SplashFragment extends Fragment {
    TextView textView;
    Animation animation;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_splash,container,false);
        textView= (TextView) view.findViewById(R.id.mighty);
        textView.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getActivity().finish();
                Intent intent=new Intent(getContext(),MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animation= AnimationUtils.loadAnimation(getContext(),R.anim.clockwise);
//        new MightyDbHelper(getContext()).getWritableDatabase().rawQuery("ALTER TABLE " +
//                MightyContract.SongEntry.TABLE_NAME + " AUTOINCREMENT=0",null);
//        Handler handler=new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                getActivity().finish();
//                Intent intent=new Intent(getContext(),MainActivity.class);
//                startActivity(intent);
//            }
//        },2000);
    }
}
