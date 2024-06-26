package com.receiptofi.receiptapp.fragments;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.receiptofi.receiptapp.HomeActivity;
import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.ReceiptListActivity;
import com.receiptofi.receiptapp.adapters.ReceiptListAdapter;
import com.receiptofi.receiptapp.model.ReceiptGroupHeader;
import com.receiptofi.receiptapp.model.ReceiptGroupObservable;
import com.receiptofi.receiptapp.model.ReceiptModel;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.views.PinnedHeaderExpandableListView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * User: PT
 * Date: 1/1/15 12:44 PM
 */
public class ReceiptListFragment extends Fragment implements PinnedHeaderExpandableListView.OnHeaderUpdateListener {
    private static final String TAG = ReceiptListFragment.class.getSimpleName();

    public static final int RECEIPT_MODEL_UPDATED = 0x2436;

    public static List<ReceiptGroupHeader> groups = new LinkedList<>();
    public static List<List<ReceiptModel>> children = new LinkedList<>();

    public ReceiptListAdapter adapter = null;

    private PinnedHeaderExpandableListView explv;
    private OnReceiptSelectedListener mCallback;
    private SearchView searchView;
    private static ReceiptGroupObservable receiptGroupObservable = ReceiptGroupObservable.getInstance();
    private DataSetObserver receiptGroupObserver;

    private DateFormat inputDF = new SimpleDateFormat("M yyyy", Locale.US);
    private DateFormat outputDF = new SimpleDateFormat("MMM yyyy", Locale.US);

    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case RECEIPT_MODEL_UPDATED:
                    Log.d(TAG, "receiptGroupObserver onChanged RECEIPT_MODEL_UPDATED");
                    groups = ReceiptGroupObservable.getMonthlyReceiptGroup().getReceiptGroupHeaders();
                    children = ReceiptGroupObservable.getMonthlyReceiptGroup().getReceiptModels();
                    ((ReceiptListAdapter) explv.getExpandableListAdapter()).notifyDataSetChanged();
                    int groupPosition = ((HomeActivity) getActivity()).getGroupIndex();
                    int childPosition = ((HomeActivity) getActivity()).getChildIndex();

                    // we wouldn't want to run this, if there is nothing selected in list
                    if (groupPosition != -1 && childPosition != -1) {
                        Log.d(TAG, "groupPosition at start is: " + groupPosition + " childPosition at start is: " + childPosition);
                        if (groups.size() > groupPosition) {
                            if (children.get(groupPosition).size() > childPosition) {
                                // In this case there is current group has a child at same position
                                Log.d(TAG, "Keeping group and child position as it is");
                                Log.d(TAG, "groupPosition line 58 is: " + groupPosition + " childPosition is: " + childPosition);
                                ((ReceiptListActivity) getActivity()).onReceiptSelected(groupPosition, childPosition);
                            } else {
                                // In this case last child was deleted from current group, but the group have more children
                                ((ReceiptListActivity) getActivity()).setChildIndex(--childPosition);
                                childPosition = ((ReceiptListActivity) getActivity()).getChildIndex();
                                Log.d(TAG, "Reducing child position");
                                Log.d(TAG, "groupPosition line 64 is: " + groupPosition + " childPosition is: " + childPosition);
                                if (explv != null) {
                                    explv.expandGroup(groupPosition);
                                    int index = explv.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                                    explv.setItemChecked(index, true);
                                }
                                ((ReceiptListActivity) getActivity()).onReceiptSelected(groupPosition, childPosition);
                            }
                        } else {
                            // In this case last child was deleted from current group, but the group doesn't has more children
                            ((ReceiptListActivity) getActivity()).setGroupIndex(--groupPosition);
                            groupPosition = ((ReceiptListActivity) getActivity()).getGroupIndex();
                            ((ReceiptListActivity) getActivity()).setChildIndex((children.get(groupPosition).size()) - 1);
                            childPosition = ((ReceiptListActivity) getActivity()).getChildIndex();
                            Log.d(TAG, "Reducing group position & setting child position");
                            Log.d(TAG, "groupPosition line 71 is: " + groupPosition + " childPosition is: " + childPosition);
                            if (explv != null) {
                                explv.expandGroup(groupPosition);
                                int index = explv.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                                explv.setItemChecked(index, true);
                            }
                            ((ReceiptListActivity) getActivity()).onReceiptSelected(groupPosition, childPosition);
                        }
                    }
                    break;
                default:
                    Log.e(TAG, "Update handler not defined for: " + what);
            }
            return true;
        }
    });

    public ReceiptListFragment() {
        super();
        if (receiptGroupObservable != null) {
            groups = ReceiptGroupObservable.getMonthlyReceiptGroup().getReceiptGroupHeaders();
            children = ReceiptGroupObservable.getMonthlyReceiptGroup().getReceiptModels();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiptGroupObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                updateHandler.sendEmptyMessage(RECEIPT_MODEL_UPDATED);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        receiptGroupObservable.registerObserver(receiptGroupObserver);
        /** Remove selected receipt from list to avoid re-loading of the detail receipt when refresh is complete. */
        ((HomeActivity) getActivity()).setGroupIndex(-1);
        ((HomeActivity) getActivity()).setChildIndex(-1);

        /** When resume, refresh anyway to update list with any changes. */
        updateHandler.sendEmptyMessage(RECEIPT_MODEL_UPDATED);
    }

    @Override
    public void onPause() {
        super.onPause();
        receiptGroupObservable.unregisterObserver(receiptGroupObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.receipt_list_fragment, container, false);
        /** Must call below method to make the fragment menu works. */
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // We only add menu within Phone environment.
        if (!AppUtils.isTablet(getActivity())) {
            inflater.inflate(R.menu.menu_main, menu);


            // Associate searchable configuration with the SearchView
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            /**
             * Replace the default menu search image.
             */
            Drawable mDraw = new IconDrawable(getActivity(), FontAwesomeIcons.fa_search)
                    .colorRes(R.color.white)
                    .actionBarSize();
           /* int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
            ImageView v = (ImageView) searchView.findViewById(searchImgId);
            v.setImageDrawable(mDraw);*/
/*
            int autoCompleteTextViewID = getResources().getIdentifier("android:id/search_src_text", null, null);
            AutoCompleteTextView searchAutoCompleteTextView = (AutoCompleteTextView) searchView.findViewById(autoCompleteTextViewID);
            searchAutoCompleteTextView.setTextColor(Color.WHITE);
            searchAutoCompleteTextView.setHint("Search");
            searchAutoCompleteTextView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);*/
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_receipt_actions:
               /* if (((ReceiptListActivity) getActivity()).isDrawerOpened()) {
                    ((ReceiptListActivity) getActivity()).closeDrawer();
                } else {
                    ((ReceiptListActivity) getActivity()).openDrawer();
                }*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        explv = (PinnedHeaderExpandableListView) view.findViewById(R.id.exp_list_view);
        explv.setEmptyView(view.findViewById(R.id.empty_view));
        explv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        Log.d(TAG, "****************************        receiptGroupObserver registered");

        adapter = new ReceiptListAdapter(getActivity());
        explv.setAdapter(adapter);
        explv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                expandableListView.setItemChecked(index, true);
                // If searchView is focused, we should clear up the focus to hidden the keyboard for the detail page.
                if (null != searchView && checkFocusRec(searchView)) {
                    searchView.clearFocus();
                }
                ((HomeActivity) getActivity()).onReceiptSelected(groupPosition, childPosition);
                return false;
            }
        });
        // We only expand group when the user have receipts.
        if (adapter.getGroupCount() > 0) {
            // expand all group
            for (int i = 0, count = explv.getCount(); i < count; i++) {
                explv.expandGroup(i);
            }
            explv.setOnHeaderUpdateListener(this);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnReceiptSelectedListener) context;
        } catch (ClassCastException e) {
            Log.e(TAG, "reason=" + e.getLocalizedMessage(), e);
            throw new ClassCastException(context.toString() + " must implement OnReceiptSelectedListener");
        }
    }

    @Override
    public View getPinnedHeader() {
        View headerView = getActivity().getLayoutInflater().inflate(R.layout.receipt_list_parent, null);
        headerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        return headerView;
    }

    @Override
    public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {
        ReceiptGroupHeader firstVisibleGroup = (ReceiptGroupHeader) adapter.getGroup(firstVisibleGroupPos);
        TextView header_month = (TextView) headerView.findViewById(R.id.exp_list_header_month);
        TextView header_amount = (TextView) headerView.findViewById(R.id.exp_list_header_amount);
        try {
            header_month.setText(getActivity().getString(R.string.receipt_list_header_month,
                    outputDF.format(inputDF.parse(firstVisibleGroup.getMonth() + " " + firstVisibleGroup.getYear())),
                    firstVisibleGroup.getCount()));

            header_amount.setText(getActivity().getString(R.string.receipt_list_header_amount,
                    AppUtils.currencyFormatter().format(firstVisibleGroup.getTotal())));
        } catch (ParseException e) {
            Log.e(TAG, "ParseException " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e.getLocalizedMessage(), e);
        }
    }

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnReceiptSelectedListener {
        /**
         * Called by HeadlinesFragment when a list item is selected.
         */
        void onReceiptSelected(int index, int position);
    }

    private boolean checkFocusRec(View view) {
        if (view.isFocused())
            return true;

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (checkFocusRec(viewGroup.getChildAt(i)))
                    return true;
            }
        }
        return false;
    }


    public static ReceiptListFragment getInstance()
    {
        ReceiptListFragment fragment = new ReceiptListFragment();
        return fragment;
    }
}
