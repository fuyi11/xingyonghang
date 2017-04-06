package com.lmq.main.activity.news;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.tools.LMQWebViewActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.item.newsItem;
import com.lmq.main.util.Default;

/**
 * 网站新闻
 *
 */
public class NoticeActivity extends BaseActivity implements OnClickListener {

	private PullToRefreshListView mListView;
	private newsAdapter adapter;

	private int curPage=1, maxPage, pageCount = 10;
	private ArrayList<newsItem> data = new ArrayList<newsItem>();
	private JSONArray list = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_list);

		findViewById(R.id.back).setOnClickListener(this);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("网站新闻");

		initView();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		doHttp();
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back:
				finish(0);
				break;
		}
	}

	public void finish() {
		super.finish();
	}

	protected void initView() {
		mListView = (PullToRefreshListView) findViewById(R.id.listview);
		mListView.setMode(PullToRefreshBase.Mode.BOTH);
		adapter = new newsAdapter();
		mListView.setAdapter(adapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {


				Intent intent = new Intent(NoticeActivity.this,LMQWebViewActivity.class);
				intent.putExtra("title","网站新闻");
				intent.putExtra("url",Default.ip+"/Mobile/appnewale?id="+data.get(arg2 - 1).getId());
				startActivity(intent);
			}
		});


		mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				if (refreshView.isHeaderShown()) {
					doHttp();
				} else {


					if (curPage + 1 <= maxPage) {
						curPage++;
						JsonBuilder builder = new JsonBuilder();
						builder.put("limit", pageCount);
						builder.put("page", curPage);
						builder.put("type", 1);
						doHttp2(builder);
					} else {
						handler.sendEmptyMessage(1);
					}
				}


			}
		});
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
				mListView.onRefreshComplete();

			}
		}

	};

	public void doHttp2(JsonBuilder builder) {


		BaseHttpClient.post(getBaseContext(), Default.notice, builder,
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
								}else {
									String message = response.getString("message");
									SystenmApi.showCommonErrorDialog(NoticeActivity.this, response.getInt("status"),message);
								}
							} else {
								showCustomToast(R.string.toast1);
							}
							dismissLoadingDialog();
							adapter.notifyDataSetChanged();
						} catch (Exception e) {
							e.printStackTrace();
						}

                        mListView.onRefreshComplete();


					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
										  Throwable throwable, JSONObject errorResponse) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable,
								errorResponse);


						adapter.notifyDataSetChanged();
						dismissLoadingDialog();
                        mListView.onRefreshComplete();
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
						newsItem item = new newsItem(templist);
						data.add(item);

					}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter.notifyDataSetChanged();

	}





	class newsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			newsItem item = (newsItem) data.get(position);

			ViewHolderFirst viewHolderSecond = null;
			if(null == convertView){

				viewHolderSecond = new ViewHolderFirst();
				LayoutInflater mInflater = LayoutInflater.from(NoticeActivity.this);
				convertView = mInflater.inflate(
						R.layout.adapter_item_news, null);


				viewHolderSecond.type = (TextView) convertView
						.findViewById(R.id.name);
				viewHolderSecond.info = (TextView) convertView
						.findViewById(R.id.time);


				convertView.setTag(viewHolderSecond);
			}else {

				viewHolderSecond = (ViewHolderFirst) convertView.getTag();
			}

			viewHolderSecond.type.setText(item.getName());
			viewHolderSecond.info.setText(item.getTime());



			return convertView;
		}
	}

	private static class ViewHolderFirst {

		TextView type;
		TextView info;

	}

	public void doHttp() {

		if (data.size() > 0) {
			data.clear();
		}
		JsonBuilder builder = new JsonBuilder();
		builder.put("page", 1);
		builder.put("limit", pageCount);
		builder.put("type", 1);

		BaseHttpClient.post(getBaseContext(), Default.notice, builder,
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
									initData(json);
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(NoticeActivity.this, json.getInt("status"),message);
								}
							} else {
								showCustomToast(R.string.toast1);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
						dismissLoadingDialog();
                        mListView.onRefreshComplete();

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

	public void initData(JSONObject json) {

		try {
			maxPage = json.getInt("totalPage");
			curPage = json.getInt("nowPage");
			if (json.has("list") && !json.isNull("list"))
				list = json.getJSONArray("list");
			if (list != null)
				for (int i = 0; i < list.length(); i++) {
					JSONObject temp = list.getJSONObject(i);
					newsItem item = new newsItem(temp);
					data.add(item);
				}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		adapter.notifyDataSetChanged();
	}


}
