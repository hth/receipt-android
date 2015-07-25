package com.receiptofi.receipts.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.receiptofi.receipts.R;
import com.receiptofi.receipts.model.NotificationModel;
import com.receiptofi.receipts.utils.AppUtils;

import org.joda.time.DateTime;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;

/**
 * Created by PT on 4/5/15.
 */
public class NotificationAdapter extends ArrayAdapter<NotificationModel> {

    private static final String TAG = NotificationAdapter.class.getSimpleName();
    private final LayoutInflater inflater;
    private Context context;
    private List<NotificationModel> notificationList;
    private static final PrettyTime prettyTime = new PrettyTime();

    public NotificationAdapter(Context context, List<NotificationModel> notificationList) {
        super(context, R.layout.rd_item_list, notificationList);
        this.context = context;
        this.notificationList = notificationList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public NotificationModel getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.notification_list_item, parent, false);

                holder = new ViewHolder();
                holder.notificationText = (TextView) convertView.findViewById(R.id.notification_text);
                holder.notificationTime = (TextView) convertView.findViewById(R.id.notification_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            NotificationModel notification = getItem(position);
            DateTime dateTime = AppUtils.getDateTime(notification.getUpdated());
            holder.notificationText.setText(notification.getMessage());
            holder.notificationTime.setText(prettyTime.format(dateTime.toDate()));

            return convertView;
        } catch (Exception e) {
            Log.d(TAG, "reason= " + e.getLocalizedMessage(), e);
        }
        return null;
    }

    private class ViewHolder {
        TextView notificationText;
        TextView notificationTime;
    }
}
