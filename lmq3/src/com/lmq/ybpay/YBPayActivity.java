package com.lmq.ybpay;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
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

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/***
 * 易宝支付托管功能
 *
 * @author zhaoshuai
 */
public class YBPayActivity extends BaseActivity implements OnClickListener {
    /***
     * 开通易宝注册按钮
     */

    private WebView webView;
    private TextView titleView;
    private int yb_types;
    private String server_url = "";
    private String post_yb_url = "";
    private HashMap<String, String> intentParams;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.yb_pay_layou);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText(R.string.yb_register);
        findViewById(R.id.back).setOnClickListener(this);

        webView = (WebView) findViewById(R.id.webView);

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
                cookieManager.setCookie(Default.ip, cookieString);

                CookieSyncManager.getInstance().sync();


            }
        }

        webView.setWebViewClient(new LMQWebClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        intentParams = new HashMap<String, String>();
        initViewInfo();
        getYBPayParams();

    }

    private void initViewInfo() {

        if (getIntent() != null) {
            Intent intent = getIntent();
            if (intent.hasExtra("YB_TYPE")) {
                yb_types = intent.getIntExtra("YB_TYPE", -1);
                cheangeInfo(yb_types, intent);

            }

        }
    }

    private void cheangeInfo(int types, Intent intent) {

        switch (types) {

            /**
             * 注册易宝调用接口
             */
            case Default.TYPE_YB_REGISTER:

                titleView.setText(R.string.yb_register);
                server_url = Default.Yb_register;
                post_yb_url = "toRegister";
                intentParams.clear();

                if (intent.hasExtra("real_name")) {
                    intentParams.put("real_name", intent.getStringExtra("real_name"));
                }

                if (intent.hasExtra("idcard")) {
                    intentParams.put("idcard", intent.getStringExtra("idcard"));
                }

                break;

            case Default.TYPE_YB_CHARGE:
                titleView.setText(R.string.yb_charge);
                server_url = Default.Yb_charge;
                post_yb_url = "toRecharge";
                intentParams.clear();

                if (intent.hasExtra("money")) {
                    intentParams.put("money", intent.getStringExtra("money"));
                }

                break;

            case Default.TYPE_YB_WITHDRAW:
                titleView.setText(R.string.yb_withDraw);
                server_url = Default.Yb_withdraw;
                post_yb_url = "toWithdraw";
                intentParams.clear();

                if (intent.hasExtra("amount")) {
                    intentParams.put("amount", intent.getStringExtra("amount"));
                }
                if (intent.hasExtra("withdraw_type")) {
                    intentParams.put("withdraw_type", intent.getStringExtra("withdraw_type"));
                }
                if (intent.hasExtra("bank_id")) {
                    intentParams.put("bank_id", intent.getStringExtra("bank_id"));
                }

                break;

            case Default.TYPE_YB_BINDBANK:
                titleView.setText("绑定银行卡");
                server_url = Default.Yb_bindbankcard;
                post_yb_url = "toBindBankCard";
                intentParams.clear();

                break;

            //散标投资
            case 4:
                titleView.setText("投标");
                server_url = Default.tzListItem4;
                post_yb_url = "toCpTransaction";
                intentParams.clear();
                if (intent.hasExtra("borrow_id")) {
                    intentParams.put("borrow_id", intent.getStringExtra("borrow_id"));
                }
                if (intent.hasExtra("money")) {
                    intentParams.put("money", intent.getStringExtra("money"));
                }
                if (intent.hasExtra("borrow_pass")) {
                    intentParams.put("borrow_pass", intent.getStringExtra("borrow_pass"));
                }
                if (intent.hasExtra("type")) {
                    intentParams.put("type", intent.getStringExtra("type"));
                }
                if (intent.hasExtra("coupon_id")) {
                    intentParams.put("coupon_id", intent.getStringExtra("coupon_id"));
                }
                break;

            //企业直投定投宝
            case 5:
                titleView.setText("投标");
                server_url = Default.tztListItem4;
                post_yb_url = "toCpTransaction";
                intentParams.clear();
                if (intent.hasExtra("borrow_id")) {
                    intentParams.put("borrow_id", intent.getStringExtra("borrow_id"));
                }
                if (intent.hasExtra("invest_type")) {
                    intentParams.put("invest_type", intent.getStringExtra("invest_type"));
                }
                if (intent.hasExtra("num")) {
                    intentParams.put("num", intent.getStringExtra("num"));
                }
                if (intent.hasExtra("type")) {
                    intentParams.put("type", intent.getStringExtra("type"));
                }
                if (intent.hasExtra("coupon_id")) {
                    intentParams.put("coupon_id", intent.getStringExtra("coupon_id"));
                }
                break;

            //债券转让
            case 6:
                titleView.setText("债券转让");
                server_url = Default.debt_investmoney;
                post_yb_url = "toCpTransaction";
                intentParams.clear();
                if (intent.hasExtra("id")) {
                    intentParams.put("id", intent.getStringExtra("id"));
                }
                if (intent.hasExtra("money")) {
                    intentParams.put("money", intent.getStringExtra("money"));
                }

                break;

            //灵活宝
            case 7:
                titleView.setText("灵活宝");
                server_url = Default.flexible_save;
                post_yb_url = "toCpTransaction";
                intentParams.clear();
                if (intent.hasExtra("bao_id")) {
                    intentParams.put("bao_id", intent.getStringExtra("bao_id"));
                }
                if (intent.hasExtra("money")) {
                    intentParams.put("money", intent.getStringExtra("money"));
                }

                break;

            //灵活宝回款项目提前赎回
            case 8:
                titleView.setText("灵活宝提前赎回");
                server_url = Default.redeemSave;
                post_yb_url = "toCpTransaction";
                intentParams.clear();
                if (intent.hasExtra("batch")) {
                    intentParams.put("batch", intent.getStringExtra("batch"));
                }
                if (intent.hasExtra("fredeemamount")) {
                    intentParams.put("fredeemamount", intent.getStringExtra("fredeemamount"));
                }

                break;
            //修改手机号码
            case 9:
                titleView.setText("修改手机号");
                server_url = Default.Yb_Changephone;
                post_yb_url = "toResetMobile";
                intentParams.clear();

                break;

            case Default.TYPE_YB_AUTOINVEST:
                titleView.setText("开启自动投标授权");
                server_url = Default.Yb_authorize;
                post_yb_url = "toAuthorizeAutoTransfer";
                intentParams.clear();

                break;
            case Default.TYPE_YB_CLOSE_AUTOINVEST:
                titleView.setText("关闭自动投标授权");
                server_url = Default.Yb_closeInvest;
                post_yb_url = "toAuthorizeAutoTransfer";
                intentParams.clear();

                break;

            default:

                titleView.setText("易宝");

                break;
        }
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
     * 从服务器获取 当前    客户在 汇付平台注册的平台信息
     */


    private void getYBPayParams() {
        JsonBuilder builder = new JsonBuilder();
        Iterator<String> iterator = intentParams.keySet().iterator();

        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = intentParams.get(key);
            builder.put(key, value);

        }
        BaseHttpClient.post(getBaseContext(), server_url, builder,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
                        showLoadingDialog("正在初始化易宝支付环境...");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, response);
                        dismissLoadingDialog();
                        if (statusCode == 200) {

                            if (response != null) {

                                if (server_url == Default.Yb_register) {
                                    if (response.optInt("status") == 0) {

                                        MyLog.e("开户返回值:" + response.toString());
                                        MyLog.e("最终参数" + getParamsFromServer(response));
                                        startWebView(getParamsFromServer(response));

                                    } else {
                                        showCustomToast(response
                                                .optString("message"));
                                        finish();
                                    }
                                } else {
                                    if (response.optInt("status") == 1) {

                                        MyLog.e("开户返回值:" + response.toString());
                                        MyLog.e("最终参数" + getParamsFromServer(response));
                                        startWebView(getParamsFromServer(response));

                                    } else {
                                        showCustomToast(response
                                                .optString("message"));
                                        finish();
                                    }
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
                        dismissLoadingDialog();
                    }
                });

    }

    private void startWebView(String params) {

        /***
         * 填充汇付接口 参数
         */


        MyLog.e("参数--->" + params);
        webView.postUrl(Default.YB_POST_URL + post_yb_url,
                EncodingUtils.getBytes(params, "utf-8"));


    }



    private String getParamsFromServer(JSONObject json) {


        StringBuffer paras = new StringBuffer();
        Iterator<String> iterator = json.keys();

        while (iterator.hasNext()) {


            try {
                String key = iterator.next();
                String value = json.getString(key);

                if (!key.equals("status")) {
                    paras.append(URLEncoder.encode(key, "UTF-8")).append("=" + URLEncoder.encode(value, "UTF-8")).append("&");
                }


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }


        paras.replace(paras.length() - 1, paras.length(), "");

        return paras.toString();


    }

    class LMQWebClient extends WebViewClient {

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

            if (url.contains("inapp://popup")) {
                YBPayActivity.this.showCustomToast(view.getTitle());
                YBPayActivity.this.finish();
                return false;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }


    }

}