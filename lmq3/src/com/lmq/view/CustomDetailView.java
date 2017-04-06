package com.lmq.view;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;

public class CustomDetailView extends BaseActivity implements OnClickListener {

	private WebView contentWebView = null;
	private String html_str = "";
	private ArrayList<String> imageArray;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		html_str = getIntent().getStringExtra("html_str");
		imageArray = getIntent().getStringArrayListExtra("imageArray");
		String html = "<html><head><style type='text/css'>p{text-align: left;border-style:"
				+ " none;border-top-width: 2px;border-right-width: 2px;border-bottom-width: 2px;border-left-width: 2px;}"
				+ "img{height:auto;width: auto;width:100%;}</style></head><body>"
				+ html_str + "</body></html>";

		setContentView(R.layout.test);
		TextView titleView = (TextView) findViewById(R.id.title);
		titleView.setText("详细说明");
		findViewById(R.id.back).setOnClickListener(this);
		contentWebView = (WebView) findViewById(R.id.myweb);
		contentWebView.getSettings().setJavaScriptEnabled(true);
		contentWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8",
				null);

		contentWebView.addJavascriptInterface(new JavascriptInterfaceMethod(
				this), "imagelistner");
		contentWebView.setWebViewClient(new MyWebViewClient());

	}
	
	/**
	 * 获取幻灯片
	 */
	public void doHttp() {
		// RequestParams params = new RequestParams();
		JsonBuilder builder = new JsonBuilder();

		// 填充参数
		BaseHttpClient.post(CustomDetailView.this, Default.bannerPicDetail, builder,
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						super.onStart();

						if (Default.NEW_VERSION) {

							showLoadingDialogNoCancle("请稍后努力加载中...");
						}
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject json) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, json);
						dismissLoadingDialog();
						MyLog.e("statusCode=" + statusCode + "--headers="
								+ headers.toString() + "--json="
								+ json.toString());

						if (statusCode == 200) {
							try {
								if (json.getInt("status") == 1) {
									//initBannerData(json);
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(CustomDetailView.this, json.getInt("status"), message);


								}

								dismissLoadingDialog();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable,
								errorResponse);

						dismissLoadingDialog();
					}
				});

	}

	

	@JavascriptInterface
	private void addImageClickListner() {
		contentWebView.loadUrl("javascript:(function(){"
				+ "var objs = document.getElementsByTagName(\"img\"); "
				+ "for(var i=0;i<objs.length;i++)  " + "{"
				+ "    objs[i].onclick=function()  " + "    {  "
				+ "        window.imagelistner.openImage(this.src,i);  "
				+ "    }  " + "}" + "})()");
	}

	public class JavascriptInterfaceMethod {

		private Context context;

		public JavascriptInterfaceMethod(Context context) {
			this.context = context;
		}

		@JavascriptInterface
		public void openImage(String img, String i) {
			Intent intent = new Intent();
			intent.putStringArrayListExtra("imageArray", imageArray);
			intent.putExtra("img", img.replaceAll("\"", "\\\""));
			intent.setClass(CustomDetailView.this, ShowWebImageView.class);
			startActivity(intent);
			// overridePendingTransition(R.anim.hyp_in, R.anim.hyp_out);
		}
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {

			view.getSettings().setJavaScriptEnabled(true);

			super.onPageFinished(view, url);
			addImageClickListner();

		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			view.getSettings().setJavaScriptEnabled(true);

			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

			super.onReceivedError(view, errorCode, description, failingUrl);

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}

	}

}