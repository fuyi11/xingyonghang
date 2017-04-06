package com.lmq.main.activity.user.manager.password;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.main.activity.user.manager.pay.ThirdPayActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Data;
import com.lmq.main.util.Default;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.menu.MainTabNewActivity;


/**
 * 修改登录密码
 * 
 * @author 孙建超
 *
 */
public class ChangeLoginPasswordActivity extends BaseActivity implements OnClickListener {
	private EditText mOld, mPassw, mPassw2;
	private String info[];

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.people_info_update_loginpsw);

		initView();
	}

	public void initView() {
		findViewById(R.id.enter).setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);

		TextView text = (TextView) findViewById(R.id.title);
		text.setText(R.string.peo_password_1);

		mOld = (EditText) findViewById(R.id.editold);
		mPassw = (EditText) findViewById(R.id.editpassw);
		mPassw2 = (EditText) findViewById(R.id.editpassw2);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.enter:
			info = getInfo();
			if (info == null) {
				return;
			}
			doHttp();
			break;
		case R.id.back:
			finish();
			break;
		}

	}

	public String[] getInfo() {
		String old = mOld.getText().toString();
		String passw = mPassw.getText().toString();
		String passw2 = mPassw2.getText().toString();

		if (old.equals("")) {
			showCustomToast(R.string.toast7);
			return null;
		}
		if (passw.equals("")) {
			showCustomToast(R.string.toast8);
			return null;
		}
		if (passw.length() < 4) {
			showCustomToast(R.string.toast13);

			return null;
		}
		if (passw.length() < 6) {
			showCustomToast(R.string.toast8_2);
			return null;
		}

		if (passw.length() > 16) {
			showCustomToast(R.string.toast8_3);
			return null;
		}
		if (passw2.equals("")) {
			showCustomToast(R.string.toast8_1);
			return null;
		}

		if (passw2.equals(passw) == false) {
			showCustomToast(R.string.toast9);
			return null;
		}

		return new String[] { old, passw, passw2 };
	}

	// public void doHttp()
	// {
	// showLoadingDialogNoCancle(getResources().getString(
	// R.string.toast2));
	//
	// JsonBuilder builder = new JsonBuilder();
	// builder.put("oldpwd", info[0]);
	// builder.put("newpwd", info[1]);
	//
	// new BaseModel(null, Default.changepass, builder)
	// .setConnectionResponseLinstener(new ConnectResponseListener()
	// {
	// public void onConnectResponseCallback(JSONObject json)
	// {
	// dismissLoadingDialog();
	// finish();
	// }
	//
	// @Override
	// public void onFail(JSONObject json)
	// {
	// dismissLoadingDialog();
	// try
	// {
	// Message msg = new Message();
	// Bundle bundel = new Bundle();
	// bundel.putString("info", json.getString("message"));
	// msg.setData(bundel);
	// handler.sendMessage(msg);
	// }
	// catch (JSONException e)
	// {
	// e.printStackTrace();
	// }
	// }
	// });
	// }
	public void doHttp() {

		JsonBuilder builder = new JsonBuilder();
		// builder.put("uid", Default.userId);
		builder.put("oldpwd", info[0]);
		builder.put("newpwd", info[1]);

		BaseHttpClient.post(getBaseContext(), Default.changepass, builder,
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


									doHttpExit();
									showCustomToast(json.getString("message"));


								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(ChangeLoginPasswordActivity.this, json.getInt("status"),message);
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

	private void doHttpExit() {

		BaseHttpClient.post(getApplicationContext(), Default.exit, null,
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
						MyLog.d("zzx", "exit成功" + json.toString());
						try {

							if (statusCode == 200) {
								if (json.getInt("status") == 1) {

									BaseHttpClient.clearCookie();
									MyLog.d("zzx", "exit成功");
									Default.layout_type = Default.pageStyleLogin;
									Default.userId = 0;

									Data.clearInfo();


									SharedPreferences sp = getApplicationContext().getSharedPreferences(
											Default.userPreferences, 0);
									SharedPreferences.Editor edit = sp.edit();
									edit.putBoolean("user_exit", true);
									edit.putBoolean("sl", false);
									edit.commit();



									Intent intent = new Intent();
									intent.setClass(ChangeLoginPasswordActivity.this,MainTabNewActivity.class);
									startActivity(intent);

								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(ChangeLoginPasswordActivity.this, json.getInt("status"),message);
								}
							} else {
								MyLog.d("zzx", "exit失败");
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
					}

				});
	}
}
