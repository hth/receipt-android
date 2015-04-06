package com.receiptofi.checkout;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.receiptofi.checkout.adapters.NotificationAdapter;
import com.receiptofi.checkout.model.NotificationModel;
import com.receiptofi.checkout.utils.db.NotificationUtils;

import java.util.List;

/**
 * Created by PT on 4/5/15.
 */
public class NotificationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_activity);

        // start query
        new NotificationDataTask().execute();
    }

    private void setListData(List<NotificationModel> notificationModels) {
        ListView listView = (ListView) findViewById(R.id.notification_list);
        listView.setAdapter(new NotificationAdapter(this, notificationModels));
    }

    private class NotificationDataTask extends AsyncTask<Void, Void, List<NotificationModel>> {

        @Override
        protected List<NotificationModel> doInBackground(Void... voids) {
            return NotificationUtils.getAll();
        }

        @Override
        protected void onPostExecute(List<NotificationModel> notificationModels) {
            setListData(notificationModels);
        }
    }

}
