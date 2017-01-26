package com.damian.myplayerv3;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
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
import android.widget.MediaController;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainActivityConstants,MediaController.MediaPlayerControl {

    ArrayList<Song> songList=new ArrayList<>();
    private Button b;
    private RecyclerView recyclerView;
    private MusicService musicService;
    private Intent musicPlayerIntent;
    //private MediaController musicController;
    private boolean isPlayerBound=false;
    private MusicControllerFragment musicControllerFragment;

    private SongListCompressBackTask backTask;

    @Override
    protected void onStart(){
        super.onStart();
        System.out.println("in onstart and waiting");


        System.out.println("done waiting... about to search");

        if(musicPlayerIntent==null){
            System.out.println("Inside onsStart");
            musicPlayerIntent=new Intent(this,MusicService.class);
            System.out.println("about spawn thread to bind service");
            boolean b=bindService(musicPlayerIntent, musicConnection, Context.BIND_AUTO_CREATE);
            System.out.println("about to startService " + b);
            if(( SongListCompressBackTask.isDone=b))
            {
                System.out.println("Music service was just inited");
                startService(musicPlayerIntent);
                if(backTask!=null){
                    System.out.println("backTask is not null ... going to init musicHolder");
                    backTask.setMusicService(musicService);
                }
            }
        }

    }







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("setting layout");
        setContentView(R.layout.activity_main);

        //musicController=(MediaController)findViewById(R.id.mediaController);
        b=(Button)findViewById(R.id.permButton);
        recyclerView=(RecyclerView)findViewById(R.id.songRecycler);




        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {

            System.out.println("INSIDE perm if");



            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                System.out.println(" permissions had been granted before about to start async");
                b.setVisibility(View.INVISIBLE);
                //remove asap
                backTask=new SongListCompressBackTask(this,recyclerView);//add an obj of backtask
                backTask.execute();
                //setMediaController();
                //setMusicController();


            }else
            {
                System.out.println("inside else about to turn button visibility on");//remove asap
                //show button only when perms are denied
                b.setVisibility(View.VISIBLE);


            }
        }
        //this takes 2 onclick listeners



    }
    //public MediaController getMusicController(){
        //return musicController;
    //}



    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            System.out.println("inside onServiceConnected of musicConnection which inits musicService");
            MusicService.MusicBinder musicBinder = (MusicService.MusicBinder) service;
            System.out.println(musicBinder.toString()+" music BINDER");
            musicService = musicBinder.getServiceInstance();
            System.out.println("MUSIC SERVICE HAS VAL " + musicService.toString());
            musicService.setSongsList(songList);
            // add progress bar here



            isPlayerBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("inside onServiceDisconnected");
            isPlayerBound = false;

        }
    };

    public MusicService getMusicService(){
        return musicService;
    }
    /*public void  setMusicController(){//this obj was of type MusicConroller which extended MediaController... the person said he did so to prevent it from hiding... gonna check it out... setting obj to MediaController and pointing to a view from the layout
        System.out.println("Setting music controller");
        musicController=new MusicController(this);
        musicController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playNext();//to be defined

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPrev();

            }
        });
        System.out.println("settingMediaPlayer");
        musicController.setMediaPlayer(this);
        System.out.println("settinAnchorView");
        musicController.setAnchorView(findViewById(R.id.mediaController));
        System.out.println("setEnabled");
        musicController.setEnabled(true);

    }*/

   /* void setMediaController(){
        System.out.println("initializing mediaController");
        //musicController.setVisibility(View.VISIBLE);
        musicController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playNext();//to be defined

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPrev();

            }
        });System.out.println("settingMediaPlayer");
        musicController.setMediaPlayer(this);
        System.out.println("settinAnchorView");
        musicController.setAnchorView(findViewById(R.id.songRecycler));
        System.out.println("setEnabled");
        musicController.setEnabled(true);

    }*/

    private void playNext(){
        musicService.playNext();
        //musicController.show(0);
    }
    private void playPrev(){
        musicService.playPrevious();
        //musicController.show(0);
    }










    public void askPerms(View v){
        //has to be public so that the xml code can access it
        System.out.println("about to ask for perm");
        if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            toast(REQUIRE_PERMS);
        System.out.println("about to get into activity compat");
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_READ_EXTERNAL);

    }
    private void toast(String a){

        Toast.makeText(this, a, Toast.LENGTH_SHORT).show();
    }






    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_READ_EXTERNAL:
                if(grantResults[0]!=PackageManager.PERMISSION_GRANTED) {
                    toast(REQUIRE_PERMS);
                    b.setVisibility(View.VISIBLE);
                }
                else {
                    System.out.println("ABOUT TO CALL AsyncThread");
                    b.setVisibility(View.INVISIBLE);
                    backTask=new SongListCompressBackTask(this,recyclerView);   //make backtask obj and excute...
                    backTask.execute();
                    //setMediaController();
                    //setMusicController();

                }
                return;

            default:
                super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        deleteCompressedImages();
        if(isPlayerBound){
            unbindService(musicConnection);
            isPlayerBound=false;
        }
    }

    void deleteCompressedImages() {

        File[] f = externalParentDir.listFiles();
        if (f != null)
            for (File x : f) {
                System.out.println("deleting " + x.getName());
                System.out.println(x.delete());
            }
        System.out.println("in on destroy prolly deleted files/folders");

    }


    //ovverriding methods of mediaplayercontrol to controll my playback
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if( musicService!=null && isPlayerBound && musicService.isPlaying())
            return musicService.getPosition();

        return 0;
    }

    @Override
    public int getDuration() {
        if(musicService!=null && isPlayerBound && musicService.isPlaying())
            return musicService.getDuration();

        return 0;
    }

    @Override
    public boolean isPlaying() {
        if(musicService!=null && isPlayerBound && musicService.isPlaying())
            return musicService.isPlaying();

        return false;
    }

    @Override
    public void pause() {
        musicService.pause();
    }

    @Override
    public void seekTo(int i) {
        musicService.seekTo(i);
    }

    @Override
    public void start() {
        musicService.startPlaying();

    }


}

interface MainActivityConstants{
    static final int MY_READ_EXTERNAL=4;
    static final String REQUIRE_PERMS="require permissions for proper functioning";
    static final String externalStoragePath = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)).getAbsolutePath();
    static final File externalParentDir=new File(externalStoragePath);

}