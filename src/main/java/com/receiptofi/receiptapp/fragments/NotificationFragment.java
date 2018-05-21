package com.receiptofi.receiptapp.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.adapters.NotificationAdapter;
import com.receiptofi.receiptapp.model.NotificationModel;
import com.receiptofi.receiptapp.utils.db.NotificationUtils;

import java.util.List;

public class NotificationFragment extends Fragment {
    private static final String TAG = NotificationFragment.class.getSimpleName();


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_notification, container, false);
        setupView();
        new NotificationDataTask().execute();
        return mView;
    }

    private void setupView() {
        listView = (ListView) mView.findViewById(R.id.notification_list);
        mEmptyTextview = (TextView) mView.findViewById(R.id.empty_view);
    }


    private class NotificationDataTask extends AsyncTask<Void, Void, List<NotificationModel>> {

        @Override
        protected List<NotificationModel> doInBackground(Void... voids) {
            return NotificationUtils.getAll();
        }

        @Override
        protected void onPostExecute(List<NotificationModel> notificationModels) {
            listView.setAdapter(new NotificationAdapter(getActivity(), notificationModels));

            // Show up the empty view when this user has empty notification.
            if (notificationModels.size() == 0) {
                mEmptyTextview.setVisibility(View.VISIBLE);
            }
        }
    }
}
