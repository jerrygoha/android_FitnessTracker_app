package com.example.teamproject_l;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.util.ArrayList;

import static android.content.Context.BIND_AUTO_CREATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicFragment extends Fragment {

    ListView musicLv;
    listViewAdapter adapter;
    Bundle bundle_info;
    Bundle bundle_service;
    MyService mService;
    private Intent serviceIntent;
    private Intent intent2;
    boolean isBind = false;
    boolean isPlaying = false;
    boolean button_state = true;
    AdapterView<?> parentA;
    int pos;
    int fileCount = 0;

    ImageButton play_pause;
    ImageButton nextSong;
    ImageButton preSong;

    ArrayList<String> playList = new ArrayList<String>();

    public MusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //서비스와 음악정보 액티비티에 넘길 번들 구성
        bundle_info = new Bundle();
        bundle_service = new Bundle();
        intent2 = new Intent(getActivity(), InfoActivity.class);

        //커스텀 어댑터를 리스트뷰에 적용
        adapter = new listViewAdapter();
        musicLv = getActivity().findViewById(R.id.musicList);
        musicLv.setAdapter(adapter);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath();

        // 파일 권한 허용 되었을 때 Music파일에서 파일을 읽어와 리스트 구성 및 리스트뷰에 추가
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if(PermissionHandler.isPermissionGranted(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, "External Storage", 1000)) {
                try {
                    File directory = new File(path);
                    File[] listFiles = directory.listFiles();
                    String fileName, fileName_Extension, musicTitle, musicArtist;
                    for(int i = 0; i < listFiles.length; i++) {
                        fileName = listFiles[i].getName();
                        fileName_Extension = fileName.substring(fileName.length() - 3);
                        if(fileName_Extension.equals("mp3")) {
                            final Uri uri = Uri.parse(path + "/" + fileName);
                            playList.add(uri.toString());
                            fileCount++;
                            MediaMetadataRetriever mmdata = new MediaMetadataRetriever();
                            mmdata.setDataSource(getActivity().getApplication(), uri);
                            byte [] data = mmdata.getEmbeddedPicture();
                            musicTitle = fileName.substring(0, fileName.length() - 4);
                            musicArtist = mmdata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            if(data != null) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                adapter.addItem(bitmap, musicTitle, musicArtist);
                            }
                            else {
                                Bitmap bitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.default_album_image)).getBitmap();
                                adapter.addItem(bitmap, musicTitle, musicArtist);
                            }
                        }
                    }

                } catch(Exception e) {}
            }
        }

        // Bind Service 설정
        final ServiceConnection svc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MyService.MyBinder myBinder = (MyService.MyBinder) service;
                mService = myBinder.getSetvice();

                isBind = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
                isBind = false;
            }
        };

        play_pause = getActivity().findViewById(R.id.btn_play_pause);
        nextSong = getActivity().findViewById(R.id.btn_forward);
        preSong = getActivity().findViewById(R.id.btn_rewind);

        if (mService.serviceIntent == null) {
            serviceIntent = new Intent(getActivity(), MyService.class);
        } else {
            serviceIntent = mService.serviceIntent;
        }

        // 리스트뷰의 아이템이 눌렸을 때 해당 음악의 정보를 보여주는 InfoActivity로 이동 및 서비스를 통해 음악재생, 미니플레이어 세팅
        musicLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parentA = parent;
                pos = position;

                listViewItem item = (listViewItem) parent.getItemAtPosition(position);
                final String uriPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath() + "/" + item.getTitle() + ".mp3";
                final Uri uri = Uri.parse(uriPath);

                MediaMetadataRetriever mmdata = new MediaMetadataRetriever();
                mmdata.setDataSource(getActivity().getApplication(), uri);
                setMiniPlayer(uri);


                bundle_info.putString("album", mmdata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
                bundle_info.putString("artist", mmdata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                bundle_info.putString("genre", mmdata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
                bundle_info.putString("title", mmdata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                bundle_info.putByteArray("albumArt", mmdata.getEmbeddedPicture());

                bundle_service.putInt("position", pos);
                bundle_service.putString("uriPath", uriPath);
                bundle_service.putBoolean("isPlaying", isPlaying);
                bundle_service.putInt("fileCount", fileCount);
                bundle_service.putStringArrayList("playList", playList);

                intent2.putExtras(bundle_info);
                serviceIntent.putExtras(bundle_service);
                getActivity().startService(serviceIntent);
                getActivity().bindService(serviceIntent,svc,BIND_AUTO_CREATE);

                startActivity(intent2);
                play_pause.setImageResource(R.drawable.ic_pause_black_24dp);
                isPlaying = true;

            }
        });
    }

    // 미니플레이어의 버튼에 맞는 동작 구현
    @Override
    public void onStart() {
        super.onStart();
            play_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPlaying) {
                        if (button_state) {
                            mService.pause();
                            button_state = false;
                            play_pause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        } else {
                            mService.play();
                            button_state = true;
                            play_pause.setImageResource(R.drawable.ic_pause_black_24dp);
                        }
                    }
                }
            });
            nextSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isPlaying) {
                        pos++;
                        if (fileCount - 1 < pos) {
                            pos = 0;
                        }
                        Uri uri = Uri.parse(playList.get(pos));
                        mService.nextSong(uri, pos);
                        setMiniPlayer(uri);
                    }
                }
            });

            preSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPlaying) {
                        pos--;
                        if (0 > pos) {
                            pos = fileCount - 1;
                        }
                        Uri uri = Uri.parse(playList.get(pos));
                        mService.nextSong(uri, pos);
                        setMiniPlayer(uri);
                    }
                }
            });

    }


    // BroadCastReceiver Set
    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("autoPlay"));
    }

    // BroadCastReceiver 동작 설정
private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        String s = intent.getStringExtra("autoPlayUri");
        int position = intent.getIntExtra("position",0);
        pos = position;
        setMiniPlayer(Uri.parse(s));
    }
};

    // BroadCastReceiver 종료
    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    // 서비스 종료
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceIntent!=null) {
            getActivity().stopService(serviceIntent);
            serviceIntent = null;
        }
    }

    // 미니플레이어 세팅
    public void setMiniPlayer(Uri uri) {

        MediaMetadataRetriever mmdata = new MediaMetadataRetriever();
        mmdata.setDataSource(getActivity().getApplication(), uri);
        byte [] data = mmdata.getEmbeddedPicture();

        ImageView albumArt = getActivity().findViewById(R.id.img_albumart);
        TextView musicTitle = getActivity().findViewById(R.id.txt_title);
        TextView musicArtist = getActivity().findViewById(R.id.txt_artist);
        if(data != null)
            albumArt.setImageBitmap(BitmapFactory.decodeByteArray(data,0, data.length));
        else
            albumArt.setImageResource(R.drawable.default_album_image);

        if(mmdata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) != null)
            musicTitle.setText(mmdata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        else
            musicTitle.setText("-");

        if(mmdata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) != null)
            musicArtist.setText(mmdata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        else
            musicArtist.setText("-");
    }
}
