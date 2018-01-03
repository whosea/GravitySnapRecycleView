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
			int value = i % 5;
//			if (value == 0) {
//				data.image = "http://h.hiphotos.baidu.com/zhidao/pic/item/060828381f30e9240ff2cd434c086e061d95f76a.jpg";
//			} else if (value == 1) {
//				data.image = "http://c.hiphotos.baidu.com/zhidao/pic/item/d788d43f8794a4c22fe6ab9408f41bd5ac6e3943.jpg";
//			} else if (value == 2) {
//				data.image = "http://b.hiphotos.baidu.com/zhidao/pic/item/1f178a82b9014a90e7eb9d17ac773912b21bee47.jpg";
//			} else if (value == 3) {
//				data.image = "http://e.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=75aaa91fa444ad342eea8f83e59220c2/0bd162d9f2d3572cf556972e8f13632763d0c388.jpg";
//			} else if (value == 4) {
//				data.image = "http://img1.imgtn.bdimg.com/it/u=1443817543,4124882906&fm=214&gp=0.jpg";
//			} else {
//				data.image = "http://imga.mumayi.com/android/img_google/2013/09/26/com.hd.live.wallpaper.beauty.anime/comhdlivewallpaperbeautyanime_litpic_2.jpg";
//			}
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
