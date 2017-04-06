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
import android.widget.Toast;

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
import com.lmq.zftpay.ZFTAddBankCardActivity;
import com.money.more.activity.ControllerActivity;
import com.money.more.basil.Conts;
import com.money.more.bean.TernData;

/**
 * 银行卡列表页
 */

public class ShowBankCardInfoActivity extends BaseActivity implements
		OnClickListener {

	private PullToRefreshScrollView scrollView;
	private ListViewForScrollView listView;
	private BankCardAdapter adapter;
	private ArrayList<BankCardListlItem> data = new ArrayList<BankCardListlItem>();
	private JSONArray list = null;
	private int maxPage,curPage = 1, pageCount = 5;
	
	/**是否开启手机验证1未开启 0开启*/
	private int is_manual;
	/**是否可以修改银行卡 0未开启 1*/
	private int edit_bank;
	/**手机号*/
	private String mobile;
	/**银行卡开户名*/
	private String real_name;
	private int id;

	//bamkImageNumber  36个
	private String tag[] = {"ABC","BOC","ICBC","BCCB","SHYH","HXB","GDB","SDB","HZYHGFYHGS","ECITIC","CCB","NJYHGFYHGS"
			,"KLYHGFYHGS","GUAZYHGFYHGS","CIB","NBYHGFYHGS","XAYHGFYHGS","HKBEA","CMBC","WZYH","SZNCSYYHGFYHGS","CDYH"
			,"BJNCSYYHGFYHGS","BOCO","HKYH","XMYHGFYHGS","ZZYH","NCYH","JISYHGFYHGS","POST","TJYH","SPDB","CMB",
			"HBYHGFYHGS","CEB","CSYHGFYHGS"};

//    private int img[] = { R.drawable.bank_05, R.drawable.bank_01, R.drawable.bank_03,};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bank_id_info_layout);

		TextView text = (TextView) findViewById(R.id.title);
		text.setText(R.string.info_banckcard);

		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.add_btn).setOnClickListener(this);
		
		scrollView = (PullToRefreshScrollView) findViewById(R.id.refreshView);
		scrollView.setMode(PullToRefreshBase.Mode.BOTH);
		
		listView = (ListViewForScrollView) findViewById(R.id.bankcard_list);
		adapter = new BankCardAdapter();
		listView.setAdapter(adapter);


		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if(edit_bank==1){
					Intent intent = new Intent();
		            if(is_manual==0){

		                intent.putExtra("mobile",mobile);
		            }
					intent.putExtra("id", id);
					intent.putExtra("real_name",real_name);
					intent.putExtra("bank_id", adapter.getItem(position).getId()+"");

					intent.setClass(ShowBankCardInfoActivity.this,
							ModifyBankCardInfoAcitivity.class);
					startActivity(intent);
				}else{
					showCustomToast("禁止修改银行卡");
				}




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
								if (response.getInt("status") == 1) {
									updateAddInfo(response);
								} else {
									String message = response.getString("message");
									SystenmApi.showCommonErrorDialog(ShowBankCardInfoActivity.this, response.getInt("status"), message);
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
		case R.id.add_btn:

			if(Default.IS_ZFT){
				startActivity(new Intent(ShowBankCardInfoActivity.this, ZFTAddBankCardActivity.class));
			}else if(Default.IS_Qdd){
				doHttpAddBankCard();
			}else {
				Intent intent = new Intent();
				intent.putExtra("real_name", real_name);
				if (is_manual == 0) {

					intent.putExtra("mobile", mobile);
				}


				intent.setClass(ShowBankCardInfoActivity.this,
						AddBankCardActivity.class);

				startActivity(intent);
			}
			break;
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
							 SystenmApi.showCommonErrorDialog(ShowBankCardInfoActivity.this, json.getInt("status"),message);
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
			if (json.has("is_manual")) {

                is_manual=(json.optInt("is_manual", -1));
			}
			if (json.has("real_name")) {
				
				real_name=(json.optString("real_name", "0"));
			}

			if (json.has("edit_bank")) {
				
				edit_bank=(json.optInt("edit_bank", -1));
			}
			if (json.has("mobile")) {
				
				mobile=(json.optString("mobile", "0"));
			}

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
				LayoutInflater mInflater = LayoutInflater.from(ShowBankCardInfoActivity.this);
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
				viewHolderFirst.tv_bank_num.setText(item.getBank_num());
				id=item.getId();
//				viewHolder.normal_kind_flag.setVisibility(View.VISIBLE);

			if(Default.IS_ZFT){
				;
//				viewHolderFirst.bank_iv.setImageResource(img[getPos(item.getBank_code())]);
				viewHolderFirst.bank_iv.setBackgroundDrawable(null);
				viewHolderFirst.bank_iv.setImageResource(R.drawable.bankphoto_list_zft);
				viewHolderFirst.bank_iv.setImageLevel(getPos(item.getBank_code()));

			}else{

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


	public void doHttpAddBankCard() {
		JsonBuilder jsonBuilder = new JsonBuilder();

		BaseHttpClient.post(getBaseContext(), Default.addbank_mmm,
				jsonBuilder, new JsonHttpResponseHandler() {

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
									submitDate(json);
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(ShowBankCardInfoActivity.this, json.getInt("status"),message);
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


	public void submitDate(JSONObject json){
		try{
			// 构造三合一所需参数
			Conts.setMddPrivateKey(Default.privateKey);
			Conts.setServiceUrl(json.getString("url"));
			TernData td = new TernData();
			td.setMddid(json.getString("MoneymoremoreId"));//设置用户乾多多标识
			// 对原卡号进行RSA签名,需使用公钥
			td.setPlatformmdd(json.getString("PlatformMoneymoremore"));//设置平台用户名
			td.setAction(Integer.parseInt(json.getString("Action")));//设置操作类型 1用户认证 2绑定体现银行卡 3汇款绑卡确认
			String cardNo = "";//json.getString("CardNo");//设置银行卡号
			td.setCardno(cardNo);//设置银行卡号
			td.setRandomTime("");//设置随机时间戳
			td.setRemark1(json.getString("Remark1"));//设置备注1
			td.setRemark2(json.getString("Remark2"));//设置备注1
			td.setRemark3(json.getString("Remark3"));//设置备注1
			td.setNotifyurl(json.getString("NotifyURL"));//设置后台通知网址
			String signData = td.signData();
			td.setSignData(signData);
			// 对参数进行RSA签名
			Intent intent = new Intent(this, ControllerActivity.class);
			intent.putExtra("data", td);
			intent.putExtra("type", 6);
			startActivityForResult(intent, 600);
		}catch(Exception e){e.printStackTrace();}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		int code = data.getIntExtra("code", -1);
		if(code == 88){
			String message = "绑卡成功！";
			Toast.makeText(this, code + ":" + message, Toast.LENGTH_SHORT).show();
		}else{
			String message = data.getStringExtra("message");
			Toast.makeText(this, code + ":" + message, Toast.LENGTH_SHORT).show();
		}
	}
}
