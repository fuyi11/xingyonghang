package com.lmq.main.activity.user.manager.apply;

import org.apache.http.Header;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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

public class ApplyForLinesActivity extends BaseActivity implements
		OnClickListener {

	private EditText apply_money = null;
	private EditText apply_info = null;
	private String mMessage;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apply_for_lines);

		TextView text = (TextView) findViewById(R.id.title);
		text.setText("额度申请");

		findViewById(R.id.back).setOnClickListener(this);
		apply_money = (EditText) findViewById(R.id.apply_money);
		apply_info = (EditText) findViewById(R.id.apply_info);
		findViewById(R.id.submit_btn).setOnClickListener(this);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish(0);
			break;

		case R.id.submit_btn:

			String app_money_str = apply_money.getText().toString();
			String apply_info_str = apply_info.getText().toString();
			if (SystenmApi.isNullOrBlank(app_money_str)) {
				showCustomToast("请输入申请金额");
				return;
			}

			if (SystenmApi.isNullOrBlank(apply_info_str)) {
				showCustomToast("请输入申请说明");
				return;

			}

			doHttp();

			break;
		}
	}

	// 提交用户反馈到服务器
	public void doHttp() {

		// 构造请求参数
		JsonBuilder jsonBuilder = new JsonBuilder();

		jsonBuilder.put("apply_type", "1");
		jsonBuilder.put("apply_money", apply_money.getText().toString());
		jsonBuilder.put("apply_info", apply_info.getText().toString());

		// 获取当前手机系统信息
		BaseHttpClient.post(getBaseContext(), Default.credit_apply,
				jsonBuilder, new JsonHttpResponseHandler() {

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
							dismissLoadingDialog();
							if (statusCode == 200) {
								if (json.getInt("status") == 1) {

									showCustomToast(json.getString("message"));
									finish();
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(ApplyForLinesActivity.this, json.getInt("status"),message);
								}
							} else {
								showCustomToast(R.string.toast1);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
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
