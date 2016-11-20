package com.smart.pinnedheaderlistview;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smart.library.PinnedHeaderBaseAdapter;

import java.util.ArrayList;

public class PinnedHeaderAdapter extends PinnedHeaderBaseAdapter {

    private static final String TAG = "PinnedHeaderAdapter";

    private SparseIntArray mGroupSparse = null;

    private Context mContext;
    private ArrayList<ArrayList<String>> mList = new ArrayList<>();

    public PinnedHeaderAdapter(Context context, ArrayList<ArrayList<String>> list) {
        mList = list;
        mContext = context;
    }

    @Override
    public SparseIntArray getGroupSparse() {
        if (mGroupSparse != null) {
            return mGroupSparse;
        }
        mGroupSparse = new SparseIntArray();
        if (mList == null) {
            return mGroupSparse;
        }

        for (int i = 0; i < mList.size(); i++) {
            mGroupSparse.put(i, mList.get(i).size());
        }
        return mGroupSparse;
    }

    @Override
    public Object getHeaderItem(int headerIndex) {
        return "Group: " + headerIndex;
    }

    @Override
    public Object getItem(int groupIndex, int itemIndexInGroup) {
        return mList.get(groupIndex).get(itemIndexInGroup);
    }

    @Override
    public View getHeaderView(int headerIndex, View convertView, ViewGroup parent) {
        final HeaderViewHolder viewHolder;
        if (convertView == null || convertView.getTag(R.layout.header_item) == null) {
            viewHolder = new HeaderViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.header_item, parent, false);
            viewHolder.mHeader = (TextView) convertView.findViewById(R.id.header);
            convertView.setTag(R.layout.header_item);
        } else {
            viewHolder = (HeaderViewHolder) convertView.getTag(R.layout.header_item);
        }
        viewHolder.mHeader.setText((String) getHeaderItem(headerIndex));
        return convertView;
    }

    @Override
    public View getItemView(int groupIndex, int itemIndexInGroup, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null || convertView.getTag(R.layout.list_item) == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
            viewHolder.mItem = (TextView) convertView.findViewById(R.id.item);
            convertView.setTag(R.layout.list_item);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.layout.list_item);
        }
        viewHolder.mItem.setText((String) getItem(groupIndex, itemIndexInGroup));
        return convertView;
    }

    private static final class HeaderViewHolder {
        TextView mHeader;
    }

    private static final class ViewHolder {
        TextView mItem;
    }
}
