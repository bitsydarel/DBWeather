package com.dbeginc.dbweather.ui.main.news.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.dbeginc.dbweather.BuildConfig;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.YOUTUBE_URL;

/**
 * Created by darel on 07.06.17.
 * YoutubePlayer Fragment
 */

public class YoutubeFragment extends YouTubePlayerSupportFragment implements YouTubePlayer.OnInitializedListener, IYoutubeView {

    private YouTubePlayer youTubePlayer;
    private YoutubeLivePresenter livePresenter;
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private String defaultUrl;

    public static synchronized YoutubeFragment newInstance(@NonNull final String defaultUrl) {
        final YoutubeFragment youtubeFragmentFragment = new YoutubeFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(YOUTUBE_URL, defaultUrl);
        youtubeFragmentFragment.setArguments(bundle);
        return youtubeFragmentFragment;
    }

    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        livePresenter = new YoutubeLivePresenter(this);
        final Bundle arguments = getArguments();

        if (bundle != null && bundle.containsKey(YOUTUBE_URL)) {
            defaultUrl = bundle.getString(YOUTUBE_URL);

        } else if (arguments != null && arguments.containsKey(YOUTUBE_URL)) {
            defaultUrl = arguments.getString(YOUTUBE_URL);
        }
        initialize(BuildConfig.YOUTUBE_API_KEY, this);
    }

    @Override
    public void onInitializationSuccess(final YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, final boolean wasRestored) {
        if (!wasRestored) {
            this.youTubePlayer = youTubePlayer;
            this.youTubePlayer.cueVideo(defaultUrl);
            this.youTubePlayer.setShowFullscreenButton(true);
            this.youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            this.youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
            this.youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
            this.youTubePlayer.setOnFullscreenListener(isFullScreen -> {
                if (isFullScreen) {
                    this.youTubePlayer.setFullscreen(false);
                    startActivity(
                            YouTubeStandalonePlayer.createVideoIntent(getActivity(), BuildConfig.YOUTUBE_API_KEY, defaultUrl, 0, true, false)
                    );
                }
            });
        }
    }

    @Override
    public void onInitializationFailure(final YouTubePlayer.Provider provider, final YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(getActivity(), RECOVERY_DIALOG_REQUEST).show();
        } else {
            Toast.makeText(getActivity(), String.format("There was an error initializing the YouTubePlayer (%1$s)",
                    errorReason.toString()), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(YOUTUBE_URL, defaultUrl);
    }

    @Override
    public void onResume() {
        super.onResume();
        livePresenter.subscribe();
    }

    @Override
    public void displayVideo(@NonNull final String url) {
        youTubePlayer.loadVideo(url);
        youTubePlayer.play();
        defaultUrl = url;
    }

    @Override
    public void onStop() {
        super.onStop();
        livePresenter.clearState();
    }
}
