package com.dbeginc.dbweather.ui.main.config;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by darel on 11.06.17.
 * Configuration Item
 */

public class ConfigurationItem implements Parcelable {

    public final ObservableInt icon = new ObservableInt();
    public final ObservableInt id = new ObservableInt();
    public final ObservableBoolean hasSwitch = new ObservableBoolean();
    public final ObservableBoolean isChecked = new ObservableBoolean();
    public final ObservableField<String> label = new ObservableField<>();


    ConfigurationItem() {
    }

    private ConfigurationItem(Parcel in) {
        icon.set(in.readInt());
        id.set(in.readInt());
        label.set(in.readString());
        hasSwitch.set(in.readInt() != 0);
    }

    public static final Creator<ConfigurationItem> CREATOR = new Creator<ConfigurationItem>() {
        @Override
        public ConfigurationItem createFromParcel(Parcel in) {
            return new ConfigurationItem(in);
        }

        @Override
        public ConfigurationItem[] newArray(int size) {
            return new ConfigurationItem[size];
        }
    };

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(icon.get());
        dest.writeInt(id.get());
        dest.writeString(label.get());
        dest.writeInt(hasSwitch.get() ? 1 : 0);
    }
}
