package com.lmq.zftpay;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPopupWindow;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;

import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;
import com.lmq.http.JsonHttpResponseHandler;

/**
 * 支付通充值
 * 
 */

public class ZFTRechargeActivity extends BaseActivity implements
		OnClickListener {

	private String authToken;
	private String bank_code;
	private String bank_name;
	private String userName;
	private String phone;
	private String loan_on;

	private TextView tv_real_name;
	private TextView tv_bank_name;
	private EditText  ed_money;
	private EditText  ed_cardnumber;
	private EditText  user_phone;
	private EditText  ed_vcode;
	/**银行选择**/
	private OptionsPopupWindow bank_select;
	private ArrayList<String>  bank_select_list;
	private List<BankItem> mBankList = new ArrayList<BankItem>();
	/**
	 * 发送验证码
	 * */
	private TextView mSendPhoneNum;
	private TimeCount time;


    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zft_recharge);

		initView();
		time = new TimeCount(60000, 1000);

		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		dohttpBank();

		
	}

	public void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("支付通快捷支付");

		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.btn_tijiao).setOnClickListener(this);
		findViewById(R.id.choice_bank_btn).setOnClickListener(this);

		tv_real_name = (TextView) findViewById(R.id.real_name);
		ed_money = (EditText) findViewById(R.id.ed_money);
		tv_bank_name = (TextView) findViewById(R.id.tv_bank_name);
		ed_cardnumber = (EditText) findViewById(R.id.ed_cardnumber);

		mSendPhoneNum = (TextView) findViewById(R.id.sendphonenum);
		mSendPhoneNum.setOnClickListener(this);
		user_phone = (EditText) findViewById(R.id.user_phone);
		ed_vcode = (EditText) findViewById(R.id.ed_vcode);

		bank_select_list = new ArrayList<String>();
		bank_select = new OptionsPopupWindow(this);

		bank_select.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {
			@Override
			public void onOptionsSelect(int options1, int option2, int options3) {

				tv_bank_name.setText(bank_select_list.get(options1));
				ed_cardnumber.setText(mBankList.get(options1).bank_num);
				authToken = mBankList.get(options1).authToken;
//				bank_code = mBankList.get(options1).bank_code;
				bank_name = mBankList.get(options1).bank_name;

			}
		});
	}

	/**
	 * 提交接口
	 * uid    paycode    loan_on
	 */
	public void doHttp() {
		JsonBuilder jsonBuilder = new JsonBuilder();
		jsonBuilder.put("loan_on", loan_on);
		jsonBuilder.put("paycode", ed_vcode.getText().toString());


		BaseHttpClient.post(getBaseContext(), Default.rechargeSure,
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
									showCustomToast(json.getString("message"));
									finish();
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(ZFTRechargeActivity.this, json.getInt("status"), message);
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
	 *  "authToken": "cb2e1884c5bb138eb1d33577f05519e6",
	 "bank_code": "ICBC",
	 "bank_num": "6222021507003729079",
	 "bank_name": "中国工商银行"
	 */
	class BankItem {
		String authToken;
		String bank_code;
		String bank_num;
		String bank_name;

		public BankItem(JSONObject json) {
			try {
				authToken = json.getString("authToken");
				bank_code = json.getString("bank_code");
				bank_num = json.getString("bank_num");
				bank_name = json.getString("bank_name");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.btn_tijiao:
				if(SystenmApi.isNullOrBlank(tv_bank_name.getText().toString())){

					showCustomToast("请选择银行卡");
					return;
				}

				if(SystenmApi.isNullOrBlank(ed_money.getText().toString())){

					showCustomToast("请输入交易金额");
					return;
				}

				if(SystenmApi.isNullOrBlank(ed_cardnumber.getText().toString())){

					showCustomToast("请输入银行卡号");
					return;
				}

				if(SystenmApi.isNullOrBlank(user_phone.getText().toString())){//strBranch

					showCustomToast("请输入手机号");
					return;
				}


				if(SystenmApi.isNullOrBlank(ed_vcode.getText().toString())){

					showCustomToast("请输入手机验证码");
					return;
				}

				doHttp();
				break;
		case R.id.sendphonenum:

			dohttpGetcode();
			break;
		case R.id.choice_bank_btn:

			if(!bank_select.isShowing()){
				bank_select.showAtLocation(v, Gravity.BOTTOM,0,0);
			}

			break;
		}
	}


	/**
	 * 获取银行列表
	 */
	private void dohttpBank() {

		BaseHttpClient.post(getBaseContext(), Default.rechargePage, null,
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
									getBankInfo(json);
								} else {
									showCustomToast(json.getString("message"));
								}
							} else {
								String message = json.getString("message");
								SystenmApi.showCommonErrorDialog(ZFTRechargeActivity.this, json.getInt("status"),message);
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

	public void getBankInfo(JSONObject json) {

		if (json.has("userName")) {

			userName=(json.optString("userName", "0"));
			tv_real_name.setText(userName);
		}
		if (json.has("user_phone")) {

			phone=json.optString("user_phone", "0");
			user_phone.setText(phone);
		}
		mBankList.clear();
		bank_select_list.clear();
		try {
			JSONArray banks = json.getJSONArray("banks");

			for (int i = 0; i < banks.length(); i++) {
				JSONObject temp = banks.getJSONObject(i);
				BankItem item = new BankItem(temp);
				mBankList.add(item);
				bank_select_list.add(item.bank_name);

			}

			bank_select.setPicker(bank_select_list);


		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/**
	 *发送短信验证码
	 *  "uid":   "money"   "bank"   "mobile"  "cardNum"    "authToken
	 */
	private void dohttpGetcode() {

		JsonBuilder builder = new JsonBuilder();
		builder.put("money", ed_money.getText().toString());
//		builder.put("bank", bank_code);
		builder.put("bank", bank_name);
		builder.put("mobile", user_phone.getText().toString());
		builder.put("cardNum", ed_cardnumber.getText().toString());
		builder.put("authToken", authToken);
		BaseHttpClient.post(getBaseContext(), Default.icardPay, builder,
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
								if (response != null) {
									if (response.has("status")) {
										if (response.getInt("status") == 1) {
											time.start();
											showCustomToast("成功发送短信");
//											if (response.has("loan_on")) {

												loan_on=response.optString("loan_on");
//											}


										}else if (response.getInt("status") == 2){
											showCustomToast(response.getString("message"));
										} else {
											String message = response.getString("message");
											SystenmApi.showCommonErrorDialog(ZFTRechargeActivity.this, response.getInt("status"), message);
										}
									}
								} else {

									showCustomToast(R.string.toast1);
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
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
					}
				});

	}

	/* 倒计时类 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {
			mSendPhoneNum.setText("重新发送");
			mSendPhoneNum.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			mSendPhoneNum.setClickable(false);
			mSendPhoneNum.setText(millisUntilFinished / 1000 + "秒");
		}
	}
}
