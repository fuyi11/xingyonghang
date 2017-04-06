package com.lmq.main.activity.investmanager.borrowmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
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


public class LendMoneyDialogDoerase extends BaseActivity implements
        View.OnClickListener {

    private TextView titleView;
    private TextView tv_content;
    private String bid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lendmanagerhk_dialog_layout);
//        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Intent intent = getIntent();

        if (intent.hasExtra("bid")) {

            bid = intent.getStringExtra("bid");
            MyLog.e("得到的bid======",bid);

        }

        initView();


    }

    private void initView() {

        findViewById(R.id.dialog_cancle).setOnClickListener(this);
        findViewById(R.id.dialog_submit).setOnClickListener(this);
        titleView = (TextView) findViewById(R.id.dialog_title);
        titleView.setText("信息");
        tv_content = (TextView)findViewById(R.id.content);

        tv_content.setText("撤销第"+bid+"号借款标？");

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
                doHttpLendMoney();

                break;

        }
    }

    private void doHttpLendMoney() {

        JsonBuilder builder = new JsonBuilder();
        builder.put("bid",bid);

//        BaseHttpClient.NO_RAS=true;
        BaseHttpClient.post(LendMoneyDialogDoerase.this, Default.DOERASE, builder,
                new JsonHttpResponseHandler() {

                    @Override
                     public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
//                        showLoadingDialogNoCancle(getResources().getString(
//                                R.string.toast2));
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

                                    showCustomToast(json.getString("mssage"));
                                    finish();


                                } else if (json.getInt("status") == 0) {

                                    showCustomToast(json.getString("mssage"));
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
