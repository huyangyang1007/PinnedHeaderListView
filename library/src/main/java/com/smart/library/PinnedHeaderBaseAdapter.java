package com.smart.library;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class PinnedHeaderBaseAdapter extends BaseAdapter
        implements PinnedHeaderListView.IPinnedHeaderAdapter {

    private static final String TAG = "PinnedHeaderBaseAdapter";

    private int mItemCount = -1;

    private SparseIntArray mHeaderPositionSparse = new SparseIntArray();

    private SparseBooleanArray mHeaderSparse = new SparseBooleanArray();

    public PinnedHeaderBaseAdapter() {
        super();
    }

    @Override
    public int getCount() {
        if (mItemCount > 0) {
            return mItemCount;
        }
        mItemCount = 0;
        int groupCount = getGroupSparse().size();
        for (int i = 0; i < groupCount; i++) {
            Log.d(TAG, "header : " + i + " position: " + mItemCount);
            mHeaderPositionSparse.put(i, mItemCount);
            mHeaderSparse.put(mItemCount, true);
            mItemCount += getGroupSparse().get(i) + 1;
        }
        return mItemCount;
    }

    @Override
    public boolean isHeader(int position) {
        return mHeaderSparse.get(position, false);
    }

    @Override
    public int getGroupIndex(int position) {
        for (int i = mHeaderPositionSparse.size() - 1; i >= 0; i--) {
            if (position >= mHeaderPositionSparse.get(i)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public int getItemPositionInGroup(int indexInGroup) {
        for (int i = mHeaderPositionSparse.size() - 1; i >= 0; i--) {
            if (indexInGroup >= mHeaderPositionSparse.get(i)) {
                return indexInGroup - mHeaderPositionSparse.get(i) - 1;
            }
        }
        return 0;
    }

    @Override
    public View getHeader(int groupIndex, View convertView, ViewGroup parent) {
        return getHeaderView(getGroupIndex(groupIndex), convertView, parent);
    }

    @Override
    public Object getItem(int position) {
        if (isHeader(position)) {
            return getHeaderItem(getGroupIndex(position));
        }
        return getItem(getGroupIndex(position), getItemPositionInGroup(position));
    }

    @Override
    public long getItemId(int listItemPosition) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (isHeader(position)) {
            return getHeaderView(getGroupIndex(position), convertView, parent);
        }
        return getItemView(getGroupIndex(position), getItemPositionInGroup(position), convertView, parent);
    }

    @Override
    public void notifyDataSetChanged() {
        mHeaderSparse.clear();
        mItemCount = -1;
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        mHeaderSparse.clear();
        mItemCount = -1;
        super.notifyDataSetInvalidated();
    }

    public abstract SparseIntArray getGroupSparse();

    public abstract Object getHeaderItem(int headerIndex);

    public abstract Object getItem(int groupIndex, int itemIndexInGroup);

    public abstract View getHeaderView(int headerIndex, View convertView, ViewGroup parent);

    public abstract View getItemView(int groupIndex, int itemIndexInGroup, View convertView, ViewGroup parent);

}
