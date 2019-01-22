package com.sea.snaprecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * @author ht
 * @date Created on 2018/1/2
 * @description
 */

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private Context context;
	private List<ItemBean> datas;
	private int itemWidth = 0;

	public ItemAdapter(Context context) {
		this.context = context;
	}

	public void updateDatas(List<ItemBean> datas){
		this.datas = datas;
	}

	public void setItemWidth(int itemWidth) {
		this.itemWidth = itemWidth;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View convertView = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
		RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		convertView.setLayoutParams(lp);
		return new ViewHolder(convertView);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder orgiHolder, int position) {
		if (orgiHolder instanceof ViewHolder) {
			ViewHolder holder = (ViewHolder) orgiHolder;
			holder.bindData(datas.get(position));
		}
	}

	@Override
	public int getItemCount() {
		return datas == null ? 0 : datas.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		ImageView ivIcon;
		TextView tvTitle;

		public ViewHolder(View itemView) {
			super(itemView);

			ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
			tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);

			itemView.getLayoutParams().width = itemWidth;
			ivIcon.getLayoutParams().width = itemWidth;
			ivIcon.getLayoutParams().height = itemWidth;
		}

		public void bindData(ItemBean data) {
			if (data != null) {
				itemView.setVisibility(View.VISIBLE);
				tvTitle.setText(data.title);
			} else {
				itemView.setVisibility(View.GONE);
			}
		}
	}

}

