package com.damian.myplayerv3;


        import android.app.Service;
        import android.content.ContentUris;
        import android.content.Intent;
        import android.media.AudioManager;
        import android.media.MediaPlayer;
        import android.net.Uri;
        import android.os.Binder;
        import android.os.IBinder;
        import android.os.PowerManager;
        import android.provider.MediaStore;
        import android.support.annotation.Nullable;

        import java.io.IOException;
        import java.util.ArrayList;

/**
 * Created by Damian on 12/30/2016.
 */
public class MusicService extends Service implements MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener {

    private MediaPlayer mediaPlayer;
    private ArrayList<Song> songsList;
    private int songPosition;
    private final MusicBinder musicBinder=new MusicBinder();

    //since services dont require ctor... use lifecycle methods
    public void onCreate(){
        //whenever you call baseclass lifecycle func... android binds and marks the current class as a service
        super.onCreate();

        songPosition=0;
        mediaPlayer=new MediaPlayer();
        initMusicPlayer();

    }
    public void initMusicPlayer(){

        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);// this lets the service run even when the device is locked
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //setting the listeners for the MediaPlayer interfaces

        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
    }
    public void setSongsList(ArrayList<Song> s) {
        songsList=s;
    }
    public boolean isListEmpty(){
        return songsList.isEmpty();
    }
    public MediaPlayer getMediaPlayer(){return mediaPlayer;}


    public class MusicBinder extends Binder{
        MusicService getServiceInstance(){
            return MusicService.this;//return an instance of MusicService... this is being calledin the MainActivity as a part of SrviceConnection callback so that you can actually obtain the instance of the service from the OS
        }
    }
    public void playSong(){
        mediaPlayer.reset();
        Song toBePlayed=songsList.get(songPosition);
        long currSong=toBePlayed.getId();
        Uri trackUri= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,currSong);

        try {
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);

        }catch (IOException io){
            System.out.println("MusicPlayer Excpetion ");
        }

        mediaPlayer.prepareAsync();

    }

    public void setSongPosition(int p){
        songPosition=p;
    }
    public int getPosition(){
        return mediaPlayer.getCurrentPosition();
    }
    public int getDuration() {
        return mediaPlayer.getDuration();

    }
    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
    public void pause(){
        mediaPlayer.pause();
    }
    public void startPlaying(){
        mediaPlayer.start();
    }
    public void seekTo(int p){
        mediaPlayer.seekTo(p);
    }

    public void playNext(){
        songPosition= (songPosition+1)%songsList.size();
        playSong();
    }
    public void playPrevious(){
        songPosition = songPosition ==0 ? songsList.size()-1:songPosition--;
        playSong();
    }






    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("About to return musicBinder");
        return musicBinder;
    }
    @Override
    public boolean onUnbind(Intent intent){
        mediaPlayer.stop();
        mediaPlayer.release();

        return false;
    }



    @Override
    public void onCompletion(MediaPlayer mp) {
        playNext();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
    }


}
