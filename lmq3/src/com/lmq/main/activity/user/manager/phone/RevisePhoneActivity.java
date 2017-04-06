package com.lmq.main.activity.user.manager.phone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;
import com.lmq.main.api.BaseActivity;

import org.apache.http.Header;
import org.json.JSONObject;

public class RevisePhoneActivity extends BaseActivity implements
        View.OnClickListener {


    private EditText ed_newphone,ed_yzm;

    private String phone;

    private  String yzm;




    private TimeCount time;

    /**发送验证码按钮*/
    private Button mSendPhoneNum;



    public RevisePhoneActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_revise_phone);

        initView();
    }

    private void initView() {

        ed_newphone = (EditText) findViewById(R.id.ed_newphone);
        ed_yzm = (EditText) findViewById(R.id.ed_yzm);

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.btn_yzm).setOnClickListener(this);
        findViewById(R.id.enter).setOnClickListener(this);

        TextView text = (TextView) findViewById(R.id.title);
        text.setText(R.string.peo_phone);
/*
        Intent intent = getIntent();

        phone = intent.getExtras().getString("phone");
*/
        mSendPhoneNum = (Button) findViewById(R.id.btn_yzm);
        mSendPhoneNum.setOnClickListener(this);

        time = new TimeCount(60000, 1000);
//        time.start();

    }

    @Override
    public void onClick(View v) {
        phone = ed_newphone.getText().toString();
        yzm = ed_yzm.getText().toString();


        switch (v.getId()) {

            case R.id.back:
                finish();
                break;
            case R.id.btn_yzm:

                if(phone.equals("") )
                {
                    showCustomToast("请输入手机号");
                    return;
                }else  if(phone.length()!=11){

                    showCustomToast("请输入正确的手机号");

                    return;
                }else{

                    time.start();
                    doHttpSendPhone(phone);
                }

                break;
            case R.id.enter:

                if(phone.equals("") )
                {
                    showCustomToast("请输入手机号");
                    return;
                }else if(phone.length()!=11){

                    showCustomToast("请输入正确的手机号");

                    return;
                }else if (yzm.equals("")){
                    showCustomToast("请输入验证码");
                    return;
                } else if (phone.length()!=11 && yzm.equals("")){
                    showCustomToast("请输入手机号和验证码");
                    return;
                }else{
                    doHttp1();


                }




                break;
        }
    }

    private void doHttp1() {{

        JsonBuilder builder = new JsonBuilder();
        builder.put("phone", phone);
        builder.put("verify_code", yzm);
       // builder.put("phone", phone);


        BaseHttpClient.post(getBaseContext(), Default.revisephone, builder,
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

                                    showCustomToast(R.string.newphone3);

                                    finish();
                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(RevisePhoneActivity.this, json.getInt("status"), message);
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

    private void doHttpSendPhone(String phone) {{

        JsonBuilder builder = new JsonBuilder();
        builder.put("phone", phone);

        BaseHttpClient.post(getBaseContext(), Default.registerPhone, builder,
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
                        dismissLoadingDialog();
                        try {
                            String msg = json.getString("message");
                            showCustomToast(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        dismissLoadingDialog();
                        showCustomToast(responseString);

                    }

                });

    }
    }


    /* 倒计时类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {

            mSendPhoneNum.setText("重新获取");
            mSendPhoneNum.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            mSendPhoneNum.setClickable(false);
            mSendPhoneNum.setText(millisUntilFinished / 1000 + "秒");
        }
    }


}
