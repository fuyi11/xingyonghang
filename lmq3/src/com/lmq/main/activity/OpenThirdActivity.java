package com.lmq.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.util.Default;
import com.lmq.ybpay.YBPayActivity;

import org.apache.http.Header;
import org.json.JSONObject;

public class OpenThirdActivity extends BaseActivity implements OnClickListener {
	private TextView title;
	private LinearLayout card_photo;
	private Button btn_tijiao;

	private EditText edit_realname, edit_idcard;

	private String mRealName;
	private String mRealIdcard;

	private String req;
	private String sign;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.people_info_jinxingrz);

		title = (TextView) findViewById(R.id.title);
		title.setText("开通第三方账户");
		card_photo = (LinearLayout) findViewById(R.id.lv_photo);
		card_photo.setVisibility(View.GONE);
		btn_tijiao = (Button) findViewById(R.id.btn_tijiao);
		btn_tijiao.setText("绑定托管");
		btn_tijiao.setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);

		edit_realname = (EditText) findViewById(R.id.edit_realname);
		edit_idcard = (EditText) findViewById(R.id.edit_idcard);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:

			finish();
			break;

		case R.id.btn_tijiao:
			mRealName = edit_realname.getText().toString();
			mRealIdcard = edit_idcard.getText().toString();

			if (mRealName.equals("")) {
				showCustomToast("请输入真实姓名");
				return;
			}

			if (mRealIdcard.equals("")) {
				showCustomToast("请输入真实身份证号码");
				return;
			}

			Intent intent = new Intent(OpenThirdActivity.this, YBPayActivity.class);
			intent.putExtra("YB_TYPE", Default.TYPE_YB_REGISTER);

			intent.putExtra("real_name", mRealName);
			intent.putExtra("idcard", mRealIdcard);
			startActivity(intent);
			
			finish();

			break;
		default:
			break;
		}
	}

	public void doHttp() {

		JsonBuilder builder = new JsonBuilder();

		builder.put("real_name", mRealName);
		builder.put("idcard", mRealIdcard);

		MyLog.e("易宝注册请求路径" + Default.Yb_register);

		MyLog.e("易宝上传参数"+builder.toJsonString());

		BaseHttpClient.post(getBaseContext(), Default.Yb_register, builder,
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

				MyLog.e("易宝注册返回数据"+json.toString());

				try {

					if (statusCode == 200) {
						if (json.getInt("status") == 0) {
							if(json.has("req")){
								req = json.getString("req");
							}
							if(json.has("sign")){
								sign = json.getString("sign");
							}
							
							MyLog.e("req和sign的值", "req="+req+"------"+"sign="+sign);
							
							Intent intent = new Intent(OpenThirdActivity.this, YBPayActivity.class);
							intent.putExtra("YB_TYPE", Default.TYPE_YB_REGISTER);
							intent.putExtra("req", req);
							intent.putExtra("sign", sign);
							startActivity(intent);
							
							finish();

						} else {
							showCustomToast(json.getString("message"));
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
