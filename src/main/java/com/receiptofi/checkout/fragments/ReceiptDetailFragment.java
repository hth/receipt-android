package com.receiptofi.checkout.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.ReceiptListActivity;
import com.receiptofi.checkout.adapters.ReceiptItemListAdapter;
import com.receiptofi.checkout.model.ReceiptItemModel;
import com.receiptofi.checkout.model.ReceiptModel;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.Constants;

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

    public final static String ARG_INDEX = "index";
    public final static String ARG_POSITION = "position";
    private static final String TAG = ReceiptDetailFragment.class.getSimpleName();
    private static boolean dateSet = false;
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
    // Receipt action drawer
    private ImageView drawerIndicator;
    private List<ReceiptItemModel> itemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "executing onCreateView");
        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(ARG_INDEX);
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

        // Inflate the layout for this fragment
        View receiptDetailView = inflater.inflate(R.layout.receipt_detail_view, container, false);
        rdBizName = (TextView) receiptDetailView.findViewById(R.id.rd_biz_name);
        rdBizAddress = (LinearLayout) receiptDetailView.findViewById(R.id.rd_biz_address);
        rdBizAddLine1 = (TextView) receiptDetailView.findViewById(R.id.rd_biz_add_line1);
        rdBizAddLine2 = (TextView) receiptDetailView.findViewById(R.id.rd_biz_add_line2);
        rdBizAddLine3 = (TextView) receiptDetailView.findViewById(R.id.rd_biz_add_line3);
        rdBizPhone = (TextView) receiptDetailView.findViewById(R.id.rd_biz_phone);

        rdDate = (TextView) receiptDetailView.findViewById(R.id.rd_date);

        rdItemsList = (ListView) receiptDetailView.findViewById(R.id.rd_items_list);
        rdItemsList.setEmptyView(receiptDetailView.findViewById(R.id.empty_view));

        // Add list header
        View header = View.inflate(getActivity(), R.layout.rd_item_list_header, null);
        //header.setTag(TAG_HEADER);
        rdItemsList.addHeaderView(header);

        // Add tax footer
        View taxFooter = View.inflate(getActivity(), R.layout.rd_item_list_footer_tax, null);
        taxDscpView = (TextView) taxFooter.findViewById(R.id.rd_item_list_footer_tax_dscp);
        taxAmountView = (TextView) taxFooter.findViewById(R.id.rd_item_list_footer_tax_amount);
        rdItemsList.addFooterView(taxFooter);

        View totalFooter = View.inflate(getActivity(), R.layout.rd_item_list_footer_total, null);
        totalAmountView = (TextView) totalFooter.findViewById(R.id.rd_item_list_footer_total_amount);
        rdItemsList.addFooterView(totalFooter);

        drawerIndicator = (ImageView) receiptDetailView.findViewById(R.id.rd_drawer_indicator);

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
            updateReceiptDetailView(args.getInt(ARG_INDEX), args.getInt(ARG_POSITION));
        } else if (mCurrentIndex != -1 && mCurrentPosition != -1) {
            // Set article based on saved instance state defined during onCreateView
            updateReceiptDetailView(mCurrentIndex, mCurrentPosition);
        }
    }

    public void updateReceiptDetailView(int index, int position) {
        // Coming from ReceiptListActivity: we show and activate drawer view
        if (drawerIndicator.getVisibility() == View.GONE) {
            drawerIndicator.setVisibility(View.VISIBLE);
        }
        drawerIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ReceiptListActivity) getActivity()).openDrawer();
            }
        });

        updateReceiptDetailView(index, position, null);
    }

    public void updateReceiptDetailView(int index, int position, ReceiptModel rdModel) {
        Log.d(TAG, "executing updateReceiptDetailView");
        try {
            if (index == -1 || position == -1) {
                return;
            }

            if (rdModel == null) {
                rdModel = ReceiptListFragment.children.get(index).get(position);
            }

            // Biz address
            final String bizName = rdModel.getBizName();
            rdBizName.setText(bizName);

            // Address and phone block
            final String address = rdModel.getAddress();

            StringTokenizer tokenizer = new StringTokenizer(address, ",");
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

                    String uriBegin = "geo:" + address;
                    String query = address + "(" + bizName + ")";
                    String encodedQuery = Uri.encode(query);
                    String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
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
            // TODO fix me
            taxAmountView.setText(Double.toString(rdModel.getPtax()));

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

        } catch (ParseException e) {
            Log.d(TAG, "ParseException " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.d(TAG, "Exception " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "executing onSaveInstanceState");

        // Save the current article selection in case we need to recreate the fragment
        outState.putInt(ARG_INDEX, mCurrentIndex);
        outState.putInt(ARG_POSITION, mCurrentPosition);
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

            Toast.makeText(getActivity(), getActivity().getString(R.string.reminder_toast) + " " + itemModel.getName(), Toast.LENGTH_SHORT).show();

        } else {
            //TODO: add logic to show - register for calendar
        }

    }

    private Map<Long, String> getAllCalendars() {
        Map<Long, String> calendarMap = new LinkedHashMap<>();
        String[] projection = new String[]{Calendars._ID,
                Calendars.NAME,
                Calendars.ACCOUNT_NAME,
                Calendars.ACCOUNT_TYPE}; // Keeping account name a type in case we want to use later.

        Cursor calCursor = getActivity().getContentResolver().query(Calendars.CONTENT_URI,
                projection,
                Calendars.VISIBLE + " = 1",
                null,
                Calendars._ID + " ASC");
        if (calCursor.moveToFirst()) {
            do {
                calendarMap.put(calCursor.getLong(0), calCursor.getString(1));
            } while (calCursor.moveToNext());
        }
        Log.d(TAG, "found calendars " + calendarMap.toString());
        return calendarMap;
    }
}