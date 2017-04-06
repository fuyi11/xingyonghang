package com.lmq.main.activity.tools;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.MyLog;
import com.lmq.main.util.Default;

import org.apache.http.cookie.Cookie;

import java.util.List;


/**
 * 优惠却列表
 */
public class LMQWebViewActivity extends BaseActivity implements
        OnClickListener {


    private String title;
    private String url;
    private WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lmq_webview_layout);

        findViewById(R.id.back).setOnClickListener(this);

        Intent intent = getIntent();
        if (null != intent) {

            if (intent.hasExtra("title")) {

                title = intent.getStringExtra("title");
            }
            if (intent.hasExtra("url")) {

                url = intent.getStringExtra("url");
                url.replace("\\", "");
            }


        }

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void initView() {

        findViewById(R.id.back).setOnClickListener(this);
        TextView text = (TextView) findViewById(R.id.title);
        text.setText(title);

        webView = (WebView) findViewById(R.id.lmq_webView);

        List<Cookie> cookies = BaseHttpClient.getCookie();
        if (!cookies.isEmpty()) {
            CookieSyncManager.createInstance(webView.getContext());

            CookieManager cookieManager = CookieManager.getInstance();
//			cookieManager.removeSessionCookie();

            cookieManager.setAcceptCookie(true);
            cookieManager.removeAllCookie();
            //sync all the cookies in the httpclient with the webview by generating cookie string
            for (Cookie cookie : cookies) {
                String cookieString = cookie.getName() + "=" + cookie.getValue() + "; domain=" + cookie.getDomain() + ";path=" + cookie.getPath();
                MyLog.e("cookieStr === >", cookieString);
                cookieManager.setCookie(url, cookieString);

                CookieSyncManager.getInstance().sync();


            }
        }


        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient( new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl(url);


    }


    public void finish() {

        super.finish();

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back:
                finish();
                break;

        }

    }
}
