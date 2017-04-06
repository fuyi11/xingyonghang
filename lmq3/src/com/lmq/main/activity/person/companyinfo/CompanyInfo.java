package com.lmq.main.activity.person.companyinfo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class CompanyInfo extends BaseActivity implements OnClickListener {


    private TextView company_name;
    private TextView company_no;
    private TextView company_person;
    private TextView company_idno;
    private TextView company_money;
    private TextView company_address;
    private TextView company_borrow_money;
    private TextView company_borrow_time;
    private TextView company_borrow_use;
    private TextView company_hk_from;

    private boolean has_info;

    private HashMap<String, String> returnMap;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_data_1_layout);

        TextView text = (TextView) findViewById(R.id.title);
        text.setText("企业资料");




        if (null != getIntent()) {

            has_info = getIntent().getBooleanExtra("has_info", false);
        }

        company_name = (TextView) findViewById(R.id.company_name);
        company_no = (TextView) findViewById(R.id.company_no);
        company_person = (TextView) findViewById(R.id.company_person);
        company_idno = (TextView) findViewById(R.id.company_idno);
        company_money = (TextView) findViewById(R.id.company_money);
        company_address = (TextView) findViewById(R.id.company_address);
        company_borrow_money = (TextView) findViewById(R.id.company_borrow_money);
        company_borrow_time = (TextView) findViewById(R.id.company_borrow_time);
        company_borrow_use = (TextView) findViewById(R.id.company_borrow_use);
        company_hk_from = (TextView) findViewById(R.id.company_hk_from);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);


    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish(0);
                break;
            case R.id.submit:
                has_info = false;
//                uploadAndgetinfo(has_info);
                checkUsreInput();
                break;


        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(has_info){
            uploadAndgetinfo(has_info);
        }


    }

    private void checkUsreInput() {

        if (SystenmApi.isNullOrBlank(company_name.getText().toString())) {

            showCustomToast("请填写公司名称");
            return ;

        }

        if (SystenmApi.isNullOrBlank(company_no.getText().toString())) {

            showCustomToast("请填公司注册号");
            return ;

        }

        if (SystenmApi.isNullOrBlank(company_person.getText().toString())) {

            showCustomToast("请填写企业法人");
            return ;

        }

        if (SystenmApi.isNullOrBlank(company_idno.getText().toString())) {

            showCustomToast("请填写企业法人的身份证号码");
            return ;

        }

        if (SystenmApi.isNullOrBlank(company_money.getText().toString())) {

            showCustomToast("请填写企业注册资金");
            return ;

        }

        if (SystenmApi.isNullOrBlank(company_address.getText().toString())) {

            showCustomToast("请填写企业注册地址");
            return ;

        }

        if (SystenmApi.isNullOrBlank(company_borrow_money.getText().toString())) {

            showCustomToast("请填写借款金额");
            return ;

        }
        if (SystenmApi.isNullOrBlank(company_borrow_use.getText().toString())) {

            showCustomToast("请填写借款用途");
            return ;

        }
        if (SystenmApi.isNullOrBlank(company_hk_from.getText().toString())) {

            showCustomToast("请填写还款来源");
            return ;

        }


         returnMap = new HashMap<String, String>();


        returnMap.put("business_name", company_name.getText().toString());
        returnMap.put("bianhao", company_no.getText().toString());
        returnMap.put("legal_person", company_person.getText().toString());
        returnMap.put("idcard", company_idno.getText().toString());
        returnMap.put("registered_capital", company_money.getText().toString());
        returnMap.put("city", company_address.getText().toString());
        returnMap.put("bid_money", company_borrow_money.getText().toString());
        returnMap.put("bid_duration", company_borrow_time.getText().toString());
        returnMap.put("use_type", company_borrow_use.getText().toString());
        returnMap.put("repay_source", company_hk_from.getText().toString());

        uploadAndgetinfo(has_info);


    }

    private void updateInfo(JSONObject object) {

        if (object.has("business_name")) {

            company_name.setText(object.optString("business_name", ""));
        }


        if (object.has("bianhao")) {

            company_no.setText(object.optString("bianhao", ""));
        }


        if (object.has("legal_person")) {

            company_person.setText(object.optString("legal_person", ""));
        }


        if (object.has("idcard")) {

            company_idno.setText(object.optString("idcard", ""));
        }


        if (object.has("registered_capital")) {

            company_money.setText(object.optString("registered_capital", ""));
        }


        if (object.has("city")) {

            company_address.setText(object.optString("city", ""));
        }


        if (object.has("bid_money")) {

            company_borrow_money.setText(object.optString("bid_money", ""));
        }


        if (object.has("bid_duration")) {

            company_borrow_time.setText(object.optString("bid_duration", ""));
        }
        if (object.has("use_type")) {

            company_borrow_use.setText(object.optString("use_type", ""));
        }
        if (object.has("repay_source")) {

            company_hk_from.setText(object.optString("repay_source", ""));
        }


    }

    // 提交用户反馈到服务器
    public void uploadAndgetinfo(final boolean has_info) {
        JsonBuilder builder = new JsonBuilder();

        if(has_info){

            builder.put("is_data", "1");

        }else {





            if (null == returnMap) {

                return;
            } else {

                Iterator<String> iterator = returnMap.keySet().iterator();

                while (iterator.hasNext()) {

                    String key = iterator.next();
                    String value = returnMap.get(key);

                    builder.put(key, value);

                }

            }
            builder.put("is_data", "2");

        }





        BaseHttpClient.post(getBaseContext(), Default.business, builder,
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

                            if (statusCode == 200) {
                                // 没有新版本
                                if (json.getInt("status") == 1) {
                                    // initData(json);
                                    // 获取新版本
                                    if (has_info) {

                                        updateInfo(json);

                                    } else {
                                        showCustomToast(json.getString("message"));
                                        finish();
                                    }
                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(CompanyInfo.this, json.getInt("status"),message);
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

    public void finish() {
        super.finish();
    }

}
