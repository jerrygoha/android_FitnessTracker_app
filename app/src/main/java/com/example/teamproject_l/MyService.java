package com.example.teamproject_l;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

public class MyService extends Service {
    private final IBinder mBinder = new MyBinder();
    protected MediaPlayer mp;
    private boolean isPlaying = false;
    public static Intent serviceIntent = null;
    Bundle bundle;
    Uri uri;
    ArrayList<String> playList = new ArrayList<String>();
    int position;
    int limit;

    public MyService() {
    }

    public class MyBinder extends Binder {
        public MyService getSetvice() {
            return MyService.this;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mp = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceIntent = intent;
        bundle = intent.getExtras();

        // 음악재생에 필요한 데이터 번들로 받아옴
        isPlaying = bundle.getBoolean("isPlaying");
        playList = bundle.getStringArrayList("playList");
        position = bundle.getInt("position");
        limit = bundle.getInt("fileCount");

        //음악이 실행중일 때와 아닐때를 구분하여 음악 재생
            if (isPlaying == false) {
                mp = MediaPlayer.create(this, Uri.parse(playList.get(position)));
                mp.setOnCompletionListener(completionListener);
                mp.start();
                isPlaying = true;
            }
            else {
                nextSong(Uri.parse(playList.get(position)), position);
            }

            return super.onStartCommand(intent, flags, startId);
    }

    // 음악이 모두 재생되면 다음곡으로 자동으로 넘어감
    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            position++;
            if(position > limit - 1)
                position = 0;
            uri = Uri.parse(playList.get(position));
            try {
                Thread.sleep(500);
            }catch (Exception e) {

            }
            sendMessage();
            nextSong(uri, position);
        }
    };

    // 미니플레이어를 갱신하기위해 BroadCast
    private void sendMessage() {
        Intent intent = new Intent("autoPlay");
        intent.putExtra("autoPlayUri", uri.toString());
        intent.putExtra("position", position);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceIntent = null;

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return mBinder;
    }

    public void play() {
            mp.start();

    }


    public void pause() {
            mp.pause();

    }

    // 다음곡 재생
    public void nextSong(Uri uriPath, int position) {
        this.position = position;
        mp.stop();
        mp.reset();
        mp.release();
        mp = MediaPlayer.create(this, uriPath);
        mp.setOnCompletionListener(completionListener);
        mp.start();


    }

}