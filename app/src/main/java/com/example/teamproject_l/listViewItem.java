package com.example.teamproject_l;

import android.graphics.Bitmap;

public class listViewItem {
    private Bitmap item_albumImage ;
    private String item_musicTitle ;
    private String item_musicArtist ;

    public void setImage(Bitmap image) {
        item_albumImage = image ;
    }
    public void setTitle(String title) {
        item_musicTitle = title ;
    }
    public void setArtist(String artist) {
        item_musicArtist = artist;
    }

    public Bitmap getImage() {
        return this.item_albumImage ;
    }
    public String getTitle() {
        return this.item_musicTitle ;
    }
    public String getArtist() {
        return this.item_musicArtist;
    }

}