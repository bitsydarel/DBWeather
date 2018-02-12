/*
 *  Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.dbeginc.dbweather.news.webclients;

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
