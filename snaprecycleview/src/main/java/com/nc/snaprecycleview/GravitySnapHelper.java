package com.nc.snaprecycleview;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author ht
 * @date Created on 2017/10/12
 * @description
 */

public class GravitySnapHelper extends LinearSnapHelper {

	@NonNull private final GravityDelegate delegate;

	public GravitySnapHelper(int gravity) {
		this(gravity, false, null);
	}

	public GravitySnapHelper(int gravity, boolean enableSnapLastItem) {
		this(gravity, enableSnapLastItem, null);
	}

	public GravitySnapHelper(int gravity, boolean enableSnapLastItem,
							 @Nullable SnapListener snapListener) {
		delegate = new GravityDelegate(gravity, enableSnapLastItem, snapListener);
	}

	public void setColumn(int column) {
		delegate.setColumn(column);
	}

	@Override
	public void attachToRecyclerView(@Nullable RecyclerView recyclerView)
			throws IllegalStateException {
		delegate.attachToRecyclerView(recyclerView);
		super.attachToRecyclerView(recyclerView);
	}

	@Override
	public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
											  @NonNull View targetView) {
		return delegate.calculateDistanceToFinalSnap(layoutManager, targetView);
	}

	@Override
	public View findSnapView(RecyclerView.LayoutManager layoutManager) {
		return delegate.findSnapView(layoutManager);
	}

	/**
	 * Enable snapping of the last item that's snappable.
	 * The default value is false, because you can't see the last item completely
	 * if this is enabled.
	 *
	 * @param snap true if you want to enable snapping of the last snappable item
	 */
	public void enableLastItemSnap(boolean snap) {
		delegate.enableLastItemSnap(snap);
	}

	public interface SnapListener {
		void onSnap(int position);
	}

}
