package com.lmq.main.activity.investmanager.borrowmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.view.ViewGroup;
import android.widget.TextView;

import com.xyh.R;

import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONObject;



public class LendMoneyDialog extends BaseActivity implements
        View.OnClickListener {

    private TextView titleView;
    private TextView tv_content;

    private String sort;
    private String bid;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lendmanagerhk_dialog_layout);
//        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Intent intent = getIntent();
        if (intent.hasExtra("sort")) {

            sort = intent.getStringExtra("sort");

        }

        if (intent.hasExtra("bid")) {

            bid = intent.getStringExtra("bid");

        }

        initView();


    }

    private void initView() {

        findViewById(R.id.dialog_cancle).setOnClickListener(this);
        findViewById(R.id.dialog_submit).setOnClickListener(this);
        titleView = (TextView) findViewById(R.id.dialog_title);
        titleView.setText("还款通知");
        tv_content = (TextView)findViewById(R.id.content);

        tv_content.setText("对第" +sort + "期进行还款?");

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.dialog_cancle:
                finish();
                break;
            case R.id.dialog_submit:

                if (Default.IS_Qdd){

                    doSQHttpLendMoney();
                }else{

                    doHttpLendMoney();

                }

                break;
        }
    }

    private void doHttpLendMoney() {

        JsonBuilder builder = new JsonBuilder();
        builder.put("sort",sort);
        builder.put("bid",bid);

        BaseHttpClient.post(LendMoneyDialog.this, Default.DOPAY, builder,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject json) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, json);

                        try {
                            if (statusCode == 200) {
                                if (json.getInt("status") == 1){

                                    showCustomToast(json.getString("message"));
                                    finish();

                                }else {


                                    showCustomToast(json.getString("message"));
                                    finish();

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
                    }

                });
    }

    private void doSQHttpLendMoney() {

        JsonBuilder builder = new JsonBuilder();
        builder.put("sort",sort);
        builder.put("bid",bid);

        BaseHttpClient.post(LendMoneyDialog.this, Default.SPDPPAY, builder,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject json) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, json);

                        try {
                            MyLog.e("123", json.toString());
                            if (statusCode == 200) {
                                if (json.getInt("status") == 1) {


                                    showCustomToast(json.getString("message"));
                                    finish();

                                } else if (json.getInt("status") == 0) {

                                    showCustomToast(json.getString("message"));
                                    finish();

                                } else {
                                    showCustomToast(json.getString("message"));
                                    finish();
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
                    }

                });
    }

}