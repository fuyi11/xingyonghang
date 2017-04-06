package com.lmq.main.activity.news;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.item.newsItem;
import com.lmq.main.util.Data;
import com.lmq.main.util.Default;
import com.lmq.http.JsonHttpResponseHandler;

public class itemNewsActivity extends BaseActivity implements OnClickListener {
	public static String source;
	private TextView name;
	private TextView time;
	private newsItem mItem;
	private WebView webview;
	private WebView webview2;
	private long itemId;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.news);

		name = (TextView) findViewById(R.id.newsName);
		time = (TextView) findViewById(R.id.newsTime);

		TextView text = (TextView) findViewById(R.id.title);
		text.setText(R.string.lhb_detail_title);

		webview = (WebView) findViewById(R.id.web);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		
		webview2 = (WebView) findViewById(R.id.web2);
		webview2.loadUrl(Default.ip+"/Mobile/appnewale?id="+itemId);
		webview2.getSettings().setJavaScriptEnabled(true);
		webview2.getSettings().setAllowFileAccess(true); 
		webview2.getSettings().setSupportMultipleWindows(true);
		webview2.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		webview2.getSettings().setAppCacheEnabled(true);
		webview2.setScrollBarStyle(0);

		findViewById(R.id.back).setOnClickListener(this);

		{
			if (Default.IS_SHOW_NESW_OR_NOTICE) {
				doShowItemhttp(Default.showNoticeId);
			} else {
				doShowItemhttp(Default.showNewsId);
			}
		}

	}

	private void updateData(JSONObject json) {
		try {

			name.setText(json.getString("title"));
			time.setText(json.getString("art_time"));
			source = json.getString("art_content");

			// doText();

			webview.loadDataWithBaseURL(Default.ip, source, "text/html",
					"utf-8", null);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish(0);
			break;
		}
	}

	public void doText() {

		new Thread(new Runnable() {
			public void run() {
				ImageGetter getter = new ImageGetter() {

					public Drawable getDrawable(String source) {
						Drawable drawable = null;
						try {
							URL url = new URL(Default.ip + source);
							String srcName = source.substring(
									source.lastIndexOf("/") + 1,
									source.length());
							InputStream is = null;
							try {
								is = url.openStream();
							} catch (IOException e) {
								e.printStackTrace();
							}
							drawable = Drawable.createFromStream(is, srcName);
							drawable.setBounds(0, 0,
									drawable.getIntrinsicWidth(),
									drawable.getIntrinsicHeight());
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}

						return drawable;
					}
				};
				Spanned spanned = Html.fromHtml(source, getter, null);
				Map map = new HashMap<String, Object>();
				map.put("spanned", spanned);
				map.put("info", source);

				Message msg = new Message();
				msg.what = 1;
				msg.obj = map;
				handler.sendMessage(msg);

			}
		}).start();
	}

	public void finish() {
		Default.IS_SHOW_NESW_OR_NOTICE = false;
		super.finish();
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				HashMap map = (HashMap) msg.obj;

				Spanned getter = (Spanned) map.get("spanned");
				// info.setText(getter);
				break;
			case 3:
				updateData(Data.noticeListItemJson);
				break;
			}
		}

	};

	// private void doShowItemhttp(long id)
	// {
	// showLoadingDialogNoCancle(getResources().getString(R.string.toast2));
	//
	// JsonBuilder builder = new JsonBuilder();
	// builder.put("id", id);
	// new BaseModel(null, Default.newsListItem, builder)
	// .setConnectionResponseLinstener(new ConnectResponseListener()
	// {
	// public void onConnectResponseCallback(JSONObject json)
	// {
	// dismissLoadingDialog();
	// Data.noticeListItemJson = json;
	// handler.sendEmptyMessage(3);
	// }
	//
	// @Override
	// public void onFail(JSONObject json)
	// {
	// dismissLoadingDialog();
	// try
	// {
	// Message msg = new Message();
	// msg.arg1 = 2;
	// Bundle bundel = new Bundle();
	// bundel.putString("info", json.getString("message"));
	// msg.setData(bundel);
	// handler.sendMessage(msg);
	// }
	// catch (JSONException e)
	// {
	// e.printStackTrace();
	// }
	// }
	// });
	//
	// }
	private void doShowItemhttp(long id) {
		JsonBuilder builder = new JsonBuilder();
		builder.put("id", id);

		BaseHttpClient.post(getBaseContext(),
				(Default.IS_SHOW_NESW_OR_NOTICE ? Default.notice
						: Default.newsListItem), builder,
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
									Data.noticeListItemJson = json;
									handler.sendEmptyMessage(3);
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(itemNewsActivity.this, json.getInt("status"),message);
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
