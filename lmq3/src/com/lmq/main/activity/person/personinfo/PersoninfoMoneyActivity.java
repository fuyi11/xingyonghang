package com.lmq.main.activity.person.personinfo;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPopupWindow;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PersoninfoMoneyActivity extends BaseActivity implements OnClickListener {


    private EditText month_income;
    private EditText month_income_dec;
    private EditText month_outcome;
    private EditText month_outcome_dec;
    private EditText house_value;
    private EditText car_value;
    private EditText stock_name;
    private EditText stock_value;
    private EditText other_value_dec;
    private boolean has_info;

    private String car_status_str;
    private String house_status_str;

    private HashMap<String,String> retMap;

    private OptionsPopupWindow choice_house;
    private OptionsPopupWindow choice_car;

    private ArrayList<String> house_lsit;
    private ArrayList<String> car_lsit;

    private TextView huseText;
    private TextView carText;

    private int car_selectid;
    private int house_selectid;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finance_status_layout);

        TextView text = (TextView) findViewById(R.id.title);
        text.setText("财务状况");


        if (null != getIntent()) {

            if (getIntent().hasExtra("has_info")) {

                has_info = getIntent().getBooleanExtra("has_info", false);
            }

        }


        month_income = (EditText) findViewById(R.id.month_income);
        month_income_dec = (EditText) findViewById(R.id.month_income_dec);
        month_outcome = (EditText) findViewById(R.id.month_outcome);
        month_outcome_dec = (EditText) findViewById(R.id.month_outcome_dec);
        house_value = (EditText) findViewById(R.id.house_value);
        car_value = (EditText) findViewById(R.id.car_value);
        stock_name = (EditText) findViewById(R.id.stock_name);
        stock_value = (EditText) findViewById(R.id.stock_value);
        other_value_dec = (EditText) findViewById(R.id.other_value_dec);


        choice_house = new OptionsPopupWindow(this);
        choice_car = new OptionsPopupWindow(this);

        huseText = (TextView) findViewById(R.id.tv_house_name);
        carText = (TextView) findViewById(R.id.tv_car_name);


        house_lsit = new ArrayList<String>();
        car_lsit = new ArrayList<String>();

        house_lsit.add("有房");
        house_lsit.add("无房");
        house_lsit.add("其他");
        choice_house.setPicker(house_lsit);
        choice_house.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                huseText.setText(house_lsit.get(options1));
                house_status_str = house_lsit.get(options1);
                house_selectid = options1;

            }
        });


        car_lsit.add("有车");
        car_lsit.add("无车");
        choice_car.setPicker(car_lsit);
        choice_car.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                carText.setText(car_lsit.get(options1));
                car_status_str = car_lsit.get(options1);

                car_selectid = options1;

            }
        });



        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);
        findViewById(R.id.choice_house_btn).setOnClickListener(this);
        findViewById(R.id.choice_car_btn).setOnClickListener(this);


    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish(0);
                break;
            case R.id.choice_car_btn:
                if(!choice_car.isShowing()){
                    choice_car.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                }
                break;
            case R.id.choice_house_btn:
                if(!choice_house.isShowing()){
                    choice_house.showAtLocation(v,Gravity.BOTTOM,0,0);
                }
                break;
            case R.id.submit:
                //TODO 提交个人资料
                has_info = false;
                checkoutUserInput();
                break;


        }
    }

    private void checkoutUserInput() {

        if (SystenmApi.isNullOrBlank(month_income.getText().toString())) {

            showCustomToast("请输入月收入");
            return ;

        }


        if (SystenmApi.isNullOrBlank(month_income_dec.getText().toString())) {

            showCustomToast("请输入收入构成描述");
            return ;

        }


        if (SystenmApi.isNullOrBlank(month_outcome.getText().toString())) {

            showCustomToast("请输入月支出");
            return ;

        }

        if (SystenmApi.isNullOrBlank(month_outcome_dec.getText().toString())) {

            showCustomToast("请输入支出构成描述");
            return ;

        }
        if (house_selectid == 0 ) {

            if (SystenmApi.isNullOrBlank(house_value.getText().toString())) {

                showCustomToast("请输入房产价值");
                return;

            }
        }else if (house_selectid == 1 || house_selectid == 2){
            house_value.setText("0");
        }




        if (SystenmApi.isNullOrBlank(car_status_str)) {

            showCustomToast("请选择是否购车");
            return ;

        }

        if (car_selectid == 0) {

            if (SystenmApi.isNullOrBlank(car_value.getText().toString())) {

                showCustomToast("请输入车辆价值");
                return;

            }
        }else if (car_selectid == 1){

            car_value.setText("0");
        }

        if (SystenmApi.isNullOrBlank(stock_name.getText().toString())) {

            showCustomToast("请输入参股企业名称");
            return ;

        }
        if (SystenmApi.isNullOrBlank(stock_value.getText().toString())) {

            showCustomToast("请输入参股企业出资额");
            return ;

        }
        if (SystenmApi.isNullOrBlank(other_value_dec.getText().toString())) {

            showCustomToast("请输入其他资产描述");
            return ;

        }

       retMap = new HashMap<String, String>();


        retMap.put("fin_monthin", month_income.getText().toString());
        retMap.put("fin_incomedes", month_income_dec.getText().toString());
        retMap.put("fin_monthout", month_outcome.getText().toString());
        retMap.put("fin_outdes", month_outcome_dec.getText().toString());
        retMap.put("fin_house", house_status_str);
        retMap.put("fin_housevalue", house_value.getText().toString());
        retMap.put("fin_car", car_status_str);
        retMap.put("fin_carvalue", car_value.getText().toString());
        retMap.put("fin_stockcompany", stock_name.getText().toString());
        retMap.put("fin_stockcompanyvalue", stock_value.getText().toString());
        retMap.put("fin_otheremark", other_value_dec.getText().toString());

        uplaodAndgetInfo();

    }


    @Override
    protected void onResume() {
        super.onResume();
        uplaodAndgetInfo();
    }




    private void updateInfo(JSONObject json) {


        if (json.has("fin_monthin")) {


            month_income.setText(json.optString("fin_monthin", ""));

        }
        if (json.has("fin_incomedes")) {


            month_income_dec.setText(json.optString("fin_incomedes", ""));

        }
        if (json.has("fin_monthout")) {


            month_outcome.setText(json.optString("fin_monthout", ""));

        }
        if (json.has("fin_outdes")) {

            month_outcome_dec.setText(json.optString("fin_outdes", ""));

        }
        if (json.has("fin_house")) {


            //old_address.setText(json.optString("fin_hofin_caruse", ""));
            huseText.setText(json.optString("fin_house",""));

        }
        if (json.has("fin_housevalue")) {


            house_value.setText(json.optString("fin_housevalue", ""));
            house_status_str = json.optString("fin_housevalue", "");

        }
        if (json.has("fin_car")) {


            carText.setText(json.optString("fin_car",""));
            car_status_str = json.optString("fin_car","");

        }
        if (json.has("fin_carvalue")) {


            car_value.setText(json.optString("fin_carvalue", ""));

        }
        if (json.has("fin_stockcompany")) {


            stock_name.setText(json.optString("fin_stockcompany", ""));

        }


        if (json.has("fin_stockcompanyvalue")) {


            stock_value.setText(json.optString("fin_stockcompanyvalue", ""));

        }
        if (json.has("fin_otheremark")) {


            other_value_dec.setText(json.optString("fin_otheremark", ""));

        }


    }

    // 提交用户反馈到服务器
    public void uplaodAndgetInfo() {

        JsonBuilder builder = new JsonBuilder();
        if (has_info) {

            builder.put("is_data", "1");
        } else {

            builder.put("is_data", "2");
            if(null != retMap){

                Iterator<String> iterator = retMap.keySet().iterator();
                while (iterator.hasNext()) {

                    String key = iterator.next();
                    String value = retMap.get(key);
                    builder.put(key, value);
                }
            }

        }


        BaseHttpClient.post(getBaseContext(), Default.editfinancial, builder
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
                                    SystenmApi.showCommonErrorDialog(PersoninfoMoneyActivity.this, json.getInt("status"),message);
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
