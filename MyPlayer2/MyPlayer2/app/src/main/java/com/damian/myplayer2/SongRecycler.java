package com.damian.myplayer2;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Damian on 12/30/2016.
 */
public class SongRecycler extends RecyclerView.Adapter<SongRecycler.SongViewHolder> {

    private Context ctx;
    private ArrayList<Song> songList;
    private static MusicService musicService;


    public SongRecycler(Context c, ArrayList a){
        ctx=c;
        songList=a;
        System.out.println("INSIDE CTOR OF SOngRecycyler");


    }
    public void setMusicService(MusicService m){
        musicService=m;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(ctx).inflate(R.layout.song_recycler_item,parent,false);
        System.out.println("inside oncreateViewHolder");

        return new SongViewHolder(view,songList,ctx);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        System.out.println("Inside onBindViewHolder");

        Song currSong=songList.get(position);
        System.out.println("binding "+currSong.getTitle());
        if(currSong.getImgPath()!=null) {
            System.out.println("imgPath of "+currSong.getTitle()+" IS "+currSong.getImgPath());
            holder.albumArt.setImageBitmap(BitmapFactory.decodeFile(currSong.getImgPath()));
            System.out.println("image to albumArt just got set");
        }
        else
        holder.albumArt.setImageResource(R.drawable.notfound);


        holder.songName.setText(currSong.getTitle());
        holder.artistName.setText(currSong.getArtist());

        System.out.println("Leaving onBindViewHolder");

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }




    public static class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView songName,artistName;
        ImageView albumArt;
        ArrayList<Song> tempSong;
        Context ctx;


        SongViewHolder(View view,ArrayList<Song> arrayList,Context c){
            super(view);
            view.setOnClickListener(this);
            tempSong=arrayList;
            ctx=c;
            songName=(TextView)view.findViewById(R.id.songTitle);
            artistName=(TextView)view.findViewById(R.id.artistName);
            albumArt=(ImageView) view.findViewById(R.id.songImg);

        }


        @Override
        public void onClick(View view){
            int position=getAdapterPosition();
            Song clickedSong=tempSong.get(position);
            Toast.makeText(ctx,"You just clicked on "+clickedSong.getTitle(),Toast.LENGTH_SHORT).show();
            SongRecycler.musicService.setSongPosition(position);
            SongRecycler.musicService.playSong();


        }

    }









}
