package com.jyo.android.eternalfriend.profile_summarize.adapter;

/**
 * Interface to listen for a move or dismissal event from a
 * {@link android.support.v7.widget.helper.ItemTouchHelper.Callback}.
 *
 * @author Paul Burke (ipaulpro)
 */
public interface ItemTouchHelperAdapter {

    void onItemDismiss(int position);
}