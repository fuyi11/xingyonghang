package com.lmq.view;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
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

public class ShowBannerViewDetail extends BaseActivity implements
		OnClickListener {

	private WebView contentWebView = null;
	private ArrayList<String> imageArray;
	private int id;
	private TextView titleView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		id = getIntent().getIntExtra("id", -1);
		imageArray = new ArrayList<String>();

		setContentView(R.layout.test);
		titleView = (TextView) findViewById(R.id.title);
		titleView.setText("");
		findViewById(R.id.back).setOnClickListener(this);
		contentWebView = (WebView) findViewById(R.id.myweb);
		contentWebView.getSettings().setJavaScriptEnabled(true);

		contentWebView.addJavascriptInterface(new JavascriptInterfaceMethod(
				this), "imagelistner");
		contentWebView.setWebViewClient(new MyWebViewClient());

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		doHttp();
	}

	/**
	 * 获取幻灯片
	 */
	public void doHttp() {
		// RequestParams params = new RequestParams();
		JsonBuilder builder = new JsonBuilder();
		builder.put("id", id);

		// 填充参数
		BaseHttpClient.post(ShowBannerViewDetail.this, Default.bannerPicDetail,
				builder, new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						super.onStart();

						showLoadingDialogNoCancle("请稍后努力加载中...");
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
									imageArray.clear();
									contentWebView.loadDataWithBaseURL(null,
											formateHtmlStr(json
													.getString("content")),
											"text/html", "utf-8", null);
									titleView.setText(json.getString("title"));
									if (json.has("arrimg_path")) {

										JSONArray array = json
												.getJSONArray("arrimg_path");
										for (int i = 0; i < array.length(); i++) {
											imageArray.add(array.getString(i));
										}
									}

								} else {
									showCustomToast(json.getString("message"));
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
				+ "        window.imagelistner.openImage(this.src);  "
				+ "    }  " + "}" + "})()");
	}

	public class JavascriptInterfaceMethod {

		private Context context;

		public JavascriptInterfaceMethod(Context context) {
			this.context = context;
		}

		@JavascriptInterface
		public void openImage(String img) {
			// showCustomToast(img);
			Intent intent = new Intent();
			intent.putStringArrayListExtra("imageArray", imageArray);
			intent.putExtra("img", img.replaceAll("\"", "\\\""));
			intent.setClass(ShowBannerViewDetail.this, ShowWebImageView.class);
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

	private String formateHtmlStr(String htmlstr) {

		return "<html><head><style type='text/css'>p{text-align: center;border-style:"
				+ " none;border-top-width: 2px;border-right-width: 2px;border-bottom-width: 2px;border-left-width: 2px;}"
				+ "img{height:auto;width: auto;width:100%;}</style></head><body>"
				+ htmlstr + "</body></html>";
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