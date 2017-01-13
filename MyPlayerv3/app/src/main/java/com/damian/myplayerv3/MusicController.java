package com.damian.myplayerv3;

import android.content.Context;
import android.widget.MediaController;

/**
 * Created by damianmandrake on 1/7/17.
 */
public class MusicController extends MediaController {

    Context context;
    MusicController(Context c){
        super(c);
        context=c;
    }

    @Override
    public void hide() {

    }
}
