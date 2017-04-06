package com.lmq.main.activity.user.manager.phone;

import org.apache.http.Header;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;
import com.lmq.http.JsonHttpResponseHandler;

/**
 * 绑定手机号码
 * 
 */
public class ModifyUserApplyPhoneActivity extends BaseActivity implements
		OnClickListener {

	private EditText ed_newphonenumber = null; // 手机号
	private EditText mEditNumber = null; // 验证码
	private String mPhone;
	private String mNumber = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.people_info_updatephone);

		findViewById(R.id.back).setOnClickListener(this);
		TextView text = (TextView) findViewById(R.id.title);
		text.setText(R.string.updatephone);

		ed_newphonenumber = (EditText) findViewById(R.id.ed_newphonenumber);
		findViewById(R.id.btn_suree).setOnClickListener(this);

		if (Default.phoneverif == 2)// 如过开通 短信验证
		{
			findViewById(R.id.layy).setVisibility(View.VISIBLE);
			mEditNumber = (EditText) findViewById(R.id.ed_newnumber);
			findViewById(R.id.yzm).setOnClickListener(this);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.yzm:
			mPhone = ed_newphonenumber.getText().toString();
			doHttp();
			break;
		case R.id.btn_suree:
			mPhone = ed_newphonenumber.getText().toString();
			if (Default.phoneverif == 2)// 如过开通 短信验证
			{
				mNumber = mEditNumber.getText().toString();
			}
			if (mPhone.equals("")) {
				showCustomToast(R.string.toastphone);
			}
			{
				doHttp2();

			}
			break;
		}
	}

	public void doHttp() {

		JsonBuilder builder = new JsonBuilder();
		builder.put("phone", mPhone);

		BaseHttpClient.post(getBaseContext(), Default.peoInfoPhone, builder,
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
									showCustomToast(R.string.fsyzm);
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(ModifyUserApplyPhoneActivity.this, json.getInt("status"), message);
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

	public void doHttp2() {

		JsonBuilder builder = new JsonBuilder();
		builder.put("phone", mPhone);
		if (Default.phoneverif == 2)// 如过开通 短信验证
			builder.put("verify_code", mNumber);

		BaseHttpClient.post(getBaseContext(), Default.peoInfoPhone2, builder,
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
									showCustomToast(R.string.yzok);
									finish();
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(ModifyUserApplyPhoneActivity.this, json.getInt("status"), message);
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
