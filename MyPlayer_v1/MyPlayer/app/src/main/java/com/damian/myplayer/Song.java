package com.damian.myplayer;

import android.media.Image;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by Damian on 12/28/2016.
 */
public class Song {
    private long id;
    private String title,artist;
    private String imgPath;

    public Song(long x,String t,String p){
        id=x;
        title=t;
        artist=p;
        imgPath=null;
    }
    public Song(long x, String t, String p, String a){
        this(x,t,p);
        imgPath=a;
    }
    public String getTitle(){return title;}
   public String getArtist(){return artist;}
    public long getId(){return id;}
    public String getImgPath(){return imgPath;}

}
