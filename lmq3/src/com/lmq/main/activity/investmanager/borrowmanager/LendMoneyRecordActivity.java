package com.lmq.main.activity.investmanager.borrowmanager;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.investmanager.debet.InvestManageDetailActivity;
import com.lmq.main.activity.investmanager.debet.InvestManagerDebetZR;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.dialog.InvestPopList;
import com.lmq.main.dialog.LendManagerRepaymentDialog;
import com.lmq.main.item.Borrowing_RecordItem;
import com.lmq.main.item.InvestManageItem;
import com.lmq.main.item.LendMoneyItem;
import com.lmq.main.util.Default;
import com.lmq.view.MyListView;
import com.lmq.view.MyListView.LoadMoreInfo;

/**
 * 
 * 借款记录
 * 
 */
public class LendMoneyRecordActivity extends BaseActivity implements
		OnClickListener {

	private LendMoneyOfUserAdapter adapter;
	private PullToRefreshListView listView;
	public int swith_flag = 0;
	private String[] tipsData;
	private ArrayList<Borrowing_RecordItem> data;

	private View button1, button2, button3, button4,button5;
	private TextView title1, title2, title3, title4, title5,text;
	private ImageView line1, line2, line3, line4, line5;

	private int  totalPage = 6;
	private int page = 1;
	private LendManagerRepaymentDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_of_lendmoney);

		findViewById(R.id.back).setOnClickListener(this);

		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		resetPostParams();
		getBorrowData(getPostParams(), true);
	}

	public void initView() {
		text = (TextView) findViewById(R.id.title);
		text.setText(R.string.record_lendmoney);
		// 绑定选项卡点击事件
		button1 = (View) findViewById(R.id.normal_btn);
		button2 = (View) findViewById(R.id.normal2_btn);
		button3 = (View) findViewById(R.id.normal3_btn);
		button4 = (View) findViewById(R.id.normal4_btn);
		button5 = (View) findViewById(R.id.normal5_btn);

		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		button4.setOnClickListener(this);
		button5.setOnClickListener(this);

		title1 = (TextView) findViewById(R.id.top_title1);
		title2 = (TextView) findViewById(R.id.top_title2);
		title3 = (TextView) findViewById(R.id.top_title3);
		title4 = (TextView) findViewById(R.id.top_title4);
		title5 = (TextView) findViewById(R.id.top_title5);

		line1 = (ImageView) findViewById(R.id.top_title1_img);
		line2 = (ImageView) findViewById(R.id.top_title2_img);
		line3 = (ImageView) findViewById(R.id.top_title3_img);
		line4 = (ImageView) findViewById(R.id.top_title4_img);
		line5 = (ImageView) findViewById(R.id.top_title5_img);

		title1.setTextColor(getResources().getColor(R.color.red));
		line1.setVisibility(View.VISIBLE);
		data = new ArrayList<Borrowing_RecordItem>();
		tipsData = LendMoneyRecordActivity.this.getResources().getStringArray(R.array.borrow_type);
		listView = (PullToRefreshListView) findViewById(R.id.listView);
		listView.setMode(PullToRefreshBase.Mode.BOTH);
		adapter = new LendMoneyOfUserAdapter();
		listView.setAdapter(adapter);

		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				if (refreshView.isHeaderShown()) {
					resetPostParams();
					getBorrowData(getPostParams(), true);
				}
				if (refreshView.isFooterShown()) {

					if (page < totalPage) {

						page += 1;
						getBorrowData(getPostParams(), false);

					} else {
						handler.sendEmptyMessage(1);
					}


				}

			}
		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				position = position -1;
				Borrowing_RecordItem item = adapter.getItem(position);
				switch (swith_flag){

					case 0:


						break;

					case 1:
						Intent intent1 = new Intent();
						intent1.putExtra("bid",item.getBid());
						intent1.setClass(LendMoneyRecordActivity.this,LendMoneyRecordDetailActivity.class);
						startActivity(intent1);

						break;
					case 2:

						Intent intent2 = new Intent();
						intent2.putExtra("bid",item.getBid());
						intent2.setClass(LendMoneyRecordActivity.this,LendMoneyRecordDetailActivity.class);
						startActivity(intent2);

						break;
					case 4:
//						Borrowing_RecordItem item = adapter.getItem(position);
						Intent intent4 = new Intent();
						intent4.putExtra("bid",item.getBid());
						intent4.setClass(LendMoneyRecordActivity.this,LendMoneyRecordDetailActivity.class);
						startActivity(intent4);

						break;

				}

			}
		});

	}

	public void LendManagerRepaymentDialog(final View view,String content){

		if(null == dialog){


			dialog = new LendManagerRepaymentDialog(LendMoneyRecordActivity.this);
			dialog.setDialogTitle("信息");

			dialog.setonClickListener(this);


		}

		dialog.setInfo(content);

		if(!dialog.isShowing()){
			dialog.showAsDropDown(view);
		}


	}

	public int getType() {
		switch (swith_flag) {
			case 0:
				return 1;
			case 1:
				return 2;
			case 2:
				return 3;
			case 3:
				return 4;
			case 4:
				return 5;
			default:
				return 1;
		}
	}
	private JsonBuilder resetPostParams() {

		page = 1;
		totalPage = 6;

		return getPostParams();
	}



	private JsonBuilder getPostParams() {

		JsonBuilder builder = new JsonBuilder();
		builder.put("page", page);
		builder.put("limit", totalPage);
		builder.put("type", getType());

		return builder;
	}

	private void getDoerase (final View view, final String i){

		JsonBuilder builder = new JsonBuilder();
		builder.put("bid", i);

		BaseHttpClient.post(getBaseContext(), Default.DOERASE, builder,
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						showLoadingDialogNoCancle(getResources().getString(
								R.string.toast2));
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
										  JSONObject json) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, json);

						try {

							if (statusCode == 200) {
								if (json.getInt("status") == 1) {

									LendManagerRepaymentDialog(view,i);
								} else {

								}
							} else {
								showCustomToast(R.string.toast1);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						dismissLoadingDialog();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
										  String responseString, Throwable throwable) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, responseString,
								throwable);
						dismissLoadingDialog();
						showCustomToast(responseString);

					}

				});


	}

	public void getBorrowData(JsonBuilder builder ,boolean refsh) {


		if(refsh){
			data.clear();
		}

		BaseHttpClient.post(getBaseContext(), Default.SUMMALIST, builder,
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						showLoadingDialogNoCancle(getResources().getString(
								R.string.toast2));
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
										  JSONObject json) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, json);

						try {

							if (statusCode == 200) {
								if (json.getInt("status") == 1) {

									if (json.has("totalPage")) {

										totalPage = json.optInt("totalPage", 0);
									}

									if (json.has("nowPage")) {

										page = json.optInt("nowPage", 0);
									}


									if (json.has("list")) {


										JSONArray array = json.getJSONArray("list");

										if (null != array && array.length() > 0) {


											for (int i = 0; i < array.length(); i++) {
												Borrowing_RecordItem item = new Borrowing_RecordItem(array.getJSONObject(i));
												data.add(item);
											}

										}

									} else {
										showCustomToast(json.getString("message"));

									}


								} else if (json.getInt("status") == 0) {
									showCustomToast(json.getString("message"));

								} else {
									showCustomToast(json.getString("message"));
								}
							} else {
								showCustomToast(R.string.toast1);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						dismissLoadingDialog();
						adapter.notifyDataSetChanged();
						listView.onRefreshComplete();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
										  String responseString, Throwable throwable) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, responseString,
								throwable);
						dismissLoadingDialog();
						showCustomToast(responseString);
						listView.onRefreshComplete();

					}

				});

		adapter.notifyDataSetChanged();

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		switch (arg0.getId()) {
		case R.id.back:
			finish();
			break;
			case R.id.normal_btn:
				changeFlag(0);
				break;
			case R.id.normal2_btn:
				changeFlag(1);
				break;
			case R.id.normal3_btn:
				changeFlag(2);
				break;
			case R.id.normal4_btn:
				changeFlag(3);
				break;
			case R.id.normal5_btn:
				changeFlag(4);
				break;

		default:
			break;
		}

	}

	public void changeFlag(int type)
	{
		swith_flag = type;
		changeBackground();
		switch(type)
		{
			case 0:
				title1.setTextColor(this.getResources().getColor(
						R.color.red));
				line1.setVisibility(View.VISIBLE);
				break;
			case 1:
				title2.setTextColor(this.getResources().getColor(
						R.color.red));
				line2.setVisibility(View.VISIBLE);
				break;
			case 2:
				title3.setTextColor(this.getResources().getColor(
						R.color.red));
				line3.setVisibility(View.VISIBLE);
				break;
			case 3:
				title4.setTextColor(this.getResources().getColor(
						R.color.red));
				line4.setVisibility(View.VISIBLE);
				break;
			case 4:
				title5.setTextColor(this.getResources().getColor(
						R.color.red));
				line5.setVisibility(View.VISIBLE);
				break;

		}
		resetPostParams();
		getBorrowData(getPostParams(), true);


	}

	private void changeBackground() {

		title1.setTextColor(this.getResources()
				.getColor(R.color.black));
		title2.setTextColor(this.getResources()
				.getColor(R.color.black));
		title3.setTextColor(this.getResources()
				.getColor(R.color.black));
		title4.setTextColor(this.getResources()
				.getColor(R.color.black));
		title5.setTextColor(this.getResources()
				.getColor(R.color.black));

		line1.setVisibility(View.GONE);
		line2.setVisibility(View.GONE);
		line3.setVisibility(View.GONE);
		line4.setVisibility(View.GONE);
		line5.setVisibility(View.GONE);

	}

	public void finish() {
		super.finish();
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);


			if (msg.what == 1) {
				showCustomToast(("无更多数据！"));

				listView.onRefreshComplete();
			}
		}

	};


	private class LendMoneyOfUserAdapter extends BaseAdapter{


		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Borrowing_RecordItem getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if(convertView == null){
				viewHolder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(),R.layout.jz_myborrow_item1,null);
				/***提示*/
				viewHolder.borrow_item_tips1 = (TextView) convertView.findViewById(R.id.borrow_item_tips1);
				viewHolder.borrow_item_tips2 = (TextView) convertView.findViewById(R.id.borrow_item_tips2);
				viewHolder.borrow_item_tips3 = (TextView) convertView.findViewById(R.id.borrow_item_tips3);
				viewHolder.borrow_item_tips4 = (TextView) convertView.findViewById(R.id.borrow_item_tips4);

				viewHolder.loan_manage_item_jkbmc = (TextView) convertView.findViewById(R.id.loan_manage_item_jkbmc);
				viewHolder.loan_manage_item_je = (TextView) convertView.findViewById(R.id.loan_manage_item_je);
				viewHolder.loan_manage_item_ll = (TextView) convertView.findViewById(R.id.loan_manage_item_ll);
				viewHolder.loan_manage_item_qx = (TextView) convertView.findViewById(R.id.loan_manage_item_qx);
				viewHolder.loan_manage_item_sj = (TextView) convertView.findViewById(R.id.loan_manage_item_sj);
				viewHolder.epayment_way_item_hkfs = (TextView) convertView.findViewById(R.id.epayment_way_item_hkfs);
				viewHolder.option = (TextView) convertView.findViewById(R.id.option);

				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			cheangeTips();

			viewHolder.borrow_item_tips1.setText(tipsData[0]);
			viewHolder.borrow_item_tips2.setText(tipsData[1]);
			viewHolder.borrow_item_tips3.setText(tipsData[2]);
			viewHolder.borrow_item_tips4.setText(tipsData[3]);

			viewHolder.loan_manage_item_jkbmc.setText(adapter.getItem(position).getBorrow_name());
			viewHolder.loan_manage_item_je.setText(SystenmApi.getMoneyInfo(adapter.getItem(position).getBorrow_money()));
			viewHolder.loan_manage_item_ll.setText(adapter.getItem(position).getBorrow_interest_rate()+ "%");
			viewHolder.loan_manage_item_qx.setText(adapter.getItem(position).getBorrow_duration());
			viewHolder.loan_manage_item_sj.setText(adapter.getItem(position).getAdd_time());
			viewHolder.epayment_way_item_hkfs.setText(adapter.getItem(position).getRepayment_type());

			if (swith_flag == 0) {

				//option 这个字段=0 不显示撤销  =1 可以撤销 ，撤销借款功能
				if(adapter.getItem(position).getOption()==1){
					viewHolder.option.setVisibility(View.VISIBLE);
					viewHolder.option.setText("撤销");

			   }else{
					viewHolder.option.setVisibility(View.GONE);
				}
				viewHolder.loan_manage_item_ll.setTextColor(Color.RED);
				viewHolder.loan_manage_item_je.setTextColor(Color.BLACK);
				viewHolder.loan_manage_item_qx.setTextColor(Color.BLACK);
				viewHolder.loan_manage_item_sj.setTextColor(Color.BLACK);

			}
			if (swith_flag == 1) {
				viewHolder.option.setVisibility(View.GONE);
				viewHolder.loan_manage_item_je.setText(SystenmApi.getMoneyInfo(adapter.getItem(position).getBorrow_money()) + "/" + adapter.getItem(position).getReceive());
				viewHolder.loan_manage_item_sj.setText(adapter.getItem(position).getDeadline());
				viewHolder.loan_manage_item_sj.setTextColor(Color.RED);
				viewHolder.loan_manage_item_ll.setTextColor(Color.BLACK);
				viewHolder.loan_manage_item_je.setTextColor(Color.BLACK);
				viewHolder.loan_manage_item_qx.setTextColor(Color.BLACK);



			}
			if (swith_flag == 2) {
				viewHolder.option.setVisibility(View.GONE);
				viewHolder.loan_manage_item_je.setText(adapter.getItem(position).getCapital());
				viewHolder.loan_manage_item_ll.setText(adapter.getItem(position).getInterest());
				viewHolder.loan_manage_item_qx.setText(adapter.getItem(position).getExpired_money_now());
				viewHolder.loan_manage_item_sj.setText(adapter.getItem(position).getCall_fee_now());
				viewHolder.epayment_way_item_hkfs.setText("逾期天数:" + adapter.getItem(position).getExpired_time() + "天");

				viewHolder.loan_manage_item_sj.setTextColor(Color.BLACK);
				viewHolder.loan_manage_item_ll.setTextColor(Color.BLACK);
				viewHolder.loan_manage_item_je.setTextColor(Color.BLACK);
				viewHolder.loan_manage_item_qx.setTextColor(Color.BLACK);
			}
			if (swith_flag == 3) {
				viewHolder.option.setVisibility(View.GONE);
				viewHolder.loan_manage_item_sj.setText(adapter.getItem(position).getBorrow_status());
				viewHolder.loan_manage_item_sj.setTextColor(Color.RED);
				viewHolder.loan_manage_item_ll.setTextColor(Color.BLACK);
				viewHolder.loan_manage_item_je.setTextColor(Color.BLACK);
				viewHolder.loan_manage_item_qx.setTextColor(Color.BLACK);
			}
			if (swith_flag == 4) {
				viewHolder.option.setVisibility(View.GONE);
				viewHolder.loan_manage_item_sj.setText(adapter.getItem(position).getAdd_time());
				viewHolder.loan_manage_item_sj.setTextColor(Color.RED);
				viewHolder.loan_manage_item_ll.setTextColor(Color.BLACK);
				viewHolder.loan_manage_item_je.setTextColor(Color.BLACK);
				viewHolder.loan_manage_item_qx.setTextColor(Color.BLACK);

			}

			viewHolder.option.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

							Intent intent = new Intent();
							intent.putExtra("bid",adapter.getItem(position).getBid());
							intent.setClass(LendMoneyRecordActivity.this,LendMoneyDialogDoerase.class);
							startActivity(intent);


				}
			});

			return convertView;
		}
	}

	private void cheangeTips(){

		tipsData = null;

		switch (swith_flag){

			case 0:
				tipsData = LendMoneyRecordActivity.this.getResources().getStringArray(R.array.borrow_type);
				break;

			case 1:
				tipsData = LendMoneyRecordActivity.this.getResources().getStringArray(R.array.borrow_type2);
				break;
			case 2:
				tipsData = LendMoneyRecordActivity.this.getResources().getStringArray(R.array.borrow_type3);
				break;
			case 3:
				tipsData = LendMoneyRecordActivity.this.getResources().getStringArray(R.array.borrow_type4);
				break;
			case 4:
				tipsData = LendMoneyRecordActivity.this.getResources().getStringArray(R.array.borrow_type5);
				break;

		}


	}

	class ViewHolder{

		/**提示*/
		TextView borrow_item_tips1;
		TextView borrow_item_tips2;
		TextView borrow_item_tips3;
		TextView borrow_item_tips4;

		private TextView loan_manage_item_jkbmc;
		private TextView loan_manage_item_je;
		private TextView loan_manage_item_ll;
		private TextView loan_manage_item_qx;
		private TextView loan_manage_item_sj;
		private TextView epayment_way_item_hkfs;
		private TextView option;

	}

	}


