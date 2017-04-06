package com.lmq.main.activity.register;

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
import com.lmq.main.activity.OpenThirdActivity;
import com.lmq.main.activity.tools.LMQWebViewActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONObject;

public class YbRegisterActivity extends BaseActivity implements OnClickListener {

	private boolean mRemember = true;

	private TimeCount time;

	private EditText mName, mPhone, user_Yzm, mPassw, mPassw2, register_people;

	private Button mSendPhoneNum; //发送验证码
	private Button registerContextBtn; //同意协议的checkbox
	private Button registerBtn; //下一步按钮

	private String name;
	private String phone;
	private String phonenum;
	private String passw;
	private String passw2;
	private String em_people;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_yb_layout);

		TextView text = (TextView) findViewById(R.id.title);
		text.setText(R.string.register);


		mName = (EditText) findViewById(R.id.ed_name);
		mPhone = (EditText) findViewById(R.id.ed_phone);
		user_Yzm = (EditText) findViewById(R.id.user_yzm);
		mPassw = (EditText) findViewById(R.id.ed_login_pwd);
		mPassw2 = (EditText) findViewById(R.id.ed_confirm_pwd);
		register_people = (EditText) findViewById(R.id.editem_people);

		mSendPhoneNum = (Button) findViewById(R.id.sendphonenum); //发送验证码按钮
		registerContextBtn = (Button) findViewById(R.id.register_flag); //注册协议的选择同意按钮
		registerBtn = (Button) findViewById(R.id.btn_finish_register); //下一步按钮

		findViewById(R.id.back).setOnClickListener(this); //返回按钮
		mSendPhoneNum.setOnClickListener(this); //发送验证码
		registerContextBtn.setOnClickListener(this);  //注册协议的选择同意按钮
		findViewById(R.id.show_context).setOnClickListener(this); //进入注册协议页面
		registerBtn.setOnClickListener(this); //下一步按钮

		time = new TimeCount(60000, 1000);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.back:
			finish();
			break;

		case R.id.register_flag:
			if (mRemember) {
				mRemember = false;
				registerContextBtn
				.setBackgroundResource(R.drawable.b_chech_1_0);
			} else {
				mRemember = true;
				registerContextBtn
				.setBackgroundResource(R.drawable.b_chech_1_1);
			}
			break;

		case R.id.show_context:

			//			startActivity(new Intent(RegisterStepFirstActivity.this,
			//					ShowRegisterContextActivity.class));
			Intent abIntent = new Intent();
			abIntent.putExtra("title","注册协议");
			abIntent.putExtra("url", Default.ip+"/Mobile/Api/ruleserver");
			abIntent.setClass(YbRegisterActivity.this, LMQWebViewActivity.class);
			startActivity(abIntent);
			break;

		case R.id.sendphonenum:
			phone = mPhone.getText().toString();
			if(phone.equals(""))
			{
				showCustomToast(R.string.toastphone);
				return;
			}else  if(phone.length()!=11){
				showCustomToast("请输入正确的手机号");
				return;
			}

			doHttpSendPhone(phone);
			break;


		case R.id.btn_finish_register:
			phone = mPhone.getText().toString();
			name = mName.getText().toString();
			passw = mPassw.getText().toString();
			passw2 = mPassw2.getText().toString();
			phonenum = user_Yzm.getText().toString();
			em_people = register_people.getText().toString();

			if (name.equals("")) {
				showCustomToast(R.string.toast12);
				return;
			}else if(SystenmApi.ByteLenth(name)<4 || SystenmApi.ByteLenth(name)>20){
				showCustomToast("4-20个字母、数字、汉字、下划线");
				return;
			}
			if(phone.equals(""))
			{
				showCustomToast(R.string.toastphone);
				return;
			}else  if(phone.length()!=11){
				showCustomToast("请输入正确的手机号");
				return;
			}

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

			if (!mRemember) {
				showCustomToast("同意注册协义才可以注册");
				return;
			}

			doHttp();
			break;

		default:
			break;
		}
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
					if (statusCode == 200) {
						if (json.getInt("status") == 1) {
							time.start();
							showCustomToast(json.getString("message"));

						} else {

							showCustomToast(json.getString("message"));

						}
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

							//                                    Intent intent = new Intent(RegisterStepSecondActivity.this,
							//                                            loginActivity.class);
							//                                    intent.putExtra("name", name);
							//                                    intent.putExtra("password", passw);
							//
							//                                    Default.userId = json.getLong("uid");
							//
							//                                    startActivity(intent);
							Intent intent = new Intent();
							intent.putExtra("name", name);
							intent.putExtra("password", passw);

							Default.userId = json.getLong("uid");

							setResult(Default.RESULT_REGISTER_TO_LOGIN,
									intent);

							if(Default.IS_YB){
								startActivity(new Intent(YbRegisterActivity.this, OpenThirdActivity.class));
							}

							finish();
						} else {
							showCustomToast(json.getString

									("message"));
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



	/* 倒计时类 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {
			mSendPhoneNum.setText("重新获取");
			mSendPhoneNum.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			mSendPhoneNum.setClickable(false);
			mSendPhoneNum.setText(millisUntilFinished / 1000 + "秒");
		}
	}


}
