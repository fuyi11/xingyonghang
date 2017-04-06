package com.lmq.menu.activity;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.invest.investdetail.InvestDetailType101;
import com.lmq.main.activity.invest.investdetail.InvestDetailType11;
import com.lmq.main.activity.invest.investdetail.InvestDetailType201;
import com.lmq.main.activity.invest.investdetail.InvestDetailType301;
import com.lmq.main.adapter.TzAdapter;
import com.lmq.main.api.BaseFragment;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.item.tzItem;
import com.lmq.main.util.Default;

public class HomeItemFragment extends BaseFragment {
	private View parentView;
	private PullToRefreshListView mListView;
	private TzAdapter adapter;

//	private int refresh = 0;

	public ArrayList<tzItem> ListInfo = new ArrayList<tzItem>();
	// 散标
	private JSONArray list = null;

	// 用户查看标种类 Flag 0:散标 1：定投宝 2：企业直投 3：债权转让
	public int swith_flag = 0;

	private int maxPage, curPage = 1, pageCount = 9;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.layout_home, container, false);
		parentView.findViewById(R.id.swith_head).setVisibility(View.GONE);
		Bundle mBundle = getArguments();
		swith_flag = mBundle.getInt("switch_flag");
		initView(parentView);
		return parentView;
	}

	public void onInflate(Activity activity, AttributeSet attrs,
			Bundle savedInstanceState) {
		super.onInflate(activity, attrs, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	protected void initView(View mainView) {
		// mChose = (Button) mainView.findViewById(R.id.chose);
		mListView = (PullToRefreshListView) mainView
				.findViewById(R.id.listview);

		// swith_flag = 0;

		initInfo(mainView);
	}

	public void initInfo(View mainView) {

		adapter = new TzAdapter(getActivity());
		mListView.setAdapter(adapter);
		if (swith_flag == 1) {
			adapter.IsSpicalView(true);
		}

		mListView.setMode(Mode.BOTH);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				tzItem item = (tzItem) ListInfo.get(position - 1);
				if (swith_flag == 1) {
					// 显示操作
					if (item.getUid() == Default.userId) {
						showCustomToast("不能购买自己发布的债权标");
					} else {
							Intent intent_zq = new Intent();
							intent_zq.setClass(getActivity(), InvestDetailType101.class);
							intent_zq.putExtra("id", item.getId() + "");
							intent_zq.putExtra("type", item.getType());
							startActivity(intent_zq);

					}

				} else {
					showItem(item);
				}

			}
		});

		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub

				if (refreshView.isHeaderShown()) {

					doHttp();
				} else {
					JsonBuilder builder = new JsonBuilder();
					if (curPage + 1 <= maxPage) {
						curPage += 1;
						builder.put("type", getType());
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

	public void showItem(tzItem item) {

		long id = item.getId();
		
		final int type = item.getType();

		
		Intent intent = new Intent();
		if(type == 7){
			intent.setClass(getActivity(),InvestDetailType201.class);
			
		}else if(type == 6){
			intent.setClass(getActivity(),InvestDetailType301.class);
			
		}else{
			intent.setClass(getActivity(),InvestDetailType11.class);
			
		}
		
		intent.putExtra("id", id+"");
		intent.putExtra("type", type);

		MyLog.e("上传的type====",type+"");
		startActivity(intent);
		
		getActivity().overridePendingTransition(R.anim.right_to_left,
				R.anim.to_left);
	}

	public void doHttp(JsonBuilder builder) {

		BaseHttpClient.NO_RAS=true;
		BaseHttpClient.post(getActivity(), Default.tzList, builder,
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
									SystenmApi.showCommonErrorDialog(getActivity(), response.getInt("status"), message);

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

						mListView.onRefreshComplete();
						adapter.notifyDataSetChanged();
						dismissLoadingDialog();
					}
				});
	}

	public void updateAddInfo(JSONObject json) {
		ArrayList<tzItem> temp_list = new ArrayList<tzItem>();
		try {

			// 初始化 散标数据

			maxPage = json.getInt("totalPage");
			list = null;

			if (!json.isNull("list")) {
				list = json.getJSONArray("list");

				if (list != null) {
					for (int i = 0; i < list.length(); i++) {
						JSONObject templist = list.getJSONObject(i);
						tzItem item = new tzItem();
						item.init(templist);
						temp_list.add(item);

					}


				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter.setDataSource(temp_list, true);
		ListInfo.addAll(temp_list);
		adapter.notifyDataSetChanged();

		mListView.onRefreshComplete();
	}

	public void doHttp() {

		JsonBuilder builder = new JsonBuilder();

		try {

			curPage = 1;

			builder.put("type", getType() + "");
			builder.put("page", curPage + "");
			builder.put("limit", pageCount + "");
			builder.put("timestamp", System.currentTimeMillis() + "");

		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		BaseHttpClient.NO_RAS=true;
		// 填充参数
		BaseHttpClient.post(getActivity(), Default.tzList, builder,
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						super.onStart();

						showLoadingDialogNoCancle("请稍后努力加载中...");
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject json) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, json);
						dismissLoadingDialog();
						MyLog.e("statusCode=" + statusCode + "--headers="
								+ headers.toString() + "--json="
								+ json.toString());

						if (statusCode == 200) {
							try {
								if (json.getInt("status") == 1)
									initData(json);
								else {
									showCustomToast(json.getString("message"));
									ListInfo.clear();
								}

								dismissLoadingDialog();

								adapter.notifyDataSetChanged();
								mListView.onRefreshComplete();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable,
								errorResponse);

						adapter.notifyDataSetChanged();

						mListView.onRefreshComplete();
						adapter.notifyDataSetChanged();
						dismissLoadingDialog();
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

	@Override
	public void onResume() {
		super.onResume();
//		if (refresh==0){
			doHttp();
//			refresh=1;
//		}
	}

	private int beginIndex, endIndex;

	public void initData(JSONObject json) {
		ListInfo.clear();

		try {
			// 散标数据初始化

			maxPage = json.getInt("totalPage");
			curPage = json.getInt("nowPage");
			list = null;
			ArrayList<tzItem> temp_list = new ArrayList<tzItem>();
			if (!json.isNull("list")) {
				list = json.getJSONArray("list");

				if (list != null)
					for (int i = 0; i < list.length(); i++) {
						JSONObject templist = list.getJSONObject(i);
						tzItem item = new tzItem();
						item.init(templist);
						temp_list.add(item);
					}
				ListInfo.clear();
				ListInfo.addAll(temp_list);
				// 将数据全部填充到 ListInfo中
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter.setDataSource(ListInfo, false);
		adapter.notifyDataSetChanged();

	}

	public int getType() {
		switch (swith_flag) {
		case 0: // 普通标
			return 301;
//		caseß
//		case 2:// 企业直投
//			return 301;
//		case 3:// 债权
//			return 101;
		default:
			return 301;
		}
	}











}