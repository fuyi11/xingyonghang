package com.lmq.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.util.Default;
import com.lmq.ybpay.YBPayActivity;

import org.apache.http.Header;
import org.json.JSONObject;

public class Yb_InfoActivity extends BaseActivity implements OnClickListener {
	private TextView title;

	private TextView tv_hyjhzt;
	private TextView tv_bdyhk;
	private TextView tv_tgzhye;
	private TextView tv_tgkyye;
	private TextView tv_tgdjje;
	
	private RelativeLayout bdyhk;
	private ToggleButton t2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yb_info_layout);
		title = (TextView) findViewById(R.id.title);
		title.setText("托管信息");
		findViewById(R.id.back).setOnClickListener(this);

		tv_hyjhzt = (TextView) findViewById(R.id.tv_hyjhzt);
		tv_bdyhk = (TextView) findViewById(R.id.tv_bdyhk);
		tv_tgzhye = (TextView) findViewById(R.id.tv_tgzhye);
		tv_tgkyye = (TextView) findViewById(R.id.tv_tgkyye);
		tv_tgdjje = (TextView) findViewById(R.id.tv_tgdjje);

		bdyhk = (RelativeLayout) findViewById(R.id.bdyhk);

		t2 = (ToggleButton) findViewById(R.id.toggle_screenLock);
		t2.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		doHttp();
		dohttpCheckbankcard();
		ckbiddoHttp();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.toggle_screenLock:


			if (t2.isChecked()) {

				t2.setChecked(true);


				Intent intent = new Intent(Yb_InfoActivity.this,YBPayActivity.class);
				intent.putExtra("YB_TYPE", Default.TYPE_YB_AUTOINVEST);

				startActivity(intent);

			} else {
				t2.setChecked(false);
//				Intent intent = new Intent(Yb_InfoActivity.this,YBPayActivity.class);
//				intent.putExtra("YB_TYPE", Default.TYPE_YB_CLOSE_AUTOINVEST);
//				startActivity(intent);
				closeInvestdoHttp();

			}


			break;

		default:
			break;
		}
	}

	public void closeInvestdoHttp() {

		BaseHttpClient.post(getBaseContext(), Default.Yb_closeInvest, null,
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						super.onStart();
						showLoadingDialogNoCancle(getResources().getString(
								R.string.toast2));
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
										  JSONObject json) {
						super.onSuccess(statusCode, headers, json);

						try {
							if (statusCode == 200) {
								if (json.getInt("status") == 1) {


									showCustomToast(json.getString("message"));
//									t2.setChecked(true);

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

	public void authorizedoHttp() {

		BaseHttpClient.post(getBaseContext(), Default.Yb_authorize, null,
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						super.onStart();
						showLoadingDialogNoCancle(getResources().getString(
								R.string.toast2));
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
										  JSONObject json) {
						super.onSuccess(statusCode, headers, json);

						try {
							if (statusCode == 200) {
								if (json.getInt("status") == 1) {

									showCustomToast(json.getString("message"));

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

	public void ckbiddoHttp() {

		BaseHttpClient.post(getBaseContext(), Default.Yb_ckbid, null,
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						super.onStart();
						showLoadingDialogNoCancle(getResources().getString(
								R.string.toast2));
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
										  JSONObject json) {
						super.onSuccess(statusCode, headers, json);

						MyLog.e("易宝注册返回数据" + json.toString());

						try {
							if (statusCode == 200) {
								if (json.getInt("status") == 1) {

									t2.setChecked(true);

									showCustomToast(json.getString("message"));

								} else if(json.getInt("status") == 3){
									t2.setChecked(false);

									showCustomToast(json.getString("message"));
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

	public void doHttp() {

		JsonBuilder builder = new JsonBuilder();

		MyLog.e("易宝注册请求路径" + Default.Yb_register);

		MyLog.e("易宝上传参数"+builder.toJsonString());

		BaseHttpClient.post(getBaseContext(), Default.Yb_register, builder,
				new JsonHttpResponseHandler() {

			@Override
			public void onStart() {
				super.onStart();
				showLoadingDialogNoCancle(getResources().getString(
						R.string.toast2));
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject json) {
				super.onSuccess(statusCode, headers, json);

				MyLog.e("易宝注册返回数据"+json.toString());

				try {
					if (statusCode == 200) {
						if (json.getInt("status") == 2) {

							getInfo(json);
							MyLog.e("易宝返回信息"+json.toString());

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

	public void getInfo(JSONObject json) {
		try {
			if(json.has("activeStatus")){
				if(json.getString("activeStatus").equals("ACTIVATED")){
					tv_hyjhzt.setText("已激活");
				}else if(json.getString("activeStatus").equals("DEACTIVATED")){
					tv_hyjhzt.setText("未激活");
				}
			}
			if(json.has("cardNo")){
				tv_bdyhk.setText(json.getString("cardNo"));
			}else{
				tv_bdyhk.setText("未绑定");
			}
			
			if(json.has("balance")){
				tv_tgzhye.setText(json.getString("balance"));
			}
			if(json.has("availableAmount")){
				tv_tgkyye.setText(json.getString("availableAmount"));
			}
			if(json.has("freezeAmount")){
				tv_tgdjje.setText(json.getString("freezeAmount"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	
	public void dohttpCheckbankcard() {

		BaseHttpClient.post(Yb_InfoActivity.this, Default.Yb_isbind_bankcard, null,
				new JsonHttpResponseHandler() {

			@Override
			public void onStart() {
				super.onStart();
				showLoadingDialogNoCancle(getResources().getString(
						R.string.toast2));
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				super.onSuccess(statusCode, headers, response);

				try {
					if (statusCode == 200) {
						if (response.getInt("status") == 1) {

							Default.has_Ybbankcard = true;

						}else if(response.getInt("status") == 2){
							Default.has_Ybbankcard = false;
							
							bdyhk.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									Intent intent = new Intent(Yb_InfoActivity.this, YBPayActivity.class);
									intent.putExtra("YB_TYPE", Default.TYPE_YB_BINDBANK);
									startActivity(intent);
								}
							});
							
						}else {
							showCustomToast(response
									.getString("message"));
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
