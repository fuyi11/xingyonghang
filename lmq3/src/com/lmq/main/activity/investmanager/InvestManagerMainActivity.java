package com.lmq.main.activity.investmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.investmanager.auto.AutoTenderActivity;
import com.lmq.main.activity.investmanager.debet.InvestManagerDebetActivity;
import com.lmq.main.activity.investmanager.invest.InvestManagerActivity;
import com.lmq.main.activity.investmanager.lhb.LHBManagerActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONObject;

public class InvestManagerMainActivity extends BaseActivity implements OnClickListener {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inverst_manager_layout);

		TextView text = (TextView) findViewById(R.id.title);
		text.setText(R.string.invest_manager);



		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.invest_manager_lhb).setOnClickListener(this);
		findViewById(R.id.invest_manager_tzgl).setOnClickListener(this);
		findViewById(R.id.invest_manager_zqzr).setOnClickListener(this);
		findViewById(R.id.invest_manager_zdtb).setOnClickListener(this);

	}

	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.back:
			finish(0);
			break;


			case R.id.invest_manager_lhb:
				//TODO 灵活宝界面跳转
				intent.setClass(InvestManagerMainActivity.this,LHBManagerActivity.class);
				startActivity(intent);
				break;
			case R.id.invest_manager_tzgl:
				//TODO  投资管理界面跳转
                intent.setClass(InvestManagerMainActivity.this,InvestManagerActivity.class);
                startActivity(intent);
				break;
			case R.id.invest_manager_zqzr:
				//TODO 债券转让界面跳转
                intent.setClass(InvestManagerMainActivity.this,InvestManagerDebetActivity.class);
                startActivity(intent);
				break;
			case R.id.invest_manager_zdtb:
				//TODO 自动投标界面跳转
				 intent.setClass(InvestManagerMainActivity.this,AutoTenderActivity.class);
	                startActivity(intent);
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
									SystenmApi.showCommonErrorDialog(InvestManagerMainActivity.this, json.getInt("status"),message);
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
