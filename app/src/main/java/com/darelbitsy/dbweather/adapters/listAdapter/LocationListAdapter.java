package com.darelbitsy.dbweather.adapters.listAdapter;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.helper.holder.ConstantHolder;

import org.geonames.InsufficientStyleException;
import org.geonames.Toponym;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Darel Bitsy on 02/04/17.
 */

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.LocationViewHolder> {
    private final List<Toponym> mListOfLocations = new ArrayList<>();

    public LocationListAdapter(List<Toponym> listOfLocations) {
        mListOfLocations.addAll(listOfLocations);
    }

    public void updateLocationList(List<Toponym> listOfLocations) {
        mListOfLocations.clear();
        mListOfLocations.addAll(listOfLocations);
        notifyDataSetChanged();
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * onBindViewHolder(ViewHolder, int, List). Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary View#findViewById(int) calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * see getItemViewType(int)
     * see onBindViewHolder(ViewHolder, int)
     */
    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LocationViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.location_list_item, parent, false));
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the ViewHolder#itemView to reflect the item at the given
     * position.
     * <p>
     * Note that unlike ListView, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use ViewHolder#getAdapterPosition() which will
     * have the updated adapter position.
     * <p>
     * Override onBindViewHolder(ViewHolder, int, List) instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) { holder.bindItem(mListOfLocations.get(position)); }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mListOfLocations.size();
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {
        final ConstraintLayout mLayout;
        final TextView cityName;
        final TextView countryName;
        final TextView continent;
        Toponym mLocation;

        final View.OnClickListener mLocationOnClickListener =
                view -> Log.i("Location", "Selected location : " + mLocation.getName());

        LocationViewHolder(View itemView) {
            super(itemView);
            mLayout = (ConstraintLayout) itemView.findViewById(R.id.locationListItemLayout);
            cityName = (TextView) itemView.findViewById(R.id.cityName);
            countryName = (TextView) itemView.findViewById(R.id.countryName);
            continent = (TextView) itemView.findViewById(R.id.continent);
        }

        void bindItem(Toponym locationInfo) {
            mLocation = locationInfo;
            mLayout.setOnClickListener(mLocationOnClickListener);
            cityName.setText(locationInfo.getName());
            countryName.setText(locationInfo.getCountryName());
            try {
                continent.setText(locationInfo.getContinentCode());
            } catch (InsufficientStyleException e) {
                Log.i(ConstantHolder.TAG, "error: " + e.getMessage());
            }
        }
    }

}
