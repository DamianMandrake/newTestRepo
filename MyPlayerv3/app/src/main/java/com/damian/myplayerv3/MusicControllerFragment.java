package com.damian.myplayerv3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by damianmandrake on 1/12/17.
 */
public class MusicControllerFragment extends Fragment{


    private MusicService musicService;
    private TextView smallSongTitle,artist,songName;
    private ImageView smallAlbumArt,imageAlbumArt;
    private ImageButton prev,next;
    private ToggleButton playPause,smallPlayPause;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        ((AppCompatActivity)getActivity()).getActionBar().hide();
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.music_cotroller_frag,container,false);

        musicService= ((MainActivity)getActivity()).getMusicService();


        return view;
    }




}
