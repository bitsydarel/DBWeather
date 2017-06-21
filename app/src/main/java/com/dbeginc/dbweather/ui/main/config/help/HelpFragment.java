package com.dbeginc.dbweather.ui.main.config.help;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.BuildConfig;
import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.HelpLayoutBinding;
import com.dbeginc.dbweather.ui.BaseFragment;
import com.dbeginc.dbweather.ui.main.config.ConfigurationItem;
import com.dbeginc.dbweather.ui.main.config.adapter.ConfigurationItemAdapter;
import com.google.android.gms.ads.AdRequest;

import java.util.Arrays;

import io.reactivex.subjects.PublishSubject;

import static android.content.Intent.EXTRA_EMAIL;
import static android.content.Intent.EXTRA_SUBJECT;

/**
 * Created by darel on 11.06.17.
 * Help Fragment
 */

public class HelpFragment extends BaseFragment implements IHelpView {
    private static final int BUG_REPORT = 380;
    private static final int RATE_ON_PLAY_STORE = 450;
    private static final int VERSION_CODE = 153;

    private HelpLayoutBinding binding;
    private final PublishSubject<Pair<Integer, Boolean>> clickEvent = PublishSubject.create();
    private HelpPresenter presenter;
    private ConfigurationItemAdapter adapter;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new HelpPresenter(this, clickEvent);
        adapter = setupHelpItems();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.help_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            inflater.inflate(R.menu.empty_menu, menu);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupAds();
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar.configToolbar);
        binding.toolbar.configToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(view.getContext().getString(R.string.help_config));
        }
        binding.toolbar.configToolbar.setNavigationOnClickListener(v -> configurationBackEvent.onNext(false));
        setupListOfHelp();
        presenter.subscribeToClickEvent();
    }

    @Override
    public void onDestroyView() {
        presenter.clearState();
        super.onDestroyView();
    }

    private ConfigurationItemAdapter setupHelpItems() {
        final ConfigurationItem version = new ConfigurationItem();
        version.icon.set(R.drawable.ic_version_code);
        version.id.set(VERSION_CODE);
        version.label.set(String.format(getAppContext().getString(R.string.version), getAppVersion()));
        version.hasSwitch.set(false);

        final ConfigurationItem reportBug = new ConfigurationItem();
        reportBug.icon.set(R.drawable.ic_bug_report);
        reportBug.id.set(BUG_REPORT);
        reportBug.label.set(getAppContext().getString(R.string.report_bug));
        reportBug.hasSwitch.set(false);

        final ConfigurationItem rateOnPlayStore = new ConfigurationItem();
        rateOnPlayStore.icon.set(R.drawable.ic_rate_on_play_store);
        rateOnPlayStore.id.set(RATE_ON_PLAY_STORE);
        rateOnPlayStore.label.set(getAppContext().getString(R.string.rate_on_play_store));
        rateOnPlayStore.hasSwitch.set(false);

        return new ConfigurationItemAdapter(Arrays.asList(version, reportBug, rateOnPlayStore), clickEvent);
    }

    private void setupListOfHelp() {
        binding.helpItems.setAdapter(adapter);
        binding.helpItems.setLayoutManager(new LinearLayoutManager(getAppContext(), LinearLayoutManager.VERTICAL, false));
        binding.helpItems.setHasFixedSize(true);
    }

    private int getAppVersion() {
        try {
            return appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0).versionCode;
        } catch (final PackageManager.NameNotFoundException e) {
            Crashlytics.logException(e);
            return 1;
        }
    }

    @Override
    public void showError() {
        Snackbar.make(binding.helpView, R.string.error_message, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void handleClick(@NonNull final Pair<Integer, Boolean> event) {
        switch (event.first) {
            case VERSION_CODE:
                break;

            case BUG_REPORT:
                reportBug();
                break;
            case RATE_ON_PLAY_STORE:
                rateOnPlayStore();
                break;
            default:
                break;
        }
    }

    private void setupAds() {
        final AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("C20BB1C5369BFDFD4992ED89CD62F271")
                .build();
        binding.adVHelp.loadAd(adRequest);
    }

    private void rateOnPlayStore() {
        Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        } else {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }

        try {
            startActivity(goToMarket);
        } catch (final ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
        }
    }

    private void reportBug() {
        final Intent emailSend = new Intent(Intent.ACTION_SEND);

        emailSend.setType("text/plain");
        emailSend.putExtra(EXTRA_SUBJECT, "DBWeather Bug Report");
        emailSend.putExtra(EXTRA_EMAIL, new String[]{BuildConfig.EMAIL_ACCOUNT});
        emailSend.setType("message/rfc822");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            emailSend.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        } else {
            emailSend.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }

        try {
            startActivity(Intent.createChooser(emailSend, getString(R.string.SEND_EMAIL_FROM)));
        } catch (ActivityNotFoundException ex) {
            Snackbar.make(binding.helpView, R.string.NO_EMAIL_CLIENTS, Snackbar.LENGTH_LONG)
                    .show();
        }
    }
}
