package com.darelbitsy.dbweather.adapters.listAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

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
        return null;
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
    public void onBindViewHolder(LocationViewHolder holder, int position) {

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return 0;
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {

        public LocationViewHolder(View itemView) {
            super(itemView);
        }
    }
}
