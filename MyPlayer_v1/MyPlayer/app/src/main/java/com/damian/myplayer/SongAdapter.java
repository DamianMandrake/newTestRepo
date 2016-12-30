package com.damian.myplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Damian on 12/29/2016.
 */
public class SongAdapter extends BaseAdapter {
    Context ctx;
    private ArrayList<Song> songs;
    private LayoutInflater songInflater;


    public SongAdapter(Context c, ArrayList<Song> a){
        ctx=c;
        songs=a;
        songInflater=LayoutInflater.from(c);//this tells android to attatch this adapter to the view from mainActivity's context...inflaters are used to create objs of xmls with their correspondingView obj
        //note it hasnt been attatched yet... it just knows its some obj from mainActivity's context
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout relativeLayout=(RelativeLayout)songInflater.inflate(R.layout.song_adapter,parent,false);

        ImageView im=(ImageView)relativeLayout.findViewById(R.id.songImg);
        TextView title=(TextView)relativeLayout.findViewById(R.id.songTitle);
        TextView artist=(TextView)relativeLayout.findViewById(R.id.artistName);

        Song currSong=songs.get(position);
        if(currSong.getImgPath()!=null)
        im.setImageBitmap(BitmapFactory.decodeFile(currSong.getImgPath()));
        title.setText(currSong.getTitle());
        artist.setText(currSong.getArtist());

        relativeLayout.setTag(position);//setTag is used  to differentiate one view from another ... you can also you getId but this is the preferred method
        return relativeLayout;
    }

}
