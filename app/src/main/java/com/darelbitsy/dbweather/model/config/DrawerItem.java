package com.darelbitsy.dbweather.model.config;

/**
 * Created by Darel Bitsy on 22/03/17.
 */

public class DrawerItem {
    private int iconResourceId;
    private String title;
    private String preferenceKey;

    public DrawerItem() {
        this(0, null, null);
    }

    public DrawerItem(int iconResourceId, String title) { this(iconResourceId, title, null); }

    public DrawerItem(int iconResourceId, String title, String preferenceKey) {
        this.iconResourceId = iconResourceId;
        this.title = title;
        this.preferenceKey = preferenceKey;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public void setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreferenceKey() {
        return preferenceKey;
    }

    public void setPreferenceKey(String preferenceKey) {
        this.preferenceKey = preferenceKey;
    }
}
