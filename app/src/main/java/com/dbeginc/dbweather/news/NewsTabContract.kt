package com.dbeginc.dbweather.news

import com.dbeginc.dbweather.base.IPresenter
import com.dbeginc.dbweather.base.IView

/**
 * Created by darel on 29.05.17.
 * NewsPaperModel View
 */

interface NewsTabContract {

    interface NewsTabView : IView {
        fun showArticles()

        fun showLives()

        fun showError(message: String)
    }

    interface NewsTabPresenter : IPresenter<NewsTabView> {
        fun selectArticles()

        fun selectLives()

    }
}
