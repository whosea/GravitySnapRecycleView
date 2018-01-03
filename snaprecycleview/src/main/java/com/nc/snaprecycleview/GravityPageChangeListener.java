package com.nc.snaprecycleview;

import android.support.v7.widget.RecyclerView;

/**
 * @author ht
 * @date Created on 2017/12/26
 * @description
 * Recycleview状态和页数变化的回调
 */
public interface GravityPageChangeListener {

    /**
     * 回调当前位置，当前页数（从1开始），总页数
     */
    void onPageSelected(int position, int currentPage, int totalPage);

    /**
     * Called when the scroll state changes. Useful for discovering when the user
     * begins dragging, when the pager is automatically settling to the current page,
     * or when it is fully stopped/idle.
     *
     * @param state The new scroll state.
     * @see RecyclerView#SCROLL_STATE_IDLE
     * @see RecyclerView#SCROLL_STATE_DRAGGING
     * @see RecyclerView#SCROLL_STATE_SETTLING
     */
    void onPageScrollStateChanged(int state);
}
