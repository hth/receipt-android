package com.receiptofi.checkout.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.model.Tag;

import java.util.LinkedList;

/**
 * Created by kevin on 5/26/15.
 */
public class TagListAdapter extends BaseAdapter {
    private LinkedList<Tag> mList;
    private Context mContext;
    public TagListAdapter(Context context, LinkedList<Tag> mlistHashMaps) {

        this.mContext = context;
        this.mList = mlistHashMaps;
    }

    public void updateList( LinkedList<Tag> mlistHashMaps) {
        this.mList = mlistHashMaps;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.i("Kevin", "in getView: i:" + i + ". And the tag is:" + mList.get(i).getTag());
        if (view == null) {
            view = View.inflate(mContext,
                    R.layout.list_item, null);
            new ViewHolder(view);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.tv_content.setText(mList.get(i).getTag());
        holder.iv_label.setBackgroundColor(mList.get(i).getColor());
        return view;
    }

    class ViewHolder {
        ImageView iv_label;
        TextView tv_content;

        public ViewHolder(View view) {
            iv_label = (ImageView) view.findViewById(R.id.tag_color);
            tv_content = (TextView) view.findViewById(R.id.tv_tag);
            view.setTag(this);
        }
    }

}

