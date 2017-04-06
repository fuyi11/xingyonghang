package com.lmq.main.activity.user.manager.tenderlogs;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.item.tzDetailItem;
import com.lmq.main.util.Default;
import com.lmq.http.JsonHttpResponseHandler;

public class InvestLogsActivity extends BaseActivity implements
		OnClickListener {

	private PullToRefreshListView mListView;
	private List mList;
	private tzDetailAdapter adapter;

	private String itemId;
	private int itemType;

	private int maxPage, curPage = 1, pageCount = 8;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tzdetail);
		initView();
	}

	public void initView() {
		TextView text = (TextView) findViewById(R.id.title);
		text.setText(R.string.tzdetail);

		mListView = (PullToRefreshListView) findViewById(R.id.listview);
		mListView.setMode(PullToRefreshBase.Mode.BOTH);

		mListView.setAdapter(adapter);
//		mListView.setDividerHeight(0);
//		mListView.showFootView(true);

		findViewById(R.id.back).setOnClickListener(this);

		Intent intent = getIntent();
		if (intent != null) {
			
			 if (intent.hasExtra("type")) {

				 itemType = intent.getIntExtra("type", 0);
		     }
			 if (intent.hasExtra("id")) {
				 
				 itemId = intent.getStringExtra("id");
				 MyLog.e("8888888888888888", itemId+"");
			 }
		}

		adapter = new tzDetailAdapter();
		mListView.setAdapter(adapter);

//		mListView.setOnLoadMoreInfo(new LoadMoreInfo() {
//			public void onRefresh() {
//				curPage = 1;
//				doHttp(false);
//			}
//
//			public void onLoadMore() {
//				if (curPage + 1 <= maxPage) {
//					curPage++;
//					JsonBuilder builder = new JsonBuilder();
//					builder.put("limit", pageCount);
//					builder.put("page", curPage);
//					doHttp(true);
//				} else {
//					showCustomToast(("无更多数据！"));
//					handler.sendEmptyMessage(1);
//				}
//			}
//		});
		
		mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                if (refreshView.isHeaderShown()) {
                	curPage = 1;
    				doHttp(false);
                } else {


                    if (curPage + 1 <= maxPage) {
                        curPage++;
                        JsonBuilder builder = new JsonBuilder();
                        builder.put("limit", pageCount);
                        builder.put("page", curPage);
                        doHttp(true);
                    } else {
                        showCustomToast(("无更多数据！"));
                        handler.sendEmptyMessage(1);
                    }
                }


            }
        });
		curPage = 1;
		doHttp(false);
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {
				mListView.onRefreshComplete();
			}
		}

	};

	public void updateData(JSONObject json) {
		mList = new ArrayList<tzDetailItem>();
		JSONArray list;
		try {
			maxPage = json.getInt("totalPage");
			list = json.getJSONArray("list");

			for (int i = 0; i < list.length(); i++) {
				JSONObject temp = list.getJSONObject(i);

				tzDetailItem item = new tzDetailItem(temp);

				mList.add(item);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter.notifyDataSetChanged();
		handler.sendEmptyMessage(1);
	}

	public void addData(JSONObject json) {
		JSONArray list;
		try {
			maxPage = json.getInt("totalPage");
			list = json.getJSONArray("list");

			for (int i = 0; i < list.length(); i++) {
				JSONObject temp = list.getJSONObject(i);

				tzDetailItem item = new tzDetailItem(temp);

				mList.add(item);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter.notifyDataSetChanged();
		handler.sendEmptyMessage(1);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		}
	}

	class tzDetailAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (mList == null)
				return 0;
			else
				return mList.size();
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
			if (convertView == null) {
				convertView = LayoutInflater.from(InvestLogsActivity.this)
						.inflate(R.layout.adapter_item_tz_detail, null);
			}
			TextView money1 = (TextView) convertView.findViewById(R.id.money1);
//			TextView money2 = (TextView) convertView.findViewById(R.id.money2);
			TextView people = (TextView) convertView.findViewById(R.id.tzpeo);
			TextView time = (TextView) convertView.findViewById(R.id.tztime);
			ImageView iv = (ImageView) convertView.findViewById(R.id.iv);
			

			tzDetailItem item = (tzDetailItem) mList.get(position);

			money1.setText(item.getInvestor_capital() + "元");
//			money2.setText(item.getInvestor_capital() + "元");
			people.setText(item.getUser_name());
			time.setText(item.getAdd_time() + "");
			if(item.getSource()==2){
				iv.setBackgroundResource(R.drawable.tbjl_phone);
			}else{
				iv.setVisibility(View.GONE);
			}

			return convertView;
		}

	}

	private void doHttp(final boolean add) {

		JsonBuilder builder = new JsonBuilder();

		builder.put("limit", pageCount);
		builder.put("page", curPage);
		builder.put("id", itemId);
//		builder.put("type", itemType);
		if(itemType==101){
		builder.put("type", itemType);	
		}

		BaseHttpClient.post(getBaseContext(), Default.showPtbjl, builder,
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
									if (add) {
										addData(json);
									} else
										updateData(json);
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(InvestLogsActivity.this, json.getInt("status"), message);
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
						 mListView.onRefreshComplete();
						showCustomToast(responseString);
					}

				});

	}

}
