package com.omar.myapps.mediaplayer;


import java.io.Serializable;

public class Song implements Serializable {

    public static int currentSongIndex = 0;
    public String name, filepath, auther;


    public String getAuther() {
        return auther;
    }

    public void setAuther(String auther) {
        this.auther = auther;
    }

    public Song(String mname, String filepath, String auther) {
        name = mname;
        this.filepath = filepath;
        this.auther = auther;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
