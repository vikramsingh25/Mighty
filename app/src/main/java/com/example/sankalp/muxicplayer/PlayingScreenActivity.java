package com.example.sankalp.muxicplayer;

import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.sankalp.muxicplayer.fragments.PlayScreenFragment;
import com.example.sankalp.muxicplayer.fragments.PlayingQueueFragment;
import com.example.sankalp.muxicplayer.utils.SlidingTabLayout;

public class PlayingScreenActivity extends AppCompatActivity {

    private ViewPager mViewPager;
//    private SlidingTabLayout mTabLayout;
    private Toolbar mMainToolbar;
    PlayScreenFragment playScreenFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_playing_screen);

//        mMainToolbar = (Toolbar) findViewById(R.id.playing_app_bar);
        setSupportActionBar(mMainToolbar);
//        mTabLayout = (SlidingTabLayout) findViewById(R.id.playingTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.playingPager);
        mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
//        mTabLayout.setViewPager(mViewPager);

        playScreenFragment = new PlayScreenFragment();
        Bundle bundle=new Bundle();
        bundle.putString("songTitle",getIntent().getExtras().getString("TrackName"));
        bundle.putString("songArtist",getIntent().getExtras().getString("TrackArtist"));
        playScreenFragment.setArguments(bundle);
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.container_playing_screen,playScreenFragment)
//                .commit();
    }

    public class PagerAdapter extends FragmentStatePagerAdapter{

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return playScreenFragment;
                case 1:
                    return new PlayingQueueFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            return super.getPageTitle(position);
//        }
    }

    public void setSongCredentials(String title, String artist, String duration,String thumbnail){
        if (PlayScreenFragment.songTitle!=null && PlayScreenFragment.songArtist!=null && PlayScreenFragment.songDuration!=null) {
            PlayScreenFragment.songTitle.setText(title);
            PlayScreenFragment.songArtist.setText(artist);
            PlayScreenFragment.songDuration.setText(duration);
            if (thumbnail!=null) {
                PlayScreenFragment.albumArt.setImageBitmap(BitmapFactory.decodeFile(thumbnail));
            }
        }
    }
}
