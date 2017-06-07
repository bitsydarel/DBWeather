package com.dbeginc.dbweather.models.datatypes.news;

/**
 * Created by Darel Bitsy on 18/02/17.
 * News Feed Object
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.dbeginc.dbweather.utils.holder.ConstantHolder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Article implements Parcelable {
    @SerializedName("author")
    @Expose
    private String author;

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("urlToImage")
    @Expose
    private String urlToImage;
    @SerializedName("publishedAt")
    @Expose
    private String publishedAt;

    private URL mArticleUrl;

    public Article() {
        //empty, because i need a default constructor to instantiate
        // the class other than the parcelable constructor
    }

    private Article(Parcel in) {
        author = in.readString();
        title = in.readString();
        description = in.readString();
        url = in.readString();
        urlToImage = in.readString();
        publishedAt = in.readString();
        mArticleUrl = (URL) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(urlToImage);
        dest.writeString(publishedAt);
        dest.writeSerializable(mArticleUrl);
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) { return new Article(in); }

        @Override
        public Article[] newArray(int size) { return new Article[size]; }
    };

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
        try {
            setArticleUrl(url);
        } catch (final MalformedURLException e) {
            Log.i(ConstantHolder.TAG, "Could not set article Url");
        }
    }

    public String getUrlToImage() {
        String urlImage = "";
        if (urlToImage != null && !urlToImage.isEmpty()) {
            try {
                final URL urlInfo = new URL(urlToImage);
                urlImage = new URI(urlInfo.getProtocol(),
                        urlInfo.getUserInfo(),
                        urlInfo.getHost(),
                        urlInfo.getPort(),
                        urlInfo.getPath(),
                        urlInfo.getQuery(),
                        urlInfo.getRef())
                        .toString();

                Log.i("URL_LOG", "Got url : " + urlImage);

            } catch (MalformedURLException | URISyntaxException e) {
                Log.i(ConstantHolder.TAG, "Error parsing url : "
                        + urlToImage);
            }
        }
        return urlImage;
    }

    public void setUrlToImage(final String urlToImage) { this.urlToImage = urlToImage; }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(final String publishedAt) {
        if (publishedAt != null) {
            this.publishedAt = publishedAt
                    .replace("T", " ")
                    .replace("Z", "");
        }

    }

    public String getArticleUrl() {
        String articleUrl = "";
        if (mArticleUrl != null) {
            try {
                articleUrl = new URI(mArticleUrl.getProtocol(),
                        mArticleUrl.getUserInfo(),
                        mArticleUrl.getHost(),
                        mArticleUrl.getPort(),
                        mArticleUrl.getPath(),
                        mArticleUrl.getQuery(),
                        mArticleUrl.getRef())
                        .toString();

            } catch (final URISyntaxException e) {
                Log.i(ConstantHolder.TAG, "Error while reformatting the url: "
                        + e.getMessage());
            }
        }
        return articleUrl;
    }

    public void setArticleUrl(final String articleUrl) throws MalformedURLException {
        mArticleUrl = new URL(articleUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Article article = (Article) o;

        if ((author != null && article.author != null) && author.equalsIgnoreCase(article.getAuthor())) {
            return getArticleUrl().equalsIgnoreCase(article.getArticleUrl());
        } else { return false; }
    }

    @Override
    public int hashCode() {
        int result = author != null ? author.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}
