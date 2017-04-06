package com.lmq.main.activity.person.personinfo;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONObject;

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

public class PersoninfoActivity extends BaseActivity implements OnClickListener {


    private TextView person_status;
    private TextView contant_status;
    private TextView uniut_status;
    private TextView money_status;
    private TextView upload_status;

    private HashMap<String, Integer> allStatusMap;

    private ArrayList<String> infoTips = new ArrayList<String>();
    private  ArrayList<String> infoKey = new ArrayList<String>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_data_layout);

        TextView text = (TextView) findViewById(R.id.title);
        text.setText("个人资料");
        infoTips.add("请先完善个人资料");
        infoTips.add("请先完善联系人方式资料");
        infoTips.add("请先完善单位资料");
        infoTips.add("请先完善财务资料");

        infoKey.add("personinfo");
        infoKey.add("contact");
        infoKey.add("department");
        infoKey.add("money");


        allStatusMap = new HashMap<String, Integer>();
        person_status = (TextView) findViewById(R.id.person_status);
        contant_status = (TextView) findViewById(R.id.contant_status);
        uniut_status = (TextView) findViewById(R.id.uniut_status);
        money_status = (TextView) findViewById(R.id.money_status);
        upload_status = (TextView) findViewById(R.id.upload_status);


        findViewById(R.id.person_btn).setOnClickListener(this);
        findViewById(R.id.contact_btn).setOnClickListener(this);
        findViewById(R.id.department_btn).setOnClickListener(this);
        findViewById(R.id.money_btn).setOnClickListener(this);
        findViewById(R.id.upload_btn).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);


    }


    private String getStatusString(int status) {

        String rtnStatus = "";
        switch (status) {


            case 0:
                rtnStatus = "未完善";
                break;
            case 1:
                rtnStatus = "已完善";
                break;
            case 2:
                rtnStatus = "";
                break;

            default:
                rtnStatus = "未完善";
                break;


        }

        return rtnStatus;
    }


    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.back:
                finish(0);
                break;
            case R.id.person_btn:

                intent.setClass(PersoninfoActivity.this, Personinfo.class);
                if (allStatusMap.get("personinfo") == 0) {

                    intent.putExtra("has_info", true);

                } else {
                    intent.putExtra("has_info", true);

                }

                startActivity(intent);

                break;
            case R.id.contact_btn:

                if(checkoutStatus(1)!= 0){

                    intent.setClass(PersoninfoActivity.this, PersoninfoContactActivity.class);
                    if(allStatusMap.get("contact") == 0){

                        intent.putExtra("has_info", true);
                    }else{
                        intent.putExtra("has_info", true);

                    }
                    startActivity(intent);

                }


                break;
            case R.id.department_btn:

                if(checkoutStatus(2)!= 0) {
                    intent.setClass(PersoninfoActivity.this, PersoninfoDepartmentActivity.class);
                    if (allStatusMap.get("department") == 0) {

                        intent.putExtra("has_info", true);
                    } else {
                        intent.putExtra("has_info", true);


                    }
                    startActivity(intent);
                }

                break;
            case R.id.money_btn:
                if(checkoutStatus(3)!= 0) {
                    intent.setClass(PersoninfoActivity.this, PersoninfoMoneyActivity.class);
                    if (allStatusMap.get("money") == 0) {

                        intent.putExtra("has_info", true);
                    } else {
                        intent.putExtra("has_info", true);


                    }
                    startActivity(intent);
                }
                break;
            case R.id.upload_btn:
                if(checkoutStatus(4)!= 0) {
                    intent.setClass(PersoninfoActivity.this, UploadUserInfoActivity.class);
//                if(allStatusMap.get("business") == 0){
//
//                    intent.putExtra("has_info", false);
//                }else{
//                    intent.putExtra("has_info", true);
//
//
//                }
                    startActivity(intent);
                }
                break;


        }


    }


    @Override
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


//                                    is_member_info
//                                            is_member_contact_info
//                                    is_member_department_info
//                                            is_member_financial_info
//                                    is_business_detail


                                    if (json.has("is_member_info")) {


                                        allStatusMap.put("personinfo", json.optInt("is_member_info"));


                                    }


                                    if (json.has("is_member_contact_info")) {


                                        allStatusMap.put("contact", json.optInt("is_member_contact_info"));


                                    }


                                    if (json.has("is_member_department_info")) {


                                        allStatusMap.put("department", json.optInt("is_member_department_info"));


                                    }


                                    if (json.has("is_member_financial_info")) {


                                        allStatusMap.put("money", json.optInt("is_member_financial_info"));


                                    }


                                    if (json.has("is_business_detail")) {


                                        allStatusMap.put("business", json.optInt("is_business_detail"));


                                    }
                                    updateInfo();


                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(PersoninfoActivity.this, json.getInt("status"),message);
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

    private void updateInfo() {


        if (null != allStatusMap) {


            person_status.setText(getStatusString(allStatusMap.get("personinfo")));

            contant_status.setText(getStatusString(allStatusMap.get("contact")));

            uniut_status.setText(getStatusString(allStatusMap.get("department")));

            upload_status.setText(getStatusString(allStatusMap.get("business")));

            money_status.setText(getStatusString(allStatusMap.get("money")));


        }


    }

    private int checkoutStatus(int checkIndex) {



        int rtn = 0;

        for(int i=0;i<checkIndex;i++){

            if(allStatusMap.get(infoKey.get(i)) == 0){
                showCustomToast(infoTips.get(i));

                rtn = 0;
                break;
            }else {

                rtn = 1;
            }


        }















        return  rtn;


    }

    public void finish() {
        super.finish();
    }

}
