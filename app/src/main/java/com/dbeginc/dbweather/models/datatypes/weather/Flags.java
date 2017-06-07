package com.dbeginc.dbweather.models.datatypes.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by darel on 05.06.17.
 */

public class Flags implements Parcelable {

    @SerializedName("units")
    @Expose
    private String units;

    public Flags() {
    }

    private Flags(Parcel in) {
        units = in.readString();
    }

    public static final Creator<Flags> CREATOR = new Creator<Flags>() {
        @Override
        public Flags createFromParcel(Parcel in) {
            return new Flags(in);
        }

        @Override
        public Flags[] newArray(int size) {
            return new Flags[size];
        }
    };

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

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
        dest.writeString(units);
    }
}
