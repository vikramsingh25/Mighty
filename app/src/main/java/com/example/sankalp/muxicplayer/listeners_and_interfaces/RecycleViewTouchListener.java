package com.example.sankalp.muxicplayer.listeners_and_interfaces;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by sankalp on 2/25/2017.
 */
public class RecycleViewTouchListener implements RecyclerView.OnItemTouchListener {

    private GestureDetectorCompat gestureDetector;
    private MightyOnClickListener mightyOnClickListener;
    private MightyOnLongClickListener mightyOnLongClickListener;

    //CONSTRUCTOR
    public RecycleViewTouchListener(Context context, final RecyclerView recyclerView, final MightyOnClickListener mightyOnClickListener) {

        this.mightyOnClickListener = mightyOnClickListener;

        gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
//                    View child=recyclerView.findChildViewUnder(e.getX(),e.getY());
//                    if (child!=null && mightyOnClickListener!=null) {
//                        mightyOnClickListener.onLongClick(child,recyclerView.getChildPosition(child));
//                    }
            }

        });

    }

//    public RecycleViewTouchListener(Context context, final RecyclerView recyclerView, final MightyOnLongClickListener mightyOnLongClickListener) {
//
//        this.mightyOnLongClickListener=mightyOnLongClickListener;
//
//        gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
//
//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                return true;
//            }
//
//            @Override
//            public void onLongPress(MotionEvent e) {
//                super.onLongPress(e);
////                    View child=recyclerView.findChildViewUnder(e.getX(),e.getY());
////                    if (child!=null && mightyOnClickListener!=null) {
////                        mightyOnClickListener.onLongClick(child,recyclerView.getChildPosition(child));
////                    }
//            }
//
//        });
//
//    }


    @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if (child!=null && mightyOnClickListener !=null && gestureDetector.onTouchEvent(e)) {
                mightyOnClickListener.onClick(child,rv.getChildPosition(child));
            }
//            if (child!=null && mightyOnLongClickListener!=null && gestureDetector.onTouchEvent(e)) {
//                mightyOnLongClickListener.onLongClick(rv.getChildPosition(child));
//            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
}
