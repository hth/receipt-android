package com.receiptofi.checkout.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.IconButton;
import android.widget.IconTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.receiptofi.checkout.BuildConfig;
import com.receiptofi.checkout.FilterListActivity;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.ReceiptListActivity;
import com.receiptofi.checkout.adapters.ReceiptItemListAdapter;
import com.receiptofi.checkout.model.ReceiptItemModel;
import com.receiptofi.checkout.model.ReceiptModel;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.Constants;
import com.receiptofi.checkout.views.ToastBox;

import java.text.ParseException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * User: PT
 * Date: 1/1/15 12:44 PM
 */
public class ReceiptDetailFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = ReceiptDetailFragment.class.getSimpleName();
    private static boolean dateSet = false;
    boolean mTypeFilter = false;
    int mCurrentIndex = -1;
    int mCurrentPosition = -1;
    int mCurrItemIndex = -1;
    // Receipt detail biz info
    private TextView rdBizName;
    private LinearLayout rdBizAddress;
    private TextView rdBizAddLine1;
    private TextView rdBizAddLine2;
    private TextView rdBizAddLine3;
    private TextView rdBizPhone;
    // Receipt detail date
    private TextView rdDate;
    // Receipt detail list item
    private ListView rdItemsList;
    private TextView taxDscpView;
    private TextView taxAmountView;
    private TextView totalAmountView;
    private List<ReceiptItemModel> itemList;

    private IconTextView tagIcon;
    private IconButton btnDownloadImage;
    private ImageView receiptImage;

    private String blobIds = "";
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "executing onCreateView");
        // Must call below method to make the fragment menu works.
        setHasOptionsMenu(true);
        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            mTypeFilter = savedInstanceState.getBoolean(Constants.ARG_TYPE_FILTER, false);
            mCurrentIndex = savedInstanceState.getInt(Constants.ARG_INDEX);
            mCurrentPosition = savedInstanceState.getInt(Constants.ARG_POSITION);
        }

        // Inflate the layout for this fragment
        View receiptDetailView = inflater.inflate(R.layout.receipt_detail_view, container, false);
        rdBizName = (TextView) receiptDetailView.findViewById(R.id.rd_biz_name);
        rdBizAddress = (LinearLayout) receiptDetailView.findViewById(R.id.rd_biz_address);
        rdBizAddLine1 = (TextView) receiptDetailView.findViewById(R.id.rd_biz_add_line1);
        rdBizAddLine2 = (TextView) receiptDetailView.findViewById(R.id.rd_biz_add_line2);
        rdBizAddLine3 = (TextView) receiptDetailView.findViewById(R.id.rd_biz_add_line3);
        rdBizPhone = (TextView) receiptDetailView.findViewById(R.id.rd_biz_phone);

        // Replace the phone textview left drawable icon with fa-phone.
        Drawable rdBizPhoneIcon = new IconDrawable(getActivity(), Iconify.IconValue.fa_phone_square)
                .colorRes(R.color.app_theme_bg)
                .sizeDp(18);
        rdBizPhone.setCompoundDrawables(rdBizPhoneIcon, null, null, null);
        rdDate = (TextView) receiptDetailView.findViewById(R.id.rd_date);

        rdItemsList = (ListView) receiptDetailView.findViewById(R.id.rd_items_list);
        rdItemsList.setEmptyView(receiptDetailView.findViewById(R.id.empty_view));

        // Add tax footer
        View taxFooter = View.inflate(getActivity(), R.layout.rd_item_list_footer_tax, null);
        taxDscpView = (TextView) taxFooter.findViewById(R.id.rd_item_list_footer_tax_dscp);
        taxAmountView = (TextView) taxFooter.findViewById(R.id.rd_item_list_footer_tax_amount);
        rdItemsList.addFooterView(taxFooter);

        // Add total footer
        View totalFooter = View.inflate(getActivity(), R.layout.rd_item_list_footer_total, null);
        totalAmountView = (TextView) totalFooter.findViewById(R.id.rd_item_list_footer_total_amount);
        rdItemsList.addFooterView(totalFooter);

        tagIcon = (IconTextView) receiptDetailView.findViewById(R.id.tag_icon);
        btnDownloadImage = (IconButton) receiptDetailView.findViewById(R.id.btn_download_receipt);
        receiptImage = (ImageView) receiptDetailView.findViewById(R.id.receiptImage);
        btnDownloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(blobIds)) {
                    String url = BuildConfig.AWSS3 + BuildConfig.AWSS3_BUCKET + blobIds;
                    if (getActivity() instanceof  ReceiptListActivity) {
                        ((ReceiptListActivity) getActivity()).showReceiptDetailImageFragment(url);
                    } else if (getActivity() instanceof FilterListActivity) {
                        ((FilterListActivity) getActivity()).showReceiptDetailImageFragment(url);
                    }
                } else {
                    SuperActivityToast superActivityToast = new SuperActivityToast(getActivity());
                    superActivityToast.setText("No Image for this receipt!");
                    superActivityToast.setDuration(SuperToast.Duration.EXTRA_LONG);
                    superActivityToast.setBackground(SuperToast.Background.BLUE);
                    superActivityToast.setTextColor(Color.WHITE);
                    superActivityToast.setTouchToDismiss(true);
                    superActivityToast.show();
                }


            }
        });
        return receiptDetailView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "executing onStart");

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateReceiptDetailView(args.getInt(Constants.ARG_INDEX), args.getInt(Constants.ARG_POSITION),
                    args.getBoolean(Constants.ARG_TYPE_FILTER, false));
            // Setup the local variable which will be used by onPrepareOptionsMenu function.
            mTypeFilter = args.getBoolean(Constants.ARG_TYPE_FILTER, false);
        } else if (mCurrentIndex != -1 && mCurrentPosition != -1) {
            // Set article based on saved instance state defined during onCreateView
            updateReceiptDetailView(mCurrentIndex, mCurrentPosition, mTypeFilter);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main_detail_receipt, menu);
        MenuItem receiptAction = menu.findItem(R.id.menu_receipt_actions)
                .setIcon(new IconDrawable(getActivity(), Iconify.IconValue.fa_tasks)
                        .colorRes(R.color.white)
                        .actionBarSize());

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        /**
         * Replace the default menu search image.
         */
        Drawable mDraw = new IconDrawable(getActivity(), Iconify.IconValue.fa_search)
                .colorRes(R.color.white)
                .actionBarSize();
        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageDrawable(mDraw);

        /**
         * Below if is designed for FilterListActivity.
         * Because this fragment can be used by both normal ReceiptList Activity and FilterList Activity
         */
        if (null != getActivity().getIntent() && !TextUtils.isEmpty(getActivity().getIntent().getStringExtra(SearchManager.QUERY))) {
            if (getActivity().getIntent().hasExtra(SearchManager.QUERY) && TextUtils.isEmpty(searchView.getQuery())) {
                searchView.setIconified(false);
                searchView.setQuery(getActivity().getIntent().getStringExtra(SearchManager.QUERY), false);
            }
            // Remove the default SearchView Icon
            int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
            ImageView magImage = (ImageView) searchView.findViewById(magId);
            magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }


        int autoCompleteTextViewID = getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView searchAutoCompleteTextView = (AutoCompleteTextView) searchView.findViewById(autoCompleteTextViewID);
        searchAutoCompleteTextView.setTextColor(Color.WHITE);
        searchAutoCompleteTextView.setHint("Search");
        searchAutoCompleteTextView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_receipt_actions:
                if (getActivity() instanceof ReceiptListActivity) {
                    if (((ReceiptListActivity) getActivity()).isDrawerOpened()) {
                        ((ReceiptListActivity) getActivity()).closeDrawer();
                    } else {
                        ((ReceiptListActivity) getActivity()).openDrawer();
                    }
                } else if (getActivity() instanceof FilterListActivity) {
                    if (((FilterListActivity) getActivity()).isDrawerOpened()) {
                        ((FilterListActivity) getActivity()).closeDrawer();
                    } else {
                        ((FilterListActivity) getActivity()).openDrawer();
                    }
                }
                return true;
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (!AppUtils.isTablet(getActivity())) {
                    getFragmentManager().popBackStack();
                    return true;
                } else {
                    // Allow the FilterListFragment to handle the Up button within Tablet Environment.
                    return false;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem rightDrawer = menu.findItem(R.id.menu_receipt_actions);
        // We only change drawer show or not within Tablet environment.
        if (AppUtils.isTablet(getActivity())) {
            if (mCurrentPosition == -1) {
                rightDrawer.setVisible(false);
            } else {
                rightDrawer.setVisible(true);
            }
        } else {
            rightDrawer.setVisible(true);
        }
    }

    public void updateReceiptDetailView(int index, int position, boolean isFilterList) {
        Log.d(TAG, "executing updateReceiptDetailView");
        // This Design only for Table Environment.
        if (AppUtils.isTablet(getActivity())) {
            this.mTypeFilter = isFilterList;
            // User Select item, we should update the menu to show right drawer.
            getActivity().invalidateOptionsMenu();
        }
        try {
            if (index == -1 || position == -1) {
                return;
            }

            final ReceiptModel rdModel;
            if (!isFilterList) {
                rdModel = ReceiptListFragment.children.get(index).get(position);
            } else {
                // Coming from FilterListActivity: we show and activate drawer view
                rdModel = FilterListFragment.children.get(index).get(position);
            }

            // Biz address
            rdBizName.setText(rdModel.getBizName());
            StringTokenizer tokenizer = new StringTokenizer(rdModel.getAddress(), ",");
            if (tokenizer.countTokens() <= 4) {
                rdBizAddLine1.setText((tokenizer.nextToken()).trim());
                String addressLine2 = "";
                while (tokenizer.hasMoreTokens()) {
                    addressLine2 = addressLine2 + tokenizer.nextToken() + ",";
                }
                addressLine2 = addressLine2.replaceAll(",$", "");
                rdBizAddLine2.setText(addressLine2.trim());
                rdBizAddLine3.setVisibility(View.GONE);
            } else {
                rdBizAddLine1.setText((tokenizer.nextToken()).trim());
                rdBizAddLine2.setText((tokenizer.nextToken()).trim());
                String addressLine3 = "";
                while (tokenizer.hasMoreTokens()) {
                    addressLine3 = addressLine3 + tokenizer.nextToken() + ",";
                }
                addressLine3 = addressLine3.replaceAll(",$", "");
                rdBizAddLine3.setText(addressLine3.trim());
                rdBizAddLine3.setVisibility(View.VISIBLE);
            }
            // Address action
            rdBizAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String geoQuery = "geo:" + rdModel.getLat() + "," + rdModel.getLng();
                    String uriString = geoQuery + "?q=" + Uri.encode(rdModel.getAddress()) + "(" + rdModel.getBizName() + ")" + "&z=16";
                    Uri uri = Uri.parse(uriString);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(mapIntent);
                }
            });

            //Phone action
            final String phoneNumber = rdModel.getPhone().trim();
            rdBizPhone.setText(phoneNumber);
            if (!AppUtils.isTablet(getActivity())) {
                rdBizPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                        dialIntent.setData(Uri.parse("tel:" + phoneNumber));
                        startActivity(dialIntent);
                    }
                });
            }

            // Date block
            String formattedDate = Constants.MMM_DD_DF.format(Constants.ISO_DF.parse(rdModel.getReceiptDate()));
            rdDate.setText(formattedDate);


            //Receipt item list Block
            // Add tax footer
            taxDscpView.setText(Double.toString(rdModel.getPtax()));
            taxAmountView.setText(Double.toString(rdModel.getTax()));

            // Add total footer
            totalAmountView.setText(Double.toString(rdModel.getTotal()));

            // Set Adaptor on items list
            rdItemsList.setAdapter(new ReceiptItemListAdapter(getActivity(), rdModel.getReceiptItems()));
            itemList = rdModel.getReceiptItems();
            if (Constants.SET_RECEIPT_REMINDER) {
                rdItemsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        final Calendar c = Calendar.getInstance();
                        DatePickerDialog dialog = new DatePickerDialog(getActivity(), ReceiptDetailFragment.this, c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                        mCurrItemIndex = position - 1;
                        String itemName = (itemList.get(mCurrItemIndex)).getName();
                        Log.d(TAG, "item name is: " + itemName);
                        dialog.setTitle(getActivity().getString(R.string.reminder_title) + " " + itemName);
                        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getActivity().getString(R.string.reminder_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // action cancelled- nothing to do
                                dateSet = false;
                            }
                        });
                        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getActivity().getString(R.string.reminder_set), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User has set date we need to add calendar event
                                dateSet = true;
                            }
                        });
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();
                        return true;
                    }
                });
            }

            // Update trackers
            mCurrentIndex = index;
            mCurrentPosition = position;

            // Set Tag Color
            /** Two checks. Check expenseTagModel is not null for avoiding to fail when expenseTagId is not empty. */
            if (!TextUtils.isEmpty(rdModel.getExpenseTagId()) && null != rdModel.getExpenseTagModel()) {
                String colorCode = rdModel.getExpenseTagModel().getColor();
                tagIcon.setTextColor(Color.parseColor(colorCode));
                tagIcon.setVisibility(View.VISIBLE);
            } else {
                tagIcon.setVisibility(View.INVISIBLE);
            }

            blobIds = rdModel.getBlobIds();

        } catch (ParseException e) {
            Log.d(TAG, "ParseException=" + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            Log.d(TAG, "reason=" + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "executing onSaveInstanceState");

        // Save the current article selection in case we need to recreate the fragment
        outState.putBoolean(Constants.ARG_TYPE_FILTER, mTypeFilter);
        outState.putInt(Constants.ARG_INDEX, mCurrentIndex);
        outState.putInt(Constants.ARG_POSITION, mCurrentPosition);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (!dateSet) {
            Log.d(TAG, "Cancelled event received in onDateSet- nothing to do");
            return;
        }

        Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth);

        ReceiptItemModel itemModel = itemList.get(mCurrItemIndex);
        ContentResolver cr = getActivity().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, cal.getTimeInMillis());
        values.put(Events.DTEND, cal.getTimeInMillis());
        values.put(Events.ALL_DAY, true);
        values.put("eventTimezone", TimeZone.getDefault().getID());
        values.put(Events.TITLE, getActivity().getString(R.string.event_title) + " " + itemModel.getName());
        values.put(Events.ORGANIZER, getActivity().getString(R.string.event_organizer));
        values.put(Events.DESCRIPTION, getActivity().getString(R.string.event_description_1) + itemModel.getName()
                + getActivity().getString(R.string.event_description_2) + " " + rdBizName.getText() + " "
                + getActivity().getString(R.string.event_description_3) + " " + rdDate.getText());
        values.put(Events.HAS_ALARM, true);
        values.put(Events.STATUS, Events.STATUS_CONFIRMED);

        // TODO: fix me - 1. No calendar
        // TODO:          2. Multiple calendar
        Map<Long, String> calendarMap = getAllCalendars();
        if (calendarMap != null && calendarMap.size() > 0) {
            Long calId = -1L;
            for (Entry entry : calendarMap.entrySet()) {
                calId = (Long) entry.getKey();
                break;
            }
            values.put(Events.CALENDAR_ID, calId);

            Uri uri = cr.insert(Events.CONTENT_URI, values);
            Long eventId = Long.parseLong(uri.getLastPathSegment());

            // Add reminder
            ContentValues reminders = new ContentValues();
            reminders.put(Reminders.EVENT_ID, eventId);
            reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
            reminders.put(Reminders.MINUTES, Constants.DEFAULT_REMINDER_TIME);

            // in case we would want to do something with event later
            Uri uri2 = cr.insert(Reminders.CONTENT_URI, reminders);

            ToastBox.makeText(getActivity(), getActivity().getString(R.string.reminder_toast) + " " + itemModel.getName(), Toast.LENGTH_SHORT).show();

        } else {
            //TODO: add logic to show - register for calendar
        }

    }

    private Map<Long, String> getAllCalendars() {
        Map<Long, String> calendarMap = new LinkedHashMap<>();
        String[] projection = new String[]{
                Calendars._ID,
                Calendars.NAME,
                Calendars.ACCOUNT_NAME,
                Calendars.ACCOUNT_TYPE}; // Keeping account name a type in case we want to use later.

        Cursor cursor = null;
        try {
            cursor = getActivity().getContentResolver().query(Calendars.CONTENT_URI,
                    projection,
                    Calendars.VISIBLE + " = 1",
                    null,
                    Calendars._ID + " ASC");
            if (cursor.moveToFirst()) {
                do {
                    calendarMap.put(cursor.getLong(0), cursor.getString(1));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting items for receipt " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        Log.d(TAG, "found calendars " + calendarMap.toString());
        return calendarMap;
    }
}
