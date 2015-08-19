package com.receiptofi.receiptapp.views;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.receiptofi.receiptapp.R;

/**
 * Created by PT on 5/15/15.
 */
public class ToastBox extends Toast {

    public ToastBox(Context context) {
        super(context);
    }

    public static Toast makeText(Context context, CharSequence text, int duration) {
        Toast toast = Toast.makeText(context, text, duration);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.toast_background);
        TextView toastTV = (TextView)view.findViewById(android.R.id.message);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(20, 0, 20, 0);
        toastTV.setLayoutParams(llp);
        return toast;
    }
}
