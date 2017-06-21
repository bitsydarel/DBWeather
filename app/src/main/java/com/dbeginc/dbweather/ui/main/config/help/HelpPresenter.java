package com.dbeginc.dbweather.ui.main.config.help;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 21.06.17.
 *
 * Help View Presenter
 */

class HelpPresenter {

    private final PublishSubject<Pair<Integer, Boolean>> clickEvent;
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();
    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private final IHelpView helpView;

    HelpPresenter(@NonNull final IHelpView view, @NonNull final PublishSubject<Pair<Integer, Boolean>> configClickEvent ) {
        helpView = view;
        clickEvent = configClickEvent;
        subscribeToClickEvent();
    }

    void subscribeToClickEvent() {
        subscriptions.add(clickEvent.subscribeOn(schedulersProvider.getComputationThread())
                .observeOn(schedulersProvider.getUIScheduler())
                .subscribe(helpView::handleClick, this::onError));
    }

    private void onError(final Throwable throwable) {
        Crashlytics.logException(throwable);
        helpView.showError();
    }

    void clearState() { subscriptions.clear(); }
}
