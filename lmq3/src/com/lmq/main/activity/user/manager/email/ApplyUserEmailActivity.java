package com.lmq.main.activity.user.manager.email;

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
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;
import com.lmq.http.JsonHttpResponseHandler;

/**
 * 绑定邮箱
 * 
 */
public class ApplyUserEmailActivity extends BaseActivity implements OnClickListener {

	private String mEmail, emailStr;
	private EditText ed_bindemail;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.people_info_bindemail);

		Intent intent = getIntent();
		if (intent != null) {
			mEmail = intent.getExtras().getString("email");
		}

		TextView text = (TextView) findViewById(R.id.title);
		text.setText(R.string.bdemail);

		findViewById(R.id.back).setOnClickListener(this);
		ed_bindemail = (EditText) findViewById(R.id.ed_bindemail);
		ed_bindemail.setText(mEmail);
		findViewById(R.id.btn_sure).setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sure:

			if (ed_bindemail.getText().toString() != null) {
				emailStr = ed_bindemail.getText().toString();
				if (emailStr.equals("")) {
					showCustomToast(R.string.toast17);
					return;
				}
				if (!SystenmApi.isEmail(emailStr)) {
					showCustomToast(R.string.toast18);
					return;
				}
				doHttp();

			}

			break;
		case R.id.back:
			finish();
			break;
		}

	}

	public void doHttp() {

		JsonBuilder builder = new JsonBuilder();
		builder.put("email", emailStr);

		BaseHttpClient.post(getBaseContext(), Default.peopleinfoEmail, builder,
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
									showCustomToast(("提交邮箱成功！"));
									finish();
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(ApplyUserEmailActivity.this, json.getInt("status"),message);
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
