package com.lmq.main.activity.register;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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


public class RegisterStepSecondActivity extends BaseActivity implements OnClickListener {
	private EditText user_Yzm, mPassw, mPassw2,register_people;
	private TextView tv_yzm;
	private String name;
	private String phone;
	private String passw;
	private String passw2;
	private String phonenum;
	private String em_people;

	private TimeCount time;
	/**注册按钮*/
	private Button registerContextBtn;
	/**发送验证码按钮*/
	private Button mSendPhoneNum;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register2);

		findViewById(R.id.back).setOnClickListener(this);
		mSendPhoneNum = (Button) findViewById(R.id.sendphonenum);
		mSendPhoneNum.setOnClickListener(this);
		registerContextBtn = (Button) findViewById(R.id.btn_finish_register);
		registerContextBtn.setOnClickListener(this);
		
		Intent intent = getIntent();
		name = intent.getExtras().getString("name");
		phone = intent.getExtras().getString("phone");
		

		TextView text = (TextView) findViewById(R.id.title);
		text.setText(R.string.register);
		 tv_yzm = (TextView) findViewById(R.id.tv_yzm);
		 tv_yzm.setText(phone+"");

		user_Yzm = (EditText) findViewById(R.id.user_yzm);
		mPassw = (EditText) findViewById(R.id.ed_login_pwd);
		mPassw2 = (EditText) findViewById(R.id.ed_confirm_pwd);
		register_people = (EditText) findViewById(R.id.editem_people);
		doHttpSendPhone(phone);
		MyLog.e("第一次执行发短信");
		time = new TimeCount(60000, 1000);
		time.start();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_finish_register:
			 passw = mPassw.getText().toString();
			 passw2 = mPassw2.getText().toString();
			 phonenum = user_Yzm.getText().toString();
			 em_people = register_people.getText().toString();
			
			if (phonenum.equals("")) {
				showCustomToast(R.string.toastyzm);

				return ;
			}
			
			if (passw.equals("")) {
				showCustomToast(R.string.toast14);

				return ;
			} else if (passw.length() < 6) {
				showCustomToast(R.string.toast15);
				return ;
			} else if (passw.length() > 16) {
				showCustomToast(R.string.toast16);
				return ;
			}
			if (!passw.equals(passw2)) {
				showCustomToast(R.string.toast19);
				return ;
			}
			
			doHttp();
			break;
		case R.id.back:
			finish();
			time.cancel();
			break;
		case R.id.sendphonenum:
//			time.start();
//			doHttpSendPhone(phone);
//			startActivityForResult(new Intent(.this, .class), 10111);
			Intent intent = new Intent(RegisterStepSecondActivity.this,RegisterPhotoActivity.class);
			intent.putExtra("tag",1);

			startActivityForResult(intent,10111);
			break;
		
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode ==10111) {

			if(resultCode == Default.RESULT_BANKCARD){
				MyLog.e("第2次重新发短信");
				doHttpSendPhone(phone);
			}
		}
	}


	public void doHttp() {

		JsonBuilder builder = new JsonBuilder();
		builder.put("name", name);
		builder.put("password", passw);
		builder.put("tel", phone);
		builder.put("tel2", phonenum);
		builder.put("people", em_people);

		BaseHttpClient.post(getBaseContext(), Default.register, builder,
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
					
									showCustomToast(R.string.toast5);

//									Intent intent = new Intent(RegisterStepSecondActivity.this,
//											loginActivity.class);
//									intent.putExtra("name", name);
//									intent.putExtra("password", passw);
//
//									Default.userId = json.getLong("uid");
//
//									startActivity(intent);
									Intent intent = new Intent();
									intent.putExtra("name", name);
									intent.putExtra("password", passw);
									
									Default.userId = json.getLong("uid");
									
									setResult(Default.RESULT_REGISTER_TO_LOGIN,
											intent);
									finish();
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(RegisterStepSecondActivity.this, json.getInt("status"),message);
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

	public void doHttpSendPhone(String phone) {

		JsonBuilder builder = new JsonBuilder();
		builder.put("phone", phone);

		BaseHttpClient.post(getBaseContext(), Default.registerPhone, builder,
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
						dismissLoadingDialog();
						try {

							if (json.getInt("status") == 1) {
								time.start();
								showCustomToast(json.getString("message"));

							} else {

								showCustomToast(json.getString("message"));
								finish();

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						super.onFailure(statusCode, headers, responseString,
								throwable);
						dismissLoadingDialog();
						showCustomToast(responseString);

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
			mSendPhoneNum.setText(millisUntilFinished / 1000 + "秒后重发");
		}
	}
}
