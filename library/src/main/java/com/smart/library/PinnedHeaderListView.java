package com.smart.library;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class PinnedHeaderListView extends ListView implements OnScrollListener {

    private static final String TAG = "PinnedHeaderListView";

    private OnScrollListener mOnScrollListener;
    private int mWidth = MeasureSpec.UNSPECIFIED;
    private float mHeaderOffset;
    private View mCurrentHeader;
    private int mCurrentHeaderPosition = 0;
    private IPinnedHeaderAdapter mAdapter;
    private final boolean isRTL = getLayoutDirection() == LAYOUT_DIRECTION_RTL;

    public PinnedHeaderListView(Context context) {
        this(context, null);
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnScrollListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getMode(widthMeasureSpec);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mCurrentHeader = null;
        if (!(adapter instanceof IPinnedHeaderAdapter)) {
            throw new IllegalArgumentException("Adapter is not implement IPinnedHeaderAdapter");
        }
        mAdapter = (IPinnedHeaderAdapter) adapter;
        super.setAdapter(adapter);
    }

    @Override
    public final void setOnScrollListener(OnScrollListener l) {
        super.setOnScrollListener(l);
    }

    public void setOnScrollListenerByPinned(OnScrollListener l) {
        mOnScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

        if (mAdapter == null || mAdapter.getItemCount() == 0 || (firstVisibleItem < getHeaderViewsCount())) {
            mCurrentHeader = null;
            mHeaderOffset = 0.0f;
            for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
                View header = getChildAt(i);
                if (header != null) {
                    header.setVisibility(VISIBLE);
                }
            }
            return;
        }

        firstVisibleItem -= getHeaderViewsCount();

        int headerPosition = mAdapter.getGroupIndex(firstVisibleItem);

        Log.d(TAG, "firstVisibleItem: " + firstVisibleItem + " headerPosition: " + headerPosition);

        if (mCurrentHeader == null || mCurrentHeaderPosition != headerPosition) {
            mCurrentHeader = mAdapter.getHeader(firstVisibleItem, view, this);
            ensurePinnedHeaderLayout(mCurrentHeader);
            mCurrentHeaderPosition = headerPosition;
        }

        mHeaderOffset = 0.0f;

        for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
            if (mAdapter.isHeader(i)) {
                View header = getChildAt(i - firstVisibleItem);
                float headerTop = header.getTop();
                float pinnedHeaderHeight = mCurrentHeader.getMeasuredHeight();
                if (pinnedHeaderHeight >= headerTop && headerTop >= 0) {
                    mHeaderOffset = headerTop - pinnedHeaderHeight;
                }
                break;
            }
        }
        Log.d(TAG, "mCurrentHeaderPosition: " + mCurrentHeaderPosition + " mHeaderOffset: " + mHeaderOffset);
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mAdapter == null || mCurrentHeader == null || getScrollY() < 0) {
            return;
        }
        int saveCount = canvas.save();
        canvas.translate(0, mHeaderOffset);
        canvas.clipRect(0, 0, getWidth(), mCurrentHeader.getMeasuredHeight());
        mCurrentHeader.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    private void ensurePinnedHeaderLayout(View header) {
        if (!header.isLayoutRequested()) {
            return;
        }

        int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), mWidth);
        int heightSpec;
        ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
        if (layoutParams != null && layoutParams.height > 0) {
            heightSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
        } else {
            heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        header.measure(widthSpec, heightSpec);
        if (isRTL) {
            header.layout(header.getMeasuredWidth(), 0, 0, header.getMeasuredHeight());
        } else {
            header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
        }

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        super.setOnItemClickListener(listener);
    }

    public static abstract class OnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PinnedHeaderBaseAdapter adapter;
            if (parent.getAdapter() instanceof HeaderViewListAdapter) {
                HeaderViewListAdapter wrapperAdapter = (HeaderViewListAdapter) parent.getAdapter();
                adapter = (PinnedHeaderBaseAdapter) wrapperAdapter.getWrappedAdapter();
            } else {
                adapter = (PinnedHeaderBaseAdapter) parent.getAdapter();
            }
            int groupIndex = adapter.getGroupIndex(position);
            int itemIndexInGroup = adapter.getItemPositionInGroup(position);

            if (itemIndexInGroup == -1) {
                onHeaderClick(parent, view, groupIndex, id);
            } else {
                onItemClick(parent, view, groupIndex, itemIndexInGroup, id);
            }
        }

        public abstract void onItemClick(AdapterView<?> adapterView, View view, int groupIndex, int itemIndexInGroup, long id);

        public abstract void onHeaderClick(AdapterView<?> adapterView, View view, int groupIndex, long id);
    }

    public interface IPinnedHeaderAdapter {

        int getItemCount();

        /**
         * judge whether the item is a header with item's position
         *
         * @param position
         * @return
         */
        boolean isHeader(int position);

        /**
         * get Header position with item's position
         *
         * @param position
         * @return
         */
        int getGroupIndex(int position);

        /**
         * get item's position in it's group
         *
         * @param position
         * @return
         */
        int getItemPositionInGroup(int position);

        /**
         * get header view with item's position
         *
         * @param groupIndex
         * @param convertView
         * @param parent
         * @return
         */
        View getHeader(int groupIndex, View convertView, ViewGroup parent);
    }

}
