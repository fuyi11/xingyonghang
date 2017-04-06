package com.lmq.main.activity.person.personinfo;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class PersoninfoDepartmentActivity extends BaseActivity implements OnClickListener {


    private EditText department_name;
    private EditText department_tel;
    private EditText department_address;
    private EditText department_age;
    private EditText department_people_name;
    private EditText department_people_tel;

    private boolean has_info;
    private HashMap<String,String> retMap;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unit_data_layout);

        TextView text = (TextView) findViewById(R.id.title);
        text.setText("单位资料");


        if(null != getIntent()){

            if(getIntent().hasExtra("has_info")){

                has_info = getIntent().getBooleanExtra("has_info",false);
            }
        }

        department_name = (EditText) findViewById(R.id.department_name);
        department_tel = (EditText) findViewById(R.id.department_tel);
        department_address = (EditText) findViewById(R.id.department_address);
        department_age = (EditText) findViewById(R.id.department_age);
        department_people_name = (EditText) findViewById(R.id.department_people_name);
        department_people_tel = (EditText) findViewById(R.id.department_people_tel);


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
                checkoutUserInput();
                break;


        }
    }

    private void checkoutUserInput() {

        if (SystenmApi.isNullOrBlank(department_name.getText().toString())) {

            showCustomToast("请输入单位名称");
            return ;

        }


        if (SystenmApi.isNullOrBlank(department_address.getText().toString())) {

            showCustomToast("请输入单位地址");
            return ;

        }


        if (SystenmApi.isNullOrBlank(department_tel.getText().toString())) {

            showCustomToast("请输入单位联系电话");
            return ;

        }

        if (SystenmApi.isNullOrBlank(department_age.getText().toString())) {

            showCustomToast("请输入工作年限");
            return ;

        }
        if (SystenmApi.isNullOrBlank(department_people_name.getText().toString())) {

            showCustomToast("请输入单位证明人");
            return ;

        }


        if (SystenmApi.isNullOrBlank(department_people_tel.getText().toString())) {

            showCustomToast("请输入证明人电话");
            return ;

        }



         retMap = new HashMap<String, String>();




        retMap.put("department_name", department_name.getText().toString());
        retMap.put("department_address", department_address.getText().toString());
        retMap.put("department_tel", department_tel.getText().toString());
        retMap.put("department_year", department_age.getText().toString());
        retMap.put("voucher_name", department_people_name.getText().toString());
        retMap.put("voucher_tel", department_people_tel.getText().toString());

        uplaodAndgetInfo();

    }


    private void updateInfo(JSONObject json) {




        if (json.has("department_name")) {


            department_name.setText(json.optString("department_name", ""));

        }
        if (json.has("department_address")) {


            department_address.setText(json.optString("department_address", ""));

        }
        if (json.has("department_tel")) {


            department_tel.setText(json.optString("department_tel", ""));

        }
        if (json.has("department_year")) {

            department_age.setText(json.optString("department_year", ""));

        }
        if (json.has("voucher_name")) {


            department_people_name.setText(json.optString("voucher_name", ""));

        }
        if (json.has("voucher_tel")) {


            department_people_tel.setText(json.optString("voucher_tel", ""));

        }


    }

    // 提交用户反馈到服务器
    public void uplaodAndgetInfo() {

        JsonBuilder builder = new JsonBuilder();
        if (has_info) {

            builder.put("is_data", "1");
        } else {

            builder.put("is_data", "2");
            if(null != retMap) {
                Iterator<String> iterator = retMap.keySet().iterator();
                while (iterator.hasNext()) {

                    String key = iterator.next();
                    String value = retMap.get(key);
                    builder.put(key, value);
                }
            }
        }


//        private EditText month_income;
//        private EditText month_income_dec;
//        private EditText month_outcome;
//        private EditText month_outcome_dec;
//        private RadioGroup house_status;
//        private EditText house_value;
//        private RadioGroup car_status;
//        private EditText car_value;
//        private EditText stock_name;
//        private EditText stock_value;
//        private EditText other_value_dec;
        BaseHttpClient.post(getBaseContext(), Default.editdepartment, builder
                ,
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

                                    if (has_info) {

                                        updateInfo(json);

                                    } else {
                                        showCustomToast(json.getString("message"));
                                        finish();
                                    }
                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(PersoninfoDepartmentActivity.this, json.getInt("status"),message);
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


    @Override
    protected void onResume() {
        super.onResume();
        uplaodAndgetInfo();
    }

    public void finish() {
        super.finish();
    }

}
