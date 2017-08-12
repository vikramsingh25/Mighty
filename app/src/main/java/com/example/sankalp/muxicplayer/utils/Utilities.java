package com.example.sankalp.muxicplayer.utils;

/**
 * Created by sankalp on 10/8/2016.
 */
public class Utilities {

    public static String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";
        String minutesString = "";


        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            if (hours > 10) {
                finalTimerString = hours + ":";
            } else {
                finalTimerString = "0"+hours + ":";
            }
        }

        if(minutes > 0){
            if (minutes > 10) {
                minutesString = minutes + ":";
            } else {
                minutesString = "0"+minutes+ ":";
            }
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutesString + secondsString;

        // return timer string
        return finalTimerString;
    }
    public static int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        // return percentage
        return percentage.intValue();
    }

    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }


    public String[] splitString(String str){
        return str.split("\\s");
    }
}
