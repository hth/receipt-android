package com.receiptofi.checkout.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.adapters.NotificationAdapter;
import com.receiptofi.checkout.model.NotificationModel;
import com.receiptofi.checkout.utils.db.NotificationUtils;

import java.util.List;

public class NotificationFragment extends Fragment {
    private static final String TAG = NotificationFragment.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private View mView;
    private ListView listView;
    private TextView mEmptyTextview;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_notification, container, false);
        setupView();
        // start query
        new NotificationDataTask().execute();
        return mView;
    }

    private void setupView() {
        listView = (ListView) mView.findViewById(R.id.notification_list);
        mEmptyTextview = (TextView) mView.findViewById(R.id.empty_view);
    }

    private void setListData(List<NotificationModel> notificationModels) {
        listView.setAdapter(new NotificationAdapter(getActivity(), notificationModels));
    }

    private class NotificationDataTask extends AsyncTask<Void, Void, List<NotificationModel>> {

        @Override
        protected List<NotificationModel> doInBackground(Void... voids) {
            return NotificationUtils.getAll();
        }

        @Override
        protected void onPostExecute(List<NotificationModel> notificationModels) {
            setListData(notificationModels);
            // Show up the empty view when this user has empty notification.
            if (notificationModels.size() == 0) {
                mEmptyTextview.setVisibility(View.VISIBLE);
            }
        }
    }
}
