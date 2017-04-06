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
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;

/**
 * 信用额度申请
 * 
 */
public class ApplyForMoneyLimitActivity extends BaseActivity implements OnClickListener {

	private String mMoney, mApplication;
	private EditText ed_money,other_application;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.borrow_credit_application);

		TextView text = (TextView) findViewById(R.id.title);
		text.setText("借款信用额度");

		findViewById(R.id.back).setOnClickListener(this);
		ed_money = (EditText) findViewById(R.id.ed_money);
		other_application = (EditText) findViewById(R.id.other_application);

		findViewById(R.id.btn_sure).setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sure:

			if (ed_money.getText() != null
			    && ed_money.getText().length() != 0) {

				mMoney = ed_money.getText().toString();

	        } else {

		         showCustomToast("申请金额不能为空");
		         return;
	        }
			
			if (other_application.getText() != null
					&& other_application.getText().length() != 0) {
				
				mApplication = other_application.getText().toString();
				
			} else {
				
				showCustomToast("申请说明不可为空");
				return;
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
		builder.put("apply_type", 1);
		builder.put("apply_money", mMoney);
		builder.put("apply_info", mApplication);

		BaseHttpClient.post(getBaseContext(), Default.credit_apply, builder,
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
									showCustomToast(("额度申请成功！"));
									finish();
								}else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(ApplyForMoneyLimitActivity.this, json.getInt("status"),message);
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
