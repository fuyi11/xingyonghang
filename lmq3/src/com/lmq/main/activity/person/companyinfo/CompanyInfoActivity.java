/**
 *
 */
package com.lmq.main.activity.person.companyinfo;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.xyh.R;

import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.user.manager.UploadUserInfoActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * 账户信息
 *
 * @author zzx
 */



public class CompanyInfoActivity extends BaseActivity implements
        OnClickListener {



    private TextView company_status;
    private int company_status_value;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_data_layout);

        TextView text = (TextView) findViewById(R.id.title);
        text.setText("企业资料");

        company_status = (TextView) findViewById(R.id.company_status);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.company_info).setOnClickListener(this);
        findViewById(R.id.uload_company).setOnClickListener(this);


    }

    protected void onResume() {
        super.onResume();
        getUserInfoStatus();

    }



    // 提交用户反馈到服务器
    public void getUserInfoStatus() {


        BaseHttpClient.post(getBaseContext(), Default.yindaoye, null,
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
                                // 没有新版本
                                if (json.getInt("status") == 1) {

                                    if(json.has("is_business_detail")){

                                        company_status_value = json.optInt("is_business_detail",-1);

                                        company_status.setText(company_status_value==1?"已完善":"未完善");

                                    }


//


                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(CompanyInfoActivity.this, json.getInt("status"),message);
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


    // 接受之前界面返回的数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void updateInfo(JSONObject json) {



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){


            case R.id.back:
                finish();
                break;
            case R.id.company_info:
                Intent infoIntent = new Intent(CompanyInfoActivity.this, CompanyInfo.class);
//                if(company_status_value == 1){
//                    infoIntent.putExtra("has_info",true);
//                }else{
//                    infoIntent.putExtra("has_info",false);
//                }

                infoIntent.putExtra("has_info",true);
                startActivity(infoIntent);


                break;

            case R.id.uload_company:

                Intent upload = new Intent(CompanyInfoActivity.this, UploadUserInfoActivity.class);


                startActivity(upload);

                break;

        }

    }


}
