package com.receiptofi.receiptapp.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.widget.IconTextView;
import com.receiptofi.receiptapp.BuildConfig;
import com.receiptofi.receiptapp.FilterListActivity;
import com.receiptofi.receiptapp.HomeActivity;
import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.ReceiptListActivity;
import com.receiptofi.receiptapp.adapters.ExpenseTagAdapterRecycleView;
import com.receiptofi.receiptapp.adapters.ReceiptItemListAdapter;
import com.receiptofi.receiptapp.model.ExpenseTagModel;
import com.receiptofi.receiptapp.model.ReceiptDetailObservable;
import com.receiptofi.receiptapp.model.ReceiptItemModel;
import com.receiptofi.receiptapp.model.ReceiptModel;
import com.receiptofi.receiptapp.model.ReceiptSplitModel;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.Constants;
import com.receiptofi.receiptapp.utils.db.ExpenseTagUtils;
import com.receiptofi.receiptapp.utils.db.ReceiptSplitUtils;
import com.receiptofi.receiptapp.utils.db.ReceiptUtils;
import com.receiptofi.receiptapp.views.ToastBox;

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
    private  TextView rdBizSplitTotal;
    private TextView rdBizSplitCount;

    private IconTextView tagIcon;
    private Button btnDownloadImage;
    private ImageView receiptImage;
    private ImageView rdBizSplitImg;
    private View rdBizTagColor;
    private Button rdBtnDetailList,rdbtnOptionList,btnVerifyReceipt,btnDeleteReceipt;
    private RecyclerView expenseTagRecycleView;
    private EditText edtNotes;
    private String blobIds = "";
    private SearchView searchView;
    private DataSetObserver observer;
    public static ReceiptDetailObservable receiptDetailObservable = ReceiptDetailObservable.getInstance();
    private String receiptId;
    private LinearLayout rdListLayout,rdOptionList;

    public static final int RECEIPT_DETAIL_REFRESHED = 0x4436;
    public List<ExpenseTagModel> expenseTagModelList;
    private List<ReceiptSplitModel> rSplitModelList;

    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case RECEIPT_DETAIL_REFRESHED:
                    /** Update any visual changes if any. */
                    Log.i(TAG, "Receipt refreshed.");
                    final ReceiptModel receiptModel = ReceiptUtils.fetchReceipt(msg.obj.toString());
                    if (receiptModel != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                setTagColor(receiptModel);
                            }
                        });
                    } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(getActivity(), ReceiptListActivity.class);
                                startActivity(i);
                                getActivity().finish();
                            }
                        });
                    }
                    break;
                default:
            }

            return true;
        }
    });



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        expenseTagModelList = getAllExpenseTag();
        observer = new DataSetObserver() {
            @Override
            public void onChanged() {
                Message message = new Message();
                message.obj = receiptId;
                message.what = RECEIPT_DETAIL_REFRESHED;
                updateHandler.dispatchMessage(message);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        receiptDetailObservable.registerObserver(observer);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof ReceiptListActivity) {
            if (((ReceiptListActivity) getActivity()).isDrawerOpened()) {
                ((ReceiptListActivity) getActivity()).closeDrawer();
            }
            receiptDetailObservable.unregisterObserver(observer);
        } else if (getActivity() instanceof FilterListActivity) {
            if (((FilterListActivity) getActivity()).isDrawerOpened()) {
                ((FilterListActivity) getActivity()).closeDrawer();
            }
            receiptDetailObservable.unregisterObserver(observer);
        } else {
            Log.w(TAG, "Not an instanceof ReceiptListActivity or FilterListActivity");
        }
    }

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
        rdBizSplitTotal = (TextView)receiptDetailView.findViewById(R.id.tvSplitTotal);
        rdBizSplitCount = (TextView)receiptDetailView.findViewById(R.id.tvSplitCount);
        rdBizSplitImg = (ImageView)receiptDetailView.findViewById(R.id.imvReceiptCount);
        rdBizTagColor = (View)receiptDetailView.findViewById(R.id.exp_list_child_tag_color);
        rdBtnDetailList = (Button)receiptDetailView.findViewById(R.id.btn_rddetailList);
        rdbtnOptionList = (Button)receiptDetailView.findViewById(R.id.btn_rdoptions);
        expenseTagRecycleView = (RecyclerView)receiptDetailView.findViewById(R.id.chooseExpenseRecyclerView);
                rdListLayout = (LinearLayout)receiptDetailView.findViewById(R.id.detaillistLayout);
        rdOptionList =(LinearLayout)receiptDetailView.findViewById(R.id.optionsLayout);

        // Replace the phone textview left drawable icon with fa-phone.
        Drawable rdBizPhoneIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_phone_square)
                .colorRes(R.color.app_theme_bg)
                .sizeDp(18);
        rdBizPhone.setCompoundDrawables(rdBizPhoneIcon, null, null, null);
        rdDate = (TextView) receiptDetailView.findViewById(R.id.rd_date);

        rdItemsList = (ListView) receiptDetailView.findViewById(R.id.rd_items_list);
        rdItemsList.setEmptyView(receiptDetailView.findViewById(R.id.empty_view));

        // Add tax footer
        View taxFooter = View.inflate(getActivity(), R.layout.rd_item_list_footer_tax, null);
        taxAmountView = (TextView) taxFooter.findViewById(R.id.rd_item_list_footer_tax_amount);
        rdItemsList.addFooterView(taxFooter);

        // Add total footer
        View totalFooter = View.inflate(getActivity(), R.layout.rd_item_list_footer_total, null);
        totalAmountView = (TextView) totalFooter.findViewById(R.id.rd_item_list_footer_total_amount);
        rdItemsList.addFooterView(totalFooter);

       // tagIcon = (IconTextView) receiptDetailView.findViewById(R.id.tag_icon);
        btnDownloadImage = (Button) receiptDetailView.findViewById(R.id.btnDownloadReceipt);
        btnVerifyReceipt = (Button)receiptDetailView.findViewById(R.id.btnVerifyReceiptAgain);
        receiptImage = (ImageView) receiptDetailView.findViewById(R.id.receiptImage);
        btnDeleteReceipt = (Button)receiptDetailView.findViewById(R.id.btnDeleteReceipt);

        edtNotes = (EditText)receiptDetailView.findViewById(R.id.edtNotes);
        btnDownloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(blobIds)) {
                    String url = BuildConfig.AWSS3 + BuildConfig.AWSS3_BUCKET + blobIds;
                    if (getActivity() instanceof HomeActivity) {
                        ((HomeActivity) getActivity()).showReceiptDetailImageFragment(url);
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

        rdBtnDetailList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailListOptionsLayoutVisibleInvisible(true);
            }
        });

        rdbtnOptionList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailListOptionsLayoutVisibleInvisible(false);
            }
        });

        //Edit Note ------ Click Listner
        edtNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                final EditText edittext = new EditText(getActivity());
                edittext.setText(edtNotes.getText().toString());
                alertDialog.setMessage("Enter Your Message");
                alertDialog.setTitle("Enter Your Title");
                alertDialog.setView(edittext);

                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       edtNotes.setText(edittext.getText().toString());
                    }
                });
                alertDialog.show();

            }
        });


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(),5);
        expenseTagRecycleView.setLayoutManager(layoutManager);
        expenseTagRecycleView.setItemAnimator(new DefaultItemAnimator());
        expenseTagRecycleView.setAdapter(new ExpenseTagAdapterRecycleView(getActivity(),expenseTagModelList));
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
        rSplitModelList = ReceiptSplitUtils.getReceiptSplit(receiptId);
        detailListOptionsLayoutVisibleInvisible(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

      /*  inflater.inflate(R.menu.menu_main_detail_receipt, menu);
        MenuItem receiptAction = menu.findItem(R.id.menu_receipt_actions)
                .setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_tasks)
                        .colorRes(R.color.white)
                        .actionBarSize());

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        *//**
         * Replace the default menu search image.
         *//*
        Drawable mDraw = new IconDrawable(getActivity(), FontAwesomeIcons.fa_search)
                .colorRes(R.color.white)
                .actionBarSize();
        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageDrawable(mDraw);

        *//**
         * Below if is designed for FilterListActivity.
         * Because this fragment can be used by both normal ReceiptList Activity and FilterList Activity
         *//*
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
      */  super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
       /* switch (item.getItemId()) {
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
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        /*MenuItem rightDrawer = menu.findItem(R.id.menu_receipt_actions);
        // We only change drawer show or not within Tablet environment.
        if (AppUtils.isTablet(getActivity())) {
            if (mCurrentPosition == -1) {
                rightDrawer.setVisible(false);
            } else {
                rightDrawer.setVisible(true);
            }
        } else {
            rightDrawer.setVisible(true);
        }*/
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

            final ReceiptModel receiptModel;
            if (!isFilterList) {
                receiptModel = ReceiptListFragment.children.get(index).get(position);
            } else {
                // Coming from FilterListActivity: we show and activate drawer view
                receiptModel = FilterListFragment.children.get(index).get(position);
            }
            receiptId = receiptModel.getId();

            // Biz address
            rdBizName.setText(receiptModel.getBizName());
            if (!TextUtils.isEmpty(receiptModel.getAddress())) {
                StringTokenizer tokenizer = new StringTokenizer(receiptModel.getAddress(), ",");
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

                if (isAppInstalled("com.google.android.apps.maps")) {
                    addAddressListener(receiptModel);
                }
            } else {
                rdBizAddLine1.setText("");
                rdBizAddLine2.setText("");
                rdBizAddLine3.setText("");
            }

            //Phone action
            final String phoneNumber = receiptModel.getPhone().trim();
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
            String formattedDate = Constants.MMM_DD_DF.format(Constants.ISO_DF.parse(receiptModel.getReceiptDate()));
            rdDate.setText(formattedDate);

            //Receipt item list Block
            // Add tax footer
            taxAmountView.setText(AppUtils.currencyFormatter().format(receiptModel.getTax()));

            // Add total footer
            totalAmountView.setText(AppUtils.currencyFormatter().format(receiptModel.getTotal()));

            // Set Adaptor on items list
            rdItemsList.setAdapter(new ReceiptItemListAdapter(getActivity(), receiptModel.getReceiptItems()));
            itemList = receiptModel.getReceiptItems();
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
            // ---- add split cout ------
            if(receiptModel.getSplitCount()>1) {
                rdBizSplitCount.setText("+ "+String.valueOf(receiptModel.getSplitCount()));
                rdBizSplitImg.setVisibility(View.VISIBLE);
            }
            else
            {
                rdBizSplitImg.setVisibility(View.INVISIBLE);
            }
            rdBizSplitTotal.setText(getActivity().getString(
                    R.string.receipt_list_child_amount,
                    AppUtils.currencyFormatter().format(receiptModel.getSplitTotal())));

            // Update trackers
            mCurrentIndex = index;
            mCurrentPosition = position;

            // Set Tag Color
            setTagColor(receiptModel);

            blobIds = receiptModel.getBlobIds();

        } catch (ParseException e) {
            Log.d(TAG, "ParseException=" + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            Log.d(TAG, "reason=" + e.getLocalizedMessage(), e);
        }
    }

    private void setTagColor(ReceiptModel receiptModel) {
        /** Two checks. Check expenseTagModel is not null for avoiding to fail when expenseTagId is not empty. */
        if (!TextUtils.isEmpty(receiptModel.getExpenseTagId()) && null != receiptModel.getExpenseTagModel()) {
            String colorCode = receiptModel.getExpenseTagModel().getColor();
          //  tagIcon.setTextColor(Color.parseColor(colorCode));
         //   tagIcon.setVisibility(View.VISIBLE);
            rdBizTagColor.setBackgroundColor(Color.parseColor(colorCode));
        } else {
           // tagIcon.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Add listener to address.
     *
     * @param receiptModel
     */
    private void addAddressListener(final ReceiptModel receiptModel) {
        rdBizAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uriString = new StringBuilder()
                        .append("geo:").append(receiptModel.getLat()).append(",").append(receiptModel.getLng())
                        .append("?q=").append(Uri.encode(receiptModel.getAddress()))
                        .append("(").append(receiptModel.getBizName()).append(")")
                        .append("&z=16").toString();
                Uri uri = Uri.parse(uriString);

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(mapIntent);
            }
        });
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

    /**
     * Checks if google map is installed.
     *
     * @param uri
     * @return
     */
    private boolean isAppInstalled(String uri) {
        PackageManager pm = getActivity().getPackageManager();
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

  /*  navigate to previous fragment */

     private void detailListOptionsLayoutVisibleInvisible(boolean isDetailListVisible)
     {
         if(isDetailListVisible)
         {

             rdListLayout.setVisibility(View.VISIBLE);
             rdOptionList.setVisibility(View.GONE);
         }
         else
         {
             rdListLayout.setVisibility(View.GONE);
             rdOptionList.setVisibility(View.VISIBLE);


         }
     }

    private List<ExpenseTagModel>  getAllExpenseTag()
    {
        //ExpenseTagUtils expenseTagUtils = new ExpenseTagUtils();
        List<ExpenseTagModel> expenseTagModelList = ExpenseTagUtils.getAll();
        return expenseTagModelList;
    }

}
