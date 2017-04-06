package com.lmq.pay;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.main.activity.user.manager.bankinfo.ChoiceBankListActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.item.BankCardListlItem;
import com.lmq.main.util.Default;
import com.lmq.http.JsonHttpResponseHandler;
import com.money.more.activity.ControllerActivity;
import com.money.more.basil.Conts;
import com.money.more.bean.RechargeData;
import com.money.more.bean.WithdrawData;

public class MoneyMoreMorePayActivity extends BaseActivity implements
		OnClickListener {

	Button mEnter_money;

	private EditText mEdit_money;
	private EditText mEdit_pass;
	private String mMoney, mPassword;

	private int mType; // 0 充值 1 提现

	private TextView mTip;
	private TextView mBankCodeText;
	private int mBankCodeId;
	private String mBankCodeNum;

	private ArrayList<BankCardListlItem> data = new ArrayList<BankCardListlItem>();
	private JSONArray list = null;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moneymoremore);

		mType = getIntent().getStringExtra("type").equals("cz") ? 0 : 1;

		findViewById(R.id.back).setOnClickListener(this);

		mBankCodeText = (TextView) findViewById(R.id.code);
		mBankCodeText.setOnClickListener(this);

		mEnter_money = (Button) findViewById(R.id.enter_money);
		mEnter_money.setOnClickListener(this);

		mEdit_money = (EditText) findViewById(R.id.edit_money);
		mTip = (TextView) findViewById(R.id.edit_tip);

		TextView title = (TextView) findViewById(R.id.title);

		if (mType == 0)// 充值
		{
			mEdit_money.setHint("请输入充值金额");
			title.setText("我要充值");
			mTip.setText("充值金额");
			mEnter_money.setText("充    值");
			findViewById(R.id.ll_two).setVisibility(View.GONE);
		} else{// 提现
			mEdit_money.setHint("请输入提现金额");
			title.setText("我要提现");
			mTip.setText("提现金额");
			mEnter_money.setText("提    现");
		}

	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.enter_money:

				mMoney = mEdit_money.getText().toString();
				// mPassword = mEdit_pass.getText().toString();
				if (mMoney.equals("")) {
					showCustomToast("请输入" + (mType == 0 ? "充值" : "提现") + "金额");
					return;
				}

				if (mType == 0) {
					doHttpCz();
				} else {
					doHttpTx();
				}
				break;
			case R.id.back:
				finish();
				break;
			case R.id.code:
				startActivityForResult(new Intent(MoneyMoreMorePayActivity.this, ChoiceBankListActivity.class), 10111);
				break;
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
	}

	private void doHttpTx() {

		JsonBuilder builder = new JsonBuilder();
		builder.put("money", mMoney);
		builder.put("bank_id", mBankCodeId);

		BaseHttpClient.post(getBaseContext(), Default.withdrawMoney, builder,
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

									WithdrawData  wd = new WithdrawData();
									if (json.has("url")){

										Conts.setServiceUrl(json.getString("url"));

									}
									Conts.setMddPrivateKey(Default.privateKey);
									wd.setWithdrawnum(json.getString("WithdrawMoneymoremore"));
									wd.setPlatformmdd(json.getString("PlatformMoneymoremore"));
									wd.setOrderno(json.getString("OrderNo"));
									wd.setAmount(json.getString("Amount"));
									wd.setCardno(json.getString("CardNo"));
									wd.setCardtype(json.getInt("CardType"));
									wd.setBankCode(json.getInt("BankCode"));
									wd.setBranchBankName(json.getString("BranchBankName"));
									wd.setProvince(json.getInt("Province"));
									wd.setCity(json.getInt("City"));
									wd.setRemark1(json.getString("Remark1"));
									wd.setNotifyURL(json.getString("NotifyURL"));

									wd.setSignDate(wd.signData());
									wd.setCardno(json.getString("CardNocode"));

									Intent intent = new Intent(MoneyMoreMorePayActivity.this,ControllerActivity.class);
									intent.putExtra("type", 3);
									intent.putExtra("data", wd);
									startActivityForResult(intent, 400);


								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(MoneyMoreMorePayActivity.this, json.getInt("status"), message);


								}
							} else {
								showCustomToast("提现失败");
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



	private void doHttpCz() {
		JsonBuilder builder = new JsonBuilder();
		builder.put("money", mMoney);

		BaseHttpClient.post(getBaseContext(), Default.chargeMoney, builder,
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

									RechargeData rd = new RechargeData();

									if (json.has("url")){

										Conts.setServiceUrl(json.getString("url"));

									}
									Conts.setMddPrivateKey(Default.privateKey);
									rd.setRechargemdd(json.getString("RechargeMoneymoremore"));
									rd.setPlatformdd(json.getString("PlatformMoneymoremore"));
									rd.setOrderno(json.getString("OrderNo"));
									rd.setAmount(json.getString("Amount"));
									rd.setFeeType(json.getString("FeeType"));
									rd.setRemark1(json.getString("Remark1"));
									rd.setNotifyURL(json.getString("NotifyURL"));

									rd.setSignDate(rd.signDate());





									//跳转进入乾多多接口界面
									Intent intent = new Intent(MoneyMoreMorePayActivity.this,ControllerActivity.class);
									//添加请求参数
									intent.putExtra("data", rd);
									//添加请求类型2，表示充值
									intent.putExtra("type", 2);
									startActivityForResult(intent, 2);

								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(MoneyMoreMorePayActivity.this, json.getInt("status"), message);
								}
							} else {
								showCustomToast("充值失败");
								MyLog.d("zzx", "充值失败");
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 10111) {
			if (resultCode == Default.RESULT_BANKCARD) {

				mBankCodeNum = data.getExtras().getString("bank_num");
				mBankCodeId = data.getExtras().getInt("id", 0);
				mBankCodeText.setText(SystenmApi.getBankcode(mBankCodeNum));
			}
		}else{
			int code = data.getIntExtra("code", -1);
			if (code == 88) {
				showCustomToast(mType == 0 ? "充值成功" : "提现成功");
				finish();
			} else {
				String message = data.getStringExtra("message");
				Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
			}
		}
	}

}
