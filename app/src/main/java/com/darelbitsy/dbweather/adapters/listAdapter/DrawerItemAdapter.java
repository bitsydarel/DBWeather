package com.darelbitsy.dbweather.adapters.listAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.model.config.DrawerItem;
import com.darelbitsy.dbweather.ui.AddLocationActivity;
import com.darelbitsy.dbweather.ui.NewsDialogActivity;

import java.util.List;

import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.PREFS_NAME;

/**
 * Created by Darel Bitsy on 22/03/17.
 * Drawer item adapter
 */

public class DrawerItemAdapter extends RecyclerView.Adapter<DrawerItemAdapter.DrawerItemViewHolder> {
    private final List<DrawerItem> listOfDrawerItem;

    public DrawerItemAdapter(List<DrawerItem> listOfDrawerItem) {
        this.listOfDrawerItem = listOfDrawerItem;
    }

    @Override
    public DrawerItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DrawerItemViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.drawer_item, parent, false));
    }

    @Override
    public void onBindViewHolder(DrawerItemViewHolder holder, int position) {
        holder.bindItem(listOfDrawerItem.get(position));
    }

    @Override
    public int getItemCount() {
        return listOfDrawerItem.size();
    }

    class DrawerItemViewHolder extends RecyclerView.ViewHolder {
        final ConstraintLayout drawerItemLayout;
        final ImageView itemIcon;
        final TextView itemTitle;
        final SwitchCompat switchCompat;
        final LinearLayout drawerItemLimit;
        DrawerItem mDrawerItem;

        CompoundButton.OnCheckedChangeListener configurationListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.getContext()
                            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean(mDrawerItem.getPreferenceKey(), true)
                            .apply();

                } else {
                    buttonView.getContext()
                            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean(mDrawerItem.getPreferenceKey(), false)
                            .apply();
                }
            }
        };

        DrawerItemViewHolder(View itemView) {
            super(itemView);
            drawerItemLayout = (ConstraintLayout) itemView.findViewById(R.id.drawerItemLayout);
            itemIcon = (ImageView) itemView.findViewById(R.id.itemIcon);
            itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            switchCompat = (SwitchCompat) itemView.findViewById(R.id.switchId);
            drawerItemLimit = (LinearLayout) itemView.findViewById(R.id.drawerItemLimit);
        }

        void bindItem(DrawerItem item) {
            mDrawerItem = item;
            if (!item.getTitle().contains("Sources")) {
                itemIcon.setImageResource(mDrawerItem.getIconResourceId());

            }
            
            if (item.getTitle().equalsIgnoreCase(itemTitle
                    .getResources().getString(R.string.add_location_config_title))) {
                drawerItemLayout.setOnClickListener(view -> view.getContext()
                        .startActivity(new Intent(view.getContext(), AddLocationActivity.class)));
            }

            itemTitle.setText(mDrawerItem.getTitle());

            if (item.getPreferenceKey() != null) {
                switchCompat.setOnCheckedChangeListener(configurationListener);
                drawerItemLimit.setVisibility(View.GONE);

            } else {
                switchCompat.setVisibility(View.GONE);
            }
        }
    }
}
