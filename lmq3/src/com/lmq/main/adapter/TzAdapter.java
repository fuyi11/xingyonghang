package com.lmq.main.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.item.tzItem;
import com.lmq.view.RoundProgressBar;

public class TzAdapter extends BaseAdapter {
	
	private Context context;
	private LayoutInflater mInflater;
	/**标的数据源*/
	private ArrayList<tzItem> dataSource = new ArrayList<tzItem>();
	
	private boolean spicalViewFlag;

	public TzAdapter(Context context) {
		
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}
	
	
	public void setDataSource(ArrayList<tzItem> dataSource,boolean add){
		
		if(add){
			
			//将数据 追加到 当前List后面
			this.dataSource.addAll(dataSource);
			
		}else{
			
			//清空数据
			this.dataSource.clear();
			this.dataSource.addAll(dataSource);
			
			
		}
		
		
		
	}
	
	public void IsSpicalView(boolean isSpical){
		this.spicalViewFlag = isSpical;
	}
	
	
	
	
	/**
	 * 散标 企业直投 定投宝 Item
	 * @return
	 */
	private View customNormalItem(View convertView,tzItem item){
		NormalViewHolder viewHolder ;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_item_tz,
					null);
			viewHolder = new NormalViewHolder();
//			viewHolder.normal_kind_flag = (ImageView) convertView
//					.findViewById(R.id.normal_kind_flag);
			viewHolder.tz_title = (TextView) convertView
					.findViewById(R.id.tz_info);
			viewHolder.tz_money = (TextView) convertView
					.findViewById(R.id.tz_num1);
			viewHolder.tz_lv = (TextView) convertView
					.findViewById(R.id.tz_num2);
			viewHolder.tz_time = (TextView) convertView
					.findViewById(R.id.tz_num4);
			viewHolder.tv_iv = (ImageView) convertView
					.findViewById(R.id.tv_iv);

			viewHolder.tz_pro = (RoundProgressBar) convertView
					.findViewById(R.id.progress);
//			viewHolder.post_time = (TextView) convertView
//					.findViewById(R.id.tz_date);

//			viewHolder.normal_kind_flag.setVisibility(View.VISIBLE);
		
		}else{
			
			viewHolder = (NormalViewHolder) convertView.getTag();
			
			
		}
		initNormalViewItem(convertView, viewHolder, item);
		convertView.setTag(viewHolder);
		return convertView;
	
	}
			
			
	
	private void initNormalViewItem(View convertView,NormalViewHolder viewHolder,tzItem item){
		
//		viewHolder.normal_kind_flag.setVisibility(View.VISIBLE);
//		switch (item.getType()) {
//		case 3:
//			viewHolder.normal_kind_flag
//					.setBackgroundResource(R.drawable.b_tz_type_3);
//			break;
//		case 4:
//			viewHolder.normal_kind_flag
//					.setBackgroundResource(R.drawable.b_tz_type_4);
//			break;
//		case 5:
//			viewHolder.normal_kind_flag
//					.setBackgroundResource(R.drawable.b_tz_type_5);
//			break;
//		case 6:
//			viewHolder.normal_kind_flag
//					.setBackgroundResource(R.drawable.b_tz_type_6);
//			break;
//		case 7:
//			viewHolder.normal_kind_flag
//					.setBackgroundResource(R.drawable.b_tz_type_7);
//			break;
//		case 201:
//			viewHolder.normal_kind_flag
//					.setBackgroundResource(R.drawable.b_tz_type_201);
//			break;
//		case 301:
//			viewHolder.normal_kind_flag
//					.setBackgroundResource(R.drawable.b_tz_type_301);
//			break;
//
//		default:
//			break;
//		}


		viewHolder.tz_title.setText("" + item.getName());
//		viewHolder.post_time.setText("" + item.getTime());///
		viewHolder.tz_money.setText(SystenmApi.getMoneyInfo(item.getMoney())
				+ "元");
		viewHolder.tz_lv.setText(item.getNhll() + "%");
		viewHolder.tz_time.setText(item.getJkqx());

		viewHolder.tz_pro.setProgress((int) item.getProgress());

		viewHolder.tv_iv.setVisibility(View.VISIBLE);
		if(item.getProgress()==100){
			viewHolder.tv_iv.setBackgroundResource(R.drawable.shouqing);
		}else if (item.getIs_xinshou()==1){
			viewHolder.tv_iv.setBackgroundResource(R.drawable.xinshou);
		}
//		else if (item.getIs_taste()==1){
//			viewHolder.tv_iv.setBackgroundResource(R.drawable.tiyan);
//		}
	    else if (item.getIs_taste()==1 && item.getIs_xinshou()==1){
			viewHolder.tv_iv.setBackgroundResource(R.drawable.xinshou);
		}else{
			viewHolder.tv_iv.setVisibility(View.GONE);
		}
		
		
		convertView.setTag(viewHolder);
		
	}

		
	
	private void initSpicalViewItem(View convertView,SpicalViewHolder viewHolder,tzItem item){
		
		
//		viewHolder.normal_kind_flag.setVisibility(View.VISIBLE);
//		viewHolder.normal_kind_flag
//				.setBackgroundResource(R.drawable.b_tz_type_101);
		{

		

			viewHolder.zq_title.setText(item.getName());

			viewHolder.zq_money.setText(SystenmApi.getMoneyInfo(item.getMoney())
					+ "元");
//			viewHolder.zq_bx.setText(item.getDq_money() + "元");
			viewHolder.zq_lv.setText(item.getNhll() + "%");
			viewHolder.zq_qx.setText(item.getJkqx());
			/**是否已转让 1可投0 已转让**/
			if (item.getValid() == 1) {

//				viewHolder.zq_has_done.setVisibility(View.GONE);
//				viewHolder.normal_kind_flag
//				.setBackgroundResource(R.drawable.iv_zrz);
				viewHolder.tz_pro.setProgress((int)item.getProgress());
				
			} else {
//				viewHolder.zq_has_done.setVisibility(View.VISIBLE);
				viewHolder.normal_kind_flag
				.setBackgroundResource(R.drawable.iv_yzr);
				viewHolder.tz_pro.setProgress(100);
			}
			

		}
		
	}
	
	
	
	 static  class NormalViewHolder{
		TextView tz_title,tz_money,tz_lv,tz_time,post_time;
		RoundProgressBar tz_pro ;
//		ImageView normal_kind_flag;
		ImageView tv_iv;
		
	}
	
	
	 static class SpicalViewHolder{
		 ImageView normal_kind_flag;
		 TextView zq_title,zq_money,zq_lv,zq_qx;
//		View zq_has_done ;
		RoundProgressBar tz_pro ;
		
	}
	
	
	/**
	 * 债券转让 Item
	 * @return
	 */
	private View customSpicalItem(View convertView,tzItem item){
		
		SpicalViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater
					.inflate(R.layout.zq_item2_new, null);
			viewHolder = new SpicalViewHolder();
			viewHolder.normal_kind_flag = (ImageView) convertView
					.findViewById(R.id.normal_kind_flag);
			viewHolder.zq_title = (TextView) convertView
					.findViewById(R.id.zq_title);
			viewHolder.zq_money = (TextView) convertView
					.findViewById(R.id.zq_num1);
//			viewHolder.zq_bx = (TextView) convertView
//					.findViewById(R.id.zq_num2);
			viewHolder.zq_lv = (TextView) convertView
					.findViewById(R.id.zq_num3);
			viewHolder.zq_qx = (TextView) convertView
					.findViewById(R.id.zq_num4);
//			viewHolder.zq_has_done = convertView
//					.findViewById(R.id.zq_has_done);
			viewHolder.tz_pro = (RoundProgressBar) convertView
					.findViewById(R.id.progress);
			
			
			
		} else{
			
			viewHolder =  (SpicalViewHolder) convertView.getTag();
		
			
			
		}
		initSpicalViewItem(convertView, viewHolder, item);
		 convertView.setTag(viewHolder);
		 return convertView;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private View customItemView(View currentView,int index){
		
		// 获取当前positon 中包含的信息
		tzItem item = (tzItem) dataSource.get(index);
		
		MyLog.e("item数据"+item.toString());
		
		
		if(spicalViewFlag){
			
			currentView = customSpicalItem(currentView, item);
		}else{
			
			currentView = customNormalItem(currentView, item);
			
		}
		
		return currentView;
	

		
		
	}
	
	
	

	@Override
	public int getCount() {
		
		return dataSource.size();

	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		return customItemView(convertView, position);
	}


}
