package com.receiptofi.checkout.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.receiptofi.checkout.FilterListActivity;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.adapters.FilterListAdapter;
import com.receiptofi.checkout.model.ReceiptGroup;
import com.receiptofi.checkout.model.ReceiptGroupHeader;
import com.receiptofi.checkout.model.ReceiptModel;
import com.receiptofi.checkout.utils.AppUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * User: PT
 * Date: 3/28/15 7:11 AM
 */
public class FilterListFragment extends Fragment {

    private static final String TAG = FilterListFragment.class.getSimpleName();
    public static List<ReceiptGroupHeader> groups = new LinkedList<>();
    public static List<List<ReceiptModel>> children = new LinkedList<>();
    public static boolean hideTotal;
    private View rootView;
    private ExpandableListView explv;
    private OnReceiptSelectedListener mCallback;
    SearchView searchView;

    public FilterListFragment() {
        super();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "executing onCreateView");
        rootView = inflater.inflate(R.layout.receipt_list_fragment, container, false);
        // We only add menu within Phone environment.
        if (!AppUtils.isTablet(getActivity())) {
            // Must call below method to make the fragment menu works. REALLY IMPORTANT.
            setHasOptionsMenu(true);
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "executing onCreateOptionsMenu");
        inflater.inflate(R.menu.menu_main, menu);
        setSearchConfig(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        explv = (ExpandableListView) view.findViewById(R.id.exp_list_view);
        explv.setEmptyView(view.findViewById(R.id.empty_view));
        explv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        final FilterListAdapter adapter = new FilterListAdapter(getActivity());
        explv.setAdapter(adapter);
        explv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                expandableListView.setItemChecked(index, true);

                ((FilterListActivity) getActivity()).onReceiptSelected(groupPosition, childPosition);
                return true;
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnReceiptSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnReceiptSelectedListener");
        }
    }

    public void notifyDataChanged(ReceiptGroup receiptGroup) {
        groups = receiptGroup.getReceiptGroupHeaders();
        children = receiptGroup.getReceiptModels();
        hideTotal = ((FilterListActivity) getActivity()).hideTotal();
        ((FilterListAdapter) explv.getExpandableListAdapter()).notifyDataSetChanged();
    }

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnReceiptSelectedListener {
        /**
         * Called by HeadlinesFragment when a list item is selected
         */
        void onReceiptSelected(int index, int position);
    }

    private SearchView setSearchConfig(Menu menu) {
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setIconified(false);
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

        // Remove the default SearchView Icon
        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = (ImageView) searchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        if (getActivity().getIntent().hasExtra(SearchManager.QUERY) && TextUtils.isEmpty(searchView.getQuery())) {
            searchView.setQuery(getActivity().getIntent().getStringExtra(SearchManager.QUERY), false);
        }

        int autoCompleteTextViewID = getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView searchAutoCompleteTextView = (AutoCompleteTextView) searchView.findViewById(autoCompleteTextViewID);
        searchAutoCompleteTextView.setTextColor(Color.WHITE);
        searchAutoCompleteTextView.setHint("Search");
        searchAutoCompleteTextView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        return searchView;
    }
}
