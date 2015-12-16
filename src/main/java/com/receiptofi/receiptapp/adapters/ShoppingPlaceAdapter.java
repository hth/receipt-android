package com.receiptofi.receiptapp.adapters;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.ShoppingListActivity;
import com.receiptofi.receiptapp.ShoppingPlaceActivity;
import com.receiptofi.receiptapp.model.helper.Coordinate;
import com.receiptofi.receiptapp.model.helper.ShoppingPlace;
import com.receiptofi.receiptapp.model.types.DistanceUnit;
import com.receiptofi.receiptapp.utils.AppUtils;

import java.util.List;

/**
 * User: hitender
 * Date: 12/8/15 5:07 AM
 */
public class ShoppingPlaceAdapter extends BaseAdapter {
    private static final String TAG = ShoppingPlaceAdapter.class.getSimpleName();
    private List<ShoppingPlace> shoppingPlaces;
    private ShoppingPlaceActivity activity;

    private Drawable locationDraw;
    private Drawable locationOffDraw;
    private Drawable shoppingBasketDraw;
    private boolean mapInstalled;

    public ShoppingPlaceAdapter(List<ShoppingPlace> shoppingPlaces, ShoppingPlaceActivity activity) {
        this.shoppingPlaces = shoppingPlaces;
        this.activity = activity;

        locationDraw = new IconDrawable(activity, FontAwesomeIcons.fa_map_marker)
                .colorRes(R.color.red)
                .actionBarSize();

        locationOffDraw = new IconDrawable(activity, FontAwesomeIcons.fa_map_marker)
                .colorRes(R.color.gray_light)
                .actionBarSize();

        shoppingBasketDraw = new IconDrawable(activity, FontAwesomeIcons.fa_shopping_basket)
                .colorRes(R.color.father_bg)
                .actionBarSize();

        mapInstalled = isAppInstalled("com.google.android.apps.maps");
    }

    public void updateList(List<ShoppingPlace> shoppingPlaces) {
        this.shoppingPlaces = shoppingPlaces;
    }

    @Override
    public int getCount() {
        return shoppingPlaces.size();
    }

    @Override
    public Object getItem(int i) {
        return shoppingPlaces.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (null == view) {
            view = View.inflate(activity, R.layout.fragment_shopping_places_list, null);
            new ViewHolder(view);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        final ShoppingPlace shoppingPlace = (ShoppingPlace) getItem(i);
        holder.bizName.setText(shoppingPlace.getBizName());
        holder.lastTransactionAmount.setText(
                activity.getResources().getString(
                        R.string.last_purchase,
                        AppUtils.currencyFormatter().format(shoppingPlace.getMostRecentPurchase())));

        if (shoppingPlace.getLastShopped().isEmpty()) {
            holder.lastShopped.setText("");
        } else {
            holder.lastShopped.setText(
                    activity.getResources().getString(R.string.shopped,
                            NotificationAdapter.prettyTime.format(shoppingPlace.getLastShopped().get(0))));

            holder.shoppingBasketImage.setImageDrawable(shoppingBasketDraw);
            holder.shoppingBasketImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle sendBundle = new Bundle();
                    sendBundle.putString(ShoppingListActivity.BUNDLE_KEY_BIZ_NAME, shoppingPlace.getBizName());

                    Intent intent = new Intent(activity, ShoppingListActivity.class);
                    intent.putExtras(sendBundle);
                    activity.startActivity(intent);
                }
            });
        }

        if (!shoppingPlace.getDistance().isEmpty()) {
            holder.gpsImage.setImageDrawable(locationDraw);
            holder.bizDistance.setText(
                    activity.getResources().getString(R.string.biz_distance,
                            shoppingPlace.getDistance().get(0), DistanceUnit.M.getName()));
        } else {
            holder.gpsImage.setImageDrawable(locationOffDraw);
            holder.bizDistance.setText(activity.getResources().getString(R.string.biz_distance_empty, "-------", DistanceUnit.M.getName()));
        }

        if (mapInstalled) {
            addAddressListener(holder, shoppingPlace);
        }
        return view;
    }

    /**
     * Add listener to distance.
     *
     * @param holder
     * @param shoppingPlace
     */
    private void addAddressListener(ViewHolder holder, final ShoppingPlace shoppingPlace) {
        if (!shoppingPlace.getCoordinates().isEmpty()) {
            final Coordinate coordinate = shoppingPlace.getCoordinates().iterator().next();

            String uriString = "geo:" + coordinate.getLat() + "," + coordinate.getLng() +
                    "?q=" + Uri.encode(coordinate.getAddress()) +
                    "(" + Uri.encode(shoppingPlace.getBizName()) + ")" + "&z=16";
            final Uri uri = Uri.parse(uriString);

            final Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
            mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

            holder.bizDistance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.startActivity(mapIntent);
                }
            });
            holder.gpsImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.startActivity(mapIntent);
                }
            });
        }
    }

    class ViewHolder {
        TextView bizName;
        TextView lastTransactionAmount;
        TextView lastShopped;
        TextView bizDistance;
        ImageView gpsImage;
        ImageView shoppingBasketImage;

        public ViewHolder(View view) {
            bizName = (TextView) view.findViewById(R.id.biz_name);
            lastTransactionAmount = (TextView) view.findViewById(R.id.last_transaction_amount);
            lastShopped = (TextView) view.findViewById(R.id.last_shopped);
            bizDistance = (TextView) view.findViewById(R.id.biz_distance_in_units);
            gpsImage = (ImageView) view.findViewById(R.id.gps_image);
            shoppingBasketImage = (ImageView) view.findViewById(R.id.shopping_basket);
            view.setTag(this);
        }
    }

    /**
     * Checks if google map is installed.
     *
     * @param uri
     * @return
     */
    private boolean isAppInstalled(String uri) {
        PackageManager pm = activity.getPackageManager();
        boolean app_installed = false;
        if (!TextUtils.isEmpty(uri)) {
            try {
                pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
                app_installed = true;
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Google map not installed reason=" + e.getLocalizedMessage());
            }
        }
        return app_installed;
    }
}
