package com.lmq.pay.jdpay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.MyLog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


/***
 * 京东支付
 * @author lmq
 *
 */
public class JDPayActivity extends BaseActivity implements OnClickListener {

	public static int JD_CODE = 4001;
	public static int JD_CODE_SUCCESS = 4002;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_jdpay);
		Intent intent = getIntent();
		String url = intent.getStringExtra("url");
		String info = intent.getStringExtra("info");

		MyLog.e("URL-->",url);
		MyLog.e("params-->",info);
		WebView webView = (WebView)findViewById(R.id.webview);
		webView.postUrl(url, EncodingUtils.getBytes(info, "utf-8"));
		webView.getSettings().setJavaScriptEnabled(true);

		webView.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.contains("inapp://popup")){
					setResult(JD_CODE_SUCCESS);
					JDPayActivity.this.finish();
					return false;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
		findViewById(R.id.back).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.back:
				finish();
				break;
		}
	}

}



