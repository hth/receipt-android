package com.receiptofi.checkout.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.model.NotificationModel;
import com.receiptofi.checkout.model.ReceiptItemModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by PT on 4/5/15.
 */
public class NotificationAdapter extends ArrayAdapter<NotificationModel> {

    private static final String TAG = NotificationAdapter.class.getSimpleName();

    private Context context;
    private final LayoutInflater inflater;
    private List<NotificationModel> notificationList;

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
            if(convertView == null){
                convertView = inflater.inflate(R.layout.notification_list_item, parent, false);

                holder = new ViewHolder();
                holder.notificationText = (TextView) convertView.findViewById(R.id.notification_text);
                holder.notificationTime = (TextView) convertView.findViewById(R.id.notification_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            NotificationModel notification = getItem(position);
            DateFormat inputDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            DateFormat outputDF = new SimpleDateFormat("MMM dd',' yyyy HH:mm a");
            String formattedDate = outputDF.format(inputDF.parse(notification.getUpdated()));

            holder.notificationText.setText(notification.getMessage());
            holder.notificationTime.setText(formattedDate);

            return convertView;
        } catch (Exception e) {
            Log.d(TAG, "Exception " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private class ViewHolder {
        TextView notificationText;
        TextView notificationTime;
    }
}
