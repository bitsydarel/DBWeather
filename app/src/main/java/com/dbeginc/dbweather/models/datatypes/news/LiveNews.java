package com.dbeginc.dbweather.models.datatypes.news;

import android.databinding.ObservableField;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by darel on 07.06.17.
 * Live News Pojo
 */

public class LiveNews implements Parcelable {

    public final ObservableField<String> liveSource = new ObservableField<>();

    public final ObservableField<String> liveUrl = new ObservableField<>();

    public LiveNews() {}

    private LiveNews(final Parcel in) {
        liveSource.set(in.readString());
        liveUrl.set(in.readString());
    }

    public static final Creator<LiveNews> CREATOR = new Creator<LiveNews>() {
        @Override
        public LiveNews createFromParcel(final Parcel in) { return new LiveNews(in); }

        @Override
        public LiveNews[] newArray(final int size) { return new LiveNews[size]; }
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
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(liveSource.get());
        dest.writeString(liveUrl.get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final LiveNews liveNews = (LiveNews) o;

        return liveSource.get().equalsIgnoreCase(liveNews.liveSource.get());
    }

    @Override
    public int hashCode() {
        int result = liveSource.hashCode();
        result = 31 * result + liveUrl.hashCode();
        return result;
    }
}
