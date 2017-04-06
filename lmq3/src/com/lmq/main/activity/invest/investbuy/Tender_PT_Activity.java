package com.lmq.main.activity.invest.investbuy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.user.manager.password.ChangeTenderPasswordActivity;
import com.lmq.main.activity.investmanager.reward.RewardWithCouponActivity;
import com.lmq.main.activity.user.manager.pay.ThirdPayActivity;
import com.lmq.main.activity.user.manager.phone.RevisePhoneActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.dialog.CommonDialog;
import com.lmq.main.item.TB_YHQ_Item;
import com.lmq.main.util.Default;
import com.lmq.ybpay.YBPayActivity;
import com.money.more.activity.ControllerActivity;
import com.money.more.basil.Conts;
import com.money.more.bean.TransferData;
import com.money.more.utils.StringUtil;

public class Tender_PT_Activity extends BaseActivity implements
		android.view.View.OnClickListener {

	/**支付密码*/
	private EditText mEditPassword;
	/**投资金额*/
	private EditText mEditNum;
	/**账户余额*/
	private TextView tv_account_money;
	/**预计收益*/
	private TextView tv_yjsy;
	/**起投金额*/
	private TextView tv_borrow_min;
	/**限投金额*/
	private TextView tv_borrow_max;
	/**无限制"元"*/
	private TextView xz_yuan;
	/**可减金额*/
	private TextView tv_money;
	/**定向标密码*/
	private TextView dxTextView;
	/**定向标密码*/
	private EditText dxpsswdEdit;

	/**定向标Flag**/
	private boolean mShowDx;

	private String id;
	private int type;
	private JSONArray list = null;
	private ArrayList<TB_YHQ_Item> data = new ArrayList<TB_YHQ_Item>();


	/**优惠券ID*/
	private int expand_id;
	/**预计收益*/
	private String amount;
	private ListView ListView;
	private TenderAdapter adapter;
	private itemInfo2Handler handler;
	private String messageInfo;

	/**计息方式（定投包才有）按月还本息还是利息复投*/
	private RadioGroup tzPayKindGroup;

	private JSONArray LoanJsonList = new JSONArray();



	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.tender_layout);

		if(Default.IS_YB){
			findViewById(R.id.lv_pay_password).setVisibility(View.GONE);
		}

		if (Default.IS_Qdd){

			findViewById(R.id.lv_pay_password).setVisibility(View.GONE);

		}


		handler = new itemInfo2Handler();
		Intent intent = getIntent();
		if (intent.hasExtra("id")) {
			id = intent.getStringExtra("id");
		}
		if (intent.hasExtra("type")) {
			type = intent.getIntExtra("type",1);
		}

		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		doHttpBuy();
	}

	public void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("立即投标");

		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.enter_money).setOnClickListener(this);

		ListView = (ListView) findViewById(R.id.listview);
		adapter = new TenderAdapter();
		ListView.setAdapter(adapter);

		tv_account_money = (TextView) findViewById(R.id.tv_account_money);
		tv_yjsy = (TextView) findViewById(R.id.tv_yjsy);
		tv_borrow_min = (TextView) findViewById(R.id.tv_borrow_min);
		tv_borrow_max = (TextView) findViewById(R.id.tv_borrow_max);
		tv_money = (TextView) findViewById(R.id.tv_money);
		xz_yuan = (TextView) findViewById(R.id.xz_yuan);


		mEditNum = (EditText) findViewById(R.id.ed_money);
		mEditPassword = (EditText) findViewById(R.id.ed_pin);

		dxTextView = (TextView) findViewById(R.id.tv_dxb);
		dxpsswdEdit = (EditText) findViewById(R.id.ed_borrow_pass);


		findViewById(R.id.lv).setOnClickListener(this);
		findViewById(R.id.rl_yhq).setOnClickListener(this);

		ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				//判断是否选中的是同一个ITEM
				if(adapter.getCurrentselect() == position){

					adapter.setCurrentselect(-1);
					adapter.notifyDataSetChanged();
					tv_money.setText("0");

				}else{


					tv_money.setText("0");
					//对优惠券点击进行判断
					String userInputMonyStr = mEditNum.getText().toString().equals("") ? "0" : mEditNum.getText().toString();

					double userInputMoney = Double.parseDouble(userInputMonyStr);

					if (userInputMoney == 0) {
						showCustomToast("请输入投标金额");
					} else {
						//判断用户输入 是否更新选中
						TB_YHQ_Item item = adapter.getItem(position);
						double invest_money = Double.parseDouble(item.getInvest_money());

						if (userInputMoney < invest_money) {

							showCustomToast("此优惠券不可用,请选择其他优惠券");
						} else {
							adapter.setCurrentselect(position);
							adapter.notifyDataSetChanged();

							expand_id = adapter.getItem(position).getId();
							tv_money.setText("" + adapter.getItem(position).getMoney());
						}
					}
				}
			}
		});

		tzPayKindGroup = (RadioGroup) findViewById(R.id.tz_pay_kind);
		tzPayKindGroup.setVisibility(View.GONE);

		mEditNum.addTextChangedListener(textWatcher);

	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
								  int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
									  int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			String temp =mEditNum.getText().toString();
			if(SystenmApi.isNullOrBlank(temp)){
				temp = "0";
			}
			double lv = Double.parseDouble(temp);

			quickcountrate();
			adapter.notifyDataSetChanged();

			//如果用户已经选择优惠券  则根据数据 自动判断
			if(adapter.getCurrentselect() != -1){


				double current_Imoney = Double.parseDouble(adapter.getItem(adapter.getCurrentselect()).getInvest_money());

				if(lv>current_Imoney){
					tv_money.setText(adapter.getItem(adapter.getCurrentselect()).getMoney());
				}else{

					adapter.setCurrentselect(-1);
					adapter.notifyDataSetChanged();
					tv_money.setText("0");
				}
			}
		}
	};




	class TenderAdapter extends BaseAdapter {


		private int currentselect = -1;

		public int getCurrentselect() {
			return currentselect;
		}

		public void setCurrentselect(int currentselect) {
			this.currentselect = currentselect;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public TB_YHQ_Item getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				LayoutInflater mInflater = LayoutInflater.from(Tender_PT_Activity.this);
				convertView = mInflater.inflate(
						R.layout.tender_item, null);
			}

			TextView info = (TextView) convertView
					.findViewById(R.id.info);
			CheckBox select = (CheckBox) convertView.findViewById(R.id.select);

			// 获取当前positon 中包含的信息
			TB_YHQ_Item item = (TB_YHQ_Item) data.get(position);
			info.setText(item.getDesc());
			String userInputMonyStr = mEditNum.getText().toString().equals("")?"0":mEditNum.getText().toString();
			double userInputMoney = Double.parseDouble(userInputMonyStr);
			double invest_money = Double.parseDouble(item.getInvest_money());

			if(currentselect == position && userInputMoney >= invest_money){
				select.setChecked(true);
			}else{
				select.setChecked(false);
			}
			return convertView;
		}
	}

	public void updateInfo(JSONObject json) {
		try {
			mShowDx = json.getInt("password") == 1;
			if (json.has("account_money")) {
				tv_account_money.setText(json.optString("account_money", "0"));
			}

			if (json.has("borrow_min")) {
				tv_borrow_min.setText(json.optString("borrow_min", "0"));
			}
			if (json.has("borrow_max")) {
				if(json.optString("borrow_max").equals("无限制")){
					tv_borrow_max.setText("无限制");
					xz_yuan.setVisibility(View.GONE);
				}else{
					tv_borrow_max.setText(json.optString("borrow_max"));
				}
			}
			if (!json.isNull("expand_money_list")) {
				list = json.getJSONArray("expand_money_list");

				if (list != null)
					for (int i = 0; i < 3; i++) {//list.length()
						JSONObject templist = list.getJSONObject(i);
						TB_YHQ_Item item = new TB_YHQ_Item();
						item.init(templist);
						data.add(item);
					}
			}else{
				ListView.setVisibility(View.GONE);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (mShowDx) {
			dxpsswdEdit.setVisibility(View.VISIBLE);
			dxTextView.setVisibility(View.VISIBLE);
		}else{
			dxpsswdEdit.setVisibility(View.GONE);
			dxTextView.setVisibility(View.GONE);
		}
		adapter.notifyDataSetChanged();
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.rl_yhq:
				startActivity(
						new Intent(Tender_PT_Activity.this, RewardWithCouponActivity.class));
				break;
			case R.id.back:
				finish();
				break;
			case R.id.enter_money:
				if (SystenmApi.isNullOrBlank(mEditNum.getText().toString())) {
					showCustomToast("请输入投资金额");
					return ;
				}

				if(!Default.IS_YB&&!Default.IS_Qdd){

					if (SystenmApi.isNullOrBlank(mEditPassword.getText().toString())) {
						showCustomToast("请输入支付密码");
						return ;
					}

				}
				dohttpCheck();
				break;
		}
	}

	/**
	 * 计算金额
	 * **/
	public void quickcountrate() {

		JsonBuilder builder = new JsonBuilder();
		builder.put("id", id);
		builder.put("amount", mEditNum.getText().toString());

		BaseHttpClient.post(getBaseContext(), Default.quickcountrate, builder,
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
										  JSONObject json) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, json);
						try {

							if (statusCode == 200) {
								if (json.getInt("status") == 1) {
									if (json.has("amount")) {
										amount = json.optString("amount", "0");
									}
									tv_yjsy.setText(amount);
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(Tender_PT_Activity.this, json.getInt("status"),message);
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



	public void showCheckDialog() {

		CommonDialog.Builder ibuilder  = new CommonDialog.Builder(Tender_PT_Activity.this);
		ibuilder.setTitle(R.string.friendly);
		ibuilder.setMessage(messageInfo);
		ibuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				Message message = new Message();
				message.arg1 = 2;
				handler.sendMessage(message);

			}
		});

		ibuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		ibuilder.create().show();
	}

	public void goMoneyDialog() {

		CommonDialog.Builder ibuilder  = new CommonDialog.Builder(Tender_PT_Activity.this);
		ibuilder.setTitle(R.string.friendly);
		ibuilder.setMessage(messageInfo);
		ibuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				finish();
				startActivity(
						new Intent(Tender_PT_Activity.this, ThirdPayActivity.class));
			}
		});

		ibuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		ibuilder.create().show();



	}

	public void doHttpBuy() {

		if (data.size() > 0) {
			data.clear();
		}

		JsonBuilder builder = new JsonBuilder();
		builder.put("id", id);

		BaseHttpClient.post(getBaseContext(), Default.tzListItem2, builder,
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
									dismissLoadingDialog();
									updateInfo(json);
								} else if(json.has("is_jumpmsg")){
									String message = json.getString("is_jumpmsg");
									SystenmApi.showCommonErrorDialog(Tender_PT_Activity.this, json.getInt("status"), message);
								} else {
									showCustomToast(json.getString("message"));
									finish();
								}
							} else {
								showCustomToast(R.string.toast1);
								finish();
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
	public void showCheckDialog(String messageInfo) {

		CommonDialog.Builder ibuilder  = new CommonDialog.Builder(Tender_PT_Activity.this);
		ibuilder.setTitle(R.string.friendly);
		ibuilder.setMessage(messageInfo);
		ibuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {


				startActivity(
						new Intent(Tender_PT_Activity.this, ChangeTenderPasswordActivity.class));
				dialog.dismiss();

			}
		});

		ibuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		});
		ibuilder.create().show();


	}

	public void dohttpCheck() {

		JsonBuilder builder = new JsonBuilder();

		if (Default.IS_Qdd && Default.IS_YB){
			builder.put("borrow_id", id);
			builder.put("money", mEditNum.getText().toString());
		}else{

			builder.put("borrow_id", id);
			builder.put("pin", mEditPassword.getText().toString());
			builder.put("money", mEditNum.getText().toString());
		}

		if (mShowDx) {
			builder.put("borrow_pass", dxpsswdEdit.getText().toString());
		}
		BaseHttpClient.post(getBaseContext(), Default.tzListItem3, builder,
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
									messageInfo = json.getString("message");
									Message message = new Message();
									message.arg1 = 1;
									handler.sendMessage(message);
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(Tender_PT_Activity.this, json.getInt("status"),message);
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

	private void doHttpMoney() {
		JsonBuilder builder = new JsonBuilder();
		builder.put("borrow_id", id);
		builder.put("coupon_id", adapter.getCurrentselect() == -1 ? "" : adapter.getItem(adapter.getCurrentselect()).getId()+"");
		builder.put("pin", mEditPassword.getText().toString());
		builder.put("money", mEditNum.getText().toString());
		if (mShowDx)
			builder.put("borrow_pass", dxpsswdEdit.getText().toString());

//		BaseHttpClient.NO_RAS=true;

		BaseHttpClient.post(getBaseContext(), Default.tzListItem4, builder,
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
									/**
									 messageInfo = json.getString("message");
									 Message message = new Message();
									 message.arg1 = 3;
									 handler.sendMessage(message);
									 */


									submitTransterData(json);//本地签名


								}else if(json.has("is_jumpmsg")){
									String message = json.getString("is_jumpmsg");
									SystenmApi.showCommonErrorDialog(Tender_PT_Activity.this, json.getInt("status"), message);
								}else {
									showCustomToast(json.getString("message"));
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


	/**
	 * =========================使用本地签名
	 * @param json
	 * @throws JSONException
	 */
	private void submitTransterData(JSONObject json) throws JSONException {

		TransferData tfd = new TransferData();


		Conts.setServiceUrl(json.optString("url"));
		Conts.setMddPrivateKey(Default.privateKey);


		JSONArray array = json.getJSONArray("LoanJsonList");
		JSONObject object = array.getJSONObject(0);



		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> map1 = new HashMap<String, String>();

		map1.put("LoanOutMoneymoremore", object.getString("LoanOutMoneymoremore"));
		map1.put("LoanInMoneymoremore", object.getString("LoanInMoneymoremore"));
		map1.put("OrderNo", object.getString("OrderNo"));
		map1.put("BatchNo", object.getString("BatchNo"));
		map1.put("Amount", object.getString("Amount"));
		map1.put("TransferName",object.getString("TransferName"));
		map1.put("AdvanceBatchNo", object.getString("AdvanceBatchNo"));
		map1.put("Remark", object.getString("Remark"));
		map1.put("FullAmount", object.getString("FullAmount"));

		if (object.getString("SecondaryJsonList").equals("")){

			map1.put("SecondaryJsonList","");


		}else {

			//二次配配列表判断
			if (SystenmApi.isNullOrBlank(object.getString("SecondaryJsonList"))) {

				map1.put("SecondaryJsonList", "");

			}else {

				JSONArray array1 = object.getJSONArray("SecondaryJsonList");

				//添加二次分配列表
				List<Map<String,String>> secondslist = new ArrayList<Map<String, String>>();
				for(int i=0;i<array1.length();i++){
					JSONObject object1 = array1.getJSONObject(i);
					//二次分配列表1
					Map<String,String> smap = new HashMap<String, String>();
					smap.put("Remark",object1.getString("Remark"));
					smap.put("TransferName",object1.getString("TransferName"));
					smap.put("Amount",object1.getString("Amount"));
					smap.put("LoanInMoneymoremore",object1.getString("LoanInMoneymoremore"));

					secondslist.add(smap);
				}

				String secondaryJsonList = new Gson().toJson(secondslist);

				map1.put("SecondaryJsonList",secondaryJsonList);
			}


		}


		list.add(map1);

		String loanJsonlist = new Gson().toJson(list);


		tfd.setLoanJsonList(loanJsonlist);
		tfd.setPlatformdd(json.getString("PlatformMoneymoremore"));
		tfd.setTransferAction(json.getInt("TransferAction"));
		tfd.setAction(json.getInt("Action"));
		tfd.setTransferType(json.getInt("TransferType"));
		tfd.setNeedAudit(json.getString("NeedAudit"));
		tfd.setRemark1(json.getString("Remark1"));
		tfd.setNotifyurl(json.getString("NotifyURL"));

		tfd.setSignData(tfd.signData());


		Intent intent = new Intent(this, ControllerActivity.class);
		//设置操作类型为4，表示转账
		intent.putExtra("type", 4);
		intent.putExtra("data", tfd);
		startActivityForResult(intent, 400);

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			if (data != null) {
				String plat = data.getStringExtra("plat");
				if (!StringUtil.isEmpty(plat)) {

					showCustomToast(plat);
				}

				MyLog.e("URL===="+Conts.getServiceUrl());
			}
		} else {
			StringBuilder sb = new StringBuilder("");
			int code = data.getIntExtra("code", 0);
			String message = data.getStringExtra("message");
			if (code != 88) {
				// 成功
				sb.append("操作结果标识:").append(code).append("操作结果:")
						.append(message);
				Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
			}else{
				sb.append("投标成功！");
				Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
				finish();
			}
		}

	}

	public void showMoneyDialog() {
		CommonDialog.Builder ibuilder  = new CommonDialog.Builder(Tender_PT_Activity.this);
		ibuilder.setTitle(R.string.friendly);
		ibuilder.setMessage(messageInfo);
		ibuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});

		ibuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		ibuilder.create().show();
	}



	class itemInfo2Handler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
				case 1:
					showCheckDialog();
					break;
				case 2:
					if(Default.IS_YB){
						Intent intent = new Intent(Tender_PT_Activity.this, YBPayActivity.class);
						intent.putExtra("YB_TYPE", 4);
						intent.putExtra("borrow_id", id);
						intent.putExtra("money", mEditNum.getText().toString());
						intent.putExtra("coupon_id", adapter.getCurrentselect() == -1 ? "" : adapter.getItem(adapter.getCurrentselect()).getId()+"");
						intent.putExtra("type", adapter.getCurrentselect() == -1 ? "" : adapter.getItem(adapter.getCurrentselect()).getType()+"");
						if (mShowDx){
							intent.putExtra("borrow_pass", dxpsswdEdit.getText().toString());
						}
						startActivity(intent);
						finish();
					}else{
						doHttpMoney();
					}
					break;
				case 3:
					showMoneyDialog();
					break;
				case 4:
					showCustomToast(msg.getData().getString("info"));
					break;
				case 5:
					goMoneyDialog();
					break;
			}
		}
	}
}