package com.damian.myplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainActivityConstants{


    ArrayList<Song> songList=new ArrayList<>();
    private Button b;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b=(Button)findViewById(R.id.permButton);
        listView=(ListView)findViewById(R.id.musicList);



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
    public void askPerms(View v){
        System.out.println("about to ask for perm");
        if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
            toast(REQUIRE_PERMS);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_READ_EXTERNAL);

    }
    void toast(String a){

        Toast.makeText(this,a,Toast.LENGTH_SHORT).show();
    }
    void findSongs(){
        ContentResolver musicResolver=getContentResolver();
        Uri musicUri= android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor=musicResolver.query(musicUri,null,null,null,null);

        if(musicCursor!=null && musicCursor.moveToFirst()) {
            int titleCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idCol= musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistCol=musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumId=musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID);
            do{
                long tempId=musicCursor.getLong(idCol);
                String t=musicCursor.getString(titleCol),a=musicCursor.getString(artistCol),b=musicCursor.getString(albumId);
                Cursor albumArtCursor=musicResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,new String[]{MediaStore.Audio.Albums._ID,MediaStore.Audio.Albums.ALBUM_ART},MediaStore.Audio.Albums._ID +"=?",new String[]{b},null);
                String path=null;
                if(albumArtCursor.moveToFirst()){
                    path=albumArtCursor.getString(albumArtCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                    if(path!=null)
                    Log.d("path",path);
                }

                songList.add(new Song(tempId,t,a,path));

            }while(musicCursor.moveToNext());

        }b.setVisibility(View.INVISIBLE);
        handleListView();
        toast(songList.size()+" songs retrieved ");

    }
    private void handleListView(){
        listView.setVisibility(View.VISIBLE);
        SongAdapter songAdapter=new SongAdapter(this,songList);
        listView.setAdapter(songAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         switch (requestCode){
             case MY_READ_EXTERNAL:
                 if(grantResults[0]!=PackageManager.PERMISSION_GRANTED)
                     toast(REQUIRE_PERMS);
                 else {
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
