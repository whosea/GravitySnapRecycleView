package com.nc.snaprecycleview;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import java.util.Locale;

/**
 * @author ht
 * @date Created on 2017/10/12
 * @description
 * 注意只处理linear和grid布局，实现左右滑动
 * 支持start end top bottom 和 center 五种行为
 * start和end是靠左和靠右滑动，同理top和bottom是靠上和靠下上下滑动
 * center 目前只支持水平翻页滑动<br/>
 * 用法：<br/>
 * GravitySnapHelper snapHelper = new GravitySnapHelper(Gravity.CENTER); <br/>
 * snapHelper.setColumn(3);//如果一页里面有超过1列的都需要设置<br/>
 * snapHelper.attachToRecyclerView(recyclerview);<br/>
 */

public class GravityDelegate {
	private OrientationHelper verticalHelper;
	private OrientationHelper horizontalHelper;
	private int gravity;
	private boolean isRtlHorizontal;
	private boolean snapLastItem;
	private GravitySnapHelper.SnapListener listener;
	private boolean snapping;

	//列数
	private int column = 1;

	private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);
			if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
				snapping = false;
			}
			if (newState == RecyclerView.SCROLL_STATE_IDLE && snapping && listener != null) {
				int position = getSnappedPosition(recyclerView);
				if (position != RecyclerView.NO_POSITION) {
					listener.onSnap(position);
				}
				snapping = false;
			}
		}
	};

	public GravityDelegate(int gravity, boolean enableSnapLast,
						   GravitySnapHelper.SnapListener listener) {
		if (gravity != Gravity.START && gravity != Gravity.END
				&& gravity != Gravity.BOTTOM && gravity != Gravity.TOP
				&& gravity != Gravity.CENTER) {
			throw new IllegalArgumentException("Invalid gravity value. Use START " +
					"| END | BOTTOM | TOP | CENTER constants");
		}
		this.snapLastItem = enableSnapLast;
		this.gravity = gravity;
		this.listener = listener;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
		if (recyclerView != null) {
			recyclerView.setOnFlingListener(null);
			if (gravity == Gravity.START || gravity == Gravity.END
					|| gravity == Gravity.CENTER) {
				isRtlHorizontal = isRtl();
			}
			if (listener != null) {
				recyclerView.addOnScrollListener(mScrollListener);
			}
		}
	}

	private boolean isRtl() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			return false;
		}
		return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault())
				== View.LAYOUT_DIRECTION_RTL;
	}


	public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
											  @NonNull View targetView) {
		int[] out = new int[2];

		if (layoutManager.canScrollHorizontally()) {
			if (gravity == Gravity.START) {
				out[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager), false);
			} else if (gravity == Gravity.CENTER) {
				out[0] = distanceToCenter(layoutManager,targetView, getHorizontalHelper(layoutManager));
			}  else { // END
				out[0] = distanceToEnd(targetView, getHorizontalHelper(layoutManager), false);
			}
		} else {
			out[0] = 0;
		}

		if (layoutManager.canScrollVertically()) {
			if (gravity == Gravity.TOP) {
				out[1] = distanceToStart(targetView, getVerticalHelper(layoutManager), false);
			}else if (gravity == Gravity.CENTER) {
				out[1] = distanceToCenter(layoutManager,targetView, getVerticalHelper(layoutManager));
			}  else { // BOTTOM
				out[1] = distanceToEnd(targetView, getVerticalHelper(layoutManager), false);
			}
		} else {
			out[1] = 0;
		}

		return out;
	}

	public View findSnapView(RecyclerView.LayoutManager layoutManager) {
		View snapView = null;
		if (layoutManager instanceof LinearLayoutManager) {
			switch (gravity) {
				case Gravity.START:
					snapView = findStartView(layoutManager, getHorizontalHelper(layoutManager));
					break;
				case Gravity.END:
					snapView = findEndView(layoutManager, getHorizontalHelper(layoutManager));
					break;
				case Gravity.TOP:
					snapView = findStartView(layoutManager, getVerticalHelper(layoutManager));
					break;
				case Gravity.BOTTOM:
					snapView = findEndView(layoutManager, getVerticalHelper(layoutManager));
					break;
				case Gravity.CENTER:
					if (layoutManager.canScrollVertically()){
						snapView = findCenterView(layoutManager, getVerticalHelper(layoutManager));
					}else{
						snapView = findCenterView(layoutManager, getHorizontalHelper(layoutManager));
					}
					break;
			}
		}
		snapping = snapView != null;
		return snapView;
	}

	public void enableLastItemSnap(boolean snap) {
		snapLastItem = snap;
	}

	private int distanceToCenter(RecyclerView.LayoutManager layoutManager,View targetView, OrientationHelper helper) {

		int columnWidth = helper.getTotalSpace() / column;
		int position = layoutManager.getPosition(targetView);
		//当前第一个可见view所在的页
		int pageIndex = 0;
		int row = 1;
		//当前页第一个view的位置
		int currentPagePosition = 0;

		if (layoutManager instanceof GridLayoutManager) {
			row = ((GridLayoutManager) layoutManager).getSpanCount();
		}
		//该位置在哪一页
		pageIndex = pageIndex(position,row);
		//当前页开始的位置
		currentPagePosition = pageIndex * countOfPage(row);

		//算出当前位置距离当前页开始位置有多远
		int distance = ((position - currentPagePosition) / row) * columnWidth;

		int distanceToCenter = 0;
		int childStart = 0;
		if(isRtlHorizontal){
			childStart = helper.getDecoratedEnd(targetView);
			distanceToCenter = childStart - (helper.getTotalSpace() - distance);
		}else{
			childStart = helper.getDecoratedStart(targetView);
			distanceToCenter = childStart - distance;
		}

		return distanceToCenter;
	}

	private int distanceToStart(View targetView, OrientationHelper helper, boolean fromEnd) {
		if (isRtlHorizontal && !fromEnd) {
			return distanceToEnd(targetView, helper, true);
		}
		//targetView 就是最终要滑到开始的view，因为这里会根据最终的view来计算最终要水平滑动多少
		//Recycleview才能移到targetView的最左方
		return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
	}

	private int distanceToEnd(View targetView, OrientationHelper helper, boolean fromStart) {
		if (isRtlHorizontal && !fromStart) {
			return distanceToStart(targetView, helper, true);
		}
		return helper.getDecoratedEnd(targetView) - helper.getEndAfterPadding();
	}

	/**
	 * 返回距离Recycleview中间位置最近的view
	 * @param layoutManager
	 * @return
	 */
	private View findCenterView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {

		int childCount = layoutManager.getChildCount();
		if (childCount == 0) {
			return null;
		}

		View closestChild = null;
		final int center;
		if (layoutManager.getClipToPadding()) {
			if(isRtlHorizontal){
				center = (helper.getTotalSpace()-helper.getEndAfterPadding()) + helper.getTotalSpace() / 2;
			}else{
				center = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
			}
		} else {
			center = helper.getEnd() / 2;
		}

		int absClosest = Integer.MAX_VALUE;

		for (int i = 0; i < childCount; i++) {
			View child = layoutManager.getChildAt(i);

			int childCenter = 0;
			if(isRtlHorizontal){
				childCenter =  (helper.getTotalSpace() - helper.getDecoratedEnd(child))
						+ (helper.getDecoratedMeasurement(child) / 2);
			}else{
				childCenter = helper.getDecoratedStart(child)
						+ (helper.getDecoratedMeasurement(child) / 2);
			}

			int absDistance = Math.abs(childCenter - center);

			/** if child center is closer than previous closest, set it as closest  **/
			if (absDistance < absClosest) {
				absClosest = absDistance;
				closestChild = child;
			}

		}
		return closestChild;
	}

	/**
	 * Returns the first view that we should snap to.
	 *
	 * @param layoutManager the recyclerview's layout manager
	 * @param helper        mOrientation helper to calculate view sizes
	 * @return the first view in the LayoutManager to snap to
	 */
	private View findStartView(RecyclerView.LayoutManager layoutManager,
							   OrientationHelper helper) {

		if (layoutManager instanceof LinearLayoutManager) {
			LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
			boolean reverseLayout = linearLayoutManager.getReverseLayout();
			int firstChild = reverseLayout ? linearLayoutManager.findLastVisibleItemPosition()
					: linearLayoutManager.findFirstVisibleItemPosition();
			int offset = 1;

			if (layoutManager instanceof GridLayoutManager) {
				offset += ((GridLayoutManager) layoutManager).getSpanCount() - 1;
			}

			if (firstChild == RecyclerView.NO_POSITION) {
				return null;
			}

			View child = layoutManager.findViewByPosition(firstChild);

			float visibleWidth;

			// We should return the child if it's visible width
			// is greater than 0.5 of it's total width.
			// In a RTL configuration, we need to check the start point and in LTR the end point
			//大于0.5则滑到下一个view，小于则滑回去
			if (isRtlHorizontal) {
				visibleWidth = (float) (helper.getTotalSpace() - helper.getDecoratedStart(child))
						/ helper.getDecoratedMeasurement(child);
			} else {
				visibleWidth = (float) helper.getDecoratedEnd(child)
						/ helper.getDecoratedMeasurement(child);
			}

			// If we're at the end of the list, we shouldn't snap
			// to avoid having the last item not completely visible.
			boolean endOfList;
			if (!reverseLayout) {
				endOfList = ((LinearLayoutManager) layoutManager)
						.findLastCompletelyVisibleItemPosition()
						== layoutManager.getItemCount() - 1;
			} else {
				endOfList = ((LinearLayoutManager) layoutManager)
						.findFirstCompletelyVisibleItemPosition()
						== 0;
			}

			if (visibleWidth > 0.5f && !endOfList) {
				return child;
			} else if (snapLastItem && endOfList) {
				return child;
			} else if (endOfList) {
				return null;
			} else {
				// If the child wasn't returned, we need to return
				// the next view close to the start.
				return reverseLayout ? layoutManager.findViewByPosition(firstChild - offset)
						: layoutManager.findViewByPosition(firstChild + offset);
			}
		}

		return null;
	}

	@Nullable
	private View findEndView(RecyclerView.LayoutManager layoutManager,
							 @NonNull OrientationHelper helper) {

		if (layoutManager instanceof LinearLayoutManager) {
			LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
			boolean reverseLayout = linearLayoutManager.getReverseLayout();
			int lastChild = reverseLayout ? linearLayoutManager.findFirstVisibleItemPosition()
					: linearLayoutManager.findLastVisibleItemPosition();
			int offset = 1;

			if (layoutManager instanceof GridLayoutManager) {
				offset += ((GridLayoutManager) layoutManager).getSpanCount() - 1;
			}

			if (lastChild == RecyclerView.NO_POSITION) {
				return null;
			}

			View child = layoutManager.findViewByPosition(lastChild);

			float visibleWidth;

			if (isRtlHorizontal) {
				visibleWidth = (float) helper.getDecoratedEnd(child)
						/ helper.getDecoratedMeasurement(child);
			} else {
				visibleWidth = (float) (helper.getTotalSpace() - helper.getDecoratedStart(child))
						/ helper.getDecoratedMeasurement(child);
			}

			// If we're at the start of the list, we shouldn't snap
			// to avoid having the first item not completely visible.
			boolean startOfList;
			if (!reverseLayout) {
				startOfList = ((LinearLayoutManager) layoutManager)
						.findFirstCompletelyVisibleItemPosition() == 0;
			} else {
				startOfList = ((LinearLayoutManager) layoutManager)
						.findLastCompletelyVisibleItemPosition()
						== layoutManager.getItemCount() - 1;
			}

			if (visibleWidth > 0.5f && !startOfList) {
				return child;
			} else if (snapLastItem && startOfList) {
				return child;
			} else if (startOfList) {
				return null;
			} else {
				// If the child wasn't returned, we need to return the previous view
				return reverseLayout ? layoutManager.findViewByPosition(lastChild + offset)
						: layoutManager.findViewByPosition(lastChild - offset);
			}
		}
		return null;
	}

	/**
	 * 返回当前滑动哪个位置
	 * @param recyclerView
	 * @return
	 */
	int getSnappedPosition(RecyclerView recyclerView) {
		RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

		if (layoutManager instanceof LinearLayoutManager) {
			if (gravity == Gravity.START || gravity == Gravity.TOP || (gravity == Gravity.CENTER && !isRtlHorizontal)) {
				return ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
			} else if (gravity == Gravity.END || gravity == Gravity.BOTTOM ) {
				return ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
			}
		}

		return RecyclerView.NO_POSITION;
	}

	private OrientationHelper getVerticalHelper(RecyclerView.LayoutManager layoutManager) {
		if (verticalHelper == null) {
			verticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
		}
		return verticalHelper;
	}

	private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager) {
		if (horizontalHelper == null) {
			horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
		}
		return horizontalHelper;
	}

	private int pageIndex(int position,int row) {
		return position / countOfPage(row);
	}

	private int countOfPage(int row) {
		return row * column;
	}
}
