package com.lmq.main.activity.register;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.main.activity.tools.LMQWebViewActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;
import com.lmq.http.JsonHttpResponseHandler;

public class RegisterStepFirstActivity extends BaseActivity implements OnClickListener {
	private EditText mName,mPhone;

	/**
	 * 注册同意协议 按钮
	 */
	private Button registerContextBtn;
	private Button next_btn;
	private boolean mRemember = true;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register1);
		next_btn = (Button) findViewById(R.id.next_btn);
		next_btn.setOnClickListener(this);

		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.show_context).setOnClickListener(this);
		
		registerContextBtn = (Button) findViewById(R.id.register_flag);
		registerContextBtn.setOnClickListener(this);

		TextView text = (TextView) findViewById(R.id.title);
		text.setText(R.string.register);

		mName = (EditText) findViewById(R.id.ed_name);
		mPhone = (EditText) findViewById(R.id.ed_phone);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next_btn:
			String phone = mPhone.getText().toString();
			String name = mName.getText().toString();
			if (name.equals("")) {
				showCustomToast(R.string.toast12);
				return;
			}else if(SystenmApi.ByteLenth(name)<4 || SystenmApi.ByteLenth(name)>20){
				  showCustomToast("4-20个字母、数字、汉字、下划线");
			    return;
			}
			if(phone.equals("") )
			{
				showCustomToast(R.string.toastphone);
				return;
			}else  if(phone.length()!=11){
				showCustomToast("请输入正确的手机号");
				return;
			}
			
			if (!mRemember) {
				showCustomToast("同意注册协义才可以注册");
				return;
			}
			
//			doHttpSendPhone(phone);
//			startActivityForResult(new Intent(RegisterStepFirstActivity.this, RegisterPhotoActivity.class), 10111);
			Intent intent = new Intent(RegisterStepFirstActivity.this,RegisterStepSecondActivity.class);
		     	intent.putExtra("phone", mPhone.getText().toString());
			    intent.putExtra("name", mName.getText().toString());
			    startActivity(intent);
			finish();
			
			break;
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
			abIntent.putExtra("url",Default.ip+"/Mobile/Api/ruleserver");
			abIntent.setClass(RegisterStepFirstActivity.this, LMQWebViewActivity.class);
			startActivity(abIntent);
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
						try {

							if (statusCode == 200) {
								if (json != null) {
									if (json.has("status")) {
										if (json.getInt("status") == 1) {
											showCustomToast(json.getString("message"));
											Intent intent = new Intent(RegisterStepFirstActivity.this,RegisterStepSecondActivity.class);
								              intent.putExtra("phone", mPhone.getText().toString());
								              intent.putExtra("name", mName.getText().toString());
											  startActivity(intent);
											  finish();
										} else {
											showCustomToast(json.getString("message"));
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
						super.onFailure(statusCode, headers, responseString,
								throwable);
						dismissLoadingDialog();
						showCustomToast(responseString);

					}

				});

	}
	
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.left_to_right, R.anim.to_right);
	}

	
}
