package com.lmq.zftpay;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;
import com.lmq.main.util.RSAUtilsZFT;

import org.apache.http.Header;
import org.json.JSONObject;

import java.security.PublicKey;

public class ZFTRevisePhoneActivity extends BaseActivity implements
        View.OnClickListener {


    private EditText ed_newphone,ed_paypsw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_revise_phone_zft);

        initView();
    }

    private void initView() {

        ed_newphone = (EditText) findViewById(R.id.ed_newphone);
        ed_paypsw = (EditText) findViewById(R.id.ed_paypsw);

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.enter).setOnClickListener(this);

        TextView text = (TextView) findViewById(R.id.title);
        text.setText(R.string.peo_phone);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back:
                finish();

                break;
            case R.id.enter:

                if(TextUtils.isEmpty(ed_newphone.getText().toString()))
                {
                    showCustomToast("请输入手机号");
                    return;
                }
                if (!SystenmApi.isMobileNO(ed_newphone.getText().toString())) {
                    showCustomToast("请输入合法的手机号");

                }
                if(TextUtils.isEmpty(ed_paypsw.getText().toString()))
                {
                    showCustomToast("请输入支付密码");
                    return;
                }

                 doHttp1();

                break;
        }
    }

    private void doHttp1() {{

        JsonBuilder builder = new JsonBuilder();
        PublicKey puk;
        try {
            if(Default.ZFT_Environment){
                puk = RSAUtilsZFT.getPublicKey(Default.TEST_ZFT_PUBLIC);
                MyLog.e("支付通测试环境加密");
            }else {
                puk = RSAUtilsZFT.getPublicKey(Default.ZFT_PUBLIC);
                MyLog.e("支付通正式环境加密");
            }
            String encrpyNewPwdStr =  RSAUtilsZFT.getEncodePwdByPublicKey(ed_paypsw.getText().toString(), puk);
            builder.put("mobile", ed_newphone.getText().toString());
            builder.put("payPwd", encrpyNewPwdStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        BaseHttpClient.post(getBaseContext(), Default.changeMobile, builder,
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

                                    showCustomToast(json.getString("message"));

                                    finish();
                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(ZFTRevisePhoneActivity.this, json.getInt("status"), message);
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


}
