package com.darelbitsy.dbweather.presenters.activities;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.darelbitsy.dbweather.extensions.holder.ConstantHolder;
import com.darelbitsy.dbweather.provider.image.IImageProvider;
import com.darelbitsy.dbweather.provider.schedulers.RxSchedulersProvider;
import com.darelbitsy.dbweather.views.activities.INewsDialogView;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by Darel Bitsy on 26/04/17.
 * News Dialog View Presenter
 */

public class NewsDialogPresenter {

    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private final INewsDialogView mMainView;
    private final RxSchedulersProvider mSchedulersProvider;
    private final Scheduler mObserveOnScheduler;
    private final IImageProvider mIImageProvider;

    public NewsDialogPresenter(@NonNull final INewsDialogView view,
                               @NonNull final IImageProvider imageProvider,
                               @NonNull final Scheduler observeOnScheduler) {

        mMainView = view;
        mIImageProvider = imageProvider;
        mSchedulersProvider = RxSchedulersProvider.newInstance();
        mObserveOnScheduler = observeOnScheduler;
    }

    public void getImage(@NonNull final String imageUrl) {
        subscriptions.add(mIImageProvider.getBitmapImage(imageUrl)
                .subscribeOn(mSchedulersProvider.getNewsScheduler())
                .observeOn(mObserveOnScheduler)
                .subscribeWith(new ImageObserver()));
    }

    public void cleanUp() {
        subscriptions.clear();
    }

    private class ImageObserver extends DisposableSingleObserver<Bitmap> {
        @Override
        public void onSuccess(final Bitmap bitmap) {
            mMainView.showImage(bitmap);
        }

        @Override
        public void onError(final Throwable e) {
            Log.i(ConstantHolder.TAG, "Error while downloading news Image!");
            mMainView.showDefaultImage();
        }
    }
}
