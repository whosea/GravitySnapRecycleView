package com.nc.snaprecycleview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;

/**
 * @author ht
 * @date Created on 2017/12/25
 * @description
 * 监听用户滑动到某一页，然后触发回调
 */
public class PageIndicatorHelper{

    private static final int DEFAULT_COLUMN = 1;

    private GravityPageChangeListener listener;
    private int pageColumn = DEFAULT_COLUMN;

    protected RecyclerView recyclerView;
    protected int currentPage;

    public PageIndicatorHelper() {

    }

    private OnScrollListener scrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            onPageScrollStateChanged(newState);

            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                int page = 0;
                int position = 0;
                int secondPosition = 0;

                /**
                 * 如果只用findFirstCompletelyVisibleItemPosition会有个小问题，用户拖到一半松开时候
                 * 会有一瞬间的停顿，而系统会判定这个是空闲状态（SCROLL_STATE_IDLE），那么就会返回NO_POSITION(-1)的位置
                 * 因此需要结合findFirstVisibleItemPosition一起判断
                 */
                if (layoutManager instanceof GridLayoutManager) {
                    GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                    position = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
                    secondPosition = gridLayoutManager.findFirstVisibleItemPosition();
                    if(position == RecyclerView.NO_POSITION && secondPosition >= 0){
                        position = secondPosition;
                    }
                    page = getCurrentPage(position);
                } else if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    secondPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    if(position == RecyclerView.NO_POSITION && secondPosition >= 0){
                        position = secondPosition;
                    }
                    page = position + 1;
                }

                onPageSelected(position,page);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    /**
	 * 设置RecyclerView进行监听
     */
    public void setRecyclerView(RecyclerView view) {
        if (recyclerView == view) {
            return;
        }
        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(scrollListener);
        }
        if (view.getAdapter() == null) {
            throw new IllegalStateException("RecyclerView does not have adapter instance.");
        }
        recyclerView = view;
        recyclerView.addOnScrollListener(scrollListener);
    }

    public void setRecyclerView(RecyclerView view, int initialPosition) {
        setRecyclerView(view);
        setCurrentPage(initialPosition);
    }

    public void setOnPageChangeListener(GravityPageChangeListener listener) {
        this.listener = listener;
    }

    public void setPageColumn(int column) {
        if (column <= 0)
            throw new IllegalArgumentException("column must be not null");
        this.pageColumn = column;
    }


    private void onPageSelected(int position,int page) {
        if (currentPage == page)
            return;

        currentPage = page;
        if (listener != null) {
            listener.onPageSelected(position,page,pageCount());
        }
    }

    private void onPageScrollStateChanged(int state) {
        if (listener != null) {
            listener.onPageScrollStateChanged(state);
        }
    }

	/**
	 * 设置当前页数
     * 页数*页面总数=最终跳转的位置（position）
     */
    private void setCurrentPage(int page) {
        if (recyclerView == null) {
            throw new IllegalStateException("RecyclerView has not been bound.");
        }
        int currentPage = eachPageItemCount() * page;

        recyclerView.smoothScrollToPosition(currentPage);
        this.currentPage = page;
    }


    /**
     * 返回当前页的数量
     */
    public int eachPageItemCount() {
        if (recyclerView == null) {
            return 0;
        }

        int row = 1;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (layoutManager != null) {
            if (layoutManager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                row = gridLayoutManager.getSpanCount();
            }
        }

        return row * pageColumn;
    }

    /**
     * 返回总页数
     */
    public int pageCount() {
        if (recyclerView == null || recyclerView.getAdapter() == null)
            return 0;

        int itemCount = recyclerView.getAdapter().getItemCount();

        int eachPageCount = eachPageItemCount();
        if (eachPageCount <= 0) return 0;

        return itemCount % eachPageCount == 0 ?
                itemCount / eachPageCount : itemCount / eachPageCount + 1;
    }

	/**
	 * 根据当前位置返回对应的页数
	 */
    public int getCurrentPage(int position) {
        if (recyclerView == null || recyclerView.getAdapter() == null)
            return 0;

        int itemCount = recyclerView.getAdapter().getItemCount();

        int eachPageCount = eachPageItemCount();

        if (eachPageCount <= 0) return 0;

        return (position / eachPageCount) + 1;
    }
}
