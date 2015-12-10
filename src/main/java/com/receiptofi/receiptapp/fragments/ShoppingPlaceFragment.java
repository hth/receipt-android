package com.receiptofi.receiptapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.gc.materialdesign.views.ButtonFloat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.common.collect.Ordering;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.adapters.ShoppingPlaceAdapter;
import com.receiptofi.receiptapp.http.types.ExpenseTagSwipe;
import com.receiptofi.receiptapp.model.ShoppingItemModel;
import com.receiptofi.receiptapp.model.helper.Coordinate;
import com.receiptofi.receiptapp.model.helper.ShoppingPlace;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.db.ItemReceiptUtils;
import com.receiptofi.receiptapp.utils.db.ShoppingItemUtils;
import com.receiptofi.receiptapp.views.dialog.ExpenseTagDialog;

import junit.framework.Assert;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.DialogInterface.OnDismissListener;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

/**
 * User: hitender
 * Date: 12/7/15 4:11 AM
 */
public class ShoppingPlaceFragment extends Fragment implements OnDismissListener, ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    private static final String TAG = ShoppingPlaceFragment.class.getSimpleName();

    private final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    // Keys for storing activity state in the Bundle.
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    private View view;
    private ButtonFloat fbAddTag;
    private SwipeMenuListView mListView;
    private ShoppingPlaceAdapter mAdapter;
    private List<ShoppingItemModel> tagModelList;

    private Drawable edit;
    private Drawable delete;
    private Drawable alert;

    private List<ShoppingPlace> shoppingPlaces;

    private static Ordering<ShoppingPlace> SORT_BY_CLOSET_DISTANCE = new Ordering<ShoppingPlace>() {
        public int compare(ShoppingPlace right, ShoppingPlace left) {
            return Double.compare(right.getDistance().get(0), left.getDistance().get(0));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        shoppingPlaces = ShoppingItemUtils.getBusinessName();
        // Inflate the layout for this fragment
        if (shoppingPlaces.isEmpty()) {
            view = inflater.inflate(R.layout.fragment_shopping_places_empty, container, false);
        } else {
            shoppingPlaces = ItemReceiptUtils.populateShoppingPlaces(shoppingPlaces);
            view = inflater.inflate(R.layout.fragment_shopping_places, container, false);
            setupView(shoppingPlaces);
        }

        return view;
    }

    private void setupView(List<ShoppingPlace> shoppingPlaces) {
        fbAddTag = (ButtonFloat) view.findViewById(R.id.buttonFloat);
        fbAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment editTagDialog = ExpenseTagDialog.newInstance(null);
                editTagDialog.show(ft, "dialog");
            }
        });

        if (!shoppingPlaces.isEmpty()) {
            mListView = (SwipeMenuListView) view.findViewById(R.id.listView);
            mAdapter = new ShoppingPlaceAdapter(getActivity(), shoppingPlaces);
            mListView.setAdapter(mAdapter);
        }

        edit = new IconDrawable(getActivity(), Iconify.IconValue.fa_pencil_square_o)
                .colorRes(R.color.white)
                .sizePx(64);

        delete = new IconDrawable(getActivity(), Iconify.IconValue.fa_trash_o)
                .colorRes(R.color.white)
                .sizePx(64);

        alert = new IconDrawable(getActivity(), Iconify.IconValue.fa_exclamation_triangle)
                .colorRes(R.color.red)
                .actionBarSize();

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(getActivity());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set a icon
                openItem.setIcon(edit);
                // add to menu
                menu.addMenuItem(openItem);
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);
        // step 2. listener item click event
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                ExpenseTagSwipe expenseTagSwipe = ExpenseTagSwipe.findSwipeTypeByCode(index);
                Assert.assertNotNull(expenseTagSwipe);
                Log.d(TAG, "Selected swipe action is: " + expenseTagSwipe.name());

                final ShoppingItemModel tagModel = tagModelList.get(position);
                Log.d(TAG, "Selected tag name is: " + tagModel.getName());
                switch (expenseTagSwipe) {
                    case EDIT:
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);

                        /** Create and show the dialog.*/
                        DialogFragment editTagDialog = ExpenseTagDialog.newInstance(tagModel.getName());
                        editTagDialog.show(ft, "dialog");

                        break;
                    case DELETE:
                        deleteExpenseTag(tagModel);
                        break;
                    default:
                        Log.e(TAG, "Reached unsupported condition, expense tag swipe index=" + index);
                        throw new RuntimeException("Reached unreachable condition");
                }
                return false;
            }
        });
    }

    private void updateGeoCoordinates() {
        LocationManager service = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            alertInactiveGps();
        } else if (null == mCurrentLocation) {

        } else {
            //Update co-ordinates
            for (ShoppingPlace shoppingPlace : shoppingPlaces) {
                shoppingPlace.computeDistanceFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            }

            shoppingPlaces = SORT_BY_CLOSET_DISTANCE.sortedCopy(shoppingPlaces);
            notifyList();
        }
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            updateGeoCoordinates();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateGeoCoordinates();
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateGeoCoordinates();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }
//TODO for API 23
//    private void checkPermission() {
//        if (checkAccessLocation(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                checkAccessLocation(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            List<String> permissionsNeeded = new ArrayList<>();
//            final List<String> permissionsList = new ArrayList<>();
//
//            if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
//                permissionsNeeded.add("GPS");
//            if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
//                permissionsNeeded.add("Read Contacts");
//
//            if (permissionsList.size() > 0) {
//                if (permissionsNeeded.size() > 0) {
//                    // Need Rationale
//                    String message = "You need to grant access to " + permissionsNeeded.get(0);
//                    for (int i = 1; i < permissionsNeeded.size(); i++) {
//                        message = message + ", " + permissionsNeeded.get(i);
//                    }
//
//                    showMessageOKCancel(message,
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
//                                    }
//                                }
//                            });
//                    return;
//                }
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
//                }
//                return;
//            }
//        }
//    }
//
//    private int checkAccessLocation(String accessFineLocation) {
//        return ActivityCompat.checkSelfPermission(AppUtils.getHomePageContext(), accessFineLocation);
//    }
//
//    private boolean addPermission(List<String> permissionsList, String permission) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
//                permissionsList.add(permission);
//                // Check for Rationale Option
//                if (!shouldShowRequestPermissionRationale(permission))
//                    return false;
//            }
//        }
//        return true;
//    }
//
//    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            new AlertDialog.Builder(getContext())
//                    .setMessage(message)
//                    .setPositiveButton("OK", okListener)
//                    .setNegativeButton("Cancel", null)
//                    .create()
//                    .show();
//        }
//    }

    private AlertDialog alertInactiveGps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setTitle(getString(R.string.enable_gps_title))
                .setMessage(getString(R.string.enable_gps_message))
                .setNegativeButton(getString(R.string.no_button), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(getString(R.string.yes_button), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setIcon(new IconDrawable(getActivity(), Iconify.IconValue.fa_map_marker)
                        .colorRes(R.color.app_theme_bg)
                        .actionBarSize())
                .show();
    }

    private void deleteExpenseTag(final ShoppingItemModel shoppingItemModel) {

    }

    private void notifyList() {
        if (mAdapter != null) {
            mAdapter.updateList(shoppingPlaces);
            mAdapter.notifyDataSetChanged();
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
