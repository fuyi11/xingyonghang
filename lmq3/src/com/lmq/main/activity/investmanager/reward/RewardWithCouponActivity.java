package com.lmq.main.activity.investmanager.reward;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.item.YHQItem;
import com.lmq.main.util.Default;
import com.lmq.view.ListViewForScrollView;

/***
 * 优惠券
 * 
 * @author sjc
 *
 */
public class RewardWithCouponActivity extends BaseActivity implements OnClickListener {

	/** 未使用优惠券 */
	private TextView wsy_yhq;
	/** 已使用优惠券 */
	private TextView ygq_yhq;
	/** 未使用优惠券总额 */
	private TextView wsy_yhq_money;
	/** 已过期优惠券总额 */
	private TextView ysy_yhq_money;

	/** 我的优惠券按钮 */
	private RadioButton yhq_btn;
	/** 奖励记录按钮 */
	private RadioButton jljl_btn;

	private ListViewForScrollView listView;
	private ListViewForScrollView listView2;

	private ArrayList<YHQItem> data = new ArrayList<YHQItem>();
	private JSONArray list = null;


	private YHQadapter adapter;
	private YHQadapter2 adapter2;
	private int maxPage,curPage = 1, pageCount = 8;

	private String n_num;
	private String n_money;
	private String y_money;
	private String ex_money;
	private PullToRefreshScrollView scrollView;


	/** 优惠券Type */
	private int type = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reward_yhq);

		TextView text = (TextView) findViewById(R.id.title);
		text.setText(R.string.reward_title);

		findViewById(R.id.back).setOnClickListener(this);

		scrollView = (PullToRefreshScrollView) findViewById(R.id.refreshView);


		scrollView.setMode(PullToRefreshBase.Mode.BOTH);

		wsy_yhq = (TextView) findViewById(R.id.wsy_yhq);
		ygq_yhq = (TextView) findViewById(R.id.ygq_yhq);
		wsy_yhq_money = (TextView) findViewById(R.id.wsy_yhq_money);
		ysy_yhq_money = (TextView) findViewById(R.id.ysy_yhq_money);

		yhq_btn = (RadioButton) findViewById(R.id.yhq_btn);
		jljl_btn = (RadioButton) findViewById(R.id.jljl_btn);

		yhq_btn.setOnClickListener(this);
		jljl_btn.setOnClickListener(this);

		listView = (ListViewForScrollView) findViewById(R.id.yhq_list);
		listView2 = (ListViewForScrollView) findViewById(R.id.yhq_list2);
//		listView2.setAdapter(adapter2);


//		listView.setMode(PullToRefreshBase.Mode.BOTH);
		adapter = new YHQadapter();
		adapter2 = new YHQadapter2();
		listView.setAdapter(adapter);
		listView2.setAdapter(adapter2);



		listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				// TODO 跳转奖励记录详情页面


				Intent intent = new Intent();

				intent.putExtra("remark", adapter.getItem(position)
						.getRemark());
				intent.putExtra("add_time", adapter.getItem(position)
						.getAdd_time());
				intent.putExtra("money", adapter.getItem(position)
						.getMoney());
				intent.putExtra("status", adapter.getItem(position)
						.getStatus());
				intent.putExtra("status", adapter.getItem(position)
						.getStatus());
				intent.putExtra("exp_type", adapter.getItem(position)
						.getExp_type());
				intent.setClass(RewardWithCouponActivity.this,
						RewardWithCouponDetailActivity.class);
				startActivity(intent);


			}
		});


		scrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {

				if (refreshView.isHeaderShown()) {

					getListDataFromSever(type);
				} else {
					JsonBuilder builder = new JsonBuilder();
					if (curPage + 1 <= maxPage) {
						curPage++;
						builder.put("page", curPage);
						builder.put("limit", pageCount);
						doHttp(builder);
					} else {
						handler.sendEmptyMessage(1);
					}
				}

			}
		});





	}
	
	 public void doHttp(JsonBuilder builder) {


			BaseHttpClient.post(getBaseContext(), type == 0 ? Default.get_coupon
					: Default.expLog, builder, new JsonHttpResponseHandler() {

						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							super.onStart();
							showLoadingDialogNoCancle(getResources().getString(
									R.string.toast2));
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							// TODO Auto-generated method stub
							super.onSuccess(statusCode, headers, response);
							try {

								if (statusCode == 200) {
									if (response.getInt("status") == 1) {
										updateAddInfo(response);
									}else {
										String message = response.getString("message");
										SystenmApi.showCommonErrorDialog(RewardWithCouponActivity.this, response.getInt("status"),message);
									}
								} else {
									showCustomToast(R.string.toast1);
								}
								dismissLoadingDialog();
							} catch (Exception e) {
								e.printStackTrace();
							}

							dismissLoadingDialog();
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONObject errorResponse) {
							// TODO Auto-generated method stub
							super.onFailure(statusCode, headers, throwable,
									errorResponse);

							adapter.notifyDataSetChanged();

							scrollView.onRefreshComplete();
							adapter.notifyDataSetChanged();
							dismissLoadingDialog();
						}
					});
		}
	    
	    public void updateAddInfo(JSONObject json) {
			try {

				maxPage = json.getInt("totalPage");
				list = null;
				if (!json.isNull("list")) {
					list = json.getJSONArray("list");

					if (list != null)
						for (int i = 0; i < list.length(); i++) {
							JSONObject templist = list.getJSONObject(i);
							YHQItem item = new YHQItem();
							item.init(templist);
							data.add(item);

						}

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(type == 0){
				adapter.notifyDataSetChanged();
			}else{
				adapter2.notifyDataSetChanged();
			}


			scrollView.onRefreshComplete();
		}
	    
		Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				if (msg.arg1 == 2) {
					showCustomToast(msg.getData().getString("info"));
				}
				if (msg.what == 1) {
					showCustomToast(("无更多数据！"));

					scrollView.onRefreshComplete();
				}
			}

		};

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.back:
			finish(0);
			break;
		case R.id.yhq_btn:
			type = 0;
			data.clear();
			adapter2.notifyDataSetChanged();
			listView2.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			adapter.notifyDataSetChanged();
			getListDataFromSever(type);
			break;
		case R.id.jljl_btn:
			type = 1;
			data.clear();
			adapter.notifyDataSetChanged();
			listView.setVisibility(View.GONE);
			listView2.setVisibility(View.VISIBLE);
			adapter2.notifyDataSetChanged();
			getListDataFromSever(type);
			break;

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		getListDataFromSever(type);
	}

	private void getListDataFromSever(int type) {
		/***
		 * 清除原有数据
		 */
		if (data.size() > 0) {
			data.clear();
		}
		BaseHttpClient.NO_RAS=true;
		JsonBuilder builder = new JsonBuilder();
		builder.put("page", 0);
		builder.put("limit", 5);

		BaseHttpClient.post(getBaseContext(), type == 0 ? Default.get_coupon
				: Default.expLog, builder, new JsonHttpResponseHandler() {

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
                         // 没有新版本
                         if (json.getInt("status") == 1) {


                             MyLog.e("获取优惠券信息", "" + json.toString());

                             initData(json);
                         } else {
							 String message = json.getString("message");
							 SystenmApi.showCommonErrorDialog(RewardWithCouponActivity.this, json.getInt("status"),message);
						 }
                         dismissLoadingDialog();

						 adapter.notifyDataSetChanged();
						 scrollView.onRefreshComplete();
                     } else {
                         showCustomToast(R.string.toast1);
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
                 dismissLoadingDialog();
				scrollView.onRefreshComplete();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, responseString, throwable);
				dismissLoadingDialog();
				showCustomToast(responseString);
				scrollView.onRefreshComplete();

			}

		});

	}

	public void initData(JSONObject json) {

		try {
			maxPage = json.getInt("totalPage");
			curPage = json.getInt("nowPage");
			if (json.has("n_num")) {

				wsy_yhq.setText(json.optString("n_num", "0") + "张");
			}

			if (json.has("n_money")) {

				wsy_yhq_money.setText(json.optString("n_money", "0") + "元");
			}

			if (json.has("y_money")) {

				ysy_yhq_money.setText(json.optString("y_money", "0") + "元");
			}
			if (json.has("ex_money")) {

				ygq_yhq.setText(json.optString("ex_money", "0") + "元");
			}

			if (!json.isNull("list")) {
				list = json.getJSONArray("list");

				if (list != null)
					for (int i = 0; i < list.length(); i++) {
						JSONObject templist = list.getJSONObject(i);
						YHQItem item = new YHQItem();
						item.init(templist);
						data.add(item);
					}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(type==0){
			adapter.notifyDataSetChanged();
		}else{

			adapter2.notifyDataSetChanged();
		}

		scrollView.smootScrollToTop();

	}

	class YHQadapter extends BaseAdapter {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public YHQItem getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			MyLog.e("123","1--------------");
			// 获取当前positon 中包含的信息
			YHQItem item = (YHQItem) data.get(position);

			ViewHolderFirst viewHolderFirst;
			if(convertView == null){
				LayoutInflater mInflater = LayoutInflater.from(RewardWithCouponActivity.this);
				convertView = mInflater.inflate(
						R.layout.reward_adapter_item_rewards_yhq, null);
					viewHolderFirst = new ViewHolderFirst();

					viewHolderFirst.tv_money = (TextView) convertView
							.findViewById(R.id.tv_money);
					viewHolderFirst.money = (TextView) convertView
							.findViewById(R.id.money);
					viewHolderFirst.invest_money = (TextView) convertView
							.findViewById(R.id.invest_money);
					viewHolderFirst.type = (TextView) convertView.findViewById(R.id.type);
					viewHolderFirst.tv_yhq_endtime = (TextView) convertView
							.findViewById(R.id.tv_yhq_endtime);
					viewHolderFirst.source = (TextView) convertView
							.findViewById(R.id.source);
					viewHolderFirst.coupon_type = (TextView) convertView
							.findViewById(R.id.coupon_type);
					viewHolderFirst.yhq_has_done = (ImageView) convertView
							.findViewById(R.id.yhq_has_done);
					viewHolderFirst.rl_status = (LinearLayout) convertView
							.findViewById(R.id.rl_status);

					convertView.setTag(viewHolderFirst);



				}else {

				viewHolderFirst = (ViewHolderFirst) convertView.getTag();
			}




				viewHolderFirst.tv_money.setText(item.getMoney());
				viewHolderFirst.money.setText(item.getMoney() + "元");
				viewHolderFirst.invest_money.setText(item.getInvest_money() + "元");
				//viewHolderFirst.type.setText(item.getType() + "");
				viewHolderFirst.tv_yhq_endtime.setText(item.getFunds());
				viewHolderFirst.source.setText(item.getExp_type());
				viewHolderFirst.coupon_type.setText(item.getCoupon_type());

			/** 优惠券状态 0未使用 1已使用 2已过期 */
			if (item.getStatus() == 0) {
				viewHolderFirst.yhq_has_done.setVisibility(View.GONE);
				viewHolderFirst.rl_status.setBackgroundResource(R.drawable.uncoupon);

			}  if (item.getStatus() == 1) {
				viewHolderFirst.yhq_has_done.setVisibility(View.VISIBLE);
				viewHolderFirst.yhq_has_done.setBackgroundResource(R.drawable.coupon_use);
				viewHolderFirst.rl_status.setBackgroundResource(R.drawable.coupon);
			}

			if (item.getStatus() == 2) {
				viewHolderFirst.yhq_has_done.setVisibility(View.VISIBLE);
				viewHolderFirst.yhq_has_done.setBackgroundResource(R.drawable.coupon_ygq);
				viewHolderFirst.rl_status.setBackgroundResource(R.drawable.coupon);
			}

			return convertView;
		}
	}

	class YHQadapter2 extends BaseAdapter {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public YHQItem getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyLog.e("123","2--------------");
			YHQItem item = (YHQItem) data.get(position);

			ViewHolderSecond viewHolderSecond = null;
			if(null == convertView){

				viewHolderSecond = new ViewHolderSecond();
				LayoutInflater mInflater = LayoutInflater.from(RewardWithCouponActivity.this);
				convertView = mInflater.inflate(
						R.layout.reward_adapter_item_rewards_record, null);


				viewHolderSecond.tv_money = (TextView) convertView
						.findViewById(R.id.tv_money);
				viewHolderSecond.tv_rewardtzjf_type = (TextView) convertView
						.findViewById(R.id.tv_rewardtzjf_type);
				viewHolderSecond.tv_rewardtzjf_time = (TextView) convertView
						.findViewById(R.id.tv_rewardtzjf_time);
				viewHolderSecond.tv_rewardtzjf_detail = (TextView) convertView
						.findViewById(R.id.tv_rewardtzjf_detail);
				viewHolderSecond.lv = (LinearLayout) convertView
						.findViewById(R.id.lv);

				convertView.setTag(viewHolderSecond);
			}else {

				viewHolderSecond = (ViewHolderSecond) convertView.getTag();
			}

				viewHolderSecond.tv_money.setText(item.getMoney());
				viewHolderSecond.tv_rewardtzjf_type.setText(item.getExp_type());
				viewHolderSecond.tv_rewardtzjf_time.setText(item.getAdd_time());
				/**状态 0未使用 1已使用 2已过期**/
				if(item.getStatus()==0){
					viewHolderSecond.tv_rewardtzjf_detail.setText("未使用");

				}

				if(item.getStatus()==1){
					viewHolderSecond.tv_rewardtzjf_detail.setText("已使用");
//					viewHolderSecond.lv.setBackgroundResource(R.drawable.coupon);

				}
				if(item.getStatus()==2){
					viewHolderSecond.tv_rewardtzjf_detail.setText("已过期");
//					viewHolderSecond.lv.setBackgroundResource(R.drawable.coupon);
				}




			return convertView;
		}
	}

	private static class ViewHolderFirst {

		TextView tv_money;
		TextView money;
		TextView invest_money;
		TextView type;
		TextView tv_yhq_endtime;
		TextView source;
		TextView coupon_type;
		ImageView yhq_has_done;
		LinearLayout rl_status;
	}

	private static class ViewHolderSecond{

		TextView tv_money;
		TextView tv_rewardtzjf_type ;
		TextView tv_rewardtzjf_time ;
		TextView tv_rewardtzjf_detail;
		LinearLayout lv;


	}

	public void finish() {
		super.finish();
	}

}
