package com.dbeginc.dbweather.ui.main.news.webclients;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Bitsy Darel on 15.05.17.
 */

public class CustomWebClient extends WebViewClient {

    private String mainUrl;

    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
        return !mainUrl.contains(Uri.parse(Uri.decode(url)).getHost());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, final WebResourceRequest request) {
        return !mainUrl.contains(request.getUrl().getHost());
    }

    @Override
    public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        mainUrl = url.substring(0, url.indexOf('/', 8));
    }
}
