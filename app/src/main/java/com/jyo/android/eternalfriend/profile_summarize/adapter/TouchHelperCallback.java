package com.jyo.android.eternalfriend.profile_summarize.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * From: https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf#.roz2mstco
 * Author: Paul Burke
 * Date: Jun 23, 2015
 * License: Apache License http://www.apache.org/licenses/LICENSE-2.0
 */
public class TouchHelperCallback extends ItemTouchHelper.Callback {


    private final ProfileAdapter mAdapter;

    public TouchHelperCallback(ProfileAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        if (mAdapter.deletePosition != -1){
            return false;
        }
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}
