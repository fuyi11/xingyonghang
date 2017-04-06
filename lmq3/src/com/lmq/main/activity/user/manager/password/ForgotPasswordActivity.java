package com.lmq.main.activity.user.manager.password;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;
/***
 * 找回密码
 * @author Administrator
 *
 */
public class ForgotPasswordActivity extends BaseActivity implements OnClickListener {

	/**
	 * 用户手机验证码
	 */
	private EditText userYzm;
	/**
	 * 手机号
	 */
	private EditText user_phone;
	/**
	 * 新密码
	 */
	private EditText new_user_pwd;
	private String strYzm;
	private String phone;
	private String strNewPwd;

	/**
	 * 发送验证码
	 * */
	private Button mSendPhoneNum;
	private TimeCount time;

//	private Handler mhandle = new Handler();
//	private Runnable runnbale = new Runnable() {
//
//		@Override
//		public void run() {
//			changeSendMessageBtn();
//		}
//	};

//	private int timeMax = 60;
//	private int time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_pwd_new);
		TextView titTextView = (TextView) findViewById(R.id.title);
		titTextView.setText("找回密码");
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.forget_pwd).setOnClickListener(this);

		userYzm = (EditText) findViewById(R.id.user_yzm);
		user_phone = (EditText) findViewById(R.id.user_phone);
		new_user_pwd = (EditText) findViewById(R.id.new_user_pwd2);

		mSendPhoneNum = (Button) findViewById(R.id.sendphonenum);
		mSendPhoneNum.setOnClickListener(this);


//		mhandle.postDelayed(runnbale, 1000);
		time = new TimeCount(60000, 1000);
	}

	/**
	 * 修改密码第一步
	 */
	private void dohttp() {

		JsonBuilder builder = new JsonBuilder();
		builder.put("phone", phone);
		BaseHttpClient.post(getBaseContext(), Default.FORGOT_PWD_1, builder,
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
											showCustomToast(response
													.getString("message"));

//											mhandle.postDelayed(runnbale, 1000);
										} else {
											String message = response.getString("message");
											SystenmApi.showCommonErrorDialog(ForgotPasswordActivity.this, response.getInt("status"),message);
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

	/**
	 * 修改密码第二步
	 */
	private void dohttp2() {

		JsonBuilder builder = new JsonBuilder();
		builder.put("code", strYzm);
		builder.put("password", strNewPwd);
		BaseHttpClient.post(getBaseContext(), Default.FORGOT_PWD_3, builder,
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
											showCustomToast(response
													.getString("message"));
											finish();
										} else {
											String message = response.getString("message");
											SystenmApi.showCommonErrorDialog(ForgotPasswordActivity.this, response.getInt("status"),message);
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

//	private void changeSendMessageBtn() {
//		time = time + 1000;
//
//		if (time < 1000 * timeMax) {
//			mSendPhoneNum.setText((1000 * timeMax - time) / 1000 + "秒后重试");
//			mhandle.postDelayed(runnbale, 1000);
//		} else {
//			time = 0;
//			mSendPhoneNum.setText("获取验证码");
//			mSendPhoneNum.setEnabled(true);
//			stop();
//		}
//
//	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.sendphonenum:

//			mSendPhoneNum.setEnabled(false);
			
			phone = user_phone.getText().toString();
			if(phone.equals(""))
			{
				showCustomToast(R.string.toastphone);
				return;
			}
			
			dohttp();
			break;

		case R.id.forget_pwd:
			strYzm = userYzm.getText().toString();
			phone = user_phone.getText().toString();
			strNewPwd = new_user_pwd.getText().toString();
			if (strYzm.equals("")) {
				showCustomToast("请输入短信验证码！");
				return;
			}

			if (strNewPwd.equals("")) {
				showCustomToast("请输入新登录密码！");
				return;
			}
			

			dohttp2();

			break;

		default:
			break;
		}

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
			mSendPhoneNum.setText(millisUntilFinished / 1000 + "秒后重发");
		}
	}

//	@Override
//	public void finish() {
//		// TODO Auto-generated method stub
//		super.finish();
//		stop();
//	}
//
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		stop();
//	}
//
//	public void stop() {
//		mhandle.removeCallbacks(runnbale);
//	}
}
