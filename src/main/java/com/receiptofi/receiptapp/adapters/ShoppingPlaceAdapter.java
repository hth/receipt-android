package com.receiptofi.receiptapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.model.helper.Coordinate;
import com.receiptofi.receiptapp.model.helper.ShoppingPlace;
import com.receiptofi.receiptapp.model.types.DistanceUnit;

import java.util.List;

/**
 * User: hitender
 * Date: 12/8/15 5:07 AM
 */
public class ShoppingPlaceAdapter extends BaseAdapter {
    private static final String TAG = ShoppingPlaceAdapter.class.getSimpleName();
    private List<ShoppingPlace> shoppingPlaces;
    private Context context;

    private Drawable locationDraw;
    private Drawable locationOffDraw;
    private Drawable shoppingBasketDraw;
    private boolean mapInstalled;

    public ShoppingPlaceAdapter(Context context, List<ShoppingPlace> shoppingPlaces) {
        this.context = context;
        this.shoppingPlaces = shoppingPlaces;

        locationDraw = new IconDrawable(context, Iconify.IconValue.fa_map_marker)
                .colorRes(R.color.red)
                .actionBarSize();

        locationOffDraw = new IconDrawable(context, Iconify.IconValue.fa_map_marker)
                .colorRes(R.color.gray_light)
                .actionBarSize();

        shoppingBasketDraw = new IconDrawable(context, Iconify.IconValue.fa_shopping_cart)
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
            view = View.inflate(context, R.layout.shopping_place_list, null);
            new ViewHolder(view);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        ShoppingPlace shoppingPlace = (ShoppingPlace) getItem(i);
        holder.bizName.setText(shoppingPlace.getBizName());
        holder.lastPurchase.setText(context.getResources().getString(R.string.last_purchase, 20.00));

        if (shoppingPlace.getLastShopped().isEmpty()) {
            holder.lastShopped.setText("");
        } else {
            holder.lastShopped.setText(
                    context.getResources().getString(R.string.shopped,
                            NotificationAdapter.prettyTime.format(shoppingPlace.getLastShopped().get(0))));

            holder.shoppingBasketImage.setImageDrawable(shoppingBasketDraw);
        }

        if (!shoppingPlace.getDistance().isEmpty()) {
            holder.gpsImage.setImageDrawable(locationDraw);
            holder.bizDistance.setText(
                    context.getResources().getString(R.string.biz_distance,
                            shoppingPlace.getDistance().get(0), DistanceUnit.M.getName()));
        } else {
            holder.gpsImage.setImageDrawable(locationOffDraw);
            holder.bizDistance.setText(context.getResources().getString(R.string.biz_distance_empty, "---", DistanceUnit.M.getName()));
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

            String uriString = new StringBuilder()
                    .append("geo:").append(coordinate.getLat()).append(",").append(coordinate.getLng())
                    .append("?q=").append(Uri.encode(coordinate.getAddress()))
                    .append("(").append(Uri.encode(shoppingPlace.getBizName())).append(")")
                    .append("&z=16").toString();
            final Uri uri = Uri.parse(uriString);

            holder.bizDistance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                    mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    context.startActivity(mapIntent);
                }
            });

            holder.gpsImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                    mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    context.startActivity(mapIntent);
                }
            });
        }
    }

    class ViewHolder {
        TextView bizName;
        TextView lastPurchase;
        TextView lastShopped;
        TextView bizDistance;
        ImageView gpsImage;
        ImageView shoppingBasketImage;

        public ViewHolder(View view) {
            bizName = (TextView) view.findViewById(R.id.biz_name);
            lastPurchase = (TextView) view.findViewById(R.id.last_purchase);
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
        PackageManager pm = context.getPackageManager();
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
