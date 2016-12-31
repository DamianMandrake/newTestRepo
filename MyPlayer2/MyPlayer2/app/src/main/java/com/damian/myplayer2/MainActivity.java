package com.damian.myplayer2;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainActivityConstants {

    ArrayList<Song> songList=new ArrayList<>();
    private Button b;
    private RecyclerView recyclerView;
    private MusicService musicService;
    private Intent musicPlayerIntent;
    private boolean isPlayerBound=false;
    boolean havePerms=false;
    private SongRecycler recyclerAdapter;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        b=(Button)findViewById(R.id.permButton);
        recyclerView=(RecyclerView)findViewById(R.id.songRecycler);



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {

            System.out.println("INSIDE perm if");



            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                System.out.println("INSIDE perm 2");//remove asap
                findSongs();


            }else
            {
                System.out.println("inside else about to turn button visibility on");//remove asap
                //show button only when perms are denied
                b.setVisibility(View.VISIBLE);


            }
        }


    }


    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            System.out.println("inside onServiceConnected of musicConnection which inits musicService");
            MusicService.MusicBinder musicBinder = (MusicService.MusicBinder) service;
            System.out.println(musicBinder.toString()+" music BINDER");
            musicService = musicBinder.getServiceInstance();
            System.out.println("MUSIC SERVICE HAS VAL "+musicService.toString());
            musicService.setSongsList(songList);

            recyclerAdapter.setMusicService(musicService);

            isPlayerBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("inside onServiceDisconnected");
            isPlayerBound = false;
        }
    };



    @Override
    protected void onStart(){
        super.onStart();
        System.out.println("in onstart and waiting");


        System.out.println("done waiting... about to search");

        if(musicPlayerIntent==null){
            System.out.println("Inside onsStart");
            musicPlayerIntent=new Intent(this,MusicService.class);
            bindService(musicPlayerIntent,musicConnection, Context.BIND_AUTO_CREATE);
            startService(musicPlayerIntent);
        }
    }






    public void askPerms(View v){
        //has to be public so that the xml code can access it
        System.out.println("about to ask for perm");
        if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
            toast(REQUIRE_PERMS);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_READ_EXTERNAL);

    }
    private void toast(String a){

        Toast.makeText(this,a,Toast.LENGTH_SHORT).show();
    }
    private void findSongs(){
        ContentResolver musicResolver=getContentResolver();
        Uri musicUri= android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor=musicResolver.query(musicUri,null,null,null,null);

        if(musicCursor!=null && musicCursor.moveToFirst()) {
            int titleCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idCol= musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistCol=musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumId=musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID);
            Cursor albumArtCursor;
            do{
                long tempId=musicCursor.getLong(idCol);
                String t=musicCursor.getString(titleCol),a=musicCursor.getString(artistCol),b=musicCursor.getString(albumId);
                albumArtCursor=musicResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,new String[]{MediaStore.Audio.Albums._ID,MediaStore.Audio.Albums.ALBUM_ART},MediaStore.Audio.Albums._ID +"=?",new String[]{b},null);
                String path=null;
                if(albumArtCursor.moveToFirst()){
                    path=albumArtCursor.getString(albumArtCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                }

                songList.add(new Song(tempId,t,a,path));

            }while(musicCursor.moveToNext());
            albumArtCursor.close();
        }
        musicCursor.close();

        b.setVisibility(View.INVISIBLE);
        System.out.println("waiting");




        toast(songList.size()+" songs retrieved ");

        handleRecyclerView();


    }

    private void handleRecyclerView(){
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        System.out.println("MUSIC SERVICE HAS VAL "+musicService);
        recyclerAdapter=new SongRecycler(this,songList);
        recyclerView.setAdapter(recyclerAdapter);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_READ_EXTERNAL:
                if(grantResults[0]!=PackageManager.PERMISSION_GRANTED)
                    toast(REQUIRE_PERMS);
                else {
                    System.out.println("ABOUT TO CALL FINDSONGS");
                    findSongs();

                }
                return;

            default:
                super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        }
    }







}

interface MainActivityConstants{
    static final int MY_READ_EXTERNAL=4;
    static final String REQUIRE_PERMS="require permissions for proper functioning";

}
