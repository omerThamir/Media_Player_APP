package com.omar.myapps.mediaplayer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import java.util.concurrent.TimeUnit;


public class Utils {

    public static boolean IsOneByOneLoop, isRandomLoop=false;
    public static boolean isItSelfLoop=false;

    public static int Add2n(int n1, int n2) {
        return n1 + n2;
    }


    public static String convertMediaPalyerDurationToTimeString(int mediaPlayer_duration) {


        String time = String.format("%02d:%02d ",
                TimeUnit.MILLISECONDS.toMinutes(mediaPlayer_duration),
                TimeUnit.MILLISECONDS.toSeconds(mediaPlayer_duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mediaPlayer_duration)));
        return time;
    }


}
