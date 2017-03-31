package com.darelbitsy.dbweather.ui.alert;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.helper.holder.ConstantHolder;
import com.darelbitsy.dbweather.controller.api.adapters.helper.GetImageDownloader;
import com.darelbitsy.dbweather.helper.utility.AppUtil;
import com.darelbitsy.dbweather.model.news.Article;
import com.darelbitsy.dbweather.ui.MainActivity;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.NEWS_DATA_KEY;

/**
 * Created by Darel Bitsy on 11/03/17.
 * Dialog fragment that show description
 * about the news the user clicked
 */

public class NewsDialogActivity extends AppCompatActivity {
    @BindView(R.id.openInBrowserButton)
    Button openInBrowser;

    @BindView(R.id.closeButton)
    Button close;

    @BindView(R.id.articleFrom)
    TextView newsFrom;

    @BindView(R.id.articlePublicationDate)
    TextView newsPublicationDate;

    @BindView(R.id.articleTitleText)
    TextView newsTitle;

    @BindView(R.id.articleImage)
    ImageView newsImage;

    @BindView(R.id.articleDetails)
    TextView newsDetails;

    @BindView(R.id.newsProgressBar)
    ProgressBar newsProgressBar;

    private Article article;
    private Toolbar mToolbar;
    private Single<Bitmap> imageDownloader;

    private class GetNewsImage extends DisposableSingleObserver<Bitmap> {
        @Override
        public void onSuccess(Bitmap bitmap) {
            newsProgressBar.setVisibility(View.INVISIBLE);
            newsImage.setImageBitmap(bitmap);
            newsImage.setVisibility(View.VISIBLE);
        }

        @Override
        public void onError(Throwable e) {
            Log.i(ConstantHolder.TAG, "Error while downloading news Image!");
            newsImage.setVisibility(View.VISIBLE);
            newsProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);
        ButterKnife.bind(this);

        Typeface typeface = AppUtil.getAppGlobalTypeFace(this);
        mToolbar = (Toolbar) findViewById(R.id.newsToolbar);
        setSupportActionBar(mToolbar);


        article = getIntent().getParcelableExtra(NEWS_DATA_KEY);

        if(!article.getUrlToImage().isEmpty()) {
            imageDownloader = new GetImageDownloader(this)
                    .getObservableImageDownloader(article.getUrlToImage());

            newsProgressBar.setVisibility(View.VISIBLE);
            newsImage.setVisibility(View.INVISIBLE);

            MainActivity.subscriptions.add(imageDownloader.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new GetNewsImage()));
        }

        newsFrom.setText(String.format(Locale.getDefault(),
                getString(R.string.news_from),
                article.getAuthor()));

        newsPublicationDate.setText(article.getPublishedAt());
        newsTitle.setText(article.getTitle());
        newsDetails.setText(article.getDescription());

        if (typeface != null) {
            newsFrom.setTypeface(typeface);
            newsPublicationDate.setTypeface(typeface);
            newsTitle.setTypeface(typeface);
        }

        openInBrowser.setOnClickListener(view -> {
            String url = article.getArticleUrl().isEmpty() ?
                    "https://github.com/404" : article.getArticleUrl();

                    Intent browserIntent = new Intent()
                    .setAction(Intent.ACTION_VIEW)
                    .setData(Uri.parse(url))
                    .addCategory(Intent.CATEGORY_BROWSABLE);

            startActivity(browserIntent);
        });

        close.setOnClickListener(view -> {
            finish();
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
