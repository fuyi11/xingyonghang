package com.lmq.main.activity.investmanager.reward;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.lmq.main.dialog.Integration_DH_Dialog;
import com.lmq.main.item.IntegralItem;
import com.lmq.main.util.Default;
import com.lmq.view.ListViewForScrollView;

/***
 * 投资积分
 * 
 * @author sjc
 *
 */
public class RewardWithIntegralActivity extends BaseActivity implements OnClickListener {

	/** 可用投资积分 */
	private TextView tv_jf_usable;
	/** 累计投资积分 */
	private TextView tv_jf_lj;
	/** 已用投资积分 */
	private TextView tv_jf_enduse;

	/**积分记录按钮*/
	private RadioButton jfjl_btn;
	/**积分兑换按钮*/
	private RadioButton jldh_btn;
	
	private String active_integral;
	/**兑换券id*/
	private String goodid;
	
	private PullToRefreshScrollView scrollView;
	private ListViewForScrollView listView;
	private ListViewForScrollView listView2;
	
	private Integraladapter adapter;
	private Integraladapter2 adapter2;

	private ArrayList<IntegralItem> data = new ArrayList<IntegralItem>();
	private JSONArray list = null;

	private int maxPage,curPage = 1, pageCount = 5;
	private Integration_DH_Dialog dialog;


	/** 投资积分Type */
	private int type = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reward_my_tzjf_list);

		TextView text = (TextView) findViewById(R.id.title);
		text.setText(R.string.reward_tzjf);

		findViewById(R.id.back).setOnClickListener(this);

		tv_jf_usable = (TextView) findViewById(R.id.tv_jf_usable);
		tv_jf_lj = (TextView) findViewById(R.id.tv_jf_lj);
		tv_jf_enduse = (TextView) findViewById(R.id.tv_jf_enduse);

		jfjl_btn = (RadioButton) findViewById(R.id.jfjl_btn);
		jldh_btn = (RadioButton) findViewById(R.id.jldh_btn);
		
		jfjl_btn.setOnClickListener(this);
		jldh_btn.setOnClickListener(this);

		scrollView = (PullToRefreshScrollView) findViewById(R.id.refreshView);
		scrollView.setMode(PullToRefreshBase.Mode.BOTH);
		
		listView = (ListViewForScrollView) findViewById(R.id.tzjf_list);
		listView2 = (ListViewForScrollView) findViewById(R.id.tzjf_list2);
		
		adapter = new Integraladapter();
		listView.setAdapter(adapter);
		adapter2 = new Integraladapter2();
		listView2.setAdapter(adapter2);


		listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				goodid=adapter.getItem(position).getGoodid();
				if (type == 1) {
					
					showIntegrationDHDialog(view,adapter.getItem(position).getMoney(),
							adapter.getItem(position).getIntegral(),active_integral);

				}

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
						builder.put("page",curPage);
						builder.put("limit",pageCount);
				        if(type==0){
				            builder.put("code", 1);
				        }else{
				        	builder.put("code", 2);
				        }
						doHttp(builder);
					} else {
						handler.sendEmptyMessage(1);
					}
				}

			}
		});

	}
	
	public void doHttp(JsonBuilder builder) {

		BaseHttpClient.post(this, Default.integral_index, builder,
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
									SystenmApi.showCommonErrorDialog(RewardWithIntegralActivity.this, response.getInt("status"),message);
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
						IntegralItem item = new IntegralItem();
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
	
	 public void showIntegrationDHDialog(final View view,String money,String xhjf,String kyjf){

	        if(null == dialog){


	            dialog = new Integration_DH_Dialog(RewardWithIntegralActivity.this);
	            dialog.setDialogTitle("立即兑换");
	            dialog.setonClickListener(this);



	        }

	        dialog.setInfo(money,xhjf,kyjf);

	        if(!dialog.isShowing()){
	            dialog.showAsDropDown(view);
	        }



	    }

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.back:
			finish(0);
			break;
		case R.id.jfjl_btn:
			type = 0;
			data.clear();
			adapter2.notifyDataSetChanged();
			listView2.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			adapter.notifyDataSetChanged();
			getListDataFromSever(type);
			break;
		case R.id.jldh_btn:
			type = 1;
			data.clear();
			adapter.notifyDataSetChanged();
			listView.setVisibility(View.GONE);
			listView2.setVisibility(View.VISIBLE);
			adapter2.notifyDataSetChanged();
			getListDataFromSever(type);
			break;
		case R.id.dialog_submit:

            if(SystenmApi.isNullOrBlank(dialog.getDh_number())){
                showCustomToast("请输入兑换数量");
                return;
            }
            
            getListAjaxcredit();
            dialog.dismiss();

            
            break;
        case R.id.dialog_cancle:

            dialog.dismiss();

            break;

		}
	}

	private void getListAjaxcredit() {

		JsonBuilder builder = new JsonBuilder();
		builder.put("amount",dialog.getDh_number());
		builder.put("goodid",goodid);
		
		BaseHttpClient.post(getBaseContext(), Default.ajaxcredit, builder, new JsonHttpResponseHandler() {

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


                             MyLog.e("获取积分兑换信息", "" + json.toString());
                             dialog.dismiss();

                         } else {
							 String message = json.getString("message");
							 SystenmApi.showCommonErrorDialog(RewardWithIntegralActivity.this, json.getInt("status"),message);
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
		JsonBuilder builder = new JsonBuilder();
		builder.put("page",0);
		builder.put("limit",5);
        if(type==0){
            builder.put("code", 1);
        }else{
        	builder.put("code", 2);
        }
		
		BaseHttpClient.post(getBaseContext(), Default.integral_index, builder, new JsonHttpResponseHandler() {

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


                             MyLog.e("获取投资积分信息", "" + json.toString());

                             initData(json);
                         }else {
							 String message = json.getString("message");
							 SystenmApi.showCommonErrorDialog(RewardWithIntegralActivity.this, json.getInt("status"),message);
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
			if(type==0){
			 maxPage = json.getInt("totalPage");
			 curPage = json.getInt("nowPage");
			}else{
				
			}
			if (json.has("active_integral")) {

				active_integral=(json.optString("active_integral", "0"));
				tv_jf_usable.setText(active_integral);
			}

			if (json.has("integral")) {

				tv_jf_lj.setText(json.optString("integral", "0"));
			}

			if (json.has("integral_use")) {

				tv_jf_enduse.setText(json.optString("integral_use", "0"));
			}
			

			if (!json.isNull("list")) {
				list = json.getJSONArray("list");

				if (list != null)
					for (int i = 0; i < list.length(); i++) {
						JSONObject templist = list.getJSONObject(i);
						IntegralItem item = new IntegralItem();
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

	class Integraladapter extends BaseAdapter {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public IntegralItem getItem(int position) {
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
			IntegralItem item = (IntegralItem) data.get(position);

			ViewHolderFirst viewHolderFirst;
			if(convertView == null){
				LayoutInflater mInflater = LayoutInflater.from(RewardWithIntegralActivity.this);
				convertView = mInflater.inflate(
						R.layout.reward_adapter_item_integration_jl, null);
					viewHolderFirst = new ViewHolderFirst();

					viewHolderFirst.tvjf = (TextView) convertView
							.findViewById(R.id.tv);
					viewHolderFirst.tv_rewardtzjf_type = (TextView) convertView
							.findViewById(R.id.tv_rewardtzjf_type);
					viewHolderFirst.tv_rewardtzjf_time = (TextView) convertView
							.findViewById(R.id.tv_rewardtzjf_time);
					viewHolderFirst.tv_rewardtzjf_detail = (TextView) convertView.
							findViewById(R.id.tv_rewardtzjf_detail);

					convertView.setTag(viewHolderFirst);



				}else {

				viewHolderFirst = (ViewHolderFirst) convertView.getTag();
			}

				viewHolderFirst.tvjf.setText(item.getIntegral_log());
				viewHolderFirst.tv_rewardtzjf_type.setText(item.getAffect_integral());
				viewHolderFirst.tv_rewardtzjf_time.setText(item.getAdd_time());
				viewHolderFirst.tv_rewardtzjf_detail.setText(item.getInfo());


			return convertView;
		}
	}
	
	class Integraladapter2 extends BaseAdapter {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public IntegralItem getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyLog.e("123","2--------------");
			IntegralItem item = (IntegralItem) data.get(position);

			ViewHolderSecond viewHolderSecond = null;
			if(null == convertView){

				viewHolderSecond = new ViewHolderSecond();
				LayoutInflater mInflater = LayoutInflater.from(RewardWithIntegralActivity.this);
				convertView = mInflater.inflate(
						R.layout.reward_adapter_item_integration_dh, null);

				viewHolderSecond. tv_money = (TextView) convertView
						.findViewById(R.id.tv_money);
				viewHolderSecond. tv_rewardtzjf_detail = (TextView) convertView
						.findViewById(R.id.tv_rewardtzjf_detail);
				viewHolderSecond. tv_rewardtzjf = (TextView) convertView
						.findViewById(R.id.tv_rewardtzjf);


				convertView.setTag(viewHolderSecond);
			}else {

				viewHolderSecond = (ViewHolderSecond) convertView.getTag();
			}

				viewHolderSecond.tv_money.setText("￥" + item.getMoney() + "元");
				viewHolderSecond.tv_rewardtzjf_detail.setText(item.getInfo());
				viewHolderSecond.tv_rewardtzjf.setText(item.getIntegral());



			return convertView;
		}
	}
	
	private static class ViewHolderFirst {

		TextView tvjf;
		TextView tv_rewardtzjf_type;
		TextView tv_rewardtzjf_time;
		TextView tv_rewardtzjf_detail;
	}

	private static class ViewHolderSecond{

		TextView tv_money;
		TextView tv_rewardtzjf_detail ;
		TextView tv_rewardtzjf ;


	}


	public void finish() {
		super.finish();
	}

}
