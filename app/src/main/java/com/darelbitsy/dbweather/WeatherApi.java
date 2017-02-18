package com.darelbitsy.dbweather;

import android.os.Parcel;
import android.os.Parcelable;

import com.darelbitsy.dbweather.weather.Current;
import com.darelbitsy.dbweather.weather.Day;
import com.darelbitsy.dbweather.weather.Hour;

import java.util.Arrays;

/**
 * Created by Darel Bitsy on 07/01/17.
 */

public class WeatherApi implements Parcelable {
    private Current mCurrent;
    private Hour[] mHour;
    private Day[] mDay;

    public static final Creator<WeatherApi> CREATOR = new Creator<WeatherApi>() {
        @Override
        public WeatherApi createFromParcel(Parcel in) {
            return new WeatherApi(in);
        }

        @Override
        public WeatherApi[] newArray(int size) {
            return new WeatherApi[size];
        }
    };

    public Current getCurrent() {
        return mCurrent;
    }

    public void setCurrent(final Current current) {
        mCurrent = current;
    }

    public Hour[] getHour() { return mHour == null ? new Hour[0] : Arrays.copyOf(mHour, mHour.length); }

    public void setHour(final Hour[] hour) { mHour = Arrays.copyOf(hour, hour.length) ; }

    public Day[] getDay() { return mDay == null ? new Day[0] : Arrays.copyOf(mDay, mDay.length); }

    public void setDay(final Day[] day) { mDay = Arrays.copyOf(day, day.length); }

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
        dest.writeParcelable(mCurrent, 0);
        dest.writeTypedArray(mDay, 1);
        dest.writeTypedArray(mHour, 2);
    }

    public WeatherApi() {}

    private WeatherApi(Parcel in) {
        mCurrent = in.readParcelable(Current.class.getClassLoader());
        mDay = in.createTypedArray(Day.CREATOR);
        mHour = in.createTypedArray(Hour.CREATOR);
    }
}
