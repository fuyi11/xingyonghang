package com.lmq.main.activity.user.manager.bankinfo;

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
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.item.BankCardListlItem;
import com.lmq.main.util.Default;
import com.lmq.view.ListViewForScrollView;

/**
 * 选择银行卡
 */

public class ChoiceBankListActivity extends BaseActivity implements
		OnClickListener {

	private PullToRefreshScrollView scrollView;
	private ListViewForScrollView listView;
	private BankCardAdapter adapter;
	private ArrayList<BankCardListlItem> data = new ArrayList<BankCardListlItem>();
	private JSONArray list = null;
	private int maxPage,curPage = 1, pageCount = 5;
	
	/**银行卡开户名*/
	private String real_name;
	private int id;

	private String tag[] = {"ABC","BOC","ICBC","BCCB","SHYH","HXB","GDB","SDB","HZYHGFYHGS","ECITIC","CCB","NJYHGFYHGS"
			,"KLYHGFYHGS","GUAZYHGFYHGS","NBYHGFYHGS","XAYHGFYHGS","XAYHGFYHGS","CMBC","WZYH","SZNCSYYHGFYHGS","CDYH"
			,"BJNCSYYHGFYHGS","BOCO","HKYH","XMYHGFYHGS","ZZYH","NCYH","JISYHGFYHGS","POST","TJYH","SPDB","CMBCHINA",
			"HBYHGFYHGS","CEB","CSYHGFYHGS"};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choice_bank_layout);

		TextView text = (TextView) findViewById(R.id.title);
		text.setText("选择银行卡");

		findViewById(R.id.back).setOnClickListener(this);
		
		scrollView = (PullToRefreshScrollView) findViewById(R.id.refreshView);
		scrollView.setMode(PullToRefreshBase.Mode.BOTH);
		
		listView = (ListViewForScrollView) findViewById(R.id.bankcard_list);
		adapter = new BankCardAdapter();
		listView.setAdapter(adapter);

		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent();
				 intent.putExtra("bank_name", adapter.getItem(position).getBank_name());
				 intent.putExtra("bank_num", adapter.getItem(position).getBank_num());
				 intent.putExtra("bank_id", adapter.getItem(position).getBank_id());
				 intent.putExtra("id", adapter.getItem(position).getId());

				MyLog.e("选择银行卡的id为：",adapter.getItem(position).getBank_id()+"");
				intent.putExtra("is_idcard", 1);

				 setResult(Default.RESULT_BANKCARD,intent);
                 finish();

                

			}
		});
		
		scrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {

				if (refreshView.isHeaderShown()) {

					doHttpBankCardList();
				} else {
					JsonBuilder builder = new JsonBuilder();
					if (curPage + 1 <= maxPage) {
						curPage++;
						doHttp(builder);
					} else {
						handler.sendEmptyMessage(1);
					}
				}

			}
		});

	}
	
	public void doHttp(JsonBuilder builder) {

		BaseHttpClient.post(this, Default.bank_index, builder,
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
								if (response.getInt("status") == 1){
									updateAddInfo(response);

								}else {
									String message = response.getString("message");
									SystenmApi.showCommonErrorDialog(ChoiceBankListActivity.this, response.getInt("status"),message);
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

//			maxPage = json.getInt("totalPage");
			list = null;
			if (!json.isNull("list")) {
				list = json.getJSONArray("list");

				if (list != null)
					for (int i = 0; i < list.length(); i++) {
						JSONObject templist = list.getJSONObject(i);
						BankCardListlItem item = new BankCardListlItem();
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
	
	protected void onResume() {
		super.onResume();

		doHttpBankCardList();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			
			finish();
			break;
		}

	}

	public void doHttpBankCardList() {
		/***
		 * 清除原有数据
		 */
		if (data.size() > 0) {
			data.clear();
		}

		BaseHttpClient.post(getBaseContext(), Default.bank_index, null, new JsonHttpResponseHandler() {

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


                             MyLog.e("获取银行卡列表信息", "" + json.toString());

                             initData(json);
                         } else {
							 String message = json.getString("message");
							 SystenmApi.showCommonErrorDialog(ChoiceBankListActivity.this, json.getInt("status"),message);
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

		try {

			if (!json.isNull("list")) {
				list = json.getJSONArray("list");

				if (list != null)
					for (int i = 0; i < list.length(); i++) {
						JSONObject templist = list.getJSONObject(i);
						BankCardListlItem item = new BankCardListlItem();
						item.init(templist);
						data.add(item);
					}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		  adapter.notifyDataSetChanged();

		scrollView.smootScrollToTop();
	}
	
	class BankCardAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public BankCardListlItem getItem(int position) {
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
			BankCardListlItem item = (BankCardListlItem) data.get(position);

			ViewHolderFirst viewHolderFirst;
			if(convertView == null){
				LayoutInflater mInflater = LayoutInflater.from(ChoiceBankListActivity.this);
				convertView = mInflater.inflate(
						R.layout.bank_id_info_item, null);
					viewHolderFirst = new ViewHolderFirst();

					viewHolderFirst.tv_bank_name = (TextView) convertView
							.findViewById(R.id.tv_bank_name);
					viewHolderFirst.tv_bank_num = (TextView) convertView
							.findViewById(R.id.tv_bank_num);
					viewHolderFirst.bank_iv = (ImageView) convertView
							.findViewById(R.id.bank_iv);

					convertView.setTag(viewHolderFirst);

				}else {

				viewHolderFirst = (ViewHolderFirst) convertView.getTag();
			}

				viewHolderFirst.tv_bank_name.setText(item.getBank_name());
				viewHolderFirst.tv_bank_num.setText("尾号"+item.getBank_num());
				id=item.getId();
//				viewHolder.normal_kind_flag.setVisibility(View.VISIBLE);
			if (Default.IS_ZFT){
				viewHolderFirst.bank_iv.setBackgroundDrawable(null);
				viewHolderFirst.bank_iv.setImageResource(R.drawable.bankphoto_list_zft);
				viewHolderFirst.bank_iv.setImageLevel(getPos(item.getBank_code()));

			}else {

				viewHolderFirst.bank_iv.setBackgroundDrawable(null);
				viewHolderFirst.bank_iv.setImageResource(R.drawable.bankphoto_list);
				viewHolderFirst.bank_iv.setImageLevel(item.getBank_id());

			}
			return convertView;
		}
	}

	private static class ViewHolderFirst {

		TextView tv_bank_name;
		TextView tv_bank_num;
		ImageView bank_iv;
	}

	private  int getPos(String code){
		int pos = -1;
		for(int i = 0;i<tag.length;i++){
			if(code.equals(tag[i]))
				pos = i;
		}
		return pos;
	}

}
