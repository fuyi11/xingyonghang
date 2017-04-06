package com.lmq.pay;


import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.MyLog;
import com.lmq.main.util.Default;
import com.lmq.http.JsonHttpResponseHandler;
import com.money.more.activity.ControllerActivity;
import com.money.more.basil.Conts;
import com.money.more.bean.AuthData;
import com.money.more.bean.RegisterData;

public class MoneyMoreMoreSqActivity extends BaseActivity implements
		OnClickListener {

	private int mmm_status;
	private int sq1_status;
	private int sq2_status;

	private TextView mInfo[];

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moneymoremore_kh_sq);
		Intent intent = getIntent();
		if (intent != null) {
			mmm_status = intent.getIntExtra("mmm", 0);
			sq1_status = intent.getIntExtra("sq1", 0);
			sq2_status = intent.getIntExtra("sq2", 0);
		}
		mInfo = new TextView[3];
		mInfo[0] = (TextView) findViewById(R.id.tv_mmm);
		mInfo[1] = (TextView) findViewById(R.id.tv_mmmsq);
		mInfo[2] = (TextView) findViewById(R.id.tv_mmmsq2);

		mInfo[0].setText(getStyle(mmm_status));
		mInfo[1].setText(getStyle(sq1_status));
		mInfo[2].setText(getStyle(sq2_status));

		findViewById(R.id.rl_mmm).setOnClickListener(this);
		findViewById(R.id.rl_mmmsq).setOnClickListener(this);
		findViewById(R.id.rl_mmmsq2).setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
	}

	public String getStyle(int type) {
		switch (type) {
			case 0:
				return "未授权";
			case 1:
			default:
				return "已授权";

		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.rl_mmm:
				if (mmm_status == 1) {
					showCustomToast("您已成功绑定托管信息！");
					return;
				}
				doHttpMmm();

				break;
			case R.id.rl_mmmsq:
				if (sq1_status == 1) {
					showCustomToast("您已开启投标授权！");
					return;
				}

				openTBsq();

				break;
			case R.id.rl_mmmsq2:
				if (sq2_status == 1) {
					showCustomToast("您已开启还款授权！");
					return;
				}
				doHttpsq(2, Default.MoneyMmsq2);
				break;

		}
	}

	/**
	 * 钱多多 投标授权
	 */
	private void openTBsq() {


		BaseHttpClient.post(getApplicationContext(), Default.MoneyMmsq1, null,
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

								AuthData ad = new AuthData();
								Conts.setServiceUrl(json.getString("url"));
								Conts.setMddPrivateKey(Default.privateKey);

								ad.setOpentype(json.getString("AuthorizeTypeOpen"));
								ad.setMddid(json.getString("MoneymoremoreId"));
								ad.setPlatformdd(json.getString("PlatformMoneymoremore"));
								ad.setNotifyURL(json.getString("NotifyURL"));


								ad.setSignData(ad.signData());

								Intent intent = new Intent(MoneyMoreMoreSqActivity.this, ControllerActivity.class);
								intent.putExtra("type", 5);
								intent.putExtra("data", ad);
								startActivityForResult(intent, 600);


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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		StringBuilder sb = new StringBuilder();
		if (resultCode == 100 || resultCode == 200) {// 注册返回
			sb.append("注册返回数据:");
			int code = data.getIntExtra("code", -1);
			String message = data.getStringExtra("message");
			sb.append("code:").append(code).append(",message:").append(message);
			if (code != 88) {
				String AccountNumber = data.getStringExtra("AccountNumber");
				sb.append(",AccountNumber:").append(AccountNumber);
				showCustomToast(sb.toString());
			} else {
				showCustomToast("您已成功绑定托管信息！");
				mmm_status = 1;
				mInfo[0].setText(getStyle(mmm_status));
			}
		} else if (resultCode == 600) {// 授权接口返回
			sb.append("提现数据返回:");
			int code = data.getIntExtra("code", -1);
			String message = data.getStringExtra("message");
			sb.append("code:").append(code).append(",message").append(message);
			if (code != 88) {
				String authorizeType = data.getStringExtra("AuthorizeType");
				sb.append(",authorizeType").append(authorizeType);
				showCustomToast(sb.toString());
			} else {
				showCustomToast("您已授权成功！");
				if (requestCode == 100) {
					sq1_status = 1;
					mInfo[1].setText(getStyle(sq1_status));
				} else if (requestCode == 200) {
					sq2_status = 1;
					mInfo[2].setText(getStyle(sq2_status));

				}
			}
		}
	}

	/**
	 * 绑定钱多多 信息 开户
	 * */
	public void doHttpMmm() {

		BaseHttpClient.post(getApplicationContext(), Default.MoneyMm, null,
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

								getMoneyMm(json);
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

	public void getMoneyMm(JSONObject json) {
		try {

			String info[] = new String[15];


			info[0] = json.getString("RegisterType");
			info[1] = json.getString("AccountType");
			info[2] = json.getString("Mobile");
			info[3] = json.getString("Email");
			info[4] = json.getString("RealName");
			info[5] = json.getString("IdentificationNo");
			info[6] = json.getString("LoanPlatformAccount");

			info[7] = json.getString("PlatformMoneymoremore");
			info[8] = json.getString("RandomTimeStamp");
			info[9] = json.getString("Remark1");
			info[10] = json.getString("Remark2");
			info[11] = json.getString("Remark3");
			info[12] = json.getString("NotifyURL");
			info[13] = json.getString("SignInfo");
			info[14] = json.getString("url");


			register(info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 绑定/注册
	 */
	private void register(String info[]) {


		Intent intent = new Intent(MoneyMoreMoreSqActivity.this,
				ControllerActivity.class);

		//设置钱多多开户接口地址
		Conts.setServiceUrl(info[14]);

		Conts.setMddPrivateKey(Default.privateKey);



		//开户需要的参数
		RegisterData rd = new RegisterData();
		rd.setAccountType(info[1]);
		rd.setMobile(info[2]);
		rd.setEmail(info[3]);
		rd.setRealName(info[4]);
		rd.setIdentificationNo(info[5]);
		rd.setPlatAccount(info[7]);
		rd.setLoanPlatAccount(info[6]);
		rd.setRandomTimeStamp(info[8]);
		rd.setRemark1(info[9]);
		rd.setRemark2(info[10]);
		rd.setRemark3(info[11]);
		rd.setNotifyURL(info[12]);

		rd.setSignInfo(rd.signDate());



		MyLog.e("sign"+rd.signDate());

		intent.putExtra("data",rd);
		//添加请求类型，1：开户
		intent.putExtra("type", 1);


		startActivityForResult(intent, 200);
	}



	/**
	 * 绑定钱多多 还款授权
	 * */
	public void doHttpsq(final int type, String url) {

		BaseHttpClient.post(getBaseContext(), url, null,
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

									AuthData ad = new AuthData();
									Conts.setServiceUrl(json.getString("url"));
									Conts.setMddPrivateKey(Default.privateKey);

									ad.setOpentype(json.getString("AuthorizeTypeOpen"));
									ad.setMddid(json.getString("MoneymoremoreId"));
									ad.setPlatformdd(json.getString("PlatformMoneymoremore"));
									ad.setNotifyURL(json.getString("NotifyURL"));
									ad.setSignData(json.getString("SignInfo"));

									ad.setSignData(ad.signData());

									Intent intent = new Intent(MoneyMoreMoreSqActivity.this, ControllerActivity.class);
									intent.putExtra("type", 5);
									intent.putExtra("data", ad);
									startActivityForResult(intent, 600);



								} else {
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

}