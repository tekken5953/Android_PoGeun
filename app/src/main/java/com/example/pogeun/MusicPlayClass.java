package com.example.pogeun;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MusicPlayClass extends Service {

    MediaPlayer mp;

    int resId;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        resId = Integer.parseInt(intent.getExtras().get("resId").toString());
        mp = MediaPlayer.create(this, resId);
        mp.isLooping();
        mp.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mp.stop();
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}

