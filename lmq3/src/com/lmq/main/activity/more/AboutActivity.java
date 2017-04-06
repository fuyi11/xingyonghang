package com.lmq.main.activity.more;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPopupWindow;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;

import java.util.ArrayList;

public class AboutActivity extends BaseActivity implements OnClickListener {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_new);

		TextView text = (TextView) findViewById(R.id.title);
		text.setText(R.string.peo_info12);

		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.company_site).setOnClickListener(this);
		findViewById(R.id.company_tel).setOnClickListener(this);
		findViewById(R.id.check_new_version).setOnClickListener(this);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish(0);
			break;

		case R.id.company_site:

			ActivityInfo activityInfo = SystenmApi
					.getBrowserApp(getApplicationContext());
			if (activityInfo != null) {
				Uri uri = Uri.parse(Default.ip);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				intent.setClassName(activityInfo.packageName, activityInfo.name);
				startActivity(intent);
			} else {
				showCustomToast("手机没有浏览器，无法访问");
			}

			break;
		case R.id.company_tel:
			Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ this.getString(R.string.about_info3)));
			// intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent2);

			break;
		case R.id.check_new_version:
			//checkNewVersion();


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
									SystenmApi.showCommonErrorDialog(AboutActivity.this, json.getInt("status"),message);
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
