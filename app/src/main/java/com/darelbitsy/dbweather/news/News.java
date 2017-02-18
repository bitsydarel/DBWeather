package com.darelbitsy.dbweather.news;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by Darel Bitsy on 16/02/17.
 */
public class News implements Parcelable {
    private String mNewsSource;
    private String mNewsTitle;
    private URL mArticleUrl;

    /**
     * This constructor is empty,
     * because i need a constructor
     * to instantiate the class
     */
    public News() {}

    private News(Parcel in) {
        mNewsSource = in.readString();
        mNewsTitle = in.readString();
        mArticleUrl = (URL) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mNewsSource);
        dest.writeString(mNewsTitle);
        dest.writeSerializable(mArticleUrl);
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    public String getNewsSource() {
        return mNewsSource;
    }
    public void setNewsSource(String source) { mNewsSource = source; }

    public String getNewsTitle() {
        return mNewsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        mNewsTitle = newsTitle;
    }

    public String getArticleUrl() throws URISyntaxException {
        return new URI(mArticleUrl.getProtocol(),
                mArticleUrl.getUserInfo(),
                mArticleUrl.getHost(),
                mArticleUrl.getPort(),
                mArticleUrl.getPath(),
                mArticleUrl.getQuery(),
                mArticleUrl.getRef())
                .toString();
    }

    public void setArticleUrl(String articleUrl) throws MalformedURLException {
        mArticleUrl = new URL(articleUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
