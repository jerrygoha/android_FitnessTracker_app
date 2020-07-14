package com.example.teamproject_l;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {
    LinearLayout layout;
    ImageView albumArt;
    TextView title;
    TextView artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        layout = findViewById(R.id.informationLayout);

        byte[] data = bundle.getByteArray("albumArt");
        albumArt = findViewById(R.id.albumArt);
        if (data != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            albumArt.setImageBitmap(bitmap);
        } else {
            albumArt.setImageResource(R.drawable.default_album_image);
        }

        title = new TextView(this);
        if (bundle.getString("title") == null) {
            title.setText("Title : -");
        }
        else {
            title.setText("Title : " + bundle.getString("title"));
        }
        title.setTextSize(15);
        title.setPadding(20,0,0,0);
        layout.addView(title);

        artist = new TextView(this);
        if (bundle.getString("artist") == null)
            artist.setText("Artist : -");
        else
            artist.setText("Artist : " + bundle.getString("artist"));
        artist.setTextSize(15);
        artist.setPadding(20,0,0,0);
        layout.addView(artist);

        TextView album = new TextView(this);
        if (bundle.getString("album") == null)
            album.setText("Album : -");
        else
            album.setText("Album : " + bundle.getString("album"));
        album.setTextSize(15);
        album.setPadding(20,0,0,0);
        layout.addView(album);

        TextView genre = new TextView(this);
        if (bundle.getString("genre") == null)
            genre.setText("Genre : -");
        else
            genre.setText("Genre : " + bundle.getString("genre"));
        genre.setTextSize(15);
        genre.setPadding(20,0,0,0);
        layout.addView(genre);

    }

}


