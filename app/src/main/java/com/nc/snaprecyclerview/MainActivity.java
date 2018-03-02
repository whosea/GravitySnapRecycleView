package com.nc.snaprecyclerview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.nc.snaprecyclerview.view.indicator.CircleRecyclerPageIndicator;
import com.nc.snaprecycleview.GravityPageChangeListener;
import com.nc.snaprecycleview.GravitySnapHelper;
import com.nc.snaprecycleview.PageIndicatorHelper;
import com.nc.snaprecycleview.transform.InvertRowColumnDataTransform;
import com.nc.snaprecycleview.utils.GridPagerUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		configLeftRecyclerView(2, 3);
		configRightRecyclerView(2, 3);
		configCenterRecyclerView(2, 4);

	}

	private void configLeftRecyclerView(int row, int column) {
		RecyclerView rvLeft = (RecyclerView) findViewById(R.id.rvLeft);

		//setLayoutManager
		GridLayoutManager gridLayoutManager = new GridLayoutManager(this, row,
				LinearLayoutManager.HORIZONTAL, false);
		rvLeft.setLayoutManager(gridLayoutManager);

		//getDataSource
		List<ItemBean> dataList = addItemDatas();
		dataList = GridPagerUtils.transformAndFillEmptyData(
				new InvertRowColumnDataTransform<ItemBean>(column,row), dataList);

		//setAdapter
		ItemAdapter adapter = new ItemAdapter(this);
		adapter.updateDatas(dataList);
		adapter.setItemWidth(getScreenWidth(this)/column/5*4);
		rvLeft.setAdapter(adapter);

		//attachToRecyclerView
		GravitySnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
		snapHelper.setColumn(column);
		snapHelper.attachToRecyclerView(rvLeft);
		snapHelper.setCanPageScroll(true);

	}

	private void configRightRecyclerView(int row, int column) {

		RecyclerView rvRight = (RecyclerView) findViewById(R.id.rvRight);

		//setLayoutManager
		GridLayoutManager gridLayoutManager = new GridLayoutManager(this, row,
				LinearLayoutManager.HORIZONTAL, false);
		rvRight.setLayoutManager(gridLayoutManager);

		//getDataSource
		List<ItemBean> dataList = addItemDatas();
		dataList = GridPagerUtils.transformAndFillEmptyData(
				new InvertRowColumnDataTransform<ItemBean>(column,row), dataList);

		//setAdapter
		ItemAdapter adapter = new ItemAdapter(this);
		adapter.updateDatas(dataList);
		adapter.setItemWidth(getScreenWidth(this)/column/5*4);
		rvRight.setAdapter(adapter);

		//attachToRecyclerView
		GravitySnapHelper snapHelper = new GravitySnapHelper(Gravity.END);
		snapHelper.setColumn(column);
		snapHelper.attachToRecyclerView(rvRight);

	}


	private void configCenterRecyclerView(int row, int column) {
		RecyclerView rvCenter = (RecyclerView) findViewById(R.id.rvCenter);
		rvCenter.setHasFixedSize(true);


		//setLayoutManager
		GridLayoutManager gridLayoutManager = new GridLayoutManager(this, row,
				LinearLayoutManager.HORIZONTAL, false);
		rvCenter.setLayoutManager(gridLayoutManager);

		//getDataSource
		List<ItemBean> dataList = addItemDatas();
		dataList = GridPagerUtils.transformAndFillEmptyData(
				new InvertRowColumnDataTransform<ItemBean>(column,row), dataList);

		//setAdapter
		ItemAdapter adapter = new ItemAdapter(this);
		adapter.updateDatas(dataList);
		adapter.setItemWidth(getScreenWidth(this)/column);
		rvCenter.setAdapter(adapter);

		//attachToRecyclerView
		GravitySnapHelper snapHelper = new GravitySnapHelper(Gravity.CENTER);
		snapHelper.setColumn(column);
		snapHelper.attachToRecyclerView(rvCenter);
		snapHelper.setCanPageScroll(true);

		CircleRecyclerPageIndicator crpiCenter = (CircleRecyclerPageIndicator) findViewById(R.id.crpiCenter);
		crpiCenter.setRecyclerView(rvCenter);
		crpiCenter.setPageColumn(column);


		//加入Indicator监听
		PageIndicatorHelper pageIndicatorHelper = new PageIndicatorHelper();
        pageIndicatorHelper.setPageColumn(column);
        pageIndicatorHelper.setRecyclerView(rvCenter);
        pageIndicatorHelper.setOnPageChangeListener(new GravityPageChangeListener() {
            @Override
            public void onPageSelected(int position,int currentPage,int totalPage) {
				Log.e("MainActivity",currentPage+ "/"+totalPage);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

	}

	private static List<ItemBean> addItemDatas() {
		List<ItemBean> dataList = new ArrayList<>();
		for (int i = 0; i < 22; i++) {
			ItemBean data = new ItemBean();
			data.title = "标题" + (i + 1);
			dataList.add(data);
		}
		return dataList;
	}

	/**
	 * 获得屏幕高度
	 */
	public static int getScreenWidth(Context context)
	{
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}
}
