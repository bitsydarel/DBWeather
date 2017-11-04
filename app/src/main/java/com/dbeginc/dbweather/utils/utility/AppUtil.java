package com.dbeginc.dbweather.utils.utility;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.customtabs.CustomTabsService;

import java.util.List;

/**
 * Created by Darel Bitsy on 22/02/17.
 */

public class AppUtil {

    private AppUtil() {}

    public static String isChromeTabSupported(final Context context) {
        final PackageManager pm = context.getPackageManager();

        // Get default VIEW intent handler that can view a web url.
        final Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));

        // Get all apps that can handle VIEW intents.
        final List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        for (final ResolveInfo info : resolvedActivityList) {
            final Intent serviceIntent = new Intent();
            serviceIntent.setAction(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            if (pm.resolveService(serviceIntent, 0) != null) {
                return info.activityInfo.packageName;
            }
        }
        return null;
    }
}
