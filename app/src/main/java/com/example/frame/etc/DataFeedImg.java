package com.example.frame.etc;

import android.net.Uri;

public class DataFeedImg {
    private Uri feedImg;

    public DataFeedImg(Uri feedImg) {
        this.feedImg = feedImg;
    }

    public Uri getFeedImg() {
        return feedImg;
    }
}
