package com.darelbitsy.dbweather.ui.main.newsdetails;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.darelbitsy.dbweather.DBWeatherApplication;
import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.databinding.NewsActivityBinding;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.provider.image.IImageProvider;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.NEWS_DATA_KEY;

/**
 * Created by Darel Bitsy on 11/03/17.
 * Dialog fragment that show description
 * about the news the user clicked
 */

public class NewsDialogActivity extends AppCompatActivity implements INewsDialogView {
    private NewsActivityBinding mNewsDialogBinder;
    private NewsDialogPresenter mPresenter;
    @Inject IImageProvider mImageDownloader;

    @Override
    public void showImage(@NonNull final Bitmap image) {
        mNewsDialogBinder.newsProgressBar.setVisibility(View.INVISIBLE);
        mNewsDialogBinder.articleImage.setImageBitmap(image);
        mNewsDialogBinder.articleImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDefaultImage() {
        mNewsDialogBinder.articleImage.setVisibility(View.VISIBLE);
        mNewsDialogBinder.newsProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBWeatherApplication.getComponent()
                .inject(this);

        mNewsDialogBinder = DataBindingUtil.setContentView(this, R.layout.news_activity);
        mPresenter = new NewsDialogPresenter(this, mImageDownloader, AndroidSchedulers.mainThread());

        final Article article = getIntent().getParcelableExtra(NEWS_DATA_KEY);
        mNewsDialogBinder.setArticle(article);
        setSupportActionBar(mNewsDialogBinder.newsToolbar.toolbarId);

        mNewsDialogBinder.closeButton.setOnClickListener(view -> finish());

        if(!article.getUrlToImage().isEmpty()) {

            mPresenter.getImage(article.getUrlToImage());
            mNewsDialogBinder.newsProgressBar.setVisibility(View.VISIBLE);
            mNewsDialogBinder.articleImage.setVisibility(View.INVISIBLE);
        }

        mNewsDialogBinder.openInBrowserButton.setOnClickListener(view -> {
            final String url = article.getArticleUrl().isEmpty() ?
                    "https://github.com/404" : article.getArticleUrl();

                    final Intent browserIntent = new Intent()
                    .setAction(Intent.ACTION_VIEW)
                    .setData(Uri.parse(url))
                    .addCategory(Intent.CATEGORY_BROWSABLE);

            startActivity(browserIntent);
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.cleanUp();
    }
}
