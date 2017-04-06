package com.lmq.main.activity.investmanager.reward;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.lmq.main.item.YQJLItem;
import com.lmq.main.util.Default;
import com.lmq.view.ListViewForScrollView;

/***
 * 邀请奖励
 * 
 * @author sjc
 *
 */
public class RewardWithInviteActivity extends BaseActivity implements OnClickListener {

	
	/** 邀请奖励按钮 */
	private Button yq_btn;
	/** 邀请后赠送金额*/
	private TextView tv_jl_money;
	
	/**我要邀请返回的信息*/
	private String message;
	private String url;


	private ArrayList<YQJLItem> data = new ArrayList<YQJLItem>();
	private JSONArray list = null;

	private PullToRefreshScrollView scrollView;
	private ListViewForScrollView listView;
	private YQJLadapter adapter;
	private int page = 1;
	private int limit = 10;
	private int totalPage = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reward_yqjl_list);

		TextView text = (TextView) findViewById(R.id.title);
		text.setText(R.string.reward_invite);


		findViewById(R.id.back).setOnClickListener(this);
		
		tv_jl_money = (TextView) findViewById(R.id.tv_jl_money);
		yq_btn = (Button)findViewById(R.id.yq_btn);
		yq_btn.setOnClickListener(this);
		
		scrollView = (PullToRefreshScrollView) findViewById(R.id.refreshView);
		scrollView.setMode(PullToRefreshBase.Mode.BOTH);

		listView = (ListViewForScrollView) findViewById(R.id.yqjl_list);
		adapter = new YQJLadapter();
		listView.setAdapter(adapter);

		scrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {

				if (refreshView.isHeaderShown()) {
					resetPostParams();
					getListDataYQJL(getPostParams(),true);
				} else {

					if(page<totalPage){

						page+=1;
						getListDataYQJL(getPostParams(),false);

					}else{

						handler.sendEmptyMessage(1);
					}
				}

			}
		});

	}

	public void doHttp(JsonBuilder builder) {

		BaseHttpClient.post(this, Default.invite_index, builder,
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
										  JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						try {

							if (statusCode == 200) {
								if (response.getInt("status") == 1) {
									updateAddInfo(response);
								} else {

									String message = response.getString("message");
									SystenmApi.showCommonErrorDialog(RewardWithInviteActivity.this, response.getInt("status"), message);
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
    	if (json.has("money")) {

			tv_jl_money.setText(json.optString("money", "0"));
		}

		try {

			limit = json.getInt("totalPage");
			if (!json.isNull("list")) {
				list = json.getJSONArray("list");

				if (list != null)
					for (int i = 0; i < list.length(); i++) {
						JSONObject templist = list.getJSONObject(i);
						YQJLItem item = new YQJLItem();
						item.init(templist);
						data.add(item);
					}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			adapter.notifyDataSetChanged();

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
		case R.id.yq_btn:
			getListDataYQ();

			break;
		
		}
	}
	private void infoShare(String url) {

		SystenmApi.showShareView(RewardWithInviteActivity.this, "邀请好友", message, url);

	}

	@Override
	protected void onResume() {
		super.onResume();
		getListDataYQJL(getPostParams(),true);
		
	}

	private JsonBuilder resetPostParams() {

		page = 0;
		limit = 10;

		return getPostParams();
	}
	private JsonBuilder getPostParams() {

		JsonBuilder builder = new JsonBuilder();
		builder.put("page", page);
		builder.put("limit", limit);

		return builder;
	}

	private void getListDataYQJL(JsonBuilder builder ,boolean refsh) {
		if(refsh){
			data.clear();
		}


		BaseHttpClient.post(getBaseContext(), Default.invite_index,
				builder, new JsonHttpResponseHandler() {

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

									MyLog.e("获取邀请列表信息", "" + json.toString());


									initData(json);
								} else {

									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(RewardWithInviteActivity.this, json.getInt("status"), message);
								}
							} else {
								showCustomToast(R.string.toast1);
							}
							dismissLoadingDialog();

							adapter.notifyDataSetChanged();
							scrollView.onRefreshComplete();
						} catch (Exception e) {
							e.printStackTrace();
						}
						dismissLoadingDialog();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
										  String responseString, Throwable throwable) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, responseString, throwable);
						dismissLoadingDialog();
						showCustomToast(responseString);

					}

				});

	}

	public void initData(JSONObject json) {


		if (json.has("money")) {

			tv_jl_money.setText(json.optString("money", "0"));
		}

		try {

			totalPage = json.getInt("totalPage");
			page = json.getInt("nowPage");
			if (json.has("list")) {
				list = json.getJSONArray("list");

				if (!json.isNull("list") || list == null || (list.equals("")) || list.length()==0){

					for (int i = 0; i < list.length(); i++) {
						JSONObject templist = list.getJSONObject(i);
						YQJLItem item = new YQJLItem();
						item.init(templist);
						data.add(item);
			    }
			  }
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter.notifyDataSetChanged();
		scrollView.smootScrollToTop();
	}

	/**
	 * 邀请好友
	 * */
	private void getListDataYQ() {

		BaseHttpClient.post(getBaseContext(),Default.invite_link, null, new JsonHttpResponseHandler() {

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


                             MyLog.e("获取我要邀请信息", "" + json.toString());
                             
                             if (json.has("message")) {

                            	 message=(json.optString("message"));
                            	 
                 			 }
                             if (json.has("url")) {
                            	 
                            	 url=(json.optString("url"));
								 infoShare(url);
                             }
                             
                         } else {
							 String message = json.getString("message");
							 SystenmApi.showCommonErrorDialog(RewardWithInviteActivity.this, json.getInt("status"),message);
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
				super.onFailure(statusCode, headers, responseString, throwable);
				dismissLoadingDialog();
				showCustomToast(responseString);

			}

		});

	}

	class YQJLadapter extends BaseAdapter {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public YQJLItem getItem(int position) {
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
			YQJLItem item = (YQJLItem) data.get(position);

			ViewHolderFirst viewHolderFirst;
			if(convertView == null){
				LayoutInflater mInflater = LayoutInflater.from(RewardWithInviteActivity.this);
				convertView = mInflater.inflate(
						R.layout.reward_adapter_item_invite_rewards, null);
					viewHolderFirst = new ViewHolderFirst();

					viewHolderFirst. rewardyq_user = (TextView) convertView
							.findViewById(R.id.rewardyq_user);
					viewHolderFirst. rewardyq_usertime = (TextView) convertView
							.findViewById(R.id.rewardyq_usertime);
					viewHolderFirst. rewardyq_jl = (TextView) convertView.
							findViewById(R.id.rewardyq_jl);
					viewHolderFirst. rewardyq_shengxiao = (TextView) convertView
							.findViewById(R.id.rewardyq_shengxiao);

					convertView.setTag(viewHolderFirst);



				}else {

				viewHolderFirst = (ViewHolderFirst) convertView.getTag();
			}

				viewHolderFirst.rewardyq_user.setText(item.getUser_name());
				viewHolderFirst.rewardyq_usertime.setText(item.getReg_time());
				viewHolderFirst.rewardyq_jl.setText(item.getMoney());
				viewHolderFirst.rewardyq_shengxiao.setText(item.getBe());

			return convertView;
		}
	}
	
	private static class ViewHolderFirst {

		TextView rewardyq_user;
		TextView rewardyq_usertime;
		TextView rewardyq_jl;
		TextView rewardyq_shengxiao;
	}


	public void finish() {
		super.finish();
	}

}
