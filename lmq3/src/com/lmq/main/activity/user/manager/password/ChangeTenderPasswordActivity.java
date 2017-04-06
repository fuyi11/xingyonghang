package com.lmq.main.activity.user.manager.password;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.util.RSAUtilsZFT;

import java.security.PublicKey;


/**
 * 
 * 修改交易密码
 *
 */
public class ChangeTenderPasswordActivity extends BaseActivity implements
		OnClickListener  {
	private EditText mOldd, mPassww, mPassww2;
	private TextView text;
	private int pin_pass;
;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.people_info_update_jiaoyipsw);

		Intent intent = getIntent();

		initView();
		if(intent.hasExtra("pin_pass")) {
			pin_pass = intent.getExtras().getInt("pin_pass");
			MyLog.e("得到pin_pass--------------"+pin_pass);
			if(pin_pass==0){
				text.setText("设置支付密码");
			}
		}


	}

	public void initView() {
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.btn_jiayoyi).setOnClickListener(this);

		 text = (TextView) findViewById(R.id.title);
		text.setText(R.string.updatezfpsd_title);

		mOldd = (EditText) findViewById(R.id.ed_itold);
		mPassww = (EditText) findViewById(R.id.ed_itpassw);
		mPassww2 = (EditText) findViewById(R.id.ed_itpassw2);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_jiayoyi:
			if ( mOldd.getText().toString().equals("")) {
				showCustomToast(R.string.toast7);
				return ;
			}
			if (mPassww.getText().toString().equals("")) {
				showCustomToast(R.string.toast8);
				return ;
			}
			if (mPassww.getText().toString().length() < 6) {
				showCustomToast(R.string.toast8_2);
				return ;
			}

			if (mPassww.getText().toString().length() > 16) {
				showCustomToast(R.string.toast8_3);
				return ;
			}
			if (mPassww2.getText().toString().equals("")) {
				showCustomToast(R.string.toast8_1);
				return ;
			}
			if (mPassww2.getText().toString().equals(mPassww.getText().toString()) == false) {
				showCustomToast(R.string.toast9);
				return ;
			}
			doHttp();
			break;
		case R.id.back:
			finish();
			break;
		}

	}


	public void doHttp() {

		JsonBuilder builder = new JsonBuilder();

		if(Default.IS_ZFT){
			PublicKey puk;
			try {
				if(Default.ZFT_Environment){
					 puk = RSAUtilsZFT.getPublicKey(Default.TEST_ZFT_PUBLIC);
					MyLog.e("支付通测试环境加密");
				}else {
					 puk = RSAUtilsZFT.getPublicKey(Default.ZFT_PUBLIC);
					MyLog.e("支付通正式环境加密");
				}

				String encrpyOldPwdStr = RSAUtilsZFT.getEncodePwdByPublicKey(mOldd.getText().toString(), puk);
				String encrpyNewPwdStr =  RSAUtilsZFT.getEncodePwdByPublicKey(mPassww2.getText().toString(), puk);
				if(pin_pass==1){

					builder.put("oldpwd", encrpyOldPwdStr);
					builder.put("newpwd", encrpyNewPwdStr);
				}else{
					builder.put("oldpwd", mOldd.getText().toString());
					builder.put("newpwd", encrpyNewPwdStr);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			builder.put("oldpwd", mOldd.getText().toString());
			builder.put("newpwd", mPassww2.getText().toString());
		}


		BaseHttpClient.post(getBaseContext(), Default.peoInfoxsjiaoyipsw,
				builder, new JsonHttpResponseHandler() {

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
									finish();
									showCustomToast(json.getString("message"));

								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(ChangeTenderPasswordActivity.this, json.getInt("status"),message);
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
