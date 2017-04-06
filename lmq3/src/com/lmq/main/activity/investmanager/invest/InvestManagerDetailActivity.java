package com.lmq.main.activity.investmanager.invest;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONObject;

public class InvestManagerDetailActivity extends BaseActivity implements OnClickListener {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invest_hk_detail_layout);

		TextView text = (TextView) findViewById(R.id.title);
		text.setText("还款详情");

		findViewById(R.id.back).setOnClickListener(this);


	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish(0);
			break;



		}
	}

	// 提交用户反馈到服务器
	public void checkNewVersion() {

		BaseHttpClient.post(getBaseContext(), Default.version, null,
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
								// 没有新版本
								if (json.getInt("status") == 1) {
									// initData(json);
									// 获取新版本
									showCustomToast(json.getString("message"));
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(InvestManagerDetailActivity.this, json.getInt("status"),message);
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

	public void finish() {
		super.finish();
	}

}
