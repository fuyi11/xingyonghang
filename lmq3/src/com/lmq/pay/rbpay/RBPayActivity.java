package com.lmq.pay.rbpay;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.util.Default;

/***
 * 汇付托管注册界面
 * 
 * @author zhaoshuai
 *
 */
public class RBPayActivity extends BaseActivity implements OnClickListener {
	/***
	 * 开通汇富注册按钮
	 * 
	 * 
	 */

	private WebView webView;
	private TextView titleView;
	private int hf_types;
	private String server_url;
	private HashMap<String, String> intentParams;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.rongbao_pay);
		titleView = (TextView) findViewById(R.id.title);
//		titleView.setText("融宝支付");
		findViewById(R.id.back).setOnClickListener(this);

		webView = (WebView) findViewById(R.id.webView);
		webView.setWebViewClient(new HFWebClient());

		WebSettings webSettings = webView.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		intentParams = new HashMap<String, String>();
		initViewInfo();

		//获取融宝支付参数，不能在OnResume（）中获取，否则会回到第重新充值界面
		getHFCustomerParas();

	}

	private void initViewInfo() {

		if (getIntent() != null) {
			Intent intent = getIntent();
			cheangeInfo(intent);

		}
	}

	private void cheangeInfo(Intent intent) {

		intentParams.clear();
		if (intent.hasExtra("amoney")) {
			intentParams.put("amoney", intent.getStringExtra("amoney"));
		}

		if (intent.hasExtra("banks")) {
			intentParams.put("banks", intent.getStringExtra("banks"));
		}

		MyLog.e(intentParams.toString());

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}

	public String[] getInfo() {

		return null;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	/**
	 * 从服务器获取 当前客户在 汇付平台注册的平台信息
	 */

	private void getHFCustomerParas() {
		JsonBuilder builder = new JsonBuilder();
		Iterator<String> iterator = intentParams.keySet().iterator();

		while (iterator.hasNext()) {
			String key = iterator.next();
			String value = intentParams.get(key);
			builder.put(key, value);

		}

		String imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
				.getDeviceId();
		String simno = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
				.getSimSerialNumber();
		builder.put("cardImei", imei);
		builder.put("cardSim", simno);

		BaseHttpClient.post(getBaseContext(), Default.pay_rongbao_type,
				builder, new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						showLoadingDialog("正在初始化融宝支付环境...");
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);

						if (statusCode == 200) {
							MyLog.e("开户返回值:" + response.toString());
							if (response != null) {

								if (response.optInt("status") == 1) {

									MyLog.e("最终参数"
											+ getRequestHFParamsStr(response));
									starWebView(getRequestHFParamsStr(response));

								} else if(response.has("is_jumpmsg")){
									showCustomToast(response.optString("is_jumpmsg"));
									finish();
								}else{
									showCustomToast(response
											.optString("message"));
									finish();
								}

							}

						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, responseString,
								throwable);
					}
				});

	}

	private void starWebView(String params) {

		/***
		 * 填充汇付接口 参数
		 */

		webView.loadUrl(Default.RB_REAL_URL + "portal?" + params);

	}

	/***
	 * 获取用户开户状态
	 */
	private void bindingStatus() {

		BaseHttpClient.post(getBaseContext(), server_url, new JsonBuilder(),
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();

					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);

						if (statusCode == 200) {
							if (response != null) {
								MyLog.e("汇付返回值:" + response.toString());
								if (response.optInt("status") == 1) {

								} else {
									showCustomToast(response
											.optString("message"));
								}

							}

						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, responseString,
								throwable);
					}
				});

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			showCustomToast(msg.getData().getString("info"));
		}

	};

	private String getRequestHFParamsStr(JSONObject json) {

		StringBuffer paras = new StringBuffer();

		Iterator<String> iterator = json.keys();

		while (iterator.hasNext()) {

			try {
				String key = iterator.next();
				String value = json.getString(key);

//				try {
//					if (!key.equals("sign")) {
//						value = URLEncoder.encode(value, "utf-8");
//					}
//
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

				if (!key.equals("status")) {
					paras.append(key).append("=" + value).append("&");
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		paras.replace(paras.length() - 1, paras.length(), "");

		return paras.toString();

	}

	class HFWebClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			dismissLoadingDialog();



			MyLog.e("onLoadResource" + url);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			// TODO Auto-generated method stub
			super.onReceivedSslError(view, handler, error);
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onLoadResource(view, url);
			MyLog.e("onLoadResource" + url);
		}

		@Override
		public void onFormResubmission(WebView view, Message dontResend,
				Message resend) {
			// TODO Auto-generated method stub
			super.onFormResubmission(view, dontResend, resend);

		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if(url.contains("inapp://popup")){


				RBPayActivity.this.showCustomToast(view.getTitle());

				RBPayActivity.this.finish();
				return false;
			}
			return super.shouldOverrideUrlLoading(view, url);
		}
	}

}
